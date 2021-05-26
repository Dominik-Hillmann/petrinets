package model;

import java.util.Set;

/**
 * Abstrakte Basisklasse für Modelle von Graphen (Petrinetz, ER).
 */
abstract class Net {
	/**
	 * Gibt alle Kanten aus.
	 * @return Die Kanten.
	 */
	abstract Set<Arc> getArcs();
	
	/**
	 * Gibt alle Knoten des Netzes aus.
	 * @return Die Knoten.
	 */
	abstract Set<NodeModel> getNodes();
}
