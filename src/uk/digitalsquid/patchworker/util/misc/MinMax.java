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

package uk.digitalsquid.patchworker.util.misc;

import uk.digitalsquid.patchworker.FileEvents;

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
	private boolean mirrored = false;
	
	/**
	 * 
	 * @param notifyEvents A broadcast instance of the {@link FileEvents} to broadcast on.
	 */
	public MinMax(FileEvents notifyEvents) {
		minBound = 0;
		maxBound = 1;
		broadcast = notifyEvents;
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
		if(max < minBound) max = minBound;
		if(min > maxBound) min = maxBound;
	}
	
	/**
	 * Gets the distance between the two points.
	 * @return the range of this {@link MinMax}
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
		broadcast.minMaxLockChanged();
	}
	public boolean isLocked() {
		return locked;
	}
	
	private void mirror() {
		if(!isMirrored()) return;
		float halfWay = (minBound+maxBound)/2;
		float distOff = ((halfWay-min)+(max-halfWay))/2;
		min = halfWay - distOff;
		max = halfWay + distOff;
	}
	
	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
		mirror();
		broadcast.minMaxLockChanged();
	}
	public boolean isMirrored() {
		return mirrored;
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
		mirror();
		normalise();
		broadcast.minMaxChanged();
	}
	public void setMax(float max) {
		this.max = max;
		if(isLocked())
			min = max;
		mirror();
		normalise();
		broadcast.minMaxChanged();
	}
	
	/**
	 * Sets both variables. Disables lock if different
	 * @param min
	 * @param max
	 */
	public void setMinMax(float min, float max) {
		this.min = min;
		this.max = max;
		mirror();
		normalise();
		broadcast.minMaxChanged();
	}
	
	/**
	 * Sets both variables to the same thing
	 * @param min
	 * @param max
	 */
	public void setMinMax(float both) {
		setMinMax(both, both);
	}
	
	public void notifyChanged() {
		broadcast.minMaxChanged();
	}
	
	private final FileEvents broadcast;
	
	/**
	 * Copies settings BUT NOT VALUES from the original {@link MinMax}
	 */
	public void copyFrom(MinMax from) {
		setMirrored(from.isMirrored());
		setLocked(from.isLocked());
	}
}
