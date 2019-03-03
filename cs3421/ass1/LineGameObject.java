package ass1;

import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2;

public class LineGameObject extends GameObject {
	// Create a LineGameObject from (0,0) to (1,0)
	double[] myLineColour;
	private List<double[]> myPoints;

	/**
	 * Create a Line game object and add it to the scene tree
	 * 
	 * The line colours can possibly be not null, in which case that part of the
	 * object should be drawn.
	 *
	 * @param parent
	 *            The parent in the scene tree
	 * @param points
	 *            A list of points defining the line. It is a line through (0,0)
	 *            and (1,0) by default unless you specified in the constructor
	 * @param lineColour
	 *            The outlien colour in [r, g, b, a] form
	 */
	public LineGameObject(GameObject parent, double[] lineColour) {
		super(parent);
		myPoints = new LinkedList<>();
		myLineColour = lineColour;

		double[] p1 = { 0, 0 };
		double[] p2 = { 1, 0 };
		myPoints.add(p1);
		myPoints.add(p2);
	}

	// Create a LineGameObject from (x1,y1) to (x2,y2)
	public LineGameObject(GameObject parent, double x1, double y1, double x2, double y2, double[] lineColour) {
		super(parent);
		myPoints = new LinkedList<>();
		myLineColour = lineColour;

		double[] p1 = { x1, y1 };
		double[] p2 = { x2, y2 };
		myPoints.add(p1);
		myPoints.add(p2);
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

	/**
	 * @return the myPoints
	 */
	public List<double[]> getMyPoints() {
		return myPoints;
	}

	/**
	 * @param myPoints
	 *            the myPoints to set
	 */
	public void setMyPoints(List<double[]> myPoints) {
		this.myPoints = myPoints;
	}

	/**
	 * TODO: Draw the polygon
	 * 
	 * if the fill colour is non-null, fill the polygon with this colour if the
	 * line colour is non-null, draw the outline with this colour
	 * 
	 * @see ass1.GameObject#drawSelf(javax.media.opengl.GL2)
	 */
	@Override
	public void drawSelf(GL2 gl) {

		// TODO: Write this method
		if (myLineColour != null) {
			// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glColor4d(myLineColour[0], myLineColour[1], myLineColour[2], myLineColour[3]);
			gl.glBegin(GL2.GL_LINE_STRIP);
			{
				for (int i = 0; i < myPoints.size(); i++) {
					double x = myPoints.get(i)[0];
					double y = myPoints.get(i)[1];
					gl.glVertex3d(x, y, 0);
				}

			}
			gl.glEnd();
		}

	}

}
