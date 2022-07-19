package cscc43.mybnb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
  private static final String PASSWORD = "INSERT PASSWORD HERE";

  public static void main(String[] args) throws SQLException {
    String url = "jdbc:mysql://localhost:3306";
    Connection cxn = DriverManager.getConnection(url, "root", PASSWORD);
    cxn.close();
  }
}
