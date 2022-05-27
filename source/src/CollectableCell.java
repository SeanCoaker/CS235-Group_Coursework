
/**
 * This class represents a collectable cell.
 * Version History - version 1.0
 * Filename: CollectableCell.java
 * @author Petko Kuzmanov, Joseph Steven S, Ben Parker
 * @version 1.0
 * @since 21-11-2019
 * copyright: No Copyright Purpose
 */
public class CollectableCell extends Cell {

    /**
     * Create a collectable cell
     *
     * @param type the type of the cell
     * @param image the image of the cell
     * @param positionX the X coordinate of the cell
     * @param positionY the Y coordinate of the cell
     */
    public CollectableCell(String type, String image, int positionX, int positionY) {
        super(type, image, positionX, positionY);
    }
}
