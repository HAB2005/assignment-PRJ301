package dao;

import entity.Role;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import util.DBConnection;

public class RoleDAO {

    public List<Role> getRolesByUser(int userId) throws SQLException {
        String sql = "SELECT r.role_id, r.role_name FROM roles r "
                + "JOIN user_roles ur ON r.role_id = ur.role_id "
                + "WHERE ur.user_id = ?";
        List<Role> roles = new ArrayList<>();
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

    public int getRolePriority(String roleName) {
        return switch (roleName.toLowerCase()) {
            case "general manager" ->
                1;
            case "department head" ->
                2;
            case "direct manager" ->
                3;
            case "employee" ->
                4;
            default ->
                99;
        };
    }

    public List<String> getRoleNamesOfSubordinates(int managerId) throws SQLException {
        String sql = """
        WITH RecursiveSubordinates AS (
            SELECT user_id FROM users WHERE manager_id = ?
            UNION ALL
            SELECT u.user_id FROM users u
            JOIN RecursiveSubordinates r ON u.manager_id = r.user_id
        )
        SELECT DISTINCT r.role_name
        FROM user_roles ur
        JOIN roles r ON ur.role_id = r.role_id
        JOIN RecursiveSubordinates rs ON ur.user_id = rs.user_id
    """;

        List<String> roleNames = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roleNames.add(rs.getString("role_name"));
            }
        }
        return roleNames;
    }

    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT role_id, role_name FROM roles";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("role_id"));
                r.setRoleName(rs.getString("role_name"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
