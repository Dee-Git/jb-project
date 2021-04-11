package couponsPhase3.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

	/**
	 * 
	 * @param the original unencoded string
	 * @return encoded string
	 * @throws NoSuchAlgorithmException <MessageDigest>.getInstance exception
	 */
	public static String SHA3_512(String s) throws NoSuchAlgorithmException {

		MessageDigest digest = MessageDigest.getInstance("SHA3-512");

		// Convert string to bytes
		byte[] encodedHash = digest.digest(s.getBytes(StandardCharsets.UTF_8));

		return bytesToHex(encodedHash);
	}

	private static String bytesToHex(byte[] hash) {

		// Create string with 16 empty Chars
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < hash.length; i++) {
			// 1 AND byte at(i) (0 - 127)
			String tempHex = Integer.toHexString(0xff & hash[i]);

			// if hash[i] is under/equals 'F' = 15 = 00001111
			if (tempHex.length() == 1)
				hexString.append('0'); // SALT?

			hexString.append(tempHex);
		}

		return hexString.toString();
	}
}
