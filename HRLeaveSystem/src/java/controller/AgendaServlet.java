package controller;

import dao.AgendaDAO;
import dao.DepartmentDAO;
import dao.NotificationDAO;
import dao.RequestDAO;
import dao.RoleDAO;
import entity.Department;
import entity.Notification;
import entity.Request;
import entity.RequestApproval;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgendaServlet extends HttpServlet {

    RequestDAO requestDAO = new RequestDAO();
    AgendaDAO agendaDAO = new AgendaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String userIdParam = req.getParameter("userId");

        String servletPath = req.getServletPath();
        String[] pathParts = servletPath.split("/");

        String role = pathParts.length > 1 ? pathParts[1] : "";
        String featureName = pathParts.length > 2 ? pathParts[2] : "";
        String currentFeatureLink = role + "/" + featureName;

        if ("view_own_agenda".equals(featureName)) {
            List<Request> requests = requestDAO.getRequestsByUserId(currentUser.getUserId());
            req.setAttribute("selectedUserRequests", requests);

            Map<Integer, String> requestIdToComment = agendaDAO.getApprovalCommentsForRequests(
                    requests.stream().map(Request::getRequestId).collect(Collectors.toList())
            );
            req.setAttribute("requestIdToComment", requestIdToComment);
            req.setAttribute("selectedUser", currentUser);
            req.setAttribute("canApprove", false);
            req.setAttribute("currentFeatureLink", currentFeatureLink);

            req.getRequestDispatcher("/common/viewAgenda.jsp").forward(req, resp);
            return;
        }

        if (userIdParam == null) {
            try {
                List<User> subordinates = agendaDAO.getSubordinates(currentUser.getUserId());
                req.setAttribute("subordinates", subordinates);

                List<Request> pendingRequests = requestDAO.getPendingRequestsByManager(currentUser.getUserId());
                Set<Integer> pendingUserIds = pendingRequests.stream()
                        .map(Request::getUserId)
                        .collect(Collectors.toSet());
                req.setAttribute("pendingUserIds", pendingUserIds);

                DepartmentDAO departmentDAO = new DepartmentDAO();
                List<Department> departments = departmentDAO.getAllDepartments();
                req.setAttribute("departments", departments);

                // ✅ Gọi RoleDAO để lấy danh sách role của cấp dưới
                RoleDAO roleDAO = new RoleDAO();
                List<String> roleGroups = roleDAO.getRoleNamesOfSubordinates(currentUser.getUserId());
                req.setAttribute("roleGroups", roleGroups);

                req.setAttribute("currentFeatureLink", currentFeatureLink);

                req.getRequestDispatcher("/common/subordinates.jsp").forward(req, resp);
                return;
            } catch (SQLException e) {
                throw new ServletException("Lỗi khi tải danh sách cấp dưới", e);
            }
        }

        try {
            int viewingUserId = Integer.parseInt(userIdParam);

            List<Request> requests = requestDAO.getRequestsByUserId(viewingUserId);
            req.setAttribute("selectedUserRequests", requests);

            Map<Integer, String> requestIdToComment = agendaDAO.getApprovalCommentsForRequests(
                    requests.stream().map(Request::getRequestId).collect(Collectors.toList())
            );
            req.setAttribute("requestIdToComment", requestIdToComment);

            User selectedUser = agendaDAO.getUserById(viewingUserId);
            req.setAttribute("selectedUser", selectedUser);

            boolean isViewingSelf = (viewingUserId == currentUser.getUserId());

            boolean canApprove = false;
            if ("view_and_approve_subordinates'_agenda".equals(featureName) && !isViewingSelf) {
                canApprove = agendaDAO.hasApprovalPermission(currentUser.getUserId());
            }

            req.setAttribute("canApprove", canApprove);
            req.setAttribute("currentFeatureLink", currentFeatureLink);

            req.getRequestDispatcher("/common/viewAgenda.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        }
    }

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        req.setCharacterEncoding("UTF-8");
//
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
//            return;
//        }
//
//        User currentUser = (User) session.getAttribute("user");
//        String requestIdParam = req.getParameter("requestId");
//        String action = req.getParameter("action");
//        String comment = req.getParameter("comment");
//        String userIdParam = req.getParameter("userId");
//
//        String servletPath = req.getServletPath();
//        System.out.println(servletPath);
//        String[] pathParts = servletPath.split("/");
//
//        String featureName = pathParts.length > 2 ? pathParts[2] : "";
//
//        int targetUserId;
//        try {
//            targetUserId = Integer.parseInt(userIdParam);
//        } catch (NumberFormatException e) {
//            resp.sendRedirect(req.getContextPath() + servletPath);
//            return;
//        }
//
//        if (requestIdParam != null && action != null) {
//            try {
//                int requestId = Integer.parseInt(requestIdParam);
//
//                boolean canApprove = "view_and_approve_subordinates'_agenda".equals(featureName)
//                        && targetUserId != currentUser.getUserId()
//                        && agendaDAO.hasApprovalPermission(currentUser.getUserId());
//
//                if (canApprove) {
//                    RequestApproval approval = new RequestApproval();
//                    approval.setRequestId(requestId);
//                    approval.setApproverId(currentUser.getUserId());
//                    approval.setDecision(action);
//                    approval.setComments(comment);
//
//                    agendaDAO.insertApprovalIfNotExists(approval);
//                } else {
//                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền duyệt đơn này.");
//                    return;
//                }
//            } catch (NumberFormatException e) {                
//            }
//        }
//
//        resp.sendRedirect(req.getContextPath() + servletPath + "?userId=" + targetUserId);
//    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String requestIdParam = req.getParameter("requestId");
        String action = req.getParameter("action");
        String comment = req.getParameter("comment");
        String userIdParam = req.getParameter("userId");

        String servletPath = req.getServletPath();
        String[] pathParts = servletPath.split("/");

        String featureName = pathParts.length > 2 ? pathParts[2] : "";

        int targetUserId;
        try {
            targetUserId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + servletPath);
            return;
        }

        if (requestIdParam != null && action != null) {
            try {
                int requestId = Integer.parseInt(requestIdParam);

                boolean canApprove = "view_and_approve_subordinates'_agenda".equals(featureName)
                        && targetUserId != currentUser.getUserId()
                        && agendaDAO.hasApprovalPermission(currentUser.getUserId());

                if (canApprove) {
                    RequestApproval approval = new RequestApproval();
                    approval.setRequestId(requestId);
                    approval.setApproverId(currentUser.getUserId());
                    approval.setDecision(action);
                    approval.setComments(comment);

                    agendaDAO.insertApprovalIfNotExists(approval);

                    // ✅ Gửi thông báo sau khi duyệt
                    String message = "Đơn xin nghỉ của bạn đã được "
                            + (action.equalsIgnoreCase("approved") ? "phê duyệt" : "từ chối")
                            + " bởi " + currentUser.getFullName();

                    Notification notification = new Notification();
                    notification.setUserId(targetUserId);
                    notification.setMessage(message);
                    notification.setIsRead(false); // chưa đọc
                    notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                    NotificationDAO notificationDAO = new NotificationDAO();
                    try {
                        notificationDAO.addNotification(notification);
                    } catch (SQLException ex) {
                        Logger.getLogger(AgendaServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền duyệt đơn này.");
                    return;
                }
            } catch (NumberFormatException e) {
                throw new ServletException("Lỗi xử lý đơn và tạo thông báo", e);
            }
        }

        resp.sendRedirect(req.getContextPath() + servletPath + "?userId=" + targetUserId);
    }
}
