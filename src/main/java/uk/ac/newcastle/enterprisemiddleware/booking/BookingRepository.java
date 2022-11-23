package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService} with the
 * Domain/Entity Object (see {@link Booking}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 *
 * @author Yi Zhang
 * @see Booking
 * @see EntityManager
 */
@RequestScoped
public class BookingRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a list of all persistent {@link Booking} objects, sorted by bookDate.</p>
     *
     * @return List of Booking objects
     */
    List<Booking> findAllOrderedByOrderMassage() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long booking id.<p/>
     *
     * @param bookingId The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    Booking findByBookingId(Long bookingId) {
        return em.find(Booking.class, bookingId);
    }

    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param book The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking create(Booking book) throws Exception {
        log.info("BookingService.create() - Creating "+ book.toString());

        // Write the booking to the database.
        em.persist(book);

        return book;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     *
     * <p>{@link EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param book The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Booking update(Booking book) throws Exception {
        log.info("TaxiRepository.update() - Updating " + book.getId() );

        // Either update the booking or add it if it can't be found.
        em.merge(book);

        return book;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param book The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Booking delete(Booking book) throws Exception {
        log.info("TaxiRepository.delete() - Deleting " + book.getId() );

        if (book.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(),
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it.
             *
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database
             * to reattach it.
             *
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument.
             * You first need an object in a persistent state to be able to delete it.
             *
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(book));

        } else {
            log.info("TaxiRepository.delete() - No ID was found so can't Delete.");
        }

        return book;
    }


}
