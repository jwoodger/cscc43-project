use mybnb;
INSERT INTO user VALUES (1,'2001-02-01','Broker','1234','Jenna','Khong','jenna78'),(2,'2001-04-02','Student','12345','MD Wasim','Zaman','wasimroks'),(3,'1980-01-01','Realtor','1233','John','Sonmez','john10'),(4,'1970-01-01','Movie Producer','999','JJ','Abram','jjabram');
INSERT INTO renter VALUES (2,'123456789'),(4,'199999');
INSERT INTO host VALUES (1),(3);
INSERT INTO listing VALUES (1,1,'Cool House for Students','1-MorningSide #402','Toronto','Canada','A10000',43,-79),(1,2,'Great Expensive House','1-Belsize Dr.','Ottawa','Canada','P1S000',45,-78),(1,3,'Student Accomodation','123- Ellesmere','Toronto','Canada','M44100',43,-73.1),(3,4,'Crazy House','10-Crazy Street','Ottawa','Canada','PS2PS3',45.5,-78.01),(1,5,'Crazy Rich House','10-Richmond','Toronto','Canada','R11111',45,-78),(3,7,'House of The dead','10 - graveyard St.','Toronto','Canada','DEADAS',45,-78.8);
INSERT INTO calendar_section VALUES (1,'2022-08-01','2022-12-12',2,1,5000,0),(2,'2022-09-09','2022-10-10',2,3,8000,0);
INSERT INTO booking VALUES (1,1,2,0,'2022-08-05 13:33:13'),(2,2,2,0,'2022-08-07 11:33:44');
INSERT INTO provides_amenity VALUES (1,'Cable'),(4,'Cable'),(1,'Guest Bedroom'),(4,'Guest Bedroom'),(4,'Hydro'),(2,'Water'),(4,'Water'),(4,'Wifi'),(7,'Wifi');