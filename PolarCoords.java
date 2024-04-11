import java.util.*;

public class PolarCoords {
    double rotation, magnitude, localX, localY, globalX, globalY;
    PolarCoords(double x, double y, double rotation, double magnitude) {
        this.localX = x;
        this.localY = y;
        this.rotation = rotation;
        this.magnitude = magnitude;
        this.globalX = localX+magnitude*Math.cos(Math.toRadians(this.rotation));
        this.globalY = localY-magnitude*Math.sin(Math.toRadians(this.rotation));
    }
    void turn(double degrees) {
        this.rotation += degrees;
        recalculateGlobalCoords();
    }
    static PolarCoords add(PolarCoords p1, PolarCoords p2, double mag) {
        double netXComponenet = ((p2.globalX-p2.localX)+(p1.globalX-p1.localX));
        double netYComponent = (-(p2.globalY-p2.localY)-(p1.globalY-p1.localY));
        double angle = (Math.toDegrees(Math.atan2(netYComponent, netXComponenet)));
        double resultantMagnitude = distance(0, 0, netXComponenet, netYComponent);
        return new PolarCoords(p1.localX, p1.localY, angle, Math.min(mag, resultantMagnitude));
    }
    static PolarCoords normalizedReturned(PolarCoords p1, double mag) {
        if (distance(p1.localX, p1.localY, p1.globalX, p1.globalY) > mag) {
            return new PolarCoords(p1.localX, p1.localY, p1.rotation, mag);
        }
        return new PolarCoords(p1.localX, p1.localY, p1.rotation, p1.magnitude);
    }
    void normalize(double mag) {
        if (distance(localX, localY, globalX, globalY) > mag) {
            magnitude = mag;
            recalculateGlobalCoords();
        }
    }
    PolarCoords resizeVectorReturned(double mag) {
        return new PolarCoords(localX, localY, rotation, mag);
    }
    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    } 
    private void recalculateGlobalCoords() {
        this.globalX = localX+magnitude*Math.cos(Math.toRadians(this.rotation));
        this.globalY = localY-magnitude*Math.sin(Math.toRadians(this.rotation));
    }
    void setNewLocalCoords(double newX, double newY) {
        this.localX = newX;
        this.localY = newY;
        recalculateGlobalCoords();
    }
}
