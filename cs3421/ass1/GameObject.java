package ass1;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import ass1.GameObject;
import ass1.MathUtil;

/**
 * A GameObject is an object that can move around in the game world.
 * 
 * GameObjects form a scene tree. The root of the tree is the special ROOT
 * object.
 * 
 * Each GameObject is offset from its parent by a rotation, a translation and a
 * scale factor.
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class GameObject {

	// the list of all GameObjects in the scene tree
	public final static List<GameObject> ALL_OBJECTS = new ArrayList<GameObject>();

	// the root of the scene tree
	public final static GameObject ROOT = new GameObject();

	// the links in the scene tree
	private GameObject myParent;
	private List<GameObject> myChildren;

	// the local transformation
	// myRotation should be normalised to the range [-180..180)
	private double myRotation;
	private double myScale;
	private double[] myTranslation;

	// is this part of the tree showing?
	private boolean amShowing;

	/**
	 * Special private constructor for creating the root node. Do not use
	 * otherwise.
	 */
	private GameObject() {
		myParent = null;
		myChildren = new ArrayList<GameObject>();

		myRotation = 0;
		myScale = 1;
		myTranslation = new double[2];
		myTranslation[0] = 0;
		myTranslation[1] = 0;

		amShowing = true;

		ALL_OBJECTS.add(this);
	}

	/**
	 * Public constructor for creating GameObjects, connected to a parent
	 * (possibly the ROOT).
	 * 
	 * New objects are created at the same location, orientation and scale as
	 * the parent.
	 *
	 * @param parent
	 */
	public GameObject(GameObject parent) {
		myParent = parent;
		myChildren = new ArrayList<GameObject>();

		parent.myChildren.add(this);

		myRotation = 0;
		myScale = 1;
		myTranslation = new double[2];
		myTranslation[0] = 0;
		myTranslation[1] = 0;

		// initially showing
		amShowing = true;

		ALL_OBJECTS.add(this);
	}

	/**
	 * Remove an object and all its children from the scene tree.
	 */
	public void destroy() {
		List<GameObject> childrenList = new ArrayList<GameObject>(myChildren);
		for (GameObject child : childrenList) {
			child.destroy();
		}
		if (myParent != null)
			myParent.myChildren.remove(this);
		ALL_OBJECTS.remove(this);
	}

	/**
	 * Get the parent of this game object
	 * 
	 * @return
	 */
	public GameObject getParent() {
		return myParent;
	}

	/**
	 * Get the children of this object
	 * 
	 * @return
	 */
	public List<GameObject> getChildren() {
		return myChildren;
	}

	/**
	 * Get the local rotation (in degrees)
	 * 
	 * @return
	 */
	public double getRotation() {
		return myRotation;
	}

	/**
	 * Set the local rotation (in degrees)
	 * 
	 * @return
	 */
	public void setRotation(double rotation) {
		myRotation = MathUtil.normaliseAngle(rotation);
	}

	/**
	 * Rotate the object by the given angle (in degrees)
	 * 
	 * @param angle
	 */
	public void rotate(double angle) {
		myRotation += angle;
		myRotation = MathUtil.normaliseAngle(myRotation);
	}

	/**
	 * Get the local scale
	 * 
	 * @return
	 */
	public double getScale() {
		return myScale;
	}

	/**
	 * Set the local scale
	 * 
	 * @param scale
	 */
	public void setScale(double scale) {
		myScale = scale;
	}

	/**
	 * Multiply the scale of the object by the given factor
	 * 
	 * @param factor
	 */
	public void scale(double factor) {
		myScale *= factor;
	}

	/**
	 * Get the local position of the object
	 * 
	 * @return
	 */
	public double[] getPosition() {
		double[] t = new double[2];
		t[0] = myTranslation[0];
		t[1] = myTranslation[1];

		return t;
	}

	/**
	 * Set the local position of the object
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		myTranslation[0] = x;
		myTranslation[1] = y;
	}

	/**
	 * Move the object by the specified offset in local coordinates
	 * 
	 * @param dx
	 * @param dy
	 */
	public void translate(double dx, double dy) {
		myTranslation[0] += dx;
		myTranslation[1] += dy;
	}

	/**
	 * Test if the object is visible
	 * 
	 * @return
	 */
	public boolean isShowing() {
		return amShowing;
	}

	/**
	 * Set the showing flag to make the object visible (true) or invisible
	 * (false). This flag should also apply to all descendents of this object.
	 * 
	 * @param showing
	 */
	public void show(boolean showing) {
		amShowing = showing;
	}

	/**
	 * Update the object. This method is called once per frame.
	 * 
	 * This does nothing in the base GameObject class. Override this in
	 * subclasses.
	 * 
	 * @param dt
	 *            The amount of time since the last update (in seconds)
	 */
	public void update(double dt) {
		// do nothing
	}

	/**
	 * Draw the object (but not any descendants)
	 * 
	 * This does nothing in the base GameObject class. Override this in
	 * subclasses.
	 * 
	 * @param gl
	 */
	public void drawSelf(GL2 gl) {
		// do nothing
	}

	// ===========================================
	// COMPLETE THE METHODS BELOW
	// ===========================================

	/**
	 * Draw the object and all of its descendants recursively.
	 * 
	 * TODO: Complete this method
	 * 
	 * @param gl
	 */
	public void draw(GL2 gl) {

		// don't draw if it is not showing
		if (!amShowing) {
			return;
		}

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		gl.glPushMatrix();

		// TODO: setting the model transform appropriately

		gl.glTranslated(myTranslation[0], myTranslation[1], 0);
		gl.glRotated(myRotation, 0, 0, 1);
		gl.glScaled(myScale, myScale, 1);
		drawSelf(gl);
		// draw the object (Call drawSelf() to draw the object itself)
		// and all its children recursively
		for (GameObject go : myChildren) {
			// don't draw if it is not showing
			if (go == null | !go.amShowing)
				continue;

			go.draw(gl);
		}

		gl.glPopMatrix();
	}

	/***
	 * 
	 * @return
	 */
	private double[][] getCurrTran() {
		double[][] curr_tran;
		double[][] loca_tran;

		double[][] loca_tran_mx = MathUtil.translationMatrix(myTranslation);
		double[][] loca_rota_mx = MathUtil.rotationMatrix(myRotation);
		double[][] loca_scal_mx = MathUtil.scaleMatrix(myScale);
		loca_tran = MathUtil.multiply(MathUtil.multiply(loca_tran_mx, loca_rota_mx), loca_scal_mx);
		if (myParent == null) {
			curr_tran = loca_tran;
		} else {
			double[][] pare_mx = myParent.getCurrTran();
			curr_tran = MathUtil.multiply(pare_mx, loca_tran);
		}
		return curr_tran;

	}

	/***
	 * 
	 * @param global_tran
	 * @return
	 */
	private double[][] getCurrInverTran(double[][] global_tran) {
		double[][] loca_tran;
		double[] rever_myTranslation = { -myTranslation[0], -myTranslation[1] };
		double[][] loca_scal_mx = MathUtil.scaleMatrix(1 / myScale);
		double[][] loca_rota_mx = MathUtil.rotationMatrix(-myRotation);
		double[][] loca_tran_mx = MathUtil.translationMatrix(rever_myTranslation);
		loca_tran = MathUtil.multiply(MathUtil.multiply(loca_scal_mx, loca_rota_mx), loca_tran_mx);
		if (myParent == null) {
			return MathUtil.multiply(loca_tran, global_tran);
		} else {
			global_tran = myParent.getCurrInverTran(global_tran);
			return MathUtil.multiply(loca_tran, global_tran);
		}
	}

	/**
	 * Compute the object's position in world coordinates
	 * 
	 * TODO: Write this method
	 * 
	 * @return a point in world coordinats in [x,y] form
	 */
	public double[] getGlobalPosition() {
		double[] p = { getCurrTran()[0][2], getCurrTran()[1][2] };
		return p;
	}

	/**
	 * Compute the object's rotation in the global coordinate frame
	 * 
	 * TODO: Write this method
	 * 
	 * @return the global rotation of the object (in degrees) and normalized to
	 *         the range (-180, 180) degrees.
	 */
	public double getGlobalRotation() {
		double rotation = this.myRotation;
		if (this.myParent != null)
			rotation += this.myParent.getGlobalRotation();
		return MathUtil.normaliseAngle(rotation);
	}

	/**
	 * Compute the object's scale in global terms
	 * 
	 * TODO: Write this method
	 * 
	 * @return the global scale of the object
	 */
	public double getGlobalScale() {
		double scale = this.myScale;
		if (this.myParent != null) {
			double[] p = new double[2];
			p[0] = Math.pow(getCurrTran()[0][0], 2);
			p[1] = Math.pow(getCurrTran()[0][1], 2);
			scale = Math.sqrt((p[0] + p[1]));
		}
		return scale;
	}

	/**
	 * Change the parent of a game object.
	 * 
	 * TODO: add code so that the object does not change its global position,
	 * rotation or scale when it is reparented. You may need to add code before
	 * and/or after the fragment of code that has been provided - depending on
	 * your approach
	 * 
	 * @param parent
	 */
	public void setParent(GameObject parent) {

		double[][] curr_global = getCurrTran();
		double globalRotation = getGlobalRotation();
		double[][] curr_inver_mx = curr_global;
		myParent = parent;

		if (parent != null) {
			myParent.myChildren.remove(this);
			myParent.myChildren.add(this);
			curr_inver_mx = myParent.getCurrInverTran(curr_global);
			setRotation(globalRotation - myParent.getGlobalRotation());
		}
		double[] p = new double[2];
		p[0] = Math.pow(curr_inver_mx[0][0], 2);
		p[1] = Math.pow(curr_inver_mx[0][1], 2);
		setScale(Math.sqrt((p[0] + p[1])));
		this.setPosition(curr_inver_mx[0][2], curr_inver_mx[1][2]);

	}

}
