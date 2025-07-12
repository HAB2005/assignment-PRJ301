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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignPermissionServlet extends HttpServlet {

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            int userId = Integer.parseInt(req.getParameter("userId"));
//
//            UserDAO userDAO = new UserDAO();
//            DepartmentDAO departmentDAO = new DepartmentDAO();
//            RoleDAO roleDAO = new RoleDAO();
//            FeatureDAO featureDAO = new FeatureDAO();
//
//            User selectedUser = userDAO.getUserById(userId);
//            List<Department> departments = departmentDAO.getAllDepartments();
//            List<Role> roles = roleDAO.getAllRoles();
//            List<Feature> features = featureDAO.getAllFeatures();
//
//            String deptRaw = req.getParameter("departmentId");
//
//            // Trường hợp đầu tiên: chỉ mới nhấn "Edit", chưa chọn gán quyền
//            if (deptRaw == null) {
//                List<User> managers = selectedUser.getDepartment() != null
//                        ? userDAO.getAvailableManagers(selectedUser.getDepartment().getDepartmentId())
//                        : new ArrayList<>();
//
//                req.setAttribute("selectedUser", selectedUser);
//                req.setAttribute("departments", departments);
//                req.setAttribute("managers", managers);
//                req.setAttribute("roles", roles);
//                req.setAttribute("features", features);
//                req.setAttribute("selectedRoleId", null);
//                req.setAttribute("assignedFeatureIds", new ArrayList<>());
//                req.setAttribute("assignedFeatureString", ",");
//
//                req.getRequestDispatcher("/common/assignPermission.jsp").forward(req, resp);
//                return;
//            }
//
//            // Xử lý gán quyền (sau khi người dùng chọn xong và nhấn Submit)
//            int departmentId = Integer.parseInt(deptRaw);
//            int roleId = Integer.parseInt(req.getParameter("roleId"));
//
//            String managerIdRaw = req.getParameter("managerId");
//            Integer managerId = (managerIdRaw != null && !managerIdRaw.isEmpty())
//                    ? Integer.valueOf(managerIdRaw) : null;
//
//            String[] featureIdsRaw = req.getParameterValues("featureIds");
//            List<Integer> featureIds = new ArrayList<>();
//            if (featureIdsRaw != null) {
//                for (String fid : featureIdsRaw) {
//                    featureIds.add(Integer.valueOf(fid));
//                }
//            }
//
//            boolean success = userDAO.assignPermission(userId, departmentId, managerId, roleId, featureIds);
//
//            // Cập nhật lại dữ liệu sau khi gán quyền thành công/thất bại
//            selectedUser = userDAO.getUserById(userId);
//            List<User> managers = selectedUser.getDepartment() != null
//                    ? userDAO.getAvailableManagers(selectedUser.getDepartment().getDepartmentId())
//                    : new ArrayList<>();
//
//            // Chuỗi featureIds để dùng với fn:contains trong JSP
//            StringBuilder sb = new StringBuilder(",");
//            for (Integer fid : featureIds) {
//                sb.append(fid).append(",");
//            }
//
//            req.setAttribute("selectedUser", selectedUser);
//            req.setAttribute("departments", departments);
//            req.setAttribute("managers", managers);
//            req.setAttribute("roles", roles);
//            req.setAttribute("features", features);
//            req.setAttribute("selectedRoleId", roleId);
//            req.setAttribute("assignedFeatureIds", featureIds);
//            req.setAttribute("assignedFeatureString", sb.toString());
//
//            if (success) {
//                req.setAttribute("message", "Gán quyền thành công!");
//            } else {
//                req.setAttribute("error", "Gán quyền thất bại.");
//            }
//
//            req.getRequestDispatcher("/common/assignPermission.jsp").forward(req, resp);
//
//        } catch (ServletException | IOException | NumberFormatException | SQLException ex) {
//            Logger.getLogger(AssignPermissionServlet.class.getName()).log(Level.SEVERE, null, ex);
//            throw new ServletException(ex);
//        }
//    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("=== AssignPermissionServlet: doPost called ===");
        try {
            // B1: Nếu là AJAX yêu cầu danh sách quản lý theo phòng ban
            String ajaxFlag = req.getParameter("ajax");
            if ("true".equalsIgnoreCase(ajaxFlag)) {
                int departmentId = Integer.parseInt(req.getParameter("departmentId"));
                UserDAO userDAO = new UserDAO();

                List<User> managers = userDAO.getManagersByDepartment(departmentId); // lấy đúng người quản lý

                resp.setContentType("text/plain");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();

                for (User m : managers) {
                    out.println(m.getUserId() + "|" + m.getFullName());
                }

                out.flush();
                return;
            }

            // B2: Nếu là bước đầu tiên từ trang danh sách người dùng (chỉ có userId gửi sang)
            String deptRaw = req.getParameter("departmentId"); // Nếu null là chưa submit
            String userIdRaw = req.getParameter("userId");
            if (userIdRaw == null) {
                req.setAttribute("error", "Thiếu userId.");
                req.getRequestDispatcher("/error.jsp").forward(req, resp);
                return;
            }

            int userId = Integer.parseInt(userIdRaw);
            UserDAO userDAO = new UserDAO();
            DepartmentDAO departmentDAO = new DepartmentDAO();
            RoleDAO roleDAO = new RoleDAO();
            FeatureDAO featureDAO = new FeatureDAO();

            User selectedUser = userDAO.getUserById(userId);
            List<Department> departments = departmentDAO.getAllDepartments();
            List<Role> roles = roleDAO.getAllRoles();
            List<Feature> features = featureDAO.getAllFeatures();

            if (deptRaw == null) {
                List<User> managers = new ArrayList<>();
                int initialDeptId = -1;

                if (selectedUser.getDepartment() != null) {
                    initialDeptId = selectedUser.getDepartment().getDepartmentId();
                    managers = userDAO.getManagersByDepartment(initialDeptId);
                    System.out.println("User đã có phòng ban, deptId = " + initialDeptId);
                } else if (!departments.isEmpty()) {
                    initialDeptId = departments.get(0).getDepartmentId(); // Phòng ban đầu tiên trong danh sách
                    managers = userDAO.getManagersByDepartment(initialDeptId);
                    System.out.println("User chưa có phòng ban. Tạm lấy managers của deptId = " + initialDeptId);
                }

                req.setAttribute("selectedUser", selectedUser);
                req.setAttribute("departments", departments);
                req.setAttribute("managers", managers);
                req.setAttribute("roles", roles);
                req.setAttribute("features", features);
                req.setAttribute("selectedRoleId", null);
                req.setAttribute("assignedFeatureIds", new ArrayList<>());
                req.setAttribute("assignedFeatureString", ",");

                req.getRequestDispatcher("/common/assignPermission.jsp").forward(req, resp);
                return;
            }

            // B3: Nếu là bước nhấn Submit để gán quyền
            int departmentId = Integer.parseInt(deptRaw);
            int roleId = Integer.parseInt(req.getParameter("roleId"));

            String managerIdRaw = req.getParameter("managerId");
            Integer managerId = (managerIdRaw != null && !managerIdRaw.isEmpty()) ? Integer.valueOf(managerIdRaw) : null;

            String[] featureIdsRaw = req.getParameterValues("featureIds");
            List<Integer> featureIds = new ArrayList<>();
            if (featureIdsRaw != null) {
                for (String fid : featureIdsRaw) {
                    featureIds.add(Integer.valueOf(fid));
                }
            }

            boolean success = userDAO.assignPermission(userId, departmentId, managerId, roleId, featureIds);

            // Load lại để hiển thị form sau khi gán xong
            selectedUser = userDAO.getUserById(userId);
            List<User> managers = userDAO.getManagersByDepartment(departmentId);

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
                req.setAttribute("message", "Gán quyền thành công!");
            } else {
                req.setAttribute("error", "Gán quyền thất bại.");
            }

            req.getRequestDispatcher("/common/assignPermission.jsp").forward(req, resp);

        } catch (ServletException | IOException | NumberFormatException | SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        List<User> userList = dao.getAllUsers();
        req.setAttribute("userList", userList);
        req.setAttribute("formAction", "assign_permissions");
        req.getRequestDispatcher("/common/allUser.jsp").forward(req, resp);
    }
}
