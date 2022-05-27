import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.floor;
/**
 * This class is responsible for creating and editing levels.
 * Version History - version 1.0
 * Filename: LevelEditorController.java
 * @author - Joseph S, Marcin K, Petko K, Chester D, Sean C, Ben P, Khaled A, Hossein M
 * @version - 1.0
 * @since 27-11-2019
 * copyright: No Copyright Purpose
 */
public class LevelEditorController {

    @FXML
    public Button btnSave;

    @FXML
    public Button btnBack;

    @FXML
    public BorderPane boarderPane;

    @FXML
    public GridPane gridPaneMap;

    @FXML
    public ImageView selectedImage;

    @FXML
    public Button btnClear;

    @FXML
    public Button btnToken1;

    @FXML
    public Button btnToken2;

    @FXML
    public Button btnToken3;

    @FXML
    public Button btnToken4;

    @FXML
    public Button btnToken5;

    @FXML
    public Button btnToken6;

    @FXML
    public Button btnToken7;

    @FXML
    public Button btnToken8;

    @FXML
    public Button btnToken9;

    @FXML
    public Button btnLineEnemyHorizontal;

    @FXML
    public Button btnLineEnemyVertical;

    @FXML
    public HBox hboxLineEnemyDirection;

    @FXML
    public HBox hboxTokens;

    @FXML
    private HBox collectablesBox;

    @FXML
    private HBox doorsBox;

    @FXML
    private HBox environmentBox;

    @FXML
    private HBox moveableEntitiesBox;

    @FXML
    private Label titleBox;

    @FXML
    public Slider mySlider;

    @FXML
    ScrollPane scrollPaneMap;


    private static String title; // the title of level 
    private static int width; //
    private static int height; // 
    private static boolean isLoad; // checks if file is loaded or not
    private static String FILE_LOCATION; // defines the file location
    private double teleporterCount = 0; // to be count the teleporter
    private String LineEnemyDirection = "No direction"; // name cell to be placed
    private int[] playerPosition = {-1, -1}; // the player position

    private boolean isDraggable = false; //ADDED THIS TO PREVENT CERTAIN IMAGE TO BE DRAGGED

    public static int[] player; // number of players

    public static  Object[][] cell; // a 2d array of the map, holding each cell as their object


    private String[][] mapObjects; // a 2d array of the map, holding each map object as their object
    private ArrayList<ArrayList<SavedImage>> categories = new ArrayList<>(); // array list of saved image files. 
    private ArrayList<SavedImage> collectables; // array list of collectables.
    private ArrayList<SavedImage> doors; // array list of doors.
    private ArrayList<SavedImage> environment; // array list of environments.
    private ArrayList<SavedImage> moveableEntities; // array list of moveable entities.
    private ArrayList<SavedImage> images = new ArrayList<>(); // array list of images.


    private SavedImage[][] gridObjects = new SavedImage[height][width]; //A 2d array of the map, holding each width and height as their grid object


    private HashMap<String, Integer> possibleEnemyDirections = new HashMap<>(); //Stores 0 for directions if a wall isn't in the way and a 1 if there is a wall in the way.
    private int wallCount = 0; //wall count next to an enemy.
    private String[][] enemyDirections = new String[height][width]; //A 2d array storing the starting directions of enemies at their location on the map.

    //Public methods

    /**
     * Sets up the entire UI of the editor, building up a new grid or loading the one requested appropriately.
     */
    @FXML
    void initialize() {
        Clipboard cb = Clipboard.getSystemClipboard();
        cb.setContent(null);
        start();
        //test();

        if (!isLoad) {
            buildGrid(width, height);
        } else {
            loadBuild(LevelEditorController.FILE_LOCATION);
        }

        imageSelected();
        setTitleBox(title);

        imageButtonPressed(collectablesBox);
        imageButtonPressed(doorsBox);
        imageButtonPressed(environmentBox);
        imageButtonPressed(moveableEntitiesBox);

        mapObjects = new String[width][height];
        
    }

