package uk.digitalsquid.ninepatcher.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.TranscoderException;

/**
 * Something that can render an image
 * @author william
 *
 * @param <S> The source of this renderer
 */
public abstract class ImageLoader {
	
	/**
	 * Renders an image to a bufferedImage
	 * @param width
	 * @param height
	 * @return
	 * @throws TranscoderException
	 */
	public BufferedImage renderImage(int width, int height) throws TranscoderException {
		if(width < 10) width = 10;
		if(height < 10) height = 10;
		return internalRenderImage(width, height);
	}
	
	protected abstract BufferedImage internalRenderImage(int width, int height) throws TranscoderException;
	
	public abstract Dimension getSize();
	
	/**
	 * Returns true if antialiasing should be used for this image source
	 * @return
	 */
	public abstract boolean requiresAntialiasing();
	
	/**
	 * Saves the image to the specified location.
	 * @param destination
	 * @param type The type as according to Exporter
	 * @param width
	 * @param height
	 * @return
	 * @throws TranscoderException if an error occurs. The message should be shown to the user.
	 */
	public final boolean exportImage(final Session session, String destination, int type, int width, int height) throws TranscoderException {
		try {
			BufferedImage inner = internalRenderImage(width, height);
			BufferedImage outer = new BufferedImage(width+2, height+2, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2 = outer.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(inner, 1, 1, width, height, new ImageObserver() {
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y,
						int width, int height) {
					return false;
				}
			});
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
			// Draw 9-patches. Add 1 because of border
			
			g2.setColor(Color.BLACK);
			
			// X top
			g2.drawLine(
					(int) ((float)width * session.stretchX.getMin() + 1),
					0,
					(int) ((float)width * session.stretchX.getMax() + 1),
					0);
			// Y Left
			g2.drawLine(
					0,
					(int) ((float)height * session.stretchY.getMin() + 1),
					0,
					(int) ((float)height * session.stretchY.getMax() + 1));
			// X Bottom
			g2.drawLine(
					(int) ((float)width * session.contentX.getMin() + 1),
					height+1, // Full width - 1
					(int) ((float)width * session.contentX.getMax() + 1),
					height+1); // Full width - 1
			// Y Right
			g2.drawLine(
					width+1, // Full width - 1
					(int) ((float)height * session.contentY.getMin() + 1),
					width+1, // Full width - 1
					(int) ((float)height * session.contentY.getMax() + 1));
			
			g2.dispose();
			
			// Create parent folders if necessary
			File export = new File(destination);
			File folder = export.getParentFile();
			if(folder != null) folder.mkdirs();
			
			switch(type) {
			case Exporter.IMG_PNG:
				ImageIO.write(outer, "PNG", export);
				break;
			case Exporter.IMG_JPG:
				ImageIO.write(outer, "JPEG", export);
				break;
			case Exporter.IMG_GIF:
				ImageIO.write(outer, "gif", export);
				break;
			}
			
		} catch (TranscoderException e) {
			e.printStackTrace();
			throw new TranscoderException("Failed to transcode images");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TranscoderException("Failed to save images");
		}
		return true;
	}
}
