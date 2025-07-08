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
        System.out.println(123);
        System.out.println(234);

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setFullName(fullname);
            newUser.setEmail(email);

            UserDAO userDAO = new UserDAO();
            userDAO.createUser(newUser);

            req.setAttribute("message", "✅ Tạo người dùng thành công!");

        } catch (SQLException e) {
            req.setAttribute("error", "❌ Đã xảy ra lỗi: " + e.getMessage());
        }

        req.getRequestDispatcher("/common/createUser.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/common/createUser.jsp").forward(req, resp);
    }
}
