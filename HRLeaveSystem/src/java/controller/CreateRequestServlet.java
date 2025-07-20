package controller;

import dao.LeaveTypeDAO;
import dao.NotificationDAO;
import dao.RequestDAO;
import entity.LeaveType;
import entity.Notification;
import entity.Request;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.sql.*;

public class CreateRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String fromDateStr = req.getParameter("fromDate");
            String toDateStr = req.getParameter("toDate");
            int leaveTypeId = Integer.parseInt(req.getParameter("leaveTypeId"));
            String reason = req.getParameter("reason");

            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");

            java.sql.Date fromDate = java.sql.Date.valueOf(fromDateStr);
            java.sql.Date toDate = java.sql.Date.valueOf(toDateStr);

            // ✅ Tạo LeaveType và gán ID
            LeaveType leaveType = new LeaveType();
            leaveType.setLeaveTypeId(leaveTypeId);

            // ✅ Tạo đối tượng Request
            Request leaveRequest = new Request();
            leaveRequest.setUserId(user.getUserId());
            leaveRequest.setFromDate(fromDate);
            leaveRequest.setToDate(toDate);
            leaveRequest.setLeaveType(leaveType);
            leaveRequest.setReason(reason);

            // ✅ Gọi DAO để lưu đơn nghỉ
            RequestDAO dao = new RequestDAO();
            dao.createRequest(leaveRequest);

            // ✅ Gửi thông báo cho quản lý nếu có
            if (user.getManagerId() != 0) {
                NotificationDAO notificationDAO = new NotificationDAO();

                Notification noti = new Notification();
                noti.setUserId(user.getManagerId());
                noti.setMessage(user.getFullName() + " đã gửi một yêu cầu nghỉ phép từ "
                        + fromDateStr + " đến " + toDateStr + " cần bạn duyệt.");
                noti.setIsRead(false);
                noti.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                notificationDAO.addNotification(noti);
            }

            // ✅ Gửi thông báo thành công và forward lại trang tạo đơn
            session.setAttribute("successMessage", "Gửi yêu cầu nghỉ phép thành công!");

            LeaveTypeDAO leaveTypeDAO = new LeaveTypeDAO();
            List<LeaveType> leaveTypes = leaveTypeDAO.getAll();
            req.setAttribute("leaveTypes", leaveTypes);

            req.getRequestDispatcher("/common/createRequest.jsp").forward(req, resp);

        } catch (IOException | NumberFormatException | SQLException e) {
            req.setAttribute("error", "Có lỗi xảy ra khi gửi yêu cầu nghỉ phép.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            LeaveTypeDAO leaveTypeDAO = new LeaveTypeDAO();
            List<LeaveType> leaveTypes = leaveTypeDAO.getAll();
            req.setAttribute("leaveTypes", leaveTypes);
            req.getRequestDispatcher("/common/createRequest.jsp").forward(req, resp);
        } catch (ServletException | IOException | SQLException e) {
        }
    }
}
