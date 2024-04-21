/*
 * Program: Ship.java
 * Author: Noah Levy
 * What the program does: This is the class that controls a ship object, handling it's turning,
 * drawing, moving, etc. All speed, accleration, size, etc variables are in pixes, and rotation
 * is in degrees
 */

import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Ship {
    public double globalCenterX = 400, globalCenterY = 300, rotation = 0, maximumSpeed = 20, maximumThrust = 0.15, deathVerticiesSpeed = 2; //stores speed, position, rotation in pixels and degrees
    public PolarCoords[] verticies = new PolarCoords[10]; //vectors that make up shape
    private PolarCoords[] deathVerticiesVelocities = new PolarCoords[10]; //used for death animation
    private ArrayList<ArrayList<Integer>> deathVerticies =  new ArrayList<>(); //stores points of different vectors on ship for death animation
    private PolarCoords thrustDirection = null; //acceleration vector
    public PolarCoords heading = new PolarCoords(globalCenterX, globalCenterY, 0, 0); //direction the ship is currently facing
    private int[] xCoords = new int[10]; //xcoords and ycoords for drawing purposes
    private int[] yCoords = new int[10];
    private int[] thrusterXCoords = new int[10]; //xcoords and ycoords for drawing purposes
    private int[] thrusterYCoords = new int[10];
    public Polygon myPoly = new Polygon(xCoords, yCoords, 4); //used for collision
    public boolean show = true, deathAnimationInitalized = false, deathAnimationActive = false, isInvincible = false, isThrusting = false, showThrust = false; //flags for animations
    private int deathAnimationDelay = 120; //bellow show frame timers for different ship animations
	private int deathAnimationFrameCount = 0;
    public  int invincibilityCount = 0;
    private int invincibilityDelay = 180;
    private int numOfShipVerticies = 4; //used to clarify some magic numbers
    private int numOfThrusterVerticies = 4;
    private int thrusterFrameCount = 4;
    private double scaleFactor = 1; //how big we want the ship
    public Ship() {
        //SHIP PART
        verticies[0] = new PolarCoords(globalCenterX, globalCenterY, rotation, 30*scaleFactor);
        verticies[1] = new PolarCoords(globalCenterX, globalCenterY, rotation+120, 30*scaleFactor);
        verticies[2] = new PolarCoords(globalCenterX, globalCenterY, rotation+180, 10*scaleFactor);
        verticies[3] = new PolarCoords(globalCenterX, globalCenterY, rotation+240, 30*scaleFactor);
        //THRUSTER PART
        verticies[4] = new PolarCoords(globalCenterX, globalCenterY, rotation+120, 20*scaleFactor);
        verticies[5] = new PolarCoords(globalCenterX, globalCenterY, rotation+180, 40*scaleFactor);
        verticies[6] = new PolarCoords(globalCenterX, globalCenterY, rotation+240, 20*scaleFactor);
        verticies[7] = new PolarCoords(globalCenterX, globalCenterY, rotation+180, 10*scaleFactor);
        for (int i = 0;  i < 4; i++) {
            xCoords[i] = (int)verticies[i].globalX; //sets xcoords and ycoords
            yCoords[i] = (int)verticies[i].globalY;
        }
        //ensures maximumthrust stays under maxiumum thrust value
        thrustDirection = PolarCoords.normalizedReturned(verticies[0], maximumThrust);
    }
    public void turn(double degrees) {
        //standard code for turning a polygon
        rotation+=degrees;
        for (int i = 0; i < numOfShipVerticies+numOfThrusterVerticies; i++) {
            verticies[i].turn(degrees);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        //also rotate thrustDirection vector
        thrustDirection.turn(degrees);
    }
    //method to move the ship
    public void move() {
        //move ships to new desired coordinates and handle screen wrapping
        moveShipAndVerticiesToCoords(globalCenterX+(heading.globalX-heading.localX), globalCenterY+(heading.globalY-heading.localY));
        keepInBounds();
        //used for handling collision
        myPoly = new Polygon(xCoords, yCoords, 4);
        //update heading and thrust coordinates to new local references
        heading.setNewLocalCoords(globalCenterX, globalCenterY);
        thrustDirection.setNewLocalCoords(globalCenterX, globalCenterY);
        applyFriction();
    }
    //add acceleration vector
    public void applyThrusters() {
        thrusterFrameCount++;
        heading = PolarCoords.add(heading, thrustDirection, maximumSpeed);
        isThrusting = true;
    }
    //slightly lower magnitude each frame to act as friction
    private void applyFriction() {
        if (heading.magnitude>0.01){
            heading.normalize(heading.magnitude*0.99);
        }
        else {
            heading.magnitude = 0;
        }
    }
    //handle screen wrapping
    private void keepInBounds() {
        if (globalCenterX > GamePanel.WIDTH) {
            globalCenterX -= GamePanel.WIDTH;
            for (int i = 0; i < 8; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX-GamePanel.WIDTH, verticies[i].localY);
            }
        }
        if (globalCenterY > GamePanel.HEIGHT) {
            globalCenterY -= GamePanel.HEIGHT;
            for (int i = 0; i < 8; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX, verticies[i].localY-GamePanel.HEIGHT);
            }
        }
        if (globalCenterX < 0) {
            globalCenterX += GamePanel.WIDTH;
            for (int i = 0; i < 8; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX+GamePanel.WIDTH, verticies[i].localY);
            }
        }
        if (globalCenterY < 0) {
            globalCenterY += GamePanel.HEIGHT;
            for (int i = 0; i < 8; i++) {
                verticies[i].setNewLocalCoords(verticies[i].localX, verticies[i].localY+GamePanel.HEIGHT);
            }
        }
    }
    //moves ship reference and updates verticies accordingly
    public void moveShipAndVerticiesToCoords(double xPos, double yPos) {
        globalCenterX = xPos;
        globalCenterY = yPos;
        for (int i = 0; i < numOfShipVerticies+numOfThrusterVerticies; i++) {
            verticies[i].setNewLocalCoords(xPos, yPos);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
    }
    //this function handles logic with animation states and simply drawing the ship
    public void drawSelf(Graphics g, int invincibilityD) {
        if (!deathAnimationActive) {
            //handles flashing effect of ship
            if (show) {
                g.drawPolygon(myPoly);
                g.drawLine((int)verticies[0].localX, (int)verticies[0].localY, (int)verticies[0].globalX, (int)verticies[0].globalY);
                g.drawOval((int)globalCenterX - 5, (int)globalCenterY - 5, 10, 10);
            }
            //flashes every 5 frames
            show = invincibilityD % 5 == 4 ? !show : show;
            showThrust = thrusterFrameCount % 3 == 0 ? !showThrust : showThrust;
            if (isInvincible) {
                showThrust = show;
            }
            //show thrusting animation if actually thrusting, draw polygon seperately from the ship
            if (isThrusting && showThrust) {
                for (int i = numOfShipVerticies; i < numOfShipVerticies+numOfThrusterVerticies; i++) {
                    thrusterXCoords[i-4] = (int)verticies[i].globalX;
                    thrusterYCoords[i-4] = (int)verticies[i].globalY;
                }
                g.setColor(Color.ORANGE);
                g.drawPolygon(thrusterXCoords, thrusterYCoords, 4);
            }
        }
    }
    //this function handles running the death animation and it's respective timers
    public void runDeathAnimation(Graphics g) {
        //flags so GamePanel.java knows when to run animations
        deathAnimationActive = true;
        isInvincible = true;
        if (!deathAnimationInitalized) {
            //create verticies that will move when the ship dies
            deathVerticies.clear();
            createDeathVerticies();
            deathAnimationInitalized = true;
        }
        deathAnimationFrameCount++;
        //handles logic when the death animation is over
        if (deathAnimationFrameCount >= deathAnimationDelay) {
            deathAnimationFrameCount = 0;
            deathAnimationInitalized = false;
            deathAnimationActive = false;
            if (GamePanel.lives <= 0) {
                GamePanel.state = "game over";
                GamePanel.currentLevel = 0;
                return;
            }
            //moveShipAndVerticiesToCoords(GamePanel.WIDTH/2, GamePanel.HEIGHT/2);
            runInvincibility();
        }
        g.setColor(Color.WHITE);
        ArrayList<Integer> point;
        for (int i = 0; i < deathVerticies.size()-1; i+=2) {
            //gets each coordinate of the verticies and moves them by a set x component and y component
            //draws the line shortly afterwards
            int x1 = (deathVerticies.get(i).get(0))+(int)(deathVerticiesVelocities[i].globalX-deathVerticiesVelocities[i].localX);
            int y1 = (deathVerticies.get(i).get(1))+(int)(deathVerticiesVelocities[i].globalY-deathVerticiesVelocities[i].localY);
            int x2 = (deathVerticies.get(i+1).get(0))+(int)(deathVerticiesVelocities[i].globalX-deathVerticiesVelocities[i].localX);
            int y2 = (deathVerticies.get(i+1).get(1))+(int)(deathVerticiesVelocities[i].globalY-deathVerticiesVelocities[i].localY);
            g.drawLine(x1, y1, x2, y2);
            point = new ArrayList<>(Arrays.asList(x1, y1));
            deathVerticies.set(i, point);
            point = new ArrayList<>(Arrays.asList(x2, y2));
            deathVerticies.set(i+1, point);
        }

    }
    //gets each corner of the ship and adds it to the deathVerticies arraylist
    private void createDeathVerticies() {
        Random rand = new Random();
        //add to deathVerticies list each coordinate of the verticies, so lines can later be drawn
        ArrayList<Integer> point;
        int verticiesIndex = 0;
        for (verticiesIndex = 0; verticiesIndex < numOfShipVerticies-1; verticiesIndex++) {
            point = new ArrayList<>(Arrays.asList((int)verticies[verticiesIndex].globalX, (int)verticies[verticiesIndex].globalY));
            deathVerticies.add(point);
            point = new ArrayList<>(Arrays.asList((int)verticies[verticiesIndex+1].globalX, (int)verticies[verticiesIndex+1].globalY));
            deathVerticies.add(point);
        } 
        //add extra points so the death lines actually wrap around the ship
        point = new ArrayList<>(Arrays.asList((int)verticies[verticiesIndex].globalX, (int)verticies[verticiesIndex].globalY));
        deathVerticies.add(point);
        point = new ArrayList<>(Arrays.asList((int)verticies[0].globalX, (int)verticies[0].globalY));
        deathVerticies.add(point);
        deathVerticiesVelocities = new PolarCoords[20];
        //create random velocities for each death line
        for (int i = 0; i < deathVerticies.size(); i++) {
            deathVerticiesVelocities[i] = new PolarCoords(0, 0, rand.nextDouble()*360, 2);
        }
    }
    //handles invincibility flags and logic depending on framecount and the lifespan of this animation
    public void runInvincibility() {
		isInvincible = true;
		invincibilityCount++;
		if (invincibilityCount >= invincibilityDelay) {
			invincibilityCount = 0;
			isInvincible = false;
		}
	}
}
