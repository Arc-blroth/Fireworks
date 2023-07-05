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

public class Firework extends AbstractSprite {
	
	private Color color;
	private int sizecounter = 1;
	private int size = new Random().nextInt(10) + 6;
	private final int timePerStep = new Random().nextInt(15) + 36;
	private long startTime = System.currentTimeMillis();
	private int offX, offY;

	private Firework(int x, int y, int zindex, double size, Image img, Canvas c) {
		super(x, y, zindex, size, img, c);
	}
	
	public Firework(Canvas c) {
		this(1, 1, 0, 1, null, c);
		Random r = new Random();
		this.goTo(((double) r.nextDouble() * 2D - 0.5D) * (double) c.getGrid().getLength(),
				((double) r.nextDouble() * 2D - 0.5D) * (double) c.getGrid().getWidth());
		Color cr = new Color(Color.HSBtoRGB(r.nextFloat(), 1F - (2F * r.nextFloat() / 5F), 1F - (r.nextFloat() / 5F)));
		color = new Color(cr.getRed(), cr.getGreen(), cr.getBlue(), Math.round((0.9F - (r.nextFloat() / 4F)) * 255));
		c.addSprite(this, 0);
	}
	
	@Override
	public void paint(int x, int y, int w, int h, Graphics g) {
		super.paint(x, y, w, h, g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(color);
		
		for(int c = sizecounter; c >= sizecounter - 2; c--) {
			for(int d = 0; d < 360; d += 36) {
				g2.fillOval(
						(offX + x - 6) + (int) Math.round(Math.sin(d * Math.PI / 180) * c * size),
						(offY + y - 6) + (int) Math.round(Math.cos(d * Math.PI / 180) * c * size),
					12, 12);
			}
		}
	}
	
	public void setOffset(int offX, int offY) {
		this.offX = offX + (size - 8) / 32;
		this.offY = offY + (size - 8) / 32;
	}

	public void step(Iterator<Firework> iterator) {
		long timePassed = System.currentTimeMillis() - startTime;
		if (timePassed >= timePerStep) {
			sizecounter++;
			if(sizecounter > 10) {
				getCanvas().removeSprite(this);
				iterator.remove();
			}
			startTime = System.currentTimeMillis();
		}
	}

}
