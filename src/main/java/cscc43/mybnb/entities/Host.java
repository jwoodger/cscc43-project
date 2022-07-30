package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Host extends User {

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
        return results.getInt(1);
      }
    }
    return -1;
  }
}
