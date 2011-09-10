/*
 * This file is part of Patchworker
 * Patchworker - easily draw 9-patch images for Android development
 * Copyright (C) 2011 Will Shackleton
 *
 * Patchworker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Patchworker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Patchworker, in the file COPYING.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.patchworker.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import uk.digitalsquid.patchworker.Session;
import uk.digitalsquid.patchworker.util.misc.Shapes;
import uk.digitalsquid.patchworker.util.misc.MinMax.OnMinMaxChangeListener;

/**
 * A panel that draws and allows changes to 9-patches.
 * @author william
 *
 */
public class NinePatchPanel extends ImagePanel implements MouseListener, MouseMotionListener, OnMinMaxChangeListener {

	private static final long serialVersionUID = -8794427011188729128L;
	
	/**
	 * How much border to put around the edge of the image
	 */
	private static final int BORDER_SIZE = 20;

	public NinePatchPanel(Session session) {
		super(session, BORDER_SIZE);
		addMouseListener(this);
		addMouseMotionListener(this);
		session.contentX.addListener(this);
		session.contentY.addListener(this);
		session.stretchX.addListener(this);
		session.stretchY.addListener(this);
	}
	
	private static final Color HANDLE_PAINT = new Color(255, 255, 255);
	private static final Color STRETCH_PAINT = new Color(0, 255, 0);
	private static final Color STRETCH_PAINT_BG = new Color(0, 255, 0, 50);
	private static final Color CONTENT_PAINT = new Color(255, 0, 0);
	private static final Color CONTENT_PAINT_BG = new Color(255, 0, 0, 80);
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// If user doesn't want 9-patch, just return here.
		if(!showNinePatchParts) return;
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setClip(0, 0, getWidth(), getHeight());
		// Rectangle imageArea = getImagePos();
		Rectangle a = getImagePos();
		
		g2.setColor(HANDLE_PAINT);
		
