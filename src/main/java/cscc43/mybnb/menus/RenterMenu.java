package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Booking;
import cscc43.mybnb.entities.CalendarSection;
import cscc43.mybnb.entities.Listing;
import cscc43.mybnb.entities.Renter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    switch (choice) {
      case 1:
        bookListing();
        break;
    }
  }

  public void bookListing() {
    new QueryMenu(connection).start();
    String title = MenuUtils.askString("Enter title of listing.");
    Listing listing = null;
    try {
      // TODO: select when different listings have the same title
      listing = Listing.getAllByTitle(connection, title).get(0);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }

    List<CalendarSection> sections = null;
    try {
      sections = CalendarSection.getAllForListing(connection, listing);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }

    if (sections.size() == 0) {
      System.out.println("No availability for this listing.");
      return;
    }

    String[] sectionNames = new String[sections.size()];
    for (int i = 0; i < sections.size(); i++) {
      CalendarSection cs = sections.get(i);
      sectionNames[i] = String.format("%s - %s", cs.getFrom().toString(), cs.getUntil().toString());
    }

    int choice = MenuUtils.menu("Choose availability", sectionNames);
    CalendarSection bookedSection = sections.get(choice - 1);

    try {
      bookedSection.makeUnavailable(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }

    var booking = new Booking(bookedSection, renter);
    try {
      booking.insert(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
