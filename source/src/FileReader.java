import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * FileReader reads from the file and constructs the cell
 *  with Scanner as the parameter.
 * Version History - version 1.0, version 1.1, version 1.2, 1.3, version 1.4
 * Filename: FileReader.java
 * @author Marcin Kapcia, Chester Descallar, Ben Parker, Hossein, Sean Coaker
 * @version 1.4
 * copyright: No Copyright Purpose
 */
public class FileReader {

    //creates an array list to store all the enemies, so they can have their details set once the whole map is read
    private static ArrayList<Enemy> enemies = new ArrayList<>();
    //creates an array list to store all the teleporters, so they can have their details set once the whole map is read
    private static ArrayList<Teleporter> teleporters = new ArrayList<>();
    //stores the number of the level being read
    private static int level;
    //a double representing time, with the whole number being minutes and the decimal being seconds
    private static double time;
    //the map array
    private static Object[][] map;
    //the players coordinates
    private static int[] playerLoc;
    //the contents of the players inventory
    private static ArrayList<String> playerInv = new ArrayList<>();
    //a hash map linking file names to their level number
    private static HashMap<String, Integer> customFileNames = new HashMap<>();
    //the level number that the next custom file made would become
    private static int nextCustomFileNo = 101;

    private static HashMap<SmartTargetingEnemy, Integer[]> seMap = new HashMap<>();
    private static ArrayList<SmartTargetingEnemy> seList = new ArrayList<>();


    /**
     * Method to read a level file and returns a 2d array of objects from this
     * file. The program should handle the file with a not found exception and
     * shut down the program gracefully.
     *
     * @param fileName - the name of the file
     * @return The array of the cell from the file
     */
    public static Object[][] readLevel(String fileName) {

        if (fileName != null) {
            File inputFile = new File(fileName);
            try (Scanner in = new Scanner(inputFile)) {

                level = in.nextInt();
                Scanner postMap = FileReader.readMap(in);
                FileReader.completeEnemies(postMap);
                FileReader.completeTeleporters();

            } catch (FileNotFoundException e) {
                System.out.print("Cannot Open: " + fileName);
            }

        } else {
            System.err.print("File not found, Try again.");
            System.exit(0);
        }
        return map;
    }

    /**
     * Reads a save file and pulls out all the data from it
     *
     * @param fileName - the name of the save file
     * @return - the map read off the save file
     */
    public static Object[][] readSave(String fileName) {
        if (fileName != null) {
            File inputFile = new File(fileName);
            try (Scanner in = new Scanner(inputFile)) {
                inventoryArray(in.nextLine());
                saveDetails(in.nextLine());
                Scanner postMap = readMap(in);
                completeEnemies(postMap);
                completeTeleporters();
            } catch (FileNotFoundException e) {
                System.out.print("Cannot Open: " + fileName);
            }

        } else {
            System.err.print("File not found, Try again.");
            System.exit(0);
        }
        return map;
    }

    /**
     * Reads and creates the user profiles from a text file.
     *
     * @return - an array list of all user profiles
     */
    public static ArrayList<Profile> readProfiles() {
        try {
            Scanner in = new Scanner(new File("GlobalFiles/Profiles.txt"));
            ArrayList<Profile> list = new ArrayList<Profile>();
            String userName;
            int highestLevel;
            while (in.hasNext()) {
                userName = in.next();
                highestLevel = in.nextInt();
                list.add(new Profile(userName.substring(0, userName.length() - 1), highestLevel));
            }
            return list;
        } catch (FileNotFoundException e) {
            System.out.print("Cannot Open Profiles.txt");
            return null;
        }
    }

    /**
     * Clears the list of enemies.
     */
    public static void clearEnemies(){
        enemies.clear();
    }

    /**
     * Separates out the contents of the inventory line and stores them.
     *
     * @param totalInventory - a string of all items in the inventory
     */
    public static void inventoryArray(String totalInventory) {
        Scanner line = new Scanner(totalInventory);
        line.useDelimiter(", ");

        while (line.hasNext()) {
            playerInv.add(line.next());
        }

        line.close();
    }

