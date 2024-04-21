/*
 * Program: Asteroid.java
 * Author: Noah Levy
 * This is a class for handling asteroid objects in the game. It handles their 
 * rotation, velocities, sizes, and more. Units are in pixels (except rotation, which is degrees)
 * Each of these objects is in an arraylist in GamePanel.java
 */

import java.util.Random;
import java.awt.*;

public class Asteroid {
    private PolarCoords[] verticies = new PolarCoords[20]; //verticies that make up asteroid shape
    public double globalCenterX, globalCenterY, rotation = 0, maximumSpeed = 10, maximumThrust = 0.2; //controls position and speed (in pixels) as well as rotation in degrees
    public int [] xCoords = new int[20], yCoords = new int[20]; //for drawing purposes, coords of each verticies
    public int numOfVerticies = 0, minimumRadius, varient; //these are to avoid magic numbers
    public PolarCoords direction = null; //velocity of asteroid
    public Polygon myPoly = null;
    private Random rand = new Random();

    public Asteroid(PolarCoords d, int minRad, int v) {
        varient = v;
        direction = d;
        globalCenterX = d.localX;
        globalCenterY = d.localY;
        minimumRadius = minRad;
        numOfVerticies = constructAsteroidShape();
        //get x and y coordinates of vectors for drawing purposes
        for (int i = 0;  i < numOfVerticies; i++) {
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        //these objects are for collision
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies);
    }
    public void move() {
        //turn by a certain amount of degrees, add x and y components of velocity vector to position
        turn(0.5);
        for (int i = 0; i < numOfVerticies; i++) {
            double newX = verticies[i].localX+direction.globalX-direction.localX;
            double newY = verticies[i].localY+direction.globalY-direction.localY;
            //setNewLocalCoords automatically moves the vector in global space and does other necessary math
            verticies[i].setNewLocalCoords(newX, newY);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        //get new center
        globalCenterX = (int)verticies[0].localX;
        globalCenterY = (int)verticies[0].localY;
        //the bellow lines handle screen wrapping
        if (globalCenterX > GamePanel.WIDTH) {
            globalCenterX -= GamePanel.WIDTH;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX-GamePanel.WIDTH, verticies[i].localY);
            }
        }
        if (globalCenterY > GamePanel.HEIGHT) {
            globalCenterY -= GamePanel.HEIGHT;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX, verticies[i].localY-GamePanel.HEIGHT);
            }
        }
        if (globalCenterX < 0) {
            globalCenterX += GamePanel.WIDTH;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX+GamePanel.WIDTH, verticies[i].localY);
            }
        }
        if (globalCenterY < 0) {
            globalCenterY += GamePanel.HEIGHT;
            for (int i = 0; i < numOfVerticies; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX, verticies[i].localY+GamePanel.HEIGHT);
            }
        }
        //update collision objects with new coordinates
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies);
    }
    //goes around 360 degrees and constructs random asteroid points, ensuring minimum distances
    //between asteroid center and 
    private int constructAsteroidShape() {
        int angle = 0, currentIndex = 0;
        while (angle < 360) {
            verticies[currentIndex] = new PolarCoords(globalCenterX, globalCenterY, angle, rand.nextDouble()*(minimumRadius*((double)(3/2)))+minimumRadius);
            angle+=rand.nextDouble()*40+20; //ratio should be 3 - 2
            currentIndex+=1;
        }
        return currentIndex-1;
    }
    //standard code for rotating several verticies at once
    public void turn(double degrees) {
        rotation+=degrees;
        for (int i = 0; i < numOfVerticies; i++) {
            //turn is part of polarcoords class, rotates vector
            verticies[i].turn(degrees);
            //recalculate corner x and y positions
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
    }
}
