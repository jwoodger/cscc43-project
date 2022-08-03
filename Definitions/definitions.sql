drop database mybnb;
create database mybnb;
use mybnb;

CREATE TABLE User(
  User_ID INTEGER AUTO_INCREMENT,
  DOB DATE,
  Occupation VARCHAR(32) DEFAULT NULL,
  SIN CHAR(9),
  First_Name VARCHAR(32) NOT NULL check (LENGTH(First_Name)>0),
  Last_Name VARCHAR(32) NOT NULL check (LENGTH(Last_Name)>0),
  username VARCHAR(10) unique check (LENGTH(username)>0),
  PRIMARY KEY(User_ID)
);

-- error message and check on insert
delimiter //
CREATE trigger tr_ins_User
BEFORE insert on User
FOR EACH ROW
BEGIN
	if length(new.Occupation=0) then
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Occupation cannot be empty';
end if;
    if  length(new.First_Name)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'First_Name cannot be empty';
end if; 
    if  length(new.Last_Name)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Last_Name cannot be empty';
end if; 
    if  length(new.username)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'UserName cannot be empty';
end if; 
END;
//
delimiter ;
-- error message and check on update
delimiter //
CREATE trigger tr_upd_User
BEFORE update on User
FOR EACH ROW
BEGIN
	if length(new.Occupation=0) then
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Occupation cannot be empty';
end if;
    if  length(new.First_Name)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'First_Name cannot be empty';
end if; 
    if  length(new.Last_Name)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Last_Name cannot be empty';
end if; 
    if  length(new.username)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'UserName cannot be empty';
end if; 
END;
//
delimiter ;







