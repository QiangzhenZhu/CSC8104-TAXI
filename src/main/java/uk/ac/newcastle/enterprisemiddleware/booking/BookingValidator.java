package uk.ac.newcastle.enterprisemiddleware.booking;

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
 * <p>This class provides methods to check Customer objects against arbitrary requirements.</p>
 *
 * @author Yi Zhang
 * @see Booking
 * @see BookingRepository
 * @see Validator
 */
@ApplicationScoped
public class BookingValidator {
    @Inject
    Validator validator;

    @Inject
    BookingRepository crud;

    /**
     * <p>Validates the given customer object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing customer with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * @param book The customer object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException          If customer with the same email already exists
     */
    void validateBooking(Booking book) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(book);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
//        if (emailAlreadyExists(book.getEmail(), book.getId())) {
//            throw new UniqueEmailException("Unique Email Violation");
//        }
    }


    /**
     * <p>Checks if a customer with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Customer class.</p>
     *
     * <p>Since Update will being using an email that is already in the database we need to make sure that it is the email
     * from the record being updated.</p>
     *
     * @param email The email to check is unique
     * @param id    The user id to check the email against if it was found
     * @return boolean which represents whether the email was found, and if so if it belongs to the user with id
     */
    boolean bookingAlreadyExists(String email, Long id) {
        Booking book = null;
        Booking customerWithID = null;
//        try {
//            book = crud.findByEmail(email);
//        } catch (NoResultException e) {
//            // ignore
//        }
//
//        if (book != null && id != null) {
//            try {
//                customerWithID = crud.findById(id);
//                if (customerWithID != null && customerWithID.getEmail().equals(email)) {
//                    book = null;
//                }
//            } catch (NoResultException e) {
//                // ignore
//            }
//        }
        return book != null;
    }
}

