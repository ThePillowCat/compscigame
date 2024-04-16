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
	Image back;
	double shipRotateSpeed = 6;
	boolean canShoot = false;
	Random rand = new Random();
	int bulletDelay = 30;
	int bulletFrameCount = 0;
	int UFODelay = (int)rand.nextDouble()*(5*60)+500;
	int UFOFrameCount = 0;
	int UFOBulletDelay = 120;
	int UFOBulletFrameCount = 0;
	int currentLevel = 1;
	int score = 0;
	int lives = 3;
	String state = "menu";
	Font UIFnt_40 = null;
	Font UIFnt_60 = null;
	boolean spawnedAsteroids = false;
	double lastTime = System.nanoTime();
	double curTime = 0;

	Ship myShip = new Ship();
	ArrayList<Bullet> activeBullets = new ArrayList<Bullet>();
	ArrayList<Asteroid> activeAsteroids = new ArrayList<Asteroid>();
	ArrayList<UFO> activeUFOs = new ArrayList<UFO>();

	public void runMenu(Graphics g) {
		if (!spawnedAsteroids) {
			activeAsteroids.clear();
			PolarCoords randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
			randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200 + 600, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
			spawnedAsteroids = true;
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 600);
		g.setColor(Color.WHITE);
		g.setFont(UIFnt_60);
		g.drawString("ASTEROIDS", 130, 250);
		g.setFont(UIFnt_40);
		g.drawString("Click to Start", 250, 400);
		g.setColor(Color.GREEN);
		for (int i = activeAsteroids.size()-1; i >= 0; i--) {
			Asteroid curAsteroid = activeAsteroids.get(i);
			curAsteroid.move();
			g.drawPolygon(curAsteroid.xCoords, curAsteroid.yCoords, curAsteroid.numOfVerticies);
		}
	}

	public void startNewLevel() {
		activeAsteroids.clear();
		activeBullets.clear();
		activeUFOs.clear();
		for (int i = 0; i < 5; i++) {
			PolarCoords randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
			randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200 + 600, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
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
		keys = new boolean[1000];
		pressedKeys = new boolean[1000];
		boxx = 100;
		try {
			UIFnt_40 = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace-JvEM.ttf")).deriveFont(40f);
		} catch (IOException | FontFormatException e) {
			System.out.println("font no load");
		}
		try {
			UIFnt_60 = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace-JvEM.ttf")).deriveFont(100f);
		} catch (IOException | FontFormatException e) {
			System.out.println("font no load");
		}
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
			activeBullets.add(new Bullet((int)myShip.globalCenterX, (int)myShip.globalCenterY, myShip.verticies[0].resizeVectorReturned(20), false));
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
		if (state == "menu") {
			spawnedAsteroids = false;
			state = "game";
			startNewLevel();
		}
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
		curTime = System.currentTimeMillis();
		System.out.println((curTime-lastTime));
		lastTime = System.currentTimeMillis();
		if (true) {
			return;
		}
		if (state.equals("menu")) {
			runMenu(g);
			return;
		}
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
		//g.drawString(String.format("SCORE: %d", score), 50, 50);
		myShip.drawSelf(g);
		g.drawOval((int) myShip.globalCenterX - 5, (int) myShip.globalCenterY - 5, 10, 10);
		g.setColor(Color.GREEN);

		// for (int i = activeBullets.size() - 1; i >= 0; i--) {
		// 	boolean found = false;
		// 	for (int j = activeAsteroids.size() - 1; j >= 0; j--) {
		// 		Asteroid myAsteroid = activeAsteroids.get(j);
		// 		Bullet myBullet = activeBullets.get(i);
		// 		if (testIntersection(myAsteroid.myPoly, myBullet.myPoly) && !myBullet.isUFOBullet) {
		// 			if (myAsteroid.varient != 3) {
		// 				PolarCoords randomAsteroidDirection = new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
		// 				activeAsteroids.add(new Asteroid(randomAsteroidDirection, (int)(myAsteroid.minimumRadius / 1.5), myAsteroid.varient + 1));
		// 				randomAsteroidDirection = new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
		// 				activeAsteroids.add(new Asteroid(randomAsteroidDirection, (int)(myAsteroid.minimumRadius / 1.5), myAsteroid.varient + 1));
		// 			}
		// 			activeAsteroids.remove(j);
		// 			activeBullets.remove(i);
		// 			score+=10*myAsteroid.varient;
		// 			found = true;
		// 			break;
		// 		}
		// 	}
		// 	if (found) {break;}
		// 	for (int j = activeUFOs.size() - 1; j >= 0; j--) {
		// 		UFO myUFO = activeUFOs.get(j);
		// 		Bullet myBullet = activeBullets.get(i);
		// 		if (testIntersection(myUFO.myPoly, myBullet.myPoly) && !myBullet.isUFOBullet) {
		// 			found = true;
		// 			score+=50;
		// 			activeUFOs.remove(myUFO);
		// 			activeBullets.remove(myBullet);
		// 			break;
		// 		}
		// 	}
		// 	if (found) {break;}
		// }
		for (int i = activeBullets.size() - 1; i >= 0; i--) {
			Bullet myBullet = activeBullets.get(i);
			if (i > activeBullets.size() - 1) {
				break;
			}
			// if (myBullet.isUFOBullet && testIntersection(myBullet.myPoly, myShip.myPoly)) {
			// 	startNewLevel();
			// 	break;
			// }
			g.setColor(myBullet.isUFOBullet ? Color.RED : Color.GREEN);
			g.drawRect(myBullet.x, myBullet.y, 10, 10);
			g.setColor(Color.GREEN);
			activeBullets.get(i).move(i, activeBullets);
		}
		for (int i = activeAsteroids.size() - 1; i >= 0; i--) {
			// if (testIntersection(activeAsteroids.get(i).myPoly, myShip.myPoly)) {
			// 	startNewLevel();
			// 	break;
			// }
			Asteroid curAsteroid = activeAsteroids.get(i);
			curAsteroid.move();
			g.drawPolygon(curAsteroid.xCoords, curAsteroid.yCoords, curAsteroid.numOfVerticies);
		}
		g.setColor(Color.PINK);
		g.drawLine((int) myShip.verticies[0].localX, (int) myShip.verticies[0].localY, (int) myShip.verticies[0].globalX, (int) myShip.verticies[0].globalY);
		bulletFrameCount++;
		UFOFrameCount++;
		UFOBulletFrameCount++;
		if (bulletFrameCount >= bulletDelay) {
			bulletFrameCount = 0;
			canShoot = true;
		}
		if (UFOFrameCount >= UFODelay && activeUFOs.size() == 0) {
			PolarCoords randomDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * 600, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeUFOs.add(new UFO(0, 0, randomDirection)); 
			UFOFrameCount = 0;
			UFODelay = (int)rand.nextDouble()*(5*60)+500;
		}
		if (activeAsteroids.isEmpty()) {
			startNewLevel();
		}
		// for (int i = 0; i < activeUFOs.size(); i++) {
		// 	UFO currentUFO = activeUFOs.get(i);
		// 	currentUFO.moveSelf();
		// 	currentUFO.drawSelf(g);
		// 	if (UFOBulletFrameCount >= UFOBulletDelay) {
		// 		System.out.println("bloons");
		// 		currentUFO.fireAtPlayer(activeBullets, myShip);
		// 		UFOBulletFrameCount = 0;
		// 	}
		// 	if (testIntersection(myShip.myPoly, currentUFO.myPoly)) {
		// 		startNewLevel();
		// 		break;
		// 	}
		// }
	}
}