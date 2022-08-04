package cscc43.mybnb.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Listing {
  private Host host;
  private int id;
  private String title;
  private String streetAddress;
  private String city;
  private String country;
  private String postalCode;
  private double latitude;
  private double longitude;
  private List<Amenity> amenities;

  public static List<Listing> getAllForHost(Connection connection, Host host) throws SQLException {
    List<Listing> listings = new ArrayList<>();

    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Listing WHERE Host_Id = ?");
    stmt.setInt(1, host.getId());

    ResultSet results = stmt.executeQuery();
    while (results.next()) {
      int id = results.getInt("Listing_ID");
      String title = results.getString("Title");
      String streetAddress = results.getString("Street_Address");
      String city = results.getString("City");
      String country = results.getString("Country");
      String postalCode = results.getString("Postal_Code");
      double latitude = results.getDouble("Latitude");
      double longitude = results.getDouble("Longitude");

      var listing = new Listing(host, title, streetAddress, city, country, postalCode, latitude, longitude);
      listing.id = id;
      listings.add(listing);
    }

    results.close();
    return listings;
  }

  public Listing(Host host, String title, String streetAddress, String city,
      String country, String postalCode, double latitude, double longitude) {
    this.host = host;
    this.title = title;
    this.streetAddress = streetAddress;
    this.city = city;
    this.country = country;
    this.postalCode = postalCode;
    this.latitude = latitude;
    this.longitude = longitude;
    amenities = new ArrayList<>();
  }

  public String getTitle() {
    return title;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public String getCity() {
    return city;
  }

  public String getCountry() {
    return country;
  }

  public int getId() {
    return id;
  }

  public boolean hasAmenity(Amenity amenity) {
    return amenities.stream().anyMatch(a -> a.getName().equals(amenity.getName()));
  }

  public void addAmenity(Amenity amenity) {
    amenities.add(amenity);
  }

  private void insertAmenity(Connection connection, Amenity amenity) throws SQLException {
    var stmt = connection.prepareStatement("INSERT INTO Provides_Amenity(Listing_ID, Amenity_Name)"
      + "VALUES (?, ?)");
    stmt.setInt(1, id);
    stmt.setString(2, amenity.getName());

    stmt.executeUpdate();
  }

  public int insert(Connection connection) throws SQLException {
    var insertStmt = connection.prepareStatement(
        "INSERT INTO Listing(Host_ID, Title, Street_Address, City, Country, Postal_Code, Latitude, Longitude)"
        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS);
    insertStmt.setInt(1, host.getId());
    insertStmt.setString(2, title);
    insertStmt.setString(3, streetAddress);
    insertStmt.setString(4, city);
    insertStmt.setString(5, country);
    insertStmt.setString(6, postalCode);
    insertStmt.setDouble(7, latitude);
    insertStmt.setDouble(8, longitude);

    int affected =  insertStmt.executeUpdate();
    if (affected > 0) {
      ResultSet results = insertStmt.getGeneratedKeys();
      if (results.next()) {
        id = results.getInt(1);
        results.close();
        insertStmt.close();

        for (Amenity amenity : amenities) {
          insertAmenity(connection, amenity);
        }

        return id;
      }
      results.close();
    }
    insertStmt.close();;
    return -1;
  }
}
