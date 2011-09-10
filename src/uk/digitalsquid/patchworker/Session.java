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

package uk.digitalsquid.patchworker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgentAdapter;
import org.w3c.dom.Document;

import uk.digitalsquid.patchworker.img.ImageLoader;
import uk.digitalsquid.patchworker.img.RasterLoader;
import uk.digitalsquid.patchworker.img.SvgLoader;
import uk.digitalsquid.patchworker.util.misc.MinMax;
import uk.digitalsquid.patchworker.util.processing.ProcessingMessage;
import uk.digitalsquid.patchworker.util.processing.ProcessingThread;

/**
 * Holds info about the current file session
 * @author william
 *
 */
public final class Session {
	public final ProcessingThread thread;
	
	public static final int TYPE_SVG = 1;
	public static final int TYPE_RASTER = 2;
	
	private int type;
	
	/**
	 * Indicates if we should be exporting and making a 9patch, or just exporting different sized images.
	 */
	private boolean isNinePatch = true;
	
	private String uri;
	
	private String destination;
	
	private ImageLoader loader;
	
	/**
	 * The nine-patch content X area
	 */
	public final MinMax contentX = new MinMax();
	/**
	 * The nine-patch content Y area
	 */
	public final MinMax contentY = new MinMax();
	/**
	 * The nine-patch stretch X area
	 */
	public final MinMax stretchX = new MinMax();
	/**
	 * The nine-patch stretch Y area
	 */
	public final MinMax stretchY = new MinMax();
	
	public Session(ProcessingThread thread) {
		this.thread = thread;
		stretchX.setLocked(true);
		stretchY.setLocked(true);
		destination = PrefMgr.getExportUri();
	}
	
	public Session() {
		thread = new ProcessingThread();
		stretchX.setLocked(true);
		stretchY.setLocked(true);
		destination = PrefMgr.getExportUri();
	}

	public int getType() {
		return type;
	}

	/**
	 * Sets the resource folder in which to store drawables (project/res). Also sets in preferences.
	 * @param destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
		PrefMgr.setExportUri(destination);
	}

	public String getDestination() {
		return destination;
	}

	/**
	 * Sets whether the user wants to draw 9patches or normal images.
	 * @param isNinePatch
	 */
	public void setNinePatch(boolean isNinePatch) {
		this.isNinePatch = isNinePatch;
		broadcast.drawingNinePatch(isNinePatch);
	}

	public boolean isNinePatch() {
		return isNinePatch;
	}

	/**
	 * Loads a file given by the filename
	 * @param uri
	 */
	public void loadDocument(final String uri) {
		this.uri = uri;
		if(uri.endsWith(".svg")) {
			type = TYPE_SVG;
			// Load document on thread.
			broadcast.fileOpening();
			// Load in background, notify once finished - constructor adds to queue
			new ProcessingMessage<SvgLoader>(thread) {
	
				@Override
				public SvgLoader run() {
					DocumentLoader loader = new DocumentLoader(new UserAgentAdapter());
					Document doc = null;
					SvgLoader svg = null;
					try {
						// Load document
						doc = loader.loadDocument(new File(uri).toURI().toASCIIString());
						svg = new SvgLoader(doc);
						svg.renderImage(1, 1); // Fully load document tree - could be done better?
					} catch (final Exception e) { // IOException, TranscoderException
						// Notify of failure
						try {
							SwingUtilities.invokeAndWait(new Runnable() {
								@Override
								public void run() {
									broadcast.openFailed(e.getMessage());
								}
							});
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
					return svg;
				}
	
				@Override
				public void done(SvgLoader result) {
					loader = result;
					broadcast.fileOpened();
				}
			};
		} else { // Should only be rasters now.
			type = TYPE_RASTER;
			
			// Load document on thread.
			broadcast.fileOpening();
			// Load in background, notify once finished - constructor adds to queue
			new ProcessingMessage<BufferedImage>(thread) {
				@Override
				public BufferedImage run() {
					try {
						return ImageIO.read(new File(uri));
					} catch (IOException e) {
						e.printStackTrace();
						broadcast.openFailed(e.getMessage());
						return null;
					}
				}
				
				@Override
				public void done(BufferedImage result) {
					loader = new RasterLoader(result);
					broadcast.fileOpened();
				}
			};
		}
	}

	public String getUri() {
		return uri;
	}
	
	/**
	 * Adds a listener to file events
	 * @param l
	 */
	public void addListener(FileEvents l) {
		broadcastList.add(l);
	}
	/**
	 * Removes a file event listener
	 * @param l
	 */
	public void removeListener(FileEvents l) {
		broadcastList.remove(l);
	}

	public ImageLoader getLoader() {
		return loader;
	}
	
	public boolean isFileLoaded() {
		return loader != null;
	}

	private List<FileEvents> broadcastList = new LinkedList<FileEvents>();
	
	/**
	 * Resets the session objects of the 9 patch positions.
	 */
	private void resetLocations() {
		stretchX.setMinMax(0.5f, 0.5f);
		stretchY.setMinMax(0.5f, 0.5f);
		contentX.setMinMax(0.25f, 0.75f);
		contentY.setMinMax(0.25f, 0.75f);
	}
	
	/**
	 * Calls to these functions broadcast to all registered receivers
	 */
	private FileEvents broadcast = new FileEvents() {
		
		@Override
		public void openFailed(String message) {
			for(FileEvents i : broadcastList) {
				i.openFailed(message);
			}
		}
		
		@Override
		public void fileOpening() {
			for(FileEvents i : broadcastList) {
				i.fileOpening();
			}
		}
		
		@Override
		public void fileOpened() {
			resetLocations();
			
			for(FileEvents i : broadcastList) {
				i.fileOpened();
			}
		}

		@Override
		public void drawingNinePatch(final boolean isNinePatch) {
			for(FileEvents i : broadcastList) {
				i.drawingNinePatch(isNinePatch);
			}
		}
	};
}
