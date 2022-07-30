package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Renter extends User {
  private String creditCardNo;

  public Renter(LocalDate dob, String firstName, String lastName, String sin, String occupation, String username, String creditCardNo) {
    super(dob, firstName, lastName, sin, occupation, username);
    this.creditCardNo = creditCardNo;
  }

  @Override public int insert(Connection connection) throws SQLException {
    int userId = super.insert(connection);
    var insertStmt = connection.prepareStatement(
        "INSERT INTO Renter(Renter_ID, Credit_Card_No)"
            + "VALUES(?, ?)",
        Statement.RETURN_GENERATED_KEYS);
    insertStmt.setInt(1, userId);
    insertStmt.setString(2, creditCardNo);

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
