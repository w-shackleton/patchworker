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

package uk.digitalsquid.patchworker.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.digitalsquid.patchworker.Application;
import uk.digitalsquid.patchworker.Session;
import uk.digitalsquid.patchworker.img.Exporter;
import uk.digitalsquid.patchworker.img.Exporter.ExportStatus;

public final class ExportDialog extends JDialog implements WindowListener, ExportStatus {

	private static final long serialVersionUID = -5624193207054979643L;
	
	private final Session session;
	
	private JSpinner sizexText, sizeyText;
	
	private int sizex, sizey;
	
	private boolean keepAspect = true;
	
	// Indicates which DPIs to export
	private boolean ldpi = true, mdpi = true, hdpi = true, xdpi = true;
	
	/**
	 * The name to save the image as.
	 */
	private String imageName;
	
	/**
	 * File type, as given by values in Exporter.java
	 */
	private int fileType;
	
	private JLabel ldpiStatus, mdpiStatus, hdpiStatus, xdpiStatus;
	
	private JButton save;
	
	/**
	 * When true, spinner updates are ignored. Used to stop spinners re-updating each other.
	 */
	private boolean codeGeneratedChange = false;

	public ExportDialog(JFrame parent, Session session) {
		super(parent);
		this.session = session;
		setTitle("Export images");
		try {
			setIconImages(Application.getAppIcons());
		} catch (IOException e) {
			// Doesn't matter if not
			e.printStackTrace();
		}
		addWindowListener(this);
		
		File file= new File(session.getUri().replace("file://", "").replace("file:", ""));
		String fileName = file.getName();
		imageName = fileName.substring(0, fileName.lastIndexOf('.'));
		
		loadComponents();
	}
	
