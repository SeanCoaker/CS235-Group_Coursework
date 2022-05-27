import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * This class provides the visuals and buttons of the leaderboard.
 * Version History - version 1.0
 * Filename: leaderboardController.java
 * @author - Joseph Steven S, Marcin Kapcia, Chester D
 * @version - 1.0
 * @since 30-11-2019
 * copyright: No Copyright Purpose
 */

public class LeaderboardController {

	//the button to confirm data entered
	public Button DialogButton;
	//the buttom to go back home
	@FXML
	private Button home;
	//the output text for the leaderboard
	@FXML
	private TextArea leaderBoardOutput;

	/**
	 * Registers the button clicks for the home and dialog buttons.
	 * @param event - the most recent action by the user
	 * @throws IOException - if Menu.fxml is missing
	 */
	public void onButtonClicked(ActionEvent event) throws IOException{
		//if the home button is pressed, take them back to the start menu
		if(event.getSource().equals(this.home)){
			Node node = (Node) event.getSource();
			Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
		    Scene scene = new Scene(root);
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
		//if the dialog button is pressed, it lets them select a level outputs the appropriate leaderboard data
		}if(event.getSource().equals(this.DialogButton)){
			int num = 0;
			TextInputDialog dialog = new TextInputDialog("Value: 0, Enter Level (=>) 1");
			dialog.setTitle("Level Selector");
			dialog.setHeaderText("Get Leaderboard by Level");
			dialog.setContentText("LEVEL #: ");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				num = Integer.parseInt(result.get());
			}
			dialog.close();
			String output = "";
			String def = "Empty - no record to show";
			try{
				output = Leaderboard.toString(num);
				leaderBoardOutput.clear();
				if(output.equals("")){
					leaderBoardOutput.setText(def);
				}else{
					leaderBoardOutput.setText(output);
				}
			}catch (NullPointerException e){
				System.out.println(num);
				leaderBoardOutput.clear();
				leaderBoardOutput.setText(def);
				e.printStackTrace();
				System.exit(0);
			}

		}
		}
	}
