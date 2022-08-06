package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Comment {
  protected int id;
  protected String text;
  protected int rating;

  public Comment(String text, int rating) {
    this.text = text;
    this.rating = rating;
  }

  public String getText() {
    return text;
  }

  public int getRating() {
    return rating;
  }

  public int insert(Connection connection) throws SQLException {
    var stmt = connection.prepareStatement("INSERT INTO Comment(Text, Rating)"
      + "VALUES(?, ?)",
      Statement.RETURN_GENERATED_KEYS);
    stmt.setString(1, text);
    stmt.setInt(2, rating);

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
