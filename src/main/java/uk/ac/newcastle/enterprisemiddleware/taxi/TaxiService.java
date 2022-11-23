package uk.ac.newcastle.enterprisemiddleware.taxi;


import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.area.Area;
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
 *
 * @author Yi Zhang
 * @see TaxiValidator
 * @see TaxiRepository
 */
@Dependent
public class TaxiService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    TaxiValidator validator;

    @Inject
    TaxiRepository crud;

    //Removed temporarily due to non-existing AreaService
    @RestClient
    AreaService areaService;

    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sort by number order of registration number.<p/>
     *
     * @return List of Taxi objects
     */
    List<Taxi> findAllOrderedByRegistration() {
        return crud.findAllOrderedByRegistration();
    }

    /**
     * <p>Returns a single Taxi object, specified by a Long id.<p/>
     *
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    public Taxi findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Taxi object, specified by a String registration number.</p>
     *
     * <p>If there is more than one Taxi with the specified registration, only the first encountered will be returned.<p/>
     *
     * @param registrationNumber The email field of the Taxi to be returned
     * @return The first Taxi with the specified email
     */
    Taxi findByRegistrationNumber(String registrationNumber) {
        return crud.findByRegistrationNumber(registrationNumber);
    }

    /**
     * <p>Returns a single Taxi object, specified by an int seatNumber.<p/>
     *
     * @param seatNumber The firstName field of the Taxi to be returned
     * @return The first Taxi with the specified firstName
     */
    List<Taxi> findBySeatNumber(int seatNumber) {
        return crud.findBySeatNumber(seatNumber);
    }


    /**
     * <p>Writes the provided Taxi object to the application database.<p/>
     *
     * <p>Validates the data in the provided Taxi object using a {@link TaxiValidator} object.<p/>
     *
     * @param taxi The Taxi object to be written to the database using a {@link TaxiRepository} object
     * @return The Taxi object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Taxi create(Taxi taxi) throws Exception {
        log.info("TaxiService.create() - Creating " + taxi.getRegistrationNumber() + " " + taxi.getSeatNumber());
        // Check to make sure the data fits with the parameters in the Taxi model and passes validation.
        validator.validateTaxi(taxi);
        // Write the taxi to the database.
        return crud.create(taxi);
    }

    /**
     * <p>Updates an existing Taxi object in the application database with the provided Taxi object.<p/>
     *
     * <p>Validates the data in the provided Taxi object using a TaxiValidator object.<p/>
     *
     * @param taxi The Taxi object to be passed as an update to the application database
     * @return The Taxi object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Taxi update(Taxi taxi) throws Exception {
        log.info("TaxiService.update() - Updating " + taxi.getRegistrationNumber() + " " + taxi.getSeatNumber());

        // Check to make sure the data fits with the parameters in the Taxi model and passes validation.
        validator.validateTaxi(taxi);

        // Either update the taxi or add it if it can't be found.
        return crud.update(taxi);
    }

    /**
     * <p>Deletes the provided Taxi object from the application database if found there.<p/>
     *
     * @param taxi The Taxi object to be removed from the application database
     * @return The Taxi object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Taxi delete(Taxi taxi) throws Exception {
        log.info("delete() - Deleting " + taxi.toString());

        Taxi deletedTaxi = null;

        if (taxi.getId() != null) {
            deletedTaxi = crud.delete(taxi);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedTaxi;
    }
}
