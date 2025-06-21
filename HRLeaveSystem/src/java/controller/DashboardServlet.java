package controller;

import entity.Role;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        List<Role> roles = (List<Role>) req.getSession().getAttribute("roles");
        boolean isEmployeeOnly = roles.stream().allMatch(role -> role.getRoleName().equals("Employee"));
        if (isEmployeeOnly) {
            req.getRequestDispatcher("/employee/dashboard_employee.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/manager/dashboard_manager.jsp").forward(req, resp);
        }
    }
}
