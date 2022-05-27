
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


/**
 * Controller for selecting data from user save files.
 * Version History - version 1.0, 1.1, 1.2
 * Filename: UserProfileController.java
 *
 * @author - Joseph Steven S, Marcin Kapcia, Chester D
 * @version - 1.2
 * @since 6-12-2019
 * copyright: No Copyright Purpose
 */
public class UserProfileController {

    //the home button
    @FXML
    Button home;
    //the play button
    @FXML
    Button play;
    //shows the list of user profiles
    @FXML
    ListView showList;
    //shows the list of possible levels
    @FXML
    ListView showListLevel;

    @FXML
    ListView showCustomListLevel;

    /**
     * Reads profiles into arrayList.
     */
    public void initialize() {
        FileReader.readProfiles();
        ArrayList<Profile> profile = FileReader.readProfiles();
        for (int i = 0; i < profile.size(); i++) {
            showList.getItems().add(profile.get(i).toString());

        }

        showList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        showListLevel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        showCustomListLevel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


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
     * Method that handles what happens when an item is selected from listView containing profiles
     */
    public void mouseClickedProfile() {
        showCustomListLevel.getItems().clear();
        showListLevel.getItems().clear();

        File path = new File("GlobalFiles\\CustomFiles\\");
        File[] customLevels = path.listFiles();
        assert customLevels != null;
        for (File file : customLevels) {
            if (file.isFile()) {
                showCustomListLevel.getItems().add(file.getName());
            }
        }

        String profileName = (String) (showList.getSelectionModel().getSelectedItem());
        Profile profile = null;
        for (Profile p : FileReader.readProfiles()) {
            if (p.getUserName().equals(profileName)) {
                profile = p;
            }
        }
        // showListLevel.getItems().setAll(profile.getHighestLevel() + 1);

        for (int x = 1; x < profile.getHighestLevel() + 1; x++) {

            showListLevel.getItems().add("level" + x);
        }

        File playerDir = new File("PlayerFolders/Player" + profile.getUserName());
        for (File save : playerDir.listFiles()) {
            showListLevel.getItems().add(save.getName());
        }


        GameController.setUserProfile(profile);


    }

    /**
     * Method that handles what happens when an item is selected from listView
     *
     * @param argo mouse event
     */
    public void mouseClickedLevel(MouseEvent argo) {
        String profileLevel = (String) (showListLevel.getSelectionModel().getSelectedItem());
        String profileName = (String) (showList.getSelectionModel().getSelectedItem());
        Node node = (Node) argo.getSource();
        MapController map = new MapController();
        Parent root = map.buildGUI();
        Scene scene = new Scene(root);
        if(profileLevel.contains("Save")) {
            GameController.loadFile("PlayerFolders/Player" + profileName + "/" + profileLevel);
            GameController.setIsCustom(true);
        } else {
            GameController.loadFile("GlobalFiles\\" + profileLevel + ".txt");
            GameController.setIsCustom(false);
        }


        scene.addEventFilter(KeyEvent.KEY_PRESSED, map::processKeyEvent);
        map.drawGame();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method that handles what happens when an item is selected from listView
     *
     * @param argo mouse event
     */
    public void mouseClickedCustomLevel(MouseEvent argo) {
        String profileLevel = (String) (showCustomListLevel.getSelectionModel().getSelectedItem());
        Node node = (Node) argo.getSource();
        MapController map = new MapController();
        Parent root = map.buildGUI();
        Scene scene = new Scene(root);
        GameController.loadCustomFile("GlobalFiles\\CustomFiles\\" + profileLevel);
        GameController.setIsCustom(true);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, map::processKeyEvent);
        map.drawGame();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


}
