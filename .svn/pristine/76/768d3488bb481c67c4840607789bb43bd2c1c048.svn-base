package model;

/**
 * Eine Kante, die von einer Transition zu einer Stelle verläuft.
 */
public class TransitionToPlaceArc extends Arc {
	/**
	 * Konstruiert eine neue Kante.
	 * @param id ID der Kante.
	 * @param from Ursprungstransition.
	 * @param to Zielstelle.
	 */
	public TransitionToPlaceArc(String id, Transition from, Place to) {
		super(id, from, to);
	}
	
	@Override
	public Transition getFrom() {
		return (Transition) from;
	}
	
	@Override
	public Place getTo() {
		return (Place) to;
	}
}
