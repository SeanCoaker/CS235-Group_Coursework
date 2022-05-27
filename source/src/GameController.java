import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * Controls the flow of the game, does the main checks and updates.
 * Version History - version 1.0, version 1.1, version 1.2
 * Filename: GameController.java
 *
 * @author Ben Parker, Petko Kuzmanov, Sean Coaker
 * @version 1.2
 * @since 25-11-2019
 * copyright: No Copyright Purpose
 */
public class GameController {

    //A 2d array of the map, holding each cell as their object
    public static Object[][] levelMap;
    //The player object
    private static Player player;
    //The number of the current level
    private static int levelNo = 1;
    //An array of all the enemy objects
    private static Enemy[] enemies;
    //The time (in seconds) since the start of the level
    private static int time = 0;
    //A timer to keep track of the playtime
    private static Timer timer = new Timer();
    //Stores the current user profile being used
    private static Profile userProfile;
    //Stores all existing user profiles
    private static ArrayList<Profile> allProfiles = new ArrayList<>();
    //map width
    private static int mapWidth;
    //map height
    private static int mapHeight;
    // True if the level is custom
    private static boolean isCustom;
    
    /**
     * Calls any methods that need to be called to set up once the game has been started.
     *
     * @param passedProfile - the user profile selected
     */
    public static void startGame(Profile passedProfile) {
        FileReader.readLeaderboard();
        FileReader.setupFileNames();
        startTimer();
        if (!allProfiles.contains(passedProfile)) {
            allProfiles.add(passedProfile);
            //Stores non-exisiting profile
        }
        userProfile = passedProfile;

        //creates a folder for the profile if one doesn't exist
        File folder = new File("PlayerFolders/Player" + userProfile.getUserName());
        if (!folder.exists()) {
            folder.mkdir();
        }
        initiateLevel(1);
    }

    /**
     * Sets all the appropriate class variables to those matching the new level.
     *
     * @param level - the level to start up. This number will be 100 + the level number for custom levels
     */
    public static void initiateLevel(int level) {
        FileReader.clearEnemies();
        levelNo = level;
        String filename = "GlobalFiles/";

        //if the level isn't a user made level, set it to their highest level. Boot up the level regardless
        if (level < 100) {
            userProfile.setHighestLevel(levelNo - 1);
            filename += "level" + level + ".txt";
        } else {
            //Finds the custom map file with the correct levelNo to be loaded.
            filename += "CustomFiles/" + FileReader.getFileName(levelNo) + ".txt";
        }

        FileReader.readLevel(filename);
        levelMap = FileReader.getMap();
        enemies = FileReader.getEnemies();
        //if the enemy is smart targeting, start off the node calculations
        for (Enemy enemy : enemies) {
            if (enemy.toString().equals("smart targeting enemy")) {
                ((SmartTargetingEnemy) enemy).startTargeting(GameController.getPlayerLoc());
            }
        }
        writeProfiles();
    }

    /**
     * Makes each enemy do their next move.
     */
    public static void moveEnemies() {
        for (Enemy enemy : enemies) {
            switch (enemy.toString()) {
                case "line enemy":
                    ((StraightLineEnemy) enemy).nextMove(getPlayerLoc());
                    break;
                case "smart targeting enemy":
                    ((SmartTargetingEnemy) enemy).nextMove(getPlayerLoc());
                    break;
                case "wall following enemy":
                    ((WallFollowingEnemy) enemy).nextMove(getPlayerLoc());
                    break;
                case "dumb targetting enemy":
                    ((DumbTargettingEnemy) enemy).nextMove(getPlayerLoc());
                    break;
                case "blind enemy":
                    ((BlindEnemy) enemy).nextMove(getPlayerLoc());
                    break;
            }
        }
    }

