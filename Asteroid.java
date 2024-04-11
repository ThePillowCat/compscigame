import java.util.Random;
import java.awt.*;
import java.awt.geom.Area;

public class Asteroid {
    PolarCoords[] verticies = new PolarCoords[20];
    double globalCenterX, globalCenterY, rotation = 0, maximumSpeed = 10, maximumThrust = 0.2;
    int [] xCoords = new int[20], yCoords = new int[20];
    int numOfVerticies = 0, minimumRadius, varient;
    PolarCoords direction = null;
    Polygon myPoly = null;
    Area myArea = null;
    Random rand = new Random();

    Asteroid(PolarCoords d, int minRad, int v) {
        varient = v;
        direction = d;
        globalCenterX = d.localX;
        globalCenterY = d.localY;
        minimumRadius = minRad;
        numOfVerticies = constructAsteroidShape();
        for (int i = 0;  i < numOfVerticies; i++) {
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies);
        myArea = new Area(myPoly);
    }
    void move() {
        turn(0.5);
        for (int i = 0; i < numOfVerticies; i++) {
            double newX = verticies[i].localX+direction.globalX-direction.localX;
            double newY = verticies[i].localY+direction.globalY-direction.localY;
            verticies[i].setNewLocalCoords(newX, newY);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        globalCenterX = (int)verticies[0].localX;
        globalCenterY = (int)verticies[0].localY;
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
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies);
        myArea = new Area(myPoly);
    }
    int constructAsteroidShape() {
        int angle = 0, currentIndex = 0;
        while (angle < 360) {
            verticies[currentIndex] = new PolarCoords(globalCenterX, globalCenterY, angle, rand.nextDouble()*(minimumRadius*((double)(3/2)))+minimumRadius);
            angle+=rand.nextDouble()*40+20; //ratio should be 3 - 2
            currentIndex+=1;
        }
        return currentIndex-1;
    }
    void turn(double degrees) {
        rotation+=degrees;
        for (int i = 0; i < numOfVerticies; i++) {
            verticies[i].turn(degrees);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
    }
}
