package dao;

import entity.Feature;
import entity.LeaveType;
import entity.Request;
import entity.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class RequestDAO {

    public void createRequest(Request request) throws SQLException {
        String sql = "INSERT INTO requests (user_id, from_date, to_date, leave_type_id, reason) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, request.getUserId());
            stmt.setDate(2, new java.sql.Date(request.getFromDate().getTime()));
            stmt.setDate(3, new java.sql.Date(request.getToDate().getTime()));

            // Lấy ID từ đối tượng LeaveType
            stmt.setInt(4, request.getLeaveType().getLeaveTypeId());

            stmt.setString(5, request.getReason());
            // Trạng thái: nếu không có, gán mặc định là "Chờ xử lý"

            stmt.executeUpdate();
        }
    }

    public List<Request> getRequestsByUserId(int userId) {
        List<Request> list = new ArrayList<>();

        String sql = """
        SELECT r.request_id, r.user_id, r.from_date, r.to_date, r.reason,
               lt.leave_type_id, lt.type_name,
               ra.decision
        FROM requests r
        JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id
        LEFT JOIN (
            SELECT request_id, MAX(decision) AS decision
            FROM request_approvals
            GROUP BY request_id
        ) ra ON r.request_id = ra.request_id
        WHERE r.user_id = ?
        ORDER BY r.from_date DESC
    """;

        try (Connection con = DBConnection.getConnection(); // gọi static
                 PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Request r = new Request();
                r.setRequestId(rs.getInt("request_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setFromDate(rs.getDate("from_date"));  // java.sql.Date
                r.setToDate(rs.getDate("to_date"));      // java.sql.Date
                r.setReason(rs.getString("reason"));

                LeaveType lt = new LeaveType();
                lt.setLeaveTypeId(rs.getInt("leave_type_id"));
                lt.setTypeName(rs.getString("type_name"));
                r.setLeaveType(lt);

                String decision = rs.getString("decision");
                r.setStatus(decision == null ? "Pending" : decision);

                list.add(r);
            }

        } catch (Exception e) {
        }

        return list;
    }

    public List<Role> getRolesByUserId(int userId) throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM roles r JOIN user_roles ur ON r.role_id = ur.role_id WHERE ur.user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                roles.add(role);
            }
        }
        return roles;
    }

    public List<Feature> getFeaturesByRole(int roleId) throws SQLException {
        List<Feature> features = new ArrayList<>();
        String sql = "SELECT f.* FROM features f "
                + "JOIN role_features rf ON f.feature_id = rf.feature_id "
                + "WHERE rf.role_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Feature feature = new Feature();
                feature.setFeatureId(rs.getInt("feature_id"));
                feature.setFeatureName(rs.getString("feature_name"));
                features.add(feature);
            }
        }
        return features;
    }
}
