package controller;

import dao.DepartmentDAO;
import dao.FeatureDAO;
import dao.RoleDAO;
import dao.UserDAO;
import entity.Department;
import entity.Feature;
import entity.Role;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssignPermissionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }
        try {
            String ajaxFlag = req.getParameter("ajax");

            UserDAO userDAO = new UserDAO();
            DepartmentDAO departmentDAO = new DepartmentDAO();
            RoleDAO roleDAO = new RoleDAO();
            FeatureDAO featureDAO = new FeatureDAO();

            // Xử lý AJAX lấy danh sách managers theo phòng ban
            if ("true".equalsIgnoreCase(ajaxFlag)) {
                int departmentId = Integer.parseInt(req.getParameter("departmentId"));
                List<User> managers = userDAO.getManagersByDepartment(departmentId);

                // Thêm General Manager nếu chưa có
                User generalManager = userDAO.getUserById(55);
                if (generalManager != null && managers.stream().noneMatch(m -> m.getUserId() == 55)) {
                    managers.add(0, generalManager);
                }

                resp.setContentType("text/plain");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                for (User m : managers) {
                    out.println(m.getUserId() + "|" + m.getFullName());
                }
                out.flush();
                return;
            }

            // Bước lấy dữ liệu ban đầu
            String deptRaw = req.getParameter("departmentId");
            String userIdRaw = req.getParameter("userId");

            if (userIdRaw == null) {
                req.setAttribute("error", "Thiếu userId.");
                req.getRequestDispatcher("/error.jsp").forward(req, resp);
                return;
            }

            int userId = Integer.parseInt(userIdRaw);
            User selectedUser = userDAO.getUserById(userId);
            List<Department> departments = departmentDAO.getAllDepartments();
            List<Role> roles = roleDAO.getAllRoles();
            List<Feature> features = featureDAO.getAllFeatures();

            // ✅ Truy cập lần đầu (chưa submit form)
            if (deptRaw == null) {
                int currentDeptId = selectedUser.getDepartment() != null
                        ? selectedUser.getDepartment().getDepartmentId()
                        : departments.get(0).getDepartmentId();

                List<User> managers = userDAO.getManagersByDepartment(currentDeptId);

                // Thêm General Manager nếu cần
                User generalManager = userDAO.getUserById(55);
                if (generalManager != null && managers.stream().noneMatch(m -> m.getUserId() == 55)) {
                    managers.add(0, generalManager);
                }

                // Lấy role hiện tại (nếu có)
                List<Role> currentRoles = roleDAO.getRolesByUser(userId);
                Integer selectedRoleId = currentRoles != null && !currentRoles.isEmpty()
                        ? currentRoles.get(0).getRoleId()
                        : null;

                // Lấy các feature hiện tại
                List<Feature> assignedFeatures = featureDAO.getFeaturesByUserId(userId);
                List<Integer> featureIds = assignedFeatures.stream()
                        .map(Feature::getFeatureId)
                        .collect(Collectors.toList());

                StringBuilder sb = new StringBuilder(",");
                for (Integer fid : featureIds) {
                    sb.append(fid).append(",");
                }

                req.setAttribute("selectedUser", selectedUser);
                req.setAttribute("departments", departments);
                req.setAttribute("managers", managers);
                req.setAttribute("roles", roles);
                req.setAttribute("features", features);
                req.setAttribute("selectedRoleId", selectedRoleId);
                req.setAttribute("assignedFeatureIds", featureIds);
                req.setAttribute("assignedFeatureString", sb.toString());

                req.getRequestDispatcher("/common/assignPermission.jsp").forward(req, resp);
                return;
            }

            // ✅ Trường hợp Submit gán quyền
            int departmentId = Integer.parseInt(deptRaw);
            int roleId = Integer.parseInt(req.getParameter("roleId"));

            String managerIdRaw = req.getParameter("managerId");
            Integer managerId = (managerIdRaw != null && !managerIdRaw.isEmpty())
                    ? Integer.valueOf(managerIdRaw)
                    : null;

            String[] featureIdsRaw = req.getParameterValues("featureIds");
            List<Integer> featureIds = new ArrayList<>();
            if (featureIdsRaw != null) {
                for (String fid : featureIdsRaw) {
                    featureIds.add(Integer.valueOf(fid));
                }
            }

            boolean success = userDAO.assignPermission(userId, departmentId, managerId, roleId, featureIds);

            // Tải lại thông tin để hiển thị lại form
            selectedUser = userDAO.getUserById(userId);
            List<User> managers = userDAO.getManagersByDepartment(departmentId);
            User generalManager = userDAO.getUserById(55);
            if (generalManager != null && managers.stream().noneMatch(m -> m.getUserId() == 55)) {
                managers.add(0, generalManager);
            }

            StringBuilder sb = new StringBuilder(",");
            for (Integer fid : featureIds) {
                sb.append(fid).append(",");
            }

            req.setAttribute("selectedUser", selectedUser);
            req.setAttribute("departments", departments);
            req.setAttribute("managers", managers);
            req.setAttribute("roles", roles);
            req.setAttribute("features", features);
            req.setAttribute("selectedRoleId", roleId);
            req.setAttribute("assignedFeatureIds", featureIds);
            req.setAttribute("assignedFeatureString", sb.toString());

            if (success) {
                req.setAttribute("message", "✅ Gán quyền thành công!");
            } else {
                req.setAttribute("error", "❌ Gán quyền thất bại.");
            }

            req.getRequestDispatcher("/common/assignPermission.jsp").forward(req, resp);

        } catch (ServletException | IOException | NumberFormatException | SQLException ex) {
            throw new ServletException("Lỗi khi xử lý gán quyền", ex);
        }
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
        req.setAttribute("formAction", "assign_permissions");
        req.getRequestDispatcher("/common/allUser.jsp").forward(req, resp);
    }
}
