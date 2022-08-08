package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Booking;
import cscc43.mybnb.entities.CalendarSection;
import cscc43.mybnb.entities.Comment;
import cscc43.mybnb.entities.HostComment;
import cscc43.mybnb.entities.Listing;
import cscc43.mybnb.entities.Renter;
import cscc43.mybnb.entities.RenterComment;
import java.awt.print.Book;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RenterMenu {
  private Connection connection;
  private Renter renter;

  public RenterMenu(Connection connection, Renter renter) {
    this.connection = connection;
    this.renter = renter;
  }

  public void start() {
    int choice = 0;
    while (choice != 5) {
      String prompt = String.format("Logged in as %s", renter.getUsername());
      choice = MenuUtils.menu(prompt,
          "Book listing",
          "Cancel booking",
          "Comment on listing",
          "View comments about you",
          "Log out");
      switch (choice) {
        case 1:
          bookListing();
          break;
        case 2:
          cancelBooking();
          break;
        case 3:
          comment();
          break;
        case 4:
          viewHostComments();
          break;
      }
    }
  }

  public void bookListing() {
    boolean results = new QueryMenu(connection).start();
    if (!results)
      return;

    int id = MenuUtils.askInt("Enter ID of calendar section.");
    CalendarSection bookedSection = null;
    try {
      bookedSection = CalendarSection.getForId(connection, id);
      if(bookedSection.isAvailable()==false){
        System.out.println("Unavailable Section");
        bookedSection = null;
      }
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }

    if (bookedSection == null) {
      System.out.println("Could not access calendar section.");
      return;
    }

    var booking = new Booking(bookedSection, renter);
    try {
      booking.insert(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public Booking.Info chooseBookingRecent(Predicate<Booking.Info> p) {
    List<Booking.Info> info = null;
    try {
      info = Booking.getAllRecent(connection, renter);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return null;
    }

    //info.removeIf(p);

    if (info == null || info.size() == 0) {
      System.out.println("No non-cancelled bookings Recently.");
      return null;
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

    int choice = MenuUtils.menu("Choose booking", names);
    return info.get(choice - 1);
  }
  public Booking.Info chooseBooking(Predicate<Booking.Info> p) {
    List<Booking.Info> info = null;
    try {
      info = Booking.getAll(connection, renter);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return null;
    }

    info.removeIf(p);

    if (info == null || info.size() == 0) {
      System.out.println("No non-cancelled bookings found.");
      return null;
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

    int choice = MenuUtils.menu("Choose booking", names);
    return info.get(choice - 1);
  }

  public void comment() {
    var info = chooseBookingRecent(bi -> true);
    if(info == null)return;
    var booking = info.getBooking();
    var listingId = info.getListingId();

    String commentText = MenuUtils.askString("What would you like to say");
    int rating = MenuUtils.askInt("Rating (1-5)");

    var comment = new RenterComment(commentText, rating, renter.getId(), listingId);

    try {
      comment.insert(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void cancelBooking() {
    var info = chooseBooking(bi -> bi.getCalendarFrom().isBefore(LocalDate.now()));
    if (info == null) {
      return;
    }

    try {
      info.getBooking().cancelByRenter(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void viewHostComments() {
    List<HostComment> comments = null;

    try {
      comments = HostComment.getAllForRenter(connection, renter.getId());
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }

    if (comments.size() == 0) {
      System.out.println("No comments available");
      return;
    }

    for (HostComment c : comments) {
      String s = String.format("%s:\n%s\nRating: %d / 5", c.getHostUsername(), c.getText(), c.getRating());
      System.out.println(s);
    }
  }
}
