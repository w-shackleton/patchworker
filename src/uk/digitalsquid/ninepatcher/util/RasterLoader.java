package uk.digitalsquid.ninepatcher.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;

/**
 * Loads a raster image
 * @author william
 *
 */
public class RasterLoader extends ImageLoader {
	
	private BufferedImage image;
	
	public RasterLoader(BufferedImage image) {
		this.image = image;
	}

	@Override
	protected BufferedImage internalRenderImage(int width, int height)
			throws TranscoderException {
		return image;
	}

	@Override
	public Dimension getSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	public BufferedImage getImage() {
		return image;
	}

	@Override
	public boolean requiresAntialiasing() {
		return true;
	}

}
