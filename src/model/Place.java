package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Eine Stelle im Petrinetz.
 */
public class Place extends NodeModel {
	/** Zahl der Marken auf dieser Stelle. */
	private int tokens = 0;
	/** Ausgehende Kanten. */
	private Set<PlaceToTransitionArc> out = new HashSet<PlaceToTransitionArc>();
	/** Zulaufende Kanten. */
	private Set<TransitionToPlaceArc> in = new HashSet<TransitionToPlaceArc>();
	
	/**
	 * Konstruktor, der zunächst nur die ID des Knotens setzt.	
	 * @param id Die ID.
	 */
	public Place(String id) {
		super(id);
	}
	
	/**
	 * Konstruktor, der alle Informationen im Petrinetz setzt.
	 * @param id ID der Stelle.
	 * @param name Der angzeigte Name der Stelle.
	 * @param pos Die grafische Position der Stelle.
	 */
	public Place(String id, String name, Position pos) {
		super(id, name, pos);
	}
	
	/**
	 * Setze die Zahl der Tokens.
	 * @param num Zahl der Tokens, die diese Stelle besitzen soll.
	 */
	public void initNumTokens(int num) {
		tokens = num;
	}
		
	/**
	 * Registriert eine neue Kante an dieser Stelle.
	 * Wenn der Startknoten der registrierten Kante nicht diese
	 * Stelle ist, wird sie nicht registriert.
	 * @param arc Die Kante.
	 */
	public void register(PlaceToTransitionArc arc) {
		if (equals(arc.getFrom())) {
			out.add(arc);
		}
	}
	
	/**
	 * Registriert eine neue Kante an dieser Stelle.
	 * Wenn der Endknoten der registrierten Kante nicht diese
	 * Stelle ist, wird sie nicht registriert.
	 * @param arc Die Kante.
	 */
	public void register(TransitionToPlaceArc arc) {
		if (equals(arc.getTo())) {
			in.add(arc);
		}
	}
	
	/**
	 * Liefert alle Knoten, zu denen eine Kante von diesem Knoten
	 * aus verläuft.
	 * @return Die Zielknoten.
	 */
	public Set<Transition> getTargets() {
		Set<Transition> targets = new HashSet<Transition>();
		for (PlaceToTransitionArc arc : out) {
			targets.add(arc.getTo());
		}
		return targets;
	}
	
	/**
	 * Liefert alle Knoten, von denen eine Kante zu diesem Knoten
	 * verläuft.
	 * @return Die Knoten.
	 */
	public Set<Transition> getOrigins() {
		Set<Transition> origins = new HashSet<Transition>();
		for (TransitionToPlaceArc arc : in) {
			origins.add(arc.getFrom());
		}
		return origins;
	}
	
	/** 
	 * Übersichtlich als {@link String} dargestellte Informationen über die Stelle.
	 */
	@Override
	public String toString() {
		return "Place(id=" + id + 
				", name=" + name + 
				", tokens=" + String.valueOf(tokens) + 
				")";
	}
	
	@Override
	public String toLabel() {
		return "[" + getId() + "] " + getName() + " <" + String.valueOf(getNumTokens()) + ">";
	}
	
	/**
	 * Ob die Stelle mindestens eine Marke besitzt.
	 * @return Siehe Beschreibung.
	 */
	public boolean canSubtractToken() {
		return tokens > 0;
	}
	
	/** 
	 * Zieht eine Marke von der Stelle ab.
	 */
	public void subtractToken() {
		if (tokens > 0) {
			tokens -= 1;
		}
	}
	
	/** 
	 * Fügt der Stelle eine Marke hinzu.
	 */
	public void addToken() {
		tokens += 1;
	}
	
	/** 
	 * Anzahl der Marken auf dieser Stelle.
	 * @return Markenzahl
	 */
	public int getNumTokens() {
		return tokens;
	}
	
	@Override
	public Set<Arc> getArcs() {
		Set<Arc> arcs = new HashSet<Arc>();
		arcs.addAll(out);
		arcs.addAll(in);
		
		return arcs;
	}
	
	
	@Override
	public NodeModel copy() {
		Place copiedPlace = new Place(id, name, pos);
		
		return copiedPlace;
	}
}
