package cscc43.mybnb.menus;

import java.sql.Connection;
import java.util.Date;

public class MainMenu {

  public void start(Connection connection) {
    int result = MenuUtils.menu("Welcome to MyBNB!", "Log in.", "Register new user.", "Generate reports.");
    switch (result) {
      case 1:
        login(connection);
        break;
      case 2:
        register(connection);
        break;
      case 3:
        reports(connection);
        break;
    }
  }

  public void login(Connection connection) {
    String username = MenuUtils.askString("Username");
  }

  public void register(Connection connection) {
    int hostOrRenter = MenuUtils.menu("Register new user.", "Register as host.", "Register as renter.");
    Date dob = MenuUtils.askDate("Date of birth");
    String firstName = MenuUtils.askString("First name");
    String lastName = MenuUtils.askString("Last name");
    String sin = MenuUtils.askString("SIN");
    String occupation = MenuUtils.askString("Occupation");
  }

  public void reports(Connection connection) {
    int report = MenuUtils.menu("Choose a report.",
        "Total bookings by city",
        "Total bookings by zipcode",
        "Total number of listings",
        "Rank hosts per country",
        "Rank hosts per city",
        "Possible commercial hosts",
        "Rank renters",
        "Rank renters per city",
        "Most cancellations",
        "Most popular noun phrases for a listing");
  }
}
