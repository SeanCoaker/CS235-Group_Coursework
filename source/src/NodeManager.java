import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages the positions of the nodes for smart targetting enemies.
 * Version History - version 1.0
 * Filename: NodeManager.java
 * @author Ben Parker
 * @version 1.0
 * @since 29-11-2019
 * copyright: No Copyright Purpose
 */
public class NodeManager {

    private static final ArrayList<String> CHECKED_SQUARES = new ArrayList<>();
    private static final ArrayList<PositionNode> NODES = new ArrayList<>();
    private static PositionNode playerNode;

    /**
     * Calculates the positions of nodes, and creates and links their objects.
     * It maps every possible path for the enemy in the map through testing
     * the nodes for paths, and adds new nodes for corners/intersections to the
     * array list for nodes
     * @param playerLoc - the current location of the player
     * @param coords - coords of the player
     * @param enemyNode - enemyNode
     * @return ArrayList positionNode
     */
    public static ArrayList<PositionNode> calculateNodes(int[] playerLoc, int[] coords, PositionNode enemyNode) {
        //clears checked squares and puts the enemy and player in it
        CHECKED_SQUARES.clear();
        playerNode = new PositionNode(playerLoc);
        addCheckedSquare(coords);
        addCheckedSquare(playerNode.getCoords());

        //clears the nodes, then updates and puts the enemy and player in it
        NODES.clear();
        NODES.add(enemyNode);
        NODES.add(playerNode);
        calculateMapNodes();
        return NODES;
    }

    /**
     * Calculates all the nodes on a map through recursion.
     */
    public static void calculateMapNodes() {
        ArrayList<PositionNode> newNodes = calculateNewNodes(NODES);
        while (newNodes.size() > 0) {
            ArrayList<PositionNode> nodesToTest = (ArrayList<PositionNode>)newNodes.clone();
            newNodes = calculateNewNodes(nodesToTest);
            NODES.addAll(nodesToTest);
        }
    }

    /**
     * Calculates all the new nodes on the map, from the currently found nodes.
     * @param currentNodes - the current nodes that have been found
     * @return the new nodes found
     */
    private static ArrayList<PositionNode> calculateNewNodes(ArrayList<PositionNode> currentNodes) {
        ArrayList<PositionNode> newNodes = new ArrayList<>();
        for (PositionNode node : currentNodes) {
            //for each possible direction NESW of the node
            int[] nodeCoords = node.getCoords();
            for (int x = 0; x < 4; x++) {
                //compare pathsNextSquare to arraylist
                int[] nextPossSquare = pathsNextSquare(nodeCoords.clone(), x);
                if (!Arrays.equals(nextPossSquare, nodeCoords) && checkNextCell(nextPossSquare)) {
                    String nextSquareStr = String.format("%s, %d", nextPossSquare[0], nextPossSquare[1]);
                    //if the current possible path hasn't been explored
                    int[] endSpot = followPath(nodeCoords.clone(), x);
                    if (!CHECKED_SQUARES.contains(nextSquareStr)) {
                        if (endSpot[0] != -1) {
                            PositionNode newNode = new PositionNode(endSpot);
                            node.addConnection(x, newNode);
                            newNodes.add(newNode);
                        } else {
                            endSpot = new int[]{endSpot[1], endSpot[2]};
                        }
                        checkPathSquares(nodeCoords, endSpot, x);
                    }
                }
            }
        }
        return newNodes;
    }



    /**
     * Follows a path until it discovers a node or a dead end.
     * @param cell - the coordinates of the current cell in the path
     * @param direction - the direction the path is relative to cell. 0 = up,
     *                    1 = right, 2 = down, 3 = left
     * @return - a node's coordinates or [-1, -1] for a dead end path
     */
    private static int[] followPath(int[] cell, int direction) {
        cell = pathsNextSquare(cell, direction);
        if (checkNextCell(cell)) {
            //if the new spot is a corner, return it's coordinates
            if (checkCorner(cell)){
                return cell;
                //otherwise, recursive call following the same path
            } else {
                return followPath(cell, direction);
            }
        } else {
            //the path is a dead end, so it returns -1 before the coordinates
            //inside the array to symbolise
            return new int[]{-1, cell[0], cell[1]};
        }
    }

    /**
     * Adds a cell coordinates to the checked squares.
     * @param intCoords - the coordinates of the new cell
     */
    private static void addCheckedSquare(int[] intCoords) {
        String strCoords = String.format("%s, %d", intCoords[0], intCoords[1]);
        CHECKED_SQUARES.add(strCoords);
    }

    /**
     * Puts all of a paths squares in the checked square.
     * @param start - the start of the path, as coordinates
     * @param end - the end of the path, as coordinates
     * @param direction - the direction the end is, relative to the start
     */
    private static void checkPathSquares(int[] start, int[] end, int direction) {
        //until the end is reached, set the start to one square closer and mark
        //the spots as checked
        while (!Arrays.equals(start, end)) {
            start = pathsNextSquare(start, direction);
            addCheckedSquare(start);
        }
    }

    /**
     * Gets the next cell coordinate of the path.
     * @param cell - the current cell position
     * @param direction - the direction of the path
     * @return - the next cell in the paths coordinates
     */
    private static int[] pathsNextSquare(int[] cell, int direction){
        int newCoord;
        if (direction%2 == 0) {
            newCoord = cell[1] + (direction-1);
            if (newCoord > -1 && newCoord < GameController.levelMap.length) {
                cell[1] = newCoord;
            }
        } else {
            newCoord = cell[0] + (2-direction);
            if (newCoord > -1 && newCoord < GameController.levelMap[0].length) {
                cell[0] = newCoord;
            }
        }
        return cell;
    }

    /**
     * Checks how many cells touching the current cell are ground cells.
     * @param currentCell - the coordinates of the current cell
     * @return - an integer of how many touching cells are ground cells
     */
    private static int checkNeighbourCells(int[] currentCell) {
        int touchingGroundCells = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = -1; y < 2; y+=2) {
                currentCell[x] += y;
                if (checkNextCell(currentCell)) {
                    touchingGroundCells++;
                }
                currentCell[x] -= y;
            }
        }
        return touchingGroundCells;
    }

    /**
     * Checks if the cell is a corner or intersection.
     * @param cell - the coordinates of the cell to be checked
     * @return - a boolean value stating if the cell is a corner/intersection
     */
    private static boolean checkCorner(int[] cell) {
        return (checkNeighbourCells(cell) > 1);
    }

    /**
     * Checks if the next cell is a valid spot for a node to be placed.
     * @param coords - the coordinates of the cell to check
     * @return a boolean value on if a node can be placed there
     */
    private static boolean checkNextCell(int[] coords) {
        String cellType = GameController.getObject(coords).toString();
        return (cellType.equals("ground") || cellType.equals("player"));
    }
}