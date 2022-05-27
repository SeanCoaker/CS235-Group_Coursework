import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class creates the main window and runs the game.
 * Version History - version 1.0
 * Filename: Main.java
 * @author Joseph Semgalawe.
 * @version 1.0
 * @since 19-11-2019
 * copyright: No Copyright Purpose
 */
public class Main extends Application {

    //Variable used to close stage at the end of the game i.e when ESC is pressed
    private static Stage mainStage;

    /**
     * Starts up the main menu.
     * @param primaryStage - the primary stage to put the main menu up to
     * @throws Exception - an exception if Menu.fxml isn't found
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        primaryStage.setTitle("Window");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        setMainStage(primaryStage);
    }


    /**
     * Gets the main stage.
     * @return the main stage object
     */
    public static Stage getMainStage() {
        return mainStage;
    }


    /**
     * Sets the main stage to a new stage.
     * @param mainStage - the new mainStage object
     */
    public static void setMainStage(Stage mainStage) {
        Main.mainStage = mainStage;
    }

    /**
     * The main method that calls args.
     * @param args - the arguments passed through by the user
     */
    public static void main(String [] args){
        launch(args);
    }
}
