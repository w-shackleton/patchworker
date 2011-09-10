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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.Date;

import org.apache.batik.transcoder.TranscoderException;

import uk.digitalsquid.patchworker.FileEvents;
import uk.digitalsquid.patchworker.Session;
import uk.digitalsquid.patchworker.img.ImageLoader;
import uk.digitalsquid.patchworker.util.processing.ProcessingMessage;

public class ImagePanel extends PatternPanel implements ComponentListener, FileEvents {
	private static final long serialVersionUID = -5315690396212360662L;
	
	protected final Session session;
	
	protected final int border;
	
	public ImagePanel(Session session, int border) {
		this.border = border;
		this.session = session;
		session.addListener(this);
		addComponentListener(this);
	}
	
	private ImageLoader renderer;
	
	private BufferedImage image;
	
	public void setImageRenderer(ImageLoader renderer) {
		this.renderer = renderer;
		renderImageInBackground();
	}
	
	private Rectangle drawingArea = new Rectangle();
	private Rectangle imagePos = new Rectangle();
	
	protected Rectangle getImagePos() {
		return imagePos;
	}
	
	/**
	 * Computes the position to draw the image in.
	 * @return
	 */
	private Rectangle computeImagePosition() {
		Dimension imageSize = renderer.getSize();
		drawingArea.x = border;
		drawingArea.y = border;
		drawingArea.width  = getWidth()  - border - border;
		drawingArea.height = getHeight() - border - border;
		
		float widthRatio = (float)drawingArea.width / (float)imageSize.width;
		float heightRatio = (float)drawingArea.height / (float)imageSize.height;
		if(widthRatio < heightRatio) { // Shrink vertically
			imagePos.x = drawingArea.x;
			imagePos.width = drawingArea.width;
			
			imagePos.height = drawingArea.width * imageSize.height / imageSize.width;
			imagePos.y = (getHeight() - imagePos.height) / 2;
		} else {
			imagePos.y = drawingArea.y;
			imagePos.height = drawingArea.height;
			
			imagePos.width = drawingArea.height * imageSize.width / imageSize.height;
			imagePos.x = (getWidth() - imagePos.width) / 2;
		}
		
		// Set drawingArea to show real border.
		drawingArea.x = imagePos.x - border;
		drawingArea.y = imagePos.y - border;
		drawingArea.width = imagePos.width + border + border;
		drawingArea.height = imagePos.height + border + border;
		return drawingArea;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		setDrawingRegion(imagePos, 10);
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if(image != null) {
			computeImagePosition();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					renderer.requiresAntialiasing() ?
							RenderingHints.VALUE_ANTIALIAS_ON :
								RenderingHints.VALUE_ANTIALIAS_OFF);
			g.drawImage(image, imagePos.x, imagePos.y, imagePos.width, imagePos.height, this);
		}
	}

	@Override public void componentHidden(ComponentEvent e) { }
	@Override public void componentMoved(ComponentEvent e) { }
	@Override public void componentShown(ComponentEvent e) { }

	@Override
	public void componentResized(ComponentEvent e) {
		redrawImage();
	}
	
	/**
	 * Draws the picture to the screen.
	 */
	private void redrawImage() {
		if(renderer != null) {
			computeImagePosition();
			renderImageInBackground();
		}
	}
	
	private Date timeOfLastRender;
	
	/**
	 * Renders the image with the specified renderer.
	 */
	private void renderImageInBackground() {
		final Date now = new Date();
		if(timeOfLastRender != null && 
			now.getTime() - timeOfLastRender.getTime() < 400) return;
		
		// if queue is full, don't render now.
		if(session.thread.isFull()) {
			repaint();
			return;
		}
		
		ProcessingMessage<BufferedImage> msg = new ProcessingMessage<BufferedImage>() {
			@Override
			public BufferedImage run() {
				try {
					return renderer.renderImage(imagePos.width, imagePos.height);
				} catch (TranscoderException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			public void done(BufferedImage result) {
				image = result;
				timeOfLastRender = now;
				repaint();
			}
		};
		try {
			session.thread.queueMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fileOpening() { }

	@Override
	public void fileOpened() {
		renderer = session.getLoader();
		redrawImage();
	}

	@Override
	public void openFailed(String reason) {
		renderer = null;
		image = null;
		repaint();
	}

	// Ignore this at this level.
	@Override public void drawingNinePatch(boolean isNinePatch) { }
}
