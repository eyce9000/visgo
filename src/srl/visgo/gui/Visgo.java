package srl.visgo.gui;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PText;

public class Visgo extends JFrame {

	public static void main(String[] args){
		Visgo visgo = new Visgo();
		visgo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		visgo.setSize(600,400);
		visgo.setVisible(true);
	}
	Visgo(){
		super("Visgo");
		final PCanvas canvas = new PCanvas();
		final PText text = new PText("Hello World");
		canvas.getLayer().addChild(text);
		canvas.getLayer().addChild(new PText("Test"));
		canvas.addMouseWheelListener(new MouseWheelListener(){
			float MIN_SCALE = .0001f;
			float MAX_SCALE = 2500;
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				double currentScale = canvas.getCamera().getViewScale();
				double scaleDelta = (1.0f + (-0.01f * arg0.getWheelRotation()));
				double newScale = currentScale * scaleDelta;

				if (newScale < MIN_SCALE)
				{
					canvas.getCamera().setViewScale(MIN_SCALE);
					return;
				}
				if ((MAX_SCALE > 0) && (newScale > MAX_SCALE))
				{
					canvas.getCamera().setViewScale(MAX_SCALE);
					return;
				}
				canvas.getCamera().scaleAboutPoint(scaleDelta, arg0.getX(), arg0.getY());
				//canvas.getCamera().scaleViewAboutPoint();
				//ea.Handled = true;

			}

		});
		add(canvas);
	}

	PInputEventListener inputEventListener = new PInputEventListener(){

		@Override
		public void processEvent(PInputEvent arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	};
}
