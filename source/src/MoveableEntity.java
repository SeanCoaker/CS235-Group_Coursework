
/**
 * Gives movement to both player and enemy class.
 * Version History - version 1.0, version 1.1
 * Filename: MoveableEntity.java
 * @version 1.1
 * @since 16-11-2019
 * @author Chester Descallar, Ben Parker
 * copyright: No Copyright Purpose
 */
public abstract class MoveableEntity extends GameObject {

    protected int x; //position on screen
    protected int y; //position on screen
    protected int xPos; //position on grid
    protected int yPos; //position on grid
    private boolean collided;
    protected String image;
    protected String type; //the type of moveable entity it is
    protected Cell cellUnderMoveable = new EnvironmentCell("ground", "Images/environment/ground.png", xPos, yPos);

    /**
     * Moves the entity to a new position.
     *
     * @param newCoords - the direction of the new position relative to their
     * current one
     */
    public void move(int[] newCoords) {
        xPos += newCoords[0];
        yPos += newCoords[1];
        x = xPos; // Indicates how big the tile will when using javafx to move your boy around
        y = yPos;
    }

    /**
     * Calls the move method with the parameters needed to move up one.
     */
    public void moveUp() {
        int[] up = {0, -1};
        y = y - 1;
        move(up);
    }

    /**
     * Calls the move method with the parameters needed to move down one.
     */
    public void moveDown() {
        int[] down = {0, 1};
        y = y + 1;
        move(down);
    }

    /**
     * Calls the move method with the parameters needed to move left one.
     */
    public void moveLeft() {
        int[] left = {-1, 0};
        x = x - 1;
        move(left);
    }

    /**
     * Calls the move method with the parameters needed to move right one.
     */
    public void moveRight() {
        int[] right = {1, 0};
        x = x + 1;
        move(right);
    }

    /**
     * Returns the x position of the entity on the screen.
     *
     * @return - their x position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y position of the entity on the screen.
     *
     * @return - their y position
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the x coordinate of the entity.
     *
     * @return - their x coordinate
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Returns the y coordinate of the entity.
     *
     * @return - their y coordinate
     */
    public int getYPos() {
        return yPos;
    }

    /**
     * Returns the boolean value of collided.
     * @return the value of collided
     */
    public boolean getCollided() {
        return collided;
    }

    /**
     * Sets the value of collided.
     *
     * @param collided - a boolean value for if it has collided
     */
    public void setCollided(boolean collided) {
        this.collided = collided;
    }

    /**
     * Sets the position of the entity both on the grid and screen.
     *
     * @param xPos - the new x coordinate
     * @param yPos - the new y coordinate
     */
    public void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.x = xPos;
        this.y = yPos;
    }

    /**
     * Checks if the next cell can be stepped on.
     *
     * @param cellCoords - the cell to be checked
     * @return - a boolean value on if it can be stepped onto
     */
    public boolean checkNextCell(int[] cellCoords) {

        Object nextCell = GameController.getObject(cellCoords);

        return " ground".equals(((Cell) nextCell).getType());
    }

    /**
     * Returns the string value stating its a moveable entity.
     * @return the string 'moveable entity'
     */
    @Override
    public String toString() {
        return "moveable entity";
    }

    /**
     * Returns the coordinates of the entity
     * @return the x and y position, in an integer array
     */
    public int[] getCoords() {
        return new int[]{xPos, yPos};

    }

    /**
     * Sets the image file.
     * @param image - the string of the image file's name
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the image file name.
     * @return the string of the image file's name
     */
    @Override
    public String getImage() {
        return image;
    }

    /**
     * Gets the cell under the moveable entity.
     * @return the cell object of the cell underneath
     */
    public Cell getCellUnderMoveable() {
        return cellUnderMoveable;
    }

    /**
     * Calls the appropriate move method based on the new position.
     * @param cellCoords - the coordinates of the new cell to move to
     */
    public void moveToCell(int[] cellCoords) {
        if (xPos == cellCoords[0]) {
            if (yPos == cellCoords[1] - 1) {
                moveDown();
            } else if (yPos == cellCoords[1] + 1) {
                moveUp();
            }
        } else {
            if (xPos == cellCoords[0] - 1) {
                moveRight();
            } else if (xPos == cellCoords[0] + 1) {
                moveLeft();
            }
        }
        GameController.setMoveableOnMap(new int[]{xPos, yPos}, this);
    }

    /**
     * Moves the entity to a new cell, replacing the cell behind them with what it previously held.
     * @param newCell - the new cell object to move to
     */
    public void moveToNewCell(Cell newCell) {
        GameController.setCell(new int[]{xPos, yPos}, new EnvironmentCell(cellUnderMoveable.getType(), cellUnderMoveable.getImage(), xPos, yPos));
        moveToCell(newCell.getCoords());
        cellUnderMoveable = newCell;
    }

    /**
     * Moves the entity to a new cell, replacing the cell behind them with a ground cell.
     * @param newCell - the new cell object to move to
     */
    public void moveToNewCellAndChangeItToGround(Cell newCell) {
            GameController.setCell(new int[]{xPos, yPos}, new EnvironmentCell(cellUnderMoveable.getType(), cellUnderMoveable.getImage(), xPos, yPos));
            //GameController.getLevelMap();
            if (toString().equals("player")) {
                moveToCell(newCell.getCoords());
            } else {
                GameController.setCell(new int[]{xPos, yPos}, new EnvironmentCell(cellUnderMoveable.getType(), cellUnderMoveable.getImage(), xPos, yPos));
                xPos = newCell.getCoords()[0];
                yPos = newCell.getCoords()[1];
                GameController.setMoveableOnMap(new int[]{xPos, yPos}, this);
            }
            cellUnderMoveable = new EnvironmentCell("ground", "Images/environment/ground.png", xPos, yPos);
        }
}
