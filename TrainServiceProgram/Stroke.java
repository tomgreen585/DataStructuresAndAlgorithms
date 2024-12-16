import ecs100.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JColorChooser;

/**
 * Write a description of class Stroke here.
 *
 * @author greenthom
 * draw action method (class)
 */
public class Stroke{
    
    private double X;
    private double Y;
    private double endX;
    private double endY;
    private double Lw;
    private Color C;
    
    /**
     * Constructor for objects of class Stroke
     */
    public Stroke (double x, double y, double lastX, double lastY, double lineWidth, Color col ) { //List<Double>x, List<Double>y
        X = x;
        Y = y;
        endX = lastX;
        endY = lastY;
        Lw = lineWidth;
        C = col;
    }
    
    /**
     * variable of x used during stroke
     */
    public double returnX(){
        return X;
    }
    
    /**
     * variable of y used during stroke
     */
    public double returnY(){
        return Y;
    }
    /**
     * variable of the end point of x during stroke
     */
    public double returnendX(){
        return endX;
    }
    
    /**
     * variable of the end point of y during stroke
     */
    public double returnendY(){
        return endY;
    }
    
    /**
     * returned size of line stroke during stroke
     */
    public double returnSize(){
        return Lw;
    }
    
    /**
     * returned color of line stroke during stroke
     */
    public Color returnCol(){
        return C;
    }

}
