package controller;

import dao.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import util.CryptoUtil;

public class EditUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        String userIdRaw = req.getParameter("userId");

        // Trường hợp 1: Chỉ chọn để sửa (chưa cập nhật)
        if (action == null || !action.equals("updateUser")) {
            try {
                UserDAO userDAO = new UserDAO();
                User u = userDAO.getUserById(Integer.parseInt(userIdRaw));

                if (u != null) {
                    req.setAttribute("userId", u.getUserId());
                    req.setAttribute("username", u.getUsername());
                    req.setAttribute("password", ""); // không điền sẵn mật khẩu vào form

                    // ✅ Giải mã email nếu cần
                    String rawEmail = u.getEmail();
                    String decryptedEmail;
                    try {
                        if (rawEmail != null && rawEmail.startsWith("ENC:")) {
                            decryptedEmail = CryptoUtil.decrypt(rawEmail.substring(4));
                        } else {
                            decryptedEmail = rawEmail;
                        }
                    } catch (Exception e) {
                        decryptedEmail = "Lỗi giải mã";
                    }

                    req.setAttribute("email", decryptedEmail);
                    req.setAttribute("fullname", u.getFullName());
                    req.setAttribute("mode", "edit");
                } else {
                    req.setAttribute("error", "Không tìm thấy người dùng.");
                }

                req.getRequestDispatcher("/common/userForm.jsp").forward(req, resp);
                return;
            } catch (SQLException ex) {
                Logger.getLogger(EditUserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Trường hợp 2: Submit để cập nhật thông tin
        try {
            int userId = Integer.parseInt(userIdRaw);
            String username = req.getParameter("username").trim();
            String passwordInput = req.getParameter("password").trim();
            String fullname = req.getParameter("fullname").trim();
            String emailInput = req.getParameter("email").trim();

            UserDAO userDAO = new UserDAO();
            User existingUser = userDAO.getUserById(userId);

            // ✅ Xử lý mật khẩu
            String finalPassword;
            if (passwordInput == null || passwordInput.isBlank()) {
                finalPassword = existingUser.getPassword(); // giữ nguyên
            } else {
                finalPassword = BCrypt.hashpw(passwordInput, BCrypt.gensalt()); // hash mới
            }

            // ✅ Xử lý email
            String finalEmail;
            String decryptedExistingEmail = "";
            try {
                String raw = existingUser.getEmail();
                if (raw != null && raw.startsWith("ENC:")) {
                    decryptedExistingEmail = CryptoUtil.decrypt(raw.substring(4));
                } else {
                    decryptedExistingEmail = raw;
                }
            } catch (Exception e) {
                decryptedExistingEmail = emailInput; // fallback
            }

            if (emailInput.equals(decryptedExistingEmail)) {
                finalEmail = existingUser.getEmail(); // không thay đổi
            } else {
                finalEmail = "ENC:" + CryptoUtil.encrypt(emailInput); // mã hóa mới
            }

            boolean updated = userDAO.updateUser(userId, username, finalPassword, finalEmail, fullname);

            req.setAttribute("userId", userId);
            req.setAttribute("username", username);
            req.setAttribute("password", ""); // không hiển thị lại password
            req.setAttribute("email", emailInput);
            req.setAttribute("fullname", fullname);
            req.setAttribute("mode", "edit");

            if (updated) {
                req.setAttribute("message", "✅ Cập nhật thành công!");
            } else {
                req.setAttribute("error", "❌ Cập nhật thất bại. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            req.setAttribute("error", "❌ Lỗi khi cập nhật: " + e.getMessage());
        }

        req.getRequestDispatcher("/common/userForm.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }
        UserDAO dao = new UserDAO();
        List<User> userList = dao.getAllUsers();
        req.setAttribute("userList", userList);
        req.setAttribute("formAction", "edit_user");
        req.getRequestDispatcher("/common/allUser.jsp").forward(req, resp);
    }
}
