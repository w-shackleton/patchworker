package uk.digitalsquid.ninepatcher.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class FileLoadingDialog extends JDialog {
	private static final long serialVersionUID = -7142909793317157942L;

	public FileLoadingDialog() {
		setTitle("Loading image");
		
		getContentPane().add(new JLabel("Loading image..."), BorderLayout.CENTER);
		setSize(200, 70);
		validate();
	}
}
