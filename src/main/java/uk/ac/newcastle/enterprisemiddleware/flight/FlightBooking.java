/**
 * @Author: Yi Zhang
 * @Description: TODO
 **/
package uk.ac.newcastle.enterprisemiddleware.flight;

import java.io.Serializable;
import java.util.Date;

public class FlightBooking implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 145672123867L;

    private Long id;
    private Long flightId;
    private Long customerId;
    private Date flightDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    @Override
    public String toString() {
        return "FlightBooking{" +
                "id=" + id +
                ", flightId=" + flightId +
                ", customerId=" + customerId +
                ", flightDate=" + flightDate +
                '}';
    }
}
