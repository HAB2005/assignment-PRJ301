package controller;

import dao.LeaveTypeDAO;
import dao.RequestDAO;
import entity.LeaveType;
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

public class CreateRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String fromDateStr = req.getParameter("fromDate");
            String toDateStr = req.getParameter("toDate");
            int leaveTypeId = Integer.parseInt(req.getParameter("leaveTypeId"));
            String reason = req.getParameter("reason");

            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");

            java.sql.Date fromDate = java.sql.Date.valueOf(fromDateStr);
            java.sql.Date toDate = java.sql.Date.valueOf(toDateStr);

            Request leaveRequest = new Request();
            leaveRequest.setUserId(user.getUserId());
            leaveRequest.setFromDate(fromDate);
            leaveRequest.setToDate(toDate);
            leaveRequest.setLeaveTypeId(leaveTypeId);
            leaveRequest.setReason(reason);

            RequestDAO dao = new RequestDAO();
            dao.createRequest(leaveRequest);

            // Đặt thông báo vào session rồi redirect lại trang form
            session.setAttribute("successMessage", "Gửi yêu cầu nghỉ phép thành công!");
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
            List<LeaveType> leaveTypes = leaveTypeDAO.getAll(); // Lấy danh sách từ DB
            req.setAttribute("leaveTypes", leaveTypes); // Đưa vào request scope
            req.getRequestDispatcher("/common/createRequest.jsp").forward(req, resp);
        } catch (ServletException | IOException | SQLException e) {
        }
    }
}
