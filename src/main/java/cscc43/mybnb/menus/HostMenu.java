package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Host;
import java.sql.Connection;

public class HostMenu {
  private Connection connection;
  private Host host;

  public HostMenu(Connection connection, Host host) {
    this.connection = connection;
    this.host = host;
  }

  public void start() {
    String prompt = String.format("Logged in as %s", host.getUsername());
    int choice = MenuUtils.menu(prompt, "Create listing", "Edit listing", "Cancel booking", "Comment on user");
  }
}
