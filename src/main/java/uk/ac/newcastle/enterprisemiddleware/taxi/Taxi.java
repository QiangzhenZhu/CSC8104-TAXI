package uk.ac.newcastle.enterprisemiddleware.taxi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>This is a the Domain object. The Taxi class represents how taxi resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a taxis are retrieved from the database (with @NamedQueries), and acceptable values
 * for Taxi fields (with @NotNull, @Pattern etc...)<p/>
 *
 * @author Yi Zhang
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error phone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT t FROM Taxi t ORDER BY t.registrationNumber ASC"),
        @NamedQuery(name = Taxi.FIND_BY_REG, query = "SELECT c FROM Taxi c WHERE c.registrationNumber = :registrationNumber")
})
@XmlRootElement
@Table(name = "taxi", uniqueConstraints = @UniqueConstraint(columnNames = "registration_number"))

public class Taxi implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1456723867L;

    public static final String FIND_ALL = "Taxi.findAll";
    public static final String FIND_BY_REG = "Taxi.findByRegistrationNumber";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9]{7}$", message = "Please enter a seven-digit registrationNumber number consisting of numbers and letters.")
    @Column(name = "registration_number")
    private String registrationNumber;

    @NotNull
    @Max(20)
    @Min(2)
    @Column(name = "seat_number")
    private int seatNumber;

    @NotNull
    @Pattern(regexp = "^0\\d{10}")
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Booking.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "taxi_id")
    private List<Booking> taxiId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Booking> getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(List<Booking> taxiId) {
        this.taxiId = taxiId;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Taxi)) return false;
        Taxi taxi = (Taxi) o;
        return registrationNumber.equals(taxi.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(registrationNumber);
    }
}

