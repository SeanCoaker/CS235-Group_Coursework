import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This creates enemies that travel in a random direction.
 * Version History - version 1.0
 * Filename: BlindEnemy.java
 * @author Ben Parker
 * @version 1.0
 * @since 25-11-2019
 * copyright: No Copyright Purpose
 */
public class BlindEnemy extends Enemy {

    /**
     * Creates a blind enemy.
     */
    BlindEnemy(){
        this.image = "Images/moveableEntities/BlindEnemy.png";
    }

    /**
     * Moves the enemy to a random location.
     * @param playerLoc - the player's location, to find the appropriate direction.
     */
    @Override
    public void nextMove(int[] playerLoc) {
        Random r = new Random();
        int[] position;
        int randomInt;
        boolean hasMoved = false;
        ArrayList<Integer> validDirections = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        while (validDirections.size() > 0 && !hasMoved) {
            position = getCoords();
            randomInt = r.nextInt(4);
            if (validDirections.contains(randomInt)) {
                //change the coordinates based on the random int
                if (randomInt%2 == 0) {
                    position[1] += randomInt - 1;
                } else {
                    position[0] += 2 - randomInt;
                }
                //if these coordinates are valid, move there. Otherwise make sure the int isn't read again
                if (checkNextCell(position)) {
                    move(position);
                    hasMoved = true;
                } else {
                    validDirections.remove(validDirections.indexOf(randomInt));
                }
            }
        }
    }

    /**
     * Converts it to a string
     * @return dumb targetting enemy as a string.
     */
    @Override
    public String toString() {
        return "blind enemy";
    }
}
