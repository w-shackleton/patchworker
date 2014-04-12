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
