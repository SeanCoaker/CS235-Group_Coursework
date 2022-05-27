import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * This creates enemies that follow a certain wall.
 * Version History - version 1.0, version 1.1, version 1.2, version 2.0
 * Filename: SmartTargetingEnemy.java
 * @author Ben Parker
 * @version 2.0
 * @since 19-11-2019
 * copyright: No Copyright Purpose
 */
public class SmartTargetingEnemy extends Enemy {
    //Stores a node of the players location
    private PositionNode playerNode;
    //Stores a node of the enemy's location
    private PositionNode enemyNode;
    //an arraylist that stores the nodes of corners, intersections, player and enemy
    private ArrayList<PositionNode> nodes = new ArrayList<>();
    //Stores node array lists of the possible paths to take. Each array list in
    //this holds every node passed for tha path
    private final ArrayList<ArrayList<PositionNode>> POSSIBLE_PATHS = new ArrayList<>();
    //Stores the distances of paths, directly correlating to POSSIBLE_PATHS
    private final ArrayList<Integer> PATH_DISTANCE = new ArrayList<>();

    /**
     * The default constructor - only used for creating nodes.
     */
    public SmartTargetingEnemy(){}

    /**
     * The constructor for creating smart targeting enemies.
     * This creates a node for both the enemy and the player.
     * @param playerLoc - the current location of the player
     */
    public SmartTargetingEnemy(int[] playerLoc) {
        this.image = "Images/moveableEntities/SmartEnemy.png";
    }

    /**
     * Moves the enemy in a direction such that they're heading towards the player.
     * If there's no path to the player, they will head in a random direction
     * @param playerLoc - the current location of the player
     */
    @Override
    public void nextMove(int[] playerLoc) {
        startTargeting(playerLoc);
        coords = new int[]{xPos, yPos};
        nodes = NodeManager.calculateNodes(playerLoc, coords, enemyNode);
        orderNodes(0);
        int[] movement = coords;
        if (validPath(enemyNode, playerNode, new ArrayList<>())) {
            calculatePaths(enemyNode, playerNode, 0, new ArrayList<>());
            //gets the coordinates of the next node in the shortest path
            int pathIndex = PATH_DISTANCE.indexOf(Collections.min(PATH_DISTANCE));
            int[] nextNode = POSSIBLE_PATHS.get(pathIndex).get(1).getCoords();

            //sets movement to one closer to the next node
            if (nextNode[0] != xPos) {
                movement[0] += Integer.signum(nextNode[0]-xPos);
            } else {
                movement[1] += Integer.signum(nextNode[1]-yPos);
            }

        } else {
            movement = findRandomMovement();
        }
        coords = movement;
        move(movement);
    }

    /**
     * This is called when the enemy has all required data, and starts the targetting algorithm
     * @param playerLoc - the current coordinates of the player
     */
    public void startTargeting(int[] playerLoc) {
        coords = new int[]{xPos,yPos};
        enemyNode = new PositionNode(coords);
        playerNode = new PositionNode(playerLoc);
        nodes = NodeManager.calculateNodes(playerLoc, coords, enemyNode);
    }

    /**
     * Returns the string of the smart targeting enemy.
     * @return - the string 'smart targeting enemy'
     */
    @Override
    public String toString() {
        return "smart targeting enemy";
    }


    /**
     * Gets the distance of the path between two nodes.
     * @param startNode - the node at the beginning of the path
     * @param endNode - the node at the end of the path
     * @return - the positive result of the sum of the difference in x and y
     */
    protected int distanceToNode(PositionNode startNode, PositionNode endNode) {
        //gets the coordinates of each node
        int[] startPos = startNode.getCoords();
        int[] endPos = endNode.getCoords();
        //gets the sum of the positive of the difference between the x and y values
        int distanceX = Math.abs(endPos[0] - startPos[0]);
        int distanceY = Math.abs(endPos[1] - startPos[1]);
        return distanceX + distanceY;
    }



    /**
     * Calculates the distance of possible paths.
     * @param startNode - the starting node of the path
     * @param endNode - the end of the path
     * @param distance - the current distance travelled to get to startNode
     * @param currentPath - the nodes taken, in order, to get to startNode
     */
    private void calculatePaths(PositionNode startNode, PositionNode endNode, int distance,
                                ArrayList<PositionNode> currentPath) {
        //if you've reached the end, add the results to POSSIBLE_PATHS
        if (startNode == endNode) {
            currentPath.add(startNode);
            POSSIBLE_PATHS.add(currentPath);
            PATH_DISTANCE.add(distance);
        } else {
            //for every possible direction from the node
            for (int x = 0; x < 4; x++) {
                //if the direction has a node that can travel to endNode
                PositionNode newPathNode = startNode.getConnectedNodes()[x];
                boolean newPathValid = validPath(newPathNode, endNode,
                        new ArrayList<>());

                if (newPathValid && !currentPath.contains(newPathNode)) {
                    //add the distance between them to distance and recursive call
                    currentPath.add(startNode);
                    int addedDistance = distanceToNode(startNode, newPathNode);
                    distance += addedDistance;
                    //when making the recursive call, it copies the contents of
                    //current path so it isn't using a pointer
                    calculatePaths(newPathNode, endNode, distance,
                            (ArrayList<PositionNode>)currentPath.clone());
                    //removes these additions to the path and distance for the
                    //next iteration of the loop
                    currentPath.remove(startNode);
                    distance -= addedDistance;
                }
            }
        }
    }

    /**
     * Find a random valid movement for the enemy.
     * @return - the coordinates of the cell to move to
     */
    private int[] findRandomMovement(){
        for (int x = 0; x < 4; x++) {
            //calculates the coordinates for moving, where 0=N, 1=E, 2=S, 3=W
            int[] newCoords = coords;
            int indexToChange = Math.abs(1 - x%2);
            if (indexToChange == 0) {
                newCoords[0] += 2-x;
            } else {
                newCoords[1] += x-1;
            }
            //if these new coordinates are a valid movement, return them
            if (checkNextCell(newCoords)) {
                return newCoords;
            }
        }
        //if none of the nearby cells are valid movements, don't move
        return coords;
    }

    /**
     * Checks if there is a valid path from the start and end node.
     * @param start - the start of the possible path
     * @param end - the end of the possible path
     * @param checked - all nodes already checked for a possible path
     * @return - a boolean value stating if you can travel from start to end
     */
    private boolean validPath(PositionNode start, PositionNode end, ArrayList<PositionNode> checked) {
        //if the start node isn't a valid node, return false. Otherwise, add
        //the node to the array list of checked nodes
        if (start == null) {
            return false;
        }
        checked.add(start);
        //if the end node is connected to the start node
        PositionNode[] nodesFromStart = start.getConnectedNodes();
        if (Arrays.asList(nodesFromStart).contains(end)) {
            return true;
        } else {
            //for every node attached to the start, call validPath and return
            //true if they do, providing that node hasn't been checked
            for (PositionNode n : nodesFromStart) {
                if (!checked.contains(n) && validPath(n, end, checked)) {
                    return true;
                }
            }
            //if none of these nodes have a valid path, return false
            return false;
        }
    }

    /**
     * Orders the nodes based on how close they are to an inputted node
     * @param startNode - the node that the node's distances are based on
     */
    private void orderNodes(int startNode) {
        for (PositionNode node : nodes) {
            node.setStartNode(nodes.get(startNode));
        }
        Collections.sort(nodes);
    }

}