    /**
     * Method which allows to zoom in/out of the gridpane.
     */
    public void sliderClicked() {

        mySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            scrollPaneMap.setContent(gridPaneMap);

            gridPaneMap.scaleXProperty().bind(mySlider.valueProperty());
            gridPaneMap.scaleYProperty().bind(mySlider.valueProperty());

            scrollPaneMap.prefWidthProperty().bind(mySlider.valueProperty().multiply(gridPaneMap.getWidth()));
            scrollPaneMap.prefHeightProperty().bind(mySlider.valueProperty().multiply(gridPaneMap.getHeight()));

        });
    }

    /**
     * Method which updates the 2d array grid objects to allow checks to occur (e.g. if you can place a wall enemy
     * if there is a wall or no wall there)
     */
    public void updateGridObjects() {
        gridObjects = new SavedImage[height][width];
        ObservableList<Node> cellContent = gridPaneMap.getChildrenUnmodifiable();
        for (Node elem : cellContent) {
            if (elem instanceof Group) {
            } else {
                int row = GridPane.getRowIndex(elem);
                int col = GridPane.getColumnIndex(elem);
                ImageView view = (ImageView) elem;
                SavedImage img = (SavedImage) view.getImage();
                gridObjects[row][col] = img;
            }
        }
    }

    /**
     * This method checks how many walls are next to the location where the user has chosen to place a wall enemy.
     * It then updates wall count to say how many walls are the near the enemy. If a wall is above, below, to the left
     * or to the right of the enemy then a 1 is added to the corresponding direction in the hash map possibleDirections.
     * @param yCoord The y coordinate of the enemy's starting position.
     * @param xCoord The x coordinate of the enemy's starting position.
     */
    public void checkEnemyPlacement(int yCoord, int xCoord) {
        //Updates the gridObjects array to read what tiles are around the wall enemy.
        updateGridObjects();
        //Checks the tiles above and below the enemy and what type of tile they are, updating variables accordingly.
        for (int y = -1; y < 2; y++) {
            if (gridObjects[yCoord + y][xCoord].getName().equalsIgnoreCase("Wall.png")) {
                wallCount++;
                switch (y) {
                    case -1:
                        possibleEnemyDirections.put("up", 1);
                        break;
                    case 1:
                        possibleEnemyDirections.put("down", 1);
                        break;
                }
            }
            y++;
        }

        //Checks the tiles to the left and to the right of the enemy and what type of tile they are, updating variables accordingly.
        for (int x = -1; x < 2; x++) {
            if (gridObjects[yCoord][xCoord + x].getName().equalsIgnoreCase("Wall.png")) {
                wallCount++;
                switch (x) {
                    case -1:
                        possibleEnemyDirections.put("left", 1);
                        break;
                    case 1:
                        possibleEnemyDirections.put("right", 1);
                        break;
                }
            }
            x++;
        }
        System.out.println(wallCount);
    }

    /**
     * This method resets the hashmap so that the wall enemy can move in any possible direction before it's placed on
     * the map.
     */
    public void resetPossibleDirections() {
        possibleEnemyDirections.put("up", 0);
        possibleEnemyDirections.put("down", 0);
        possibleEnemyDirections.put("left", 0);
        possibleEnemyDirections.put("right", 0);
    }

    /**
     * Loads up a custom level file into the editor.
     * @param FILE_LOCATION - the string path of the file to edit
     */
    public void loadBuild(String FILE_LOCATION) {
        String title = null;
        if(FILE_LOCATION.length() < 23){
            title = "custom" + FILE_LOCATION.replace("GlobalFiles/", "").replace(".txt", "");
        } else{
            title = "custom" + FILE_LOCATION.replace("GlobalFiles/CustomFiles/", "").replace(".txt", "");

        }
        setTitle(title);
        GameController.loadFile(FILE_LOCATION);
        Object[][] levelMap = GameController.getLevelMap();
        String imageLocation;
        File file;
        int width = GameController.getMapWidth();
        int height = GameController.getMapHeight();

        enemyDirections = new String[height][width];
        setEnemyDirections(height, FILE_LOCATION);

        LevelEditorController.setWidth(width);
        LevelEditorController.setHeight(height);

        this.gridPaneMap.getColumnConstraints().clear();
        this.gridPaneMap.getRowConstraints().clear();

        for (int i = 0; i < width; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setMinWidth(5);
            colConst.setPrefWidth(50);
            this.gridPaneMap.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < height; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(5);
            rowConst.setPrefHeight(50);
            this.gridPaneMap.getRowConstraints().add(rowConst);
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                int childNumber = row * width + col;
                if (levelMap[row][col] instanceof StraightLineEnemy) {
                    StraightLineEnemy cell = (StraightLineEnemy) levelMap[row][col];
                    loadCell(cell, col, row);
                    if (cell.getDirection() == 1) {
                        gridPaneMap.getChildren().get(childNumber).setId("VerticalLineEnemy");
                    } else if (cell.getDirection() == 0) {
                        gridPaneMap.getChildren().get(childNumber).setId("HorizontalLineEnemy");
                    }

                } else if (levelMap[row][col] instanceof Enemy) {
                    Enemy cell = (Enemy) levelMap[row][col];
                    loadCell(cell, col, row);

                } else if (levelMap[row][col] instanceof EnvironmentCell) {
                    EnvironmentCell cell = (EnvironmentCell) levelMap[row][col];
                    loadCell(cell, col, row);

                } else if (levelMap[row][col] instanceof Player) {
                    Player cell = (Player) levelMap[row][col];
                    loadCell(cell, col, row);
                    playerPosition[0] = col;
                    playerPosition[1] = row;
                } else if (levelMap[row][col] instanceof CollectableCell) {
                    CollectableCell cell = (CollectableCell) levelMap[row][col];
                    loadCell(cell, col, row);
                } else if (levelMap[row][col] instanceof Teleporter) {
                    Teleporter cell = (Teleporter) levelMap[row][col];
                    loadCell(cell, col, row);
                    gridPaneMap.getChildren().get(childNumber).setId(String.valueOf(cell.getLinkNo()));
                    if (cell.getLinkNo() >= teleporterCount) {
                        teleporterCount = cell.getLinkNo() + 1;
                    }
                }
            }
        }
    }

    /**
     * Sets if the file is loaded or not, and the file loaded if so.
     * @param isLoad - a boolean stating if it's loaded or not
     * @param FILE_LOCATION - the location of the loaded file
     */
    public static void setIsLoad(boolean isLoad, String FILE_LOCATION) {
        LevelEditorController.isLoad = isLoad;
        LevelEditorController.FILE_LOCATION = FILE_LOCATION;
    }

    /**
     * This method updates the enemyDirections 2D array with the starting directions of each enemy in the map we're
     * editing.
     * @param height The height of the map being edited, used to reach the end of the file where enemy data is stored.
     * @param filepath The path of the file which holds the data of the custom map.
     */
    public void setEnemyDirections(int height, String filepath) {
        File file = new File(filepath);
        Scanner in = null;

        try {
            in = new Scanner(file);
            while (in.hasNext()) {
                in.nextLine();
                in.nextLine();

                for (int i = 0; i < height; i++) {
                    in.nextLine();
                }

                in.useDelimiter(";");
                while (in.hasNext()) {
                    String enemyData = in.next();
                    int index = 1;

                    while (!Character.isDigit(enemyData.charAt(index))) {
                        index++;
                    }

                    String xCoordString = enemyData.charAt(index) + "";

                    while (Character.isDigit(enemyData.charAt(index + 1))) {
                        xCoordString = xCoordString + enemyData.charAt(index + 1);
                        index++;
                    }

                    String yCoordString = "" + enemyData.charAt(index + 2);
                    index = index + 2;

                    while (Character.isDigit(enemyData.charAt(index + 1))) {
                        yCoordString = yCoordString + enemyData.charAt(index + 1);
                        index++;
                    }

                    int direction = Integer.parseInt(enemyData.charAt(enemyData.length() - 1) + "");

                    int xCoord;
                    int yCoord;

                    if (xCoordString.length() > 1) {
                        xCoord = Integer.parseInt(xCoordString);
                    } else {
                        xCoord = Integer.parseInt("" + xCoordString.charAt(0));
                    }

                    if (yCoordString.length() > 1) {
                        yCoord = Integer.parseInt(yCoordString);
                    } else {
                        yCoord = Integer.parseInt("" + yCoordString.charAt(0));
                    }

                    switch (direction) {
                        case 0:
                            enemyDirections[yCoord][xCoord] = "left";
                            break;
                        case 1:
                            enemyDirections[yCoord][xCoord] = "up";
                            break;
                        case 2:
                            enemyDirections[yCoord][xCoord] = "right";
                            break;
                        case 3:
                            enemyDirections[yCoord][xCoord] = "down";
                            break;
                    }

                }
            }
            in.close();

        } catch (IOException e) {
            System.out.println("File could not be found.");
        }
    }

    //Getters and setters

    /**
     * This method displays a window with a combo box so that the user can select a legal starting direction for the enemy.
     * It makes sure the user doesn't choose a direction which moves the enemy away from the wall. However it does allow
     * the user to choose a starting direction where the enemy can move into a tunnel in between 2 walls. The method
     * also makes sure that the user chooses a legal direction and shows an error alert otherwise.
     * @param yCoord y coordinate of the wall enemy.
     * @param xCoord x coordinate of the wall enemy.
     * @param board The current map the user is editing.
     * @param target The location on the map where the wall enemy will be placed.
     * @param trgImage The image at the location on the map where the wall enemy will placed.
     * @param piece ImageView at the location on the map where the wall enemy will be placed.
     */
    public void setDirection(int yCoord, int xCoord, GridPane board, Node target, SavedImage trgImage, ImageView piece) {
        ComboBox<String> directionDropDown = new ComboBox<>();
        ObservableList<String> directionList = FXCollections.observableArrayList();
        directionList.addAll("Up", "Down", "Left", "Right");
        directionDropDown.setItems(directionList);

        Button submitButton = new Button("Submit");

        FlowPane root = new FlowPane();
        root.getChildren().addAll(directionDropDown, submitButton);
        Scene scene = new Scene(root);
        Stage secondStage = new Stage();
        secondStage.setScene(scene);
        secondStage.show();

        submitButton.setOnAction(event -> {
            String direction = directionDropDown.getSelectionModel().getSelectedItem();
            Boolean directionSet = false;
            String wall = "Wall.png";
            switch (direction) {
                case "Up":
                    if (possibleEnemyDirections.get("up") == 0) {
                        if (wallCount == 1) {
                            if (!gridObjects[yCoord - 1][xCoord - 1].getName().equalsIgnoreCase(wall) &&
                                    !gridObjects[yCoord - 1][xCoord + 1].getName().equalsIgnoreCase(wall)) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Enemy can't start in this direction");
                                a.show();
                                setDirection(yCoord, xCoord, board, target, trgImage, piece);
                            } else {
                                enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                directionSet = true;
                                updateGridObjects();
                            }
                        } else {
                            enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                            System.out.println("REPLACING TARGET = " + trgImage.getName());
                            board.getChildren().remove(target);
                            board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                            dragAndDrop(piece, board);
                            directionSet = true;
                            updateGridObjects();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Enemy can't start in this direction, there's a wall in the way");
                        a.show();
                        setDirection(yCoord, xCoord, board, target, trgImage, piece);
                    }
                    break;
                case "Down":
                    if (possibleEnemyDirections.get("down") == 0) {
                        if (wallCount == 1) {
                            if (!gridObjects[yCoord + 1][xCoord - 1].getName().equalsIgnoreCase(wall) &&
                                    !gridObjects[yCoord + 1][xCoord + 1].getName().equalsIgnoreCase(wall)) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Enemy can't start in this direction");
                                a.show();
                                setDirection(yCoord, xCoord, board, target, trgImage, piece);
                            } else {
                                enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                directionSet = true;
                                updateGridObjects();
                            }
                        } else {
                            enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                            System.out.println("REPLACING TARGET = " + trgImage.getName());
                            board.getChildren().remove(target);
                            board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                            dragAndDrop(piece, board);
                            directionSet = true;
                            updateGridObjects();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Enemy can't start in this direction, there's a wall in the way");
                        a.show();
                        setDirection(yCoord, xCoord, board, target, trgImage, piece);
                    }
                    break;
                case "Left":
                    if (possibleEnemyDirections.get("left") == 0) {
                        if (wallCount == 1) {
                            if (!gridObjects[yCoord + 1][xCoord - 1].getName().equalsIgnoreCase(wall) &&
                                    !gridObjects[yCoord - 1][xCoord - 1].getName().equalsIgnoreCase(wall)) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Enemy can't start in this direction");
                                a.show();
                                setDirection(yCoord, xCoord, board, target, trgImage, piece);
                            } else {
                                enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                directionSet = true;
                                updateGridObjects();
                            }
                        } else {
                            enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                            System.out.println("REPLACING TARGET = " + trgImage.getName());
                            board.getChildren().remove(target);
                            board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                            dragAndDrop(piece, board);
                            directionSet = true;
                            updateGridObjects();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Enemy can't start in this direction, there's a wall in the way");
                        a.show();
                        setDirection(yCoord, xCoord, board, target, trgImage, piece);
                    }
                    break;
                case "Right":
                    if (possibleEnemyDirections.get("right") == 0) {
                        if (wallCount == 1) {
                            if (!gridObjects[yCoord + 1][xCoord + 1].getName().equalsIgnoreCase(wall) &&
                                    !gridObjects[yCoord - 1][xCoord + 1].getName().equalsIgnoreCase(wall)) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Enemy can't start in this direction");
                                a.show();
                                setDirection(yCoord, xCoord, board, target, trgImage, piece);
                            } else {
                                enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                directionSet = true;
                                updateGridObjects();
                            }
                        } else {
                            enemyDirections[yCoord][xCoord] = direction.toLowerCase();
                            System.out.println("REPLACING TARGET = " + trgImage.getName());
                            board.getChildren().remove(target);
                            board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                            dragAndDrop(piece, board);
                            directionSet = true;
                            updateGridObjects();
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Enemy can't start in this direction, there's a wall in the way");
                        a.show();
                        setDirection(yCoord, xCoord, board, target, trgImage, piece);
                    }
                    break;
            }

            if (directionSet) {
                resetPossibleDirections();
                wallCount = 0;
            }
            secondStage.close();
        });
    }

    /**
     * Sets the stored title of the map.
     * @param title - the new name for the map
     */
    public static void setTitle(String title) {
        LevelEditorController.title = title;
    }

    /**
     * Sets the text of the title box that shows the maps name.
     * @param title - the map name
     */
    private void setTitleBox(String title) {
        titleBox.setText(title);
    }

    /**
     * Sets the width of the map.
     * @param width - the new map width
     */
    public static void setWidth(int width) {
        LevelEditorController.width = width;
    }

    /**
     * Sets the height of the map.
     * @param height - the new height's map
     */
    public static void setHeight(int height) {
        LevelEditorController.height = height;
    }

    /**
     * Sets the location of the player object.
     * @param location - the x and y coordinates of the player
     */
    public static void setPlayer(int[] location){
        LevelEditorController.player = location;
    }

    /**
     * Sets the map of objects.
     * @param cell - the new map contents
     */
    public static void setCell(Object[][] cell) {
        LevelEditorController.cell = cell;
    }

    /**
     * Places all the images on the left hand side of the screen into a list.
     * @param hbox - the HBox currently holding all the UI images
     * @return the list holding all the images as ImageView's
     */
    public List<ImageView> getHboxChildren(HBox hbox) {
        ObservableList list = FXCollections.observableList(hbox.getChildrenUnmodifiable());
        List<ImageView> imageViews = list;
        return imageViews;
    }


    //Private methods

    /**
     * Sets up the images on the left hand side to act as buttons, and sets what they do upon being clicked.
     * @param groupBox - the HBox holding all the images
     */
    private void imageButtonPressed(HBox groupBox) {
        for (ImageView elem : getHboxChildren(groupBox)) {
            dragAndDrop(elem, gridPaneMap);

            elem.setOnMouseClicked(event -> {
                Clipboard cb = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                SavedImage savedImage = (SavedImage) elem.getImage();
                content.putImage(savedImage);
                List<File> file = new ArrayList<>();
                file.add(new File("src\\Images\\" + savedImage.getFolderPath()));
                content.putFiles(file);
                cb.setContent(content);
                this.selectedImage.setImage(elem.getImage());
                System.out.println(cb.toString());

                if ("doorsBox".equals(groupBox.getId()) && groupBox.getChildren().get(3) == elem) {
                    LineEnemyDirection = "No direction";
                    showTokenDoorButtons();
                    //cb.setContent(null);
                } else if ("environmentBox".equals(groupBox.getId()) && groupBox.getChildren().get(3) == elem) {
                    LineEnemyDirection = "No direction";
                    isDraggable = false; //ADDED THIS TO PREVENT CERTAIN IMAGE TO BE DRAGGED
                    hideTokensDoorButtons();
                    hideLineEnemyDirectionButtons();
                } else if ("moveableEntitiesBox".equals(groupBox.getId()) && groupBox.getChildren().get(2) == elem) {
                    LineEnemyDirection = "No direction";
                    showLineEnemyDirectionButtons();
                    isDraggable = false;
                } else if ("moveableEntitiesBox".equals(groupBox.getId()) && (groupBox.getChildren().get(0) == elem
                        //ADDED THIS TO PREVENT CERTAIN IMAGE TO BE DRAGGED
                        || groupBox.getChildren().get(1) == elem
                        || groupBox.getChildren().get(3) == elem
                        || groupBox.getChildren().get(4) == elem
                        || groupBox.getChildren().get(5) == elem)) {
                    LineEnemyDirection = "No direction";
                    isDraggable = false;
                    hideTokensDoorButtons();
                    hideLineEnemyDirectionButtons();
                } else {
                    LineEnemyDirection = "No direction";
                    isDraggable = true; //ADDED THIS TO PREVENT CERTAIN IMAGE TO BE DRAGGED
                    hideTokensDoorButtons();
                    hideLineEnemyDirectionButtons();
                }
            });


        }
    }

    /**
     * Reads all files within a folder, and adds them to an ArrayList.
     * @param folder - the folder to search within
     * @return an ArrayList holding all the SavedImage objects of the folder's contents
     */
    @SuppressWarnings("ALL")
    private ArrayList<SavedImage> readFile(String folder) {
        ArrayList<SavedImage> imageCollection;
        try {
            File path = new File("src\\Images\\" + folder);
            imageCollection = new ArrayList();
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isFile()) { //this line weeds out other directories/folders
                    imageCollection.add(readImageFile(file));
                    //allCells.add(readImageFile(file));
                }
            }
            return imageCollection;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads all the images in subfolders within the Images folder.
     */
    @SuppressWarnings("ALL")
    private void readFolders() {
        try {
            File path = new File("src\\Images");
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) { //this line weeds out other directories/folders
                    switch (file.getName()) {
                        case "collectables":
                            collectables = readFile(file.getName());
                            categories.add(collectables);
                            break;
                        case "doors":
                            doors = readFile(file.getName());
                            categories.add(doors);
                            break;
                        case "environment":
                            environment = readFile(file.getName());
                            categories.add(environment);
                            break;
                        case "moveableEntities":
                            moveableEntities = readFile(file.getName());
                            categories.add(moveableEntities);
                            break;
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a SavedImage object from an image's file
     * @param file - the image file
     * @return a SavedImage object containing the image and it's filename
     */
    private SavedImage readImageFile(File file) {
        SavedImage image = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));
            image = new SavedImage(is, file);
            images.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * For every category, resizes each objects image and places it along the left hand side of the screen.
     */
    private void resizeImage() {
        for (SavedImage img : collectables) {
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            collectablesBox.getChildren().add(imageView);
            collectablesBox.setSpacing(10);
        }
        for (SavedImage img : doors) {
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            doorsBox.getChildren().add(imageView);
            doorsBox.setSpacing(10);
        }
        for (SavedImage img : environment) {
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            environmentBox.getChildren().add(imageView);
            environmentBox.setSpacing(10);
        }
        for (SavedImage img : moveableEntities) {
            ImageView imageView = new ImageView();
            imageView.setImage(img);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            moveableEntitiesBox.getChildren().add(imageView);
            moveableEntitiesBox.setSpacing(10);
        }
    }

    /**
     * Reads the folders and loads their images onto the side.
     */
    private void start() {
        readFolders();
        resizeImage();
    }

    /**
     * If the back button is pressed, it sends them back to the level editor menu.
     * @param event - the most recent input event from the user
     */
    @FXML
    private void btnSizeClicked(ActionEvent event) {
        if (event.getSource().equals(btnBack)) {
            try {
                LevelEditorController.isLoad = false;
                System.out.println("clicked = " + btnBack.getText());
                Node node = (Node) event.getSource();
                Parent root = FXMLLoader.load(getClass().getResource("LevelEditorMenu.fxml"));
                Scene scene = new Scene(root);
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(LevelEditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Builds the default layout of the custom map when created.
     * @param width - the width of the custom map created.
     * @param height - the height of the custom map created.
     */
    private void buildGrid(int width, int height) {
        this.gridPaneMap.getColumnConstraints().clear();
        this.gridPaneMap.getRowConstraints().clear();
        for (int i = 0; i < width; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setMinWidth(5);
            colConst.setPrefWidth(50);
            this.gridPaneMap.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < height; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(5);
            rowConst.setPrefHeight(50);
            this.gridPaneMap.getRowConstraints().add(rowConst);
        }

        File fileGR = new File("src/Images/environment/Ground.png");
        File fileWall = new File("src/Images/environment/Wall.png");
        SavedImage imageGR = new SavedImage(fileGR.toURI().toString(), fileGR);
        SavedImage imageWall = new SavedImage(fileWall.toURI().toString(), fileWall);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (row == 0 || column == 0 || row == (height - 1) || column == (width - 1)) {
                    ImageView destination = new ImageView();
                    images.add(imageWall);
                    destination.setFitHeight(50);
                    destination.setFitWidth(50);
                    destination.setImage(imageWall);
                    destination.setDisable(true);
                    this.gridPaneMap.add(destination, column, row);
                } else {
                    ImageView destination = new ImageView();
                    images.add(imageGR);
                    destination.setFitHeight(50);
                    destination.setFitWidth(50);
                    destination.setImage(imageGR);
                    this.gridPaneMap.add(destination, column, row);
                }
            }
        }
    }

    /**
     * Sorts the object images into their four categories.
     */
    private void imageSelected() {
        ArrayList<ImageView> imageView = new ArrayList<>();
        imageView.addAll((getHboxChildren(collectablesBox)));
        imageView.addAll((getHboxChildren(doorsBox)));
        imageView.addAll((getHboxChildren(environmentBox)));
        imageView.addAll((getHboxChildren(moveableEntitiesBox)));
    }


    /**
     * This method deals with the drag and drop and click events.
     * @param image - this is the imageview that the user can choose to drag or click (different icons in the game)
     * @param board - this is the gridpane that the the images will be dragged or clicked to
     */
    private void dragAndDrop(ImageView image, GridPane board) {
        List<Node> tiles = board.getChildrenUnmodifiable();
        for (int i = 0; i < tiles.size(); i++) {
            Node target = tiles.get(i);

            //This event deals with allowing the image to be dragged (making it easier to change long area of the grid)
            image.setOnDragDetected(event -> {
                {
                    //ADDED THIS TO PREVENT CERTAIN IMAGE TO BE DRAGGED
                    if (isDraggable) {
                        Dragboard db = image.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                        ClipboardContent content = new ClipboardContent();
                        SavedImage savedImage = (SavedImage) image.getImage();
                        content.putImage(savedImage);
                        List<File> file = new ArrayList<>();
                        file.add(new File("src\\Images\\" + savedImage.getFolderPath()));
                        content.putFiles(file);
                        System.out.println("DRAG = " + savedImage.toString());
                        db.setContent(content);
                        System.out.println("Drag detected");
                        event.consume();
                    }
                }
            });

            //This event deals with changing the gridpane's image to the selected image once it is being dragged over,
            //(making it easier to change long area of the grid)
            target.setOnDragOver(event2 -> {
                {
                    if (event2.getDragboard().hasImage()) {
                        event2.acceptTransferModes(TransferMode.COPY);
                        Dragboard db = event2.getDragboard();
                        SavedImage savedImage = readImageFile(db.getFiles().get(0));
                        ImageView piece = new ImageView(savedImage);
                        System.out.println("IMAGE OVER = " + piece.getImage());
                        piece.setPreserveRatio(true);
                        piece.setFitHeight(50);
                        piece.setFitWidth(50);
                        ImageView trg = (ImageView) target;
                        SavedImage trgImage = (SavedImage) trg.getImage();
                        System.out.println("TARGET UNDER = " + trgImage.getName());

                        //prevents the user from replacing the boundary walls as there still needs to be some
                        //control of what the user is able to do (preventing broken maps)
                        if (GridPane.getRowIndex(target) == 0 || GridPane.getColumnIndex(target) == 0
                                || GridPane.getRowIndex(target) == (height - 1) || GridPane.getColumnIndex(target) == (width - 1)
                                && trgImage.getName().equalsIgnoreCase("Wall")) {
                            target.setDisable(true);
                        } else if (!(trgImage.getName().equals(savedImage.getName()))) {
                            System.out.println("REPLACING TARGET = " + trgImage.getName());
                            board.getChildren().remove(target);
                            board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                            dragAndDrop(piece, board);
                            updateGridObjects();

                        } else {
                            System.out.println("NOT REPLACED:\n" + "TARGET = " + trg.getImage() + " == " + "SELECTED = " + savedImage.getName());
                        }
                        dragAndDrop(piece, board);
                        updateGridObjects();
                    }
                    int col = GridPane.getColumnIndex(target);
                    int row = GridPane.getRowIndex(target);
                    System.out.println("Drag Over Detected over column " + col + " and row " + row);
                    event2.consume();
                }
            });


            //This event deals with replacing the image once it has been dropped onto the gridpane,
            target.setOnDragDropped(event3 -> {
                {
                    event3.acceptTransferModes(TransferMode.ANY);
                    Dragboard db = event3.getDragboard();
                    boolean success = false;
                    if (db.hasImage()) {
                        SavedImage savedImage = readImageFile(db.getFiles().get(0));
                        ImageView piece = new ImageView(savedImage);
                        System.out.println("DROP" + piece.getImage());
                        piece.setPreserveRatio(true);
                        piece.setFitHeight(50);
                        piece.setFitWidth(50);
                        ImageView trg = (ImageView) target;
                        SavedImage trgImage = (SavedImage) trg.getImage();
                        System.out.println("TARGET TO DROP ONTO = " + trgImage.getName());
                        if (!(trgImage.getName().equals(savedImage.getName()))) {
                            System.out.println("REPLACING TARGET = " + trgImage.getName());
                            board.getChildren().remove(target);
                            board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                            dragAndDrop(piece, board);
                            updateGridObjects();
                        } else {
                            System.out.println("NOT (DROP) REPLACED:\n" + "TARGET = "
                                    + trg.getImage() + " == " + "SELECTED = " + savedImage.getName());
                        }
                        dragAndDrop(piece, board);
                        updateGridObjects();
                        success = true;
                    }
                    event3.setDropCompleted(success);
                    System.out.println("Drop detected");
                    event3.consume();
                }
            });



            //This event deals with replacing the gridpane's image one it has been clicked, (allowing for more options
            //if they user wants to drag or individually click each grid to edit the level.
            target.setOnMouseClicked(event9 -> {
                try {
                    MouseButton button = event9.getButton();
                    if (button == MouseButton.PRIMARY) {
                        Clipboard cb = Clipboard.getSystemClipboard();
                        SavedImage savedImage = readImageFile(cb.getFiles().get(0));
                        ImageView piece = new ImageView(savedImage);
                        System.out.println("CLICKED = " + savedImage.toString());
                        piece.setPreserveRatio(true);
                        piece.setFitHeight(50);
                        piece.setFitWidth(50);
                        int col = GridPane.getColumnIndex(target);
                        int row = GridPane.getRowIndex(target);
                        int childNumber = row * width + col;
                        ImageView trg = (ImageView) target;
                        SavedImage trgImage = (SavedImage) trg.getImage();
                        if (!(trgImage.getName().equals(savedImage.getName()))) {
                            System.out.println("CellID = " + gridPaneMap.getChildren().get(childNumber));

                            if (trgImage.getName().equals("Player.png") && !savedImage.getName().equals("TokenDoor.png")) {
                                playerPosition[0] = -1;
                                playerPosition[1] = -1;
                            }

                            if ("WallEnemy.png".equalsIgnoreCase(savedImage.getName())) {
                                try {
                                    resetPossibleDirections();
                                    checkEnemyPlacement(row, col);

                                    if (wallCount > 0) {
                                        setDirection(row, col, board, target, trgImage, piece);
                                    } else {
                                        Alert a = new Alert(Alert.AlertType.ERROR);
                                        a.setContentText("Wall Enemy needs to be placed next to a wall");
                                        a.show();
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Alert a = new Alert(Alert.AlertType.ERROR);
                                    a.setContentText("There needs to be a wall between enemy and the edge of the map");
                                    a.show();
                                }
                                gridPaneMap.getChildren().get(childNumber).setId(null);
                            } else if ("BlindEnemy.png".equalsIgnoreCase(savedImage.getName())) {
                                enemyDirections[row][col] = "up";
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                updateGridObjects();
                            } else if ("DumbEnemy.png".equalsIgnoreCase(savedImage.getName())) {
                                enemyDirections[row][col] = "up";
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                updateGridObjects();
                            } else if ("SmartEnemy.png".equalsIgnoreCase(savedImage.getName())) {
                                enemyDirections[row][col] = "up";
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                updateGridObjects();
                            } else if ("Teleporter.png".equals(savedImage.getName())) {
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                updateGridObjects();
                                gridPaneMap.getChildren().get(childNumber).setId(String.valueOf((int) floor(teleporterCount)));
                                gridObjects[row][col].setData(Integer.parseInt(gridPaneMap.getChildren().get(childNumber).getId()));
                                System.out.println(teleporterCount);
                                teleporterCount += 0.5;
                                if (teleporterCount == 10) {
                                    teleporterCount = 0;
                                }
                            } else if ("Player.png".equals(savedImage.getName())) {
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                updateGridObjects();
                                int oldPlayerNumberOnGrid = playerPosition[1] * width + playerPosition[0];
                                if (playerPosition[0] >= 0 && oldPlayerNumberOnGrid != childNumber) {
                                    File file = new File("src/Images/environment/Ground.png");
                                    piece = new ImageView(new SavedImage(file.toURI().toString(), file));
                                    piece.setPreserveRatio(true);
                                    piece.setFitHeight(50);
                                    piece.setFitWidth(50);
                                    gridPaneMap.add(piece, playerPosition[0], playerPosition[1]);
                                    dragAndDrop(piece, board);
                                    updateGridObjects();
                                }
                                playerPosition[0] = col;
                                playerPosition[1] = row;
                                gridPaneMap.getChildren().get(childNumber).setId(null);
                            } else if ("TokenDoor.png".equals(savedImage.getName())) {
                                System.out.println("REPLACING TARGET = " + trgImage.getName());
                                dragAndDrop(piece, board);
                                updateGridObjects();
                            } else if (savedImage.getName().length() > 9 && ("TokenDoor" + savedImage.getName().charAt(9) + ".png").equals(savedImage.getName())) {
                                board.getChildren().remove(target);
                                board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                dragAndDrop(piece, board);
                                updateGridObjects();
                            } else {
                                if (GridPane.getRowIndex(target) == 0 || GridPane.getColumnIndex(target) == 0 || GridPane.getRowIndex(target) == (height - 1) || GridPane.getColumnIndex(target) == (width - 1)
                                        && trgImage.getName().equalsIgnoreCase("Wall")) {
                                    target.setDisable(true);
                                } else {
                                    System.out.println("REPLACING TARGET = " + trgImage.getName());
                                    board.getChildren().remove(target);
                                    board.add(piece, GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
                                    dragAndDrop(piece, board);
                                    updateGridObjects();
                                    gridPaneMap.getChildren().get(childNumber).setId(null);
                                }
                            }

                            switch (LineEnemyDirection) {
                                case "HorizontalLineEnemy":
                                    gridPaneMap.getChildren().get(childNumber).setId("HorizontalLineEnemy");
                                    enemyDirections[row][col] = "left";
                                    break;
                                case "VerticalLineEnemy":
                                    gridPaneMap.getChildren().get(childNumber).setId("VerticalLineEnemy");
                                    enemyDirections[row][col] = "up";
                                    break;
                                default:
                                    break;
                            }

                            System.out.println("Player postition = " + playerPosition[0] + "  " + playerPosition[1]);
                            System.out.println("CellID = " + gridPaneMap.getChildren().get(childNumber));
                            System.out.println("Drag Over Detected over column " + col + " and row " + row);
                            event9.consume();
                        } else {
                            System.out.println("NOT (CLICKED) REPLACED:\n" + "TARGET = "
                                    + trg.getImage() + " == " + "SELECTED = " + savedImage.getName());
                        }
                     //this is allows for the user to revert (replace it back to a ground cell) the grid change that
                     // they did by pressing right click
                    } else if (button == MouseButton.SECONDARY) {
                        File file = new File("src/Images/environment/Ground.png");
                        SavedImage imageClear = new SavedImage(file.toURI().toString(), file);
                        ImageView piece = new ImageView(imageClear);
                        piece.setPreserveRatio(true);
                        piece.setFitHeight(50);
                        piece.setFitWidth(50);
                        int col = GridPane.getColumnIndex(target);
                        int row = GridPane.getRowIndex(target);
                        int childNumber = row * width + col;

                        ImageView oldImage = (ImageView) target;
                        SavedImage old = (SavedImage) oldImage.getImage();

                        enemyDirections[row][col] = null;

                        board.getChildren().remove(target);
                        gridPaneMap.add(piece, col, row);
                        dragAndDrop(piece, board);
                        updateGridObjects();
                        System.out.println("Drag Over Detected over column " + col + " and row " + row);
                        event9.consume();
                    }
                }
                catch (IndexOutOfBoundsException e){
                    System.out.println("No Image Selected");
                }
            });
        }
    }

    /**
     * Sets the token requirements for a token door, places it down and hides the token buttons.
     *
     * @param event - the most recent action to happen
     */
    @FXML
    private void onClickTokens(ActionEvent event) {
        //gets the integer value of the button from it's source data, and uses it to find the image url
        String nextEvent = event.getSource().toString();
        int doorNo = Integer.parseInt(nextEvent.charAt(nextEvent.length()-2)+"");
        String url = "src/Images/tokenDoors/TokenDoor" + doorNo + ".png" ;

        //creates the image objects for the token door
        File file = new File(url);
        SavedImage img = new SavedImage(file.toURI().toString(), file);
        ImageView elem = new ImageView(img);
        dragAndDrop(elem, gridPaneMap);

        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        SavedImage savedImage = (SavedImage) elem.getImage();
        content.putImage(savedImage);
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("src\\Images\\" + savedImage.getFolderPath()));
        content.putFiles(fileList);
        cb.setContent(content);
        selectedImage.setImage(elem.getImage());

        hideTokensDoorButtons();
    }

    /**
     * Sets the line enemies direction once one of their selection buttons is pressed, and hides the buttons.
     *
     * @param event - the most recent action to happen
     */
    @FXML
    private void onClickLineEnemyDirection(ActionEvent event) {
        if (event.getSource().equals(this.btnLineEnemyHorizontal)) {
            System.out.println("Horizontal");
            LineEnemyDirection = "HorizontalLineEnemy";
        } else if (event.getSource().equals(this.btnLineEnemyVertical)) {
            System.out.println("Vertical");
            LineEnemyDirection = "VerticalLineEnemy";
        }
        hideLineEnemyDirectionButtons();
    }

    /**
     * Clears the grid if the clear button is pressed.
     *
     * @param event - the most recent action to happen
     */
    @FXML
    private void onClickClear(ActionEvent event) {

        if (event.getSource().equals(this.btnClear)) {
            this.gridPaneMap.getChildren().clear();
            this.collectablesBox.getChildren().clear();
            this.environmentBox.getChildren().clear();
            this.moveableEntitiesBox.getChildren().clear();
            this.doorsBox.getChildren().clear();
            this.selectedImage.setImage(null);
            initialize();
        }
    }

    /**
     * Calls the save method if the save button is pressed.
     *
     * @param event - the most recent action to happen
     */
    @FXML
    private void onClickSave(ActionEvent event) throws IOException {
        if (event.getSource().equals(this.btnSave)) {
            Save(this.gridPaneMap);
        }
    }

    /**
     * Makes the buttons visible to select how many tokens a token door should require.
     */
    private void showTokenDoorButtons() {
        hboxLineEnemyDirection.setVisible(false);
        hboxTokens.setVisible(true);
    }

    /**
     * Hides the buttons that select how many tokens a token door should require.
     */
    private void hideTokensDoorButtons() {
        hboxTokens.setVisible(false);
    }

    /**
     * Makes the buttons visible to select which direction a line enemy should travel.
     */
    private void showLineEnemyDirectionButtons() {
        hboxTokens.setVisible(false);
        hboxLineEnemyDirection.setVisible(true);
    }

    /**
     * Hides the buttons that select which direction a line enemy should travel.
     */
    private void hideLineEnemyDirectionButtons() {
        hboxLineEnemyDirection.setVisible(false);
    }


    /**
     * Converts the GridPane into a SavedImage 2d array, then saves it as a map to a text file.
     *
     * @param map - the map to be saved
     */
    private void Save(GridPane map) {
        ObservableList<Node> cellContent = map.getChildrenUnmodifiable();
        SavedImage[][] elements = new SavedImage[height][width];

        for (Node elem : cellContent) {
            if (!(elem instanceof Group)) {
                int row = GridPane.getRowIndex(elem);
                int col = GridPane.getColumnIndex(elem);
                ImageView view = (ImageView) elem;
                SavedImage img = (SavedImage) view.getImage();
                elements[row][col] = img;
                System.out.println("Value = " + img.getName());
            }
        }

        System.out.println(elements.length);
        writeToFile(elements);
        System.out.println("");
    }

    /**
     * Converts an array's contents into text file characters, and writes them to the text file.
     *
     * @param elements - the 2d array representing the map
     */
    private void writeToFile(SavedImage[][] elements) {
        try {
            FileReader.setFileName(title);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("GlobalFiles/CustomFiles/" + title + ".txt"));
            if (!hasPlayer(elements)) {
                elements = setDefaultPlayerPosition(elements);
            }
            int levelno = FileReader.getLevelFromName(title);
            String levelnoString = levelno + "";
            bufferedWriter.write(levelnoString);
            System.out.println(levelnoString);
            bufferedWriter.newLine();
            String size = width + " " + height;
            bufferedWriter.write(size);
            bufferedWriter.newLine();
            String output = "";
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    //ADDED THIS FOR TELEPORTERS
                    if (elements[row][col].getName().contains("Teleporter.png"))
                    {
                        output = ("*" + elements[row][col].getData());
                        bufferedWriter.write(output);
                    }
                    else {
                        output = GameController.objectify(elements[row][col].getName(), 0);
                        bufferedWriter.write(output);
                    }
                }
                bufferedWriter.newLine();
            }

            for (int row = 0; row < height; row ++) {
                for (int col = 0; col < width; col++) {
                    if (enemyDirections[row][col] != null) {
                        bufferedWriter.write("(" + col + "," + row + ") ");

                        switch (enemyDirections[row][col]) {
                            case "up":
                                bufferedWriter.write("1");
                                break;
                            case "down":
                                bufferedWriter.write("3");
                                break;
                            case "left":
                                bufferedWriter.write("0");
                                break;
                            case "right":
                                bufferedWriter.write("2");
                                break;
                        }
                        bufferedWriter.write(";");
                    }
                }
            }
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a single cell's image into the GridPane map.
     * @param cell - the object the cell will represent
     * @param col - the column index
     * @param row - the row index
     */
    private void loadCell(GameObject cell, int col, int row) {
        String imageLocation = cell.getImage();
        File file = new File("src/" + imageLocation);
        SavedImage image = new SavedImage(file.toURI().toString(), file);
        ImageView cellContent = new ImageView(image);
        cellContent.setFitHeight(50);
        cellContent.setFitWidth(50);

        if (row == 0 || col == 0 || row == (height - 1) || col == (width - 1)) {
            cellContent.setDisable(true);
        }

        gridPaneMap.add(cellContent, col, row);
    }

    /**
     * Sets the first ground cell in the map to a player.
     * @param elements - a 2D array representing the map
     * @return the appended map, containing a player providing there was at least one ground slot
     */
    private SavedImage[][] setDefaultPlayerPosition(SavedImage[][] elements) {
        File playerImg = new File("src/Images/moveableEntities/Player.png");
        SavedImage imagePl = new SavedImage(playerImg.toURI().toString(), playerImg);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (elements[row][col].getName().equals("Ground.png")) {
                    elements[row][col] = imagePl;
                    return elements;
                }
            }
        }
        System.out.println("Error - map contains no player, nor any ground positions they could start in");
        return elements;
    }

    /**
     * Checks if the map currently has a player in it.
     * @param elements - a 2D array representing the map
     * @return a boolean, stating whether it's true that the map contains a player
     */
    private boolean hasPlayer(SavedImage[][] elements) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                System.out.println(elements[row][col].getName());
                if (elements[row][col].getName().equalsIgnoreCase("Player.png")) {
                    return true;
                }
            }
        }
        return false;
    }
}