package cscc43.mybnb.menus;

import cscc43.mybnb.entities.Host;
import cscc43.mybnb.entities.Renter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QueryMenu {
    //NOTE: This shows listings by queries, it is primarily meant for the Renter's sake
    private Connection connection;
    public QueryMenu(Connection connection) {
        this.connection = connection;
    }

    public boolean start() {
        int choice = MenuUtils.menu("Listing Queries","By distance","by postal code","by exact address","by host","By title","None");
        try{
            dropView("v1");
            dropView("v0");
            Statement sql = connection.createStatement();

        switch (choice){

            case 1:
                distance();
                break;
            case 2:
                postal();
                break;
            case 3:
                exact();
                break;
            case 4:
                byhost();
                break;
            case 5:
                bytitle();
                break;
            default:
                sql.executeUpdate("create view v1 as select * from Listing");
        }

        int choice2 = MenuUtils.menu("Would you like to add more specifications?","Yes","No");
        dropView("v2");
        if(choice2 == 1){
            custom(choice == 3,choice == 2);
        }else{
            sql.executeUpdate("create view v2 as select * from v1 NATURAL JOIN Calendar_Section");
        }
        return printresults();
        }
        catch(SQLException e){
            MenuUtils.showError(e);
        }
        return false;
    }
    public boolean printresults() throws SQLException{
        Statement s = connection.createStatement();
        int order = MenuUtils.menu("Sort by Price:","Ascending","Descending");
        ResultSet r = s.executeQuery("select * from v2 order by price "+((order==1)?"asc":"desc"));
        if(!r.isBeforeFirst()){System.out.println("No results found!");
        return false;
        }
        //print out each result
        while(r.next()){
            System.out.println("---------------------------------------");
            String title = r.getString("Title");
            System.out.println("Title :"+title);
            System.out.println("Rented by :"+Host.getUsernameFromID(connection,r.getInt("Host_ID")) );
            System.out.println("Calendar ID :"+r.getString("Calendar_ID"));
            double price = r.getDouble("Price");
            System.out.println("Price :"+price);
            Date from = r.getDate("Date_From");
            System.out.println("Date From :"+from.toLocalDate());
            Date to = r.getDate("Date_To");
            System.out.println("Date To :"+to.toLocalDate());
            System.out.println("Street Address :"+r.getString("Street_Address"));
            System.out.println("Country :"+r.getString("Country"));
            System.out.println("City :"+r.getString("City"));
            System.out.println("ZipCode :"+r.getString("Postal_Code"));
            boolean available = r.getBoolean("Available");
            System.out.println("Available?: "+(available?"YES":"NOPE"));
        }
        return true;
    }
    public void dropView(String s){
        try {
            Statement sql = connection.createStatement();
            sql.execute("DROP VIEW IF EXISTS "+s);
        }catch(SQLException e){
            MenuUtils.showError(e);
        }
    }
    public void custom(Boolean exact,Boolean postal) throws SQLException{
    dropView("temp1");
    //FILTER BY PRICE
    Statement sql = connection.createStatement();
    int c = MenuUtils.menu("Add filter on price?","Yes","No");
    if(c==1){
        PreparedStatement ps = connection.prepareStatement("create view temp1 as select * from v1 NATURAL JOIN Calendar_Section " +
                "where price >= ? and price <= ?");
        double lower = MenuUtils.askDouble("lower price point?");
        double upper = MenuUtils.askDouble("upper price point?");
        ps.setDouble(1,lower);
        ps.setDouble(2,upper);
        ps.executeUpdate();
    }
    else{sql.executeUpdate("create view temp1 as select * from v1 NATURAL JOIN Calendar_Section");}
    //FILTER BY DATE
    dropView("temp2");
    c = MenuUtils.menu("Add filter on Dates?","Yes","No");
    if(c==1){
        PreparedStatement ps = connection.prepareStatement("create view temp2 as select * from temp1 where " +
                "Date_From >= ? and Date_To <= ?");
        var lower = MenuUtils.askDate("Earliest date? ");
        var upper = MenuUtils.askDate("Latest date? ");
        ps.setDate(1, Date.valueOf(lower));
        ps.setDate(2,Date.valueOf(upper));
        ps.executeUpdate();
    }else{
        sql.executeUpdate("create view temp2 as select * from temp1");
    }

    //FILTER BY COUNTRY / CITY
        dropView("temp3");
    if(exact == false && postal == false) {
        c = MenuUtils.menu("Add filter on Country and City?", "Yes", "No");
    }else{
        System.out.println("Skipping the filter on Country / City, since that was specified earlier");
    }
    if(c==1 && exact == false && postal == false){
        PreparedStatement ps = connection.prepareStatement("create view temp3 as select * from temp2 where " +
                " Country = ? and City LIKE ?");
        String country = MenuUtils.askString("Country ?");
        String City = MenuUtils.askString("City? (write % for all cities)");
        ps.setString(1,country);
        ps.setString(2,City);
        ps.executeUpdate();
    }else{
        sql.executeUpdate("create view temp3 as select * from temp2");
    }
    //FILTER BY AMENITIES
        dropView("v2");
        if(exact== Boolean.FALSE)
            c = MenuUtils.menu("Add filter on Amenities? ","Yes","No");
        if(c==1 && exact==Boolean.FALSE){
            System.out.println("Here are all the amenities");
            ArrayList<String> s =PrintAmenities();
            ArrayList<String> choices = new ArrayList<>();
            String choice;
            boolean nothing = true;
            choice = MenuUtils.askString("Which ones to you want (enter none to start query)?");
            while (!choice.equals("none") && !s.isEmpty()){

                if(s.contains(choice)){
                    choices.add(choice);
                    s.remove(choice);
                }

                for(int i =0; i<s.size();i++)
                    System.out.println(s.get(i));
                choice = MenuUtils.askString("Which ones to you want (enter none to start query)?");
            }
            //build our where clause
            String query = "";
            while(!choices.isEmpty()){
                query = query.concat(" EXISTS(select * from Provides_Amenity P where L.Listing_ID = P.Listing_ID AND P.Amenity_Name = \'" +
                        choices.get(0)+"\') ");
                choices.remove(0);
                if(!choices.isEmpty()){query += "AND";}else{query = "where "+query;}
            }
            sql.executeUpdate("create view v2 as select * from temp3 L "+query);
        }else{
            sql.executeUpdate("create view v2 as select * from temp3");
        }
    }
    public ArrayList<String> PrintAmenities() throws SQLException{
        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery("select * from amenity");
        ArrayList<String> set = new ArrayList<>();
        while(r.next()){
            String name = r.getString("name");
            System.out.println(name);
            set.add(name);
        }
        return set;
    }
    public void distance() throws SQLException{
        Statement sql = connection.createStatement();
        double lat1 = MenuUtils.askDouble("Latitude ( -90 to 90 ) ?");
        double lng1 = MenuUtils.askDouble("Longitude ( -180 to 180 ) ?");
        double distance = MenuUtils.askDouble("Distance in km? (enter <= 0 for default value)");
        if(distance<=0)distance = 10.0;
        sql.executeUpdate("create view v1 as select * from Listing where " +
                "(6371 * acos( \n" +
                "                cos( radians(Latitude) ) \n" +
                "              * cos( radians( "+lat1+" ) ) \n" +
                "              * cos( radians( "+lng1+" ) - radians(Longitude) ) \n" +
                "              + sin( radians(Latitude) ) \n" +
                "              * sin( radians( "+lat1+" ) )\n" +
                "                ) ) <= "+distance);


    }
    public  void postal() throws  SQLException{
        PreparedStatement sql = connection.prepareStatement("create view v1 as select * from Listing where " +
                "Country = ? AND Postal_Code like ? AND City = ?");
        String country = MenuUtils.askString("Country? ");
        String City = MenuUtils.askString("City?");
        String PC = MenuUtils.askString("PostalCode? ");
        sql.setString(1,country);
        sql.setString(2,PC.substring(0,3)+"%");
        sql.setString(3,City);
        sql.executeUpdate();
    }
    public void exact() throws SQLException{
        PreparedStatement sql = connection.prepareStatement("create view v1 as select * from Listing where " +
                "Country = ? AND Postal_Code = ? AND City = ? AND Street_Address like ?");
        String country = MenuUtils.askString("Country?");
        String PC = MenuUtils.askString("PostalCode?");
        String city = MenuUtils.askString("City?");
        String Address = MenuUtils.askString("Street Address?");
        sql.setString(1,country);
        sql.setString(2,PC);
        sql.setString(3,city);
        sql.setString(4,Address+"%");
        sql.executeUpdate();
    }
    public void byhost() throws  SQLException{
        PreparedStatement sql = connection.prepareStatement("create view v1 as select * from Listing where " +
                "Host_ID = ?");
        String uname = MenuUtils.askString("Host Username?");

            sql.setInt(1, Host.getByUsername(connection, uname).getId());
            sql.executeUpdate();


        }
    public void bytitle() throws SQLException{
        PreparedStatement sql = connection.prepareStatement("create v1 as select * from Listing where " +
                "Title like ?");
        String title = MenuUtils.askString("Title?");
        sql.setString(1,title+"%");
        sql.executeUpdate();
    }

}
