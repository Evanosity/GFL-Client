package com.grindforloot.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import javafx.scene.control.TextField;

public class AuthUtils {
	
	/**
	 * Attempt to log the user in. The result of this request will return asynchronously from the socket
	 * @param socket
	 * @param emailField
	 * @param passField
	 */
	public static void attemptAuthentication(NetSocket socket, TextField emailField, TextField passField) {
		
		String email = emailField.getText();
		String password = passField.getText();
		
		//TODO enforce a regex on both of these.
		boolean passwordAllowed = false;
		boolean emailAllowed = false;
		
		if(false == emailAllowed) {
			Game.displayError("Auth Error", "Invalid email.");
		}
		if(false == passwordAllowed) {
			Game.displayError("Auth Error", "Invalid Password.");
		}
		
		String hashedPassword = hashString(password);
		
		JsonObject outgoing = new JsonObject();
		
		outgoing.put("type", "action")
		.put("action", "login")
		.put("email", email)
		.put("password", hashedPassword);
		
		socket.write(Json.encodeToBuffer(outgoing));
		
	}
	
	/**
	 * Hash a string using SHA3-256
	 * @param original
	 * @return
	 */
	public static String hashString(String original) {
		byte[] rawHash = hashString(original, "SHA3-256");
		return new String(rawHash, StandardCharsets.UTF_8);
	}
	
	/**
	 * Hash the given string
	 * @param original - the string
	 * @param algorithm - the algorithm to use
	 * @return a byte array; the hash
	 */
	private static byte[] hashString(String original, String algorithm) {
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance(algorithm);
			return digest.digest(original.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			//I don't foresee doing anything other than SHA3-256 so ignore it
		}
		
		return new byte[0];
	}
}
