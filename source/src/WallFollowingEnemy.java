/**
 * This creates enemies that follow a certain wall.
 * Version History - version 1.0
 * Filename: WallFollowingEnemy.java
 * @author Ben Parker
 * @version 1.0
 * @since 19-11-2019
 * copyright: No Copyright Purpose
 */
public class WallFollowingEnemy extends Enemy {
    //this shows the coordinate direction of the wall they're following. The
    //value of each coordinate can be -1, 0 or 1.
    private int[] wallDirection;
    //if the enemy turned to go around a corner on their last movement
    private boolean justTurned = false;

    /**
     * This constructor sets the location of the wall relative to the enemy.
     * @param initialWallLoc - the walls location relative to the enemy
     */
    public WallFollowingEnemy(int[] initialWallLoc) {
        wallDirection = initialWallLoc;
        this.image = "Images/moveableEntities/WallEnemy.png";
    }

    /**
     * Moves the enemy in a direction such that they're following their wall.
     * @param playerLoc - the player's location, to fit abstraction rules.
     */
    @Override
    public void nextMove(int[] playerLoc) {
        int[] oldWallDirection = new int[]{wallDirection[0], wallDirection[1]};
        if (!checkAhead()){
            //swaps wallDirection to point towards the wall in front of them
            wallDirection[0] = oldWallDirection[1];
            wallDirection[1] = oldWallDirection[0]*-1;
            justTurned = true;
            //and then calls nextMove again, in case it's a dead end
            nextMove(playerLoc);
        } else if (!checkWall() && !justTurned) {
            //swaps wallDirection to make them turn left
            wallDirection[0] = oldWallDirection[1] * -1;
            wallDirection[1] = oldWallDirection[0];
            justTurned = true;
            nextMove(playerLoc);
        } else {
            //they move to the cell in front of them
            justTurned = false;
            move(calculateCellInFront());
        }
    }

    /**
     * Sets the direction, and sets the wall location to be left of the enemy based upon that
     * @param direction - 0 = up, 1 = down, 2 = left, 3 = right.
     */
    public void setDirection(int direction) {
        startDirection = direction;
        if (direction%2 == 0) {
            wallDirection = new int[]{direction-1,0};
        } else {
            wallDirection = new int[]{0,direction-2};
        }
    }

    /**
     * Reads a string stating its a wall following enemy.
     * @return the string 'wall following enemy'
     */
    public String toString() {
        return "wall following enemy";
    }



    /**
     * Calculates the position of the wall the enemy is following.
     * @return the coordinates of the wall square currently being followed.
     */
    private int[] calculateWallLoc(){
        return new int[]{xPos + wallDirection[0], yPos + wallDirection[1]};
    }

    /**
     * Calculates the coordinate of the cell in front of the enemy, assuming
     * they're facing with their wall to their left.
     * @return the coordinates of the cell in front of them.
     */
    private int[] calculateCellInFront(){
        if (wallDirection[0] != 0){
            return new int[]{xPos, yPos + wallDirection[0]};
        } else {
            return new int[]{xPos + wallDirection[1], yPos};
        }
    }

    /**
     * Checks if the wall is still on their appropriate side, of if it turned
     * a corner.
     * @return - a boolean on whether the wall is still a straight line.
     */
    private boolean checkWall() {
        int[] wallLoc = calculateWallLoc();
        //returns if the cell is a wall or not
        return GameController.getObject(wallLoc).toString().equals("wall");
    }

    /**
     * Checks whether there are any obstructions in front of the enemy.
     * @return - a boolean on whether the next square is just ground.
     */
    private boolean checkAhead() {
        int[] cellAheadLoc = calculateCellInFront();
        //returns if the cell is a ground cell
        return checkNextCell(cellAheadLoc);
    }
}
