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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                // Bước 2: Lấy danh sách vai trò của người dùng
                List<Role> roles = roleDAO.getRolesByUser(user.getUserId());

                // Bước 2.1: Sắp xếp vai trò theo mức độ ưu tiên (nếu cần)
                for (int i = 0; i < roles.size() - 1; i++) {
                    for (int j = i + 1; j < roles.size(); j++) {
                        if (getPriority(roles.get(j).getRoleName()) < getPriority(roles.get(i).getRoleName())) {
                            Role temp = roles.get(i);
                            roles.set(i, roles.get(j));
                            roles.set(j, temp);
                        }
                    }
                }

                // Gán roles đã sắp xếp vào user
                user.setRoles(roles);

                // Bước 3: Lấy danh sách feature
                List<Feature> features = featureDAO.getFeaturesByUserId(user.getUserId());

                // Bước 4: Tạo Map để ánh xạ feature name -> link path
                Map<String, String> featureLinks = new HashMap<>();

                if (!roles.isEmpty()) {
                    // Dùng role đầu tiên (ưu tiên nhất)
                    String rolePath = roles.get(0).getRoleName().toLowerCase().replace(" ", "_");

                    for (Feature f : features) {
                        String featurePath = f.getFeatureName().toLowerCase().replace(" ", "_");
                        String fullPath = rolePath + "/" + featurePath;
                        featureLinks.put(f.getFeatureName(), fullPath);
                    }
                }

                // Bước 5: Lưu thông tin vào session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("roles", roles);
                session.setAttribute("features", features);
                session.setAttribute("featureLinks", featureLinks);
                session.setAttribute("department", user.getDepartment());

                // Bước 6: Chuyển hướng đến trang menu chung
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
        return 4; // Employee hoặc vai trò thấp nhất
    }

}
