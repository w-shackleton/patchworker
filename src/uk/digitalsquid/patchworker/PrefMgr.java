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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Manages preferences.
 * @author william
 *
 */
public final class PrefMgr {
	
	private static final Preferences prefs = Preferences.userNodeForPackage(PrefMgr.class);
	
	/**
	 * Saves user options at a per image level.
	 */
	private static HashMap<String, SavedState> imagePreferences;

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
	
	@SuppressWarnings("unchecked")
	private static <T> T getSerializable(String name) throws IOException, ClassNotFoundException {
		byte[] bytes = prefs.getByteArray(name, null);
		if(bytes == null) return null;
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream stream = new ObjectInputStream(byteStream);
		try {
			T obj = (T) stream.readObject();
			stream.close();
			return obj;
		} catch(ClassCastException e) {
			throw new ClassNotFoundException("Failed to cast object to specified type", e);
		} finally {
			stream.close();
		}
	}
	private static void putSerializable(String name, Serializable value) throws IOException {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutputStream stream = new ObjectOutputStream(byteOutStream);
		stream.writeObject(value);
		stream.close();
		byte[] data = byteOutStream.toByteArray();
		byteOutStream.close();
		
		prefs.putByteArray(name, data);
	}
	
	/**
	 * Gets the {@link SavedState} for the image at the given path on the computer.
	 * @param url
	 * @return
	 */
	public static SavedState getImagePreferences(String url) {
		if(imagePreferences == null) {
			try {
				imagePreferences = getSerializable("imagePrefs");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(imagePreferences == null) // Create
			imagePreferences = new HashMap<String, SavedState>();
		return imagePreferences.get(url);
	}
	
	public static synchronized void setImagePreferences(String url, SavedState state) {
		if(imagePreferences == null) {
			try {
				imagePreferences = getSerializable("imagePrefs");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(imagePreferences == null) // Create
			imagePreferences = new HashMap<String, SavedState>();
		
		imagePreferences.put(url, state);
		
		// Write back again
		try {
			putSerializable("imagePrefs", imagePreferences);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
