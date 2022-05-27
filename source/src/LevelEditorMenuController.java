import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Controller for the selection of different
 * button events. Loading different stages of 
 * map editor.
 * Version History - version 1.0
 * Filename: LevelEditorMenuController.java
 * @author - Marcin Kapcia, Joseph Steven S, Sean Coaker
 * @version - 1.0
 * @since 27-11-2019
 * copyright: No Copyright Purpose
 */
public class LevelEditorMenuController {
    // Creatig the map button
    @FXML
    Button createButton;
    // Edit existing map button
    @FXML
    Button editButton;
    // Delete a edited map
    @FXML
    Button deleteButton;
    // Goes back to the home menu button
    @FXML
    Button backButton;

    /**
     * Method that allows to navigate to different stages on a button event.
     * @param e - the most recent action by the use.
     * @throws IOException handles IO exceptions. 
    */
    @FXML
    public void onButtonClicked(ActionEvent e) throws IOException {
        if (e.getSource().equals(createButton)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("MapProperties.fxml"));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle("Level Editor");
            stage.setScene(new Scene(root));
            stage.show();
        } else if (e.getSource().equals(editButton)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("ChooseMapToEdit.fxml"));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle("Edit Map");
            stage.setScene(new Scene(root));
            stage.show();
        } else if (e.getSource().equals(deleteButton)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("DeleteMap.fxml"));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle("Delete Map");
            stage.setScene(new Scene(root, 390, 380));
            stage.show();
        } else if (e.getSource().equals(backButton)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle("Menu");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
}
