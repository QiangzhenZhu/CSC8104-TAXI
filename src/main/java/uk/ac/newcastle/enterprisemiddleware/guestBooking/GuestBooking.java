package uk.ac.newcastle.enterprisemiddleware.guestBooking;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

import java.io.Serializable;

/**
 * <p>This is a the Domain object. The Customer class represents how customer resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a customers are retrieved from the database (with @NamedQueries), and acceptable values
 * for Customer fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Yi Zhang
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error phone due to the syntax being in a String.  This makes it harder to debug.
 */

public class GuestBooking implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 14654887L;

    Customer customer;

    Booking booking;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }


}

