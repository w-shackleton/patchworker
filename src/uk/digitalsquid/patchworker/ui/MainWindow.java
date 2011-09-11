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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import uk.digitalsquid.patchworker.Application;
import uk.digitalsquid.patchworker.FileEvents;
import uk.digitalsquid.patchworker.PrefMgr;
import uk.digitalsquid.patchworker.Session;

/**
 * Main UI element in the program
 * @author william
 *
 */
public class MainWindow extends JFrame implements WindowListener, FileEvents {
	private static final long serialVersionUID = -5010616265178392396L;
	
	private Session session = new Session();
	
	NinePatchPanel imagePanel;
	
	private JMenuItem exportMenuItem;
	
	public MainWindow() {
		session.addListener(this);
		setTitle("Patchworker");
		try {
			setIconImages(Application.getAppIcons());
		} catch (IOException e) {
			// Doesn't matter if not
			e.printStackTrace();
		}
		setSize(700, 500);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		loadComponents();
	}
	
	/**
	 * Loads the UI
	 */
	private void loadComponents() {
		// Menus
		{
			JMenuBar mb = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			JMenuItem loadImage = fileMenu.add("Load image");
			loadImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
			loadImage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(PrefMgr.getLoadDirectory());
					FileFilter fileFilter = new FileFilter() {
						
						@Override
						public String getDescription() {
							return "Images (PNG, JPG, GIF, SVG)";
						}
						
						@Override
						public boolean accept(File f) {
							if(f.isDirectory()) return true;
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
						PrefMgr.setLoadDirectory(fileChooser.getCurrentDirectory());
					}
				}
			});
			
			exportMenuItem = fileMenu.add("Export");
			exportMenuItem.setEnabled(false);
			exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
			exportMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ExportDialog export = new ExportDialog(MainWindow.this, session);
					export.setVisible(true);
				}
			});
			
			JMenuItem exit = fileMenu.add("Exit");
			exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			mb.add(fileMenu);
			
			JMenu optionsMenu = new JMenu("Options");
			final JMenuItem enableNormalImage = new JCheckBoxMenuItem("Export normal images (not 9-patch)");
			enableNormalImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
			optionsMenu.add(enableNormalImage);
			enableNormalImage.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					// update session to notify everything if graphic is a 9patch or not.
					session.setNinePatch(!enableNormalImage.isSelected());
				}
			});
			mb.add(optionsMenu);
			
			JMenu helpMenu = new JMenu("Help");
			final JMenuItem about = new JMenuItem("About");
			about.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AboutDialog d = new AboutDialog(MainWindow.this);
					d.setVisible(true);
				}
			});
			helpMenu.add(about);
			
			mb.add(helpMenu);
			
			getContentPane().add(mb, BorderLayout.NORTH);
			
		}
		
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.LINE_AXIS));
		
		// Left hand side contents
		{
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			
			imagePanel = new NinePatchPanel(session);
			mainPanel.add(imagePanel, BorderLayout.CENTER);
			
			// Top panel - stretch area settings
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
				final JCheckBox limit = new JCheckBox("Limit to 1 pixel", true);
				limit.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						boolean checked = limit.isSelected();
						session.stretchX.setLocked(checked);
						session.stretchY.setLocked(checked);
					}
				});
				final JCheckBox mirrored = new JCheckBox("Mirrored");
				mirrored.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						boolean checked = mirrored.isSelected();
						session.stretchX.setMirrored(checked);
						session.stretchY.setMirrored(checked);
					}
				});
				panel.add(Box.createHorizontalGlue());
				panel.add(new JLabel("Stretch areas: "));
				panel.add(limit);
				panel.add(mirrored);
				panel.add(Box.createHorizontalGlue());
				
				mainPanel.add(panel, BorderLayout.NORTH);
			}
			// Bottom panel - content area settings
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
				final JCheckBox mirrorX = new JCheckBox("Mirror X");
				mirrorX.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						boolean checked = mirrorX.isSelected();
						session.contentX.setMirrored(checked);
					}
				});
				final JCheckBox mirrorY = new JCheckBox("Mirror Y");
				mirrorY.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						boolean checked = mirrorY.isSelected();
						session.contentY.setMirrored(checked);
					}
				});
				panel.add(Box.createHorizontalGlue());
				panel.add(new JLabel("Content areas: "));
				panel.add(mirrorX);
				panel.add(mirrorY);
				panel.add(Box.createHorizontalGlue());
				
				mainPanel.add(panel, BorderLayout.SOUTH);
			}
			
			outerPanel.add(mainPanel);
			
			getContentPane().add(outerPanel, BorderLayout.CENTER);
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
	
	private FileLoadingDialog fileLoadingDialog;

	/**
	 * Opens a dialog showing that the file is opening
	 */
	@Override
	public void fileOpening() {
		fileLoadingDialog = new FileLoadingDialog(this);
		fileLoadingDialog.setVisible(true);
	}

	/**
	 *  Closes the dialog showing that a file is opening
	 */
	@Override
	public void fileOpened() {
		if(fileLoadingDialog == null) return;
		fileLoadingDialog.setVisible(false);
		fileLoadingDialog.dispose();
		fileLoadingDialog = null;
		
		exportMenuItem.setEnabled(true);
	}

	@Override
	public void openFailed(String reason) {
		if(fileLoadingDialog == null) return;
		fileLoadingDialog.setVisible(false);
		fileLoadingDialog.dispose();
		fileLoadingDialog = null;
		exportMenuItem.setEnabled(false);
		
		JOptionPane.showMessageDialog(this, "Failed to load image", "Failed to load", JOptionPane.ERROR_MESSAGE);
	}

	@Override public void drawingNinePatch(boolean isNinePatch) { }
}
