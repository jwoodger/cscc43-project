-- Some view that are helpful
drop view if exists location_details;
create view location_details as 
select distinct Country, City,Postal_Code
from mybnb.listing;
select *
from location_details;
drop view if exists host_user;
create view host_user as
select username,host_id from user join host on User_ID = Host_ID;
drop view if exists renter_user;
create view renter_user as
select username,renter_id from user join renter on User_ID = Renter_ID;

-- report A
-- by zip and city
select b.country,b.city,b.postal_code,count(bookingid) as count
from (select * from booking natural join calendar_section natural join listing
	where date_from >= now() and date_to <= now()
) a right join listing b on a.listing_id = b.listing_id
group by country,city,postal_code;
-- by city only
select b.country,b.city,count(bookingid) as count
from (select * from booking natural join calendar_section natural join listing
	where date_from >= now() and date_to <= now()
) a right join listing b on a.listing_id = b.listing_id
group by country,city;
-- report B
-- by city and postcode
select country,city,Postal_Code,count(*) as count
from listing
group by country,city,postal_code;
-- by city only
select country,city,count(*) as count
from listing
group by country,city;
-- by country only
select country,count(*) as count
from listing
group by country;
-- report C
-- per city
select a.country,a.city,username,count(Listing_ID) as count
from
(select Country,City,username,host_ID
from  location_details l cross join host_user u
group by Country,City,username) a left join listing b on a.host_ID = b.host_ID and a.country=b.country and a.city=b.city
group by a.country,a.city,a.username
order by a.country,a.city,count(Listing_ID) desc;
-- per country
select a.country,username,count(Listing_ID) as count
from
(select Country,City,username,host_ID
from  location_details l cross join host_user u
group by Country,City,username) a left join listing b on a.host_ID = b.host_ID and a.country=b.country and a.city=b.city
group by a.country,a.username
order by a.country,count(Listing_ID) desc;
-- report D
select Country,City,username,count
from(
select Country,City,username,count(*) as count
from listing l natural join host_user
group by Country,City,username
) L natural join (select Country,City,count(*) as total  from listing group by Country,City) t
where count >0.1*(total);
-- report E
-- per city
select country,city,username,count(bookingid) as count
from renter_user natural join booking natural join calendar_section natural join listing
where ? <= date(BookedOn) and date(BookedOn) <= ?
group by country,city,username
having count(bookingid) >= 2
order by country,city,count(bookingid);
-- not per city

select username,count(Bookingid) as count
from renter_user a left join (select * from booking where date(BookedOn)>= ? and date(BookedOn) <= ? ) b on a.renter_id = b.renter_id
group by username
 
 order by count(Bookingid);
-- report F 
-- Renters with Highest cancellations
select b.username,count(bookingid) as count
from (select * from renter_user natural join booking where cancelled = 1 and 2022<=year(BookedOn) and year(BookedOn)<2023) a right join renter_user b on a.username = b.username
group by b.username
order by count(bookingid) desc
limit 5;
-- Hosts with Highest cancellations
select b.username, count(bookingid) as count
from (select username,bookingid from booking b join calendar_section c on b.Calendar_ID = c.Calendar_ID natural join listing natural join host natural join host_user where cancelled = 2 and
2022<=year(BookedOn) and year(BookedOn)<2023) a
right join host_user b on a.username = b.username
group by b.username
order by count(bookingid) desc
limit 5;
select year(BookedOn)<2023 and year(BookedOn)>=2022, BookedOnhost_user
from Booking;
-- Host toolkit
-- price guess
select avg((price/(datediff(Date_To,Date_From)+1))*30) as average_ppm
from listing L natural join calendar_section 
where  country = 'Canada' and city = 'Toronto' and -- String together all the queries for amenities here
 year(Date_To)+10 >= year(now());