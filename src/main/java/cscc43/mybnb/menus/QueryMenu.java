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

    public void start() {
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
            custom(choice == 3);
        }else{
            sql.executeUpdate("create view v2 as select Title,Price,Date_From,Date_To,Available from v1 NATURAL JOIN Calendar_Section");
        }
        printresults();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void printresults() throws SQLException{
        Statement s = connection.createStatement();
        ResultSet r = s.executeQuery("select * from v2");
        //print out each result
        System.out.println("Title  Price   Date_From  Date_To   Available");
        while(r.next()){
            String title = r.getString("Title");
            double price = r.getDouble("Price");
            Date from = r.getDate("Date_From");
            Date to = r.getDate("Date_To");
            boolean available = r.getBoolean("Available");
            System.out.println(title+"   "+price+"   "+from.toLocalDate()+"   "+to.toLocalDate()+"   "+available);
        }
    }
    public void dropView(String s){
        try {
            Statement sql = connection.createStatement();
            sql.execute("DROP VIEW IF EXISTS "+s);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void custom(Boolean exact) throws SQLException{
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
    if(exact == Boolean.FALSE){
    c = MenuUtils.menu("Add filter on Country and City?","Yes","No");
    }
    if(c==1 && exact == Boolean.FALSE){
        PreparedStatement ps = connection.prepareStatement("create view temp3 as select * from temp2 where " +
                " Country == ? and City like ?");
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
            choice = MenuUtils.askString("Which ones to you want (enter none to start query)?");
            while (!choice.equals("none") || s.isEmpty()){
                if(s.contains(choice)){
                    choices.add(choice);
                    s.remove(choice);
                }
            }
            //build our where clause
            String query = "";
            while(!choices.isEmpty()){
                query = query.concat(" EXISTS(select * from provides_amenity P where L.listing_ID = P.listing_ID AND P.Amenity_Name = " +
                        choices.get(0)+") ");
                choices.remove(0);
                if(!choices.isEmpty())query += "AND";
            }
            sql.executeUpdate("create view v2 as select * from temp3 L where "+query);
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
        double lng1 = MenuUtils.askDouble("Longitude ( -90 to 90 ) ?");
        double distance = MenuUtils.askDouble("Distance in km? (enter <= 0 for default value)");
        if(distance<=0)distance = 10.0;
        sql.executeUpdate("create view v1 as select * from listing where " +
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
                "Country = ? AND Postal_Code like ?");
        String country = MenuUtils.askString("Country? ");
        String PC = MenuUtils.askString("PostalCode? ");
        sql.setString(1,country);
        sql.setString(2,PC.substring(0,3)+"%");
        sql.executeUpdate();
    }
    public void exact() throws SQLException{
        PreparedStatement sql = connection.prepareStatement("create view v1 as select * from listing where " +
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
        PreparedStatement sql = connection.prepareStatement("create view v1 as select * from listing where " +
                "Host_ID = ?");
        String uname = MenuUtils.askString("Host Username?");
        try {
            sql.setInt(1, Host.getByUsername(connection, uname).getId());
        }catch(Exception e){
            e.printStackTrace();
        }

        }
    public void bytitle() throws SQLException{
        PreparedStatement sql = connection.prepareStatement("create v1 as select * from listing where " +
                "Title like ?");
        String title = MenuUtils.askString("Title?");
        sql.setString(1,title+"%");
    }

}
