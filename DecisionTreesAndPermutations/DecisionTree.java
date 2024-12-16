// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 4
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;
    
    Set <String> savingSet;
    List <String> nodesOutputArray = new ArrayList<String>();
    
    private double X;
    private double Y;
    
    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Save Tree", this::saveTree);  // completion
        UI.addButton("Draw Tree", this::drawTree);  // challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node.
     * 
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        UI.clearText();
        if(theTree == null){
            return; //makes the tree 
        }
        ptSubTree(theTree, 0, "");
        
    }
    
    /**
     * Uses draw branches method to produce tree for
     * questions and answers.
     * states basic x and y variables called 
     * and drawBranch method called
     */
    public void drawTree(){
        UI.clearText();
        if(theTree == null){
            return;
        }
        
        X = 100; // x position
        Y = 300; // y position
        drawBranches(theTree, X, Y, 1);
        
    }
    
    /**
     * Draw branch method then called in draw tree
     */
    public void drawBranches(DTNode subtree, double x, double y, int ct){
        if(subtree != null){
            
            double nY = Y / Math.pow(2, ct);
            double nX = DTNode.WIDTH + x;
            subtree.draw(x, y);
            
            if(subtree.getYes() != null){
                UI.drawLine( x, y, nX, y - nY); // draw line between yes branch
                drawBranches(subtree.getYes(), nX, y - (nY), ct + 1); // draw yes branch
                
            }
            if(subtree.getNo() != null){
                UI.drawLine( x, y, nX, y + nY); //draw line between no branch
                drawBranches(subtree.getNo(), nX, y + (nY), ct + 1); // draw no branch
                
            }
        }
    }
    
    private void ptSubTree(DTNode subTree, int indent, String s){
        if(subTree != null){
            for(int i = 0; i < indent; i++){
                UI.print("\t");
            }
            UI.println(s + " " + subTree.getText());
            ptSubTree(subTree.getYes(),indent + 1, " Yes: "); //prints text and recalls method
            ptSubTree(subTree.getNo(),indent + 1, " No: "); //prints text and recalls method
            
        }
        
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree() {
        UI.clearText();
        if(theTree == null){
            return;
        }
        DTNode curNode = theTree;
        
        while(!curNode.isAnswer()){
            boolean question = UI.askBoolean("Is it true: " + curNode.getText() + "(Y/N): ");
            
            if(question){
                curNode = curNode.getYes(); //set to yes
            } else {
                curNode = curNode.getNo(); //set to no
            }
        }
        UI.println("Your answer is: " + curNode.getText());
        
    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {
        UI.clearText();
        boolean q = UI.askBoolean("Is it true: " + theTree.getText() + "(Y/N): "); //question
        DTNode currentNode = theTree;
        
        while(currentNode.isAnswer() == false){
            if(q){
                DTNode transfer = currentNode.getYes();
                currentNode = transfer;
                
                if(currentNode.isAnswer()){
                    Boolean check =  UI.askBoolean("I think I know. Is it a " + currentNode.getText()+"?"); //grow tree guess
                    
                    if(check){
                        UI.println("Great"); //if grow tree (yes)
                        break;
                        
                    } else {
                        String answer = UI.askString("What was the answer you wanted?"); //if grow tree (no)
                        String trait = UI.askString("How do I distinguish this?"); //if grow tree expand string 
                        
                        DTNode a = new DTNode(answer);
                        DTNode b = new DTNode(currentNode.getText());
                        currentNode.setText(trait);
                        currentNode.setChildren(a,b);
                        
                        UI.println("The tree has been changed"); //changed tree
                        break;
                        
                    }
                }
                q = UI.askBoolean("Is it true " + currentNode.getText() + " (Y/N): ");
                
            } else {
                DTNode transfer = currentNode.getNo();
                currentNode = transfer;
                
                if(currentNode.isAnswer()){
                    Boolean check = UI.askBoolean("I think I know. Is it a " + currentNode.getText()+"?");
                    
                    if(check){
                        UI.println("Great");
                        break;
                        
                    } else {
                        String answer = UI.askString("What was the answer you wanted?");
                        String trait = UI.askString("How do I distinguish this?");
                        
                        DTNode a = new DTNode(answer);
                        DTNode b = new DTNode(currentNode.getText());
                        currentNode.setText(trait);
                        currentNode.setChildren(a,b);
                        
                        UI.println("The tree has been changed");
                        break;
                        
                    }
                }
                q = UI.askBoolean("Is it true " + currentNode.getText() + " (Y/N): "); 
                
            }
        }
        
    }

    /**
     * save tree method, uses saveHelper method to store 
     * questions and answers. 
     */
    public void saveTree(){
        nodesOutputArray.clear();
        saveHelper(theTree);
        String filename = UIFileChooser.save("Choose a file to save");
        
        try{
            PrintStream pStream = new PrintStream(filename);
            for(String s: nodesOutputArray){
                pStream.println(s);
                
            }
        } catch (IOException e) {
            UI.println("Fail"); //print fail if doesnt work
            
        }
        
    }
    
    /**
     * save helper method called in save tree
     */
    public void saveHelper(DTNode currentNode){
        if(currentNode != null){
            if(currentNode.isAnswer()){
                nodesOutputArray.add("Answer: " + currentNode.getText()); //answer array
                
            } else {
                nodesOutputArray.add("Question: " + currentNode.getText()); //question array
                
            }
            saveHelper(currentNode.getYes());
            saveHelper(currentNode.getNo());
            
        }
        
    }

    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        UI.clearGraphics();
        UI.clearPanes();
        
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
            
        }
        try{
            theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));
            
        }
        catch(IOException e){
            UI.println("File reading failed: " + e);
        }
        
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
            
        }
        return node;
        
    }
}