CREATE TABLE Host(
  Host_ID INTEGER PRIMARY KEY,
  FOREIGN KEY (Host_ID) REFERENCES User(User_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Renter(
  Renter_ID INTEGER PRIMARY KEY,
  Credit_Card_No CHAR(16) NOT NULL, -- we need credit cards
  FOREIGN KEY (Renter_ID) REFERENCES User(User_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Listing(
  Host_ID INTEGER references Host(Host_ID) ON DELETE CASCADE ON UPDATE CASCADE,
  Listing_ID INTEGER AUTO_INCREMENT,
  Title VARCHAR(32) NOT NULL CHECK (LENGTH(Title)>0),
  Street_Address VARCHAR(32) NOT NULL CHECK (LENGTH(Street_Address)>0),
  City VARCHAR(32) NOT NULL CHECK (LENGTH(City)>0),
  Country VARCHAR(32) NOT NULL CHECK (LENGTH(Country)>0),
  Postal_Code CHAR(6) NOT NULL,
  Latitude FLOAT CHECK(Latitude >= -90 AND Latitude <=90),
  Longitude FLOAT CHECK(Longitude >= -90 AND Longitude <=90),
  PRIMARY KEY(Listing_ID)
);
-- error message and check on insert
delimiter //
CREATE trigger tr_ins_Listing
BEFORE insert on Listing
FOR EACH ROW
BEGIN
	if length(new.Title=0) then
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Title cannot be empty';
end if;
    if  length(new.City)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'City cannot be empty';
end if;
    if  length(new.Country)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Country cannot be empty';
end if;
    if  new.Latitude<-90 OR new.Latitude>90 OR new.Longitude>90 or new.Longitude <-90 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Invalid Coordinates';
end if; 
END;
//
delimiter ;
-- error message and check on update
delimiter //
CREATE trigger tr_upd_Listing
BEFORE update on Listing
FOR EACH ROW
BEGIN
	if length(new.Title=0) then
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Title cannot be empty';
end if;
    if  length(new.City)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'City cannot be empty';
end if;
    if  length(new.Country)=0 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Country cannot be empty';
end if;
    if  new.Latitude<-90 OR new.Latitude>90 OR new.Longitude>90 or new.Longitude <-90 then
        SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Invalid Coordinates';
end if; 
END;
//
delimiter ;
/*
CREATE TABLE Hosting (
  Host_ID INTEGER,
  Listing_ID INTEGER,
  FOREIGN KEY (Host_ID) REFERENCES Host(Host_ID),
  FOREIGN KEY (Listing_ID) REFERENCES Listing(Listing_ID)
);
not necessary since its a one to many relationship 
*/

CREATE TABLE Amenity(
  Name VARCHAR(32) not null CHECK (length(Name)!=0),
  PRIMARY KEY(Name)
);
/*
List of Amenities is set by us so I didn't add any trigger messages
Todo: Add more amenities
*/

Insert into Amenity(Name) values ("Wifi"),("Cable"),("Hydro"),("Water"),("Guest Bedroom");


/*
keep this table since it is a many to many relation
*/
CREATE TABLE Provides_Amenity(
  Listing_ID INTEGER,
  Amenity_Name VARCHAR(32),
  FOREIGN KEY (Listing_ID) REFERENCES Listing(Listing_ID) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (Amenity_Name) REFERENCES Amenity(Name) ON UPDATE CASCADE ON DELETE CASCADE,
  PRIMARY KEY (Listing_ID,Amenity_Name)
);


CREATE TABLE Calendar_Section(
  Calendar_ID INTEGER UNIQUE,
  Date_From DATE NOT NULL,
  Date_To DATE NOT NULL,
  Renter_ID INTEGER DEFAULT NULL,
  -- Host_ID INTEGER, no need we can get this information from Listing
  Listing_ID INTEGER,
  Price FLOAT(2),
  Available bool DEFAULT TRUE, 
  PRIMARY KEY(Calendar_ID),
  CONSTRAINT CHECK (Date_From <= Date_To),
  CONSTRAINT CHECK (Price > 0.0),
  foreign key (Listing_ID) references Listing(Listing_ID) ON UPDATE CASCADE ON DELETE CASCADE,
  foreign key (Renter_ID) references Renter(Renter_ID) ON UPDATE CASCADE ON DELETE CASCADE
  
  
);

-- Makes sure that no listing overlaps with itself
-- check on insert
delimiter //
CREATE trigger tr_ins_Cal
BEFORE insert on Calendar_Section
FOR EACH ROW
BEGIN
if(new.price <=0) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Invalid price';
end if;
if(new.Date_From > new.Date_To) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Invalid date range';
end if;
if (EXISTS(
select *
from Calendar_Section C
where	C.Calendar_ID != new.Calendar_ID AND
		C.Listing_ID = new.Listing_ID AND
        (C.Date_From <= new.Date_To and C.Date_To >= new.Date_From)
)) then
		SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Date Overlaps';
end if;

if (new.Available = false and NOT(Renter_ID=NULL)) OR(new.Available=True and Renter_ID=NULL) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Cannot be unavailable and have no renter/ available and have renter';
end if;
END;
//
delimiter ;
-- check on update
delimiter //
CREATE trigger tr_upd_Cal
BEFORE update on Calendar_Section
FOR EACH ROW
BEGIN
if(new.price <=0) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Invalid price';
end if;
if(new.Date_From > new.Date_To) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Invalid date range';
end if;
if (EXISTS(
select *
from Calendar_Section C
where	C.Calendar_ID != new.Calendar_ID AND
		C.Listing_ID = new.Listing_ID AND
        (C.Date_From <= new.Date_To and C.Date_To >= new.Date_From)
)) then
		SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Date Overlaps';
end if;

if (new.Available = false and NOT(Renter_ID=NULL)) OR(new.Available=True and Renter_ID=NULL) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Cannot be unavailable and have no renter/ available and have renter';
end if;
END;
//
delimiter ;

CREATE TABLE Booking(
BookingID Integer AUTO_INCREMENT not null PRIMARY KEY
Calendar_ID INTEGER references Calendar_Section(Calendar_ID) ON UPDATE CASCADE ON DELETE CASCADE,
Renter_ID Integer references Renter(Renter_ID) ON UPDATE CASCADE ON DELETE CASCADE,
Cancelled smallint default 0 CHECK(Cancelled>=0 and Cancelled <=2), -- 0 for not cancelled, 1 for cancelled by Renter, 2 for cancelled by Host
BookedOn datetime default now()
);
-- The Cancelled bit is set by us so no trigger messages for user


-- When we insert or update a booking automatically change the availability of the calendar listing
delimiter //
CREATE trigger tr_ins_Booking
BEFORE insert on Booking
FOR EACH ROW
BEGIN
	if new.Cancelled=0 then
		update Calendar_Section 
        SET
        Renter_ID=new.Renter_ID,
		Available = false
        where Calendar_ID = new.Calendar_ID;
	end if;
	if new.Cancelled!=0 then
		update Calendar_Section 
        SET
        Renter_ID=NULL,
		Available = true
        where Calendar_ID = new.Calendar_ID;
	end if;
		
END;
//
delimiter ;

delimiter //
CREATE trigger tr_update_Booking
BEFORE update on Booking
FOR EACH ROW
BEGIN
	if new.Cancelled=0 then
		update Calendar_Section 
        SET
        Renter_ID=new.Renter_ID,
		Available = false
        where Calendar_ID = new.Calendar_ID;
	end if;
	if new.Cancelled!=0 then
		update Calendar_Section 
        SET
        Renter_ID=NULL,
		Available = true
        where Calendar_ID = new.Calendar_ID;
	end if;
		
END;
//
delimiter ;

CREATE TABLE Comment(
  Comment_ID INTEGER AUTO_INCREMENT,
  Calendar_ID INTEGER,
  Rating SMALLINT,
  Time datetime NOT NULL DEFAULT now(),
  Text VARCHAR(512) CHECK(LENGTH(Text) >0),
  PRIMARY KEY(Comment_ID),
  CONSTRAINT CHECK(Rating >= 0 AND Rating <= 5),
  FOREIGN KEY (Calendar_ID) references Calendar_Section(Calendar_ID) ON DELETE CASCADE ON UPDATE CASCADE
);
-- before we update or insert a comment apply checks and send appropriate error message
delimiter //
CREATE trigger tr_ins_Com
BEFORE insert on Comment
FOR EACH ROW
BEGIN
if(new.Text <=0) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Comment cannot be empty';
end if;
if(new.Rating <0 or new.Rating > 5) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Rating is 0-5';
end if;
-- can only comment if they have finished their stay i.e Date_To is passed but within 1 year from now()
-- and There exists a Booking

if not((select (Date_To) from Calendar_Section C where C.Calendar_ID = new.Calendar_ID) < now()
	and YEAR((select (Date_To) from Calendar_Section C where C.Calendar_ID = new.Calendar_ID))+1>YEAR(now())
    and EXISTS((select * from Booking where Booking.Calendar_ID = new.Calendar_ID and Booking.Cancelled =0)))
 then
 SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Cannot Comment on about this listing';
end if;

END;
//
delimiter ;

delimiter //
CREATE trigger tr_upd_Com
BEFORE update on Comment
FOR EACH ROW
BEGIN
if(new.Text <=0) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Comment cannot be empty';
end if;
if(new.Rating <0 or new.Rating > 5) then
	SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Rating is 0-5';
end if;
-- can only comment if they have finished their stay i.e Date_To is passed but within 1 year from now()
-- and There exists a Booking

if not((select (Date_To) from Calendar_Section C where C.Calendar_ID = new.Calendar_ID) < now()
	and YEAR((select (Date_To) from Calendar_Section C where C.Calendar_ID = new.Calendar_ID))+1>YEAR(now())
    and EXISTS((select * from Booking where Booking.Calendar_ID = new.Calendar_ID and Booking.Cancelled =0)))
 then
 SIGNAL SQLSTATE '45000'   
        SET MESSAGE_TEXT = 'Cannot Comment on about this listing';
end if;

END;
//
delimiter ;

/*
I am not adding in any triggers or checks here
If the Listing was not recent / valid We shouldn't enter into Host_Comment or Renter_Comment
*/

-- make sure that host_id and renter_id and listing_id all share a calendar entry
CREATE TABLE Host_Comment (
  Comment_ID INTEGER PRIMARY KEY,
  Host_ID INTEGER not null REFERENCES Host(Host_ID) ON UPDATE CASCADE ON DELETE CASCADE,
  Renter_ID INTEGER not null REFERENCES Renter(Renter_ID) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (Comment_ID) REFERENCES Comment(Comment_ID) ON UPDATE CASCADE ON DELETE CASCADE
  
);
/*
make sure the host and renter has a listing recently
*/
CREATE TABLE Renter_Comment(
  Comment_ID INTEGER PRIMARY KEY,
  Renter_ID INTEGER not null references Renter(Renter_ID) ON UPDATE CASCADE ON DELETE CASCADE,
  Listing_ID INTEGER not null references Listing(Listing_ID) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (Comment_ID) REFERENCES Comment(Comment_ID) ON UPDATE CASCADE ON DELETE CASCADE
	-- make sure that the renter is commenting on a listing they have actually rented
);

/*
similar as before since its a one to many , just add a Calendar id on the comments relation
CREATE TABLE Comment_On(
  Calendar_ID INTEGER,
  Comment_ID INTEGER,
  FOREIGN KEY (Calendar_ID) REFERENCES Calendar_Section(Calendar_ID),
  FOREIGN KEY (Comment_ID) REFERENCES Comment(Comment_ID),
  PRIMARY KEY (Calendar_ID,Comment_ID)
);*/
