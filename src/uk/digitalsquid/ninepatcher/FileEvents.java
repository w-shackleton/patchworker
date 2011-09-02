package uk.digitalsquid.ninepatcher;

/**
 * File events. These callbacks are always done on the UI thread.
 * @author william
 *
 */
public interface FileEvents {
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
