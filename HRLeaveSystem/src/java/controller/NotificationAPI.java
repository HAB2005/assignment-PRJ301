package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.NotificationDAO;
import entity.Notification;
import entity.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationAPI extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            List<Notification> notifications = notificationDAO.getNotificationsByUserId(user.getUserId());
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            resp.getWriter().write(gson.toJson(notifications));
        } catch (SQLException ex) {
            Logger.getLogger(NotificationAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
