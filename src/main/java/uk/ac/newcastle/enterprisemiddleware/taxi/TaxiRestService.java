package uk.ac.newcastle.enterprisemiddleware.taxi;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.booking.UniqueTaxiIdException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * <p>This class produces a RESTFul service exposing the functionality of {@link TaxiService}.</p>
 *
 * <p>The Path annotation defines this as a REST Web Service using JAX-RS.</p>
 *
 * <p>By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they
 * can be overriden by adding the Consumes or Produces annotations to the individual methods.</p>
 *
 * <p>It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow
 * transaction demarcation when accessing the database." - Antonio Goncalves</p>
 *
 * <p>The full path for accessing endpoints defined herein is: api/contacts/*</p>
 *
 * @author Yi Zhang
 * @see TaxiService
 * @see Response
 */
@Path("/taxis")

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaxiRestService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    TaxiService service;

    /**
     * <p>Returns all taxis. They are sorted by the numerical order of the registration number.     </p>
     *
     * <p>The url can contain query parameters specifying the Taxi's registration number </p>
     *
     * <p>Examples: <pre>GET api/taxis?registration=1945693</pre>, <pre>GET api/taxis?registration=1945693</pre></p>
     *
     * @return A Response containing a list of Taxis
     */
    @GET
    @Operation(summary = "Fetch all Taxis", description = "Returns a JSON array of all stored Taxi objects.")
    public Response retrieveAllTaxis(@QueryParam("registrationNumber") String registrationNumber) {
        //Create an empty collection to contain the intersection of Taxis to be returned
        List<Taxi> taxis;

        if (registrationNumber == null) {
            taxis = service.findAllOrderedByRegistration();
        } else {
            taxis = (List<Taxi>) service.findByRegistrationNumber(registrationNumber);
        }
        return Response.ok(taxis).build();
    }

    /**
     * <p>Search for and return a Taxi identified by id.</p>
     *
     * @param taxiId The long parameter value provided as a Taxi's id
     * @return A Response containing a single Taxi
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(
            summary = "Fetch a Taxi by id",
            description = "Returns a JSON representation of the Taxi object with the provided id."
    )

    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Taxi found"),
            @APIResponse(responseCode = "404", description = "Taxi with id not found")
    })
    public Response retrieveCustomerById(
            @Parameter(description = "Id of Customer to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("id")
            long taxiId) {

        Taxi taxi = service.findById(taxiId);
        if (taxi == null) {
            // Verify that the customer exists. Return 404, if not present.
            throw new RestServiceException("No Customer with the id " + taxiId + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + taxiId + ": found Customer = " + taxi);

        return Response.ok(taxi).build();
    }

    /**
     * <p>Search for and return a Taxi identified by registration number.</p>
     *
     * @param registrationNumber The long parameter value provided as a Taxi's id
     * @return A Response containing a single Taxi
     */
    @GET
    @Cache
    @Path("/{registrationNumber:[a-zA-Z0-9] {7}$}")
    @Operation(
            summary = "Fetch a Taxi by registration number",
            description = "Returns a JSON representation of the Taxi object with the provided registration number."
    )

    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Taxi found"),
            @APIResponse(responseCode = "404", description = "Taxi with id not found")
    })
    public Response retrieveCustomerBy1Id(
            @Parameter(description = "Id of Customer to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("registrationNumber")
            String registrationNumber) {

        Taxi taxi = service.findByRegistrationNumber(registrationNumber);
        if (taxi == null) {
            // Verify that the customer exists. Return 404, if not present.
            throw new RestServiceException("No Customer with the registration number " + registrationNumber + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findByRegistrationNumber " + registrationNumber + ": found Customer = " + taxi);

        return Response.ok(taxi).build();
    }

    /**
     * <p>Search for and return a Taxi identified by seat number.</p>
     *
     * @param seatNumber The long parameter value provided as a Taxi's id
     * @return A Response containing a single Taxi
     */
    @GET
    @Cache
    @Max(20)
    @Min(2)
    @Path("/{seatNumber}")
    @Operation(
            summary = "Fetch a Taxi by seat number",
            description = "Returns a JSON representation of the Taxi object with the provided seat number."
    )

    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Taxi found"),
            @APIResponse(responseCode = "404", description = "Taxi with id not found")
    })
    public Response retrieveTaxiBySeatNumber(
            @Parameter(description = "Seat number of Taxi to be fetched")
            @Schema(minimum = "2", required = true)
            @PathParam("seatNumber")
            int seatNumber) {

        Taxi taxi = (Taxi) service.findBySeatNumber(seatNumber);
        if (taxi == null) {
            // Verify that the taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the seat number " + seatNumber + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findBySeatNumber " + seatNumber + ": found Taxi = " + taxi);

        return Response.ok(taxi).build();
    }

    /**
     * <p>Creates a new taxi from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param taxi The Taxi object, constructed automatically from JSON input, to be <i>created</i> via
     *             {@link TaxiService#create(Taxi)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    @Operation(description = "Add a new Taxi to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Taxi created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Taxi supplied in request body"),
            @APIResponse(responseCode = "409", description = "Taxi supplied in request body conflicts with an existing Taxi"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createTaxi(
            @Parameter(description = "JSON representation of Taxi object to be added to the database", required = true)
            Taxi taxi) {

        if (taxi == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Clear the ID if accidentally set
            taxi.setId(null);

            // Go add the new Taxi.
            service.create(taxi);

            // Create a "Resource Created" 201 Response and pass the taxi back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(taxi);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueTaxiIdException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("registration", "That registration is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        }  catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createTaxi completed. Taxi = " + taxi);
        return builder.build();
    }

    /**
     * <p>Updates the taxi with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param taxi The Taxi object, constructed automatically from JSON input, to be <i>updated</i> via
     *             {@link TaxiService#update(Taxi)}
     * @param id   The long parameter value provided as the id of the Taxi to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @Operation(description = "Update a Taxi in the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Taxi updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid Taxi supplied in request body"),
            @APIResponse(responseCode = "404", description = "Taxi with id not found"),
            @APIResponse(responseCode = "409", description = "Taxi details supplied in request body conflict with another existing Taxi"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response updateCustomer(
            @Parameter(description = "Id of Taxi to be updated", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id,
            @Parameter(description = "JSON representation of Taxi object to be updated in the database", required = true)
            Taxi taxi) {

        if (taxi == null || taxi.getId() == null) {
            throw new RestServiceException("Invalid Taxi supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (taxi.getId() != null && taxi.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Taxi ID in the request body must match that of the Taxi being updated");
            throw new RestServiceException("Taxi details supplied in request body conflict with another Taxi",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(taxi.getId()) == null) {
            // Verify that the taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            // Apply the changes the Taxi.
            service.update(taxi);

            // Create an OK Response and pass the taxi back in case it is needed.
            builder = Response.ok(taxi);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueTaxiIdException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("registration", "That registration is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("registration", "The registration area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createTaxi completed. Taxi = " + taxi);
        return builder.build();
    }

    /**
     * <p>Deletes a taxi using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the taxi to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Taxi from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The Taxi has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Taxi id supplied"),
            @APIResponse(responseCode = "404", description = "Taxi with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteTaxi(
            @Parameter(description = "Id of Taxi to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Taxi taxi = service.findById(id);
        if (taxi == null) {
            // Verify that the taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(taxi);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteTaxi completed. Taxi = " + taxi);
        return builder.build();
    }
}