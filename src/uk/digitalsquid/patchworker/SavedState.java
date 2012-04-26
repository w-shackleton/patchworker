package uk.digitalsquid.patchworker;

import java.io.Serializable;

import uk.digitalsquid.patchworker.util.misc.MinMax;

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
	
	public SavedState(Session sessionToSave) {
		contentX = sessionToSave.getContentX();
		contentY = sessionToSave.getContentY();
		stretchX = sessionToSave.getStretchX();
		stretchY = sessionToSave.getStretchY();
		destination = sessionToSave.getDestination();
	}
}
