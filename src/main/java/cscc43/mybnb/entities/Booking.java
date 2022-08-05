package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Booking {
  private int id;
  CalendarSection calendarSection;
  Renter renter;
  boolean cancelled;
  LocalDate bookedDate;

  public Booking(CalendarSection calendarSection, Renter renter) {
    this.calendarSection = calendarSection;
    this.renter = renter;
    this.cancelled = false;
    this.bookedDate = null;
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
