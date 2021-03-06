package model;

/**
 * Repräsentiert eine gerichtete Kante, unabhänge vom genauen Typ des Anfangs-
 * und Endknoten.
 */
public class Arc extends NetElement {
	/** Der Startknoten der Kante. */
	protected NodeModel from;
	/** Der Endknoten der Kante. */
	protected NodeModel to;
	
	/**
	 * Einziger Konstruktor, der nur von Kindklassen aufgerufen werden soll 
	 * (nicht instanzierbar).
	 * @param id Die ID der Kante.
	 * @param from Startknoten der Kante.
	 * @param to Endknoten der Kante.
	 */
	protected Arc(String id, NodeModel from, NodeModel to) {
		super(id);
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Gibt den Startknoten der Kante aus.
	 * @return Der Startknoten.
	 */
	public NodeModel getFrom() {
		return from;
	}
	
	/**
	 * Der Zielknoten der Kante.
	 * @return Der Zielknoten.
	 */
	public NodeModel getTo() {
		return to;
	}
	
	/**
	 * Übersichtliche Darstellung als String.
	 */
	@Override
	public String toString() {
		return from.toString() + " --> " + to.toString();
	}
	
	/**
	 * Im Graphen sichtbares Label.
	 * @return Das Label.
	 */
	public String toLabel() {
		return "[" + getId() + "]";
	}
}
