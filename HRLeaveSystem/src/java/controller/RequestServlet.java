package controller;

import dao.RequestDAO;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lấy user từ session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Chưa đăng nhập, chuyển về trang login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // Gọi DAO để lấy danh sách đơn nghỉ phép theo userId
        RequestDAO dao = new RequestDAO();
        List<Request> leaveRequests = null;
        try {
            leaveRequests = dao.getRequestsByUserId(user.getUserId());
        } catch (SQLException ex) {
            Logger.getLogger(RequestServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Gửi danh sách đơn về JSP
        req.setAttribute("leaveRequests", leaveRequests);
        // Chuyển tiếp đến trang hiển thị
        req.getRequestDispatcher("/employee/view_own_leave_requests.jsp").forward(req, resp);
    }
}
