package ass2.spec;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import ass2.spec.Game.Model;

/**
 * COMMENT: Comment Road
 *
 * @author malcolmr, BrandonSandoval, James Shin
 */
public class Road {
	private List<Double> myPoints;
	private double myWidth;
	private Texture myTexture;
	private Terrain myTerrain;

	// VBO

	private int maxVertices;

	private FloatBuffer verticesBuffer;
	private FloatBuffer texBuffer;
	private int bufferIds[] = new int[1];

	// Variables needed for using our shaders
	private static final String VERTEX_SHADER = "src/ass2/spec/VertexTex.glsl";
	private static final String FRAGMENT_SHADER = "src/ass2/spec/FragmentTex.glsl";
	private int texUnitLoc;

	private int shaderprogram;

	/**
	 * Create a new road starting at the specified point
	 */
	public Road(double width, double x0, double y0) {
		myWidth = width;
		myPoints = new ArrayList<Double>();
		myPoints.add(x0);
		myPoints.add(y0);
	}

	/**
	 * Create a new road with the specified spine
	 *
	 * @param width
	 * @param spine
	 */
	public Road(double width, double[] spine, Terrain myTerrain) {
		myWidth = width;
		myPoints = new ArrayList<Double>();
		for (int i = 0; i < spine.length; i++) {
			myPoints.add(spine[i]);
		}
		this.myTerrain = myTerrain;

		// set up the buffer size (VBO)
		maxVertices = 200;
		verticesBuffer = FloatBuffer.allocate(maxVertices * 3);
		texBuffer = FloatBuffer.allocate(maxVertices * 2);
	}

	/**
	 * The width of the road.
	 *
	 * @return
	 */
	public double width() {
		return myWidth;
	}

	/**
	 * Add a new segment of road, beginning at the last point added and ending
	 * at (x3, y3). (x1, y1) and (x2, y2) are interpolated as bezier control
	 * points.
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
		myPoints.add(x1);
		myPoints.add(y1);
		myPoints.add(x2);
		myPoints.add(y2);
		myPoints.add(x3);
		myPoints.add(y3);
	}

	/**
	 * Get the number of segments in the curve
	 *
	 * @return
	 */
	public int size() {
		return myPoints.size() / 6;
	}

	/**
	 * Get the specified control point.
	 *
	 * @param i
	 * @return
	 */
	public double[] controlPoint(int i) {
		double[] p = new double[2];
		p[0] = myPoints.get(i * 2);
		p[1] = myPoints.get(i * 2 + 1);
		return p;
	}

	/**
	 * Get a point on the spine. The parameter t may vary from 0 to size().
	 * Points on the kth segment take have parameters in the range (k, k+1).
	 *
	 * @param t
	 * @return
	 */
	public double[] point(double t) {
		int i = (int) Math.floor(t);
		t = t - i;

		i *= 6;

		double x0 = myPoints.get(i++);
		double y0 = myPoints.get(i++);
		double x1 = myPoints.get(i++);
		double y1 = myPoints.get(i++);
		double x2 = myPoints.get(i++);
		double y2 = myPoints.get(i++);
		double x3 = myPoints.get(i++);
		double y3 = myPoints.get(i++);

		double[] p = new double[2];

		p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
		p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;

		return p;
	}

	/**
	 * Calculate the Bezier coefficients
	 *
	 * @param i
	 * @param t
	 * @return
	 */
	private double b(int i, double t) {

		switch (i) {

		case 0:
			return (1 - t) * (1 - t) * (1 - t);

		case 1:
			return 3 * (1 - t) * (1 - t) * t;

		case 2:
			return 3 * (1 - t) * t * t;

		case 3:
			return t * t * t;
		}

		// this should never happen
		throw new IllegalArgumentException("" + i);
	}

	public void drawRoad(GL2 gl, int counter) {

		double y = myTerrain.altitude(point(0)[0], point(0)[1]);

		// Turn on OpenGL texturing.

		// Load textures
		gl.glEnable(GL2.GL_TEXTURE_2D);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

		// enable polygon offset for filled polygons
		gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
		// push this polygon to the front a little
		gl.glPolygonOffset(-1, -1);

		gl.glPushMatrix();
		{
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			{

				double[] start = point(0);
				double[] spinePoint;
				double rate = 0.5;

				for (double i = 0.01; i < 1; i += 0.01) {
					// fix y from getAtitute
					counter += 2;
					spinePoint = point(i);
					double[][] normals = normal(start, spinePoint);
					normals[0] = normalise(normals[0]);
					normals[1] = normalise(normals[1]);

					// get points on x,z coordinate
					double[] p = getPoint(normals[0], spinePoint, rate);
					double[] q = getPoint(normals[1], spinePoint, rate);

					gl.glTexCoord2d(i, 0);

					gl.glVertex3d(q[0], y, q[1]);

					gl.glTexCoord2d(i, 1);

					gl.glVertex3d(p[0], y, p[1]);
					int val = bound(p[0], myTerrain.size().width);

					start = spinePoint;
				}

			}
			gl.glEnd();
		}
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

	}