    /**
     * Saves the game contents to a file.
     *
     * @throws IOException if an invalid file name is passed through
     */
    public static void saveGame() throws IOException {
        pauseTimer();
        String foldername = "PlayerFolders/Player" + userProfile.getUserName();
        String filename = "";

        //creates a file name based on the users' name
        if (levelNo < 100) {
            filename = foldername + "/PlayerSave.txt";
        } else {
            //Finds the custom map file with the correct levelNo to be saved to.
            filename = foldername + "/PlayerSaveCustom.txt";


        }

        BufferedWriter file = new BufferedWriter(new FileWriter(filename));

        //saves the players inventory, item by item
        ArrayList<String> playerInv = player.getPlayerInventory();
        StringBuilder invStr = new StringBuilder();
        for (String item : playerInv) {
            invStr.append(item).append(", ");
        }
        if (invStr.length() > 0) {
            file.write(invStr.substring(0, invStr.length() - 2));
        }

        //saves the current time, level and map size
        String timeLayout = time / 60 + ":" + Math.round(time % 60);
        file.write("\n" + timeLayout + ":" + levelNo);
        file.write("\n" + levelMap[0].length + " " + levelMap.length + "\n");

        //calls a method to add the map layout
        file = saveMap(file);

        //write down the coordinates and facing position of every enemy
        int noOfEnemies = enemies.length;
        int counter = 1;
        for (Enemy n : enemies) {
            int[] loc = n.getCoords();
            file.write("(" + loc[0] + ", " + loc[1] + ") ");
            file.write(n.getDirection() + "");
            if (noOfEnemies > counter) {
                file.write("; ");
            }
            counter++;
        }
        file.close();
        startTimer();
    }

    /**
     * Writes the text file of all the profile names and highest beaten levels.
     */
    public static void writeProfiles() {
        try {
            FileWriter writer = new FileWriter("GlobalFiles/Profiles.txt", false); // changed from false -> true
            for (Profile profile : allProfiles) {
                writer.write(profile.getUserName() + ", " + profile.getHighestLevel() + "\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a move depending on the object on the new coordinates.
     *
     * @param coords - the coordinates of the new object
     */
    public static void nextMove(int[] coords) {
        try {
            Cell newCell = (Cell) levelMap[coords[1]][coords[0]];
            ArrayList<String> playerInventory = player.getPlayerInventory();
            switch (newCell.getType()) {
                case "ground":
                    player.moveToNewCell(newCell);
                    break;
                case "fire":
                    if (playerInventory.contains("fireboots")) {
                        player.moveToNewCell(newCell);
                    } else {
                        dieDialog();
                        initiateLevel(levelNo);
                    }
                    break;
                case "water":
                    if (playerInventory.contains("flippers")) {
                        player.moveToNewCell(newCell);
                    } else {
                        dieDialog();
                        initiateLevel(levelNo);
                    }
                    break;
                case "goal":
                    System.out.println(isCustom);
                    if (!isCustom) {
                        getLevelAt(levelNo);
                        levelNo++;
                    }
                    initiateLevel(levelNo);
                    break;
                case "teleporter":
                    player.moveToNewCell(newCell);
                    break;
                case "reddoor":
                    if (playerInventory.contains("redkey")) {
                        player.removeFromInventory("redkey");
                        GameController.changeToGround(newCell.getCoords());
                        player.moveToNewCellAndChangeItToGround(newCell);
                    } //if you dont have a key, do nothing
                    break;
                case "greendoor":
                    if (playerInventory.contains("greenkey")) {
                        player.removeFromInventory("greenkey");
                        GameController.changeToGround(newCell.getCoords());
                        player.moveToNewCellAndChangeItToGround(newCell);
                    }
                    break;
                case "bluedoor":
                    if (playerInventory.contains("bluekey")) {
                        player.removeFromInventory("bluekey");
                        GameController.changeToGround(newCell.getCoords());
                        player.moveToNewCellAndChangeItToGround(newCell);
                    }
                    break;
                case "tokendoor":
                    EnvironmentCell env = (EnvironmentCell) newCell;
                    System.out.println("players tokens: " + player.countTokens());
                    System.out.println("doors tokens: " + env.getTokenReq());
                    if (player.countTokens() >= env.getTokenReq()) {
                        GameController.changeToGround(newCell.getCoords());
                        player.moveToNewCellAndChangeItToGround(env);
                    }
                    break;
                case "token":
                case "fireboots":
                case "flippers":
                case "redkey":
                case "greenkey":
                case "bluekey":
                    //if its a collectable, pick it up
                    player.addToInventory(newCell);
                    player.moveToNewCellAndChangeItToGround(newCell);
                    break;
                case "wall":
                default:
            }
        } catch (Exception e) {
            dieDialog();
            initiateLevel(levelNo);

        }
    }

    /**
     * Sets the user profile as a new profile
     *
     * @param userProfile - the new user profile to be used
     */
    public static void setUserProfile(Profile userProfile) {
        GameController.userProfile = userProfile;
    }

    /**
     * Starts the timer and makes it increment every second.
     */
    public static void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                time++;
            }
        }, 1000, 1000);
    }

