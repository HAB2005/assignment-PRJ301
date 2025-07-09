package dao;

import entity.Department;
import entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password, email, full_name FROM users";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setFullName(rs.getString("full_name"));

                list.add(u);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public boolean updateUser(int userId, String username, String password, String email, String fullname) {
        String sql = "UPDATE users SET username=?, password=?, email=?, full_name=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, fullname);
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setFullName(rs.getString("full_name"));
                return u;
            }
        } catch (Exception e) {
        }
        return null;
    }
}
