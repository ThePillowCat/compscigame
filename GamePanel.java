import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

// Main game logic
class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener{
	Timer timer;
	int boxx;
	boolean []keys, pressedKeys;
	Font comicFnt=null;
	Image back;
	int shipRotateSpeed = 3;
	boolean canShoot = false;

	Ship myShip = new Ship();
	ArrayList<Bullet> activeBullets = new ArrayList<Bullet>();
	
	public Image loadImage(String img){
		return new ImageIcon(img).getImage();
	}
	
	public GamePanel(){
		setPreferredSize(new Dimension(800, 600));
		comicFnt = new Font("Comic Sans MS", Font.PLAIN, 32);
		back = loadImage("OuterSpace.jpg");		
		
		keys = new boolean[1000];
		pressedKeys = new boolean[1000];
		boxx = 100;
		timer = new Timer(16, this);
		timer.start();
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
	}

	public void move(){
		if(keys[KeyEvent.VK_A]){
			myShip.turn(shipRotateSpeed);
		}
		if(keys[KeyEvent.VK_D]){
			myShip.turn(-shipRotateSpeed);
		}
		if(keys[KeyEvent.VK_W]){
			myShip.applyThrusters();
		}
		if (keys[KeyEvent.VK_SPACE] && canShoot) {
			activeBullets.add(new Bullet(myShip.globalCenterX, myShip.globalCenterY, PolarCoords.normalizedReturned(myShip.thrustDirection, Bullet.MAXPEED)));
			canShoot = false;
		}
		//System.out.println(activeBullets.size());
		myShip.move();
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		move();		
		repaint();
	}
	
	@Override
	public void	keyPressed(KeyEvent e){
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void	keyReleased(KeyEvent e){
		//System.out.println(e.getKeyCode());
		keys[e.getKeyCode()] = false;
		System.out.println(e.getKeyChar());
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			canShoot = true;
		}
	}

	@Override
	public void	keyTyped(KeyEvent e){}

	@Override
	public void	mouseClicked(MouseEvent e){}

	@Override
	public void	mouseEntered(MouseEvent e){}

	@Override
	public void	mouseExited(MouseEvent e){}
	
	@Override
	public void	mousePressed(MouseEvent e){
		// circx = e.getX();
		// circy = e.getY();
	}
	
	@Override
	public void	mouseReleased(MouseEvent e){}

	@Override
	public void paint(Graphics g){
		//g.setColor(new Color(111,222,111));
		//g.fillRect(0,0,getWidth(),getHeight());
		g.drawImage(back, 0, 0, null);
		g.setColor(Color.RED);
		//g.fillRect(boxx,200,40,40);
		
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		
		int mx = mouse.x-offset.x;
		int my = mouse.y-offset.y;
		g.setFont(comicFnt);
		g.drawString(String.format("(%d,%d)\n",mx,my), 50,50);
		g.drawPolygon(myShip.xCoords, myShip.yCoords, myShip.verticies.length);
		g.drawOval(myShip.globalCenterX-5, myShip.globalCenterY-5, 10, 10);
		//g.drawLine((int)myShip.thrustDirection.localX, (int)myShip.thrustDirection.localY, (int)myShip.thrustDirection.globalX, (int)myShip.thrustDirection.globalY);
		for (int i = 0; i < activeBullets.size(); i++) {
			if (i > activeBullets.size()-1) {break;}
			g.drawRect(activeBullets.get(i).x, activeBullets.get(i).y, 10, 10);
			activeBullets.get(i).move(i, activeBullets);
		}
		g.drawLine((int)myShip.verticies[0].localX, (int)myShip.verticies[0].localY, (int)myShip.verticies[0].globalX, (int)myShip.verticies[0].globalY);
    }
}