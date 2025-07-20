package controller;

import dao.FeatureDAO;
import dao.NotificationDAO;
import dao.RoleDAO;
import dao.UserDAO;
import entity.Feature;
import entity.Notification;
import entity.Role;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final FeatureDAO featureDAO = new FeatureDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();
        String remember = request.getParameter("remember");

        try {
            User user = userDAO.login(username, password);

            if (user != null) {

                // ✅ Ghi nhớ đăng nhập nếu cần
                if ("on".equals(remember)) {
                    Cookie userCookie = new Cookie("rememberedUsername_" + username, password);
                    userCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
                    response.addCookie(userCookie);
                }

                // ✅ Lấy danh sách vai trò
                List<Role> roles = roleDAO.getRolesByUser(user.getUserId());

                // Sắp xếp vai trò theo độ ưu tiên
                for (int i = 0; i < roles.size() - 1; i++) {
                    for (int j = i + 1; j < roles.size(); j++) {
                        if (getPriority(roles.get(j).getRoleName()) < getPriority(roles.get(i).getRoleName())) {
                            Role temp = roles.get(i);
                            roles.set(i, roles.get(j));
                            roles.set(j, temp);
                        }
                    }
                }

                user.setRoles(roles);

                // ✅ Lấy danh sách feature
                List<Feature> features = featureDAO.getFeaturesByUserId(user.getUserId());
                Map<String, String> featureLinks = new HashMap<>();

                String rolePath = "admin";
                if (roles != null && !roles.isEmpty()) {
                    rolePath = roles.get(0).getRoleName().toLowerCase().replace(" ", "_");
                }

                for (Feature f : features) {
                    String featurePath = f.getFeatureName().toLowerCase().replace(" ", "_");
                    String fullPath = rolePath + "/" + featurePath;
                    featureLinks.put(f.getFeatureName(), fullPath);
                }

                // ✅ Lấy danh sách thông báo
                List<Notification> notifications = notificationDAO.getNotificationsByUserId(user.getUserId());

                // ✅ Đếm số thông báo chưa đọc (không dùng stream)
                int unreadCount = 0;
                for (Notification n : notifications) {
                    if (!n.isRead()) {
                        unreadCount++;
                    }
                }

                // ✅ Lưu vào session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("roles", roles);
                session.setAttribute("features", features);
                session.setAttribute("featureLinks", featureLinks);
                session.setAttribute("department", user.getDepartment());
                session.setAttribute("rolePath", rolePath);
                session.setAttribute("notifications", notifications);
                session.setAttribute("unreadCount", unreadCount); // ✅ số thông báo chưa đọc

                // ✅ Chuyển hướng đến menu
                response.sendRedirect(request.getContextPath() + "/" + rolePath + "/menu");

            } else {
                // ❌ Đăng nhập thất bại
                request.setAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
                request.getRequestDispatcher("/common/login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            throw new ServletException("Lỗi kết nối CSDL", e);
        }
    }

    private int getPriority(String roleName) {
        if (roleName.equals("General Manager")) {
            return 1;
        }
        if (roleName.equals("Department Head")) {
            return 2;
        }
        if (roleName.equals("Direct Manager")) {
            return 3;
        }
        return 4;
    }
}
