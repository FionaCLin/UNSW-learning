package ass2.spec;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */


import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;


public class Tree {
	private Texture myTextures[] = new Texture[2];
    private double[] myPos;

    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }

    public void setTextures(Texture leaves, Texture trunk){
    	myTextures[0] = leaves;
		myTextures[1] = trunk;
    }
    public double[] getPosition() {
        return myPos;
    }

	public void drawTree(GL2 gl) {
		gl.glPushMatrix();
		// Turn on OpenGL texturing.
			gl.glTranslated(myPos[0], myPos[1], myPos[2]);
			gl.glScaled(.35, .35, .35);
			gl.glTranslated(0, 2.5, 0);
			gl.glColor3f(102f / 255, 0, 51f / 255);
			// bind the texture
			gl.glBindTexture(GL.GL_TEXTURE_2D, myTextures[0].getTextureId());

			GLU glu = new GLU();
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricTexture(quadric, true);
            glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
            glu.gluSphere(quadric, 1, 10, 10);

			
			gl.glPushMatrix();
				gl.glTranslated(0, -.5, 0);
				gl.glRotated(90, 1, 0, 0);
				// bind the texture
				gl.glBindTexture(GL.GL_TEXTURE_2D, myTextures[1].getTextureId());

				gl.glColor3f(102f / 255, 51f / 255, 0);
				glu.gluQuadricTexture(quadric, true);
	            glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
				glu.gluCylinder(quadric,.5,.5,2, 20, 20);
			gl.glPopMatrix();
		gl.glPopMatrix();

	}

}