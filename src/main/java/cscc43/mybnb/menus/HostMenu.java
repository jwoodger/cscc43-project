package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Amenity;
import cscc43.mybnb.entities.CalendarSection;
import cscc43.mybnb.entities.Host;
import cscc43.mybnb.entities.HostComment;
import cscc43.mybnb.entities.Listing;
import cscc43.mybnb.entities.Renter;

import cscc43.mybnb.entities.RenterComment;
import java.awt.MenuItem;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import opennlp.tools.cmdline.parser.ParserTool;

public class HostMenu {
  private Connection connection;
  private Host host;

  public HostMenu(Connection connection, Host host) {
    this.connection = connection;
    this.host = host;
  }

  public void start() {
    int choice = 0;
    while (choice != 9) {
      String prompt = String.format("Logged in as %s", host.getUsername());
      choice = MenuUtils.menu(prompt,
          "Create listing",
          "Create availability",
          "Edit listing",
          "Edit calendar section",
          "Comment on user",
          "View comments on listing",
          "Remove listing",
          "Remove calendar section",
          "Log out");
      switch (choice) {
        case 1:
          createListing();
          break;
        case 2:
          createCalendarSection();
          break;
        case 3:
          editListing();
          break;
        case 4:
          editCalendarSection();
          break;
        case 5:
          commentOnUser();
          break;
        case 6:
          viewRenterComments();
          break;
        case 7:
          removeListing();
          break;
        case 8:
          removeCalendarSection();
          break;
      }
    }
  }


