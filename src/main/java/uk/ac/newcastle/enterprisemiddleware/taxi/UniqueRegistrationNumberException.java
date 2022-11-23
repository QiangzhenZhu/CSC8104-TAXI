package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Contact's email address conflicts with that of another Contact.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 * @author Yi Zhang
 * @see Taxi
 */
public class UniqueRegistrationNumberException extends ValidationException {

    public UniqueRegistrationNumberException(String message) {
        super(message);
    }

    public UniqueRegistrationNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueRegistrationNumberException(Throwable cause) {
        super(cause);
    }
}

