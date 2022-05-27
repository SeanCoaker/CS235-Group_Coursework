/**
 * This creates enemies that travel directly towards the players location.
 * Upon hitting obstacles, the enemy will continue bumping into them until the 
 * direct path to the player changes.
 * Filename: DumbTargettingEnemy.java
 * Version History - version 1.0
 * @author Ben Parker
 * @version 1.0
 * @since 20-11-2019
 * copyright: No Copyright Purpose
 */
public class DumbTargettingEnemy extends Enemy {

    /**
     * Creates a dumb targetting enemy.
     */
    DumbTargettingEnemy(){
        this.image = "Images/moveableEntities/DumbEnemy.png";
    }

    /**
     * Moves the enemy towards the player, assuming there's nothing in the way.
     * @param playerLoc - the player's location, to find the appropriate direction.
     */
    @Override
    public void nextMove(int[] playerLoc) {
        int xDiff = playerLoc[0] - xPos;
        int yDiff = playerLoc[1] - yPos;
        int[] newCoords = getCoords();
        if (Math.abs(xDiff) >= Math.abs(yDiff)) {
            newCoords[0] += (xDiff / Math.abs(xDiff));
        } else {
            newCoords[1] += (yDiff / Math.abs(yDiff));
        }
        if (checkNextCell(newCoords)){
            move(newCoords);
        }
    }

    /**
     * Converts it to a string
     * @return dumb targetting enemy as a string.
     */
    @Override
    public String toString() {
        return "dumb targetting enemy";
    }
}
