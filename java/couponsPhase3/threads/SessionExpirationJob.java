package couponsPhase3.threads;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import couponsPhase3.login.Session;

/**
 * Scans for expired sessions and removes from WebConfig.sMap; every 300000 mSec
 * 
 * @author D
 *
 */
@Service
public class SessionExpirationJob implements Runnable {

	@Autowired
	private Map<String, Session> sMap;

	private Thread t;
	private boolean quit = false;

	public SessionExpirationJob() {
	}

	@Override
	public void run() {

		while (!quit) {

			synchronized (sMap) {

				try {

					if (!sMap.isEmpty()) {

						sMap.values().removeIf((s) -> (System.currentTimeMillis() - s.getLastAccessed()) > 1800000);

					} // session timeout after 30 minutes

					// Lambda implements;
					//
					// sMap.values().removeIf(new Predicate<Session>() {
					//
					// @Override
					// public boolean test(Session t) {
					//
					// if (System.currentTimeMillis() - t.getLastAccessed() > 1800000)
					// return true;
					//
					// return false;
					// }
					// });

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}

			try { // Sleep for 5 minutes
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Scans for expired sessions and removes from WebConfig.sMap
	 */
	public void start() {

		if (t == null) {

			t = new Thread(this, "SessionExpirationJob");
			t.setDaemon(true);
			t.start();
			System.out.println("\nSTART : Session Expiration Job " + LocalDate.now());
		}
	}

	public void stop() {

		quit = true;
		t.interrupt();
		System.out.println("\nSTOP : Session Expiration Job " + LocalDate.now());
	}
}