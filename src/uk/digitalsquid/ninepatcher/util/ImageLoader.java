package uk.digitalsquid.ninepatcher.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

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
}
