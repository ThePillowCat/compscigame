/*
 * Program: UFO.java
 * Author: Noah Levy
 * This file is the class for UFO objects. It controls their size, velocity, and AI.
 * It also has everything needed to draw the UFO with PolarCoords, and has logic to 
 * fire at the player, with the desired angle needed being obtained through Math.atan2
 */

import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Random;

public class UFO {
    private int globalCenterX, globalCenterY, numOfVerticies = 9; //handles position measured in pixles
    public final int MAXSPEED = 2;
    private int[] xCoords = new int[10]; //used for drawing purposes (verticies)
    private int[] yCoords = new int[10];
    public Polygon myPoly = null; //used for colision
    private PolarCoords direction = null;
    private PolarCoords[] verticies = new PolarCoords[10]; //actual vectors that make up shape
    private Random rand = new Random();
    private double scaleFactor = rand.nextDouble()*2+1; //create a random scale
    private int desiredX, desiredY;

    public UFO (int x, int y) {
        globalCenterX = x; //construct UFO and pre-determined settings
        globalCenterY = y;
        verticies[0] = new PolarCoords(globalCenterX, globalCenterY, 20, 10*scaleFactor);
        verticies[1] = new PolarCoords(globalCenterX, globalCenterY, 70, 20*scaleFactor);
        verticies[2] = new PolarCoords(globalCenterX, globalCenterY, 110, 20*scaleFactor);
        verticies[3] = new PolarCoords(globalCenterX, globalCenterY, 160, 10*scaleFactor);
        verticies[4] = new PolarCoords(globalCenterX, globalCenterY, 190, 25*scaleFactor);
        verticies[5] = new PolarCoords(globalCenterX, globalCenterY, 230, 25*scaleFactor);
        verticies[6] = new PolarCoords(globalCenterX, globalCenterY, 270, 19*scaleFactor);
        verticies[7] = new PolarCoords(globalCenterX, globalCenterY, 310, 25*scaleFactor);
        verticies[8] = new PolarCoords(globalCenterX, globalCenterY, 350, 25*scaleFactor);
        for (int i = 0;  i < 9; i++) {
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies); //update polygon object
        createNewHeading(); //create a new heading so the UFO goes to a random point on the screen
    }
    //draw the ship
    public void drawSelf(Graphics g) {
        g.drawPolygon(xCoords, yCoords, numOfVerticies);
        g.drawLine((int)verticies[0].globalX, (int)verticies[0].globalY, (int)verticies[3].globalX, (int)verticies[3].globalY);
        g.drawLine((int)verticies[4].globalX, (int)verticies[4].globalY, (int)verticies[8].globalX, (int)verticies[8].globalY);
    }
    public void moveSelf() {
        for (int i = 0; i < numOfVerticies; i++) {
            //set each vector to new local references
            //also add current position by x and y components of velocity vector
            double newX = verticies[i].localX+direction.globalX-direction.localX;
            double newY = verticies[i].localY+direction.globalY-direction.localY;
            verticies[i].setNewLocalCoords(newX, newY);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        //update center
        globalCenterX = (int)verticies[0].localX;
        globalCenterY = (int)verticies[0].localY;
        //handle screen wrapping
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
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies);
        //if somewhat close to desired point, create a new random point
        if (Math.hypot((globalCenterX-desiredX), (globalCenterY-desiredY)) < 10) {
            createNewHeading();
        }
    }
    //gets x and y component of vector from center of UFO to player center
    //use math.atan2 to get desired angle and add a bullet going the correct velocity
    public void fireAtPlayer(ArrayList<Bullet> bulletList, Ship playerShip) {
        double netXComponent = -globalCenterX+playerShip.globalCenterX;
        double netYComponent = globalCenterY-playerShip.globalCenterY;
        double angleToPlayer = Math.toDegrees(Math.atan2(netYComponent, netXComponent));
        PolarCoords bulletDirection = new PolarCoords(globalCenterX, globalCenterY, angleToPlayer, Bullet.MAXUFOSPEED);
        bulletList.add(new Bullet(globalCenterX, globalCenterY, bulletDirection, true));
    }
    private void createNewHeading() {
        //create vector pointing towards random point on screen
        //again use math.atan2
        desiredX = (int)(rand.nextDouble()*GamePanel.WIDTH);
        desiredY = (int)(rand.nextDouble()*GamePanel.HEIGHT);
        double netXComponent = -globalCenterX+desiredX;
        double netYComponent = globalCenterY-desiredY;
        double angleToDesiredPoint = Math.toDegrees(Math.atan2(netYComponent, netXComponent));
        direction = new PolarCoords(globalCenterX, globalCenterY, angleToDesiredPoint, rand.nextDouble()*3+1);
    }
}
