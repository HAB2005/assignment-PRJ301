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

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        // Lấy thông tin người gửi form và người đang được xem
        User currentUser = (User) session.getAttribute("user");
        int targetUserId = Integer.parseInt(req.getParameter("userId"));

        // Lấy user được chọn và request của họ
        User selectedUser = dao.getUserById(targetUserId);
        List<Request> requests = dao.getRequestsByUser(targetUserId);
        System.out.println(requests);

        // Kiểm tra xem currentUser có quyền duyệt không
        boolean canApprove = dao.hasApprovalPermission(currentUser.getUserId());

        req.setAttribute("selectedUser", selectedUser);
        req.setAttribute("selectedUserRequests", requests);
        req.setAttribute("canApprove", canApprove);

        // Chuyển đến trang hiển thị agenda của cấp dưới
        req.getRequestDispatcher("/common/viewSubordinateAgenda.jsp").forward(req, resp);
    }

}
