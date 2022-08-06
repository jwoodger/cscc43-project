package cscc43.mybnb.menus;

import cscc43.mybnb.entities.User;
import cscc43.mybnb.reports.NounPhraseReport;
import java.sql.Connection;
import java.sql.SQLException;

public class MainMenu {
  private Connection connection;

  public MainMenu(Connection connection) {
    this.connection = connection;
  }

  public void start() {
    int result = 0;

    while (result != 5) {
      result = MenuUtils.menu("Welcome to MyBNB!",
          "Log in.",
          "Register new user.",
          "Delete user.",
          "Generate reports.",
          "Quit");
      switch (result) {
        case 1:
          new LoginMenu(connection).start();
          break;
        case 2:
          new RegistrationMenu(connection).start();
          break;
        case 3:
          deleteUser();
          break;
        case 4:
          reports();
          break;
      }
    }
  }

  public void deleteUser() {
    String username = MenuUtils.askString("Username to delete.");
    try {
      User.deleteByUsername(connection, username);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  public void reports() {
    int choice = MenuUtils.menu("Choose a report.",
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
    switch (choice) {
      case 10:
        new NounPhraseReport(connection).run();
        break;
    }
  }
}
