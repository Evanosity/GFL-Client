package com.grindforloot.client;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * I want the game to be a completely modular set of windows, that can be popped out of the main window.
 * Essentially, seamless hover-over graphics that can
 * @author Evan
 *
 */
public class Game extends Application{
	
	public NetSocket socket = null;
	public Vertx vertx = Vertx.vertx();
	
	public static void main(String...args) {
		launch(args);
	}
	
	public void connect() {
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
				connect();
			}
		});		
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		
		connect();
		

		Circle circle = new Circle();
		
		circle.setCenterX(300);
		circle.setCenterY(135);
		circle.setRadius(30);
		circle.setFill(Color.BLUE);
		circle.setStrokeWidth(20);
		
		circle.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			
			
			JsonObject outgoing = new JsonObject();
			outgoing.put("action", "get_paid");
			
			socket.write(Json.encodeToBuffer(outgoing));
			
			/*
			Stage newStage = new Stage();
			Group root = new Group();
			
			Scene newScene = new Scene(root, 600, 300);
			newStage.setTitle("YEET");
			newStage.setScene(newScene);
			newStage.show();
			*/
			
		});
		
		
		Circle circ = new Circle();
		circ.setCenterX(300);
		circ.setCenterY(135);
		circ.setRadius(30);
		circ.setFill(Color.RED);
		
		
		Tooltip test = new Tooltip("Test");
		test.setGraphic(circ);
		Tooltip.install(circle, test);
		
		Group root = new Group(circle);
		
		
		Scene scene = new Scene(root, 600, 300);	
		stage.setScene(scene);
		
		stage.setTitle("Evan is the best");
		stage.setAlwaysOnTop(false);
		stage.setMinWidth(600);
		stage.setMinHeight(300);
		stage.setOnCloseRequest(event -> {
			vertx.close();
			System.exit(0);
		});
		stage.show();
	}
}
