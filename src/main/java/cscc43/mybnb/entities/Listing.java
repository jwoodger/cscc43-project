package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Listing {
  private Host host;
  private int id;
  private String title;
  private String streetAddress;
  private String city;
  private String province;
  private String country;
  private String postalCode;
  private double latitude;
  private double longitude;
  private float price;

  public Listing(Host host, String title, String streetAddress, String city, String province,
      String country, String postalCode, double latitude, double longitude, float price) {
    this.host = host;
    this.title = title;
    this.streetAddress = streetAddress;
    this.city = city;
    this.province = province;
    this.country = country;
    this.postalCode = postalCode;
    this.latitude = latitude;
    this.longitude = longitude;
    this.price = price;
  }

  public int insert(Connection connection) throws SQLException {
    var insertStmt = connection.prepareStatement(
        "INSERT INTO Listing(Host_ID, Title, Street_Address, City, Province, Country, Postal_Code, Latitude, Longitude, Price)"
        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS);
    insertStmt.setInt(1, host.getId());
    insertStmt.setString(2, title);
    insertStmt.setString(3, streetAddress);
    insertStmt.setString(4, city);
    insertStmt.setString(5, province);
    insertStmt.setString(6, country);
    insertStmt.setString(7, postalCode);
    insertStmt.setDouble(8, latitude);
    insertStmt.setDouble(9, longitude);
    insertStmt.setFloat(10, price);

    int affected =  insertStmt.executeUpdate();
    if (affected > 0) {
      ResultSet results = insertStmt.getGeneratedKeys();
      if (results.next()) {
        id = results.getInt(1);
        results.close();
        insertStmt.close();
        return id;
      }
      results.close();
    }
    insertStmt.close();;
    return -1;
  }
}
