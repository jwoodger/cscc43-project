package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Booking;
import cscc43.mybnb.entities.Booking.Info;
import cscc43.mybnb.entities.CalendarSection;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CalendarSectionMenu {
  private Connection connection;
  private CalendarSection section;

  public CalendarSectionMenu(Connection connection, CalendarSection section) {
    this.connection = connection;
    this.section = section;
  }

  public void start() {
    int e = MenuUtils.menu("Editing calendar section",
        "Change price",
        "Change date range",
        "Cancel bookings"
    );
    switch (e) {
      case 1:
        if (section.isAvailable()) {
          changePrice();
        } else {
          System.out.println("Section is already booked; cannot change price.");
        }
        break;
      case 2:
        if (section.isAvailable()) {
          changeDate();
        } else {
          System.out.println("Section is already booked; cannot change dates.");
        }
        break;
      case 3:
        cancelBookings();
        break;
    }
  }

  public void changePrice() {
    float price = (float) MenuUtils.askDouble("New price");
    try {
      section.updatePrice(connection, price);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void changeDate() {
    LocalDate from = MenuUtils.askDate("New start date");
    LocalDate to = MenuUtils.askDate("New end date");
    try {
      section.updateDates(connection, from, to);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void cancelBookings() {
    List<Info> info = null;
    try {
      info = Booking.getAllForCalendar(connection, section);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }

    String[] names = new String[info.size()];
    for (int i = 0; i < info.size(); i++) {
      var bi = info.get(i);
      names[i] = String.format("%s, booked %s", bi.getRenterUsername(), bi.getBooking().getBookedDate().toString());
    }

    int choice = MenuUtils.menu("Cancel which booking?", names);
    Booking booking = info.get(choice - 1).getBooking();

    try {
      booking.cancelByHost(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }
}
