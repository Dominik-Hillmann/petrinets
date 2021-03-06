package model;

/** 
 * Eine Kante, die von einer Stelle zu einer Transition verläuft. 
 */
public class PlaceToTransitionArc extends Arc {
	/**
	 * Eine neue Kante, die von einer Stelle zu einer Transition verläuft. 
	 * @param id Die ID dieser Kante.
	 * @param from Urpsrungsstelle.
	 * @param to Zieltransition.
	 */
	public PlaceToTransitionArc(String id, Place from, Transition to) {
		super(id, from, to);
	}
	
	/**
	 * Liefert die Ursprungsstelle der Kante.
	 * @return Die Ursprungsstelle.
	 */
	public Place getFrom() {
		return (Place) from;
	}
	
	/**
	 * Liefert die Zieltransition.
	 * @return Die Zieltransition.
	 */
	public Transition getTo() {
		return (Transition) to;
	}
}
