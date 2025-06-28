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

public class ApprovalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int requestId = Integer.parseInt(req.getParameter("requestId"));
            int userId = Integer.parseInt(req.getParameter("userId"));
            String action = req.getParameter("action"); // "Approved" hoặc "Rejected"

            User currentUser = (User) session.getAttribute("user");

            // Cập nhật trạng thái đơn xin nghỉ
            AgendaDAO dao = new AgendaDAO();
            dao.updateRequestStatus(requestId, action, currentUser.getUserId());

            // Chuyển tiếp về lại servlet hiển thị agenda (giống doPost của AgendaServlet)
            // Gọi lại dữ liệu để cập nhật trạng thái mới
            User selectedUser = dao.getUserById(userId);
            List<Request> requests = dao.getRequestsByUser(userId);
            boolean canApprove = dao.hasApprovalPermission(currentUser.getUserId());

            req.setAttribute("selectedUser", selectedUser);
            req.setAttribute("selectedUserRequests", requests);
            req.setAttribute("canApprove", canApprove);

            // Lấy lại link để hiển thị nút quay về menu
            String fullURI = req.getRequestURI();
            String contextPath = req.getContextPath();
            String featureLink = fullURI.substring(contextPath.length() + 1);
            req.setAttribute("currentFeatureLink", "agenda/view_and_approve_subordinates'_agenda"); // hoặc lấy từ session nếu có lưu

            req.getRequestDispatcher("/common/viewSubordinateAgenda.jsp").forward(req, resp);

        } catch (ServletException | IOException | NumberFormatException e) {
            resp.sendError(500, "Lỗi xử lý yêu cầu duyệt đơn.");
        }
    }
}
