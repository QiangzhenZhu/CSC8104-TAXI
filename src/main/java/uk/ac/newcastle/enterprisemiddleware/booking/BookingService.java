package uk.ac.newcastle.enterprisemiddleware.booking;


import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.area.AreaService;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 * @author Yi Zhang
 * @see BookingValidator
 * @see BookingRepository
 */
@Dependent
public class BookingService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingValidator validator;

    @Inject
    BookingRepository crud;

    //Removed temporarily due to non-existing AreaService
    @RestClient
    AreaService areaService;


    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @return The Booking with the specified id
     */
    public Booking findById(long id) {
        return crud.findByBookingId(id);
    }


    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     * <p>Validates the data in the provided Booking object using a {@link BookingValidator} object.<p/>
     *
     * @param book The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking create(Booking book) throws Exception {
        log.info("BookingService.create() - Creating "+ book.toString());

        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        //validator.validateBooking(book);

        // Write the booking to the database.
        return crud.create(book);
    }


    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.<p/>
     *
     * <p>Validates the data in the provided Booking object using a BookingValidator object.<p/>
     *
     * @param book The Booking object to be passed as an update to the application database
     * @param book The Booking object to be removed from the application database
     * @return The Booking object that has been successfully updated in the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws ConstraintViolationException, ValidationException, Exception
     *
     *                                       <p>Deletes the provided Booking object from the application database if found there.<p/>
     * @throws Exception
     */
    Booking update(Booking book) throws Exception {
        log.info("BookingService.update() - Updating " + book.getId() + " " + book.getBookDate());

        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        validator.validateBooking(book);

        // Either update the booking or add it if it can't be found.
        return crud.update(book);
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     *
     * @param book The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Booking delete(Booking book) throws Exception {
        log.info("delete() - Deleting " + book.toString());

        Booking deletedBooking = null;

        if (book.getId() != null) {
            deletedBooking = crud.delete(book);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedBooking;
    }
}
