-- Assumptions we are making for our project
-- Deletion Rules
/*
We enforce hard delete, where if a listing, user, host, is deleted
all information related to them is also deleted on cascade.

We assume that all IDs used are final and cannot be updated, only deleted
*/
-- Users,Hosts,Renter
/*
    Users can be of two sub-classes: Renter and Host
    They are mutually exclusive. Host_ID and Renter_ID are refer to the User_ID just renamed.
    All users have a unique username and a unique ID
*/
-- Listing
/*
    A Listing contains all the details about a lease (physical information like geo-location and amenities, and title,etc).
    It is not the actual lease itself, as we store the price and dates for renting on the Calendar_Section. 
    A lease only has one host.
*/
-- Amenities / Provides_amenities
/*
    A listing can have multiple amenities. Multiple Amenities can belong to different listings.
    Provides_amenities table allows for the many to many relationship.
    Amenities are hard coded (inserted only by DDL statements during database creation).
*/
-- Calendar
/*
    A calendar section notes the availability of the lease, on certain days.
    The host can control the pricing and dates available. Each calendar section can be booked by one renter only.
    We are assuming a lease is for only one renter.
    When it is available, renter_id is null. Else it can be unavailable with no renter(renter_id = null) as set by host.
    OR it can be unavailable with a renter meaning it was booked.
*/
-- Booking
/*
    Booking can be cancelled by host or renter,
    Cancelled 0 means uncancelled, 1 means cancelled by Renter, 2 means Cancelled by Host
    We make sure to synchronize the availability with the Booking status
    A calendar entry is always available with NULL renter_ID until someone books it
    triggers make sure to take appropriate steps
    We also use booking_id and note time of booking, since multiple bookings can be made / cancelled,
    which are used for reports.
    Hosts can cancel a booking on the day and any day before.
    Renters can only cancel any day before.
*/

-- Comments
/*
    Renters can only comment on a listing they have rented recently,
    we define recently as in the span of a year. They can only comment if they have finished their stay

    Hosts can only comment on renters if they have booked a listing recently, same defenition of recently applies.
    But they can only comment if the stay was completed

*/

-- Postal Code,Country,City
/*
    Hierarchy:
    Country -> City -> Postal Code
    
    The same postal code structure is present within a city.
    We are assuming all postal codes are 6 alphanumeric characters and similar to the canadian postal code format.
    Adjacent postal codes have first 3 characters matching.

*/