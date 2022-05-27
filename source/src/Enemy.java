/**
 * This class represents the game enemies.
 * This acts as an abstract superclass, setting the necessities for all enemies.
 * Version History - version 1.0, version 1.0, version 1.2
 * Filename: Enemy.java
 * @author Ben Parker and Chester Descallar
 * @version 1.2
 * @since 22-11-2019
 * copyright: No Copyright Purpose
 */
public abstract class Enemy extends MoveableEntity {
    //startDirection states which way the enemy initially moves.
    protected int startDirection;
    //coords holds the x and y positions in an array together for algorithms.
    protected int[] coords = new int[] {xPos, yPos};

    /**
     * Sets the enemies starting direction.
     * @param direction 0 = up, 1 = down, 2 = left, 3 = right.
     */
    public void setDirection(int direction) {
        this.startDirection = direction;
    }

    /**
     * Returns the startDirection.
     *
     * @return the objects start direction. The range is from 0 to 3.
     */
    public int getDirection() {
        return startDirection;
    }

    /**
     * This calls the movement algorithm used by the enemy based on the type of
     * enemy they are, represented by their subclass. This is abstract to ensure
     * all enemy subclasses call the same method to move.
     *
     * @param playerLoc the coordinates the player's on for calculating paths.
     */
    public abstract void nextMove(int[] playerLoc);

    /**
     * Checks if the next cell is a valid movement spot for enemies.
     * @param coords the coordinates of the cell
     * @return a boolean stating if the cell can be moved onto
     */
    public boolean checkNextCell(int[] coords) {
        String cellType = GameController.getObject(coords).toString();
        return (cellType.equals("ground") || cellType.equals("player"));
    }

    /**
     * Overwrites the move method to take an arrray of coordinates
     * @param newCoords an array holding an x and y position
     */
    @Override
    public void move (int[] newCoords){
        Object newLoc = GameController.getObject(newCoords);
        if(newLoc.toString().equals("player")) {
            GameController.dieDialog();
            GameController.initiateLevel(GameController.getLevel());
        } else if (newLoc != this) {
            moveToNewCellAndChangeItToGround((Cell) newLoc);
        }
    }
}