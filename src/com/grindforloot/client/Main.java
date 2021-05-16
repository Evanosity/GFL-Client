package com.grindforloot.client;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

public class Main {
	static NetSocket socket = null;
	public static void main(String[]args) {
		Vertx vertx = Vertx.vertx();
		
		
		NetClient client = vertx.createNetClient();
		client.connect(8080, "localhost", res -> {
			if(res.succeeded()) {
				socket = res.result();
				
				socket.handler(buffer -> {
					JsonObject received = buffer.toJsonObject();
					
					System.out.println(received.getString("message"));
				});
				
				JsonObject json = new JsonObject();
				json.put("action", "Signup");
				
				Buffer buffer = Json.encodeToBuffer(json);
				
				socket.write(buffer);
			}
			else {
				System.out.println("Connection failed");
			}
				
		});
	}
}