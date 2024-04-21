/*
 * Program: PolarCoords.java
 * Author: Noah Levy
 * This class is the bread and butter of the whole asteroids game. It is like a vector, except the tip and tail are stored
 * on a cartesian plane, and the tip is in reference to the tail based on a rotation in degrees. 
 * There are methods for rotating, translating, resizing vectors, etc.
 */

public class PolarCoords {
    //localX - X coordinate reference point
    //localY - Y coordinate reference point
    //globalX and globalY are calculated based on current rotation in relation to local coordinates
    //variables are measured in pixels
    public double rotation, magnitude, localX, localY, globalX, globalY;
    public PolarCoords(double x, double y, double rotation, double magnitude) {
        this.localX = x;
        this.localY = y;
        this.rotation = rotation;
        this.magnitude = magnitude;
        //handle rotating outer coordinates
        this.globalX = localX+magnitude*Math.cos(Math.toRadians(this.rotation));
        this.globalY = localY-magnitude*Math.sin(Math.toRadians(this.rotation));
    }
    //recalculate global coordinates based on new angle
    public void turn(double degrees) {
        this.rotation += degrees;
        recalculateGlobalCoords();
    }
    //logic for adding two vectors
    public static PolarCoords add(PolarCoords p1, PolarCoords p2, double mag) {
        double netXComponenet = ((p2.globalX-p2.localX)+(p1.globalX-p1.localX));
        double netYComponent = (-(p2.globalY-p2.localY)-(p1.globalY-p1.localY));//negative Y because y increases downwards
        double angle = (Math.toDegrees(Math.atan2(netYComponent, netXComponenet))); //calculate angle formed from net X and Y components
        double resultantMagnitude = distance(0, 0, netXComponenet, netYComponent);
        return new PolarCoords(p1.localX, p1.localY, angle, Math.min(mag, resultantMagnitude)); //returns the added vector, but no larger then the maximum allowed magnitude
    }
    //similar to below function, except a resultant vector is returned
    public static PolarCoords normalizedReturned(PolarCoords p1, double mag) {
        if (distance(p1.localX, p1.localY, p1.globalX, p1.globalY) > mag) {
            return new PolarCoords(p1.localX, p1.localY, p1.rotation, mag);
        }
        return new PolarCoords(p1.localX, p1.localY, p1.rotation, p1.magnitude);
    }
    //normalize DOES NOT SET THE VECTOR TO A MAGNITUDE OF 1
    //it simply ensures the size of a vector is under a threshold
    public void normalize(double mag) {
        if (distance(localX, localY, globalX, globalY) > mag) {
            magnitude = mag;
            recalculateGlobalCoords();
        }
    }
    public PolarCoords resizeVectorReturned(double mag) {
        return new PolarCoords(localX, localY, rotation, mag);
    }
    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    } 
    //does the triginometry to calculate new verticies
    private void recalculateGlobalCoords() {
        this.globalX = localX+magnitude*Math.cos(Math.toRadians(this.rotation));
        this.globalY = localY-magnitude*Math.sin(Math.toRadians(this.rotation));
    }
    //set new reference coordinates
    public void setNewLocalCoords(double newX, double newY) {
        this.localX = newX;
        this.localY = newY;
        recalculateGlobalCoords();
    }
}
