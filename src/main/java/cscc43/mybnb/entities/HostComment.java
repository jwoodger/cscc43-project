package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HostComment extends Comment {
  private int hostId;
  private int renterId;
  private String hostUsername;

  public static List<HostComment> getAllForRenter(Connection connection, int renterId) throws SQLException {
    var comments = new ArrayList<HostComment>();
    var sql = "SELECT * FROM Host_Comment H JOIN Comment C ON H.Comment_ID = C.Comment_ID JOIN User U ON U.User_ID = H.Host_ID\n"
        + " WHERE H.Renter_ID = ?";
    var stmt = connection.prepareStatement(sql);

    stmt.setInt(1, renterId);
    ResultSet results = stmt.executeQuery();

    while (results.next()) {
      String text = results.getString("Text");
      int rating = results.getInt("Rating");
      int hostId = results.getInt("Host_ID");
      String hostName = results.getString("username");

      var hc = new HostComment(text, rating, hostId, renterId);
      hc.hostUsername = hostName;
      comments.add(hc);
    }

    results.close();
    stmt.close();
    return comments;
  }

  public HostComment(String text, int rating, int hostId, int renterId) {
    super(text, rating);
    this.hostId = hostId;
    this.renterId = renterId;
  }

  public String getHostUsername() {
    return hostUsername;
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
