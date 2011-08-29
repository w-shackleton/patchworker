package uk.digitalsquid.ninepatcher;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import uk.digitalsquid.ninepatcher.ui.MainWindow;

public final class Application {
	/**
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		MainWindow win = new MainWindow();
		
		// Set system UI if possible
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) { // Just revert to metal UI
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
		
		win.setVisible(true);
	}
}
