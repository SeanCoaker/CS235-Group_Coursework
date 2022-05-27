import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;



/**
 * Controller for loading the files
 * Version History - version 1.0
 * Filename: loadController.java
 * @author Joseph S, Marcin K
 * @version 1.0
 * @since 30-11-2019
 * copyright: No Copyright Purpose
 **/
 
public class LoadController {

	@FXML
	private Button home;
	@FXML
	private Button loadFile;
	@FXML
	private ListView loadFileDisplay;
	@FXML
	private Button playGame;

	/**
	 * Method that handles button clicks to load different scenes.
	 *
	 * @param event the button that was clicked.
	 * @throws IOException handles IO exceptions.
	 */
	public void onButtonClicked(ActionEvent event) throws IOException {
		if (event.getSource().equals(home)) {
			Node node = (Node) event.getSource();
			Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
			Scene scene = new Scene(root);
			Stage stage = (Stage) node.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		}
		if (event.getSource().equals(loadFile)) {
			FileChooser file = new FileChooser();
			//Set Directory where we get files.
			file.setInitialDirectory(new File("./src"));
			file.getExtensionFilters().addAll(new ExtensionFilter("txt Files", "*.txt"));
			File selectedFile = file.showOpenDialog(null);
			
			if(selectedFile != null) {
				loadFileDisplay.getItems().add(selectedFile.getAbsolutePath());
				
			}else {
				System.out.print("System not allowed");
			}
		}
		
		if (event.getSource().equals(playGame)) {
			Node node = (Node) event.getSource();
			MapController map = new MapController();
			Parent root = map.buildGUI();
			Scene scene = new Scene(root);
			GameController.loadFile((String)loadFileDisplay.getItems().get(0));
			scene.addEventFilter(KeyEvent.KEY_PRESSED, map::processKeyEvent);
			map.drawGame();
			Stage stage = (Stage) node.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		}

	}
}
