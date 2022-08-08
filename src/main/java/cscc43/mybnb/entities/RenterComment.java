package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RenterComment extends Comment {
  private int renterId;
  private int listingId;
  private String renterUsername;

  public static List<RenterComment> getAllForListing(Connection connection, int listingId) throws SQLException {
    var comments = new ArrayList<RenterComment>();
    var sql = "SELECT * FROM Renter_Comment R JOIN Comment C ON R.Comment_ID = C.Comment_ID JOIN User U ON R.Renter_ID = U.User_ID"
        + "WHERE L.Listing_ID = ?";
    var stmt = connection.prepareStatement(sql);
    stmt.setInt(1, listingId);

    ResultSet results = stmt.executeQuery();
    while (results.next()) {
      String text = results.getString("Text");
      int rating = results.getInt("Rating");
      int renterId = results.getInt("Renter_ID");
      String renterName = results.getString("username");

      var rc = new RenterComment(text, rating, renterId, listingId);
      rc.renterUsername = renterName;
      comments.add(rc);
    }

    results.close();
    stmt.close();
    return comments;
  }

  public RenterComment(String text, int rating, int renterId, int listingId) {
    super(text, rating);
    this.renterId = renterId;
    this.listingId = listingId;
  }

  public String getRenterUsername() {
    return renterUsername;
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
