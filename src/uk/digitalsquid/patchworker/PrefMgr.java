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

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Manages preferences.
 * @author william
 *
 */
public final class PrefMgr {
	
	private static final Preferences prefs = Preferences.userNodeForPackage(PrefMgr.class);

	public static String getExportUri() {
		return prefs.get("exportUri", "");
	}
	
	public static void setExportUri(String uri) {
		prefs.put("exportUri", uri);
		sync();
	}
	
	public static File getLoadDirectory() {
		return new File(prefs.get("loadDirectory", ""));
	}
	
	public static void setLoadDirectory(File folder) {
		prefs.put("loadDirectory", folder.getAbsolutePath());
		sync();
	}
	
	/**
	 * Syncs the preferences. Ignores errors.
	 */
	private static void sync() {
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
