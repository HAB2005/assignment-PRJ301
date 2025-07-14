package dao;

import entity.Notification;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import util.DBConnection;

public class NotificationDAO {

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT notification_id, user_id, message, is_read, created_at "
                + "FROM notification WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setNotificationId(rs.getInt("notification_id"));
                n.setUserId(rs.getInt("user_id"));
                n.setMessage(rs.getString("message"));
                n.setIsRead(rs.getBoolean("is_read"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt == null) {
                    createdAt = new Timestamp(System.currentTimeMillis());
                }
                n.setCreatedAt(createdAt);

                notifications.add(n);
            }
        }

        return notifications;
    }

    public void addNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notification (user_id, message, is_read, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setBoolean(3, notification.isRead());
            stmt.setTimestamp(4, notification.getCreatedAt());

            stmt.executeUpdate();
        }
    }

    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE Notification SET is_read = 1 WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

}
