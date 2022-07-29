package cscc43.mybnb;

import cscc43.mybnb.menus.MainMenu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

  public static String loadPassword(String fileName) throws IOException {
    InputStream passwordFile = ClassLoader.getSystemResourceAsStream(fileName);
    BufferedReader reader = new BufferedReader(new InputStreamReader(passwordFile));

    return reader.readLine();
  }

  public static void main(String[] args) throws SQLException {
    String password;
    try {
      password = loadPassword("password.txt");
    } catch (IOException exception) {
      System.err.println("Could not find password file.");
      return;
    }

    String url = "jdbc:mysql://localhost:3306";
    Connection cxn = DriverManager.getConnection(url, "root", password);

    new MainMenu().start(cxn);

    cxn.close();
  }
}
