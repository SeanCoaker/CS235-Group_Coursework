/**
 * Stores user name and highest level of the player.
 * Version History - version 1.0, version 1.1
 * Filename: Profile.java
 * @author Hossein Mohammmadi, Ben Parker
 * @version 1.1
 * @since 20-11-2019
 * copyright: No Copyright Purpose
 */
public class Profile{
    private String userName;
    private int highestLevel;

    /**
     * Constructs the profile object with a name and highest level beaten.
     * @param userName - the username
     * @param highestLevel - the highest level completed
     */
    Profile(String userName, int highestLevel) {
        this.userName = userName;
        this.highestLevel = highestLevel;
    }

    /**
     * The method returns the username.
     * @return userName - the user's profile name.
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Sets the highest level completed by the player.
     * @param highestLevel- the new highest level, an integer from 1 upwards
     */
    public void setHighestLevel(int highestLevel){
        this.highestLevel = highestLevel;
    }
    /**
     * This method returns highest level completed by the player.
     * @return highestLevel - the highest level.
     */
    public int getHighestLevel()   {
        return highestLevel;
    }
    
    public String toString() {
		return userName;
    	
    }
}