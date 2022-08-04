package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class CalendarSection {
  private int id;
  private LocalDate from;
  private LocalDate until;
  private Renter renter;
  private Listing listing;
  private float price;
  private boolean available;

  public CalendarSection(LocalDate from, LocalDate until, Listing listing, float price) {
    this.from = from;
    this.until = until;
    this.listing = listing;
    this.price = price;
    renter = null;
    available = true;
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
}
