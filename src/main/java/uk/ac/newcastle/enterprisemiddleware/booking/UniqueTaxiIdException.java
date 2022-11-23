package uk.ac.newcastle.enterprisemiddleware.booking;


import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Contact's email address conflicts with that of another Contact.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 * @author Yi Zhang
 * @see Booking
 */
public class UniqueTaxiIdException extends ValidationException {

    public UniqueTaxiIdException(String message) {
        super(message);
    }

    public UniqueTaxiIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueTaxiIdException(Throwable cause) {
        super(cause);
    }
}

