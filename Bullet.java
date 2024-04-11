import java.util.ArrayList;
import java.util.Arrays;
import java.awt.geom.*;
import java.awt.*;

public class Bullet {
    int x, y, width = 10, height = 10, rightX, downX;
    public static final int MAXPEED = 10;
    PolarCoords direction = null;
    int[] xCoords = new int[4];
    int[] yCoords = new int[4];
    Polygon myPoly = new Polygon(xCoords, yCoords, 4);
    Area myArea = new Area(myPoly);
    Bullet(int xPos, int yPos, PolarCoords d) {
        x = xPos;
        y = yPos;
        direction = d;
        d.normalize(MAXPEED);
    }
    void move(int curIndex, ArrayList<Bullet> a) {
        x+=(direction.globalX-direction.localX);
        y+=(direction.globalY-direction.localY);
        rightX = x+width;
        downX = y+height;
        int[] xCoords = {x, (x+width), x, (x+width)};
        int[] yCoords = {y, y, (y+height), (y+height)};
        myPoly = new Polygon(xCoords, yCoords, 4);
        myArea = new Area(myPoly);
        if (x > 2000 || x < 0 || y > 1500 || y < 0) {
            a.remove(curIndex);
        }
    }
}