	private void loadComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		// Destination selector
		{
			JPanel destPanel = new JPanel();
			destPanel.setLayout(new BoxLayout(destPanel, BoxLayout.LINE_AXIS));
			
			final JTextField dest = new JTextField(30);
			dest.setEnabled(false);
			dest.setText(session.getDestination());
			
			JButton setDest = new JButton("Change");
			setDest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if(chooser.showOpenDialog(ExportDialog.this) == JFileChooser.APPROVE_OPTION) {
						session.setDestination(chooser.getSelectedFile().getAbsolutePath());
						dest.setText(chooser.getSelectedFile().getAbsolutePath());
					}
					validateFields();
				}
			});
			
			destPanel.add(new JLabel("Destination (<project>/res): "));
			destPanel.add(setDest);
			destPanel.add(dest);
			
			panel.add(destPanel);
		}
		panel.add(Box.createVerticalStrut(5));
		
		// Size (dip)
		{
			JPanel sizePanel = new JPanel();
			sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.LINE_AXIS));
			
			Dimension origSize = session.getLoader().getSize();
			sizexText = new JSpinner(new SpinnerNumberModel(origSize.width, 1, Integer.MAX_VALUE, 1));
			sizexText.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					sizex = (Integer)sizexText.getValue();
					
					if(keepAspect) {
						Dimension aspectRatio = session.getLoader().getSize();
						sizey = aspectRatio.height * sizex / aspectRatio.width;
						if(!codeGeneratedChange) {
							codeGeneratedChange = true;
							sizeyText.setValue(sizey);
							codeGeneratedChange = false;
						}
					}
				}
			});
			sizex = session.getLoader().getSize().width;
			sizexText.setValue(sizex);
			
			sizeyText = new JSpinner(new SpinnerNumberModel(origSize.height, 1, Integer.MAX_VALUE, 1));
			sizeyText.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					sizey = (Integer)sizeyText.getValue();
					
					if(keepAspect) {
						Dimension aspectRatio = session.getLoader().getSize();
						sizex = aspectRatio.width * sizey / aspectRatio.height;
						if(!codeGeneratedChange) {
							codeGeneratedChange = true;
							sizexText.setValue(sizex);
							codeGeneratedChange = false;
						}
					}
				}
			});
			sizey = session.getLoader().getSize().height;
			sizeyText.setValue(sizey);
			
			
			final JToggleButton aspect = new JToggleButton("Lock", keepAspect);
			aspect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					keepAspect = aspect.isSelected();
				}
			});
			
			sizePanel.add(new JLabel("Image size (dip):"));
			sizePanel.add(sizexText);
			sizePanel.add(new JLabel("x"));
			sizePanel.add(sizeyText);
			sizePanel.add(aspect);
			sizePanel.add(Box.createHorizontalGlue());
			
			panel.add(sizePanel);
		}
		panel.add(Box.createVerticalStrut(5));
		
		// Sizes to generate
		{
			JPanel ldpiPanel = new JPanel();
			ldpiPanel.setLayout(new BoxLayout(ldpiPanel, BoxLayout.LINE_AXIS));
			JPanel mdpiPanel = new JPanel();
			mdpiPanel.setLayout(new BoxLayout(mdpiPanel, BoxLayout.LINE_AXIS));
			JPanel hdpiPanel = new JPanel();
			hdpiPanel.setLayout(new BoxLayout(hdpiPanel, BoxLayout.LINE_AXIS));
			JPanel xdpiPanel = new JPanel();
			xdpiPanel.setLayout(new BoxLayout(xdpiPanel, BoxLayout.LINE_AXIS));
			
			final JCheckBox ldpiBox = new JCheckBox("Generate low DPI image", true);
			final JCheckBox mdpiBox = new JCheckBox("Generate medium DPI image", true);
			final JCheckBox hdpiBox = new JCheckBox("Generate high DPI image", true);
			final JCheckBox xdpiBox = new JCheckBox("Generate extra-high DPI image", true);
			
			ldpiStatus = new JLabel("");
			mdpiStatus = new JLabel("");
			hdpiStatus = new JLabel("");
			xdpiStatus = new JLabel("");
			
			ActionListener dpiChange = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ldpi = ldpiBox.isSelected();
					mdpi = mdpiBox.isSelected();
					hdpi = hdpiBox.isSelected();
					xdpi = xdpiBox.isSelected();
					
					validateFields();
				}
			};
			
			ldpiBox.addActionListener(dpiChange);
			mdpiBox.addActionListener(dpiChange);
			hdpiBox.addActionListener(dpiChange);
			xdpiBox.addActionListener(dpiChange);
			
			ldpiPanel.add(ldpiBox);
			ldpiPanel.add(Box.createHorizontalGlue());
			ldpiPanel.add(ldpiStatus);
			mdpiPanel.add(mdpiBox);
			mdpiPanel.add(Box.createHorizontalGlue());
			mdpiPanel.add(mdpiStatus);
			hdpiPanel.add(hdpiBox);
			hdpiPanel.add(Box.createHorizontalGlue());
			hdpiPanel.add(hdpiStatus);
			xdpiPanel.add(xdpiBox);
			xdpiPanel.add(Box.createHorizontalGlue());
			xdpiPanel.add(xdpiStatus);
			
			panel.add(ldpiPanel);
			panel.add(Box.createVerticalStrut(5));
			panel.add(mdpiPanel);
			panel.add(Box.createVerticalStrut(5));
			panel.add(hdpiPanel);
			panel.add(Box.createVerticalStrut(5));
			panel.add(xdpiPanel);
		}
		panel.add(Box.createVerticalStrut(5));
		
		// Name
		{
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.LINE_AXIS));
			
			final JTextField name = new JTextField(imageName);
			name.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					imageName = name.getText();
					validateFields();
				}
			});
			
			namePanel.add(new JLabel("File name:"));
			namePanel.add(name);
			
			panel.add(namePanel);
		}
		panel.add(Box.createVerticalStrut(5));
		
		// Save buttons & type
		{
			final JComboBox fileType = new JComboBox(new DefaultComboBoxModel(new String[] { "PNG", "JPG", "GIF" }));
			fileType.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switch(fileType.getSelectedIndex()) {
					case 0:
						ExportDialog.this.fileType = Exporter.IMG_PNG;
						break;
					case 1:
						ExportDialog.this.fileType = Exporter.IMG_JPG;
						break;
					case 2:
						ExportDialog.this.fileType = Exporter.IMG_GIF;
						break;
					}
					validateFields();
				}
			});
			ExportDialog.this.fileType = Exporter.IMG_PNG;
			
			JPanel savePanel = new JPanel();
			savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.LINE_AXIS));
			
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ExportDialog.this.setVisible(false);
					ExportDialog.this.dispose();
				}
			});
			save = new JButton("Save");
			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Exporter exporter = new Exporter(session, imageName, ExportDialog.this, ExportDialog.this.fileType, ldpi, mdpi, hdpi, xdpi, sizex, sizey);
						
						session.thread.queueMessage(exporter);
					}
					catch (IllegalArgumentException e1) {
						JOptionPane.showMessageDialog(ExportDialog.this, "Please enter valid options", "Invalid options", JOptionPane.ERROR_MESSAGE);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
			});
			
			savePanel.add(fileType);
			savePanel.add(Box.createHorizontalGlue());
			savePanel.add(cancel);
			savePanel.add(Box.createHorizontalStrut(10));
			savePanel.add(save);
			
			panel.add(savePanel);
		}
		
		// Message that not exporting 9-patch
		if(!session.isNinePatch()) {
			panel.add(Box.createVerticalStrut(5));
		
			JPanel msgPanel = new JPanel();
			msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("Not exporting 9-patches, exporting normal images.");
			
			msgPanel.add(label);
			msgPanel.add(Box.createHorizontalGlue());
			
			panel.add(msgPanel);
		}
		
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		getContentPane().add(Box.createHorizontalStrut(5), BorderLayout.EAST);
		getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
		pack();
	}
	
	protected void validateFields() {
		if(!imageName.equals("") && !session.getDestination().equals(""))
			save.setEnabled(true);
		else
			save.setEnabled(false);
	}

	@Override public void windowActivated(WindowEvent e) { }
	@Override public void windowDeactivated(WindowEvent e) { }
	@Override public void windowDeiconified(WindowEvent e) { }
	@Override public void windowIconified(WindowEvent e) { }
	@Override public void windowOpened(WindowEvent e) { }
	@Override public void windowClosed(WindowEvent e) { }

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}
	
	// EXPORT CALLBACKS

	@Override
	public void exportStarted(int image) {
		switch(image) {
		case ExportStatus.IMAGE_LDPI:
			ldpiStatus.setText("Exporting...");
			break;
		case ExportStatus.IMAGE_MDPI:
			mdpiStatus.setText("Exporting...");
			break;
		case ExportStatus.IMAGE_HDPI:
			hdpiStatus.setText("Exporting...");
			break;
		case ExportStatus.IMAGE_XDPI:
			xdpiStatus.setText("Exporting...");
			break;
		}
	}

	@Override
	public void exportFinished(int image) {
		switch(image) {
		case ExportStatus.IMAGE_LDPI:
			ldpiStatus.setText("Done");
			break;
		case ExportStatus.IMAGE_MDPI:
			mdpiStatus.setText("Done");
			break;
		case ExportStatus.IMAGE_HDPI:
			hdpiStatus.setText("Done");
			break;
		case ExportStatus.IMAGE_XDPI:
			xdpiStatus.setText("Done");
			break;
		}
	}

	@Override
	public void finished() {
		setVisible(false);
		dispose();
	}

	@Override
	public void error(String error) {
		JOptionPane.showMessageDialog(this, error, "An error occured", JOptionPane.ERROR_MESSAGE);
	}
}
