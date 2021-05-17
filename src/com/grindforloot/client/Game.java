package com.grindforloot.client;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	public static Stage mainStage;
	
	public static void main(String...args) {
		launch(args);
	}
	
	public void connect() {
		//todo consider how many event loop threads we want. I'm thinking 2....
		NetClient client = vertx.createNetClient();
		client.connect(8080, "localhost", res -> {
			if(res.succeeded()) {
				socket = res.result();
				
				socket.handler(buffer -> {
					JsonObject received = buffer.toJsonObject();
					
					switch(received.getString("type")) {
					
					/**
					 * A chat message has come down the pipe. We need to display it.
					 */
					case "chat":
						break;
					
					/**
					 * An asynchronous game update has occured. A player moved to your location, an opponent swung in combat, etc.
					 * 
					 * This also represents a reply to a request sent by the user. IE, they hit move location, an update will come down the
					 * socket describing the client update.
					 */
					case "update":
						break;
						
					
					/**
				    * Generate the given view.
					* User clicks on button -> blank windows appears with a loading sign -> window gets filled asynchronously when this returns
					* 
				    */
					case "view":
						
						//display an error if the view they requested wasnt the one that got loaded?

						break;
						
					
					//A server-side error has occured; display this back to the user in the form of a popup.
					case "error":
						String title = received.getString("title");
						String message = received.getString("message");
						String stackTrace = received.getString("stackTrace");
						
						displayError(title, message, stackTrace);
						
						break;
					}
					
					System.out.println(received.getString("message"));
				});
				
				JsonObject json = new JsonObject();
				json.put("action", "Signup");
				
				Buffer buffer = Json.encodeToBuffer(json);
				
				socket.write(buffer);
			}
			else {
				displayError("Connection Error", "Could not connect to the server");
			}
		});		
	}
	
	/**
	 * Generate an error popup for the user; this will not display any stack trace.
	 * @param title
	 * @param message
	 */
	public void displayError(String title, String message) {
		displayError(title, message, null);
	}
	
	/**
	 * Generate an error popup for the user.
	 * @param title
	 * @param message
	 * @param stackTrace
	 */
	public void displayError(String title, String message, String stackTrace) {
		
		Stage errorStage = new Stage();
		errorStage.setTitle(title);
		errorStage.focusedProperty().addListener((ov, onHidden, onShown) -> {
			if(false == errorStage.isFocused())
				Platform.runLater(() -> errorStage.close());
		});
		
		Label label = new Label(message);
		label.setMinHeight(50);
		label.setMinWidth(80);
		label.setStyle("-fx-background-color: red;");
		
		Button button = new Button("Ok!");
		button.setLayoutX(50);
		button.setLayoutY(50);
		button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			Platform.runLater(() -> errorStage.close());
		});
		
		Group group = new Group(label, button);
		
		//If a stack trace was specified, provide an option to display the full error.
		if(stackTrace != null) {
			Button stackTraceButton = new Button("Display the full error");
			stackTraceButton.setLayoutX(100);
			stackTraceButton.setLayoutY(50);
			
			stackTraceButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				Stage stackStage = new Stage();
				stackStage.setTitle("Stack Trace for " + title);
				
				//TODO format this nicely. I'm not yet sure how the stackTrace will get ported over TCP... so ill deal w this later
				
				stackStage.show();
			});
			
			group.getChildren().add(stackTraceButton);
		}
		
		Scene errorScene = new Scene(group, 300, 300);
		
		errorStage.setScene(errorScene);
		errorStage.show();
		errorStage.setAlwaysOnTop(true);
	}

	@Override
	public void start(Stage stage) throws Exception {		

		Circle circle = new Circle();
		
		circle.setCenterX(300);
		circle.setCenterY(135);
		circle.setRadius(30);
		circle.setFill(Color.BLUE);
		circle.setStrokeWidth(20);
		
		circle.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			
			displayError("bro", "whats up?", "I LOVE MYSELF!!!");
			
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
		
		mainStage = stage;
	}
}
