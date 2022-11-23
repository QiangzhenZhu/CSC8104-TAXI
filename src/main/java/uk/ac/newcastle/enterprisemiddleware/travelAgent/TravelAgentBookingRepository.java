/**
 * @Author: Yi Zhang
 * @Description: TODO
 **/
package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class TravelAgentBookingRepository {
    @Inject
    EntityManager em;

    List<TravelAgentBooking> findAll() {
        TypedQuery<TravelAgentBooking> query = em.createNamedQuery(TravelAgentBooking.FIND_ALL, TravelAgentBooking.class);
        return query.getResultList();
    }
    public TravelAgentBooking findById(Long id) {

        return em.find(TravelAgentBooking.class, id);
    }

    List<TravelAgentBooking> findAllByCustomer(Long customerId) {
        TypedQuery<TravelAgentBooking> query = em.createNamedQuery(TravelAgentBooking.FIND_BY_CUSTOMER, TravelAgentBooking.class).setParameter("customerId", customerId);
        return query.getResultList();
    }

    TravelAgentBooking create(TravelAgentBooking tbooking) throws Exception {

        // Write the consumer to the database.
        em.persist(tbooking);

        return tbooking;
    }

    TravelAgentBooking delete(TravelAgentBooking tbooking) throws Exception {

        if (tbooking.getId() != null) {

            em.remove(em.merge(tbooking));

        } else {

        }
        return tbooking;
    }

}

