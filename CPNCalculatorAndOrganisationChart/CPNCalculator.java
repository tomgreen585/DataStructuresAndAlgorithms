// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 5
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{
    
    final double PI = Math.PI;
    final double E = Math.E;
    
    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }

        ExpElem element = expr.getItem();

        //Return number type
        if(element.operator.equals("#")) return element.value; // return number
        
        if(element.operator.equals("PI")) return PI;// if pi return
        
        if(element.operator.equals("E")) return E; // if e return 

        if(expr.numberOfChildren() > 0) {
            if(element.operator.equals("+")) { //add 
                double returningInt = evaluate(expr.getChild(0)); // first element
                expr.removeChild(0); // remove 
                for(GTNode<ExpElem> currentElement : expr) {
                    returningInt += evaluate(currentElement); // add 
                }
                return returningInt; // return
                //METHODS REPEATED THROUGHOUT just altered on math-figure
                //sorry for alot of if and else but it works and thats what matters!
            } else if(element.operator.equals("-")) { // sub
                double returningInt = evaluate(expr.getChild(0));
                expr.removeChild(0);
                for(GTNode<ExpElem> currentElement : expr) {
                    returningInt -= evaluate(currentElement);
                }
                return returningInt;
            } else if(element.operator.equals("*")) { // mult
                double returningInt = evaluate(expr.getChild(0));
                expr.removeChild(0);
                for(GTNode<ExpElem> currentElement : expr) {
                    returningInt *= evaluate(currentElement);
                }
                return returningInt;
            } else if(element.operator.equals("/")) { // div
                double returningInt = evaluate(expr.getChild(0));
                expr.removeChild(0);
                for(GTNode<ExpElem> currentElement : expr) {
                    returningInt /= evaluate(currentElement);
                }
                return returningInt;
            } else if(element.operator.equals("avg")) { // avg
                double returningInt = evaluate(expr.getChild(0));
                expr.removeChild(0);
                for(GTNode<ExpElem> currentElement : expr) {
                    returningInt += evaluate(currentElement); 
                }
                return returningInt / (expr.numberOfChildren() + 1); 
            } else if(element.operator.equals("^")) { // pow
                if(expr.numberOfChildren() == 2) {
                    return Math.pow(evaluate(expr.getChild(0)), evaluate(expr.getChild(1))); 
                }
                else if(expr.numberOfChildren() > 2) { // print statement for 2 much
                    UI.println("Too many operands for '^' to work"); //print statement for a null return
                    return Double.NaN;
                }
                else { //print statement for 2 little
                    UI.println("Not enough operands for '^' to work"); //print statement for a null return
                    return Double.NaN;
                }
            } else if(element.operator.equals("log")) { //log
                if(expr.numberOfChildren() == 1) { 
                    return Math.log10(evaluate(expr.getChild(0)));
                } else if(expr.numberOfChildren() == 2) {  
                    return Math.log(evaluate(expr.getChild(0))) / Math.log(evaluate(expr.getChild(1)));
                } else if(expr.numberOfChildren() < 2) {
                    UI.println("Too many operands for 'Log' to work"); //print statement for a null return
                    return Double.NaN;
                } else {
                    UI.println("Not enough operands for 'Log' to work"); //print statement for a null return
                    return Double.NaN;
                }
            } else if(element.operator.equals("ln")) { // lognat
                if(expr.numberOfChildren() == 1) {
                    return Math.log(evaluate(expr.getChild(0)));  
                }
                UI.println("Too much or not enough operands to perform 'ln' to work"); //print statement for a null return
                return Double.NaN;
            } else if(element.operator.equals("sqrt")) { //squareroot
                if(expr.numberOfChildren() == 1) {
                    return Math.sqrt(evaluate(expr.getChild(0))); //print statement for a null return
                } else {
                    UI.println("To many operands for 'sqrt' to work"); //print statement for a null return
                }
            } else if(element.operator.equals("dist")) {
                if(expr.numberOfChildren() == 4) {// 2
                    double firstX = evaluate(expr.getChild(0));
                    double firstY = evaluate(expr.getChild(1));
                    double secX = evaluate(expr.getChild(2));
                    double secY = evaluate(expr.getChild(3));
                    double totX = Math.abs(firstX - secX);
                    double totY = Math.abs(firstY - secY);
                    
                    return Math.sqrt((totX) * (totX) + (totY) * (totY)); 
                } else if(expr.numberOfChildren() == 6) { //3
                    double firstX = evaluate(expr.getChild(0));
                    double firstY = evaluate(expr.getChild(1));
                    double firstZ = evaluate(expr.getChild(2));
                    double secX = evaluate(expr.getChild(3));
                    double secY = evaluate(expr.getChild(4));
                    double secZ = evaluate(expr.getChild(5));
                    
                    return Math.pow((Math.pow(secX - firstX, 2) + Math.pow(secY - firstY, 2) + Math.pow(secZ - firstZ, 2) * 1.0), 0.5);
                } else {
                    UI.println("To much or not enough operands for 'distance' to work"); //print statement for a null return
                }
            } else if(element.operator.equals("sin")) { //s
                if(expr.numberOfChildren() == 1) {
                    return Math.sin(evaluate(expr.getChild(0))); 
                } else {
                    UI.println("To many operands for 'sin' to work"); //print statement for a null return
                }
            } else if(element.operator.equals("cos")) { //c
                if(expr.numberOfChildren() == 1) {
                    return Math.cos(evaluate(expr.getChild(0))); 
                } else {
                    UI.println("To many operands for 'cos' to work"); //print statement for a null return
                }
            } else if(element.operator.equals("tan")) { //t
                if(expr.numberOfChildren() == 1) {
                    return Math.tan(evaluate(expr.getChild(0))); 
                } else {
                    UI.println("To many operands for 'tan' to work"); //print statement for a null return
                }   
            }
        }
        UI.println("Operator " + element.operator + " is invalid"); 
        return Double.NaN;
    }
    
    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");

        String cOfExpr = new String(expr); // creates copy 
        Scanner bChecker = new Scanner(expr); //bracketcheck
        Stack<String> bStack = new Stack<String>();
        
        boolean valid = true; 
        boolean sInside = false; // if returns value 
        boolean lstOpen = false;
        
        while(bChecker.hasNext()) { // for all tags
            String token = bChecker.next();
            if(token.contains("(") || token.contains(")")) {
                if(token.contains("(")) {
                    bStack.push(token); // add
                    lstOpen = true; 
                }
                else {
                    if(sInside = false || bStack.isEmpty() || lstOpen == true) { 
                        valid = false;
                    }
                    else {
                        bStack.pop(); // remove closing
                        sInside = false; // reset variable
                    }
                }
            }
            else { 
                sInside = true; // something inside identified
                lstOpen = false; 
            }
        }
        
        if(!bStack.isEmpty()) { 
            valid = false;
        }
        
        if(valid == true) { // if valid go ahead
            return readExpr(new Scanner(expr));   
        }
        else {
            UI.println("Invalid Brackets");
        }
        return null; 

        // MAKE WORK IF NOTHING HEREString expr = UI.askString("expr:");
        // MAKE WORK IF NOTHING HEREreturn readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            sc.next();                                // read and throw away the closing ')'
            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
                                                      // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }
}