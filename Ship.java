import java.awt.*;
import java.awt.geom.*;

public class Ship {
    static int lives = 3;
    double globalCenterX = 400, globalCenterY = 300, rotation = 0, maximumSpeed = 20, maximumThrust = 0.15;
    PolarCoords[] verticies = new PolarCoords[4];
    PolarCoords thrustDirection = null;
    PolarCoords heading = new PolarCoords(globalCenterX, globalCenterY, 0, 0);
    int[] xCoords = new int[4];
    int[] yCoords = new int[4];
    Polygon myPoly = new Polygon(xCoords, yCoords, verticies.length);
    Area myArea = new Area(myPoly);
    Ship() {
        verticies[0] = new PolarCoords(globalCenterX, globalCenterY, rotation, 30);
        verticies[1] = new PolarCoords(globalCenterX, globalCenterY, rotation+120, 30);
        verticies[2] = new PolarCoords(globalCenterX, globalCenterY, rotation+180, 10);
        verticies[3] = new PolarCoords(globalCenterX, globalCenterY, rotation+240, 30);
        for (int i = 0;  i < verticies.length; i++) {
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        thrustDirection = PolarCoords.normalizedReturned(verticies[0], maximumThrust);
    }
    void turn(double degrees) {
        rotation+=degrees;
        for (int i = 0; i < verticies.length; i++) {
            verticies[i].turn(degrees);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        thrustDirection.turn(degrees);
    }
    void move() {
        // for (int i = 0; i < verticies.length; i++) {
        //     double newX = verticies[i].localX+heading.globalX-heading.localX;
        //     double newY = verticies[i].localY+heading.globalY-heading.localY;
        //     verticies[i].setNewLocalCoords(newX, newY);
        //     xCoords[i] = (int)verticies[i].globalX;
        //     yCoords[i] = (int)verticies[i].globalY;
        // }
        // globalCenterX = (int)verticies[0].localX;
        // globalCenterY = (int)verticies[0].localY;
        moveShipAndVerticiesToCoords(globalCenterX+(heading.globalX-heading.localX), globalCenterY+(heading.globalY-heading.localY));
        keepInBounds();
        myPoly = new Polygon(xCoords, yCoords, verticies.length);
        myArea = new Area(myPoly);
        heading.setNewLocalCoords(globalCenterX, globalCenterY);
        thrustDirection.setNewLocalCoords(globalCenterX, globalCenterY);
        applyFriction();
    }
    public void applyThrusters() {
        heading = PolarCoords.add(heading, thrustDirection, maximumSpeed);
    }
    private void applyFriction() {
        if (heading.magnitude>0.01){
            heading.normalize(heading.magnitude*0.99);
        }
        else {
            heading.magnitude = 0;
        }
    }
    private void keepInBounds() {
        int numOfVerticies = verticies.length;
        if (globalCenterX > 800) {
            globalCenterX -= 800;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX-800, verticies[i].localY);
            }
        }
        if (globalCenterY > 600) {
            globalCenterY -= 600;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX, verticies[i].localY-600);
            }
        }
        if (globalCenterX < 0) {
            globalCenterX += 800;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX+800, verticies[i].localY);
            }
        }
        if (globalCenterY < 0) {
            globalCenterY += 600;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX, verticies[i].localY+600);
            }
        }
    }
    void moveShipAndVerticiesToCoords(double xPos, double yPos) {
        globalCenterX = xPos;
        globalCenterY = yPos;
        for (int i = 0; i < verticies.length; i++) {
            verticies[i].setNewLocalCoords(xPos, yPos);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
    }
    void drawSelf(Graphics g) {
        g.drawPolygon(myPoly);
    }
}
