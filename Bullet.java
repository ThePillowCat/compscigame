import java.util.ArrayList;
import java.awt.*;

/*
 * Program: Bullet.java
 * Author: Noah Levy
 * This is a class for handling bullet objects in the game. It handles their 
 * rotation, velocities, sizes, and more. Units are in pixels.
 * Each of these objects is in an arraylist in GamePanel.java
 */

public class Bullet {
    public int x, y, width = 10, height = 10, rightX, downX; //position and heights of bullets in pixels
    public boolean isUFOBullet = false;
    public static final int MAXPEED = 10;
    public static final int MAXUFOSPEED = 7;
    private PolarCoords direction = null; //bullet velocity
    private int[] xCoords = new int[4]; //x coords and y coords for drawing
    private int[] yCoords = new int[4];
    public Polygon myPoly = new Polygon(xCoords, yCoords, 4); //polygon for collision
    private int bulletLifeUFO = 60; //#of frames until UFO bullet deletion
    private int bulletLifeUFOFrameCount = 0; //UFO bullet time alive in frames
    private int bulletLife = 70; //#of frames until bullet deletion
    private int bulletLifeFrameCount = 0; //bullet time alive in frames
    public Bullet(int xPos, int yPos, PolarCoords d, boolean isBullet) {
        x = xPos;
        y = yPos;
        direction = d;
        isUFOBullet = isBullet;
        d.normalize(MAXPEED);
    }
    //handles movement and screen wrapping
    public void move(int curIndex, ArrayList<Bullet> a) {
        //controls lifespan between UFO bullet and normal bullet
        if (isUFOBullet) {
            bulletLifeUFOFrameCount++;
        }
        else {
            bulletLifeFrameCount++;
        }
        x+=(direction.globalX-direction.localX);//adds x component of velocity
        y+=(direction.globalY-direction.localY);//adds y component of velocity
        rightX = x+width;
        downX = y+height;
        int[] xCoords = {x, (x+width), x, (x+width)};//update drawing coords
        int[] yCoords = {y, y, (y+height), (y+height)};
        myPoly = new Polygon(xCoords, yCoords, 4);
        //handle screen wrapping
        if (x > GamePanel.WIDTH) {
            x = 0;
        }
        if (x < 0) {
            x = GamePanel.WIDTH;
        }
        if (y < 0) {
            y = GamePanel.HEIGHT;
        }
        if (y > GamePanel.HEIGHT) {
            y = 0;
        }
        //controls lifespan between UFO bullet and normal bullet
        if (!isUFOBullet && bulletLifeFrameCount >= bulletLife) {
            a.remove(curIndex);
        }
        if (isUFOBullet && bulletLifeUFOFrameCount >= bulletLifeUFO) {
            a.remove(curIndex);
        }
    }
}
