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

package uk.digitalsquid.patchworker.img;

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

import uk.digitalsquid.patchworker.Session;

/**
 * Something that can render an image
 * @author william
 * 
 */
public abstract class ImageLoader {
	
	/**
	 * Renders an image to a bufferedImage
	 * @param width
	 * @param height
	 * @return The new image
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
	 * @return <code>true</code> if it is required
	 */
	public abstract boolean requiresAntialiasing();
	
	/**
	 * Saves the image to the specified location.
	 * @param destination
	 * @param type The type as according to Exporter
	 * @param width
	 * @param height
	 * @return true on success
	 * @throws TranscoderException if an error occurs. The message should be shown to the user.
	 */
	public final boolean exportImage(final Session session, String destination, boolean exportNinePatch, int type, int width, int height) throws TranscoderException {
		try {
			BufferedImage inner = internalRenderImage(width, height);
			BufferedImage outer = exportNinePatch ?
					new BufferedImage(width+2, height+2, BufferedImage.TYPE_INT_ARGB) :
					new BufferedImage(width  , height  , BufferedImage.TYPE_INT_ARGB); // If not 9patch, create same size image
			
			Graphics2D g2 = outer.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			/**
			 * Where to export the image on the new one
			 */
			final int exportPos = exportNinePatch ? 1 : 0;
			g2.drawImage(inner, exportPos, exportPos, width, height, new ImageObserver() {
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y,
						int width, int height) {
					return false;
				}
			});
			
			if(exportNinePatch) {
				// Draw 9-patches if required. Add 1 because of border
				
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				
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
				
			}
			g2.dispose();
			
			// Create parent folders if necessary
			File export = new File(destination);
			File folder = export.getParentFile();
			if(folder != null) folder.mkdirs();
			
			switch(type) {
			default:
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
