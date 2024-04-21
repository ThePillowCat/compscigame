/*
 * Program: Particle.java
 * Author: Noah Levy
 * This class handles particle objects in the game. It handles velocity, rotation, speed, etc in meters
 * and degrees for each object. Each particle object is stored in the activeParticles array list in
 * GamePanel.java
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Particle {
    public static final int ASTEROIDPARTICLESPEED = 2; //speed and radius of particles in pixels
    public static final int RADIUS = 3;
    private int centralX, centralY; //position
    private Color myColor = null;
    private PolarCoords currentDirection; //current velocity
    private int particleDelay = 30; //controls particle lifetime
    private int particleFrameCount = 0;
    public Particle(int x, int y, PolarCoords d, Color c) {
        centralX = x;
        centralY = y;
        currentDirection = d;
        myColor = c;
    }
    public void moveAndDrawSelf(Graphics g, ArrayList<Particle> particles, int index) {
        //draw self
        g.setColor(myColor);
        g.drawOval(centralX, centralY, RADIUS*2, RADIUS*2);
        //destory particle if it's lifetime is over
        particleFrameCount++;
        if (particleFrameCount > particleDelay) {
            particles.remove(index);
        }
        //move particle by x and y components of velocity vector
        centralX += currentDirection.globalX-currentDirection.localX;
        centralY += currentDirection.globalY-currentDirection.localY;
    }
}
