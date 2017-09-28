package songlibrary.application;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import songlibrary.controller.SongLibraryController;

public class SongLib extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//Sets FXML Document that contains the View
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/songlibrary/view/SongLibraryView.fxml"));
		
		//Loads the View
		GridPane root = (GridPane)loader.load();
		
		//Loads the Controller
		SongLibraryController controller = loader.getController();
		controller.start(primaryStage);
		
		//Sets up the scene and assigns properties to the view
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);
		
		//Shows the view on screen
		primaryStage.show();
		
		//Saves to the song document when view is closed, then closes the view
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				try {
					controller.saveSongLibrary();
				}
				catch (IOException e) {
					throw new RuntimeException();
				}
				finally {
					primaryStage.close();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
