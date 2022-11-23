/**
 * @Author: Yi Zhang
 * @Description: TODO
 **/
package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@Entity
@NamedQueries({
        @NamedQuery(name = TravelAgentBooking.FIND_ALL, query = "SELECT c FROM TravelAgentBooking c ORDER BY c.customerId"),
        @NamedQuery(name = TravelAgentBooking.FIND_BY_CUSTOMER, query = "SELECT c FROM TravelAgentBooking c WHERE c.customerId = :customerId")

})
@XmlRootElement
@Table(name = "Travel_Agent_Booking")
public class TravelAgentBooking implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1462154887L;

    public static final String FIND_ALL = "TravelAgentBooking.findAll";
    public static final String FIND_BY_CUSTOMER="TravelBooking.findByCustomer";

    @Id
    @TableGenerator(name = "TRAVEL_AGENT_ID", table = "PK_TRAVEL_AGENT_GENERATE_TABLE")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TRAVEL_AGENT_ID")
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "hotel_booking_id")
    private Long hotelBookingId;
    @Column(name = "taxi_booking_id")
    private Long taxiBookingId;
    @Column(name = "flight_booking_id")
    private Long flightBookingId;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Booking booking;

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

    public Long getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public Long getTaxiBookingId() {
        return taxiBookingId;
    }

    public void setTaxiBookingId(Long taxiBookingId) {
        this.taxiBookingId = taxiBookingId;
    }

    public Long getFlightBookingId() {
        return flightBookingId;
    }

    public void setFlightBookingId(Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
