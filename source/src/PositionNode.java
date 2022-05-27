import java.util.Arrays;

/**
 * This represents a position on the grid, and other positions it's linked to.
 * Version History - version 1.0, version 1.1
 * Filename: PositionNode.java
 * @author Ben Parker
 * @version 1.1
 * @since 1.0
 * copyright: No Copyright Purpose
 */
public class PositionNode extends SmartTargetingEnemy implements Comparable<PositionNode> {
    //The coordinates of the node being stored
    private int[] nodeCoords;
    //pointers to the nearest nodes in a direct path of the node, in order NESW
    private final PositionNode[] CONNECTED_NODES = new PositionNode[]{null, null, null, null};
    //boolean values stating if there's a node direct from this, in order NESW
    private final boolean[] IS_CONNECTED = new boolean[]{false, false, false, false};
    //the start node for comparison with another node
    private PositionNode startNode;
    
    /**
     * A constructor that sets the coordinates of the node.
     * @param startCoords - the node's coordinates
     */
    public PositionNode(int[] startCoords) {
        nodeCoords = startCoords;
    }
    
    
    /**
     * Adds a pointer and boolean connection to another node.
     * @param direction - the direction(NESW) of the node to add
     * @param connectedNode - a pointer to the node object being added
     */
    public void addConnection(int direction, PositionNode connectedNode) {
        if (!Arrays.equals(nodeCoords, connectedNode.getCoords())) {
            IS_CONNECTED[direction] = true;
            CONNECTED_NODES[direction] = connectedNode;
            //if the connected node doesn't already have a connection to this node,
            //add a connection
            PositionNode[] connectedNodesConnections = connectedNode.getConnectedNodes();
            if (!Arrays.asList(connectedNodesConnections).contains(this)) {
                connectedNode.addConnection((direction + 2) % 4, this);
            }

            for (int x = 0; x < 4; x++) {
                System.out.print(IS_CONNECTED[x] + " - ");
                if (IS_CONNECTED[x]) {
                    System.out.print(CONNECTED_NODES[x].getCoords()[0] + ", " + CONNECTED_NODES[x].getCoords()[1]);
                }
            }
            System.out.print("\n");
        }
    }
    
    /**
     * Returns the coordinates of the node.
     * @return - the node's coordinates
     */
    public int[] getCoords() {
        return nodeCoords;
    }
    
    /**
     * Returns an array of pointers the node is a direct path to.
     * @return - an array of node pointers, in order NESW
     */
    public PositionNode[] getConnectedNodes() {
        return CONNECTED_NODES;
    }
    
    /**
     * Returns a boolean array of which directions there are nodes towards.
     * @return - a boolean array of if there are nodes nearby, in order NESW
     */
    public boolean[] getConnectionLocs() {
        return IS_CONNECTED;
    }
    
    /**
     * Sets the start node to a new node.
     * @param n - the new node to become the start node
     */
    protected void setStartNode(PositionNode n) {
        startNode = n;
    }

    /**
     * Returns if one node is closer to the start than another.
     * @param otherNode - the node that this node is being tested against
     * @return - a boolean on if this node is closer to start than otherNode
     */
    @Override
    public int compareTo(PositionNode otherNode) {
        int nodeOneDistance = distanceToNode(startNode, this);
        int nodeTwoDistance = distanceToNode(startNode, otherNode);
        return nodeOneDistance - nodeTwoDistance;
    }



    /**
     * Updates the coordinates of the node
     * @param newCoords - the new node's coordinates
     */
    protected void setCoords(int[] newCoords) {
        nodeCoords = newCoords;
    }
}
