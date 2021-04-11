package couponsPhase3.threads;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import couponsPhase3.facade.AdminFacade;
import couponsPhase3.tables.Coupon;

/**
 * Scans for expired coupons every 86400000 mSec and calls
 * AdminFacade.deleteCoupon()
 * 
 * @author D
 *
 */
@Service
public class CouponExpirationDailyJob implements Runnable {

	@Autowired
	private AdminFacade adminFacade;

	private ArrayList<Coupon> coupons = new ArrayList<Coupon>();

	private Thread t;
	private boolean quit = false;

	public CouponExpirationDailyJob() {
	}

	@Override
	public void run() {

		while (!quit) {

			try {

				if (!coupons.isEmpty()) // fill list with new coupons
					coupons = new ArrayList<Coupon>();

				coupons.addAll(adminFacade.getAllCoupons());

				for (Coupon c : coupons) {

					Date exDate = c.getEndDate();

					if (exDate != null)

						if (exDate.compareTo(Date.valueOf(LocalDate.now())) <= 0) {

							adminFacade.deleteCoupon(c.getId());
							System.out.println("Coupon with id " + c.getId() + " expired and was removed.");
						} // If coupon's date has expired
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		try { // Sleep for a day
			coupons = new ArrayList<Coupon>();
			Thread.sleep(86400000);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Once a day; scan coupons in database; delete all with endDate before local
	 * now()
	 */
	public void start() {

		if (t == null) {

			t = new Thread(this, "CouponExpirationJob");
			t.setDaemon(true);
			t.start();
			System.out.println("\nSTART : Coupon Expiration Job " + LocalDate.now());
		}
	}

	public void stop() {

		quit = true;
		t.interrupt();
		System.out.println("\nSTOP : Coupon Expiration Job " + LocalDate.now());
	}
}
