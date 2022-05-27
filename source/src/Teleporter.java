/**
 * Implements the teleporter cell.
 * Version History - version 1.0, version 2.0, version 2.1
 * Filename:  Teleporter.java
 * @author Petko Kuzmanov, Joseph Steven S, Ben Parker
 * @version 2.1
 * @since 10-11-2019
 * copyright: No Copyright Purpose
 */
public class Teleporter extends Cell {
	
	// Link is pointer to a teleporter.
	private Teleporter link;
	// The link number used to represent the connection.
	private int linkNo;

	/**
	 * Create a teleporter with it's necessary data.
	 * 
	 * @param image - the name of the image file for the teleporter.
	 * @param positionX - the x coordinates of the teleporter.
	 * @param positionY - the y coordinates of the teleporter.
	 */
	public Teleporter(String image, int positionX, int positionY) {
		super("teleporter", image, positionX, positionY);
	}

	/**
	 * Returns the link pointer for the teleporter.
	 * 
     * @return link - The teleporter link.
     */
	public Teleporter getLink() {
		return link;
	}

	/**
	 * Gets the new cell on which the player will be from passing through the teleporter.
	 *
	 * @param playerLoc - the player's location
	 * @return the new cell for the player
	 */
	public Object getTeleportLoc(int[] playerLoc) {
		int[] possibleLoc;
		if (playerLoc[0] != positionX) {
			possibleLoc = new int[]{link.getX() + (positionX-playerLoc[0]), link.getY()};
		} else {
			possibleLoc = new int[]{link.getX(), link.getY() + (positionY-playerLoc[1])};
		}
		if ("wall".equals(((Cell)GameController.getObject(possibleLoc)).toString())) {
			return GameController.getObject(playerLoc);
		} else {
			return GameController.getObject(possibleLoc);
		}
	}

	/**
	* Returns the number of teleporter pointer.
	* 
	* @return linkNo - The number of teleporter pointer.
	*/
	public int getLinkNo() {
		return linkNo;
	}

	/**
	* Sets the teleporter link.
	* 
	* @param link - pointer for teleporter.
	*/
	public void setLink(Teleporter link) {
		this.link = link;
	}

	/**
	* Sets the teleporter link number.
	* 
	* @param linkNo - number of teleporter pointer.
	*/
	public void setLinkNo(int linkNo) {
		this.linkNo = linkNo;
	}
}
