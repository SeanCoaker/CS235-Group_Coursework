import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


/**
 * Controller for selecting data from user save files.
 * Version History - version 1.0, 1.1, 1.2
 * Filename: UserProfileController.java
 * @author - Joseph Steven S, Marcin Kapcia, Chester D
 * @version - 1.2
 * @since 6-12-2019
 * copyright: No Copyright Purpose
 */
public class ChooseMapToEditController {

    //the home button
    @FXML
    Button home;
    //shows the list of possible levels
    @FXML
    ListView showListLevel;

    @FXML
    ListView showListCustomLevel;


    private ObservableList<String> customFiles = FXCollections.observableArrayList();//List that contains custom map filenames.
    private ObservableList<String> files = FXCollections.observableArrayList();
    /**
     * Reads profiles into arrayList.
     */
    public void initialize() {
        showListLevel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        showListCustomLevel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        populateCustomList();
        populateList();

    }

    /**
     * Registers if the home, update or play button are pressed, and sets up the
     * appropriate scenes if so.
     *
     * @param e - the most recent action by the user
     * @throws IOException - if the Menu.fxml or LoadFile.fxml files are missing
     */
    public void onButtonClicked(ActionEvent e) throws IOException {
        if (e.getSource().equals(home)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    /**
     * This method populates the custom list view with a list of the custom maps.
     */
    public void populateCustomList() {
        showListCustomLevel.getItems().clear();

        File dir = new File("GlobalFiles/CustomFiles/");
        File[] listOfFiles = dir.listFiles();

        for (File f : listOfFiles) {
            customFiles.add(f.toString().substring(24, f.toString().length() - 4));
        }

        showListCustomLevel.setItems(customFiles);
    }

    /**
     * This method populates the list view with a list of the custom maps.
     */
    public void populateList() {
        showListLevel.getItems().clear();

        File dir = new File("GlobalFiles/");
        File[] listOfFiles = dir.listFiles();

        for (File f : listOfFiles) {
            if(f.toString().substring(12).contains("level")) {
                files.add(f.toString().substring(12, f.toString().length() - 4));
            }
        }

        showListLevel.setItems(files);
    }

    /**
     * Method that handles what happens when an item is selected from listView
     * @param argo mouse event
     */
    public void mouseClickedLevel(MouseEvent argo) throws IOException {

        String profileLevel = (String) (showListLevel.getSelectionModel().getSelectedItem());

        final String FILE_LOCATION = "GlobalFiles/" + profileLevel + ".txt";

        LevelEditorController.setIsLoad(true, FILE_LOCATION);
        Node node = (Node) argo.getSource();
        Parent root = FXMLLoader.load(getClass().getResource("LevelEditor.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }


    public void mouseClickedCustomLevel(MouseEvent mouseEvent) throws IOException {
        String profileLevel = (String) (showListCustomLevel.getSelectionModel().getSelectedItem());
        System.out.println(profileLevel);
        final String FILE_LOCATION = "GlobalFiles/CustomFiles/"+ profileLevel + ".txt";
        LevelEditorController.setIsLoad(true, FILE_LOCATION);
        Node node = (Node) mouseEvent.getSource();
        Parent root = FXMLLoader.load(getClass().getResource("LevelEditor.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
}
