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
import java.util.List;

public class ViewMyRequestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false); // không tạo mới session

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        // Lấy user từ session
        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        // Lấy danh sách request
        RequestDAO requestDAO = new RequestDAO();
        System.out.println(userId);
        List<Request> myRequests = requestDAO.getRequestsByUserId(userId);
        System.out.println(myRequests);

        // Gửi danh sách request đến JSP
        req.setAttribute("myRequests", myRequests);
        req.getRequestDispatcher("/common/viewOwnRequest.jsp").forward(req, resp);
    }
}
