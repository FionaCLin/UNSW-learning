package ass2.spec;

import java.awt.Dimension;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Terrain {

	private Dimension mySize;
	private double[][] myAltitude;
	private List<Tree> myTrees;
	private List<Road> myRoads;
	private float[] mySunlight;
	private Texture myTexture;
	// terrain size range (10, 20)

	// VBO

	private int numFace;
	private int maxVertices;

	private FloatBuffer verticesBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer texBuffer;
	private int bufferIds[] = new int[1];

	// Variables needed for using our shaders
	private static final String VERTEX_SHADER = "src/ass2/spec/VertexTex.glsl";
	private static final String FRAGMENT_SHADER = "src/ass2/spec/FragmentTex.glsl";
	private int texUnitLoc;

	private int shaderprogram;
	private float[] texCoords = { 0, 0, 1, 0, 1, 1 };

	/**
	 * Create a new terrain
	 *
	 * @param width
	 *            The number of vertices in the x-direction
	 * @param depth
	 *            The number of vertices in the z-direction
	 */
	public Terrain(int width, int depth) {
		mySize = new Dimension(width, depth);
		myAltitude = new double[width][depth];
		myTrees = new ArrayList<Tree>();
		myRoads = new ArrayList<Road>();
		mySunlight = new float[3];

		// set up the buffer size (VBO)
		maxVertices = (int) ((mySize.getHeight() - 1) * (mySize.getWidth() - 1) * 6);
		verticesBuffer = FloatBuffer.allocate(maxVertices * 3);
		normalsBuffer = FloatBuffer.allocate(maxVertices * 3);
		texBuffer = FloatBuffer.allocate(maxVertices * 2);

	}

	public Terrain(Dimension size) {
		this(size.width, size.height);
	}

	public Dimension size() {
		return mySize;
	}

	public List<Tree> trees() {
		return myTrees;
	}

	public List<Road> roads() {
		return myRoads;
	}

	public float[] getSunlight() {
		return mySunlight;
	}

	/**
	 * Set the sunlight direction.
	 * 
	 * Note: the sun should be treated as a directional light, without a
	 * position
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void setSunlightDir(float dx, float dy, float dz) {
		mySunlight[0] = dx;
		mySunlight[1] = dy;
		mySunlight[2] = dz;
	}

	/**
	 * Resize the terrain, copying any old altitudes.
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		mySize = new Dimension(width, height);
		double[][] oldAlt = myAltitude;
		myAltitude = new double[width][height];

		for (int i = 0; i < width && i < oldAlt.length; i++) {
			for (int j = 0; j < height && j < oldAlt[i].length; j++) {
				myAltitude[i][j] = oldAlt[i][j];
			}
		}
	}

	/**
	 * Get the altitude at a grid point
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public double getGridAltitude(int x, int z) {
		return myAltitude[x][z];
	}

	/**
	 * Set the altitude at a grid point
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public void setGridAltitude(int x, int z, double h) {
		myAltitude[x][z] = h;
	}

	/**
	 * Get the altitude at an arbitrary point. Non-integer points should be
	 * interpolated from neighbouring grid points
	 * 
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public double altitude(double x, double z) {
		double altitude = 0;
		int x1 = (int) x;
		int z1 = (int) z;

		if ((x1 < mySize.getWidth() - 1 && z1 < mySize.getHeight() - 1) && (x1 > 0 && z1 > 0)) {
			// Problem index out of bond when it hits the largest conner
			// A==> vertex {x1, this.myAltitued[x1][z1],z1}
			// B==> vertex {x1, this.myAltitued[x1][z1+1],z1+1}
			double ya = interpolate(z1, z, z1 + 1, this.myAltitude[x1][z1], this.myAltitude[x1][z1 + 1]);
			// new vertex { x1, ya, z}

			// A==> vertex {x2, this.myAltitued[x2][z1],z1}
			// C==> vertex {x2, this.myAltitued[x2][z1+1],z1+1}
			double yb = interpolate(z1, z, z1 + 1, this.myAltitude[x1 + 1][z1], this.myAltitude[x1 + 1][z1 + 1]);
			// new vertex {x2, yb, z}

			// new vertex { x1, ya, z}
			// new vertex {x2, yb, z}
			altitude = interpolate(x1, x, x1 + 1, ya, yb);
		}
		return altitude;
	}

	private double interpolate(double x1, double x, double x2, double y1, double y2) {
		return (x - x1) / (x2 - x1) * y2 + (x2 - x) / (x2 - x1) * y1;
	}

	/**
	 * Add a tree at the specified (x,z) point. The tree's y coordinate is
	 * calculated from the altitude of the terrain at that point.
	 * 
	 * @param x
	 * @param z
	 */
	public void addTree(double x, double z) {
		double y = altitude(x, z);
		Tree tree = new Tree(x, y, z);
		myTrees.add(tree);
	}

	/**
	 * Add a road.
	 * 
	 * @param x
	 * @param z
	 */
	public void addRoad(double width, double[] spine) {
		Road road = new Road(width, spine, this);
		myRoads.add(road);
	}

	public double[][][] vertex_mesh() {
		int i = 0;
		int w = this.mySize.width;
		int d = this.mySize.height;

		double[][][] verties = new double[(w - 1) * (d - 1) * 2][4][4];
		for (int x = 0; x < w - 1; x++) {
			for (int z = 0; z < d - 1; z++) {
				double[] vertexa = { x, this.myAltitude[x][z], z, 1 };
				verties[i][0] = vertexa;
				double[] vertexb = { x, this.myAltitude[x][z + 1], z + 1, 1 };
				verties[i][1] = vertexb;
				double[] vertexc = { x + 1, this.myAltitude[x + 1][z + 1], z + 1, 1 };
				verties[i][2] = vertexc;
				double[] normal1 = normal(vertexa, vertexb, vertexc);
				verties[i++][3] = normal1;

				double[] vertexd = { x + 1, this.myAltitude[x + 1][z], z, 1 };
				verties[i][0] = vertexc;
				verties[i][1] = vertexd;
				verties[i][2] = vertexa;
				double[] normal2 = normal(vertexc, vertexd, vertexa);
				verties[i++][3] = normal2;
				numFace += 2;
			}
		}
		return verties;
	}

	public void generateBuffers(GL2 gl) {

		generateData();

		// Generate 1 VBO buffer and get its ID
		gl.glGenBuffers(1, bufferIds, 0);

		// This buffer is now the current array buffer
		// array buffers hold vertex attribute data
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
		int size = maxVertices * 8 * Float.BYTES;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, size, null, GL2.GL_STATIC_DRAW);
		size = maxVertices * 3 * Float.BYTES;
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, maxVertices * 3 * Float.BYTES, verticesBuffer);
		size = maxVertices * Float.BYTES;
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, maxVertices * 3 * Float.BYTES, maxVertices * Float.BYTES,
				normalsBuffer);
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, maxVertices * 6 * Float.BYTES, maxVertices * 2 * Float.BYTES, texBuffer);

	}

	private void generateData() {

		double[][][] verties = vertex_mesh();

		for (int i = 0; i < verties.length; i++) {
			double[] normal = verties[i][3];

			for (int j = 0; j < 3; j++) {
				float[] textCoord = { texCoords[j * 2], texCoords[j * 2 + 1] };
				texBuffer.put(textCoord[0]);
				texBuffer.put(textCoord[1]);
				normalsBuffer.put((float) normal[0]);
				normalsBuffer.put((float) normal[1]);
				normalsBuffer.put((float) normal[2]);
				double[] vertex = verties[i][j];
				verticesBuffer.put((float) vertex[0]);
				verticesBuffer.put((float) vertex[1]);
				verticesBuffer.put((float) vertex[2]);
			}
		}
		verticesBuffer.rewind();
		texBuffer.rewind();
		normalsBuffer.rewind();
	}

	public void draw(GL2 gl) {

		generateBuffers(gl);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);

		try {
			shaderprogram = Shader.initShaders(gl, VERTEX_SHADER, FRAGMENT_SHADER);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		texUnitLoc = gl.glGetUniformLocation(shaderprogram, "texUnit");

		//Use the shader.
        gl.glUseProgram(shaderprogram);
        //Tell the shader that our texUnit is the 0th one 
        //Since we are only using 1 texture it is texture 0
        gl.glUniform1i(texUnitLoc , 0);
      
		
		
		// Enable two vertex arrays: co-ordinates and color.
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

	
		// Specify locations for the co-ordinates and color arrays.
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0); // last num is the offset
		gl.glNormalPointer(GL.GL_FLOAT, 0, maxVertices * 3 * Float.BYTES);
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, maxVertices * 6 * Float.BYTES);
	    
		
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, maxVertices);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		// Disable these. Not needed in this example, but good practice.
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		// Unbind the buffer.
		// This is not needed in this simple example but good practice
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}

	public void dispose(GL2 gl) {

		gl.glDeleteBuffers(1, bufferIds, 0);
	}

	/*
	 * Some maths utility functions
	 * 
	 */

	double[] cross(double u[], double v[]) {
		double crossProduct[] = new double[3];
		crossProduct[0] = u[1] * v[2] - u[2] * v[1];
		crossProduct[1] = u[2] * v[0] - u[0] * v[2];
		crossProduct[2] = u[0] * v[1] - u[1] * v[0];

		return crossProduct;
	}

	// Find normal for planar polygon
	public double[] normal(double[] p0, double p1[], double p2[]) {
		double[] u = { p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2] };
		double[] v = { p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2] };
		double[] normal = cross(u, v);
		return normalise(normal);
	}

	double[] normalise(double[] n) {
		double mag = getMagnitude(n);
		double norm[] = { n[0] / mag, n[1] / mag, n[2] / mag };
		return norm;
	}

	double getMagnitude(double[] n) {
		double mag = n[0] * n[0] + n[1] * n[1] + n[2] * n[2];
		mag = Math.sqrt(mag);
		return mag;
	}

	public static double[] multiply(double[][] m, double[] v) {

		double[] u = new double[4];

		for (int i = 0; i < 4; i++) {
			u[i] = 0;
			for (int j = 0; j < 4; j++) {
				u[i] += m[i][j] * v[j];
			}
		}

		return u;
	}

	/**
	 * @param myTexture the myTexture to set
	 */
	public void setMyTexture(Texture myTexture) {
		this.myTexture = myTexture;
	}

}
