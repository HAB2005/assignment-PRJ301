package dao;

import entity.RequestApproval;
import java.sql.*;
import util.DBConnection;

public class RequestApprovalDAO {

    public void createApproval(RequestApproval approval) throws SQLException {
        String sql = "INSERT INTO request_approvals (request_id, approver_id, decision, comments) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, approval.getRequestId());
            stmt.setInt(2, approval.getApproverId());
            stmt.setString(3, approval.getDecision());
            stmt.setString(4, approval.getComments());
            stmt.executeUpdate();
        }
    }
}
