import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public class UFO {
    int globalCenterX, globalCenterY, numOfVerticies = 9, rotation = 0;
    final int MAXSPEED = 5;
    int[] xCoords = new int[10];
    int[] yCoords = new int[10];
    Polygon myPoly = null;
    Area myArea = null;
    PolarCoords direction = null;
    PolarCoords[] verticies = new PolarCoords[10];

    UFO (int x, int y, PolarCoords d) {
        globalCenterX = x;
        globalCenterY = y;
        verticies[0] = new PolarCoords(globalCenterX, globalCenterY, 20, 10);
        verticies[1] = new PolarCoords(globalCenterX, globalCenterY, 70, 20);
        verticies[2] = new PolarCoords(globalCenterX, globalCenterY, 110, 20);
        verticies[3] = new PolarCoords(globalCenterX, globalCenterY, 160, 10);
        verticies[4] = new PolarCoords(globalCenterX, globalCenterY, 190, 25);
        verticies[5] = new PolarCoords(globalCenterX, globalCenterY, 230, 25);
        verticies[6] = new PolarCoords(globalCenterX, globalCenterY, 270, 19);
        verticies[7] = new PolarCoords(globalCenterX, globalCenterY, 310, 25);
        verticies[8] = new PolarCoords(globalCenterX, globalCenterY, 350, 25);
        for (int i = 0;  i < 9; i++) {
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        myPoly = new Polygon(xCoords, yCoords, numOfVerticies);
        direction = d.resizeVectorReturned(MAXSPEED);
        turn(direction.rotation);
    }
    void shootAtPlayer(ArrayList a, int playerX, int playerY) {

    }
    void drawSelf(Graphics g) {
        g.drawPolygon(xCoords, yCoords, numOfVerticies);
        g.drawLine((int)verticies[0].globalX, (int)verticies[0].globalY, (int)verticies[3].globalX, (int)verticies[3].globalY);
        g.drawLine((int)verticies[4].globalX, (int)verticies[4].globalY, (int)verticies[8].globalX, (int)verticies[8].globalY);
        //turn(3);
    }
    void moveSelf() {
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
    void turn(double degrees) {
        rotation+=degrees;
        for (int i = 0; i < numOfVerticies; i++) {
            verticies[i].turn(degrees);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
    }
    void fireAtPlayer(ArrayList<Bullet> bulletList, Ship playerShip) {
        double netXComponent = -globalCenterX+playerShip.globalCenterX;
        double netYComponent = globalCenterY-playerShip.globalCenterY;
        double angleToPlayer = 0;/*Math.toDegrees(Math.atan2(netYComponent, netXComponent));*/
        PolarCoords bulletDirection = new PolarCoords(globalCenterX, globalCenterY, angleToPlayer, Bullet.MAXUFOSPEED);
        bulletList.add(new Bullet(globalCenterX, globalCenterY, bulletDirection, true));
    }
}
