package dao;

import entity.Department;
import entity.User;
import java.sql.*;
import util.DBConnection;

public class UserDAO {

    private final RoleDAO roleDAO = new RoleDAO();

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));

                // Gán Department ID thông qua DAO
                int departmentId = rs.getInt("department_id");
                DepartmentDAO departmentDAO = new DepartmentDAO();
                Department department = departmentDAO.getDepartmentById(departmentId);
                user.setDepartment(department);

                int managerId = rs.getInt("manager_id");
                user.setManagerId(rs.wasNull() ? null : managerId);
                user.setRoles(roleDAO.getRolesByUser(user.getUserId()));

                return user;
            }
            return null;
        }
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());

            stmt.executeUpdate();
        }
    }
}