	public void generateBuffers(GL2 gl) {

		generateData();

		// Generate 1 VBO buffer and get its ID
		gl.glGenBuffers(1, bufferIds, 0);
		// Load textures

		// This buffer is now the current array buffer
		// array buffers hold vertex attribute data
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
		int size = maxVertices * 3 * Float.BYTES + maxVertices * Float.BYTES;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, size, null, GL2.GL_STATIC_DRAW);
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, maxVertices * 3 * Float.BYTES, verticesBuffer);
		size = maxVertices * 3 * Float.BYTES;
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, size, maxVertices * Float.BYTES, texBuffer);

	}

	public void generateData() {

		double[] start = point(0);
		double y = myTerrain.altitude(start[0], start[1]);
		double[] curve_point;
		double rate = 0.5;
		for (double i = 0.01; i < 1; i += 0.01) {
			// fix y from getAtitute
			curve_point = point(i);
			double[][] normals = normal(start, curve_point);
			normals[0] = normalise(normals[0]);
			normals[1] = normalise(normals[1]);

			// get points on x,z coordinate
			double[] p = getPoint(normals[0], curve_point, rate);
			double[] q = getPoint(normals[1], curve_point, rate);

			texBuffer.put((float) i);
			texBuffer.put((float) 0);

			texBuffer.put((float) i);
			texBuffer.put((float) 1);

			verticesBuffer.put((float) q[0]);
			verticesBuffer.put((float) y);
			verticesBuffer.put((float) q[1]);

			verticesBuffer.put((float) p[0]);
			verticesBuffer.put((float) y);
			verticesBuffer.put((float) p[1]);

			start = curve_point;

		}
		texBuffer.rewind();
		verticesBuffer.rewind();
	}

	// check the bound for arrary myAttitude[][]
	// without checking would suck my life
	public int bound(double x, double upbound) {
		if (x > upbound)
			return (int) upbound;
		if (x < upbound && x > 0)
			return (int) x;
		else
			return 0;

	}

	public double[][] normal(double[] spinePoint1, double[] spinePoint2) {

		double[][] normals = new double[2][2];
		double dx = spinePoint2[0] - spinePoint1[0];
		double dz = spinePoint2[1] - spinePoint1[1];

		normals[0][0] = -dz;
		normals[0][1] = dx;
		normals[1][0] = dz;
		normals[1][1] = -dx;

		return normals;
	}

	public double[] getPoint(double[] normal, double[] spinePoint, double rate) {

		return new double[] { spinePoint[0] + normal[0] * myWidth * rate, spinePoint[1] + normal[1] * myWidth * rate };
	}

	public void setTextures(Texture road) {
		myTexture = road;

	}

	/*
	 * double getMagnitude(double [] n){ double mag = n[0]*n[0] + n[1]*n[1] +
	 * n[2]*n[2]; mag = Math.sqrt(mag); return mag; }
	 */

	public double[] normalise(double[] v) {

		double mag = Math.sqrt(v[0] * v[0] + v[1] * v[1]);

		double[] norm = new double[2];

		norm[0] = v[0] / mag;
		norm[1] = v[1] / mag;

		return norm;
	}

	public void draw(GL2 gl) {

		generateBuffers(gl);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);

		// Load textures
		gl.glEnable(GL2.GL_TEXTURE_2D);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

		try {
			shaderprogram = Shader.initShaders(gl, VERTEX_SHADER, FRAGMENT_SHADER);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		texUnitLoc = gl.glGetUniformLocation(shaderprogram, "texUnit");
		// Use the shader.
		gl.glUseProgram(shaderprogram);
		// Tell the shader that our texUnit is the 0th one
		// Since we are only using 1 texture it is texture 0
		gl.glUniform1i(texUnitLoc, 0);

		// Enable two vertex arrays: co-ordinates and color.
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		// Specify locations for the co-ordinates and color arrays.
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0); // last num is the offset
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, maxVertices * 3 * Float.BYTES);
		// enable polygon offset for filled polygons
		gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
		// push this polygon to the front a little
		gl.glPolygonOffset(-1, -1);
		for (int i = 0; i < 100; i++) {
			gl.glDrawArrays(GL2.GL_QUAD_STRIP, i * 2, 4);
		}
		gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		// Disable these. Not needed in this example, but good practice.
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		// Unbind the buffer.
		// This is not needed in this simple example but good practice
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}

}