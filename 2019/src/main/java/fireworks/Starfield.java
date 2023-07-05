package fireworks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Timer;

import base.canvas.AbstractSprite;
import base.canvas.Canvas;

public class Starfield extends AbstractSprite {
	
	private static final Color starColor = new Color(255, 254, 242);
	private int offX, offY;
	private ArrayList<Coordinate> stars;
	private static final int NUMBER_OF_STARS = 125;

	private Starfield(int x, int y, int zindex, double size, Image img, Canvas c) {
		super(x, y, zindex, size, img, c);
	}
	
	public Starfield(Canvas c) {
		this(1, 1, 0, 1, null, c);
		stars = new ArrayList<Coordinate>(NUMBER_OF_STARS);
		Random r = new Random();
		for(int i = 0; i < NUMBER_OF_STARS; i++) {
			Coordinate star = new Coordinate(
					(double) r.nextDouble(),
					(double) r.nextDouble()
				);
			stars.add(star);
		}
	}
	
	@Override
	public void paint(int x, int y, int w, int h, Graphics g) {
		super.paint(x, y, w, h, g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(starColor);
		for(Coordinate star : stars) {
			g2.fillOval(
					(int) Math.round(star.x * getCanvas().getWidth() + offX - 2),
					(int) Math.round(star.y * getCanvas().getHeight() + offY - 2),
					4, 4);
		}
	}
	
	public void setOffset(int offX, int offY) {
		this.offX = (int) Math.round(offX * 0.2);
		this.offY = (int) Math.round(offY * 0.2);
	}

}
