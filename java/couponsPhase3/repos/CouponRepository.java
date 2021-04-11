package couponsPhase3.repos;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import couponsPhase3.tables.Category;
import couponsPhase3.tables.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

	// By Company
	List<Coupon> findAllByCompanyId(int id);

	List<Coupon> findAllByCompanyIdAndCategory(int id, Category category);

	List<Coupon> findAllByCompanyIdAndPriceGreaterThanEqual(int id, double price);

	List<Coupon> findAllByCompanyIdAndPriceLessThanEqual(int id, double price);

	List<Coupon> findAllByCompanyIdAndPriceBetween(int id, double minPrice, double maxPrice);

	// By Category
	List<Coupon> findAllByCategory(Category category);

	// By price
	List<Coupon> findAllByPriceGreaterThanEqual(double price);

	List<Coupon> findAllByPriceLessThanEqual(double price);

	List<Coupon> findAllByPriceBetween(double minPrice, double maxPrice);

	// By amount
	List<Coupon> findAllByAmountGreaterThan(int amount);

	List<Coupon> findAllByAmountLessThan(int amount);

	List<Coupon> findAllByAmountBetween(int lowerBound, int upperBound);

	// By date
	List<Coupon> findAllByEndDateBefore(Date date);

	List<Coupon> findAllByEndDateAfter(Date date);

	List<Coupon> findAllByStartDateBefore(Date date);

	List<Coupon> findAllByStartDateAfter(Date date);
}
