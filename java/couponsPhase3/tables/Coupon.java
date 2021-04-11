package couponsPhase3.tables;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Check;

@Entity
@Table(name = "coupons")
public class Coupon implements Comparable<Coupon> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

//	@ManyToOne
//	@JoinColumn(foreignKey = @ForeignKey(name = "fk_company"))
	private int companyId;

	@Column(nullable = false)
	@Min(0)
	private int amount;

	@Column(nullable = false, length = 64)
	private String title;

	@Column(nullable = false, length = 128)
	private String description;

	@Column
	private String image;

	@Column
	@Enumerated(EnumType.ORDINAL)
	private Category category;

	@Column(nullable = false)
	@Check(constraints = "start_date > now()")
	private Date startDate;

	@Column(nullable = false)
	@Check(constraints = "end_date > start_date")
	private Date endDate;

	@Column(nullable = false, precision = 2)
	@Min(0)
	private double price;

	public Coupon() {
	}

	public Coupon(int companyId, @Min(1) int amount, String title, String description, String image, Category category,
			@FutureOrPresent Date startDate, @Future Date endDate, @Min(0) double price) {
		this.companyId = companyId;
		this.amount = amount;
		this.title = title;
		this.description = description;
		this.image = image;
		this.category = category;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
	}

	//
	// Getters
	//

	public int getId() {
		return id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public int getAmount() {
		return amount;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	public Category getCategory() {
		return category;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public double getPrice() {
		return price;
	}

	//
	// Setters
	//

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setCategory(Category category) {

		this.category = category;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	//
	// Override
	//

	@Override
	public String toString() {
		return "Coupon " + id + " | companyId: " + companyId + " | amount: " + amount + " | title: " + title
				+ " | description: " + description + " | image: " + image + " | category: " + category
				+ " | startDate: " + startDate + " | endDate: " + endDate + " | price: " + price;
	}

	@Override
	/**
	 * This method is mainly used for validating coupons for purchase.
	 */
	public int hashCode() {

		return String.valueOf(this.id).hashCode() + String.valueOf(this.companyId).hashCode()
				+ String.valueOf(this.amount).hashCode() + String.valueOf(this.price).hashCode() + this.title.hashCode()
				+ this.description.hashCode() + this.image.hashCode() + this.category.toString().hashCode()
				+ this.startDate.toString().hashCode() + this.endDate.toString().hashCode();
	}

	@Override
	/**
	 * This method is mainly used for validating coupons for purchase. In order to
	 * establish equality; disregards description, title, and image. To establish
	 * greater/lesser; compares companyId
	 */
	public int compareTo(Coupon o) {

		if (o != null) {
			if (this.id == o.id && this.companyId == o.companyId && this.amount == o.amount
					&& this.category == o.category && this.endDate.equals(o.endDate)
					&& this.startDate.equals(o.startDate) && this.price == o.price)
				return 0;

			if (this.companyId - o.companyId < 0)
				return -1;
		}

		return 1;
	}
}