		if(a.width * a.height != 0) { // Don't draw if no image
			// Left
			g2.translate(+(a.x), +(a.y + session.stretchY.getMin() * a.height));
			g2.fillPolygon(Shapes.leftArrow1);
			g2.translate(-(a.x), -(a.y + session.stretchY.getMin() * a.height));
			g2.translate(+(a.x), +(a.y + session.stretchY.getMax() * a.height));
			g2.fillPolygon(Shapes.leftArrow2);
			g2.translate(-(a.x), -(a.y + session.stretchY.getMax() * a.height));
			// Right
			g2.translate(+(a.x + a.width), +(a.y + session.contentY.getMin() * a.height));
			g2.fillPolygon(Shapes.rightArrow1);
			g2.translate(-(a.x + a.width), -(a.y + session.contentY.getMin() * a.height));
			g2.translate(+(a.x + a.width), +(a.y + session.contentY.getMax() * a.height));
			g2.fillPolygon(Shapes.rightArrow2);
			g2.translate(-(a.x + a.width), -(a.y + session.contentY.getMax() * a.height));
			// Top
			g2.translate(+(a.x + session.stretchX.getMin() * a.width), +(a.y));
			g2.fillPolygon(Shapes.topArrow1);
			g2.translate(-(a.x + session.stretchX.getMin() * a.width), -(a.y));
			g2.translate(+(a.x + session.stretchX.getMax() * a.width), +(a.y));
			g2.fillPolygon(Shapes.topArrow2);
			g2.translate(-(a.x + session.stretchX.getMax() * a.width), -(a.y));
			// Bottom
			g2.translate(+(a.x + session.contentX.getMin() * a.width), +(a.y + a.height));
			g2.fillPolygon(Shapes.bottomArrow1);
			g2.translate(-(a.x + session.contentX.getMin() * a.width), -(a.y + a.height));
			g2.translate(+(a.x + session.contentX.getMax() * a.width), +(a.y + a.height));
			g2.fillPolygon(Shapes.bottomArrow2);
			g2.translate(-(a.x + session.contentX.getMax() * a.width), -(a.y + a.height));
			
			// Draw stretch
			g2.setStroke(new BasicStroke(2));
			// Stretch Y
			{
				int yPos1 = (int) (a.y + session.stretchY.getMin() * a.height);
				int yPos2 = (int) (a.y + session.stretchY.getMax() * a.height);
				g2.setColor(STRETCH_PAINT_BG);
				if(!session.stretchY.isLocked()) // Only need if fill will be visible
					g2.fillRect(a.x, yPos1, a.width, yPos2 - yPos1);
				g2.setColor(STRETCH_PAINT);
				g2.drawRect(a.x, yPos1, a.width, yPos2 - yPos1);
			}
			// Stretch X
			{
				int xPos1 = (int) (a.x + session.stretchX.getMin() * a.width);
				int xPos2 = (int) (a.x + session.stretchX.getMax() * a.width);
				g2.setColor(STRETCH_PAINT_BG);
				if(!session.stretchX.isLocked()) // Only need if fill will be visible
					g2.fillRect(xPos1, a.y, xPos2 - xPos1, a.height);
				g2.setColor(STRETCH_PAINT);
				g2.drawRect(xPos1, a.y, xPos2 - xPos1, a.height);
			}
			
			// Content
			{
				int x = (int) (a.x + session.contentX.getMin() * a.width);
				int y = (int) (a.y + session.contentY.getMin() * a.height);
				int w = (int) (session.contentX.gap() * a.width);
				int h = (int) (session.contentY.gap() * a.height);
				
				/*
				g2.setColor(CONTENT_PAINT_BG);
				g2.fillRect(a.x, y, a.width, h);
				g2.fillRect(x, a.y, w, a.height);
				g2.setColor(CONTENT_PAINT);
				g2.drawRect(a.x, y, a.width, h);
				g2.drawRect(x, a.y, w, a.height);
				*/
				g2.setColor(CONTENT_PAINT_BG);
				g2.fillRect(x, y, w, h);
				g2.setColor(CONTENT_PAINT);
				g2.drawRect(x, y, w, h);
			}
		}
	}
	
	// Catchment areas for resizes
	private static final Rectangle HANDLE_LEFT_CATCH1 = new Rectangle(-20, -20, 20, 20);
	private static final Rectangle HANDLE_LEFT_CATCH2 = new Rectangle(-20, 0, 20, 20);
	private static final Rectangle HANDLE_TOP_CATCH1 = new Rectangle(-20, -20, 20, 20);
	private static final Rectangle HANDLE_TOP_CATCH2 = new Rectangle(0, -20, 20, 20);
	
	private static final Rectangle HANDLE_RIGHT_CATCH1 = new Rectangle(0, -20, 20, 20);
	private static final Rectangle HANDLE_RIGHT_CATCH2 = new Rectangle(0, 0, 20, 20);
	private static final Rectangle HANDLE_BOTTOM_CATCH1 = new Rectangle(-20, 0, 20, 20);
	private static final Rectangle HANDLE_BOTTOM_CATCH2 = new Rectangle(0, 0, 20, 20);
	
	private static final int STRETCH_X_MIN = 1;
	private static final int STRETCH_X_MAX = 2;
	private static final int STRETCH_Y_MIN = 3;
	private static final int STRETCH_Y_MAX = 4;
	
	private static final int CONTENT_X_MIN = 5;
	private static final int CONTENT_X_MAX = 6;
	private static final int CONTENT_Y_MIN = 7;
	private static final int CONTENT_Y_MAX = 8;
	
	private int selectedItem = -1;

	@Override public void mouseClicked(MouseEvent e) { }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
	@Override public void mouseMoved(MouseEvent e) { }
	@Override public void minMaxChanged() { }

	@Override
	public void mousePressed(MouseEvent e) {
		// Compute which handle was selected.
		Rectangle a = getImagePos();
		// Inner, user selected areas
		int cx = (int) (a.x + session.contentX.getMin() * a.width);
		int cy = (int) (a.y + session.contentY.getMin() * a.height);
		int cw = (int) (session.contentX.gap() * a.width);
		int ch = (int) (session.contentY.gap() * a.height);
		
		// Inner, user selected areas (stretches)
		int sx = (int) (a.x + session.stretchX.getMin() * a.width);
		int sy = (int) (a.y + session.stretchY.getMin() * a.height);
		int sw = (int) (session.stretchX.gap() * a.width);
		int sh = (int) (session.stretchY.gap() * a.height);
		// Outer, image size based areas
		int x2 = a.x;
		int y2 = a.y;
		int w2 = a.width;
		int h2 = a.height;
		
		int mx = e.getX();
		int my = e.getY();
		
		if(HANDLE_LEFT_CATCH1.contains(mx - x2, my - sy))
			selectedItem = STRETCH_Y_MIN;
		if(HANDLE_LEFT_CATCH2.contains(mx - x2, my - (sy+sh)))
			selectedItem = STRETCH_Y_MAX;
		if(HANDLE_TOP_CATCH1.contains(mx - sx, my - y2))
			selectedItem = STRETCH_X_MIN;
		if(HANDLE_TOP_CATCH2.contains(mx - (sx+sw), my - y2))
			selectedItem = STRETCH_X_MAX;
		
		if(HANDLE_RIGHT_CATCH1.contains(mx - (x2+w2), my - cy))
			selectedItem = CONTENT_Y_MIN;
		if(HANDLE_RIGHT_CATCH2.contains(mx - (x2+w2), my - (cy+ch)))
			selectedItem = CONTENT_Y_MAX;
		if(HANDLE_BOTTOM_CATCH1.contains(mx - cx, my - (y2+h2)))
			selectedItem = CONTENT_X_MIN;
		if(HANDLE_BOTTOM_CATCH2.contains(mx - (cx+cw), my - (y2+h2)))
			selectedItem = CONTENT_X_MAX;
		
		mouseDragged(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		selectedItem = -1;
	}

	/**
	 * Calculates and updates UI
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		Rectangle a = getImagePos();
		switch(selectedItem) {
		case STRETCH_X_MIN:
			session.stretchX.setMin((float)(e.getX() - a.x) / a.width);
			repaint();
			break;
		case STRETCH_X_MAX:
			session.stretchX.setMax((float)(e.getX() - a.x) / a.width);
			repaint();
			break;
		case STRETCH_Y_MIN:
			session.stretchY.setMin((float)(e.getY() - a.y) / a.height);
			repaint();
			break;
		case STRETCH_Y_MAX:
			session.stretchY.setMax((float)(e.getY() - a.y) / a.height);
			repaint();
			break;
			
		case CONTENT_X_MIN:
			session.contentX.setMin((float)(e.getX() - a.x) / a.width);
			repaint();
			break;
		case CONTENT_X_MAX:
			session.contentX.setMax((float)(e.getX() - a.x) / a.width);
			repaint();
			break;
		case CONTENT_Y_MIN:
			session.contentY.setMin((float)(e.getY() - a.y) / a.height);
			repaint();
			break;
		case CONTENT_Y_MAX:
			session.contentY.setMax((float)(e.getY() - a.y) / a.height);
			repaint();
			break;
		}
	}
	
	/**
	 * Redraws when a minMax status was changed.
	 */
	@Override
	public void lockChanged() {
		repaint();
	}
	
	protected boolean showNinePatchParts = true;

	// Redraw UI to show/hide drag pins etc.
	@Override
	public void drawingNinePatch(boolean isNinePatch) {
		showNinePatchParts = isNinePatch;
		repaint();
	}
}
