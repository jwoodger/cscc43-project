package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking {

  // This is for information associated with a booking (calendar section dates, price, listing
  // title, etc.) that we retrieve from the DB.
  public static class Info {
    private Booking booking;
    private String listingTitle;
    private int calendarId;
    private LocalDate calendarFrom;
    private LocalDate calendarTo;

    public Booking getBooking() {
      return booking;
    }

    public String getListingTitle() {
      return listingTitle;
    }

    public LocalDate getCalendarFrom() {
      return calendarFrom;
    }

    public LocalDate getCalendarTo() {
      return calendarTo;
    }

    public int getCalendarId() {
      return calendarId;
    }
  }

  private int id;
  CalendarSection calendarSection;
  Renter renter;
  boolean cancelled;
  LocalDate bookedDate;

  public static List<Info> getAllRecent(Connection connection, Renter renter) throws SQLException {
    var bookings = new ArrayList<Info>();

    var stmt = connection.prepareStatement("SELECT B.*, C.Date_From, C.Date_To, L.Title "
      + "FROM Booking B JOIN Calendar_Section C ON B.Calendar_ID = C.Calendar_ID "
      + "JOIN Listing L ON C.Listing_ID = L.Listing_ID "
      + "WHERE B.Renter_ID = ? AND B.BookedOn > NOW() - INTERVAL 30 DAY AND NOT B.Cancelled"
    );
    stmt.setInt(1, renter.getId());

    ResultSet results = stmt.executeQuery();
    while (results.next()) {
      int id = results.getInt("BookingId");
      LocalDate bookedDate = results.getDate("BookedOn").toLocalDate();
      LocalDate calendarFrom = results.getDate("Date_From").toLocalDate();
      LocalDate calendarTo = results.getDate("Date_To").toLocalDate();
      String listingTitle = results.getString("Title");
      int calendarId = results.getInt("Calendar_ID");

      var booking = new Booking(null, renter);
      booking.bookedDate = bookedDate;

      var info = new Info();
      info.booking = booking;
      info.calendarFrom = calendarFrom;
      info.calendarTo = calendarTo;
      info.listingTitle = listingTitle;
      info.calendarId = calendarId;
      bookings.add(info);
    }

    stmt.close();
    results.close();
    return bookings;
  }

  public Booking(CalendarSection calendarSection, Renter renter) {
    this.calendarSection = calendarSection;
    this.renter = renter;
    this.cancelled = false;
    this.bookedDate = null;
  }

  public LocalDate getBookedDate() {
    return bookedDate;
  }

  public int insert(Connection connection) throws SQLException {
    var stmt = connection.prepareStatement("INSERT INTO Booking(Calendar_ID, Renter_ID)"
            + "VALUES(?, ?)",
        Statement.RETURN_GENERATED_KEYS);
    stmt.setInt(1, calendarSection.getId());
    stmt.setInt(2, renter.getId());

    int affected = stmt.executeUpdate();
    if (affected > 0) {
      ResultSet results = stmt.getGeneratedKeys();
      if (results.next()) {
        id = results.getInt(1);
        results.close();
        stmt.close();

        return id;
      }
    }
    stmt.close();
    return -1;
  }
}
