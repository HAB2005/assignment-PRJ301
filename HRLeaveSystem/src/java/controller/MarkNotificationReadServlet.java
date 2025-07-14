package controller;

import dao.NotificationDAO;
import entity.Notification;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/mark-read")
public class MarkNotificationReadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            User user = (User) session.getAttribute("user");
            int userId = user.getUserId();
            
            NotificationDAO notificationDAO = new NotificationDAO();
            notificationDAO.markAllAsRead(userId); 
            // Cập nhật lại danh sách thông báo đã đọc trong session (nếu cần)
            List<Notification> updatedNotifications = null;
            try {
                updatedNotifications = notificationDAO.getNotificationsByUserId(userId);
            } catch (SQLException ex) {
                Logger.getLogger(MarkNotificationReadServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            session.setAttribute("notifications", updatedNotifications);
            session.setAttribute("unreadCount", 0);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException ex) {
            Logger.getLogger(MarkNotificationReadServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
