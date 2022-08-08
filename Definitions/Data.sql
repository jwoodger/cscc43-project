use mybnb;
INSERT INTO user VALUES (1,'2001-02-01','Broker','1234','Jenna','Khong','jenna78'),(2,'2001-04-02','Student','12345','MD Wasim','Zaman','wasimroks'),(3,'1980-01-01','Realtor','1233','John','Sonmez','john10'),(4,'1970-01-01','Movie Producer','999','JJ','Abram','jjabram'),(5,'2000-01-01','Manager','1234','Jack','Jack','jack1'),(6,'1999-01-01','Manager','999','Abdul','Kuddus','abdul100');

INSERT INTO renter VALUES (2,'123456789'),(4,'199999'),(5,'1234566');

INSERT INTO host VALUES (1),(3),(6);

INSERT INTO listing VALUES (1,1,'Cool House for Students','1-MorningSide #402','Toronto','Canada','A10000',43,-79),(1,2,'Great Expensive House','1-Belsize Dr.','Ottawa','Canada','P1S000',45,-78),(1,3,'Student Accomodation','123- Ellesmere','Toronto','Canada','M44100',43,-73.1),(3,4,'Crazy House','10-Crazy Street','Ottawa','Canada','PS2PS3',45.5,-78.01),(1,5,'Crazy Rich House','10-Richmond','Toronto','Canada','R11111',45,-78),(3,7,'House of The dead','10 - graveyard St.','Toronto','Canada','DEADAS',45,-78.8),(1,8,'Arizona Hot House','1 - Desert St.','Arizona','USA','HOT000',34,-111.1),(3,9,'Detroit House','1- Eminem St.','Detroit','USA','DED111',42,-82),(3,10,'Detroid House Roommate','1- Eminem St.','Detroit','USA','DED111',42,-82),(6,11,'Dhaka House','10- Baridhara','Dhaka','Bangladesh','BDG111',23,90);

INSERT INTO provides_amenity VALUES (1,'Cable'),(4,'Cable'),(8,'Cable'),(9,'Cable'),(10,'Cable'),(11,'Cable'),(1,'Guest Bedroom'),(4,'Guest Bedroom'),(8,'Guest Bedroom'),(4,'Hydro'),(9,'Hydro'),(10,'Hydro'),(2,'Water'),(4,'Water'),(8,'Water'),(9,'Water'),(10,'Water'),(11,'Water'),(4,'Wifi'),(7,'Wifi'),(11,'Wifi');

INSERT INTO calendar_section VALUES (1,'2022-08-01','2022-12-12',2,1,5000,0),(2,'2022-09-09','2022-10-10',NULL,3,8000,1),(3,'2022-01-01','2022-02-03',5,8,4000,0),(4,'2022-01-04','2022-12-04',NULL,9,60000,1),(5,'2023-01-01','2024-01-01',4,11,30000,0),(6,'2022-11-01','2022-12-01',4,11,1000,0);

INSERT INTO booking VALUES (1,1,2,0,'2022-08-05 13:33:13'),(2,2,2,2,'2022-08-07 11:33:44'),(3,3,5,0,'2022-08-08 07:44:28'),(4,2,5,1,'2022-08-08 08:06:41'),(5,2,5,1,'2022-08-08 08:06:59'),(6,6,4,1,'2022-08-08 08:23:13'),(7,5,4,1,'2022-08-08 08:41:10'),(8,6,4,0,'2022-08-08 08:46:35'),(9,5,4,0,'2022-08-08 08:46:48');

INSERT INTO comment VALUES (1,NULL,5,'2022-08-08 07:47:47','Very Good tenant.'),(2,NULL,5,'2022-08-08 07:51:42','Such a nice house. I love the heat <3');

INSERT INTO host_comment VALUES (1,1,5);

INSERT INTO renter_comment VALUES (2,5,8);