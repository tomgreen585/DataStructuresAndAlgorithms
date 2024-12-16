// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 6
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

import ecs100.UI;
import java.awt.*;
import java.util.*;

/**
 * Search for a path to the goal in a maze.
 * The maze consists of a graph of MazeCells:
 *  Each cell has a collection of neighbouring cells.
 *  Each cell can be "visited" and it will remember that it has been visited
 *  A MazeCell is Iterable, so that you can iterate through its neighbour cells with
 *    for(MazeCell neighbour : cell){....
 *
 * The maze has a goal cell (shown in green, two thirds towards the bottom right corner)
 * The maze.getGoal() method returns the goal cell of the maze.
 * The user can click on a cell, and the program will search for a path
 * from that cell to the goal.
 * 
 * Every cell that is looked at during the search is coloured  yellow, and then,
 * if the cell turns out to be on a dead end, it is coloured red.
 */

public class MazeSearch {

    private Maze maze;
    private String search = "first";   // "first", "all", or "shortest"
    private int pathCount = 0;
    private boolean stopNow = false;
    
    //added ----- variable for end result
    private boolean outcome = false;


    /** CORE
     * Search for a path from a cell to the goal.
     * Return true if we got to the goal via this cell (and don't
     *  search for any more paths).
     * Return false if there is not a path via this cell.
     * 
     * If the cell is the goal, then we have found a path - return true.
     * If the cell is already visited, then abandon this path - return false.
     * Otherwise,
     *  Mark the cell as visited, and colour it yellow [and pause: UI.sleep(delay);]
     *  Recursively try exploring from the cell's neighbouring cells, returning true
     *   if a neighbour leads to the goal
     *  If no neighbour leads to a goal,
     *    colour the cell red (to signal failure)
     *    abandon the path - return false.
     */
    public boolean exploreFromCell(MazeCell cell) {
        if (cell == maze.getGoal()) {
            cell.draw(Color.blue);   // to indicate finding the goal
            return true;
            
        } 
        
        cell.visit(); //visit 
        cell.draw(Color.yellow); //color for cell visit
        
        //Iterate through neighbours
        UI.sleep(delay); 
        Iterator<MazeCell> nBour = cell.iterator();
        
        while(nBour.hasNext()){
            
            MazeCell current = nBour.next();
            
            if(!current.isVisited()){ //nbour not visited
                if(exploreFromCell(current)){ //found then draw line
                    
                    return true;
                    
                } else{
                    
                    current.draw(Color.red);
                    
                }
            }
        }
        
        return false;
    } 

    /** COMPLETION
     * Search for all paths from a cell,
     * If we reach the goal, then we have found a complete path,
     *  so pause for 1000 milliseconds 
     * Otherwise,
     *  visit the cell, and colour it yellow [and pause: UI.sleep(delay);]
     *  Recursively explore from the cell's neighbours, 
     *  unvisit the cell and colour it white.
     * 
     */
    public void exploreFromCellAll(MazeCell cell) {
        if (stopNow){ 
            return; 
        }    // exit if user clicked the stop now button
        
        if(cell == maze.getGoal()){ //cell end
            
            pathCount++;
            cell.draw(Color.blue); //draw
            
            UI.println("Found complete path."); 
            UI.sleep(1500);
            
        } else{
            
            cell.visit(); //visit cell draw yellow
            cell.draw(Color.yellow);
            
            UI.sleep(delay);
            
            for(MazeCell neighbour : cell){ //cell no visited then recur
                if(!neighbour.isVisited()){
                    
                    exploreFromCellAll(neighbour);
                    
                } 
            }
        }
        
        //univist for next path
        cell.unvisit(); 
        cell.draw(Color.white);
        
        maze.getGoal().draw(Color.green);
    }

    /** CHALLENGE
     * Search for shortest path from a cell,
     * Use Breadth first search.
     */
    public void exploreFromCellShortest(MazeCell start) {
        ArrayDeque<MazeCell> queueStart = new ArrayDeque<MazeCell>();
        HashMap<MazeCell, MazeCell> cellPath = new HashMap<MazeCell, MazeCell>();
        
        queueStart.offer(start); //start into queue
        start.draw(Color.yellow);
        MazeCell current = null; //current maze
        
        while(!queueStart.isEmpty()){ //nothing in queue
            current = queueStart.poll(); //put current
            
            if(current == maze.getGoal()){ //reached then break
                break;
            }
            
            current.visit(); //visted to not go back to cell
            
            if(current == start){ //first cell
                cellPath.put(current, null);
            }
            
            for(MazeCell neighbourCell : current){ //neighbour
                if(!neighbourCell.isVisited()){ //not visited continue
                    
                    queueStart.offer(neighbourCell);
                    
                    if(!current.equals(start)){ //not first cell
                    cellPath.put(neighbourCell, current);
                    }
                    
                }
            }
        }
        
        while(current != null){ //every cell draw yellow
            
            current.draw(Color.yellow);
            
            if(current == maze.getGoal()){ //goal blue
                current.draw(Color.blue);
            }
            
            current = cellPath.get(current);
            
        }
    }

    //=================================================

    // fields for gui.
    private int delay = 20;
    private int size = 10;
    /**
     * Set up the interface
     */
    public void setupGui(){
        UI.addButton("New Maze", this::makeMaze);
        UI.addSlider("Maze Size", 4, 40,10, (double v)->{size=(int)v;});
        UI.setMouseListener(this::doMouse);
        UI.addButton("First path",    ()->{search="first";});
        UI.addButton("All paths",     ()->{search="all";});
        UI.addButton("Shortest path", ()->{search="shortest";});
        UI.addButton("Stop",          ()->{stopNow=true;});
        UI.addSlider("Speed", 1, 101,80, (double v)->{delay=(int)(100-v);});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.);
    }

    /**
     * Creates a new maze and draws it .
     */
    public void makeMaze(){
        maze = new Maze(size);
        maze.draw();
    }

    /**
     * Clicking the mouse on a cell should make the program
     * search for a path from the clicked cell to the goal.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            maze.reset();
            maze.draw();
            pathCount = 0;
            MazeCell start = maze.getCellAt(x, y);
            if (search=="first"){
                exploreFromCell(start);
            }
            else if (search=="all"){
                stopNow=false;
                exploreFromCellAll(start);
            }
            else if (search=="shortest"){
                exploreFromCellShortest(start);
            }
        }
    }

    public static void main(String[] args) {
        MazeSearch ms = new MazeSearch();
        ms.setupGui();
        ms.makeMaze();
    }
}

