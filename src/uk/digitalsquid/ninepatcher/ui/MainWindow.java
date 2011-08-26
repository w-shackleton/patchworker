package uk.digitalsquid.ninepatcher.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgentAdapter;
import org.w3c.dom.Document;

import uk.digitalsquid.ninepatcher.util.SvgLoader;
import uk.digitalsquid.ninepatcher.util.processing.ProcessingMessage;
import uk.digitalsquid.ninepatcher.util.processing.ProcessingThread;

/**
 * Main UI element in the program
 * @author william
 *
 */
public class MainWindow extends JFrame implements WindowListener {
	private static final long serialVersionUID = -5010616265178392396L;
	
	private ProcessingThread processing = new ProcessingThread();
	
	ImagePanel imagePanel;
	
	public MainWindow() {
		processing.start();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		imagePanel = new ImagePanel(processing, 5);
		getContentPane().add(imagePanel, BorderLayout.CENTER);
		
		loadImage("file:///home/william/netspoofIconPlain.svg");
	}

	@Override public void windowActivated(WindowEvent arg0) { }
	@Override public void windowDeactivated(WindowEvent arg0) { }
	@Override public void windowDeiconified(WindowEvent arg0) { }
	@Override public void windowIconified(WindowEvent arg0) { }
	@Override public void windowOpened(WindowEvent arg0) { }

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		dispose();
	}
	
	protected void loadImage(final String uri) {
		ProcessingMessage<SvgLoader> load = new ProcessingMessage<SvgLoader>() {

			@Override
			public SvgLoader run() {
				DocumentLoader loader = new DocumentLoader(new UserAgentAdapter());
				Document doc = null;
				try {
					doc = loader.loadDocument(uri);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return new SvgLoader(doc);
			}

			@Override
			public void done(SvgLoader result) {
				imagePanel.setImageRenderer(result);
			}
		};
		try {
			processing.queueMessage(load);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
