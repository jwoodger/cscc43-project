package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.SQLException;

public class RenterComment extends Comment {
  private int renterId;
  private int listingId;

  public RenterComment(String text, int rating, int renterId, int listingId) {
    super(text, rating);
    this.renterId = renterId;
    this.listingId = listingId;
  }

  @Override public int insert(Connection connection) throws SQLException {
    int id = super.insert(connection);

    var stmt = connection.prepareStatement("INSERT INTO Renter_Comment(Comment_ID, Renter_ID, Listing_ID) "
      + "VALUES(?, ?, ?)");
    stmt.setInt(1, id);
    stmt.setInt(2, renterId);
    stmt.setInt(3, listingId);

    stmt.executeUpdate();

    return id;
  }
}
