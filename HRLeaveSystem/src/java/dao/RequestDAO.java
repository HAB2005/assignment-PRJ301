package dao;

import entity.Feature;
import entity.Request;
import entity.Role;
import entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.DBConnection;

public class RequestDAO {

    public void createRequest(Request request) throws SQLException {
        String sql = "INSERT INTO requests (user_id, from_date, to_date, leave_type_id, reason) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, request.getUserId());

            // Chuyển java.util.Date → java.sql.Date
            stmt.setDate(2, new java.sql.Date(request.getFromDate().getTime()));
            stmt.setDate(3, new java.sql.Date(request.getToDate().getTime()));

            stmt.setInt(4, request.getLeaveTypeId());
            stmt.setString(5, request.getReason());

            stmt.executeUpdate();
        }
    }

    public List<Request> getRequestsByUserId(int userId) throws SQLException {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT r.*, lt.type_name FROM requests r "
                + "JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id "
                + "WHERE r.user_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Request r = new Request();
                r.setRequestId(rs.getInt("request_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setFromDate(rs.getDate("from_date"));
                r.setToDate(rs.getDate("to_date"));
                r.setLeaveTypeId(rs.getInt("leave_type_id"));
                r.setReason(rs.getString("reason"));

                // Gán thêm tên loại nghỉ phép
                r.setLeaveTypeName(rs.getString("type_name"));

                list.add(r);
            }
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

    public List<Request> getRequestsByUser(int userId) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.*, lt.leave_type_name FROM requests r JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id WHERE r.user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Request req = new Request();
                req.setId(rs.getInt("request_id"));
                req.setUserId(rs.getInt("user_id"));
                req.setLeaveTypeName(rs.getString("leave_type_name"));
                req.setFromDate(rs.getDate("from_date"));
                req.setToDate(rs.getDate("to_date"));
                req.setStatus(rs.getString("status"));
                req.setReason(rs.getString("reason"));
                requests.add(req);
            }
        }
        return requests;
    }

    public Request getRequestById(int requestId) throws SQLException {
        String sql = "SELECT r.*, lt.leave_type_name FROM requests r JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id WHERE r.request_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Request req = new Request();
                req.setId(rs.getInt("request_id"));
                req.setUserId(rs.getInt("user_id"));
                req.setLeaveTypeName(rs.getString("leave_type_name"));
                req.setFromDate(rs.getDate("from_date"));
                req.setToDate(rs.getDate("to_date"));
                req.setStatus(rs.getString("status"));
                req.setReason(rs.getString("reason"));
                return req;
            }
        }
        return null;
    }

}
