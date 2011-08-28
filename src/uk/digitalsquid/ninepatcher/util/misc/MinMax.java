package uk.digitalsquid.ninepatcher.util.misc;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a minimum and maximum value of something
 * @author william
 *
 */
public final class MinMax {
	private float min;
	private float max;
	
	private final float minBound, maxBound;
	
	private boolean locked = false;
	
	public MinMax() {
		minBound = 0;
		maxBound = 1;
	}
	public MinMax(float min, float max) {
		this.min = min;
		this.max = max;
		minBound = 0;
		maxBound = 1;
	}
	public MinMax(float min, float max, float minBound, float maxBound) {
		this.min = min;
		this.max = max;
		this.minBound = minBound;
		this.maxBound = maxBound;
	}
	
	public void reset() {
		min = 0;
		max = 0;
	}
	
	/**
	 * Swaps {@link #min} and {@link #max} around if {@link #min} is greater than {@link #max}
	 */
	private void normalise() {
		if(min > max) {
			min = max = (min + max) / 2;
		}
		if(min < minBound) min = minBound;
		if(max > maxBound) max = maxBound;
	}
	
	/**
	 * Gets the distance between the two points.
	 * @return
	 */
	public float gap() {
		return max - min;
	}
	
	/**
	 * When a MinMax is locked, the values are always the same.
	 * @param locked
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
		min = max = (min + max) / 2;
		broadcast.lockChanged();
	}
	public boolean isLocked() {
		return locked;
	}
	
	public float getMin() {
		return min;
	}
	public float getMax() {
		return max;
	}
	public void setMin(float min) {
		this.min = min;
		if(isLocked())
			max = min;
		else
			normalise();
	}
	public void setMax(float max) {
		this.max = max;
		if(isLocked())
			min = max;
		else
			normalise();
	}
	
	/**
	 * Sets both variables. Disables lock if different
	 * @param min
	 * @param max
	 */
	public void setMinMax(float min, float max) {
		this.min = min;
		this.max = max;
		normalise();
	}
	
	public void notifyChanged() {
		broadcast.minMaxChanged();
	}
	
	/**
	 * Events for when the MinMax class changes
	 * @author william
	 *
	 */
	public static interface OnMinMaxChangeListener {
		public void lockChanged();
		/**
		 * The minMax has changed. Only called when someone calls MinMax.notifyChanged.
		 */
		public void minMaxChanged();
	}
	
	private OnMinMaxChangeListener broadcast = new OnMinMaxChangeListener() {
		
		@Override
		public void minMaxChanged() {
			for(OnMinMaxChangeListener l : broadcastList) {
				l.minMaxChanged();
			}
		}
		
		@Override
		public void lockChanged() {
			for(OnMinMaxChangeListener l : broadcastList) {
				l.lockChanged();
			}
		}
	};
	
	private List<OnMinMaxChangeListener> broadcastList = new LinkedList<MinMax.OnMinMaxChangeListener>();
	
	/**
	 * Adds a property changed listener
	 * @param l
	 */
	public void addListener(OnMinMaxChangeListener l) {
		broadcastList.add(l);
	}
}
