package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Taxi objects against arbitrary requirements.</p>
 *
 * @author Yi Zhang
 * @see Taxi
 * @see TaxiRepository
 * @see Validator
 */
@ApplicationScoped//将对象实例化到CDI容器中，其作用域为application
public class TaxiValidator {
    @Inject
    Validator validator;

    @Inject
    TaxiRepository crud;

    /**
     * <p>Validates the given taxi object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing taxi with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param taxi The taxi object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If taxi with the same email already exists
     */
    void validateTaxi(Taxi taxi) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Taxi>> violations = validator.validate(taxi);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the registration number
        if (registrationNumberAlreadyExists(taxi.getRegistrationNumber(), taxi.getId())) {
            throw new UniqueRegistrationNumberException("Unique registration number Violation");
        }
    }

    /**
     * <p>Checks if a taxi with the same registration number is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Taxi class.</p>
     *
     * <p>Since Update will being using a registration number that is already in the database we need to make sure that it is the registration number
     * from the record being updated.</p>
     *
     * @param registrationNumber The registrationNumber to check is unique
     * @param id The user id to check the registrationNumber against if it was found
     * @return boolean which represents whether the registrationNumber was found, and if so if it belongs to the user with id
     */
    boolean registrationNumberAlreadyExists(String registrationNumber, Long id) {
        Taxi taxi = null;
        Taxi taxiWithID = null;
        try {
            taxi = crud.findByRegistrationNumber(registrationNumber);
        } catch (NoResultException e) {
            e.printStackTrace();
            // ignore
        }

        if (taxi != null && id != null) {
            try {
                taxiWithID = crud.findById(id);
                if (taxiWithID != null && taxiWithID.getRegistrationNumber().equals(registrationNumber)) {
                    taxi = null;
                }
            } catch (NoResultException e) {
                e.printStackTrace();
                // ignore
            }
        }
        return taxi != null;
    }
}

