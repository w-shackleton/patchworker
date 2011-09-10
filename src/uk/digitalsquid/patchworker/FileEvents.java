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

/**
 * File events. These callbacks are always done on the UI thread.
 * @author william
 *
 */
public interface FileEvents {
	
	/**
	 * A file has started to load
	 */
	public void fileOpening();
	
	/**
	 * The file has opened successfully
	 */
	public void fileOpened();
	
	/**
	 * The file failed to open. This also signifies that the file was 'unloaded' - its state shoud be cleared.
	 * @param reason
	 */
	public void openFailed(String reason);
	
	/**
	 * This function is called with <code>true</code> if we are currently drawing 9patches, or <code>false</code> if just normal images.
	 * @param isNinePatch
	 */
	public void drawingNinePatch(boolean isNinePatch);
}
