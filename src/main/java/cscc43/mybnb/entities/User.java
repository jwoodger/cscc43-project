package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class User {
  private LocalDate dob;
  private String firstName;
  private String lastName;
  private String sin;
  private String occupation;
  private String username;

  public User(LocalDate dob, String firstName, String lastName, String sin, String occupation, String username) {
    this.dob = dob;
    this.firstName = firstName;
    this.lastName = lastName;
    this.sin = sin;
    this.occupation = occupation;
    this.username = username;
  }

  public int insert(Connection connection) throws SQLException {
    var insertStmt = connection.prepareStatement(
        "INSERT INTO User(DOB, Occupation, SIN, First_Name, Last_Name, username)"
           + "VALUES(?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS);
    insertStmt.setObject(1, dob);
    insertStmt.setString(2, occupation);
    insertStmt.setString(3, sin);
    insertStmt.setString(4, firstName);
    insertStmt.setString(5, lastName);
    insertStmt.setString(6, username);

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
