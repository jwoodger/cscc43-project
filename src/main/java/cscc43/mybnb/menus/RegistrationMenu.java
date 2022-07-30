package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Host;
import cscc43.mybnb.entities.Renter;
import cscc43.mybnb.entities.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrationMenu {

  public void start(Connection connection) {
    int hostOrRenter = MenuUtils.menu("Register new user.", "Register as host.", "Register as renter.");
    String username = MenuUtils.askString("Username");
    LocalDate dob = MenuUtils.askDate("Date of birth");
    String firstName = MenuUtils.askString("First name");
    String lastName = MenuUtils.askString("Last name");
    String sin = MenuUtils.askString("SIN");
    String occupation = MenuUtils.askString("Occupation");

    User user;
    if (hostOrRenter == 1) {
      user = new Host(dob, firstName, lastName, sin, occupation, username);
    } else {
      String creditCardNo = MenuUtils.askString("Credit Card #");
      user = new Renter(dob, firstName, lastName, sin, occupation, username, creditCardNo);
    }

    try {
      user.insert(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
    }
  }
}
