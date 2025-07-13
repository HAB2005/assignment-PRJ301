package dao;

import util.CryptoUtil;
import entity.Department;
import entity.Role;
import entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import util.DBConnection;

public class UserDAO {

    private final RoleDAO roleDAO = new RoleDAO();

    public User login(String username, String passwordInput) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                // Mặc định: password trong DB là BCrypt hash
                if (BCrypt.checkpw(passwordInput, dbPassword)) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(dbPassword); // hoặc bỏ qua nếu không dùng

                    user.setFullName(rs.getString("full_name"));

                    // ✅ Giải mã email nếu cần
                    String emailInDb = rs.getString("email");
                    if (emailInDb != null && emailInDb.startsWith("ENC:")) {
                        try {
                            String encryptedPart = emailInDb.substring(4); // bỏ "ENC:"
                            String decryptedEmail = CryptoUtil.decrypt(encryptedPart);
                            user.setEmail(decryptedEmail);
                        } catch (Exception e) {
                            user.setEmail("Lỗi giải mã email");
                        }
                    } else {
                        user.setEmail(emailInDb); // email chưa mã hóa
                    }

                    // Gán phòng ban
                    int departmentId = rs.getInt("department_id");
                    DepartmentDAO departmentDAO = new DepartmentDAO();
                    Department department = departmentDAO.getDepartmentById(departmentId);
                    user.setDepartment(department);

                    int managerId = rs.getInt("manager_id");
                    user.setManagerId(rs.wasNull() ? null : managerId);

                    user.setRoles(roleDAO.getRolesByUser(user.getUserId()));

                    return user;
                }
            }

            // Sai mật khẩu hoặc không tồn tại username
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

                // ✅ Giải mã email nếu có mã hóa
                String rawEmail = rs.getString("email");
                if (rawEmail != null && rawEmail.startsWith("ENC:")) {
                    try {
                        String decryptedEmail = CryptoUtil.decrypt(rawEmail.substring(4));
                        u.setEmail(decryptedEmail);
                    } catch (Exception e) {
                        u.setEmail("Lỗi giải mã");
                    }
                } else {
                    u.setEmail(rawEmail);
                }

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

            // ✅ Kiểm tra xem mật khẩu đã hash chưa
            String finalPassword = password;
            if (!(password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"))) {
                finalPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            }

            // ✅ Mã hóa email nếu chưa mã hóa
            String finalEmail = email;
            try {
                if (!email.startsWith("ENC:")) {
                    finalEmail = "ENC:" + CryptoUtil.encrypt(email);
                }
            } catch (Exception ex) {
                return false;
            }

            ps.setString(1, username);
            ps.setString(2, finalPassword);
            ps.setString(3, finalEmail);
            ps.setString(4, fullname);
            ps.setInt(5, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUserById(int userId) throws SQLException {
        String sql = """
        SELECT u.user_id, u.username, u.password, u.full_name, u.email, u.manager_id,
               d.department_id, d.department_name
        FROM users u
        LEFT JOIN departments d ON u.department_id = d.department_id
        WHERE u.user_id = ?
        """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setManagerId((Integer) rs.getObject("manager_id"));

                int deptId = rs.getInt("department_id");
                if (deptId != 0) {
                    Department dept = new Department();
                    dept.setDepartmentId(deptId);
                    dept.setDepartmentName(rs.getString("department_name"));
                    u.setDepartment(dept);
                }

                return u;
            }
        }

        return null;
    }

    public List<Role> getManagerRoles() throws SQLException {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT role_id, role_name FROM roles WHERE role_name IN ('General Manager', 'Department Head', 'Direct Manager')";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("role_id"));
                r.setRoleName(rs.getString("role_name"));
                list.add(r);
            }
        }
        return list;
    }

    public List<User> getAvailableManagers(int departmentId) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = """
        SELECT u.user_id, u.full_name
        FROM users u
        JOIN roles r ON u.role_id = r.role_id
        WHERE (r.role_name IN ('Direct Manager', 'Department Head') AND u.department_id = ?)
           OR r.role_name = 'General Manager'
        """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setFullName(rs.getString("full_name"));
                    list.add(u);
                }
            }
        }
        return list;
    }

    public boolean assignPermission(int userId, int departmentId, Integer managerId, int roleId, List<Integer> featureIds) {
        String updateUserSql = "UPDATE users SET department_id=?, manager_id=? WHERE user_id=?";
        String deleteRoleSql = "DELETE FROM user_roles WHERE user_id=?";
        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        String deleteFeatureSql = "DELETE FROM user_features WHERE user_id=?";
        String insertFeatureSql = "INSERT INTO user_features (user_id, feature_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // bắt đầu transaction

            try (
                    PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql); PreparedStatement deleteRoleStmt = conn.prepareStatement(deleteRoleSql); PreparedStatement insertRoleStmt = conn.prepareStatement(insertRoleSql); PreparedStatement deleteFeatureStmt = conn.prepareStatement(deleteFeatureSql); PreparedStatement insertFeatureStmt = conn.prepareStatement(insertFeatureSql)) {
                // 1. Cập nhật department và manager
                updateUserStmt.setInt(1, departmentId);
                if (managerId != null) {
                    updateUserStmt.setInt(2, managerId);
                } else {
                    updateUserStmt.setNull(2, java.sql.Types.INTEGER);
                }
                updateUserStmt.setInt(3, userId);
                updateUserStmt.executeUpdate();

                // 2. Xóa role cũ
                deleteRoleStmt.setInt(1, userId);
                deleteRoleStmt.executeUpdate();

                // 3. Gán role mới
                insertRoleStmt.setInt(1, userId);
                insertRoleStmt.setInt(2, roleId);
                insertRoleStmt.executeUpdate();

                // 4. Xóa feature cũ
                deleteFeatureStmt.setInt(1, userId);
                deleteFeatureStmt.executeUpdate();

                // 5. Thêm các feature mới
                for (int featureId : featureIds) {
                    insertFeatureStmt.setInt(1, userId);
                    insertFeatureStmt.setInt(2, featureId);
                    insertFeatureStmt.addBatch();
                }
                insertFeatureStmt.executeBatch();

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> getManagersByDepartment(int deptId) throws SQLException {
        List<User> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();

        String sql = "SELECT DISTINCT u.* FROM users u "
                + "JOIN user_roles ur ON u.user_id = ur.user_id "
                + "JOIN roles r ON ur.role_id = r.role_id "
                + "WHERE u.department_id = ? AND r.role_name IN ('Department Head', 'Direct Manager')";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, deptId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User u = new User();
            u.setUserId(rs.getInt("user_id"));
            u.setFullName(rs.getString("full_name"));
            list.add(u);
        }
        return list;
    }

}
