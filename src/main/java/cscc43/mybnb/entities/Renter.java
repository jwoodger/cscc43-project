package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Result;

public class Renter extends User {
  private String creditCardNo;

  public static Renter getByUsername(Connection connection, String username) throws SQLException {
    PreparedStatement queryStmt = connection.prepareStatement(
        "SELECT * FROM User JOIN Renter ON User.User_ID = Renter.Renter_ID "
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
      String creditCardNo = results.getString("Credit_Card_No");
      results.close();

      Renter renter = new Renter(dob, firstName, lastName, sin, occupation, username, creditCardNo);
      renter.id = id;
      return renter;
    }
    results.close();
    queryStmt.close();
    return null;
  }

  public static List<Renter> getForHost(Connection connection, Host host) throws SQLException {
    List<Renter> renters = new ArrayList<>();
    var sql = "SELECT * FROM User U JOIN Renter R ON U.User_ID = R.Renter_ID "
      + "WHERE U.User_ID IN "
      + "(SELECT B.Renter_ID FROM Booking B JOIN Calendar_Section C ON B.Calendar_ID = C.Calendar_ID JOIN Listing L ON C.Listing_ID = L.Listing_ID "
      + "WHERE L.Host_ID = ?)";
    var stmt = connection.prepareStatement(sql);
    stmt.setInt(1, host.getId());

    ResultSet results = stmt.executeQuery();
    while (results.next()) {
      int id = results.getInt("User_ID");
      LocalDate dob = results.getDate("DOB").toLocalDate();
      String occupation = results.getString("Occupation");
      String sin = results.getString("SIN");
      String firstName = results.getString("First_Name");
      String lastName = results.getString("Last_Name");
      String creditCardNo = results.getString("Credit_Card_No");
      String username = results.getString("username");

      Renter renter = new Renter(dob, firstName, lastName, sin, occupation, username, creditCardNo);
      renter.id = id;
      renters.add(renter);
    }

    results.close();
    stmt.close();
    return renters;
  }

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
