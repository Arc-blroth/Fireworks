package fireworks;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import base.canvas.Canvas;
import base.swing.*;

public class Main {

	private Canvas canvas;
	private double mouseAccelX, mouseAccelY;
	private double previousMouseX, previousMouseY = 0;
	private double mouseX, mouseY;
	private double mouseOffsetX, mouseOffsetY;
	private static final double motionOffsetX = 0.1;
	private static final double motionOffsetY = 0.1;
	public static final int TOTAL_FIREWORKS = 24;

	public static void main(String[] args) {
		new Main().go();
	}

	public Main() {
		Image i = null;
		try {i = ImageIO.read(Firework.class.getResource("Firework.png"));} catch (IOException e) {e.printStackTrace();}
		canvas = Init.getInit("Fireworks - Happy 4th of July!", null, i, 16, 16).Go(true);
		canvas.setBackground(Color.BLACK);
		canvas.getInputMap(canvas.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
		canvas.getActionMap().put("exit", new AbstractAction() { @Override public void actionPerformed(ActionEvent arg0) {System.exit(0);}});
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				mouseX = (((double) me.getX() / (double) canvas.getWidth()) - 0.5D) * 2D;
				mouseY = (((double) me.getY() / (double) canvas.getHeight()) - 0.5D) * 2D;
				mouseAccelX += (mouseX - previousMouseX);
				mouseAccelY += (mouseY - previousMouseY);
				previousMouseX = mouseX;
				previousMouseY = mouseY;
			}
		});
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
		
		JFrame f = canvas.getFrame();
		f.setVisible(false);
		f.dispose();
		f.setAlwaysOnTop(true);
		f.setAutoRequestFocus(true);
		f.setUndecorated(true);
		f.setCursor(blankCursor);
		f.setVisible(true);
	}

	public void go() {
		ArrayList<Firework> fireworks = new ArrayList<Firework>();
		try {
			
			class Fireworking extends Thread {

				private final int timePerStep = 25;
				private long startTime = System.currentTimeMillis();

				public void run() {
					try {
						Starfield sf = new Starfield(canvas);
						canvas.addSprite(sf, 0);
						for(;;) {

							long timePassed = System.currentTimeMillis() - startTime;
							if (timePassed >= timePerStep) {
								mouseOffsetX += mouseAccelX;
								mouseOffsetY += mouseAccelY;
								mouseAccelX *= 0.9;
								mouseAccelY *= 0.9;
								startTime = System.currentTimeMillis();
							}

							if (fireworks.size() < TOTAL_FIREWORKS)
								fireworks.add(new Firework(canvas));

							int offX = (int) Math.round(mouseOffsetX * canvas.getWidth() * motionOffsetX);
							int offY = (int) Math.round(mouseOffsetY * canvas.getHeight() * motionOffsetY);

							sf.setOffset(offX, offY);

							for (Iterator<Firework> iterator = fireworks.iterator(); iterator.hasNext();) {
								Firework fw = iterator.next();
								fw.setOffset(offX, offY);
								fw.step(iterator);
							}
							canvas.revalidate();
							canvas.repaint();
						}
					} catch (Exception e) {e.printStackTrace();}
				}
			}
			
			new Fireworking().start();
			
		} catch (Exception e) {e.printStackTrace();}
		
	}
}
