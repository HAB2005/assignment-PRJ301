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
import org.mindrot.jbcrypt.BCrypt;
import util.CryptoUtil;

public class CreateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String fullname = req.getParameter("fullname").trim();
        String email = req.getParameter("email").trim();

        try {
            // ✅ Mã hóa mật khẩu
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            // ✅ Mã hóa email
            String encryptedEmail = null;
            if (!email.isEmpty()) {
                encryptedEmail = "ENC:" + CryptoUtil.encrypt(email);
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(hashedPassword);
            newUser.setFullName(fullname);
            newUser.setEmail(encryptedEmail);

            UserDAO userDAO = new UserDAO();
            userDAO.createUser(newUser);

            req.setAttribute("message", "✅ Tạo người dùng thành công!");

        } catch (SQLException e) {
            req.setAttribute("error", "❌ Đã xảy ra lỗi: " + e.getMessage());
        } catch (Exception e) {
            req.setAttribute("error", "❌ Mã hóa email thất bại: " + e.getMessage());
        }

        req.getRequestDispatcher("/common/userForm.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/common/userForm.jsp").forward(req, resp);
    }
}
