package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Host;
import cscc43.mybnb.entities.Listing;
import java.sql.Connection;
import java.sql.SQLException;

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
    if (choice == 1) {
      createListing();
    }
  }

  public void createListing() {
    String title = MenuUtils.askString("Title of listing");
    String address = MenuUtils.askString("Street address");
    String city = MenuUtils.askString("City");
    String province = MenuUtils.askString("Province (may leave blank)");
    String country = MenuUtils.askString("Country");
    String postalCode = MenuUtils.askString("Postal code");
    double latitude = MenuUtils.askDouble("Latitude");
    double longitude = MenuUtils.askDouble("Longitude");
    float price = (float) MenuUtils.askDouble("Rental price");

    var listing = new Listing(host, title, address, city, province, country, postalCode, latitude, longitude, price);
    try {
      listing.insert(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
