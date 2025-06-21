package dao;

import entity.LeaveType;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import util.DBConnection;

public class LeaveTypeDAO {

    public List<LeaveType> getAll() throws SQLException {
        List<LeaveType> list = new ArrayList<>();
        String sql = "SELECT leave_type_id, type_name FROM leave_types";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new LeaveType(rs.getInt("leave_type_id"), rs.getString("type_name")));
            }
        }
        return list;
    }
}
