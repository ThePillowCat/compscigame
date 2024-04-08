import java.util.*;

public class PolarCoords {
    public double rotation, magnitude, localX, localY, globalX, globalY;
    PolarCoords(double x, double y, double rotation, double magnitude) {
        this.localX = x;
        this.localY = y;
        this.rotation = rotation;
        this.magnitude = magnitude;
        this.globalX = localX+magnitude*Math.cos(Math.toRadians(this.rotation));
        this.globalY = localY-magnitude*Math.sin(Math.toRadians(this.rotation));
    }
    void turn(int degrees) {
        this.rotation += degrees;
        recalculateGlobalCoords();
    }
    static PolarCoords add(PolarCoords p1, PolarCoords p2, int mag) {
        int netXComponenet = (int)((p2.globalX-p2.localX)+(p1.globalX-p1.localX));
        int netYComponent = (int)(-(p2.globalY-p2.localY)-(p1.globalY-p1.localY));
        int angle = (int)Math.toDegrees(Math.atan2(netYComponent, netXComponenet));
        return new PolarCoords((int)p1.localX, (int)p1.localY, angle, mag);
    }
    static PolarCoords normalizedReturned(PolarCoords p1, double mag) {
        if (distance(p1.localX, p1.localY, p1.globalX, p1.globalY) > mag) {
            return new PolarCoords((int)p1.localX, (int)p1.localY, (int)p1.rotation, (int)mag);
        }
        return new PolarCoords((int)p1.localX, (int)p1.localY, (int)p1.rotation, (int)p1.magnitude);
    }
    void normalize(double mag) {
        if (distance(localX, localY, globalX, globalY) > mag) {
            magnitude = mag;
            recalculateGlobalCoords();
        }
    }
    void printCoordinates() {
        System.out.println("GlobalX: " + globalX + ", GlobalY: " + globalY);
    }
    static double distance(double x1, double x2, double y1, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    } 
    void recalculateGlobalCoords() {
        this.globalX = localX+magnitude*Math.cos(Math.toRadians(this.rotation));
        this.globalY = localY-magnitude*Math.sin(Math.toRadians(this.rotation));
    }
}
