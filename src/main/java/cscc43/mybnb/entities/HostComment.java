package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.SQLException;

public class HostComment extends Comment {
  private int hostId;
  private int renterId;

  public HostComment(String text, int rating, int hostId, int renterId) {
    super(text, rating);
    this.hostId = hostId;
    this.renterId = renterId;
  }

  @Override public int insert(Connection connection) throws SQLException {
    int id = super.insert(connection);

    var stmt = connection.prepareStatement("INSERT INTO Host_Comment(Comment_ID, Host_ID, Renter_ID) "
        + "VALUES(?, ?, ?)");
    stmt.setInt(1, id);
    stmt.setInt(2, hostId);
    stmt.setInt(3, renterId);

    stmt.executeUpdate();

    return id;
  }
}
