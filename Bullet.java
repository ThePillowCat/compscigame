import java.util.ArrayList;

public class Bullet {
    double x, y;
    public static final int MAXPEED = 20;
    PolarCoords direction = null;
    Bullet(double xPos, double yPos, PolarCoords d) {
        x = xPos;
        y = yPos;
        direction = d;
        d.normalize(MAXPEED);
    }
    void move(int curIndex, ArrayList<Bullet> a) {
        x+=(direction.globalX-direction.localX);
        y+=(direction.globalY-direction.localY);
        if (x > 2000 || x < 0 || y > 1500 || y < 0) {
            a.remove(curIndex);
        }
    }
}
