/**
 * @Author: Yi Zhang
 * @Description: TODO
 **/
package uk.ac.newcastle.enterprisemiddleware.hotel;

import java.io.Serializable;
import java.util.Date;

public class HotelBooking implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 145672123867L;

    private Long id;
    private Long hotelId;
    private Long customerId;
    private Date bookingdate;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Date getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(Date bookingdate) {
        this.bookingdate = bookingdate;
    }

    @Override
    public String toString() {
        return "HotelBooking{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", customerId=" + customerId +
                ", bookingdate=" + bookingdate +
                '}';
    }
}
