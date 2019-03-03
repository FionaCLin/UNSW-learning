package ass2.spec;

import java.awt.Dimension;

import com.jogamp.opengl.glu.GLU;

public class Camera {
	// the compass direction of the camera in degrees
	private double angle = 0;
	private double camerax;
	private double cameray;
	private double cameraz;
	private Avatar person;
	private double step = .1;
	private double dstAbovePerson = 3;
	private double dstAwayPerson = 4;
	private double angleAroundPerson = 10;
	private boolean isfollow;

	public Camera() {

	}

	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle
	 *            the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @return the camerax
	 */
	public double getCamerax() {
		return camerax;
	}

	/**
	 * @param camerax
	 *            the camerax to set
	 */
	public void setCamerax(double camerax) {
		this.camerax = camerax;
	}

	private void setCameray(double d) {
		cameray = d;
	}

	/**
	 * @return the cameraz
	 */
	public double getCameraz() {
		return cameraz;
	}

	/**
	 * @param cameraz
	 *            the cameraz to set
	 */
	public void setCameraz(double cameraz) {
		this.cameraz = cameraz;
	}

	public void setCamera(Terrain myTerrain) {
		GLU glu = new GLU();
		double[] centre = { 5, 0, 5 };
		double[] eyes = { 5, 0, 15 };

		eyes[0] = camerax;
		eyes[1] += cameray; // Minimum height of camera.
		eyes[2] = cameraz;

		// System.out.println("height = " + eyes[1]);

		// Compass direction.
		double[] dir = { 0, 0, 0 };

		if (!isfollow) {

			dir[0] = Math.sin(Math.toRadians(angle));
			dir[1] = 0;
			dir[2] = -Math.cos(Math.toRadians(angle));

			centre[0] = eyes[0] + dir[0];
			centre[1] = eyes[1] + dir[1];
			centre[2] = eyes[2] + dir[2];

		} else {
			double[] camera = person.getMyPos(); // look at person

			dir[0] = Math.sin(Math.toRadians(-angle));
			dir[1] = -.5;
			dir[2] = Math.cos(Math.toRadians(-angle));

			eyes[0] = camera[0] + this.dstAwayPerson * dir[0];
			eyes[1] = camera[1] + this.dstAbovePerson;
			eyes[2] = camera[2] + this.dstAwayPerson * dir[2];
			double h = myTerrain.altitude(eyes[0], eyes[2])+2;
			eyes[1] = Math.max(h, eyes[1]);
			centre[0] = camera[0] + dir[0];
			centre[1] = camera[1] + dir[1];
			centre[2] = camera[2] + dir[2];

		}
		glu.gluLookAt(eyes[0], eyes[1], eyes[2], centre[0], centre[1], centre[2], 0.0, 1.0, 0.0);
	}

	public void up(double h) {

		double dirx = Math.sin(Math.toRadians(angle));
		double dirz = -Math.cos(Math.toRadians(angle));
		cameray = h + 2;
		camerax += dirx * step;
		cameraz += dirz * step;
		double[] pos = { camerax, cameray - 2, cameraz };
		person.setMyPos(pos, angle);
	}

	public void down(double h) {

		double dirx = Math.sin(Math.toRadians(angle));
		double dirz = -Math.cos(Math.toRadians(angle));
		cameray = h + 2;
		camerax -= dirx * step;
		cameraz -= dirz * step;
		double[] pos = { camerax, cameray - 2, cameraz };
		person.setMyPos(pos, angle);

	}

	public void right(double h) {
		h = Math.max(h, cameray - 2);
		cameray = h + 2;
		angle = (angle + 10) % 360;
		double[] pos = { camerax, h, cameraz };
		person.setMyPos(pos, angle);
	}
	public void left(double h) {
		h = Math.max(h, cameray - 2);
		cameray = h + 2;
		angle = (angle - 10) % 360;
		double[] pos = { camerax, h, cameraz };
		person.setMyPos(pos, angle);
	}

	/**
	 * @param person
	 *            the person to set
	 */
	public void setPerson(Avatar person) {
		this.person = person;
		setCamerax(person.getMyPos()[0]);
		setCameray(person.getMyPos()[1] + .5); // set min height
		setCameraz(person.getMyPos()[2]);
	}

	public void rightAngleAroundPerson() {
		angleAroundPerson = (angleAroundPerson + 10) % 360;
	}

	public void leftAngleAroundPerson() {
		angleAroundPerson = (angleAroundPerson - 10) % 360;
	}

	public void setFollow() {
		isfollow = !isfollow;
	}

	public boolean isFollow() {
		return isfollow;
	}
}
