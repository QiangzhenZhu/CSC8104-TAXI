package uk.ac.newcastle.enterprisemiddleware.customer;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.lastName ASC, c.firstName ASC"),
        @NamedQuery(name = Customer.FIND_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email")
})
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 14654887L;

    public static final String FIND_ALL = "Customer.findAll";
    public static final String FIND_BY_EMAIL = "Customer.findByEmail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;


    @OneToMany(cascade = CascadeType.ALL, targetEntity = Booking.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private List<Booking> bookings;

    @NotNull
    @Pattern(regexp = "^0\\d{10}$")
    @Column(name = "phone_number")
    private String phoneNumber;


    @Past(message = "Birth dates can not be in the future. Please choose one from the past")
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    public Customer() {
    }

    public Customer(@NotNull @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String firstName,
                    @NotNull @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String lastName,
                    @NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String email,
                    @NotNull @Pattern(regexp = "^0\\d{10}") String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List getBookings() {
        return bookings;
    }

    public void setBookings(List bookings) {
        this.bookings = bookings;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return email.equals(customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}

