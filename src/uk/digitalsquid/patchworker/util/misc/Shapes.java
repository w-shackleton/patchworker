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

import java.awt.Polygon;

/**
 * Static shapes used in various places
 * @author william
 *
 */
public final class Shapes {
	private Shapes() {}
	
	public static final Polygon topArrow1 = new Polygon(
			new int[] {0, -10, 0},
			new int[] {0, -10, -10},
			3);
	public static final Polygon topArrow2 = new Polygon(
			new int[] {0, 0, 10},
			new int[] {0, -10, -10},
			3);
	public static final Polygon leftArrow1 = new Polygon(
			new int[] {0, -10, -10},
			new int[] {0, 0, -10},
			3);
	public static final Polygon leftArrow2 = new Polygon(
			new int[] {0, -10, -10},
			new int[] {0, 10, 0},
			3);
	
	public static final Polygon rightArrow1 = new Polygon(
			new int[] {0, 10, 10},
			new int[] {0, -10, 0},
			3);
	public static final Polygon rightArrow2 = new Polygon(
			new int[] {0, 10, 10},
			new int[] {0, 10, 0},
			3);
	public static final Polygon bottomArrow1 = new Polygon(
			new int[] {0, -10, 0},
			new int[] {0, 10, 10},
			3);
	public static final Polygon bottomArrow2 = new Polygon(
			new int[] {0, 10, 0},
			new int[] {0, 10, 10},
			3);
}
