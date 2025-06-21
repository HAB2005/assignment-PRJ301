package util;

import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=assignment.prj;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "123456";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
