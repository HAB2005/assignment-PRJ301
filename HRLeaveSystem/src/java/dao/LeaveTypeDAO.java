package dao;

import entity.LeaveType;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import util.DBConnection;

public class LeaveTypeDAO {

    public List<LeaveType> getAllLeaveTypes() throws SQLException {
        String sql = "SELECT * FROM leave_types";
        List<LeaveType> leaveTypes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LeaveType type = new LeaveType();
                type.setLeaveTypeId(rs.getInt("leave_type_id"));
                type.setTypeName(rs.getString("type_name"));
                leaveTypes.add(type);
            }
        }
        return leaveTypes;
    }
}
