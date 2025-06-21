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
}
