import javafx.scene.image.Image;

/**
 * This class represents the cells of the map
 * Version History - version 1.0, version 1.1
 * Filename: Cell.java
 * @author Petko Kuzmanov, Joseph Steven S, Ben Parker
 * @version 1.1
 * @since 19-11-2019
 * copyright: No Copyright Purpose
 */
public abstract class Cell extends GameObject {

    //The type of the cell
    protected String type;
    //The image that represents the cell on the map
    protected String image;
    //The cell's X axis position
    protected int positionX;
    //The cell's Y axis position
    protected int positionY;

    /**
     * Create a cell with it's necessary data.
     * 
     * @param type - the type of cell.
     * @param image - the name image file of the cell.
     * @param positionX - x coordinates of the cell.
     * @param positionY - y coordinates of the cell.
     */
    public Cell(String type, String image, int positionX, int positionY) {
        this.type = type;
        this.image = image;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * Gets the cell's image.
     *
     * @return - the name of the image file of the cell
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the position of the cell on the x axis.
     *
     * @return - the x value
     */
    public int getX() {
        return positionX;
    }

    /**
     * Gets the position of the cell on the y axis.
     *
     * @return - the y value
     */
    public int getY() {
        return positionY;
    }

    /**
     * Returns the type of the cell.
     *
     * @return - a string stating its type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the coordinates of the cell.
     *
     * @return - the x and y position in an integer array
     */
    public int[] getCoords() {
        return new int[]{positionX, positionY};
    }

    public Cell getClone() {
        try {
            return (Cell) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Sets the image of the cell.
     *
     * @param image - the name of the image file
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Sets the position on the X axis.
     *
     * @param positionX - the new x position
     */
    public void setX(int positionX) {
        this.positionX = positionX;
    }

    /**
     * Sets the position on the Y axis.
     *
     * @param positionY - the new y position
     */
    public void setY(int positionY) {
        this.positionY = positionY;
    }

    /**
     * Converts it to a string.
     * 
     * @return type of cell as string.
     */
    public String toString() {
        return type;
    }
}
