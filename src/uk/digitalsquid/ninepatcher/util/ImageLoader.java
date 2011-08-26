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
public abstract class ImageLoader<S> {
	/**
	 * Sets the image source to render from
	 * @param source
	 */
	abstract void setSource(S source);
	
	/**
	 * Renders an image to a bufferedImage
	 * @param width
	 * @param height
	 * @return
	 * @throws TranscoderException
	 */
	public BufferedImage renderImage(int width, int height) throws TranscoderException {
		if(width < 0) width = 1;
		if(height < 0) height = 1;
		return internalRenderImage(width, height);
	}
	
	protected abstract BufferedImage internalRenderImage(int width, int height) throws TranscoderException;
	
	public abstract Dimension getSize();
}
