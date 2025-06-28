package controller;

import dao.AgendaDAO;
import entity.Request;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class AgendaServlet extends HttpServlet {

    AgendaDAO dao = new AgendaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        // Lấy link người dùng đã click từ menu (từ URI)
        String fullURI = req.getRequestURI(); // ví dụ: /leave_system/department_head/view_and_approve_subordinates'_agenda
        String contextPath = req.getContextPath(); // /leave_system
        String featureLink = fullURI.substring(contextPath.length() + 1); // kết quả: department_head/view_and_approve_subordinates'_agenda

        // Lưu lại đường link để dùng ở view
        req.setAttribute("currentFeatureLink", featureLink);

        // Lấy danh sách cấp dưới
        List<User> subordinates = dao.getSubordinates(currentUser.getUserId());

        req.setAttribute("subordinates", subordinates);
        req.getRequestDispatcher("/common/subordinates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        // Lấy người dùng hiện tại
        User currentUser = (User) session.getAttribute("user");

        // Bắt buộc phải có userId (người đang được xem)
        int targetUserId = Integer.parseInt(req.getParameter("userId"));

        // 🔹 Nếu có thông tin duyệt đơn được gửi lên thì xử lý
        String requestIdParam = req.getParameter("requestId");
        String action = req.getParameter("action"); // Approved hoặc Rejected

        if (requestIdParam != null && action != null) {
            try {
                int requestId = Integer.parseInt(requestIdParam);

                // Gọi DAO để cập nhật trạng thái duyệt đơn
                dao.updateRequestStatus(requestId, action, currentUser.getUserId());
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Log lỗi nếu requestId sai định dạng
            }
        }

        // 🔸 Dù có duyệt hay không, luôn thực hiện các bước dưới đây (giữ nguyên logic cũ)
        User selectedUser = dao.getUserById(targetUserId);
        List<Request> requests = dao.getRequestsByUser(targetUserId);

        boolean canApprove = dao.hasApprovalPermission(currentUser.getUserId());

        req.setAttribute("selectedUser", selectedUser);
        req.setAttribute("selectedUserRequests", requests);
        req.setAttribute("canApprove", canApprove);

        // Lưu lại link để giữ tính năng "quay lại menu"
        String fullURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String featureLink = fullURI.substring(contextPath.length() + 1);
        req.setAttribute("currentFeatureLink", featureLink);

        // Chuyển đến trang hiển thị agenda của cấp dưới
        req.getRequestDispatcher("/common/viewSubordinateAgenda.jsp").forward(req, resp);
    }

}
