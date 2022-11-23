package uk.ac.newcastle.enterprisemiddleware.flight;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@Path("/flightBookings")
@RegisterRestClient(configKey = "flight-api")
public interface FlightService {

    @GET
    List<FlightBooking> getFlightBookings();


    @GET
    @Path("/{id:[0-9]+}")
    FlightBooking getFlightBookingById(@PathParam("id") Long id);

    @GET
    @Path("/customerId/{customerId:[0-9]+}")
    List<FlightBooking> getFlightBookingsByCustomer(@PathParam("customerId") Long id);

    @POST
    FlightBooking createFlightBooking(FlightBooking flightbooking);

    @DELETE
    @Path("/{id:[0-9]+}")
    FlightBooking deleteFlightBooking(@PathParam("id") Long id);
}