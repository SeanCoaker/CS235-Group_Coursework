import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * This class handles actions for the buttons in the main menu scene.
 * Version History - version 1.0
 * Filename: MenuController.java
 * @author - Joseph Steven S, Marcin Kapcia
 * @version - 1.0
 * @since 27-11-2019
 * copyright: No Copyright Purpose
 */
public class MenuController {
    @FXML
    Button startButton;

    @FXML
    Button load;

    @FXML
    Button leaderBoardButton;

    @FXML
    Button messageButton;

    @FXML
    Button createLevelButton;

    @FXML
    Label outputTextDown;

    @FXML
    Label outputTextUp;


    /**
     * reads data from profiles and leaderboards.
     *
     */
     public void initialize() {
         GameController.setAllProfiles();
         FileReader.setupFileNames();
         FileReader.readLeaderboard();
     }

    /**
     * Method that handles button clicks to load different scenes.
     *
     * @param e the button that was clicked.
     * @throws IOException handles IO exceptions.
     */
@FXML
    public void onButtonClicked(ActionEvent e) throws IOException {
        if(e.getSource().equals(startButton)){
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("NewGame.fxml"));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle("Start");
            stage.setScene(new Scene(root));
            stage.show();
        }else if (e.getSource().equals(load)) {
        Node node = (Node) e.getSource();
        Parent root = FXMLLoader.load(getClass().getResource("UserProfile.fxml"));
        Stage stage = (Stage) (node.getScene().getWindow());
        stage.setTitle("load");
        stage.setScene(new Scene(root,900,400));
        stage.show();
       }else if (e.getSource().equals(leaderBoardButton)) {
        Node node = (Node) e.getSource();
        Parent root = FXMLLoader.load(getClass().getResource("leaderboard.fxml"));
        Stage stage = (Stage) (node.getScene().getWindow());
        stage.setTitle("LeaderBoard");
        stage.setScene(new Scene(root));
        stage.show();
       }else if (e.getSource().equals(messageButton)) {

        MessageOfTheDay msg = new MessageOfTheDay();

        outputTextDown.setText(msg.getMessage().toString());
        outputTextUp.setText(msg.getMessage().toString());
       } else if (e.getSource().equals(createLevelButton)) {
            Node node = (Node) e.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("LevelEditorMenu.fxml"));
            Stage stage = (Stage) (node.getScene().getWindow());
            stage.setTitle("Level Editor Menu");
            stage.setScene(new Scene(root));
            stage.show();
    }

}

}
