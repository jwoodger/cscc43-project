package cscc43.mybnb.reports;

import cscc43.mybnb.menus.MenuUtils;

import java.sql.*;
import java.time.LocalDate;

public class GeneralReport {
    private Connection connection;
    public GeneralReport(Connection c){
        this.connection = c;
    }
    public void helpers() throws SQLException{
        Statement s = connection.createStatement();
        s.executeUpdate("drop view if exists location_details;");
        s.executeUpdate("create view location_details as \n" +
                "select distinct Country, City,Postal_Code\n" +
                "from mybnb.Listing;");
        s.executeUpdate("drop view if exists host_user;");
        s.executeUpdate("create view host_user as\n" +
                "select username,Host_ID from User join Host on User_ID = Host_ID;");
        s.executeUpdate("drop view if exists renter_user;");
        s.executeUpdate("create view renter_user as\n" +
                "select username,Renter_ID from User join Renter on User_ID = Renter_ID;");
    }
    public void bookings()throws SQLException{
        int c = MenuUtils.menu("By city or city/zipcode","City","Zipcode");
        LocalDate low = MenuUtils.askDate("Start Date?");
        LocalDate up = MenuUtils.askDate("End Date?");
        if(c==1){
            PreparedStatement s = connection.prepareStatement("select b.Country,b.City,count(BookingId) as count\n" +
                    "from (select * from Booking natural join Calendar_Section natural join Listing\n" +
                    "\twhere Date_From >= ? and Date_To <= ?\n" +
                    ") a right join Listing b on a.Listing_ID = b.Listing_ID\n" +
                    "group by Country, City;");
            s.setDate(1,Date.valueOf(low));
            s.setDate(2,Date.valueOf(up));
            ResultSet r = s.executeQuery();
            System.out.println("Country     City     Count");
            while(r.next()){

                System.out.println(r.getString("Country")+"     "+r.getString("City")+
                        "       "+r.getInt("Count"));

            }
        }else{
            PreparedStatement s = connection.prepareStatement("select b.Country,b.City,b.Postal_Code,count(BookingId) as count\n" +
                    "from (select * from Booking natural join Calendar_Section natural join Listing\n" +
                    "\twhere Date_From >= ? and Date_To <= ?\n" +
                    ") a right join Listing b on a.Listing_ID = b.Listing_ID\n" +
                    "group by Country,City,Postal_Code;");
            s.setDate(1, Date.valueOf(low));
            s.setDate(2,Date.valueOf(up));
            ResultSet r = s.executeQuery();
            System.out.println("Country     City        ZipCode     Count");
            while(r.next()){

                System.out.println(r.getString("Country")+"     "+r.getString("City")+
                        "       "+r.getString("Postal_Code")+"      "+r.getInt("Count"));
            }
        }
    }
    public void listings()throws SQLException{
        int choice = MenuUtils.menu("By?","Country","Country,City","Country,City,Zipcode");
        Statement s = connection.createStatement();
        ResultSet r;
        switch (choice){
            case 1:
                r = s.executeQuery("select Country,count(*) as count\n" +
                        "from Listing\n" +
                        "group by Country;");
                System.out.println("Country`    Count");
                while(r.next()){
                    System.out.println(r.getString("Country")+"     "+r.getString("Count"));
                }
                break;
            case 2:
                r = s.executeQuery("select Country,City,count(*) as count\n" +
                        "from Listing\n" +
                        "group by Country,City;");
                System.out.println("Country`    City    Count");
                while(r.next()){
                    System.out.println(r.getString("Country")+"     "+r.getString("City")+"      "+r.getString("Count"));
                }
                break;
            case 3:
                r = s.executeQuery("select Country,City,Postal_Code,count(*) as count\n" +
                        "from listing\n" +
                        "group by Country,City,Postal_Code;");
                System.out.println("Country     City    ZipCode     Count");
                while(r.next()){
                    System.out.println(r.getString("Country")+"     "+r.getString("City")+"      "+r.getString("postal_code")+"     "+r.getString("Count"));
                }
                break;
        }
    }
    public void host() throws  SQLException{
        int choice = MenuUtils.menu("By?","Country","Country,City");
        Statement s = connection.createStatement();
        ResultSet r;
        if(choice ==1){
            r = s.executeQuery("select a.Country,username,count(Listing_ID) as count\n" +
                    "from\n" +
                    "(select Country,City,username,host_ID\n" +
                    "from  location_details l cross join host_user u\n" +
                    "group by Country,City,username) a left join Listing b on a.Host_ID = b.Host_ID and a.Country=b.Country and a.City=b.City\n" +
                    "group by a.Country,a.username\n" +
                    "order by a.Country,count(Listing_ID) desc;");
            System.out.println("Country     Username    Count");
            while (r.next()){
                System.out.println(r.getString("Country")+"     "+r.getString("Username")+"     "
                +r.getString("Count"));
            }

        }else{
            r = s.executeQuery("select a.Country,a.City,username,count(Listing_ID) as count\n" +
                    "from\n" +
                    "(select Country,City,username,Host_ID\n" +
                    "from  location_details l cross join host_user u\n" +
                    "group by Country,City,username) a left join Listing b on a.Host_ID = b.Host_ID and a.Country=b.Country and a.City=b.City\n" +
                    "group by a.Country,a.City,a.username\n" +
                    "order by a.Country,a.City,count(Listing_ID) desc;");
            System.out.println("Country     City    Username    Count");
            while (r.next()){
                System.out.println(r.getString("Country")+"     "+r.getString("City")+"    "+
                        r.getString("Username")+"     "
                        +r.getString("Count"));
            }
        }
    }
    public void commercial() throws SQLException{
        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery("select Country,City,username,count\n" +
                "from(\n" +
                "select Country,City,username,count(*) as count\n" +
                "from listing l natural join host_user\n" +
                "group by Country,City,username\n" +
                ") L natural join (select Country,City,count(*) as total  from listing group by Country,City) t\n" +
                "where count >0.1*(total);");
        System.out.println("Country     City    Username");
        while (r.next()){
            System.out.println(r.getString("Country")+"     "+r.getString("City")+"     "+
                    r.getString("username"));
        }
    }
    public void renter() throws SQLException{
        int choice = MenuUtils.menu("By City?","Yes","No");
        LocalDate low = MenuUtils.askDate("Earliest Date?");
        LocalDate up = MenuUtils.askDate("Latest Date?");

        if(choice == 1){
            PreparedStatement s = connection.prepareStatement("select Country,City,username,count(BookingId) as count\n" +
                    "from renter_user natural join booking natural join Calendar_Section natural join Listing\n" +
                    "where ? <= date(BookedOn) and date(BookedOn) <= ?\n" +
                    "group by Country,City,Username\n" +
                    "having count(BookingId) >= 2\n" +
                    "order by Country,City,count(BookingId);");
            s.setDate(1, Date.valueOf(low));
            s.setDate(2, Date.valueOf(up));
            ResultSet r = s.executeQuery();
            System.out.println("Country     City    Username    Count");
            while (r.next()){
                System.out.println(r.getString("Country")+"     "+r.getString("City")+
                        "       "+r.getString("Username")+"     "+r.getString("Count"));
            }
        }else{

            PreparedStatement s = connection.prepareStatement("\n" +
                    "select username,count(Bookingid) as count\n" +
                    "from renter_user a left join (select * from Booking where date(BookedOn)>= ? and date(BookedOn) <= ? ) b on a.Renter_ID = b.Renter_ID\n" +
                    "group by username");
            s.setDate(1, Date.valueOf(low));
            s.setDate(2, Date.valueOf(up));
            ResultSet r = s.executeQuery();
            System.out.println("Username    Count");
            while (r.next()){
                System.out.println(r.getString("Username")+"     "+r.getString("Count"));
            }
        }

    }
    public void cancel() throws SQLException{
        int year = MenuUtils.askInt("Which Year? must be 1970 onwards");
        LocalDate lowest = LocalDate.of(year,1,1);
        LocalDate highest = LocalDate.of(year,12,31);
        System.out.println("Showing cancelled bookings that were booked from "+lowest+" to "+highest);
        PreparedStatement s = connection.prepareStatement("select b.username,count(BookingId) as count\n" +
                "from (select * from Renter_User natural join Booking where Cancelled = 1 and ?<=year(BookedOn) and year(BookedOn)<? ) a right join renter_user b on a.username = b.username\n" +
                "group by b.username\n" +
                "order by count(BookingId) desc\n" +
                "limit 5;");
        s.setInt(1,year);
        s.setInt(2,year+1);
        ResultSet r = s.executeQuery();
        System.out.println("Renters with Highest Cancellations");
        System.out.println("Username    Count");
        while (r.next()){
            System.out.println(r.getString("username")+"    "+r.getString("Count"));
        }
        PreparedStatement s2 = connection.prepareStatement("select b.username, count(BookingId) as count\n" +
                "from (select username,BookingID from Booking b join Calendar_Section c on b.Calendar_ID = c.Calendar_ID natural join Listing natural join Host natural join host_user where Cancelled = 2 and\n" +
                " ?<=year(BookedOn) and year(BookedOn)<? ) a\n" +
                "right join host_user b on a.username = b.username\n" +
                "group by b.username\n" +
                "order by count(BookingId) desc\n" +
                "limit 5;");
        s2.setInt(1,year);
        s2.setInt(2,year+1);
        r = s2.executeQuery();
        System.out.println("Hosts with Highest Cancellations");
        System.out.println("Username Count");
        while (r.next()){
            System.out.println(r.getString("username")+"    "+r.getString("Count"));
        }
    }
    public void start() throws SQLException {
        //create helper views
        helpers();

        int choice = MenuUtils.menu("Choose a report.",
                "Total bookings",
                "Total number of listings",
                "Rank hosts by listings",
                "Possible commercial hosts",
                "Rank renters by bookings",
                "Most cancellations",
                "Most popular noun phrases for a listing");
        switch (choice) {
            case 1:
                bookings();
                break;
            case 2:
                listings();
                break;
            case 3:
                host();
                break;
            case 4:
                commercial();
                break;
            case 5:
                renter();
                break;
            case 6:
                cancel();
                break;
            case 7:
                new NounPhraseReport(connection).run();
                break;
        }

    }
}
