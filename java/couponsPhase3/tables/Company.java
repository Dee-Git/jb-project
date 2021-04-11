package couponsPhase3.tables;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Entity
@Table(name = "companies")
public class Company implements Comparable<Company> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, unique = true, length = 64)
	private String name;

	@Column(nullable = false, unique = true, length = 64)
	@Email
	private String email;

	@Column(nullable = false, length = 256)
	private String password;

	@OneToMany(mappedBy = "companyId", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<Coupon> coupons;

	public Company() {
	}

	public Company(String name, @Email String email, String password) {

		this.name = name;
		this.email = email;
		this.password = password;
	}

	//
	// Getters
	//

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	//
	// Setters
	//

	public void setName(String name) {
		this.name = name;
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
		return "Company " + id + " | name: " + name + " | email: " + email + " | password: " + password;
	}

	@Override
	public int hashCode() {

		return this.id + this.name.hashCode() + this.email.hashCode() + this.password.hashCode();
	}

	@Override
	public int compareTo(Company c) {

		if (c != null) {
			if (this.name.hashCode() - c.name.hashCode() > 0)
				return 1;

			if (this.name.hashCode() - c.name.hashCode() < 0)
				return -1;

			return 0;
		}

		return 1;
	}
}