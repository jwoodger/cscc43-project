package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Amenity;
import cscc43.mybnb.entities.CalendarSection;
import cscc43.mybnb.entities.Host;
import cscc43.mybnb.entities.Listing;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HostMenu {
  private Connection connection;
  private Host host;

  public HostMenu(Connection connection, Host host) {
    this.connection = connection;
    this.host = host;
  }

  public void start() {
    String prompt = String.format("Logged in as %s", host.getUsername());
    int choice = MenuUtils.menu(prompt, "Create listing", "Create availability", "Comment on user");
    switch (choice) {
    case 1:
      createListing();
      break;
    case 2:
      createCalendarSection();
      break;
    }
  }

  public void createListing() {
    String title = MenuUtils.askString("Title of listing");
    String address = MenuUtils.askString("Street address");
    String city = MenuUtils.askString("City");
    String country = MenuUtils.askString("Country");
    String postalCode = MenuUtils.askString("Postal code");
    double latitude = MenuUtils.askDouble("Latitude");
    double longitude = MenuUtils.askDouble("Longitude");

    var listing = new Listing(host, title, address, city, country, postalCode, latitude, longitude);
    addAmenities(listing);

    try {
      listing.insert(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  public void addAmenities(Listing listing) {
    List<Amenity> amenities = null;
    try {
      amenities = Amenity.getAll(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }

    amenities.removeIf(a -> listing.hasAmenity(a));

    boolean finished = false;

    while (!finished) {
      String[] options = new String[amenities.size() + 1];
      for (int i = 0; i < amenities.size(); i++) {
        options[i] = amenities.get(i).getName();
      }
      options[amenities.size()] = "Finish";

      int choice = MenuUtils.menu("Add amenity", options);
      if (choice == amenities.size() + 1) {
        finished = true;
      } else {
        listing.addAmenity(amenities.remove(choice - 1));
      }
    }
  }

  public void createCalendarSection() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] names = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      names[i] = listings.get(i).getTitle();
    }
    int choice = MenuUtils.menu("Choose a listing:", names);
    Listing listing = listings.get(choice - 1);

    LocalDate dateFrom = MenuUtils.askDate("First day available");
    LocalDate dateTo = MenuUtils.askDate("Last day available");
    float price = (float) MenuUtils.askDouble("Asking price");

    var section = new CalendarSection(dateFrom, dateTo, listing, price);
    try {
      section.insert(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
