-- Assumptions we are making for our project
-- Deletion Rules
/*
We enforce hard delete, where if a listing, user, host, is deleted
all information related to them is also deleted on cascade.

We assume that all IDs used are final and cannot be updated, only deleted
*/
-- Booking
/*
Booking can be cancelled by host or renter,
Cancelled 0 means uncancelled, 1 means cancelled by Renter, 2 means Cancelled by Host
We make sure to synchronize the availability with the Booking status
A calendar entry is always available with NULL renter_ID until someone books it
triggers make sure to take appropriate steps

*/
-- Comments
/*
Renters can only comment on a listing they have rented recently,
we define recently as in the span of a year. They can only comment if they have finished their stay

Hosts can only comment on renters if they have booked a listing recently, same defenition of recently applies.
But they can only comment if the stay was completed


*/
-- Postal Code
/*
The same postal code structure is present within a country.
We are assuming all postal codes are 6 alphanumeric characters and similar to the canadian postal code format.
Adjacent postal codes have first 3 characters matching.
*/