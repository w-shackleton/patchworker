package uk.digitalsquid.ninepatcher.img;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts an SVG image to data
 * @author william
 *
 */
public class SvgLoader extends ImageLoader {
	
	private TranscoderInput source;
	
	private final Dimension size = new Dimension();
	
	public SvgLoader(Document document) {
		source = new TranscoderInput(document);
		generateBounds();
	}
	
	/**
	 * Generates the bounds (size) of the SVG
	 */
	private void generateBounds() {
		if(source == null) return;
		Element root = source.getDocument().getDocumentElement();
		String width = root.getAttributeNS(null, "width");
		String height = root.getAttributeNS(null, "height");
		if(width.equals("") && height.equals("")) {
			size.width = 1;
			size.height = 1;
		} else {
			try {
			size.width = Integer.parseInt(width);
			size.height = Integer.parseInt(height);
			} catch(NumberFormatException e) {
				size.width = 1;
				size.height = 1;
			}
		}
	}
	
	/**
	 * Renders the given image at the given size
	 * @param width
	 * @param height
	 * @return
	 */
	@Override
	protected BufferedImage internalRenderImage(int width, int height) throws TranscoderException {
		RawTranscoderOutput output = new RawTranscoderOutput();
		imageTranscoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float)width);
		imageTranscoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float)height);
		imageTranscoder.transcode(source, output);
		return output.getImage();
	}
	
	/**
	 * The transcoder to render the image
	 */
	private final RawImageTranscoder imageTranscoder = new RawImageTranscoder();
	
	/**
	 * An {@link ImageTranscoder} that outputs the same image it receives.
	 */
	private final class RawImageTranscoder extends ImageTranscoder {
		
		@Override
		public void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
			if(output instanceof RawTranscoderOutput) {
				((RawTranscoderOutput)output).setImage(img);
			}
			else
				throw new TranscoderException("Transcoder not a RawTranscoderOutput");
		}
		
		@Override
		public BufferedImage createImage(int width, int height) {
			return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
	};
	
	private class RawTranscoderOutput extends TranscoderOutput {
		private BufferedImage image;

		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public BufferedImage getImage() {
			return image;
		}
	}

	public void setSource(Document document) {
		source = new TranscoderInput(document);
		generateBounds();
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public boolean requiresAntialiasing() {
		return false;
	}
}
