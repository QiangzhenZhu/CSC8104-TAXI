package uk.ac.newcastle.enterprisemiddleware.hotel;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;


@Path("/hotelBookings")
@RegisterRestClient(configKey = "hotel-api")
public interface HotelService {

    @GET
    List<HotelBooking> getFlightBookings();


    @GET
    @Path("/{id:[0-9]+}")
    HotelBooking getHotleBookingById(@PathParam("id") Long id);

    @GET
    @Path("/customerId/{customerId:[0-9]+}")
    List<HotelBooking> getHotelBookingsByCustomer(@PathParam("customerId") Long id);

    @POST
    HotelBooking createHotelBooking(HotelBooking hotelBooking);

    @DELETE
    @Path("/{id:[0-9]+}")
    HotelBooking deleteHotelBooking(@PathParam("id") Long id);
}
