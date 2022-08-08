package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarSection {
  private int id;
  private LocalDate from;
  private LocalDate until;
  private Renter renter;
  private Listing listing;
  private float price;
  private boolean available;

  public static List<CalendarSection> getAllForListing(Connection connection, Listing listing) throws SQLException {
    List<CalendarSection> sections = new ArrayList<>();
    var stmt = connection.prepareStatement("SELECT * FROM Calendar_Section "
        + "JOIN Listing ON Calendar_Section.Listing_ID = Listing.Listing_ID "
        + "WHERE Listing.Listing_ID = ?");
    stmt.setInt(1, listing.getId());

    ResultSet results = stmt.executeQuery();
    while (results.next()) {
      int id = results.getInt("Calendar_Section.Calendar_ID");
      LocalDate from = results.getDate("Calendar_Section.Date_From").toLocalDate();
      LocalDate to = results.getDate("Calendar_Section.Date_To").toLocalDate();
      float price = results.getFloat("Calendar_Section.Price");
      boolean available = results.getBoolean("Calendar_Section.Available");

      var calendar = new CalendarSection(from, to, listing, price);
      calendar.id = id;
      calendar.available = available;
      sections.add(calendar);
    }

    results.close();
    return sections;
  }

  public static CalendarSection getForId(Connection connection, int id) throws SQLException {
    var stmt =  connection.prepareStatement("SELECT * FROM Calendar_Section WHERE Calendar_ID = ?");
    stmt.setInt(1, id);
    ResultSet results = stmt.executeQuery();
    if (results.next()) {
      LocalDate from = results.getDate("Calendar_Section.Date_From").toLocalDate();
      LocalDate to = results.getDate("Calendar_Section.Date_To").toLocalDate();
      float price = results.getFloat("Calendar_Section.Price");
      boolean available = results.getBoolean("Calendar_Section.Available");

      var calendar = new CalendarSection(from, to, null, price);
      calendar.id = id;
      calendar.available = available;
      return calendar;
    }
    return null;
  }

  public CalendarSection(LocalDate from, LocalDate until, Listing listing, float price) {
    this.from = from;
    this.until = until;
    this.listing = listing;
    this.price = price;
    renter = null;
    available = true;
  }

  public int getId() {
    return id;
  }

  public LocalDate getFrom() {
    return from;
  }

  public LocalDate getUntil() {
    return until;
  }

  public boolean isAvailable() {
    return available;
  }

  public int insert(Connection connection) throws SQLException {
    var stmt = connection.prepareStatement("INSERT INTO Calendar_Section(Date_From, Date_To, Listing_ID, Price)"
      + "VALUES(?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS);
    stmt.setObject(1, from);
    stmt.setObject(2, until);
    stmt.setInt(3, listing.getId());
    stmt.setFloat(4, price);

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

  public void updatePrice(Connection connection, float price) throws SQLException {
    this.price = price;

    var sql = "UPDATE Calendar_Section SET Price = ? WHERE Calendar_ID = ?";
    var stmt = connection.prepareStatement(sql);
    stmt.setFloat(1, price);
    stmt.setInt(2, id);
    stmt.executeUpdate();
    stmt.close();
  }

  public void updateDates(Connection connection, LocalDate from, LocalDate to) throws SQLException {
    this.from = from;
    this.until = to;

    var sql = "UPDATE Calendar_Section SET Date_From = ?, Date_To = ? WHERE Calendar_ID = ?";
    var stmt = connection.prepareStatement(sql);
    stmt.setObject(1, from);
    stmt.setObject(2, to);
    stmt.setInt(3, id);

    stmt.executeUpdate();
    stmt.close();
  }

  public void updateAvailability(Connection connection, boolean available) throws SQLException {
    System.out.println("Changing availability Note:Booked Calendar Sections have to be cancelled!");
    var sql = "UPDATE Calendar_Section SET Available = ? WHERE Calendar_ID = ? AND Renter_ID = null";
    var stmt = connection.prepareStatement(sql);
    stmt.setBoolean(1, available);
    stmt.executeUpdate();
    stmt.close();
  }

  public void delete(Connection connection) throws SQLException {
    var stmt = connection.prepareStatement("DELETE FROM Calendar_Section WHERE Calendar_ID = ?");
    stmt.setInt(1, id);

    stmt.executeUpdate();
  }
}
