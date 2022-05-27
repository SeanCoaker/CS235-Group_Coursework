
/**
 * This creates enemies that travel in a straight line.
 * Upon hitting obstacles, the enemy will go back the way they came.
 * Filename: StraightLineEnemy.java
 * @author Ben Parker
 * @version 1.1
 * copyright: No Copyright Purpose
 */
public class StraightLineEnemy extends Enemy {

    //This is how much the enemies axis should be increased by
    private int movementIncrement;
    //sets the type of enemy to a line enemy
    protected String type = "line enemy";

    /**
     * This acts as the default constructor, setting their axis to 1 as default.
     */
    public StraightLineEnemy() {
        startDirection = 1;
        movementIncrement = 1;
        this.image = "Images/moveableEntities/LineEnemy.png";
    }

    /**
     * This constructor stores the axis and starting increment of the enemy.
     *
     * @param axis - 0 represents horizontally, and 1 vertically.
     * @param increment - possible values of 1(right or up) and -1(left or
     * down).
     */
    public StraightLineEnemy(int axis, int increment) {
        startDirection = axis;
        movementIncrement = increment;
        this.image = "Images/moveableEntities/LineEnemy.png";
    }

    /**
     * Moves the enemy one square in the current direction.
     *
     * @param playerLoc - the player coordinates, to follow abstraction rules.
     */
    @Override
    public void nextMove(int[] playerLoc) {
        int[] newLoc = getCoords();
        Object[][] map = GameController.getLevelArray();
        if (startDirection == 0 && newLoc[0] < map[0].length) {
            newLoc[0] += movementIncrement;
        } else if (startDirection == 1 && newLoc[1] < map.length) {
            newLoc[1] += movementIncrement;
        } else {
            reverseMovement();
            nextMove(playerLoc);
        }
        if (!checkNextCell(newLoc)) {
            reverseMovement();
            if (startDirection == 0) {
                newLoc[0] += movementIncrement;
            } else {
                newLoc[1] += movementIncrement ;
            }
            nextMove(playerLoc);
        }
        move(newLoc);
    }

    /**
     * Method to convert line enemy to a string
     *
     * @return The line
     */
    @Override
    public String toString() {
        return "line enemy";
    }



    /**
     * Reverses the direction the enemy is travelling along the axis.
     */
    private void reverseMovement() {
        movementIncrement *= -1;
    }
}
