package uk.digitalsquid.ninepatcher.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.filechooser.FileFilter;

import uk.digitalsquid.ninepatcher.util.Session;

/**
 * Main UI element in the program
 * @author william
 *
 */
public class MainWindow extends JFrame implements WindowListener {
	private static final long serialVersionUID = -5010616265178392396L;
	
	private Session session = new Session();
	
	NinePatchPanel imagePanel;
	
	public MainWindow() {
		setSize(200, 200);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		loadComponents();
		
		imagePanel = new NinePatchPanel(session);
		getContentPane().add(imagePanel, BorderLayout.CENTER);
	}
	
	/**
	 * Loads the UI
	 */
	private void loadComponents() {
		// Menus
		{
			JMenuBar mb = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			fileMenu.add("Load image").addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					FileFilter fileFilter = new FileFilter() {
						
						@Override
						public String getDescription() {
							return "Images (PNG, JPG, GIF, SVG)";
						}
						
						@Override
						public boolean accept(File f) {
							String name = f.getName();
							if(name.endsWith(".svg")) return true;
							if(name.endsWith(".png")) return true;
							if(name.endsWith(".jpg")) return true;
							if(name.endsWith(".gif")) return true;
							return false;
						}
					};
					fileChooser.setFileFilter(fileFilter);
					
					// Load document if accepted.
					if(fileChooser.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION) {
						File imageFile = fileChooser.getSelectedFile();
						session.loadDocument(imageFile.getAbsolutePath());
					}
				}
			});
			mb.add(fileMenu);
			getContentPane().add(mb, BorderLayout.NORTH);
		}
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
}
