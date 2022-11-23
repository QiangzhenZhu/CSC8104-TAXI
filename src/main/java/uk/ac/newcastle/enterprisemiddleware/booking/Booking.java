package uk.ac.newcastle.enterprisemiddleware.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>This is a the Domain object. The Booking class represents how booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a booking are retrieved from the database (with @NamedQueries), and acceptable values
 * for Booking fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Yi Zhang
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error phone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b ORDER BY b.customerId ASC"),
})
@XmlRootElement
@Table(name = "Booking", uniqueConstraints = @UniqueConstraint(columnNames = {"taxi_id","customer_id"}))

public class Booking implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1462154887L;

    public static final String FIND_ALL = "Booking.findAll";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @NotNull
    @Column(name = "taxi_id")
    private Long taxiId;


    @Future(message = "The scheduled time cannot be in the past. Please select from the future time.")
    @Column(name = "book_date")
    @Temporal(TemporalType.DATE)
    private Date bookDate;


    public Booking( Long id, Date bookingDate) {
        this.id=id;
        this.bookDate=bookingDate;
    }

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    public Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    @JsonIgnore
    @JoinColumn(name = "customerId", insertable = false, updatable = false)
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Customer customer;

    @JsonIgnore
    @JoinColumn(name = "taxiId", insertable = false, updatable = false)
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Taxi taxi;

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", taxiId=" + taxiId +
                ", bookDate=" + bookDate +
                ", customer=" + customer +
                ", taxi=" + taxi +
                '}';
    }
}

