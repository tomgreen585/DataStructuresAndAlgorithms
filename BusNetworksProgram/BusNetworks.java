// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 6
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.Scanner;

public class BusNetworks {

    /** Map of towns, indexed by their names */
    private Map<String,Town> busNetwork = new HashMap<String,Town>();
    
    //added
    public static final int SIZE = 6;
    private int numTown = 0;

    /** CORE
     * Loads a network of towns from a file.
     * Constructs a Set of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            
            busNetwork.clear();
            UI.clearText();
            
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);
            
            /*# YOUR CODE HERE */
            
            String [] netTown = firstLine.split(" "); //first line of file -- string into net
            
            for(int i = 0; i < netTown.length; i ++){
                
                this.busNetwork.put(netTown[i],new Town(netTown[i]));
                
            }
            
            for(String twn : lines){
                
                Scanner scan = new Scanner(twn);
                String firstTown = scan.next();
                String secondTown = scan.next();
                
                //neighbour to 1-2 2-1
                this.busNetwork.get(firstTown).addNeighbour(this.busNetwork.get(secondTown)); 
                this.busNetwork.get(secondTown).addNeighbour(this.busNetwork.get(firstTown));
            }
            //Print this
            UI.println("Loaded " + busNetwork.size() + " towns:");
            
        } catch (IOException e) {
            throw new RuntimeException("Loading data.txt failed" + e);
        }
        
    }

    /**  CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     *  the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        
        /*# YOUR CODE HERE */
        
        for(Map.Entry<String, Town> eTown : busNetwork.entrySet()){
            
            //store
            Town prntTwn = eTown.getValue();
            Set<Town> printNeig = prntTwn.getNeighbours();
            
            //print methods
            UI.print(prntTwn.getName() + " => ");
            
            for(Town currentTwn : printNeig){
                
                UI.print(currentTwn.getName() + " ");
                
            }
            
            UI.print("\n");
        }
        
    }

    /** COMPLETION
     * Return a set of all the nodes that are connected to the given node.
     * Traverse the network from this node in the standard way, using a
     * visited set, and then return the visited set
     */
    public Set<Town> findAllConnected(Town town) {
        /*# YOUR CODE HERE */
        //set to hold visited towns
        Set <Town> visitTown = new HashSet<Town>();
        //pulls method
        findAllConnectedMethod(town,visitTown);
        
        return visitTown;
    }
    
    public void findAllConnectedMethod(Town town, Set<Town> visitTown){
        visitTown.add(town); //current down list
        
        for(Town townNeig : town.getNeighbours()){
            if(!visitTown.contains(townNeig)){
                continue;
            }
            
            visitTown.add(townNeig);
            findAllConnectedMethod(townNeig,visitTown); //reccur neighbour
            
        }
    }

    /**  COMPLETION
     * Print all the towns that are reachable through the network from
     * the town with the given name.
     * Note, do not include the town itself in the list.
     */
    public void printReachable(String name){
        Town town = busNetwork.get(name);
        
        if (town==null){
            
            UI.println(name+" not recognised town");
            
        }
        else {
            
            UI.println("\nFrom "+town.getName()+" you can get to:");
            
            /*# YOUR CODE HERE */
            for(Town currentTown : findAllConnected(town)){ //reachable from town given
                UI.println(currentTown.getName()); //print town
            }
            
        }
    }

    /**  COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");
        int groupNum = 1;
        
        /*# YOUR CODE HERE */
        Set<Town> printTown = new HashSet<Town>(); //hold printed towns
        
        for(String conectTown : this.busNetwork.keySet()){ //town in network
            if(!printTown.contains(this.busNetwork.get(conectTown))){ //set does not contain
                
                printTown.add(this.busNetwork.get(conectTown)); //add to set
                String print = ""; //set string empty
                
                for(Town townNeigh : findAllConnected(this.busNetwork.get(conectTown))){
                    
                    printTown.add(townNeigh);
                    String tNName = townNeigh.getName(); //string to contain townNeigh
                    
                    print += tNName + " ";
                    
                }
                
                UI.println("Group " + groupNum + ": " + print );
                
                groupNum++;
            }
        }
        
    }
    
    //CHANLLENGE
    public void drawNetwork() {
        try {
            
            //x & y offsets
            double yOS = 35;
            double xOS = -166;
            
            int margin = 50;
            busNetwork.clear(); //clear map
            UI.clearPanes();
            
            List<String> connectLines = Files.readAllLines(Path.of("data-with-lat-long.txt"));
            
            String initialLine = connectLines.remove(0);
            
            Scanner scanTown = new Scanner(initialLine);
            int numTowns = scanTown.nextInt();
            
            for (int i = 0; i < numTowns; i++ ) {
                String lineQ = connectLines.remove(0); //line queue
                Scanner scanLine = new Scanner(lineQ);
                String townName = scanLine.next();
                double latitude = scanLine.nextDouble();
                
                //y lat
                if (latitude > 0) latitude *= (-1); 
                latitude += yOS; //offset
                
                //x long
                double longitude = scanLine.nextDouble(); //offset
                longitude += xOS; //offset
                
                //X and Y variable
                double X = longitude * 45;
                double Y = (latitude * -45) + margin;
                
                //Draw
                UI.fillOval(X-(SIZE/2),Y-(SIZE/2),SIZE,SIZE);
                UI.setFontSize(9);
                UI.drawString(townName,X,Y);
                
                //run through town
                Town town = new Town(townName);
                town.x = X; town.y = Y;
                
                //name
                busNetwork.put(townName,town);
                
            }
            
            for(String connectTown : connectLines){
                Scanner scConTown = new Scanner(connectTown);
                
                String oneTown = scConTown.next();
                String twoTown = scConTown.next();
                
                Town initialTown = busNetwork.get(oneTown);
                Town nextTown = busNetwork.get(twoTown);
                
                if (initialTown == null || nextTown == null){
                    
                    return;
                    
                } else {
                    //1-2 2-1
                    initialTown.addNeighbour(nextTown);
                    nextTown.addNeighbour(initialTown);
                    
                }
                
            }
            
            for (Town currentTown : busNetwork.values()) {
                for (Town neighbourTown : currentTown.getNeighbours()) {
                    
                    UI.drawLine(currentTown.x, currentTown.y, neighbourTown.x, neighbourTown.y);
                    
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Loading data.txt failed" + e);
        }
    }
    
    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", ()->{loadNetwork(UIFileChooser.open());});
        UI.addButton("Print Network", this::printNetwork);
        UI.addTextField("Reachable from", this::printReachable);
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Challenge", this::drawNetwork);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        BusNetworks bnw = new BusNetworks();
        bnw.setupGUI();
    }

}
