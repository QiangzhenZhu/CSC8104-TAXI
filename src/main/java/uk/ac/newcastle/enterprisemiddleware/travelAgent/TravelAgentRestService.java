/**
 * @Author: Yi Zhang
 * @Description: TODO
 **/
package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightBooking;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightService;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelBooking;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/travelAgent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TravelAgentRestService {

    @Inject
    Logger log;
    @Inject
    BookingService bookingservice;

    @Inject
    CustomerService customerservice;

    @Inject
    TravelAgentBookingRepository crud;

    @RestClient
    FlightService flightService;

    @RestClient
    HotelService hotelService;

    @GET
    @Operation(summary = "Fetch all TravelAgent", description = "Returns a JSON array of all stored TravelAgent objects.")
    public Response retrieveAll() {

        List<TravelAgentBooking> travelAgentBookings = crud.findAll();

        return Response.ok(travelAgentBookings).build();
    }

    @GET
    @Path("/customerId/{customerId:[0-9]+}")
    @Operation(summary = "Fetch all TravelAgent", description = "Returns a JSON array of all stored TravelAgent objects.")
    public Response retrieveAllBookingsByCId(
            @Parameter(description = "Id of Customer to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("customerId")
            long customerId) {

        Customer customer = customerservice.findById(customerId);
        if (customer == null) {
            // Verify that the hotel exists. Return 404, if not present.
            throw new RestServiceException("No Customer with the customerId " + customerId + " was found!", Response.Status.NOT_FOUND);
        }

        List<TravelAgentBooking> travelAgentBookings = crud.findAllByCustomer(customerId);

        return Response.ok(travelAgentBookings).build();
    }

    @DELETE
    @Operation(description = "Delete TravelAgent to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "TravelAgent delete successfully.")
    })
    @Transactional
    public Response deleteTravelAgentBooking(
            @Parameter
            Long id) {
        TravelAgentBooking travelAgentBooking = crud.findById(id);
        Customer customer = customerservice.findById(travelAgentBooking.getCustomerId());
        if (customer == null) {
            throw new RestServiceException("TravelAgent not exsit", Response.Status.BAD_REQUEST);
        }
        Response.ResponseBuilder builder;

        try {
            flightService.deleteFlightBooking(travelAgentBooking.getFlightBookingId());
            hotelService.deleteHotelBooking(travelAgentBooking.getHotelBookingId());
            Booking booking = bookingservice.findById(travelAgentBooking.getHotelBookingId());
            bookingservice.delete(booking);
            crud.delete(travelAgentBooking);

            builder = Response.ok(travelAgentBooking);

        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (Exception e) {
            throw new RestServiceException(e);
        }
        return builder.build();
    }

    @POST
    @Operation(description = "Add a new TravelAgent to the database")
    @Transactional
    public Response createTravelAgent(
            @Parameter(description =
                    "JSON representation of TravelAgent object to be added to the database", required = true)
            TravelAgent travelagent) {

        if (travelagent == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }
        if (travelagent.getCustomer() == null) {
            throw new RestServiceException("TravelAgent not exsit", Response.Status.BAD_REQUEST);
        }
        Response.ResponseBuilder builder;

        try {
            log.info(travelagent.toString());

            Booking bk = travelagent.getTaxiBooking();
            bk.setId(null);
            Booking booking = bookingservice.create(bk);
            log.info(booking.toString());

            log.info("flight-------------------------------------------");
            log.info(travelagent.getFlightBooking().toString());

            FlightBooking flightBooking = flightService.createFlightBooking(travelagent.getFlightBooking());

            log.info("hotel-------------------------------------------");
            log.info(travelagent.getHotelBooking().toString());

            HotelBooking hotelBooking = hotelService.createHotelBooking(travelagent.getHotelBooking());

            TravelAgentBooking travelAgentBooking = new TravelAgentBooking();
            travelAgentBooking.setId(null);
            travelAgentBooking.setCustomerId(travelagent.getCustomer().getId());
            travelAgentBooking.setFlightBookingId(flightBooking.getId());
            travelAgentBooking.setHotelBookingId(hotelBooking.getId());
            travelAgentBooking.setTaxiBookingId(booking.getId());
            travelAgentBooking = crud.create(travelAgentBooking);

            builder = Response.status(Response.Status.CREATED).entity(travelAgentBooking);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestServiceException(e);
        }
        return builder.build();
    }
}
