package dao;

import entity.Department;
import entity.LeaveType;
import entity.Request;
import entity.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));

                Department d = new Department();
                d.setDepartmentName(rs.getString("department_name"));
                u.setDepartment(d);

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
            e.printStackTrace();
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
            e.printStackTrace(); // Thêm log cho dễ debug
        }
        return list;
    }

    public void updateRequestStatus(int requestId, String status, int approverId) {
        String insertApproval = "INSERT INTO request_approvals (request_id, approver_id, decision) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(insertApproval)) {
            ps.setInt(1, requestId);
            ps.setInt(2, approverId);
            ps.setString(3, status);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasApprovalPermission(int userId) {
        String sql = "SELECT 1 FROM user_roles ur JOIN role_features rf ON ur.role_id = rf.role_id "
                + "JOIN features f ON rf.feature_id = f.feature_id WHERE ur.user_id = ? AND f.feature_name = 'View and approve subordinates'' agenda'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
