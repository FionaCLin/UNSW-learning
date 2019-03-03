package ass1;

import java.util.List;

import com.jogamp.opengl.GL2;

public class CircularGameObject extends GameObject {

	private static final int NPOINTS = 32;
	private double radius = 1;
	private double[] center = { 0, 0 };

	private double[] myFillColour;
	private double[] myLineColour;

	/**
	 * Create a Circular game object and add it to the scene tree
	 * 
	 * The line and fill colours can possibly be null, in which case that part
	 * of the object should not be drawn.
	 *
	 * @param parent
	 *            The parent in the scene tree
	 * @param radius
	 *            It is 1 by default unless you specified in the constructor
	 * @param fillColour
	 *            The fill colour in [r, g, b, a] form
	 * @param lineColour
	 *            The outlien colour in [r, g, b, a] form
	 */

	// Create a CircularGameObject with centre 0,0 and radius 1
	public CircularGameObject(GameObject parent, double[] fillColour, double[] lineColour) {
		super(parent);
		myFillColour = fillColour;
		myLineColour = lineColour;
	}

	// Create a CircularGameObject with centre 0,0 and a given radius
	public CircularGameObject(GameObject parent, double radius, double[] fillColour, double[] lineColour) {
		super(parent);
		this.radius = radius;
		myFillColour = fillColour;
		myLineColour = lineColour;
	}

	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * @return the center
	 */
	public double[] getCenter() {
		return center;
	}

	/**
	 * @param center
	 *            the center to set
	 */
	public void setCenter(double[] center) {
		this.center = center;
	}

	/**
	 * @return the myFillColour
	 */
	public double[] getMyFillColour() {
		return myFillColour;
	}

	/**
	 * @param myFillColour
	 *            the myFillColour to set
	 */
	public void setMyFillColour(double[] myFillColour) {
		this.myFillColour = myFillColour;
	}

	/**
	 * @return the myLineColour
	 */
	public double[] getMyLineColour() {
		return myLineColour;
	}

	/**
	 * @param myLineColour
	 *            the myLineColour to set
	 */
	public void setMyLineColour(double[] myLineColour) {
		this.myLineColour = myLineColour;
	}

	@Override
	public void drawSelf(GL2 gl) {

		if (myLineColour != null) {
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glColor4d(myLineColour[0], myLineColour[1], myLineColour[2], myLineColour[3]);
		}
		drawCircle(gl);

		if (myFillColour != null) {
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			gl.glColor4d(myFillColour[0], myFillColour[1], myFillColour[2], myFillColour[3]);
		}
		drawCircle(gl);
//		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}

	public void drawCircle(GL2 gl) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d(center[1], center[1]); // The centre of the circle
		for (int d = 0; d <= 32; d++) {
			double angle = 2 * Math.PI / 32 * d;
			gl.glVertex2d(radius * Math.cos(angle), radius *Math.sin(angle));
		}
		gl.glEnd();
	}
}
