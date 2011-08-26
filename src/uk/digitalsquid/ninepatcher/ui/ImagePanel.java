package uk.digitalsquid.ninepatcher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JPanel;

import org.apache.batik.transcoder.TranscoderException;

import uk.digitalsquid.ninepatcher.util.ImageLoader;
import uk.digitalsquid.ninepatcher.util.processing.ProcessingMessage;
import uk.digitalsquid.ninepatcher.util.processing.ProcessingThread;

public class ImagePanel extends JPanel implements ComponentListener {
	private static final long serialVersionUID = -5315690396212360662L;
	
	private final ProcessingThread processingThread;
	
	private final int border;
	
	public ImagePanel(ProcessingThread processingThread, int border) {
		this.border = border;
		this.processingThread = processingThread;
		addComponentListener(this);
	}
	
	private ImageLoader<?> renderer;
	
	private BufferedImage image;
	
	public void setImageRenderer(ImageLoader<?> renderer) {
		this.renderer = renderer;
	}
	
	private Rectangle drawingArea = new Rectangle();
	private Rectangle imagePos = new Rectangle();
	
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
		super.paintComponent(g);
		if(image != null) {
			computeImagePosition();
			g.drawImage(image, imagePos.x, imagePos.y, imagePos.width, imagePos.height, this);
		}
	}

	@Override public void componentHidden(ComponentEvent e) { }
	@Override public void componentMoved(ComponentEvent e) { }
	@Override public void componentShown(ComponentEvent e) { }

	@Override
	public void componentResized(ComponentEvent e) {
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
		if(processingThread.isFull()) {
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
			processingThread.queueMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
