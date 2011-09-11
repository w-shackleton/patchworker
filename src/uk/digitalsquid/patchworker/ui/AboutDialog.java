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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.digitalsquid.patchworker.Application;

/**
 * About dialog
 * @author william
 *
 */
public class AboutDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = 5211295405860753683L;

	public AboutDialog(JFrame owner) {
		super(owner);
		addWindowListener(this);
		setTitle("About");
		try {
			setIconImages(Application.getAppIcons());
		} catch (IOException e) {
			// Doesn't matter if not
			e.printStackTrace();
		}
		loadComponents();
		validate();
		pack();
	}
	
	private void loadComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		// Title
		{
			JPanel titlePanel = new JPanel();
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("Patchworker");
			label.setFont(label.getFont().deriveFont(24f));
			titlePanel.add(label);
			
			panel.add(titlePanel);
		}
		
		// Desc1
		{
			JPanel descPanel = new JPanel();
			descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("Alternative 9-patch creator");
			descPanel.add(label);
			
			panel.add(descPanel);
		}
		// Desc2
		{
			JPanel descPanel = new JPanel();
			descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("for Android development");
			descPanel.add(label);
			
			panel.add(descPanel);
		}
		// Desc3
		{
			JPanel descPanel = new JPanel();
			descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("digitalsquid.co.uk");
			descPanel.add(label);
			
			panel.add(descPanel);
		}
		
		/**
		// Links
		{
			JPanel linksPanel = new JPanel();
			linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.LINE_AXIS));
			
			JButton website = new JButton("Website");
			website.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			linksPanel.add(website);
			
			panel.add(linksPanel);
		}
		*/
		
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.WEST);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
	}

	@Override public void windowActivated(WindowEvent arg0) { }
	@Override public void windowClosed(WindowEvent e) { }
	@Override public void windowClosing(WindowEvent e) { }
	@Override public void windowDeactivated(WindowEvent e) { }
	@Override public void windowDeiconified(WindowEvent e) { }
	@Override public void windowIconified(WindowEvent e) { }
	@Override public void windowOpened(WindowEvent e) { }
}
