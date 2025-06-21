package controller;

import dao.RequestDAO;
import entity.Feature;
import entity.Request;
import entity.Role;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RequestServlet extends HttpServlet {

    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        List<Role> roles = (List<Role>) req.getSession().getAttribute("roles");
        List<Feature> roleFeatures = (List<Feature>) req.getSession().getAttribute("roleFeatures"); // ✅ lấy từ session

        if (user == null || roles == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Request> requests;
            String type = req.getParameter("type");

            if ("subordinates".equals(type)) {
                boolean isManager = roles.stream().anyMatch(role
                        -> role.getRoleName().equalsIgnoreCase("admin")
                        || role.getRoleName().equalsIgnoreCase("Head of Department")
                        || role.getRoleName().equalsIgnoreCase("Direct Manager"));
                if (isManager) {
                    requests = requestDAO.getSubordinateRequests(user.getUserId(), roles);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/employee/dashboard_employee");
                    return;
                }
            } else {
                requests = requestDAO.getRequestsByUser(user.getUserId());
            }

            req.setAttribute("requests", requests);
            req.setAttribute("roleFeatures", roleFeatures); // vẫn cần set cho JSP (nhưng dùng bản từ session)
            req.getRequestDispatcher("/direct_manager_menu.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi khi tải danh sách đơn");
            req.getRequestDispatcher("/direct_manager_menu.jsp").forward(req, resp);
        }
    }
}
