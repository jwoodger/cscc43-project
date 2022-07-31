package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Renter;
import java.sql.Connection;

public class RenterMenu {
  private Connection connection;
  private Renter renter;

  public RenterMenu(Connection connection, Renter renter) {
    this.connection = connection;
    this.renter = renter;
  }

  public void start() {
    String prompt = String.format("Logged in as %s", renter.getUsername());
    int choice = MenuUtils.menu(prompt, "Book listing", "Cancel booking", "Comment on listing");
  }
}
