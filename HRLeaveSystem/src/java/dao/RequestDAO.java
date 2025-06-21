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
        String sql = "INSERT INTO requests (user_id, title, from_date, to_date, leave_type_id, reason, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getUserId());
            stmt.setString(2, request.getTitle());
            stmt.setDate(3, new java.sql.Date(request.getFromDate().getTime()));
            stmt.setDate(4, new java.sql.Date(request.getToDate().getTime()));
            stmt.setDouble(5, request.getTotalDays());
            stmt.setInt(6, request.getLeaveTypeId());
            stmt.setString(7, request.getReason());
            stmt.setString(8, request.getStatus());
            stmt.executeUpdate();
        }
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

    public List<Request> getSubordinateRequests(int userId, List<Role> roles) throws SQLException {
        String sql;
        if (roles.stream().anyMatch(role -> role.getRoleName().equals("CEO"))) {
            sql = "SELECT r.*, lt.leave_type_name FROM requests r JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id WHERE r.status = 'Inprogress'";
        } else if (roles.stream().anyMatch(role -> role.getRoleName().equals("Head of Department"))) {
            sql = "SELECT r.*, lt.leave_type_name FROM requests r JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id JOIN users u ON r.user_id = u.user_id WHERE u.department_id = (SELECT department_id FROM users WHERE user_id = ?) AND r.status = 'Inprogress'";
        } else {
            sql = "SELECT r.*, lt.leave_type_name FROM requests r JOIN leave_types lt ON r.leave_type_id = lt.leave_type_id JOIN users u ON r.user_id = u.user_id WHERE u.manager_id = ? AND r.status = 'Inprogress'";
        }
        List<Request> requests = new ArrayList<>();
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

    public void updateRequestStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }

    public Map<User, List<Request>> getAgendaData(int departmentId, String startDate, String endDate) throws SQLException {
        // Truy vấn phức tạp để lấy dữ liệu cho Agenda
        // Ví dụ: Lấy tất cả đơn đã duyệt trong khoảng thời gian
        String sql = "SELECT r.*, u.full_name FROM requests r JOIN users u ON r.user_id = u.user_id "
                + "WHERE u.department_id = ? AND r.status = 'Approved' AND r.from_date >= ? AND r.to_date <= ?";
        Map<User, List<Request>> agendaData = new HashMap<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                Request req = new Request();
                req.setId(rs.getInt("request_id"));
                req.setFromDate(rs.getDate("from_date"));
                req.setToDate(rs.getDate("to_date"));
                agendaData.computeIfAbsent(user, k -> new ArrayList<>()).add(req);
            }
        }
        return agendaData;
    }
}
