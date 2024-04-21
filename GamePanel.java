/*
 * Program: GamePanel.java
 * Author: Noah Levy
 * This file is the coordinator of the game. It interfaces with different classes and handles
 * objects to ensure correct gameplay. It contains logic for delays, collition checks, movements, and more
 */

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
	//HANDLES JFRAME RELATED THINGS (input, etc.)
	Timer timer;
	int boxx;
	boolean[] keys, pressedKeys;
	Font comicFnt = null;

	//GENERAL GAME VARIABLES
	double shipRotateSpeed = 6;
	boolean canShoot = false;
	Random rand = new Random();
	static final int WIDTH = (int)(800*1.5);
	static final int HEIGHT = (int)(600*1.5);

	//HANDLES DELAYS IN THE GAME
	int bulletDelay = 30;
	int bulletFrameCount = 0;
	int UFODelay = (int) rand.nextDouble() * (5 * 60) + 500;
	int UFOFrameCount = 0;
	int UFOBulletDelay = 120;
	int UFOBulletFrameCount = 0;
	int newLevelDelay = 120;
	int newLevelFrameCount = newLevelDelay;

	//GENERAL UI RELATED VARIABLES
	static int currentLevel = 0;
	int score = 0;
	static int lives = 3;
	int teleportsLeft = 0;
	Font UIFnt_40 = null;
	Font UIFnt_50 = null;
	Font UIFnt_100 = null;

	//HANDLES STATES
	static String state = "menu";
	boolean spawnedAsteroids = false;
	boolean canTeleport = true;
	boolean isTransitioningLevels = false;
	boolean canPause = true;
	boolean paused = false;

	//PLAYER AND CONTAINERS TO KEEP TRACK OF ENEMIES
	Ship myShip = new Ship();
	ArrayList<Bullet> activeBullets = new ArrayList<Bullet>();
	ArrayList<Asteroid> activeAsteroids = new ArrayList<Asteroid>();
	ArrayList<UFO> activeUFOs = new ArrayList<UFO>();
	ArrayList<Particle> activeParticles = new ArrayList<Particle>();

	//SOUNDS
	SoundEffect shoot = new SoundEffect("sounds/fire.wav");
	SoundEffect thrust = new SoundEffect("sounds/thrust.wav");
	SoundEffect beat1 = new SoundEffect("sounds/beat1.wav");
	SoundEffect beat2 = new SoundEffect("sounds/beat2.wav");

	//the following handles playing music
	boolean isBeat1 = true;
	public void playMusic() {
		if (!beat1.c.isRunning() && !beat2.c.isRunning()) {
			if (isBeat1) {
				beat1.play();
				isBeat1 = false;
			}
			else {
				beat2.play();
				isBeat1 = true;
			}
		}
	}
	public void runMenu(Graphics g) {
		//draw asteroids for menu if not already spawned in
		if (!spawnedAsteroids) {
			activeAsteroids.clear();
			for (int i = 0; i < 4; i++) {
				PolarCoords randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * GamePanel.HEIGHT,
					rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
			}
			spawnedAsteroids = true;
		}
		//draw menu
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(UIFnt_100);
		g.drawString("ASTEROIDS", 320, 400);
		g.setFont(UIFnt_50);
		g.drawString("Click to Start", 390, 550);
		g.setColor(Color.GREEN);
		//draws demo asteroids on title
		for (int i = 0; i < activeAsteroids.size(); i++) {
			Asteroid myAsteroid = activeAsteroids.get(i);
			myAsteroid.move();
			g.drawPolygon(myAsteroid.xCoords, myAsteroid.yCoords, myAsteroid.numOfVerticies);
		}
	}

	public void runGameOver(Graphics g) {
		//draw game over UI
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(UIFnt_100);
		g.drawString("GAME OVER", 320, 450);
		g.setFont(UIFnt_40);
		g.drawString("Click to return to menu", 310, 500);
	}

	public void startNewLevel() {
		//starts new level and handles transitioning levels
		if (state.equals("game")){
			isTransitioningLevels = true;
			newLevelFrameCount++;
		}
		if (newLevelFrameCount >= newLevelDelay) {
			newLevelFrameCount = 0;
		}
		else {
			return ;
		}
		//clear containers, add new asteroids and teleports
		isTransitioningLevels = false;
		myShip.isInvincible = true;
		activeAsteroids.clear();
		activeBullets.clear();
		activeUFOs.clear();
		currentLevel++;
		teleportsLeft = 3;
		for (int i = 0; i < currentLevel; i++) {
			//add asteroids based on a random direction
			PolarCoords randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200, rand.nextDouble() * HEIGHT,
					rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
			randomAsteroidDirection = new PolarCoords(rand.nextDouble() * 200 + HEIGHT, rand.nextDouble() * HEIGHT,
					rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
			activeAsteroids.add(new Asteroid(randomAsteroidDirection, 30, 1));
		}
		//move ship to center of the screen
		myShip.moveShipAndVerticiesToCoords(WIDTH/2, HEIGHT/2);
	}

	//reset some variables
	public void startNewGame() {
		lives = 3;
		score = 0;
		teleportsLeft = 0;
		myShip.deathAnimationActive = false;
		myShip.heading = new PolarCoords(myShip.globalCenterX, myShip.globalCenterY, 0, 0);
		myShip.rotation = 0;
	}

	public Image loadImage(String img) {
		return new ImageIcon(img).getImage();
	}

	//convert passed in polygons to shape, test if they collide with Area.intersect
	private boolean testIntersection(Shape shapeA, Shape shapeB) {
		Area areaA = new Area(shapeA);
		areaA.intersect(new Area(shapeB));
		return !areaA.isEmpty();
	}

	public GamePanel() {
		//set fonts, variables, inputs, etc.
		setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
		comicFnt = new Font("Comic Sans MS", Font.PLAIN, 32);
		keys = new boolean[1000];
		pressedKeys = new boolean[1000];
		boxx = 100;
		try {
			UIFnt_40 = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace-JvEM.ttf")).deriveFont(40f);
		} catch (IOException | FontFormatException e) {
			System.out.println("font no load");
		}
		try {
			UIFnt_50 = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace-JvEM.ttf")).deriveFont(50f);
		} catch (IOException | FontFormatException e) {
			System.out.println("font no load");
		}
		try {
			UIFnt_100 = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Hyperspace-JvEM.ttf")).deriveFont(100f);
		} catch (IOException | FontFormatException e) {
			System.out.println("font no load");
		}
		//timer is set to around 60fps
		timer = new Timer(15, this);
		timer.start();
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
	}

	public void move() {
		if (myShip.deathAnimationActive || isTransitioningLevels || state != "game") {
			return ;
		}
		//controls pausing logic
		if (keys[KeyEvent.VK_P] && canPause) {
			paused = !paused;
			canPause = false;
		}
		if (paused) {
			return; 
		}
		//controls the input
		myShip.isThrusting = false;
		if (keys[KeyEvent.VK_A]) {
			myShip.turn(shipRotateSpeed);
		}
		if (keys[KeyEvent.VK_D]) {
			myShip.turn(-shipRotateSpeed);
		}
		if (keys[KeyEvent.VK_W]) {
			if (!thrust.c.isRunning()) {
				thrust.play();
			}
			myShip.applyThrusters();
		}
		//if can shoot, add a bullet to bullet container
		if (keys[KeyEvent.VK_SPACE] && canShoot) {
			shoot.play();
			activeBullets.add(new Bullet((int) myShip.globalCenterX, (int) myShip.globalCenterY, myShip.verticies[0].resizeVectorReturned(20), false));
			canShoot = false;
		}
		if (keys[KeyEvent.VK_E] && !state.equals("menu") && canTeleport && teleportsLeft >= 1) {
			myShip.moveShipAndVerticiesToCoords(rand.nextDouble() * GamePanel.WIDTH, rand.nextDouble() * GamePanel.HEIGHT);
			canTeleport = false;
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
		//handles flags so certian buttons only give out one input
		keys[e.getKeyCode()] = false;
		if (e.getKeyChar() == 'e') {
			canTeleport = true;
			if (teleportsLeft>=1) {
				teleportsLeft--;
			}
		}
		if (e.getKeyChar() == 'p') {
			canPause = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//handles changing screens
		if (state.equals("menu")) {
			spawnedAsteroids = false;
			state = "game";
			startNewLevel();
		}
		if (state.equals("game over")) {
			newLevelFrameCount = newLevelDelay;
			state = "menu";
			startNewGame();
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void paint(Graphics g) {
		//run menu or game over screens if needed
		if (state.equals("menu")) {
			runMenu(g);
			return;
		}
		if (state.equals("game over")) {
			runGameOver(g);
			return ;
		}
		if (paused) {
			return;
		}
		//draw backrounds and run animations
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setFont(UIFnt_50);
		if (myShip.deathAnimationActive) {
			myShip.runDeathAnimation(g);
		}
		else if (myShip.isInvincible) {
			myShip.runInvincibility();
		}

		//playMusic();
		g.setColor(Color.WHITE);

		//draw UI and ship
		g.drawString(String.format("SCORE: %d", score), 50, 50);
		g.drawString(String.format("LIVES: %d", lives), 950, 50);
		myShip.drawSelf(g, myShip.invincibilityCount);
		g.setColor(Color.GREEN);

		//loop every bullet against every asteroid and check for collision
		for (int i = activeBullets.size() - 1; i >= 0; i--) {
			boolean found = false;
			Bullet myBullet = activeBullets.get(i);
			for (int j = activeAsteroids.size() - 1; j >= 0; j--) {
				Asteroid myAsteroid = activeAsteroids.get(j);
				if (testIntersection(myAsteroid.myPoly, myBullet.myPoly)) {
					//if collided and already not as small as can be, create two new 
					//asteroids with random velocities. Add them to activeAsteroids container
					if (myAsteroid.varient != 3) {
						PolarCoords randomAsteroidDirection = new PolarCoords(myAsteroid.globalCenterX,
								myAsteroid.globalCenterY, rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
						activeAsteroids.add(new Asteroid(randomAsteroidDirection,
								(int) (myAsteroid.minimumRadius / 1.5), myAsteroid.varient + 1));
						randomAsteroidDirection = new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY,
								rand.nextDouble() * 360, rand.nextDouble() * 2 + 1);
						activeAsteroids.add(new Asteroid(randomAsteroidDirection,
								(int) (myAsteroid.minimumRadius / 1.5), myAsteroid.varient + 1));
					}
					//add particle effects
					activeAsteroids.remove(j);
					for (int k = 0; k < 10; k++) {
						activeParticles.add(new Particle((int)myAsteroid.globalCenterX, (int)myAsteroid.globalCenterY, new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY, rand.nextDouble()*360, rand.nextDouble()*3+Particle.ASTEROIDPARTICLESPEED), Color.GREEN));
					}
					activeBullets.remove(i);
					//add to score based on size of asteroid
					score += 10 * myAsteroid.varient;
					//flag to break out of both loops becomes true
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
			//loop through active UFOs and check if they are hit by a player bullet
			for (int j = activeUFOs.size() - 1; j >= 0; j--) {
				UFO myUFO = activeUFOs.get(j);
				if (testIntersection(myUFO.myPoly, myBullet.myPoly) && !myBullet.isUFOBullet) {
					found = true;
					score += 50;
					activeUFOs.remove(myUFO);
					activeBullets.remove(myBullet);
					break;
				}
			}
			if (found) {
				break;
			}
			//check if bullet is hit by UFO bullet
			if (myBullet.isUFOBullet && testIntersection(myBullet.myPoly, myShip.myPoly) && !myShip.isInvincible) {
				lives--;
				myShip.runDeathAnimation(g);
				break;
			}
			//set respective bullet colours
			g.setColor(myBullet.isUFOBullet ? Color.RED : Color.GREEN);
			g.drawRect(myBullet.x, myBullet.y, 10, 10);
			g.setColor(Color.GREEN);
			myBullet.move(i, activeBullets);
		}
		for (int i = 0; i < activeAsteroids.size(); i++) {
			//test if the asteroid collides with the ship
			Asteroid myAsteroid = activeAsteroids.get(i);
			if (testIntersection(myAsteroid.myPoly, myShip.myPoly) && !myShip.isInvincible) {
				//remove a life and run the death animation
				lives--;
				myShip.runDeathAnimation(g);
				//add particle effects
				activeAsteroids.remove(myAsteroid);
				for (int k = 0; k < 10; k++) {
					activeParticles.add(new Particle((int)myAsteroid.globalCenterX, (int)myAsteroid.globalCenterY, new PolarCoords(myAsteroid.globalCenterX, myAsteroid.globalCenterY, rand.nextDouble()*360, rand.nextDouble()*3+Particle.ASTEROIDPARTICLESPEED), Color.GREEN));
				}
				break;
			}
			//draw asteroid and move it
			myAsteroid.move();
			g.drawPolygon(myAsteroid.xCoords, myAsteroid.yCoords, myAsteroid.numOfVerticies);
		}
		//draw particles
		for (int i = 0; i < activeParticles.size(); i++) {
			activeParticles.get(i).moveAndDrawSelf(g, activeParticles, i);
		}
		g.setColor(Color.PINK);
		//update delay counters
		bulletFrameCount++;
		UFOFrameCount++;
		UFOBulletFrameCount++;
		if (bulletFrameCount >= bulletDelay) {
			bulletFrameCount = 0;
			canShoot = true;
		}
		//ensure there are no UFOS on the screen and add a UFO to the container
		if (UFOFrameCount >= UFODelay && activeUFOs.size() == 0) {
			activeUFOs.add(new UFO(0, 0));
			UFOFrameCount = 0;
			UFODelay = (int) rand.nextDouble() * (5 * 60) + 1000;
		}
		//loop through active UFOs and handle logic like firing at the player
		for (int i = 0; i < activeUFOs.size(); i++) {
			UFO currentUFO = activeUFOs.get(i);
			currentUFO.moveSelf();
			currentUFO.drawSelf(g);
			if (UFOBulletFrameCount >= UFOBulletDelay) {
				currentUFO.fireAtPlayer(activeBullets, myShip);
				UFOBulletFrameCount = 0;
			}
			if (testIntersection(myShip.myPoly, currentUFO.myPoly) && !myShip.isInvincible) {
				//remove a life and draw death animation if ship collides with UFO
				lives--;
				myShip.runDeathAnimation(g);
				break;
			}
		}
		//check if asteroids are clear to start a new level
		if (activeAsteroids.isEmpty()) {
			startNewLevel();
		}
		//congradulate the player if transitioning levels
		if (isTransitioningLevels) {
			activeUFOs.clear();
			activeAsteroids.clear();
			activeBullets.clear();
			g.setFont(UIFnt_100);
			g.setColor(Color.WHITE);
			g.drawString("LEVEL", 450, 350);
			g.drawString("COMPLETE", 350,450);
			startNewLevel();
		}
	}
}