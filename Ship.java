import java.awt.*;

public class Ship {
    double globalCenterX = 100, globalCenterY = 100, rotation = 0, maximumSpeed = 10;
    double maximumThrust = 0.2;
    PolarCoords[] verticies = new PolarCoords[4];
    //thrust direction, to be added to heading when moving
    PolarCoords thrustDirection = null;
    //overall direction the spaceship is heading on a fram - starts with a 0 magnitude vector
    PolarCoords heading = new PolarCoords(globalCenterX, globalCenterY, 0, 0);
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
        thrustDirection = PolarCoords.normalizedReturned(verticies[0], maximumThrust);
    }
    void turn(double degrees) {
        rotation+=degrees;
        for (int i = 0; i < verticies.length; i++) {
            verticies[i].turn(degrees);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        thrustDirection.turn(degrees);
    }
    void move() {
        for (int i = 0; i < verticies.length; i++) {
            double newX = verticies[i].localX+heading.globalX-heading.localX;
            double newY = verticies[i].localY+heading.globalY-heading.localY;
            verticies[i].setNewLocalCoords(newX, newY);
            xCoords[i] = (int)verticies[i].globalX;
            yCoords[i] = (int)verticies[i].globalY;
        }
        globalCenterX = (int)verticies[0].localX;
        globalCenterY = (int)verticies[0].localY;
        heading.setNewLocalCoords(globalCenterX, globalCenterY);
        thrustDirection.setNewLocalCoords(globalCenterX, globalCenterY);
        applyFriction();
    }
    public void applyThrusters() {
        heading = PolarCoords.add(heading, thrustDirection, maximumSpeed);
    }
    private void applyFriction() {
        if (heading.magnitude>0.01){
            heading.normalize(heading.magnitude*0.99);
        }
        else {
            heading.magnitude = 0;
        }
    }
}
