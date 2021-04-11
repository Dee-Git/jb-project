package couponsPhase3;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import couponsPhase3.threads.CouponExpirationDailyJob;
import couponsPhase3.threads.SessionExpirationJob;

@SpringBootApplication
public class Application {
	
	
	// TODO read up on AspectJ
	// TODO complete registration process
	// TODO Monad legal input verification, maybe

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

//		TESTING HASH
//		
//		try {
//			String s = Encrypt.SHA3_512("siteAction");
//			System.out.println(s);
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	

//		OLD TESTING
//		
//		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
//
//		Test test = ctx.getBean(Test.class);
//
//		boolean finished = false;
//
//		while (!finished)
//			try {
//				finished = test.testAll();
//			} catch (Exception e) {
//				System.out.println("\nError: " + e.getMessage() + "\n");
//				e.printStackTrace();
//			}
//		ctx.close();
	}
}