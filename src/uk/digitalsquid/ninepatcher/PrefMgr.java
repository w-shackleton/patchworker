package uk.digitalsquid.ninepatcher;

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
