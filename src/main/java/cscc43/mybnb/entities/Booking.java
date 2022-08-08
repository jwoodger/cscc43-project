package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    private int listingId;
    private String listingTitle;
    private int calendarId;
    private LocalDate calendarFrom;
    private LocalDate calendarTo;
    private String renterUsername;

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

    public int getListingId() {
      return listingId;
    }

    public int getCalendarId() {
      return calendarId;
    }

    public String getRenterUsername() {
      return renterUsername;
    }
  }

  private int id;
  CalendarSection calendarSection;
  Renter renter;
  boolean cancelled;
  LocalDate bookedDate;

  private static List<Info> getList(PreparedStatement statement) throws SQLException {
    var bookings = new ArrayList<Info>();
    ResultSet results = statement.executeQuery();
    while (results.next()) {
      int id = results.getInt("BookingId");
      LocalDate bookedDate = results.getDate("BookedOn").toLocalDate();
      LocalDate calendarFrom = results.getDate("Date_From").toLocalDate();
      LocalDate calendarTo = results.getDate("Date_To").toLocalDate();
      String listingTitle = results.getString("Title");
      int calendarId = results.getInt("Calendar_ID");
      int listingId = results.getInt("Listing_ID");
      String username = results.getString("username");

      var booking = new Booking(null, null);
      booking.id = id;
      booking.bookedDate = bookedDate;

      var info = new Info();
      info.booking = booking;
      info.calendarFrom = calendarFrom;
      info.calendarTo = calendarTo;
      info.listingTitle = listingTitle;
      info.calendarId = calendarId;
      info.listingId = listingId;
      info.renterUsername = username;
      bookings.add(info);
    }
    results.close();
    return bookings;
  }

  public static List<Info> getAllRecent(Connection connection, Renter renter) throws SQLException {
    var stmt = connection.prepareStatement("SELECT B.*, C.Date_From, C.Date_To, L.Title, L.Listing_ID, U.username "
      + "FROM Booking B JOIN Calendar_Section C ON B.Calendar_ID = C.Calendar_ID "
      + "JOIN Listing L ON C.Listing_ID = L.Listing_ID "
      + "JOIN User U ON C.Renter_ID = U.User_ID "
      + "WHERE B.Renter_ID = ? AND C.Date_To < NOW() and C.Date_To  > NOW() - INTERVAL 1 YEAR AND  B.Cancelled = 0"
    );
    stmt.setInt(1, renter.getId());
    var info = getList(stmt);
    stmt.close();
    return info;
  }
  public static List<Info> getAll(Connection connection, Renter renter) throws SQLException {
    var stmt = connection.prepareStatement("SELECT B.*, C.Date_From, C.Date_To, L.Title, L.Listing_ID, U.username "
            + "FROM Booking B JOIN Calendar_Section C ON B.Calendar_ID = C.Calendar_ID "
            + "JOIN Listing L ON C.Listing_ID = L.Listing_ID "
            + "JOIN User U ON C.Renter_ID = U.User_ID "
            + "WHERE B.Renter_ID = ? AND B.Cancelled = 0"
    );
    stmt.setInt(1, renter.getId());
    var info = getList(stmt);
    stmt.close();
    return info;
  }
  public static List<Info> getAllForCalendar(Connection connection, CalendarSection section) throws SQLException {
    var stmt = connection.prepareStatement("SELECT B.*, C.Date_From, C.Date_To, L.Title, L.Listing_ID, U.username "
        + "FROM Booking B JOIN Calendar_Section C ON B.Calendar_ID = C.Calendar_ID "
        + "JOIN Listing L ON C.Listing_ID = L.Listing_ID "
        + "JOIN User U ON C.Renter_ID = U.User_ID "
        + "WHERE C.Calendar_ID = ?"
    );
    stmt.setInt(1, section.getId());

    var info = getList(stmt);
    stmt.close();
    return info;
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

  public void cancelByHost(Connection connection) throws SQLException {
    var sql = "UPDATE Booking SET Cancelled = 2 WHERE BookingID = ?";
    var stmt = connection.prepareStatement(sql);
    stmt.setInt(1, id);
    stmt.executeUpdate();
    stmt.close();
  }

  public void cancelByRenter(Connection connection) throws SQLException {
    var sql = "UPDATE Booking SET Cancelled = 1 WHERE BookingID = ?";
    var stmt = connection.prepareStatement(sql);
    stmt.setInt(1, id);
    stmt.executeUpdate();
    stmt.close();
  }
}
