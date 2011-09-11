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
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import uk.digitalsquid.patchworker.Application;

public class FileLoadingDialog extends JDialog {
	private static final long serialVersionUID = -7142909793317157942L;

	public FileLoadingDialog(JFrame parent) {
		super(parent);
		setTitle("Loading image");
		try {
			setIconImages(Application.getAppIcons());
		} catch (IOException e) {
			// Doesn't matter if not
			e.printStackTrace();
		}
		
		getContentPane().add(new JLabel("Loading image..."), BorderLayout.CENTER);
		
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.WEST);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		getContentPane().add(Box.createVerticalStrut(10), BorderLayout.NORTH);
		getContentPane().add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
		
		pack();
	}
}
