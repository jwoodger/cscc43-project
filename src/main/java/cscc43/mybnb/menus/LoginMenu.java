package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Host;
import cscc43.mybnb.entities.Renter;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginMenu {
  private Connection connection;

  public LoginMenu(Connection connection) {
    this.connection = connection;
  }

  public void start() {
    int hostOrRenter = MenuUtils.menu("Login as...", "Host", "Renter");
    String username = MenuUtils.askString("Username");

    if (hostOrRenter == 1) {
      Host host = loginHost(username);
      new HostMenu(connection, host).start();
    } else {
      Renter renter = loginRenter(username);
      new RenterMenu(connection, renter).start();
    }
  }

  public Renter loginRenter(String username) {
    try {
      Renter renter = Renter.getByUsername(connection, username);
      return renter;
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    return null;
  }

  public Host loginHost(String username) {
    try {
      Host host = Host.getByUsername(connection, username);
      return host;
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    return null;
  }
}
