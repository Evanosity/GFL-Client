package com.grindforloot.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtils {
	/**
	 * Hash a string using SHA3-256
	 * @param original
	 * @return
	 */
	public String hashString(String original) {
		byte[] rawHash = hashString(original, "SHA3-256");
		return new String(rawHash, StandardCharsets.UTF_8);
	}
	
	/**
	 * Hash the given string
	 * @param original - the string
	 * @param algorithm - the algorithm to use
	 * @return a byte array; the hash
	 */
	private byte[] hashString(String original, String algorithm) {
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance(algorithm);
			return digest.digest(original.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			//this algorithm does exist so suck it
		}
		
		return new byte[0];
	}
}
