package controller;

import dao.FeatureDAO;
import dao.RoleDAO;
import dao.UserDAO;
import entity.Feature;
import entity.Role;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();         // bạn đã có getRolesByUser
    private final FeatureDAO featureDAO = new FeatureDAO(); // bạn đã có getFeaturesByRoles

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();

        try {
            // Bước 1: Xác thực tài khoản
            User user = userDAO.login(username, password);

            if (user != null) {
                // Bước 2: Lấy danh sách vai trò
                List<Role> roles = roleDAO.getRolesByUser(user.getUserId());
                user.setRoles(roles);
                
                // Bước 3: Lấy danh sách tính năng từ các vai trò
                List<Feature> features = featureDAO.getFeaturesByUserId(user.getUserId());

                // Bước 4: Lưu thông tin vào session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("roles", roles);
                session.setAttribute("features", features);

                // Bước 5: Chuyển hướng sang trang chính
                response.sendRedirect("common/menu.jsp");
            } else {
                // Sai tài khoản hoặc mật khẩu
                request.setAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
                request.getRequestDispatcher("/common/login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            throw new ServletException("Lỗi kết nối CSDL", e);
        }
    }
}
