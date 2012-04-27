package uk.digitalsquid.patchworker;

import java.io.Serializable;

import uk.digitalsquid.patchworker.util.misc.MinMax;

/**
 * Represents a saved state for a certain image
 * @author william
 *
 */
public class SavedState implements Serializable {

	private static final long serialVersionUID = -5165020540671531771L;
	
	/**
	 * The nine-patch content X area
	 */
	private final MinMax contentX;
	/**
	 * The nine-patch content Y area
	 */
	private final MinMax contentY;
	/**
	 * The nine-patch stretch X area
	 */
	private final MinMax[] stretchX;
	/**
	 * The nine-patch stretch Y area
	 */
	private final MinMax[] stretchY;
	
	private final String destination;
	
	private final boolean isNinePatch;
	
	public SavedState(Session sessionToSave) {
		contentX = new MinMax(sessionToSave.getContentX());
		contentY = new MinMax(sessionToSave.getContentY());
		
		stretchX = MinMax.cloneArray(sessionToSave.getStretchX());
		stretchY = MinMax.cloneArray(sessionToSave.getStretchY());
		System.out.println("Saved MinMax: " + stretchX[0].getMin());
		destination = sessionToSave.getDestination();
		isNinePatch = sessionToSave.isNinePatch();
	}

	public MinMax getContentX() {
		System.out.println("Restored MinMax: " + stretchX[0].getMin());
		return contentX;
	}

	public MinMax getContentY() {
		return contentY;
	}

	public MinMax[] getStretchX() {
		return stretchX;
	}

	public MinMax[] getStretchY() {
		return stretchY;
	}

	public String getDestination() {
		return destination;
	}

	public boolean isNinePatch() {
		return isNinePatch;
	}
}
