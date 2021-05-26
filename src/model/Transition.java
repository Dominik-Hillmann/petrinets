package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Eine Transition in einem Petrinetz.
 */
public class Transition extends NodeModel {
	/** Kanten, die von dieser Transition weg führen. */
	private Set<TransitionToPlaceArc> postsetArcs = new HashSet<TransitionToPlaceArc>();
	/** Kanten, die zu dieser Transition hin führen. */
	private Set<PlaceToTransitionArc> presetArcs = new HashSet<PlaceToTransitionArc>();
	
	/**
	 * Erstellt eine neue Transition.
	 * @param id ID der Transition.
	 * @param name Der angezeigte Name der Transition.
	 * @param pos Die grafische Position der Transition.
	 */
	public Transition(String id, String name, Position pos) {
		super(id, name, pos);
	}
	
	/**
	 * Erstellt eine Transition, sodass alle anderen Informationen
	 * nachträglich gesetzt werden müssen.
	 * @param id Die ID der Transition.
	 */
	public Transition(String id) {
		super(id);
	}
		
	/**
	 * Registriert eine neue Kante an dieser Transition.
	 * Wenn die Starttransition nicht diese Transition ist,
	 * wird sie nicht registriert.
	 * @param arc Zu registriernde Kante.
	 */
	public void register(TransitionToPlaceArc arc) {
		if (equals(arc.getFrom())) {
			postsetArcs.add(arc);
		}
	}
	
	/**
	 * Registriert eine neue Kante an dieser Transition.
	 * Wenn die Starttransition nicht diese Transition ist,
	 * wird sie nicht registriert.
	 * @param arc Zu registriernde Kante.
	 */	
	public void register(PlaceToTransitionArc arc) {
		if (equals(arc.getTo())) {
			presetArcs.add(arc);
		}
	}
	
	/**
	 * Gibt alle Kanten aus, die von dieser Transition weg führen.
	 * @return Die Kanten.
	 */
	public Set<Place> getPostset() {
		Set<Place> postset = new HashSet<Place>();
		postsetArcs.forEach(arc -> postset.add(arc.getTo()));
		
		return postset;
	}
	
	/**
	 * Gibt alle Kanten aus, die zu dieser Transition hin führen.
	 * @return Die Kanten.
	 */
	public Set<Place> getPreset() {
		Set<Place> preset = new HashSet<Place>();
		for (PlaceToTransitionArc arc : presetArcs) {
			preset.add(arc.getFrom());
		}
		return preset;
	}
	
	/**
	 * Feuert diese Transition.
	 * @throws CannotFireException Wenn eine einlaufende Kante existiert,
	 * deren Ursprungsstelle keine Marke besitzt.
	 */
	public void fire() throws CannotFireException {
		Set<Place> preset = getPreset();
		Set<Place> postset = getPostset();
		
		for (Place origin : preset) {
			if (!origin.canSubtractToken()) {
				throw new CannotFireException(this, origin);
			}			
		}
		
		for (Place origin : preset) 
			origin.subtractToken();
				
		for (Place target : postset)
			target.addToken();
	}
	
	@Override
	public Set<Arc> getArcs() {
		Set<Arc> arcs = new HashSet<Arc>();
		arcs.addAll(presetArcs);
		arcs.addAll(postsetArcs);
		return arcs;		
	}
	
	@Override
	public NodeModel copy() {
		Transition copiedTrans = new Transition(id, name, pos);
		return copiedTrans;
	}
	
	/** Übersichtliche Darstellung als String. */
	@Override
	public String toString() {
		return "Transition(id=" + id + 
				", pos=" +  pos.toString() + 
				", name=" + name +
				")";
	}
	
	@Override
	public String toLabel() {
		return "[" + getId() + "] " + getName();
	}
}
