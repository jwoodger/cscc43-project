package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Host extends User {

  public static Host getByUsername(Connection connection, String username) throws SQLException {
    PreparedStatement queryStmt = connection.prepareStatement(
        "SELECT * FROM User JOIN Host ON User.User_ID = Host.Host_ID "
            + "WHERE STRCMP(User.username, ?) = 0");
    queryStmt.setString(1, username);

    ResultSet results = queryStmt.executeQuery();
    if (results.next()) {
      int id = results.getInt("User_ID");
      LocalDate dob = results.getDate("DOB").toLocalDate();
      String occupation = results.getString("Occupation");
      String sin = results.getString("SIN");
      String firstName = results.getString("First_Name");
      String lastName = results.getString("Last_Name");
      results.close();

      Host host = new Host(dob, firstName, lastName, sin, occupation, username);
      host.id = id;
      return host;
    }
    results.close();
    return null;
  }

  public Host(LocalDate dob, String firstName, String lastName, String sin, String occupation, String username) {
    super(dob, firstName, lastName, sin, occupation, username);
  }

  @Override public int insert(Connection connection) throws SQLException {
    int userId = super.insert(connection);
    var insertStmt = connection.prepareStatement(
        "INSERT INTO Host(Host_ID)"
        + "VALUES(?)",
        Statement.RETURN_GENERATED_KEYS);
    insertStmt.setInt(1, userId);

    int affected =  insertStmt.executeUpdate();
    if (affected > 0) {
      ResultSet results = insertStmt.getGeneratedKeys();
      if (results.next()) {
        results.close();
        insertStmt.close();
        return results.getInt(1);
      }
      results.close();
    }
    insertStmt.close();;
    return -1;
  }
}
