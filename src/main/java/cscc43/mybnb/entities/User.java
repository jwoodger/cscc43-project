package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
  protected int id;

  public static boolean deleteByUsername(Connection connection, String username) throws SQLException {
    PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM User WHERE STRCMP(User.username, ?) = 0");
    deleteStmt.setString(1, username);

    int affected = deleteStmt.executeUpdate();
    return affected > 0;
  }
  public static String getUsernameFromID(Connection c,int id) throws SQLException{
    PreparedStatement s = c.prepareStatement("select username from user where user_id = ?");
    s.setInt(1,id);
    ResultSet r = s.executeQuery();
    while (r.next()){
      return r.getString("username");
    }
    return "ERROR";
  }
  public User(LocalDate dob, String firstName, String lastName, String sin, String occupation, String username) {
    this.dob = dob;
    this.firstName = firstName;
    this.lastName = lastName;
    this.sin = sin;
    this.occupation = occupation;
    this.username = username;
    id = 0;
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
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
        int id = results.getInt(1);
        results.close();
        insertStmt.close();
        return id;
      }
      results.close();
    }
    insertStmt.close();;
    return -1;
  }
}
