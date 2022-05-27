/**
 * Implements the environment cell.
 * Version History - version 1.0
 * Filename: EnvironmentCell.java
 * @author Petko Kuzmanov, Joseph Steven S, Ben Parker
 * @version 1.0
 * @since 19-11-2019
 * copyright: No Copyright Purpose
 */
public class EnvironmentCell extends Cell {

    private int tokenReq = 0;

    /**
     * A constructor that sets up the necessary variables for any environment cell.
     * @param type - the type of environment cell it is
     * @param image - the name of the image file for the cell
     * @param positionX - the x coordinate
     * @param positionY - the y coordinate
     */
    public EnvironmentCell(String type, String image, int positionX, int positionY) {
        super(type, image, positionX, positionY);
    }

    /**
     * A constructor that sets up the necessary variables for a token door
     * @param type the type of environment cell it is
     * @param image the name of the image file for the cell
     * @param positionX the x coordinate of the cell
     * @param positionY the y coordinate of the cell
     * @param tokens the amount of tokens needed to unlock the door
     */
    public EnvironmentCell(String type, String image, int positionX,
                           int positionY, int tokens) {
        super(type, image, positionX, positionY);
        this.tokenReq = tokens;
    }

    /**
     * Returns the token requirement.
     *
     * @return tokenReq token requirement.
     */
    public int getTokenReq() {
        return tokenReq;
    }

    /**
     * Converts to a string.
     *
     * @return type.
     */
    public String toString() {
        return type;
    }
}