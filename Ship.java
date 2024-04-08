import java.awt.*;

public class Ship {
    int globalCenterX = 100, globalCenterY = 100, rotation = 0, maximumSpeed = 4;
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
        if (heading.magnitude != 0) {
            for (int i = 0; i < verticies.length; i++) {
                verticies[i].localX += heading.globalX-heading.localX;
                verticies[i].localY += heading.globalY-heading.localY;
                verticies[i].recalculateGlobalCoords();
                xCoords[i] = (int)verticies[i].globalX;
                yCoords[i] = (int)verticies[i].globalY;
            }
            globalCenterX = (int)verticies[0].localX;
            globalCenterY = (int)verticies[0].localY;
            applyFriction();
        }
    }
    public void applyThrusters() {
        //heading = PolarCoords.add(heading, thrustDirection);
        //heading.normalize(maximumSpeed);
        heading = new PolarCoords((int)thrustDirection.localX, (int)thrustDirection.localY, (int)thrustDirection.rotation, (int)thrustDirection.magnitude);
        heading.normalize((double)maximumSpeed);
    }
    private void applyFriction() {
        //System.out.println(heading.magnitude);
        if (heading.magnitude>0.1){
            heading.magnitude*=0.9;
            heading.recalculateGlobalCoords();
        }
        else {
            heading.magnitude = 0;
        }
    }
}
