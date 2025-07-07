package dao;

import entity.Department;
import entity.LeaveType;
import entity.Request;
import entity.RequestApproval;
import entity.Role;
import entity.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgendaDAO {

    public List<User> getSubordinates(int managerId) {
        List<User> list = new ArrayList<>();

        String sql = """
        WITH RecursiveSubordinates AS (
            SELECT * FROM users WHERE manager_id = ?
            UNION ALL
            SELECT u.* FROM users u
            INNER JOIN RecursiveSubordinates r ON u.manager_id = r.user_id
        )
        SELECT rs.*, d.department_name
        FROM RecursiveSubordinates rs
        JOIN departments d ON rs.department_id = d.department_id
    """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, managerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setManagerId(rs.getInt("manager_id"));

                // Gán department
                Department d = new Department();
                d.setDepartmentId(rs.getInt("department_id"));
                d.setDepartmentName(rs.getString("department_name"));
                u.setDepartment(d);

                // Gán roles
                RoleDAO roleDao = new RoleDAO();
                List<Role> roles = roleDao.getRolesByUser(u.getUserId());
                roles.sort(Comparator.comparingInt(r -> roleDao.getRolePriority(r.getRoleName())));
                u.setRoles(roles);

                list.add(u);
            }

        } catch (Exception e) {
        }

        return list;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                return u;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List<Request> getRequestsByUser(int userId) {
        List<Request> list = new ArrayList<>();
        String sql = """
        SELECT r.request_id, r.from_date, r.to_date, r.reason,
               l.type_name, ra.decision
        FROM requests r
        JOIN leave_types l ON r.leave_type_id = l.leave_type_id
        LEFT JOIN request_approvals ra ON r.request_id = ra.request_id
        WHERE r.user_id = ?
    """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Request r = new Request();
                r.setRequestId(rs.getInt("request_id"));
                r.setFromDate(rs.getDate("from_date"));
                r.setToDate(rs.getDate("to_date"));
                r.setReason(rs.getString("reason"));
                r.setStatus(rs.getString("decision") != null ? rs.getString("decision") : "Pending");

                LeaveType lt = new LeaveType();
                lt.setTypeName(rs.getString("type_name"));
                r.setLeaveType(lt);

                list.add(r);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public boolean hasApprovalPermission(int userId) {
        String sql = "SELECT 1 FROM user_features uf "
                + "JOIN features f ON uf.feature_id = f.feature_id "
                + "WHERE uf.user_id = ? AND f.feature_name = 'View and approve subordinates'' agenda'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
        }
        return false;
    }

    public void updateRequestStatus(int requestId, String newStatus, int approverId) {
        String checkApproval = "SELECT COUNT(*) FROM request_approvals WHERE request_id = ?";
        String insertApproval = "INSERT INTO request_approvals(request_id, approver_id, decision) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement checkStmt = con.prepareStatement(checkApproval); PreparedStatement insertStmt = con.prepareStatement(insertApproval)) {

            checkStmt.setInt(1, requestId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                // Chỉ insert nếu đơn chưa được duyệt trước đó
                insertStmt.setInt(1, requestId);
                insertStmt.setInt(2, approverId);
                insertStmt.setString(3, newStatus);
                insertStmt.executeUpdate();
            }

        } catch (Exception e) {
        }
    }

    public void insertApproval(RequestApproval approval) {
        String sql = "INSERT INTO request_approvals (request_id, approver_id, decision, comments) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, approval.getRequestId());
            ps.setInt(2, approval.getApproverId());
            ps.setString(3, approval.getDecision());
            ps.setString(4, approval.getComments());
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public Map<Integer, String> getApprovalCommentsForRequests(List<Integer> requestIds) {
        Map<Integer, String> result = new HashMap<>();
        if (requestIds == null || requestIds.isEmpty()) {
            return result;
        }

        String placeholders = requestIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT request_id, comments FROM request_approvals "
                + "WHERE approval_id IN (SELECT MAX(approval_id) FROM request_approvals WHERE request_id IN (" + placeholders + ") GROUP BY request_id)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < requestIds.size(); i++) {
                ps.setInt(i + 1, requestIds.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("request_id"), rs.getString("comments"));
            }
        } catch (Exception e) {
        }

        return result;
    }

}
