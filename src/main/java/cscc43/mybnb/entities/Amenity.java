package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Amenity {
  private String name;

  public static List<Amenity> getAll(Connection connection) throws SQLException {
    List<Amenity> amenities = new ArrayList<>();
    var stmt = connection.prepareStatement("SELECT * FROM Amenity");
    ResultSet results = stmt.executeQuery();

    while (results.next()) {
      String name = results.getString("Name");
      amenities.add(new Amenity(name));
    }
    results.close();
    return amenities;
  }

  public Amenity(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
