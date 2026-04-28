package akademik;

import java.sql.*;

public class DBConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "akademik_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&serverTimezone=UTC";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Gagal konek ke database: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error menutup koneksi: " + e.getMessage());
        }
    }
}
