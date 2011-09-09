package uk.digitalsquid.ninepatcher.ui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class FileLoadingDialog extends JDialog {
	private static final long serialVersionUID = -7142909793317157942L;

	public FileLoadingDialog(JFrame parent) {
		super(parent);
		setTitle("Loading image");
		
		getContentPane().add(new JLabel("Loading image..."), BorderLayout.CENTER);
		
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.WEST);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		getContentPane().add(Box.createVerticalStrut(10), BorderLayout.NORTH);
		getContentPane().add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
		
		pack();
	}
}
