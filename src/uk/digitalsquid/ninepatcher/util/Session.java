package uk.digitalsquid.ninepatcher.util;

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

import uk.digitalsquid.ninepatcher.FileEvents;
import uk.digitalsquid.ninepatcher.util.misc.MinMax;
import uk.digitalsquid.ninepatcher.util.processing.ProcessingMessage;
import uk.digitalsquid.ninepatcher.util.processing.ProcessingThread;

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
	
	private String uri;
	
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
	}
	
	public Session() {
		thread = new ProcessingThread();
	}

	public int getType() {
		return type;
	}

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
					try {
						// Load document
						doc = loader.loadDocument("file://" + uri);
					} catch (final IOException e) {
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
					return new SvgLoader(doc);
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
	};
}
