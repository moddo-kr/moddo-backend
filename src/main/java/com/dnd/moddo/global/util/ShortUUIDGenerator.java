package com.dnd.moddo.global.util;

import java.math.BigInteger;

public class ShortUUIDGenerator {
	private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String shortenUUID(String uuidString) {
		String hex = uuidString.replace("-", "");

		BigInteger number = new BigInteger(hex, 16);

		String base62 = encodeBase62(number);

		//앞 8자리 반환 (짧을 경우 0-padding)
		return base62.length() >= 8 ? base62.substring(0, 8) :
			String.format("%8s", base62).replace(' ', '0');
	}

	private static String encodeBase62(BigInteger number) {
		StringBuilder sb = new StringBuilder();
		BigInteger base = BigInteger.valueOf(62);

		while (number.compareTo(BigInteger.ZERO) > 0) {
			BigInteger[] divmod = number.divideAndRemainder(base);
			sb.append(BASE62.charAt(divmod[1].intValue()));
			number = divmod[0];
		}

		return sb.reverse().toString();
	}

}