    /**
     * Pauses the timer.
     */
    public static void pauseTimer() {
        timer.cancel();
    }

    /**
     * Prints the level, time and the name of the player.
     *
     * @param time     - the time in which the player has finished the level
     * @param userName - the player's username
     * @param levelNo  - the number of the level
     */
    public static void endMenu(int time, String userName, int levelNo) {
        System.out.println(time);
        System.out.println(userName);
        System.out.println(levelNo);
    }

    /**
     * Loads a save file.
     *
     * @param filepath - the name of the save file, including '.txt'
     */
    public static void loadFile(String filepath) {
        File dir = new File("GlobalFiles/CustomFiles/");
        File[] listOfFiles = dir.listFiles();
        boolean legalLoadFile = false;

        File newFile = new File(filepath);
        String filename = newFile.getName();
        if (filename.contains("level")) {
            FileReader.readLevel(filepath);
        } else {
            for (File elem : listOfFiles) {
                if (elem.getName().equals(filename)) {
                    legalLoadFile = true;
                }
            }

            if (legalLoadFile) {
                FileReader.readLevel(filepath);
            } else {
                FileReader.readSave(filepath);
            }
        }
        levelNo = FileReader.getLevel();
        levelMap = FileReader.getMap();
        enemies = FileReader.getEnemies();
        player = new Player();
        int[] playerLoc = FileReader.getPlayerLoc();
        player.setPosition(playerLoc[0], playerLoc[1]);
        player.setPlayerInventory(FileReader.getPlayerInv());
        time = (int) FileReader.getTime();
    }

    /**
     * Loads a custom file.
     *
     * @param filepath - the name of the save file, including '.txt'
     */
    public static void loadCustomFile(String filepath) {
        FileReader.readLevel(filepath);
        levelNo = FileReader.getLevel();
        levelMap = FileReader.getMap();
        enemies = FileReader.getEnemies();
        player = new Player();
        int[] playerLoc = FileReader.getPlayerLoc();
        player.setPosition(playerLoc[0], playerLoc[1]);
        player.setPlayerInventory(FileReader.getPlayerInv());
        time = (int) FileReader.getTime();
    }

    /**
     * Change the cell in the given position to a ground cell.
     *
     * @param coords - the coordinates, stored in an array
     */
    public static void changeToGround(int[] coords) {
        setCell(coords, new EnvironmentCell("ground", "Images/environment/ground.png", coords[1], coords[0]));
    }


    /**
     * Sets a cell in the given position.
     *
     * @param coords - the coordinates of the cell
     * @param cell   - the cell
     */
    public static void setCell(int[] coords, Cell cell) {
        levelMap[coords[1]][coords[0]] = cell;
    }

    /**
     * Sets a location on the map to the player object.
     *
     * @param coords - the map coordinates for the player to take up
     */
    public static void setPlayerOnMap(int[] coords) {
        levelMap[coords[1]][coords[0]] = player;
    }

    /**
     * Sets a location on the map to a moveable entity.
     *
     * @param coords  - the map coordinates to be changed
     * @param movable - the entity to take up the coordinates spot
     */
    public static void setMoveableOnMap(int[] coords, MoveableEntity movable) {
        levelMap[coords[1]][coords[0]] = movable;
    }


