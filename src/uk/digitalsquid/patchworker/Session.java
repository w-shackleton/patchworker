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
	private final MinMax contentX;
	/**
	 * The nine-patch content Y area
	 */
	private final MinMax contentY;
	/**
	 * The nine-patch stretch X area
	 */
	private MinMax[] stretchX;
	/**
	 * The nine-patch stretch Y area
	 */
	private MinMax[] stretchY;
	
	public Session(ProcessingThread thread) {
		contentX = new MinMax(broadcast);
		contentY = new MinMax(broadcast);
		stretchX = new MinMax[1];
		stretchX[0] = new MinMax(broadcast);
		stretchX[0].setLocked(true);
		stretchY = new MinMax[1];
		stretchY[0] = new MinMax(broadcast);
		stretchY[0].setLocked(true);
		
		this.thread = thread;
		destination = PrefMgr.getExportUri();
	}
	
	public Session() {
		contentX = new MinMax(broadcast);
		contentY = new MinMax(broadcast);
		stretchX = new MinMax[1];
		stretchX[0] = new MinMax(broadcast);
		stretchX[0].setLocked(true);
		stretchY = new MinMax[1];
		stretchY[0] = new MinMax(broadcast);
		stretchY[0].setLocked(true);
		
		thread = new ProcessingThread();
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
		saveFilePreferences(); // Saves old file settings if there is an open file
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
					
					loadFilePreferences();
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
					
					loadFilePreferences();
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
		resetLocations(false);
	}
	
	/**
	 * Resets the session objects of the 9 patch positions.
	 * @param onlyStretch If true, only resets the stretch areas.
	 */
	private void resetLocations(boolean onlyStretch) {
		// Spread out over equal area
		for(int i = 0; i < getStretchXCount(); i++) {
			getStretchX(i).setMinMax(((float)i+1) / ((float)getStretchXCount()+1));
		}
		for(int i = 0; i < getStretchYCount(); i++) {
			getStretchY(i).setMinMax(((float)i+1) / ((float)getStretchYCount()+1));
		}
		if(!onlyStretch) {
			getContentX().setMinMax(0.25f, 0.75f);
			getContentY().setMinMax(0.25f, 0.75f);
		}
	}
	
	public MinMax getContentX() {
		return contentX;
	}

	public MinMax getContentY() {
		return contentY;
	}

	/**
	 * Gets the X stretch areas. Don't modify the array contents; the {@link MinMax} themselves can be modified however.
	 * @return
	 */
	public MinMax[] getStretchX() {
		return stretchX;
	}
	
	/**
	 * Gets the nth X stretch area
	 * @param index
	 * @return
	 */
	public MinMax getStretchX(int index) {
		return stretchX[index];
	}
	public int getStretchXCount() {
		return stretchX.length;
	}
	/**
	 * Sets the number of stretch areas on the X
	 * @param number
	 */
	public synchronized void setStretchXCount(int number) {
		MinMax copyFrom = stretchY[0]; // Should never be less than 1
		stretchX = new MinMax[number];
		for(int i = 0; i < number; i++) {
			stretchX[i] = new MinMax(broadcast);
			stretchX[i].copySettingsFrom(copyFrom);
		}
		resetLocations(true);
		broadcast.minMaxCountChanged();
	}

	/**
	 * Gets the Y stretch areas. Don't modify the array contents; the {@link MinMax} themselves can be modified however.
	 * @return
	 */
	public MinMax[] getStretchY() {
		return stretchY;
	}
	/**
	 * Gets the nth Y stretch area
	 * @param index
	 * @return
	 */
	public MinMax getStretchY(int index) {
		return stretchY[index];
	}
	public int getStretchYCount() {
		return stretchY.length;
	}
	/**
	 * Sets the number of stretch areas on the Y
	 * @param number
	 */
	public synchronized void setStretchYCount(int number) {
		MinMax copyFrom = stretchY[0]; // Should never be less than 1
		stretchY = new MinMax[number];
		for(int i = 0; i < number; i++) {
			stretchY[i] = new MinMax(broadcast);
			stretchY[i].copySettingsFrom(copyFrom);
		}
		resetLocations(true);
		broadcast.minMaxCountChanged();
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

		@Override
		public void minMaxChanged() {
			for(FileEvents i : broadcastList) {
				i.minMaxChanged();
			}
		}

		@Override
		public void minMaxLockChanged() {
			for(FileEvents i : broadcastList) {
				i.minMaxLockChanged();
			}
		}

		@Override
		public void minMaxCountChanged() {
			for(FileEvents i : broadcastList) {
				i.minMaxCountChanged();
			}
		}
	};
	
	/**
	 * If an image is open, saves the current {@link MinMax} settings and user options to the (class)
	 * so that they can be loaded next time
	 */
	public void saveFilePreferences() {
		if(!isFileLoaded()) return;
		System.out.println("Writing preferences for image " + uri);
		PrefMgr.setImagePreferences(uri, new SavedState(this));
	}
	
	public void loadFilePreferences() {
		System.out.println("Loading preferences for image " + uri);
		SavedState restore = PrefMgr.getImagePreferences(uri);
		contentX.copyFrom(restore.getContentX());
		contentY.copyFrom(restore.getContentY());
		stretchX = MinMax.cloneArray(restore.getStretchX());
		stretchY = MinMax.cloneArray(restore.getStretchY());
		setDestination(restore.getDestination());
		setNinePatch(restore.isNinePatch());
	}
}
