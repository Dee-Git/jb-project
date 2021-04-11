package couponsPhase3.tables;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Entity
@Table(name = "customers")
public class Customer implements Comparable<Customer> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(length = 64, nullable = false)
	private String firstName;

	@Column(length = 64, nullable = false)
	private String lastName;

	@Column(nullable = false, unique = true, length = 64)
	@Email
	private String email;

	@Column(nullable = false, length = 256)
	private String password;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	// .PERSIST results in "detached entity passed to persist" (spring) error
	@JoinTable(name = "customers_vs_coupons")
	private Set<Coupon> coupons;

	public Customer() {
	}

	public Customer(String firstName, String lastName, @Email String email, String password) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	//
	// Getters
	//

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public Set<Coupon> getCoupons() {

		return coupons;
	}

	//
	// Setters
	//

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	//
	// Override
	//

	@Override
	public String toString() {
		return "Customer " + id + " | firstName: " + firstName + " | lastName: " + lastName + " | email: " + email
				+ " | password: " + password;
	}

	@Override
	public int hashCode() {

		return this.id + this.firstName.hashCode() + this.lastName.hashCode() + this.email.hashCode()
				+ this.password.hashCode();
	}

	@Override
	public int compareTo(Customer c) {

		if (c != null) {
			if (this.lastName.hashCode() - c.lastName.hashCode() > 0)
				return 1;

			if (this.lastName.hashCode() - c.lastName.hashCode() < 0)
				return -1;

			return 0;
		}

		return 1;
	}
}