package ass1;

import java.util.Arrays;
import java.util.LinkedList;

import com.jogamp.opengl.GL2;

public class MyCoolGameObject extends GameObject {

	public MyCoolGameObject(GameObject parent) {
		super(parent);

	}

	public MyCoolGameObject() {
		super(GameObject.ROOT);

		ALL_OBJECTS.add(this);
	}

	public void drawSelf(GL2 gl) {
		// draw bear face
		// face
		double white[] = { 1, 1, 1, 1 };
		double black[] = { 0, 0, 0, 1 };
		double faceFillCol[] = { 102f / 255, 51f / 255, 0, 1 };
		double earFillCol[] = { 204f / 255, 102f / 255, 0, 1 };
		CircularGameObject face = drawFace(black, faceFillCol, earFillCol);
		drawEyebrows(black, face);
		drawEyes(white, black, face);
		drawMouth(black, earFillCol, face);

	}

	public void drawMouth(double[] black, double[] earFillCol, CircularGameObject face) {
		CircularGameObject mouth = new CircularGameObject(face, 1, earFillCol, earFillCol);
		mouth.setPosition(0, -0.7);
		mouth.setScale(0.75);

		PolygonalGameObject nose = drawNose(black, mouth);

		// mouth under nose
		LineGameObject mouth_p1 = new LineGameObject(mouth, black);
		mouth_p1.setPosition(0, -0.25);
		mouth_p1.rotate(90);
		mouth_p1.setScale(0.5);
		LineGameObject mouth_p2 = new LineGameObject(mouth, black);
		mouth_p2.setPosition(-0.25, -0.25);
		mouth_p2.setScale(0.5);
	}

	public PolygonalGameObject drawNose(double[] black, CircularGameObject mouth) {
		// nose under eyes
		double points[][] = { { 0.5, 0.5 }, { -0.5, 0.5 }, { -0.2, 0.2 }, { 0.2, 0.2 } };
		PolygonalGameObject nose = new PolygonalGameObject(mouth, Arrays.asList(points), black, black);
		nose.setScale(0.8);
		return nose;
	}

	public CircularGameObject drawFace(double[] black, double[] faceFillCol, double[] earFillCol) {
		CircularGameObject face = new CircularGameObject(this, 1.5, faceFillCol, faceFillCol);
		face.setScale(0.5);
		// ears top
		CircularGameObject left_ear = new CircularGameObject(face, 1, earFillCol, earFillCol);
		left_ear.setPosition(-1.25, 1.35);
		left_ear.setScale(0.5);
		CircularGameObject left_inner_ear = new CircularGameObject(left_ear, 0.4, black, black);

		CircularGameObject right_ear = new CircularGameObject(face, 1, earFillCol, earFillCol);
		right_ear.setPosition(1.25, 1.35);
		right_ear.setScale(0.5);
		CircularGameObject right_inner_ear = new CircularGameObject(right_ear, 0.4, black, black);
		return face;
	}

	public void drawEyes(double[] white, double[] black, CircularGameObject face) {
		// eyes center
		int move = (int) (Math.random() * 10) % 2 + 1;
		CircularGameObject left_eye = new CircularGameObject(face, 0.65, black, black);
		left_eye.setPosition(-0.6, 0.3);
		left_eye.setScale(0.5);
		CircularGameObject left_inner_eye = new CircularGameObject(left_eye, 0.2, white, white);
		left_inner_eye.setPosition(move * 0.2, 0);
		left_inner_eye.setScale(move*0.5);
		CircularGameObject right_eye = new CircularGameObject(face, 0.65, black, black);
		right_eye.setPosition(0.6, 0.3);
		right_eye.setScale(0.5);
		CircularGameObject right_inner_eye = new CircularGameObject(right_eye, 0.2, white, white);
		
		right_inner_eye.setPosition(move * 0.2, 0);
		right_inner_eye.setScale(move*0.5);
	}

	public void drawEyebrows(double[] black, CircularGameObject face) {
		int theta = (int) (Math.random() * 10) % 3;
		double left_points[][] = { { 0.6, 0.1 }, { -0.7, 0.1 }, { -0.5, 0.4 }, { 0.5, 0.5 } };
		PolygonalGameObject left_eye_brow = new PolygonalGameObject(face, Arrays.asList(left_points), black, black);
		left_eye_brow.setPosition(-0.6, 0.7);
		left_eye_brow.setRotation(theta * 3);
		left_eye_brow.setScale(0.5);
		double right_points[][] = { { 0.7, 0.1 }, { -0.6, 0.1 }, { -0.5, 0.5 }, { 0.5, 0.4 } };
		PolygonalGameObject right_eye_brow = new PolygonalGameObject(face, Arrays.asList(right_points), black, black);
		right_eye_brow.setPosition(0.6, 0.7);
		right_eye_brow.setRotation(theta * -3);
		right_eye_brow.setScale(0.5);
	}

}
