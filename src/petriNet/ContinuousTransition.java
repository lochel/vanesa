package petriNet;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class ContinuousTransition extends Transition {

	private final double delay = 1;

	public ContinuousTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.contoniousTransition);
		setModellicaString("PNlib.TC");
		setColor(Color.WHITE);
		shapes = new VertexShapes();
//		setShape(shapes.getDoubleRectangle(getVertex()));
		Rectangle bounds = getShape().getBounds();
		// System.out.println("hoehe: "+bounds.getHeight());
		// System.out.println("weite: "+bounds.getWidth());
		AffineTransform transform = new AffineTransform();
		// transform.translate(x2, y2 - bounds.getHeight() / 2);
		transform.scale(bounds.getWidth() * 3, bounds.getHeight());
		setShape(transform.createTransformedShape(getShape()));
	}

	public double getDelay() {
		return delay;
	}

	public void rebuildShape(VertexShapes vs) {
		Shape s = null; //vs.getDoubleRectangle(getVertex());
		// s.
		// Rectangle bounds = s.getBounds();
		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(1, 2);
		setShape(transform.createTransformedShape(s));
		// setShape(s);
	}
	
}
