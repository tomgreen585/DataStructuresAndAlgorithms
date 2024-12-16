// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 2
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;
    
    //main methods including from class
    private Stack<Stack<Stroke>> undoStrokes = new Stack<>();
    Stack<Stroke>Inbetween = new Stack<>();
    private Stack<Stack<Stroke>> redoStrokes = new Stack<>();
    Stack<Stroke> Strokes = new Stack<>();
   
    //challenge tings!
    private Color lineColor = Color.black;
    private double lineWidth = 1;

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Line Color", this::setLineColor);
        UI.addSlider("Line Width", 0,10, this::setLineWidth);
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
        UI.addButton("Quit", UI::quit);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * set line width based on the slider called in the setupGUI method
     */
    public void setLineWidth(double value){
        lineWidth = value;
    }
    
    /**
     * set line color when running program using the button called within the setupGUI method
     */
    public void setLineColor(){
        this.lineColor = JColorChooser.showDialog(null, "Choose Color", lineColor);
    }
    
    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        UI.setLineWidth(this.lineWidth);
        UI.setColor(this.lineColor);
        if (action.equals("pressed")){
            lastX = x;
            lastY = y;
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);
            Strokes.push(new Stroke (lastX, lastY, x, y, lineWidth, lineColor));
            lastX = x;
            lastY = y;
        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
            Strokes.push(new Stroke (lastX, lastY, x, y, lineWidth, lineColor));
            undoStrokes.push(Strokes);
            UI.println("Amount of strokes" + Strokes.size());
            UI.println("Round of undo actions" + undoStrokes.size());
            Strokes = new Stack<Stroke>();
            redoStrokes.clear();
        }
    }
    
    /**
     * method to undo the users actions
     */
    public void undo(){
        if (undoStrokes.isEmpty() == false){
        Stack<Stroke>UNDO = undoStrokes.pop();
        while (UNDO.isEmpty() == false){
            Stroke s = UNDO.pop();
            double X = s.returnendX();
            double Y = s.returnendY();
            double x = s.returnX();
            double y = s.returnY();
            double lineW = s.returnSize();
            Color col = s.returnCol();
            UI.setLineWidth(lineW);
            UI.setColor(col);
            //list or an array
            //list of strokes
            UI.eraseLine(X,Y,x,y);
            Inbetween.push(s);
        }
        redoStrokes.push(Inbetween);
        Inbetween = new Stack<Stroke>();
        }
    }
    
    /**
     * method to redo the users actions after using undo
     */
    public void redo(){
        if(redoStrokes.isEmpty() == false){
            Stack<Stroke> REDO = redoStrokes.pop();
            while (REDO.isEmpty() == false){
                Stroke z = REDO.pop();
                double X = z.returnendX();
                double Y = z.returnendY();
                double x = z.returnX();
                double y = z.returnY();
                double lineW = z.returnSize();
                Color col = z.returnCol();
                UI.setLineWidth(lineW);
                UI.setColor(col);
                
                UI.drawLine(X,Y,x,y);
                Inbetween.push(z);
            }
            undoStrokes.push(Inbetween);
            Inbetween = new Stack<Stroke>();
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }
}