    /**
     * Gets the time and level number.
     *
     * @param levelStats - a string holding the time and level details
     */
    public static void saveDetails(String levelStats) {
        Scanner line = new Scanner(levelStats);
        line.useDelimiter(":");
        int timeMins = line.nextInt();
        int timeSecs = line.nextInt();
        level = line.nextInt();
        time = timeMins + timeSecs * 0.01;
    }

    /**
     * Maps all the custom level numbers to their file names within the hash map.
     */
    public static void setupFileNames(){
        try {
            Scanner in = new Scanner(new File("GlobalFiles/CustomFileNames.txt"));
            String nextLine;
            String[] data;
            while (in.hasNextLine()) {
                nextLine = in.nextLine();
                data = nextLine.split(" - ");
                customFileNames.put(data[1], Integer.parseInt(data[0]));
                nextCustomFileNo++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File CustomFileNames in folder GlobalFiles is missing");
        }
    }

    /**
     * Reads the leaderboard, level by level.
     */
    public static void readLeaderboard() {
        try {
            Scanner in = new Scanner(new File("GlobalFiles/leaderboard.txt"));
            in.useDelimiter("\nLevel ");
            while (in.hasNext()) {
                readLevelLeaderboard(in.next());
            }
        } catch (FileNotFoundException e) {
            System.out.print("The file 'leaderboard.txt' is missing");
        }
    }

    /**
     * Gets the name of the file from its level number.
     * @param levelNo - the levels number, which will always be over 100
     * @return the string name of the file, or null if none match
     */
    public static String getFileName(int levelNo) {
        for (Map.Entry<String, Integer> entry : customFileNames.entrySet()) {
            if (Objects.equals(levelNo, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns the maps level number from it's name.
     * @param filename - the name assigned to the map
     * @return the level number of the map, over 100
     */
    public static int getLevelFromName(String filename) {
        return customFileNames.get(filename);
    }

    /**
     * Gets the 2d map array
     *
     * @return - a 2d array of objects in the loation of their appropriate
     * coordinates
     */
    public static Object[][] getMap() {
        return map;
    }

    /**
     * Gets all the enemies
     *
     * @return - an array list of enemy objects
     */
    public static Enemy[] getEnemies() {
        Enemy[] enemyArray = new Enemy[enemies.size()];
        for (int x = 0; x < enemies.size(); x++) {
            enemyArray[x] = enemies.get(x);
        }
        return enemyArray;
    }

    /**
     * Gets the coordinates of the player
     *
     * @return - an array of two integer coordinates
     */
    public static int[] getPlayerLoc() {
        return playerLoc;
    }

    /**
     * Gets the number of the current level
     *
     * @return - the levels integer
     */
    public static int getLevel() {
        return level;
    }

    /**
     * Gets the current time.
     * @return the time in seconds.
     */
    public static double getTime() {
        return time;
    }

    /**
     * Gets the contents of the player's inventory.
     * @return an array list of the string names of each item in the players inventory
     */
    public static ArrayList<String> getPlayerInv() {
        return playerInv;
    }

    /**
     * Adds a new file name to both the hash map and text file.
     * @param filename - the allocated name to the map
     */
    public static void setFileName(String filename) {
        int levelNo = nextCustomFileNo;
        try {
            if (!(customFileNames.get(filename) == null)){
                System.out.println("The entered name has already been used for another map");
            } else {
                customFileNames.put(filename, levelNo);
                FileWriter writer = new FileWriter(new File("GlobalFiles/CustomFileNames.txt"), true);
                writer.write(levelNo + " - " + filename + '\n');
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("File CustomFileNames in folder GlobalFiles is missing");
        }
    }




    /**
     * readMap reads the data file used by the program and returns the
     * constructed ArrayList.
     *
     * @param in - the scanner of the file
     * @return the array represented by the data file
     */
    private static Scanner readMap(Scanner in) {
        int mapWidth = in.nextInt();
        int mapHeight = in.nextInt();
        int y = 0;

        //saved to GameController memory.
        GameController.setMapWidth(mapWidth);
        GameController.setMapHeight(mapHeight);

        in.nextLine();

        //creates a 2d array the size of the map
        map = new Object[mapHeight][mapWidth];

        // read in the shape files and place them on the ArrayList.
        while (y < mapHeight) {
            Scanner line = new Scanner(in.nextLine());
            map[y] = FileReader.createRow(line, mapHeight, y);
            y++;
        }

        for (SmartTargetingEnemy elem : seList) {
            Integer[] coords = seMap.get(elem);
            SmartTargetingEnemy newSEnemy = new SmartTargetingEnemy(GameController.getPlayerLoc());
            newSEnemy.setPosition(coords[0], coords[1]);
            enemies.add(newSEnemy);
            map[coords[1]][coords[0]] = newSEnemy;
        }

        return in;
    }

    /**
     * Creating a new object of cells. The scanner is passed in and each value
     * of the file is read. Once the value is read it is assigned to a position
     * This variable is stored and passed into the new instances of the object
     * @param in - value from the scanner when the file is read
     * @param width - the width of the map
     * @param y - the current value of y, and therefore the y position the created objects will be on
     * @return the created object array of the rows contents
     */
    private static Object[] createRow(Scanner in, int width, int y) {
        Object[] mapLine = new Object[width];
        int x = 0;
        in.useDelimiter("");
        while (in.hasNext()) {
            String letterOne = in.next();
            String letterTwo = in.next();
            //compare the first letters and create objects based on them
            //if the first letter could be one of multiple objects, compare the second letter
            //if the object is an enemy, it adds them to the enemies array list first
            switch (letterOne) {
                case "P":
                    GameController.setPlayer(new Player());
                    mapLine[x] = GameController.getPlayer();
                    GameController.getPlayer().setPosition(x, y);
                    playerLoc = new int[]{x, y};
                    break;
                case "-":
                    mapLine[x] = new EnvironmentCell("ground", "Images/environment/ground.png", x, y);
                    break;
                case "F":
                    if (letterTwo.equals("F")) {
                        mapLine[x] = new EnvironmentCell("fire", "Images/environment/fire.png", x, y);
                    } else if (letterTwo.equals("L")) {
                        mapLine[x] = new EnvironmentCell("flippers", "Images/collectables/flippers.png", x, y);
                    } else {
                        mapLine[x] = new CollectableCell("fireboots", "Images/collectables/fireboots.png", x, y);
                    }
                    break;
                case "T":
                    if (letterTwo.equals("T")) {
                        mapLine[x] = new CollectableCell("token", "Images/collectables/token.png", x, y);
                    } else {
                        mapLine[x] = new EnvironmentCell("tokendoor", "Images/tokenDoors/tokendoor" + letterTwo + ".png", x, y, Integer.parseInt(letterTwo));
                    }
                    break;
                case "K":
                    if (letterTwo.equals("R")) {
                        mapLine[x] = new CollectableCell("redkey", "Images/collectables/redkey.png", x, y);
                    } else if (letterTwo.equals("G")) {
                        mapLine[x] = new CollectableCell("greenkey", "Images/collectables/greenkey.png", x, y);
                    } else {
                        mapLine[x] = new CollectableCell("bluekey", "Images/collectables/bluekey.png", x, y);
                    }
                    break;
                case "D":
                    switch (letterTwo) {
                        case "R":
                            mapLine[x] = new EnvironmentCell("reddoor", "Images/doors/reddoor.png", x, y);
                            break;
                        case "G":
                            mapLine[x] = new EnvironmentCell("greendoor", "Images/doors/greendoor.png", x, y);
                            break;
                        case "B":
                            mapLine[x] = new EnvironmentCell("bluedoor", "Images/doors/bluedoor.png", x, y);
                            break;
                        default:
                            DumbTargettingEnemy newDEnemy = new DumbTargettingEnemy();
                            newDEnemy.setPosition(x, y);
                            enemies.add(newDEnemy);
                            mapLine[x] = newDEnemy;
                            break;
                    }
                    break;
                case "L":
                    StraightLineEnemy newLEnemy = new StraightLineEnemy();
                    newLEnemy.setPosition(x, y);
                    enemies.add(newLEnemy);
                    mapLine[x] = newLEnemy;
                    break;
                case "W":
                    if (letterTwo.equals("E")) {
                        WallFollowingEnemy newWEnemy = new WallFollowingEnemy(new int[]{0, 1});
                        newWEnemy.setPosition(x, y);
                        enemies.add(newWEnemy);
                        mapLine[x] = newWEnemy;
                    } else {
                        mapLine[x] = new EnvironmentCell("water", "Images/environment/water.png", x, y);
                    }
                    break;
                case "S":
                    if (playerLoc == null) {
                        SmartTargetingEnemy newSEnemy = new SmartTargetingEnemy(null);
                        seMap.put(newSEnemy, new Integer[]{x,y});
                        seList.add(newSEnemy);
                    } else {
                        SmartTargetingEnemy newSEnemy = new SmartTargetingEnemy(GameController.getPlayerLoc());
                        newSEnemy.setPosition(x, y);
                        enemies.add(newSEnemy);
                        mapLine[x] = newSEnemy;
                    }
                    break;
                case "*":
                    Teleporter newTeleporter = new Teleporter("Images/environment/teleporter.png", x, y);
                    newTeleporter.setLinkNo(Integer.parseInt(letterTwo));
                    teleporters.add(newTeleporter);
                    mapLine[x] = newTeleporter;
                    break;
                case "G":
                    mapLine[x] = new EnvironmentCell("goal", "Images/environment/goal.png", x, y);
                    break;
                case "#":
                    mapLine[x] = new EnvironmentCell("wall", "Images/environment/wall.png", x, y);
                    break;
                case "B":
                    BlindEnemy newBEnemy = new BlindEnemy();
                    newBEnemy.setPosition(x, y);
                    enemies.add(newBEnemy);
                    mapLine[x] = newBEnemy;
                    break;
            }
            x++;
        }
        return mapLine;
    }

    /**
     * Goes through the enemy data at the end of the level files and applies
     * this to the enemy objects.
     *
     * @param in - the scanner holding the line of enemy data
     */
    private static void completeEnemies(Scanner in) {
        in.useDelimiter(";");
        int counter = 0;
        while (in.hasNext()) {
            String enemyData = in.next();
            //converts the last letter of the string to an integer, then sets that as the next enemies direction
            int enemyFacing = Integer.parseInt("" + enemyData.charAt(enemyData.length() - 1));
            enemies.get(counter).setDirection(enemyFacing);
            counter++;
        }
    }

    /**
     * Sets up the link between appropriate teleporter objects
     */
    private static void completeTeleporters() {
        Teleporter currentTel;
        Teleporter incrementedTel;
        for (int x = 0; x < teleporters.size(); x++) {
            currentTel = teleporters.get(x);
            if (currentTel.getLink() == null) {
                for (int y = x + 1; y < teleporters.size(); y++) {
                    incrementedTel = teleporters.get(y);
                    if (currentTel.getLinkNo() == incrementedTel.getLinkNo()) {
                        currentTel.setLink(incrementedTel);
                        incrementedTel.setLink(currentTel);
                    }
                }
            }
        }
    }

    /**
     * Reads one levels worth of data and sets this to the leaderboard.
     *
     * @param leveldata - the text chunk of all the level data
     */
    private static void readLevelLeaderboard(String leveldata) {
        Scanner in = new Scanner(leveldata);
        Scanner line;
        Profile[] users = new Profile[]{null, null, null};
        int[] times = new int[]{-1, -1, -1};
        int counter = 0;
        int level = Integer.parseInt(in.nextLine());
        while (in.hasNextLine()) {
            line = new Scanner(in.nextLine());
            line.useDelimiter(":");
            int timeMins = line.nextInt();
            int timeSecs = line.nextInt();
            users[counter] = GameController.getProfile(line.next(), level);
            times[counter] = timeMins * 60 + timeSecs;
            counter++;
        }
        Leaderboard.setLevel(level, users, times);
    }
}