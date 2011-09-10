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

import java.io.File;

import javax.swing.SwingUtilities;

import org.apache.batik.transcoder.TranscoderException;

import uk.digitalsquid.patchworker.Session;
import uk.digitalsquid.patchworker.util.processing.ProcessingMessage;

/**
 * Exports images to the specified place.
 * @author william
 *
 */
public final class Exporter extends ProcessingMessage<String> {
	
	public static final int IMG_JPG = 1;
	public static final int IMG_PNG = 2;
	public static final int IMG_GIF = 3;
	
	/**
	 * Export folder names
	 */
	private static final String DRAWABLE_LDPI = "drawable-ldpi";
	private static final String DRAWABLE_MDPI = "drawable-mdpi";
	private static final String DRAWABLE_HDPI = "drawable-hdpi";
	private static final String DRAWABLE_XDPI = "drawable-xhdpi";
	
	private final int fileType;
	
	private final Session session;
	
	/**
	 * <code>true</code> if this size should be rendered.
	 */
	private final boolean ldpi, mdpi, hdpi, xdpi;
	
	/**
	 * The size in DIP
	 */
	private final int dipx, dipy;
	
	/**
	 * The callback to send status messages to.
	 */
	private final ExportStatus callback;
	
	/**
	 * The name of the images to create.
	 */
	private final String imageName;
	
	/**
	 * The folder where resources are located in the project. (/res)
	 */
	private final String resFolder;
	
	/**
	 * If true, export 9patch. Otherwise export normal images.
	 */
	private final boolean isNinePatch;
	
	public Exporter(Session session, String imageName, ExportStatus callback, int fileType, boolean ldpi, boolean mdpi, boolean hdpi, boolean xdpi, int dipx, int dipy) {
		this.session = session;
		this.imageName = imageName;
		this.callback = callback;
		this.resFolder = session.getDestination();
		this.isNinePatch = session.isNinePatch();
		this.ldpi = ldpi;
		this.mdpi = mdpi;
		this.hdpi = hdpi;
		this.xdpi = xdpi;
		
		this.dipx = dipx;
		this.dipy = dipy;
		
		this.fileType = fileType;
		
		if(dipx < 1 || dipy < 1)
			throw new IllegalArgumentException("Bad sizes");
		
		if(!new File(resFolder).isDirectory())
			throw new IllegalArgumentException("Path doesn't exist");
	}
	
	/**
	 * Saves an image, scaling it by the specified amount
	 * @param scale
	 * @throws TranscoderException 
	 */
	protected void saveImage(float scale, int type, String destination) throws TranscoderException {
		int sx = (int) ((float)dipx * scale);
		int sy = (int) ((float)dipy * scale);
		
		session.getLoader().exportImage(session, destination, isNinePatch, type, sx, sy);
	}

	/**
	 * Starts the worker.
	 * @return a string with an error, or null if completed.
	 */
	@Override
	public String run() {
		String extension = "";
		switch(fileType) {
		case IMG_JPG: extension = isNinePatch ? ".9.jpg" : ".jpg"; break;
		case IMG_PNG: extension = isNinePatch ? ".9.png" : ".png"; break;
		case IMG_GIF: extension = isNinePatch ? ".9.gif" : ".gif"; break;
		}
		String filename = imageName + extension;
		String path;
		
		try {
			if(ldpi) {
				callbackUIProxy.exportStarted(ExportStatus.IMAGE_LDPI);
				path = resFolder + "/" + DRAWABLE_LDPI + "/" + filename;
				saveImage(0.75f, fileType, path);
				callbackUIProxy.exportFinished(ExportStatus.IMAGE_LDPI);
			}
			if(mdpi) {
				callbackUIProxy.exportStarted(ExportStatus.IMAGE_MDPI);
				path = resFolder + "/" + DRAWABLE_MDPI + "/" + filename;
				saveImage(1f, fileType, path);
				callbackUIProxy.exportFinished(ExportStatus.IMAGE_MDPI);
			}
			if(hdpi) {
				callbackUIProxy.exportStarted(ExportStatus.IMAGE_HDPI);
				path = resFolder + "/" + DRAWABLE_HDPI + "/" + filename;
				saveImage(1.5f, fileType, path);
				callbackUIProxy.exportFinished(ExportStatus.IMAGE_HDPI);
			}
			if(xdpi) {
				callbackUIProxy.exportStarted(ExportStatus.IMAGE_XDPI);
				path = resFolder + "/" + DRAWABLE_XDPI + "/" + filename;
				saveImage(2f, fileType, path);
				callbackUIProxy.exportFinished(ExportStatus.IMAGE_XDPI);
			}
		} catch(TranscoderException e) {
			return e.getMessage();
		}
		
		try {
			Thread.sleep(400); // Wait so user can see that process is done
		} catch (InterruptedException e) { }
		
		return null;
	}

	@Override
	public void done(String result) {
		if(result == null) callback.finished();
		else callback.error(result);
	}
	
	public static interface ExportStatus {
		public static final int IMAGE_LDPI = 1;
		public static final int IMAGE_MDPI = 2;
		public static final int IMAGE_HDPI = 3;
		public static final int IMAGE_XDPI = 4;
		
		public void exportStarted(int image);
		public void exportFinished(int image);
		
		public void finished();
		
		public void error(String error);
	}
	
	private final ExportStatus callbackUIProxy = new ExportStatus() {
		
		@Override
		public void finished() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					callback.finished();
				}
			});
		}
		
		@Override
		public void exportStarted(final int image) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					callback.exportStarted(image);
				}
			});
		}
		
		@Override
		public void exportFinished(final int image) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					callback.exportFinished(image);
				}
			});
		}
		
		@Override
		public void error(final String error) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					callback.error(error);
				}
			});
		}
	};
}
