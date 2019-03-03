package ass2.spec;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

import ass2.spec.Game.Model;

public class Sun {
	// terrain center is x/2, z/2
	private double[] terrain_centre;
	private double angle = 0;
	private static double interval = 180 / 11;
	private double [][]myColor={{1d,0d,0d},{1d,128/255d,0d},{1d,1d,0d},
			{128/255d,1d,0d},{0d,1d,128/255d},{0d,1d,1d},{0d,0d,1d},{0.5d,0d,1d},{0.5d,0d,1d},
			{1d,0d,1d},{1d,0d,0.5d},{0.5d,0.5d,0.5d}};
	private int index = 0;

	public Sun(double[] pos) {
		terrain_centre = pos;
	}

	public void setIndex(int i){
		index = i;
	}

	public void drawSun(GL2 gl, GLUT glut) {
		gl.glPushMatrix();
		gl.glRotated(angle, 0, 0, terrain_centre[2]);
		// shift along sun.x = - 4*terrain.x
		gl.glTranslated(-2 * terrain_centre[0], 0, -10);
		// rotate an angel around z = terrain.z / 2 axie,
		gl.glColor4d(myColor[index][0],myColor[index][1],myColor[index][2],1);

		glut.glutSolidSphere(1.0, 20, 20);
		gl.glPopMatrix();
	}

	// set the rotate angle
	public void up() {
		angle = (angle - interval) % 180;
		System.out.println("sun angle " + angle + " interval " + interval);
		System.out.println(myColor[0][0]);
	}



}
