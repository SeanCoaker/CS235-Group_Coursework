/**
 * Stores all names of images so that all objects can be drawn in JavaFX.
 * Version History - version 1.0
 * Filename: GameObject.java
 * @author Chester Descallar.
 * @version 1.0
 * @since 29-11-2019
 * copyright: No Copyright Purpose
 */

public class GameObject {
    private String image;

    /**
     * Returns the appropriate image of the object, prioritising the image of the lowest down subclass
     * @return - the name of the image text file
     */
    public String getImage() {
        return image;
    }
}