package model;

/** 
 * Die Position eines Knotens.
 */
public class Position {
	/** x-Koordinate. */
	private int x;
	/** y-Koordinate. */
	private int y;
	
	/**
	 * Eine neue Position durch Angabe der Koordinaten.
	 * @param x Die x-Koordinate.
	 * @param y Die y-Koordinate.
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * x-Koordinate der Position.
	 * @return Die Koordinate.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * y-Koordinate der Position.
	 * @return Die Koordinate.
	 */
	public int getY() {
		return y;
	}
	
	/** Übersichtliche Darstellung als (x, y)-Tupel. */
	@Override
	public String toString() {
		return "(" + String.valueOf(x) + ", " + String.valueOf(y) + ")";
	}
}
