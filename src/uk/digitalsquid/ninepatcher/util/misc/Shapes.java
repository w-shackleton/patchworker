package uk.digitalsquid.ninepatcher.util.misc;

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
