/**
 * @Author: Yi Zhang
 * @Description: TODO
 **/
package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightBooking;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelBooking;

import javax.xml.crypto.Data;
import java.awt.print.Book;
import java.io.Serializable;
import java.util.Date;

public class TravelAgent implements Serializable {
    private static final long serialVersionUID = 145672386778L;
    Customer customer;
    Booking taxiBooking;
    FlightBooking flightBooking;
    HotelBooking hotelBooking;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking getTaxiBooking() {
        return taxiBooking;
    }

    public void setTaxiBooking(Booking taxiBooking) {
        this.taxiBooking = taxiBooking;
    }

    public FlightBooking getFlightBooking() {
        return flightBooking;
    }

    public void setFlightBooking(FlightBooking flightBooking) {
        this.flightBooking = flightBooking;
    }

    public HotelBooking getHotelBooking() {
        return hotelBooking;
    }

    public void setHotelBooking(HotelBooking hotelBooking) {
        this.hotelBooking = hotelBooking;
    }

    @Override
    public String toString() {
        return "TravelAgent{" +
                "customer=" + customer +
                ", taxiBooking=" + taxiBooking +
                ", flightBooking=" + flightBooking +
                ", hotelBooking=" + hotelBooking +
                '}';
    }
}
