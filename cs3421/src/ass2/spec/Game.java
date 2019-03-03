package ass2.spec;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class Game extends JFrame implements ActionListener, GLEventListener, MouseMotionListener, KeyListener {
	private Timer timer;
	private GL2 gl;
	private int hour = 6;
	private Terrain myTerrain;
	private Avatar person;
	private Camera camera;

	// texture files
	private Model myModel = Model.Terrain;
	private boolean mySmooth = true;

	private String textureNames[] = { "grass.bmp", "tree.png", "leaves.jpg", "road.png", "bearfur.jpg",
			"bearface.jpg" };
	private String textureExtensions[] = { "bmp", "png", "jpg", "png", "jpg", "jpg" };
	private int curTex;
	private boolean myModulate = true;
	private boolean mySpecularSep = true;
	private boolean mySoomth = true;
	private Sun sun;

	public enum Model {
		Terrain, Tree, Leaves, Road, AvatarFur, AvatarFace
	}

	private Texture myTextures[];

	public Game(Terrain terrain) {
		super("Assignment 2");

		myTerrain = terrain;
		camera = new Camera();

		double[] centre = { terrain.size().getWidth(), 0, terrain.size().getHeight() };
		sun = new Sun(centre);

	}

	public void updateSun() {
		hour++;
		hour %= 12;

		setUpLighting(gl, (float) (hour / 12.0), .5f, .5f, (float) (hour / 12.0) * 10);
		System.out.println("update sun at " + hour + ":00");
		System.out.println(" Ambi " + (float) (hour / 12.0) + " shiness " + (float) (hour / 12.0) * 10);
		sun.up();
		sun.setIndex(hour);

	}

	/**
	 * Run the game.
	 *
	 */
	public void run() {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLJPanel panel = new GLJPanel();

		// add a GL Event listener to handle rendering
		panel.addGLEventListener(this);

		// NEW: add a key listener to respond to keypresses
		panel.addKeyListener(this);
		// the panel needs to be focusable to get key events
		panel.setFocusable(true);

		// Add an animator to call 'display' at 60fps
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(panel);
		animator.start();

		getContentPane().add(panel);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Load a level file and display it.
	 *
	 * @param args
	 *            - The first argument is a level file in JSON format
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Terrain terrain = LevelIO.load(new File(args[0]));
		Game game = new Game(terrain);
		game.run();
		Timer timer = new Timer(5000, game);

		timer.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor((float) hour / 11, (float) hour / 11, (float) hour / 11, 1f);

		setUpLighting(gl, .2f, .5f, .5f, 20f);

		gl.glLoadIdentity();
		camera.setCamera(myTerrain);

		float[] pos = myTerrain.getSunlight();
		float[] lightpos = { pos[0], pos[1], pos[2], 0 };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightpos, 0);

		// use the texture to modulate diffuse and ambient lighting
		if (getModulate()) {
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		} else {
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		}

		draw(gl);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		this.gl = gl;
		gl.glClearColor(135 / 255f, 206 / 255f, 250 / 255f, 0.5f);

		gl.glEnable(GL2.GL_DEPTH_TEST);

		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL.GL_CULL_FACE);

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);

		// Turn on OpenGL texturing.
		gl.glEnable(GL2.GL_TEXTURE_2D);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		// Load textures

		myTextures = new Texture[this.getNumTextures()];
		for (int i = 0; i < this.getNumTextures(); i++) {
			myTextures[i] = new Texture(this, gl, "src/texture/" + this.getTexName(i), this.getTexExtension(i), true);
		}
		myTerrain.setMyTexture(myTextures[Model.Terrain.ordinal()]);
		Texture face = myTextures[Model.AvatarFace.ordinal()];
		Texture fur = myTextures[Model.AvatarFur.ordinal()];

		int centrex = (int) myTerrain.size().getHeight() / 2;
		int centrez = (int) myTerrain.size().getWidth() / 2;
		person = new Avatar(centrex, myTerrain.altitude(centrex, centrez), centrez, face, fur);
		camera.setPerson(person);

	}

	public void setUpLighting(GL2 gl, float ambi, float diff, float spec, float shin) {

		gl.glEnable(GL2.GL_LIGHTING);
		// When you enable lighting you must still actually
		// turn on a light such as this default light.

		// material parameter set for metallic gold or brass

		float ambient[] = { ambi, ambi, ambi, 1.0f };
		float diffuse[] = { diff, diff, diff, 1.0f };
		float specular[] = { spec, spec, spec, 1.0f };
		float shininess = shin;

		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
		gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

		gl.glShadeModel(this.isSmooth() ? GL2.GL_SMOOTH : GL2.GL_FLAT);

		if (this.isSpecular()) {
			gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
		} else {
			gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SINGLE_COLOR);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		double w = this.myTerrain.size().getWidth();
		double h = this.myTerrain.size().getHeight();

		GLU glu = new GLU();
		glu.gluPerspective(60.0, (float) w / (float) h, 1.0, 200.0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

	}

	private void draw(GL2 gl) {
		GLUT glut = new GLUT();

		gl.glDisable(GL2.GL_LIGHTING);
		sun.drawSun(gl, glut);
		gl.glEnable(GL2.GL_LIGHTING);


		// Turn on OpenGL texturing.
		// bind the texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, myTextures[getTexId()].getTextureId());

		myTerrain.draw(gl);
		// draw trees
		List<Tree> trees = myTerrain.trees();
		for (Tree t : trees) {

			t.setTextures(myTextures[Model.Leaves.ordinal()], myTextures[Model.Tree.ordinal()]);
			t.drawTree(gl);
		}

		List<Road> roads = myTerrain.roads();
		for (Road r : roads) {
			r.setTextures(myTextures[Model.Road.ordinal()]);
			// int counter = 0;
			// r.drawRoad(gl, counter);
			r.draw(gl);
		}

		if (camera.isFollow())
			person.drawAvatar(gl, glut);

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		double h = 0;
		double[] pos = person.getMyPos();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			if (camera.isFollow()) {
				h = myTerrain.altitude(pos[0], pos[2]);
				camera.up(h);
			}
			break;
		case KeyEvent.VK_S:
			if (camera.isFollow()) {
				h = myTerrain.altitude(pos[0], pos[2]);
				camera.down(h);
			}
			break;
		case KeyEvent.VK_A:
			if (camera.isFollow()) {
				h = myTerrain.altitude(pos[0], pos[2]);
				camera.left(h);
			}
			break;
		case KeyEvent.VK_D:
			if (camera.isFollow()) {
				h = myTerrain.altitude(pos[0], pos[2]);
				camera.right(h);
			}
			break;
		case KeyEvent.VK_U:
			sun.up();
			break;
		// case KeyEvent.VK_Q:
		// if (camera.isFollow()) {
		// camera.leftAngleAroundPerson();
		// }
		// break;
		// case KeyEvent.VK_E:
		// if (camera.isFollow()) {
		// camera.rightAngleAroundPerson();
		// }
		// break;
		case KeyEvent.VK_SPACE:
			camera.setFollow();
			break;
		case KeyEvent.VK_UP:
			if (!camera.isFollow()) {
				if (pos[2] < myTerrain.size().getWidth() - 1)
					h = myTerrain.altitude(pos[0], pos[2]);
				camera.up(h);
			}
			break;
		case KeyEvent.VK_DOWN:
			if (!camera.isFollow()) {
				if (pos[2] < myTerrain.size().getWidth() - 1)
					h = myTerrain.altitude(pos[0], pos[2]);
				camera.down(h);
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (!camera.isFollow()) {
				camera.right(h);
			}

			break;
		case KeyEvent.VK_LEFT:
			if (!camera.isFollow()) {
				camera.left(h);
			}
			break;
		case KeyEvent.VK_C:
			if (e.isControlDown()) {
				System.exit(EXIT_ON_CLOSE);
			}
		default:
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean getModulate() {
		return myModulate;
	}

	public int getNumTextures() {
		return textureNames.length;
	}

	public int getTexId() {
		return curTex;
	}

	public String getTexName(int i) {
		return textureNames[i];

	}

	public String getTexExtension(int i) {
		return textureExtensions[i];
	}

	public Model getModel() {
		return myModel;
	}

	public boolean isSmooth() {
		return mySoomth;
	}

	public boolean isSpecular() {
		return mySpecularSep;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		updateSun();
	}
}