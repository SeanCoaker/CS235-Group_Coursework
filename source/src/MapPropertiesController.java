/**
 * @author Sean Coaker,
 */

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class MapPropertiesController {

    @FXML
    private TextField wSize;

    @FXML
    private TextField title;

    @FXML
    private Button back;


    @FXML
    private void okButton(ActionEvent event) {
        try {
            File dir = new File("GlobalFiles/CustomFiles/");
            File[] files = dir.listFiles();
            boolean duplicate = false;

            for (File elem : files) {
                if (elem.getName().equalsIgnoreCase(title.getText() + ".txt")) {
                    duplicate = true;
                }
            }

            if (duplicate) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("A file already exists with this name.");
                a.show();
            } else if (title.getText().equals("")) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Please enter a file name for your custom level.");
                a.show();
            } else {
                LevelEditorController.setTitle(title.getText());

                if (wSize.getText().isEmpty()) {
                    LevelEditorController.setWidth(10);
                    LevelEditorController.setHeight(10);
                } else {
                    LevelEditorController.setWidth(Integer.parseInt(wSize.getText()));
                    LevelEditorController.setHeight(Integer.parseInt(wSize.getText()));
                }

                Node node = (Node) event.getSource();
                Parent root = FXMLLoader.load(getClass().getResource("LevelEditor.fxml"));
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 700));
                stage.show();
            }
        } catch (IOException ex) {
            Logger.getLogger(LevelEditorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onButtonClicked(ActionEvent actionEvent) throws IOException {
        if (actionEvent.getSource().equals(back)) {
            Node node = (Node) actionEvent.getSource();
            Parent root = FXMLLoader.load(getClass().getResource("LevelEditorMenu.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

//    public void start(Stage primaryStage){
//        AnchorPane pane = new AnchorPane();
//        Scene scene =  new Scene(pane,800,600);
//        Button openLevelEditor = new Button("Custom Level");
//        pane.getChildren().add(openLevelEditor);
//        openLevelEditor.setAlignment(Pos.CENTER);
//        primaryStage.setScene(scene);
//        openLevelEditor.setOnAction(event -> {
//            try {
//                Parent root = FXMLLoader.load(getClass().getResource("MapProperties.fxml"));
//                primaryStage.setScene(new Scene(root,800,600));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        primaryStage.show();
//    }
//
//    public static void main(String [] args){
//        launch(args);
//    }
}