    /**
     * Gets the player.
     *
     * @return the object of the player
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * If the inputted name is a current existing profile, return it. Otherwise, create and return it.
     *
     * @param profileName - the name of the profile being checked
     * @param level       - the highest level we know the profile has beaten without checking their profile object
     * @return the new or existing user profile
     */
    public static Profile getProfile(String profileName, int level) {
        for (Profile profile : allProfiles) {
            //if the profile does match the inputted name
            if (profile.getUserName().equals(profileName)) {
                return profile;
            }
        }
        Profile newProf = new Profile(profileName, level);
        allProfiles.add(newProf);
        return newProf;
    }

    /**
     * Gets the map's contents.
     *
     * @return a 2D array of objects, stored in the location of their map coordinates
     */
    public static Object[][] getLevelMap() {
        return levelMap;
    }

    /**
     * Gets the username's of all players.
     *
     * @return an array list containing all username's
     */
    public static ArrayList<String> getUsername() {
        ArrayList<String> username = new ArrayList<>();

        for (Profile elem : getAllProfiles()) {
            username.add(elem.getUserName());
        }

        return username;
    }

    /**
     * Gets the map of objects in their coordinate positions
     *
     * @return a 2d array of objects
     */
    public static Object[][] getLevelArray() {
        return levelMap;
    }

    /**
     * Gets the current number of the level they're on.
     *
     * @return the level number
     */
    public static int getLevel() {
        return levelNo;
    }

    /**
     * Gets the player's location.
     *
     * @return an array of the coordinates of the player
     */
    public static int[] getPlayerLoc() {
        return new int[]{player.getXPos(), player.getYPos()};
    }

    /**
     * Returns the specified object from the level map.
     *
     * @param coords - the coordinates of the object
     * @return the object on the coordinates
     */
    public static Object getObject(int[] coords) {
        return levelMap[coords[1]][coords[0]];
    }

    /**
     * Gets the current time played of the game.
     *
     * @return the time taken in seconds
     */
    public static int getTime() {
        return time;
    }

    /**
     * Returns all the currently created profiles.
     *
     * @return an array list holding all the profiles
     */
    public static ArrayList<Profile> getAllProfiles() {
        return allProfiles;
    }


    /**
     * Sets the current player.
     *
     * @param p - the new player object
     */
    public static void setPlayer(Player p) {
        player = p;
    }

    /**
     * Sets the array list of profiles by reading from the Profiles text file
     */
    public static void setAllProfiles() {
        allProfiles = FileReader.readProfiles();
    }


    /**
     * Gets the level at the specified location, and compares its slowest
     * entry(unless they aren't full) to the current time.
     * If the current time is faster, it is added.
     *
     * @param levelNo - the level to be looking at
     */
    private static void getLevelAt(int levelNo) {
        int slowestTime;
        try {
            slowestTime = (int) Leaderboard.getLevelDetails(levelNo)[2][1];
        } catch (NullPointerException e) {
            slowestTime = 9999;
        }
        if (time < slowestTime || slowestTime == -1) {
            Leaderboard.addProfile(levelNo, userProfile, time);
        }
        Leaderboard.writeUp();
    }


