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

    public Department getDepartmentById(int departmentId) throws SQLException {
        String sql = "SELECT * FROM departments WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department dept = new Department();
                    dept.setDepartmentId(rs.getInt("department_id"));
                    dept.setDepartmentName(rs.getString("department_name"));
                    dept.setHeadOfDepartment(rs.getInt("head_of_department") == 0 ? null : rs.getInt("head_of_department"));
                    return dept;
                }
            }
        }
        return null;
    }
}
