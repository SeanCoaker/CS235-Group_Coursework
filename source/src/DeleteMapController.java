import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is responsible for deleting maps.
 * Version History - version 1.0
 * Filename: DeleteMapController.java
 * @author - Sean Coaker
 * @version - 1.0
 * @since 27-11-2019
 * copyright: No Copyright Purpose
 */
public class DeleteMapController implements Initializable {
    @FXML
    ListView createdMapList; //ListView of custom maps.

    @FXML
    Button backButton;

    @FXML
    Button deleteButton;

    private ObservableList<String> files = FXCollections.observableArrayList();//List that contains custom map filenames.

    /**
     * This method populates the list view with custom maps upon initialization of the DeleteMap window.
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        populateList();
    }

    /**
     * This method deals with button clicks. When the back button is clicked the user is taken back to the LevelEditorMenu.
     * Otherwise, when the deleted button is clicked, the selected custom map is deleted. It catches an error also, where
     * a map hasn't been selected to be deleted by the user.
     * @param e An action event of a button being clicked
     * @throws IOException
     */
    @FXML
    public void onButtonClicked(ActionEvent e) throws IOException {
        if (e.getSource().equals(backButton)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("LevelEditorMenu.fxml"));
            Stage stage = (Stage) (node.getScene().getWindow());
            stage.setTitle("Level Editor Menu");
            stage.setScene(new Scene(root));
            stage.show();
        } else if (e.getSource().equals(deleteButton)) {
            try {
                File fileToDelete = new File("GlobalFiles/CustomFiles/" +
                        createdMapList.getSelectionModel().getSelectedItem().toString());
                fileToDelete.delete();
                populateList();
            } catch (NullPointerException x) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Error! Please select a map to delete");
                a.show();
            }
        }
    }

    /**
     * This method populates the list view with a list of the custom maps.
     */
    public void populateList() {
        createdMapList.getItems().clear();

        File dir = new File("GlobalFiles/CustomFiles/");
        File[] listOfFiles = dir.listFiles();

        for (File f : listOfFiles) {
            files.add(f.toString().substring(24));
        }

        createdMapList.setItems(files);
    }
}