    /**
     * Creates a task such that the time is incremented every second by the timer.
     */
    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            time++;
        }
    };


    /**
     * Created die confirmation AlertBox to quit or restart game
     **/
    public static void dieDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("YOU ARE DEAD !!");
        alert.setContentText("Press OK to Restart / Cancel to Quit the Game");
        Optional<ButtonType> result = alert.showAndWait();
        if (((Optional) result).get() == ButtonType.OK) {
            alert.close();
        } else if (result.get() == ButtonType.CANCEL) {
            Main.getMainStage().close();
        }
    }

    /**
     * Saves the map layout to the passed through file.
     *
     * @param file - the file to append to
     * @return the appended file
     * @throws IOException - if the passed through file can't be written to
     */
    private static BufferedWriter saveMap(BufferedWriter file) throws IOException {
        String cellType;
        String addition;
        for (Object[] row : levelMap) {
            for (Object cellAsObj : row) {
                try {
                    cellType = ((Cell) cellAsObj).toString();
                    addition = writeCell(cellType, cellAsObj);
                } catch (Exception e) {
                    String typeLetter = ((MoveableEntity) cellAsObj).toString().charAt(0) + "";
                    if (typeLetter.equals("p")) {
                        addition = "PP";
                    } else {
                        addition = (typeLetter + "e").toUpperCase();
                    }
                }
                file.write(addition);
            }
            file.write("\n");
        }
        return file;
    }

    /**
     * Converts a text file url into the two-character map representation.
     *
     * @param input - the text file url
     * @param data  - any needed additional data, for teleporter links and token door requirements
     * @return the two-character string that represents it
     */
    public static String objectify(String input, int data) {
        input = input.replace(".png", "").toLowerCase();
        String output;
        if (input.equals("player")) {
            output = "PP";
        } else if (input.contains("enemy")) {
            output = input.charAt(0) + "e";
            output = output.toUpperCase();
        } else {
            output = writeCell(input, data);
        }
        return output;
    }

    /**
     * Converts a cell's name into it's two-character map representation.
     *
     * @param input     - the cell's name
     * @param cellAsObj - either the object of the cell, or integer data for a teleporter or token door
     * @return the two-character string that represents it
     */
    private static String writeCell(String input, Object cellAsObj) {
        String output;
        switch (input) {
            case "ground":
                output = "--";
                break;
            case "fireboots":
                output = "FB";
                break;
            case "tokendoor":
                try {   //cellAsObj is the created cell
                    output = "T" + ((EnvironmentCell) cellAsObj).getTokenReq();
                } catch (Exception e) { //cellAsObj is the data needed for the cell
                    output = "T" + cellAsObj;
                }
                break;
            case "tokendoor1":
                output = "T1";
                break;
            case "tokendoor2":
                output = "T2";
                break;
            case "tokendoor3":
                output = "T3";
                break;
            case "tokendoor4":
                output = "T4";
                break;
            case "tokendoor5":
                output = "T5";
                break;
            case "tokendoor6":
                output = "T6";
                break;
            case "tokendoor7":
                output = "T7";
                break;
            case "tokendoor8":
                output = "T8";
                break;
            case "tokendoor9":
                output = "T9";
                break;
            case "flippers":
                output = "FL";
                break;
            case "wall":
                output = "##";
                break;
            case "teleporter":
                try {   //cellAsObj is the created cell
                    output = "*" + ((Teleporter) cellAsObj).getLinkNo();
                } catch (Exception e) { //cellAsObj is the data needed for the cell
                    output = "*" + cellAsObj;
                }
                break;
            default:

                String firstLetter = (input.charAt(0) + "").toUpperCase();
                if (input.contains("door")) {
                    output = "D" + firstLetter;
                } else if (input.contains("key")) {
                    output = "K" + firstLetter;
                } else {
                    output = firstLetter + firstLetter;
                }
                break;
        }
        return output;
    }


    /**
     * Getting the the map width.
     *
     * @return mapWidth - returns the width of the map as an number.
     */
    public static int getMapWidth() {
        return mapWidth;
    }

    /**
     * Sets the current map width.
     *
     * @param mapWidth - the new map width number.
     */
    public static void setMapWidth(int mapWidth) {
        GameController.mapWidth = mapWidth;
    }

    /**
     * Getting the the map height.
     *
     * @return mapHeight - returns the height of the map as an number.
     */
    public static int getMapHeight() {
        return mapHeight;
    }

    /**
     * Sets the current map height.
     *
     * @param mapHeight - the new map height number.
     */
    public static void setMapHeight(int mapHeight) {
        GameController.mapHeight = mapHeight;
    }

    /**
     * Sets the isCustom boolean
     *
     * @param isCustom the new isCustom
     */
    public static void setIsCustom(boolean isCustom) {
        GameController.isCustom = isCustom;
    }

}