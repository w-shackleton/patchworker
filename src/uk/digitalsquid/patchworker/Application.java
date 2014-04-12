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

package uk.digitalsquid.patchworker;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import uk.digitalsquid.patchworker.ui.MainWindow;

public final class Application {
	/**
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		// Set system UI if possible
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) { // Just revert to metal UI
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
		
		MainWindow win = new MainWindow();
		
		win.setVisible(true);
	}
	
	private static List<Image> appIcons;
	
	/**
	 * Gets the application icon
	 * @return A list of loaded images
	 * @throws IOException if an image fails to load
	 */
	public static List<Image> getAppIcons() throws IOException {
		if(appIcons != null) return appIcons;
		
		appIcons = new ArrayList<Image>(3);
		appIcons.add(ImageIO.read(Application.class.getResource("images/icon8.png")));
		appIcons.add(ImageIO.read(Application.class.getResource("images/icon32.png")));
		appIcons.add(ImageIO.read(Application.class.getResource("images/icon64.png")));
		
		return appIcons;
	}
}
