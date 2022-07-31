package cscc43.mybnb.menus;

import java.sql.Connection;

public class MainMenu {
  private Connection connection;

  public MainMenu(Connection connection) {
    this.connection = connection;
  }

  public void start() {
    int result = 0;

    while (result != 4) {
      result = MenuUtils.menu("Welcome to MyBNB!",
          "Log in.",
          "Register new user.",
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
          reports();
          break;
      }
    }
  }

  public void reports() {
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
