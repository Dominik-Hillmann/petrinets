package model;

import java.util.Set;

/**
 * Eine Basisklasse, die grundlegende Attribute aller
 * Arten von Knoten, die im Programm erstellt werden, vereint.
 * Die Art und Weise, wie Knoten untereinander in Beziehung stehen
 * wird von den Kindklassen bestimmt.
 */
public abstract class NodeModel extends NetElement {
	/** Der Name, der in der GUI angezeigt wird. */
	protected String name;
	/** Position des Knotens in der GUI. */
	protected Position pos;
	
	/**
	 * Der Konstruktor ist protected, da er außerhalb des super-Calls nicht instanzierbar
	 * sein soll.
	 * @param id Die ID des Knotens.
	 * @param posX Die x-Position.
	 * @param posY Die y-Position.
	 */
	protected NodeModel(String id, int posX, int posY) {
		super(id);
		pos = new Position(posX, posY);
		this.name = null;
	}
	
	/**
	 * Konstrktur für den Parser.
	 * @param id Die ID des Knotens.
	 * @param name Der Name des Knotens.
	 * @param pos Die grafische Position des Knotens.
	 */
	protected NodeModel(String id, String name, Position pos) {
		super(id);
		this.pos = pos;
		this.name = name;
	}
	
	
	/**
	 * Der Konstruktor ist protected, da er außerhalb des super-Calls nicht 
	 * instanzierbar sein soll.
	 * @param id Die ID des Knotens.
	 */
	protected NodeModel(String id) {
		super(id);
		pos = null;
		this.name = null;
	}
	
	
	/**
	 * Setzt die Position des Knotens.
	 * @param pos Die neue Position.
	 */
	public void setPosition(Position pos) {
		this.pos = pos;
	}
	
	/**
	 * Setzt den Namen des Knotens.
	 * @param replaceName Der neue Name.
	 */
	public void setName(String replaceName) {
		name = replaceName;
	}
	
	/**
	 * Gibt den Namen des Knotens aus.
	 * @return Der Name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gibt die grafische Position des Knotens aus.
	 * @return Die Position.
	 */
	public Position getPosition() {
		return pos;
	}
	
	/** 
	 * Erstellt unabhängige Kopie dieses Knotens.
	 */
	public abstract NodeModel copy();
	
	/**
	 * Gibt alle angrenzenden Kanten aus.
	 * @return Die Kanten.
	 */
	public abstract Set<Arc> getArcs();
	
	/**
	 * Gibt den Namen aus, der in der GUI erscheinen wird.
	 * @return Der Name, der in der GUI erscheinen wird.
	 */
	public abstract String toLabel();
}
