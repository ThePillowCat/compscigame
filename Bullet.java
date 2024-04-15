import java.util.ArrayList;
import java.util.Arrays;
import java.awt.geom.*;
import java.awt.*;

public class Bullet {
    int x, y, width = 10, height = 10, rightX, downX;
    boolean isUFOBullet = false;
    public static final int MAXPEED = 10;
    public static final int MAXUFOSPEED = 2;
    PolarCoords direction = null;
    int[] xCoords = new int[4];
    int[] yCoords = new int[4];
    Polygon myPoly = new Polygon(xCoords, yCoords, 4);
    Area myArea = new Area(myPoly);
    int bulletLife = 60;
    int bulletLifeFrameCount = 0;
    Bullet(int xPos, int yPos, PolarCoords d, boolean isBullet) {
        x = xPos;
        y = yPos;
        direction = d;
        isUFOBullet = isBullet;
        d.normalize(MAXPEED);
    }
    void move(int curIndex, ArrayList<Bullet> a) {
        bulletLifeFrameCount++;
        x+=(direction.globalX-direction.localX);
        y+=(direction.globalY-direction.localY);
        rightX = x+width;
        downX = y+height;
        int[] xCoords = {x, (x+width), x, (x+width)};
        int[] yCoords = {y, y, (y+height), (y+height)};
        myPoly = new Polygon(xCoords, yCoords, 4);
        myArea = new Area(myPoly);
        if (x > 800) {
            x = 0;
        }
        if (x < 0) {
            x = 800;
        }
        if (y < 0) {
            y = 600;
        }
        if (y > 600) {
            y = 0;
        }
        if (bulletLifeFrameCount >= bulletLife) {
            a.remove(curIndex);
        }
    }
}
