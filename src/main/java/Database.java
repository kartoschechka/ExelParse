import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/schedule?useUnicode=true&characterEncoding=UTF8";
    private static final String USER = "root";
    private static final String PASSWORD = "verbessern";
    private static Connection connection;

    private Database() {

    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection != null) {
            return connection;
        } else {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("connected");

            } catch (SQLException ex) {
                System.out.println("то то пошло не так с БД");
                ex.printStackTrace();
            }

        }
        return connection;
    }
}
