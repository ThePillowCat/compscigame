import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.IOException;

// Main game logic
class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {
	Timer timer;
	int boxx;
	boolean[] keys, pressedKeys;
	Font comicFnt = null;
	Font UIFnt = null;
	Image back;
	double shipRotateSpeed = 6;
	boolean canShoot = false;
	Random rand = new Random();
	int bulletDelay = 30;
	int bulletFrameCount = 0;
	int UFOFrameCount = 0;
	int UFODelay = (int)rand.nextDouble()*(5*60)+500;
	int currentLevel = 1;
	int score = 0;
	int lives = 3;

	Ship myShip = new Ship();
	ArrayList<Bullet> activeBullets = new ArrayList<Bullet>();
	ArrayList<Asteroid> activeAsteroids = new ArrayList<Asteroid>();
	ArrayList<UFO> activeUFOs = new ArrayList<UFO>();

	public void startNewLevel() {
		activeAsteroids.clear();
		activeBullets.clear();
		activeUFOs.clear();
		for (int i = 0; i < 4; i++) {
			PolarCoords randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 40, 1));
			randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200 + 600, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 40, 1));
		}
		myShip.moveShipAndVerticiesToCoords(400, 300);
	}

	public Image loadImage(String img) {
		return new ImageIcon(img).getImage();
	}

	private boolean testIntersection(Shape shapeA, Shape shapeB) {
		Area areaA = new Area(shapeA);
		areaA.intersect(new Area(shapeB));
		return !areaA.isEmpty();
	}

	public GamePanel() {
		setPreferredSize(new Dimension(800, 600));
		comicFnt = new Font("Comic Sans MS", Font.PLAIN, 32);
		back = loadImage("img/OuterSpace.jpg");
		try {
			UIFnt = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace-JvEM.ttf")).deriveFont(40f);
		} catch (IOException | FontFormatException e) {
			System.out.println("font did not load");
		}
		keys = new boolean[1000];
		pressedKeys = new boolean[1000];
		boxx = 100;
		timer = new Timer(16, this);
		timer.start();
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
		startNewLevel();
	}

	public void move() {
		if (keys[KeyEvent.VK_A]) {
			myShip.turn(shipRotateSpeed);
		}
		if (keys[KeyEvent.VK_D]) {
			myShip.turn(-shipRotateSpeed);
		}
		if (keys[KeyEvent.VK_W]) {
			myShip.applyThrusters();
		}
		if (keys[KeyEvent.VK_SPACE] && canShoot) {
			activeBullets.add(new Bullet((int) myShip.globalCenterX, (int)myShip.globalCenterY, PolarCoords.normalizedReturned(myShip.verticies[0], Bullet.MAXPEED)));
			canShoot = false;
		}
		myShip.move();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		move();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// circx = e.getX();
		// circy = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void paint(Graphics g) {
		// g.setColor(new Color(111,222,111));
		// g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		// g.fillRect(boxx,200,40,40);

		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();

		int mx = mouse.x - offset.x;
		int my = mouse.y - offset.y;
		g.setFont(UIFnt);
		g.drawString(String.format("SCORE: %d", score), 50, 50);
		myShip.drawSelf(g);
		g.drawOval((int) myShip.globalCenterX - 5, (int) myShip.globalCenterY - 5, 10, 10);
		g.setColor(Color.GREEN);

		for (int i = activeBullets.size() - 1; i >= 0; i--) {
			boolean found = false;
			for (int j = activeAsteroids.size() - 1; j >= 0; j--) {
				Asteroid myAsteroid = activeAsteroids.get(j);
				Bullet myBullet = activeBullets.get(i);
				if (testIntersection(myAsteroid.myPoly, myBullet.myPoly)) {
					if (myAsteroid.varient != 2) {
						PolarCoords randomAsteroidDirection = new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
						activeAsteroids.add(new Asteroid(randomAsteroidDirection, myAsteroid.minimumRadius / 2, myAsteroid.varient + 1));
						randomAsteroidDirection = new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
						activeAsteroids.add(new Asteroid(randomAsteroidDirection, myAsteroid.minimumRadius / 2, myAsteroid.varient + 1));
					}
					activeAsteroids.remove(j);
					activeBullets.remove(i);
					score+=10*myAsteroid.varient;
					found = true;
					break;
				}
			}
			if (found) {break;}
			for (int j = activeUFOs.size() - 1; j >= 0; j--) {
				UFO myUFO = activeUFOs.get(j);
				Bullet myBullet = activeBullets.get(i);
				if (testIntersection(myUFO.myPoly, myBullet.myPoly)) {
					found = true;
					score+=50;
					activeUFOs.remove(myUFO);
					activeBullets.remove(myBullet);
					break;
				}
			}
			if (found) {
				break;
			}
		}
		for (int i = 0; i < activeBullets.size(); i++) {
			if (i > activeBullets.size() - 1) {
				break;
			}
			g.drawRect((int) activeBullets.get(i).x, (int) activeBullets.get(i).y, 10, 10);
			activeBullets.get(i).move(i, activeBullets);
		}
		for (int i = 0; i < activeAsteroids.size(); i++) {
			if (i > activeAsteroids.size() - 1) {
				break;
			}
			if (testIntersection(activeAsteroids.get(i).myPoly, myShip.myPoly)) {
				startNewLevel();
				break;
			}
			activeAsteroids.get(i).move();
			// if (myShip.myPoly.contains(activeAsteroids.get(i).myPoly));
			g.drawPolygon(activeAsteroids.get(i).xCoords, activeAsteroids.get(i).yCoords, activeAsteroids.get(i).numOfVerticies);
		}
		g.setColor(Color.PINK);
		g.drawLine((int) myShip.verticies[0].localX, (int) myShip.verticies[0].localY, (int) myShip.verticies[0].globalX, (int) myShip.verticies[0].globalY);
		bulletFrameCount++;
		UFOFrameCount++;
		if (bulletFrameCount >= bulletDelay) {
			bulletFrameCount = 0;
			canShoot = true;
		}
		if (UFOFrameCount >= UFODelay) {
			PolarCoords randomDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeUFOs.add(new UFO(0, 0, randomDirection)); 
			UFOFrameCount = 0;
			UFODelay = (int)rand.nextDouble()*(5*60)+500;
		}
		if (activeAsteroids.isEmpty()) {
			startNewLevel();
		}
		for (int i = 0; i < activeUFOs.size(); i++) {
			UFO currentUFO = activeUFOs.get(i);
			currentUFO.moveSelf();
			currentUFO.drawSelf(g);
			if (testIntersection(myShip.myPoly, currentUFO.myPoly)) {
				startNewLevel();
				break;
			}
		}
	}
}