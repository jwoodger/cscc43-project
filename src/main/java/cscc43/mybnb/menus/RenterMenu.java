package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Booking;
import cscc43.mybnb.entities.CalendarSection;
import cscc43.mybnb.entities.Comment;
import cscc43.mybnb.entities.Listing;
import cscc43.mybnb.entities.Renter;
import cscc43.mybnb.entities.RenterComment;
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
      case 3:
        comment();
        break;
    }
  }

  public void bookListing() {
    boolean results = new QueryMenu(connection).start();
    if(!results)return;
    // TODO: Assume the user sees the print out from query menu and just inputs an calendar ID
    // TODO: check the calendar id is available, if it is add a booking entry
    // TODO: if not tell the user that calendar entry is already booked

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

    sections.removeIf(c -> !c.isAvailable());

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

  public void comment() {
    List<Booking.Info> info = null;
    try {
      info = Booking.getAllRecent(connection, renter);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    if (info.size() == 0) {
      System.out.println("No non-cancelled bookings from the last 30 days to comment on.");
      return;
    }

    String[] names = new String[info.size()];
    for (int i = 0; i < info.size(); i++) {
      var bi = info.get(i);
      names[i] = String.format("%s: %s - %s; booked %s",
          bi.getListingTitle(),
          bi.getCalendarFrom().toString(),
          bi.getCalendarTo().toString(),
          bi.getBooking().getBookedDate().toString());
    }

    int choice = MenuUtils.menu("Choose booking to comment on", names);
    var booking = info.get(choice - 1).getBooking();
    var listingId = info.get(choice - 1).getListingId();

    String commentText = MenuUtils.askString("What would you like to say");
    int rating = MenuUtils.askInt("Rating (1-5)");

    var comment = new RenterComment(commentText, rating, renter.getId(), listingId);

    try {
      comment.insert(connection);
    } catch (SQLException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
