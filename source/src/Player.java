import java.util.ArrayList;
import javafx.scene.input.KeyEvent;

/**
 * This class models the behaviours of the player
 * Version History - version 1.0, version 1.1
 * Filename: Enemy.java
 * Filename: Player.java
 * @author Khaled Alamri, Chester D, Petko K
 * @version 1.1
 * copyright: No Copyright Purpose
 */
public class Player extends MoveableEntity {

    // An ArrayList to store collectables.
    private ArrayList<String> playerInventory = new ArrayList<String>();

    /**
     * Creates a player.
     */
    Player() {
        this.image = "Images/moveableEntities/player.png";
    }

    /**
     * Check the cell coords on which the player wants to move
     *
     * @param event - the key that was pressed.
     * @return newCoords - an array holding x and y positions.
     */
    public int[] checkNextObject(KeyEvent event) {
        int[] newCoords = new int[] { xPos, yPos };
        switch (event.getCode()) {
            case RIGHT:
                // Right arrow key was pressed. So get right cell.
                newCoords[0] += 1;
                break;
            case LEFT:
                // Left arrow key was pressed. So get left cell.
                newCoords[0] -= 1;
                break;
            case UP:
                // UP arrow key was pressed. So get cell on top.
                newCoords[1] -= 1;
                break;
            case DOWN:
                // Down arrow key was pressed. So get cell below.
                newCoords[1] += 1;
                break;
        }
        return newCoords;
    }

    /**
     * Move to the next cell.
     *
     * @param cellCoords - the cell's coordinates
     */
    public void moveToCell(int[] cellCoords) {
        if (xPos == cellCoords[0]) {
            if (yPos == cellCoords[1] - 1) {
                moveDown();
            } else if (yPos == cellCoords[1] + 1) {
                moveUp();
            } else {
                teleport(cellCoords);
            }
        } else {
            if (xPos == cellCoords[0] - 1) {
                moveRight();
            } else if (xPos == cellCoords[0] + 1) {
                moveLeft();
            } else {
                teleport(cellCoords);
            }
        }
        GameController.setPlayerOnMap(getCoords());
    }

    /**
     * Adds the item to player's inventory.
     *
     * @param type - The type of collectable.
     */
    public void addToInventory(Cell type) {
        playerInventory.add(type.getType());
    }

    /**
     * Returns the player inventory.
     *
     * @return playerInventory - The player's inventory.
     */
    public ArrayList<String> getPlayerInventory() {
        return playerInventory;
    }

    /**
     * Returns the player's inventory without the tokens.
     *
     * @return newList - A list of items in player inventory.
     */
    public ArrayList<String> getPlayerInventoryWithoutTokens() {
        ArrayList<String> newList = new ArrayList<String>();
        for (String s : playerInventory) {
            if (!"token".equals(s)) {
                newList.add(s);
            }
        }
        return newList;
    }

    /**
     * Sets the player inventory.
     *
     * @param newInventory - a new inventory for the player.
     */
    public void setPlayerInventory(ArrayList<String> newInventory) {
        playerInventory = newInventory;
    }

    /**
     * Removes the item to player's inventory.
     *
     * @param item - The type of collectable.
     */
    public void removeFromInventory(String item) {
        playerInventory.remove(item);
    }

    /**
     * Counts player's tokens
     *
     * @return count - Amount of player's tokens.
     */
    public int countTokens() {
        int count = 0;
        for (String s : playerInventory) {
            if (("token").equals(s)) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Converts it to a string.
     *
     * @return player as a string.
     */
    @Override
    public String toString() {
        return "player";
    }

    /**
     * Move the player to the new cell and make the old cell be the same as
     * cellUnderPlayer.
     *
     * @param newCell - the cell to be moved to
     */
    public void moveToNewCell(Cell newCell) {
        GameController.setCell(new int[] { xPos, yPos },
                new EnvironmentCell(cellUnderMoveable.getType(), cellUnderMoveable.getImage(), xPos, yPos));
        // GameController.getLevelMap();
        if (newCell.toString().equals("teleporter")) {
            // calls the teleport method on the coordinates calculated by getTeleportLoc
            Cell cellToTravel = (Cell) ((Teleporter) newCell).getTeleportLoc(getCoords());
            teleport(cellToTravel.getCoords());
            cellUnderMoveable = cellToTravel;
            GameController.setPlayerOnMap(cellToTravel.getCoords());
        } else {
            moveToCell(newCell.getCoords());
            cellUnderMoveable = newCell;
            GameController.setPlayerOnMap(newCell.getCoords());
        }
    }

    /**
     * Move the player to the new cell and turn the old cell to ground.
     *
     * @param newCell - the cell to be moved to
     */
    public void moveToNewCellAndChangeItToGround(Cell newCell) {
        GameController.setCell(new int[] { xPos, yPos },
                new EnvironmentCell(cellUnderMoveable.getType(), cellUnderMoveable.getImage(), xPos, yPos));
        // GameController.getLevelMap();
        moveToCell(newCell.getCoords());
        cellUnderMoveable = new EnvironmentCell("ground", "Images/environment/ground.png", xPos, yPos);
    }

    /**
     * returns an array of coordinates.
     *
     * @return a new array of coordinates.
     */
    public int[] getCoords() {
        return new int[] { xPos, yPos };
    }

    /**
     * Teleports the player.
     *
     * @param newCoords - coordinates of new cell moving to.
     */
    private void teleport(int[] newCoords) {
        xPos = newCoords[0];
        yPos = newCoords[1];
    }
}
