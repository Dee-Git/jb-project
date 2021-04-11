package couponsPhase3;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import couponsPhase3.threads.CouponExpirationDailyJob;
import couponsPhase3.threads.SessionExpirationJob;

@SpringBootApplication
public class Application {
	

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		CouponExpirationDailyJob cJob = ctx.getBean(CouponExpirationDailyJob.class);
		SessionExpirationJob sJob = ctx.getBean(SessionExpirationJob.class);

		cJob.start();
		sJob.start();

		Scanner scan = new Scanner(System.in);
		String input = "";

		while (!input.equals("exit")) {
			input = scan.nextLine();
		}

		scan.close();
		sJob.stop();
		cJob.stop();
		ctx.close();
	}
}
