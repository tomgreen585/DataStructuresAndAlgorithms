// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 6
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ecs100.*;

public class Town {

    private String name;
    private Set<Town> neighbours = new HashSet<Town>();
    
    //added for challenge
    public double x; 
    public double y; 

    public Town(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Set<Town> getNeighbours() {
        return Collections.unmodifiableSet(neighbours);
    }

    public void addNeighbour(Town node) {
        neighbours.add(node);
    }

    public String toString(){
        return name+" ("+neighbours.size()+" connections)";
    }

}
