package uk.ac.newcastle.enterprisemiddleware.guestBooking;

import org.eclipse.microprofile.openapi.annotations.Operation;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.customer.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;

import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * <p>This class produces a RESTFul service exposing the functionality of {@link CustomerService}{@link BookingService}{@link TaxiService}.</p>
 *
 * <p>The Path annotation defines this as a REST Web Service using JAX-RS.</p>
 *
 * <p>By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they
 * can be overriden by adding the Consumes or Produces annotations to the individual methods.</p>
 *
 * <p>It is Stateless to "inform the container that this RESTFul web service should also be treated as an EJB and allow
 * transaction demarcation when accessing the database." - Antonio Goncalves</p>
 *
 * <p>The full path for accessing endpoints defined herein is: api/customers/*</p>
 *
 * @author Yi Zhang
 * @see CustomerService,BookingService,TaxiService
 * @see Response
 */
@Path("/guestBookings")

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerService service;
    @Inject
    BookingService booking;
    @Inject
    TaxiService taxi;
    @Inject
    UserTransaction transaction;

    /**
     * <p>Creates a new customer from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param guestBooking The Customer object, constructed automatically from JSON input, to be <i>created</i> via
     *                     {@link CustomerService#create(Customer)}{@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */

    @POST
    @Operation(description = "Add a new Customer to the booking")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Customer supplied in request body"),
            @APIResponse(responseCode = "404", description = "Taxi with ID not found"),
            @APIResponse(responseCode = "409", description = "Customer supplied in request body conflicts with an existing Customer"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    //@Transactional
    public Response createGuestBooking(
            @Parameter(description = "JSON representation of Customer object to be added to the database", required = true)
            GuestBooking guestBooking) throws SystemException {
        log.info("GuestBookingRestService -- createGuestBooking starts executing");

        if (guestBooking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            transaction.begin();
            Customer customer = guestBooking.getCustomer();
            try{
                customer = service.findByEmail(guestBooking.getCustomer().getEmail());
            }catch(Exception e){
                customer.setId(null);
                customer = service.create(customer);
            }
//            if(customer == null){
//                customer = service.create(customer);
//            }
            Booking book = guestBooking.getBooking();
            book.setCustomerId(customer.getId());
            book.setId(null);
            book=booking.create(book);
            guestBooking.setBooking(book);
            guestBooking.setCustomer(customer);

            builder = Response.status(Response.Status.CREATED).entity(guestBooking);

            transaction.commit();

        } catch (ConstraintViolationException ce) {
            transaction.rollback();
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueEmailException e) {
            transaction.rollback();
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            transaction.rollback();
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("area_code", "The telephone area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            throw new RestServiceException(e);
        }

        log.info("GuestBookingRestService -- createGuestBooking starts executing");
        return builder.build();
    }

}