  //suggests price based on all the amenities in l plus the amenity in e plus city,and country
  public double suggest_price(String City,String Country,List<Amenity> l,Amenity e)  {
    double price = -1.0;
    try{
      String query = "";
      if(e!=null){
        query = query.concat("and EXISTS(select * from provides_amenity P where L.listing_ID = P.listing_ID AND P.Amenity_Name = \'" +
                e.getName()+"\') ");
      }
      if(l!=null && !l.isEmpty())
      for(int i=0;i<l.size();i++){
        Amenity a = l.get(i);
        String s = a.getName();

        query = query.concat("and EXISTS(select * from provides_amenity P where L.listing_ID = P.listing_ID AND P.Amenity_Name = \'" +
                s+"\') ");
      }
    PreparedStatement s = connection.prepareStatement("select avg((price/(datediff(Date_To,Date_From)+1))*30) as average_ppm\n" +
            "from listing L natural join calendar_section \n" +
            "where  country = ? and city = ? and\n" +
            " year(Date_To)+10 >= year(now()) \n"+
            query);
    s.setString(1,Country);
    s.setString(2,City);
    ResultSet r = s.executeQuery();
    while (r.next()){
      price = r.getDouble("average_ppm");
      if(r.wasNull()){
        price = -1.0;
      }
    }}catch (SQLException exception){
      MenuUtils.showError(exception);
      return price;
    }
    return price;
  }
  public void createListing() {
    String title = MenuUtils.askString("Title of listing");
    String address = MenuUtils.askString("Street address");
    String city = MenuUtils.askString("City");
    String country = MenuUtils.askString("Country");
    String postalCode = MenuUtils.askString("Postal code");
    double latitude = MenuUtils.askDouble("Latitude");
    double longitude = MenuUtils.askDouble("Longitude");
    var listing = new Listing(host, title, address, city, country, postalCode, latitude, longitude);

    addAmenities(listing);

    try {
      listing.insert(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void addAmenities(Listing listing) {
    List<Amenity> amenities = null;
    try {
      amenities = Amenity.getAll(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }

    amenities.removeIf(a -> listing.hasAmenity(a));

    boolean finished = false;
        while (!finished) {
      String[] options = new String[amenities.size() + 1];
      for (int i = 0; i < amenities.size(); i++) {
        options[i] = amenities.get(i).getName();
      }
      options[amenities.size()] = "Finish";
      System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
          double ppm = suggest_price(listing.getCity(), listing.getCountry(),listing.getAmenities(),null );
          if(ppm>0)
            System.out.println("Based on leasings of last 10 years in your city with all the amenities you currently have, we estimate a price point of $"+ppm+" per month");

          for(int j=0;j<amenities.size();j++){
        double ppm_new = suggest_price(listing.getCity(), listing.getCountry(), listing.getAmenities(),amenities.get(j));
        if(ppm_new>0){
          System.out.println("If you add "+amenities.get(j).getName()+" You are expected to increase price per month by: $"+(ppm_new-ppm));
        }
      }
      int choice = MenuUtils.menu("Add amenity", options);

      if (choice == amenities.size() + 1) {
        finished = true;
      } else {
        listing.addAmenity(amenities.remove(choice - 1));
      }
    }
  }

  public void createCalendarSection() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] names = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      names[i] = listings.get(i).getTitle();
    }
    int choice = MenuUtils.menu("Choose a listing:", names);
    Listing listing = listings.get(choice - 1);

    LocalDate dateFrom = MenuUtils.askDate("First day available");
    LocalDate dateTo = MenuUtils.askDate("Last day available");
    float price = (float) MenuUtils.askDouble("Asking price");

    var section = new CalendarSection(dateFrom, dateTo, listing, price);
    try {
      section.insert(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void editListing() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] names = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      names[i] = listings.get(i).getTitle();
    }

    int choice = MenuUtils.menu("Listing to edit", names);
    Listing listing = listings.get(choice - 1);

    addAmenities(listing);

    try {
      for (Amenity amenity : listing.getAmenities()) {
        listing.insertAmenity(connection, amenity);
      }
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void commentOnUser() {
    List<Renter> renters = null;
    try {
      renters = Renter.getForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (renters.size() == 0) {
      System.out.println("No renters who have booked your listings.");
      return;
    }

    String[] names = new String[renters.size()];
    for (int i = 0; i < renters.size(); i++) {
      Renter r = renters.get(i);
      names[i] = r.getUsername();
    }

    int choice = MenuUtils.menu("Choose user to rate", names);
    Renter renterToComment = renters.get(choice - 1);

    String text = MenuUtils.askString("What would you like to say?");
    int rating = MenuUtils.askInt("Rating(1 - 5)");

    var comment = new HostComment(text, rating, host.getId(), renterToComment.getId());
    try {
      comment.insert(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void editCalendarSection() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] listingNames = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      listingNames[i] = listings.get(i).getTitle();
    }

    int l = MenuUtils.menu("Choose a listing.", listingNames);
    Listing listing = listings.get(l - 1);

    List<CalendarSection> calendarSections = null;
    try {
      calendarSections = CalendarSection.getAllForListing(connection, listing);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (calendarSections.size() == 0) {
      System.out.println("No calendar sections for this listing. Please create an availability first.");
      return;
    }

    String[] csNames = new String[calendarSections.size()];
    for (int i = 0; i < calendarSections.size(); i++) {
      CalendarSection cs = calendarSections.get(i);
      csNames[i] = String.format("%s - %s", cs.getFrom().toString(), cs.getUntil().toString());
    }
    int c = MenuUtils.menu("Choice calendar section", csNames);
    CalendarSection section = calendarSections.get(c - 1);

    new CalendarSectionMenu(connection, section).start();
  }

  public void viewRenterComments() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] names = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      names[i] = listings.get(i).getTitle();
    }

    int choice = MenuUtils.menu("Listing to view", names);
    Listing listing = listings.get(choice - 1);

    List<RenterComment> comments = null;
    try {
      comments = RenterComment.getAllForListing(connection, listing.getId());
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }

    if (comments.size() == 0) {
      System.out.println("No comments available");
      return;
    }

    for (RenterComment c : comments) {
      String s = String.format("%s:\n%s\nRating: %d / 5", c.getRenterUsername(), c.getText(), c.getRating());
      System.out.println(s);
    }
  }

  public void removeListing() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] names = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      names[i] = listings.get(i).getTitle();
    }

    int choice = MenuUtils.menu("Listing to view", names);
    Listing listing = listings.get(choice - 1);

    try {
      listing.delete(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }

  public void removeCalendarSection() {
    List<Listing> listings = null;
    try {
      listings = Listing.getAllForHost(connection, host);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (listings.size() == 0) {
      System.out.println("No listings for this host. Please create a listing first.");
      return;
    }

    String[] names = new String[listings.size()];
    for (int i = 0; i < listings.size(); i++) {
      names[i] = listings.get(i).getTitle();
    }

    int choice = MenuUtils.menu("Listing to view", names);
    Listing listing = listings.get(choice - 1);

    List<CalendarSection> calendarSections = null;
    try {
      calendarSections = CalendarSection.getAllForListing(connection, listing);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
    if (calendarSections.size() == 0) {
      System.out.println("No calendar sections for this listing. Please create an availability first.");
      return;
    }

    String[] csNames = new String[calendarSections.size()];
    for (int i = 0; i < calendarSections.size(); i++) {
      CalendarSection cs = calendarSections.get(i);
      csNames[i] = String.format("%s - %s", cs.getFrom().toString(), cs.getUntil().toString());
    }
    int c = MenuUtils.menu("Choose calendar section", csNames);
    CalendarSection section = calendarSections.get(c - 1);

    try {
      section.delete(connection);
    } catch (SQLException e) {
      MenuUtils.showError(e);
      return;
    }
  }
}
