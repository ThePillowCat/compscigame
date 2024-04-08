import java.awt.*;

public class Ship {
    int globalCenterX = 100, globalCenterY = 100, rotation = 0, maximumSpeed = 2;
    PolarCoords[] verticies = new PolarCoords[4];
    //thrust direction, to be added to heading when moving
    PolarCoords thrustDirection = null;
    //overall direction the spaceship is heading on a fram - starts with a 0 magnitude vector
    PolarCoords heading = new PolarCoords(0, 0, 0, 0);
    PolarCoords friction = null;
    int[] xCoords = new int[4];
    int[] yCoords = new int[4];
    Ship() {
        verticies[0] = new PolarCoords(globalCenterX, globalCenterY, rotation, 30);
        verticies[1] = new PolarCoords(globalCenterX, globalCenterY, rotation+120, 30);
        verticies[2] = new PolarCoords(globalCenterX, globalCenterY, rotation+180, 10);
        verticies[3] = new PolarCoords(globalCenterX, globalCenterY, rotation+240, 30);
        for (int i = 0;  i < verticies.length; i++) {
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        thrustDirection = verticies[0];
        //setCentralRotationPoint();
        //thrustDirection = verticies[0];
    }
    void turn(int degrees) {
        rotation+=degrees;
        for (int i = 0; i < verticies.length; i++) {
            verticies[i].turn(degrees);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
    }
    void move() {
        for (int i = 0; i < verticies.length; i++) {
            verticies[i].localX += heading.globalX-heading.localX;
            verticies[i].localY -= heading.globalY-heading.localY;
            verticies[i].recalculateGlobalCoords();
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        globalCenterX = (int)verticies[0].localX;
        globalCenterY = (int)verticies[0].localY;
    }
    public void applyThrusters() {
        heading = PolarCoords.add(heading, thrustDirection);
        heading.normalize(maximumSpeed);
    }
    private void applyFriction() {

    }
    private void setCentralRotationPoint() {
        int xAvg = 0;
        int yAvg = 0;
        for (int i = 0; i < verticies.length; i++) {
            xAvg+=(int)verticies[i].localX;
            yAvg+=(int)verticies[i].localY;
        }
        xAvg/=verticies.length;
        yAvg/=verticies.length;
        globalCenterX = xAvg;
        globalCenterY = yAvg;
        for (int i = 0; i < verticies.length; i++) {
            verticies[i].localX = xAvg;
            verticies[i].localY = yAvg;
            verticies[i].turn(0);
        }
    }
}
