package uk.digitalsquid.ninepatcher.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A JPanel that draws a transparent-type background behind the image.
 * @author william
 *
 */
public class PatternPanel extends JPanel {
	private static final long serialVersionUID = -7391412724434651252L;
	
	public PatternPanel() {
		area = new Rectangle();
		size = 5;
	}
	
	private static final int PATTERN_SIZE = 40;
	
	//private static final Color dark  = new Color(0.4f, 0.4f, 0.4f);
	//private static final Color light = new Color(0.6f, 0.6f, 0.6f);
	private static final int dark  = 0xFF777777;
	private static final int light = 0xFFAAAAAA;
	
	private static final BufferedImage pattern = new BufferedImage(PATTERN_SIZE, PATTERN_SIZE, BufferedImage.TYPE_INT_RGB);
	
	static {
		// Set up pattern
		for(int y = 0; y < PATTERN_SIZE; y++) {
			for(int x = 0; x < PATTERN_SIZE; x++) {
				if((x + y) % 2 == 0)
					pattern.setRGB(x, y, dark);
				else
					pattern.setRGB(x, y, light);
			}
		}
	}
	
	/**
	 * Draw the BG in the specified area.
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setClip(area.x, area.y, area.width, area.height);
		for(int y = area.y; y < area.y + area.height; y += size * PATTERN_SIZE) {
			for(int x = area.x; x < area.x + area.width; x += size * PATTERN_SIZE) {
				g.drawImage(pattern, x, y, size * PATTERN_SIZE, size * PATTERN_SIZE, this);
			}
		}
	}
	
	private Rectangle area;
	
	private int size;
	
	/**
	 * Sets the area to draw the pattern in
	 * @param rect The area to draw in
	 * @param size The size of each square
	 */
	protected void setDrawingRegion(Rectangle rect, int size) {
		if(size < 1) size = 1;
		this.size = size;
		
		area = rect;
	}
}
