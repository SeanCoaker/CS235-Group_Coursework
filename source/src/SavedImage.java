import javafx.scene.image.Image;

import java.io.*;
import java.util.regex.Pattern;

/**
 * This class reads and makes a saved image file as output.
 * Version History - version 1.0, version 1.1
 * Filename: SavedImage.java
 * @author Joseph S, Chester Descallar
 * @version 1.1
 * @since 20-11-2019
 * copyright: No Copyright Purpose
 */
public class SavedImage extends Image {
    private String name;
    private int data;
    private String folder;

    /**
     * A constructor that saves the name, and the image from it's url.
     * @param url - the url of the image
     * @param file - the images file
     */
    public SavedImage(String url, File file) {
        super(url);
        folder = file.getPath().substring(11);
        this.name = file.getName();
        checkFolder();
    }

    /**
     * A constructor that saves the name, and the image from an InputStream.
     * @param is - the location of the image
     * @param file - the images file
     */
    public SavedImage(InputStream is, File file) {
        super(is);
        folder = file.getPath().substring(11);
        this.name = file.getName();
        checkFolder();
    }

    /**
     * Gets the data linked to the image.
     * @return the images data
     */
    public int getData() {
        return data;
    }

    /**
     * Gets the images filename.
     * @return the filename of the image
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the folder the file is contained in.
     * @return the folder and file name
     */
    public String getFolderPath() {
        return folder;
    }

    /**
     * Sets the name of the image to it's filename.
     * @param name - the images filename
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the data linked to the image.
     * @param data - the images' data
     */
    public void setData(int data) {
        this.data = data;
    }

    /**
     * Checks the folder is only one folder and the file, and changes it if not.
     */
    private void checkFolder() {
        String[] path = folder.split(Pattern.quote("\\"));
        if (path.length > 2) {
            folder = path[path.length - 2] + "\\" + path[path.length - 1];
        }
        System.out.println(folder);
    }

    /**
     * Reads the file and makes a SavedImage file out of it.
     * @param file - the file to be read
     * @return a SavedImage object made from the file
     */
    public static SavedImage readFile(File file) {
        SavedImage img = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));
            img = new SavedImage(is, file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    /**
     * Converts it into a string
     * @return it's data as a string
     */
    @Override
    public String toString() {
        return "SavedImage{" +
                "name='" + name + '\'' + "data='" + data + '\'' + '}';
    }
}