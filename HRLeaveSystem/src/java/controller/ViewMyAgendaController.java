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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ViewMyAgendaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/common/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        RequestDAO requestDAO = new RequestDAO();
        List<Request> myRequests = requestDAO.getRequestsByUserId(userId);

        // Xử lý thông báo
        LocalDate today = LocalDate.now();
        List<String> notifications = new ArrayList<>();

        for (Request r : myRequests) {
            LocalDate fromDate = r.getFromDate().toLocalDate();

            if ("Approved".equalsIgnoreCase(r.getStatus())) {
                if (!fromDate.isBefore(today) && !fromDate.isAfter(today.plusDays(7))) {
                    notifications.add("Bạn có ngày nghỉ vào " + fromDate + " đã được duyệt.");
                }
            } else if ("Pending".equalsIgnoreCase(r.getStatus())) {
                if (ChronoUnit.DAYS.between(fromDate, today) >= 7) {
                    notifications.add("Đơn nghỉ ngày " + fromDate + " đang chờ duyệt quá lâu.");
                }
            }
        }

        req.setAttribute("notifications", notifications);
        req.setAttribute("myRequests", myRequests);
        req.getRequestDispatcher("/common/viewOwnAgenda.jsp").forward(req, resp);
    }

}
