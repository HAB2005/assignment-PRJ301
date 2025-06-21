package dao;

import entity.Department;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;
import java.sql.*;

public class DepartmentDAO {

    public List<Department> getAllDepartments() throws SQLException {
        String sql = "SELECT * FROM departments";
        List<Department> departments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentId(rs.getInt("department_id"));
                dept.setDepartmentName(rs.getString("department_name"));
                dept.setHeadOfDepartment(rs.getInt("head_of_department") == 0 ? null : rs.getInt("head_of_department"));
                departments.add(dept);
            }
        }
        return departments;
    }
}
