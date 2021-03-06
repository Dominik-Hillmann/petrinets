package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * Markierung eines Petrinetzes und Knoten im Erreichbarkeitsgraphen.
 */
public class Marking extends NodeModel {
	/** Bereits verwendete IDs der {@link Marking}s, um neue zu wählen. */
	private static Set<String> usedIds = new HashSet<String>();
	/** Verteilung der Zahl der Tokens auf die Stellen, per ID identifiziert. */
	private Map<String, Integer> tokens;
	/** Eingehende Kanten des Graphen. */
	private Set<MarkingToMarkingArc> origins = new HashSet<MarkingToMarkingArc>();
	/** Ausgehende Kanten des Graphen. */
	private Set<MarkingToMarkingArc> targets = new HashSet<MarkingToMarkingArc>();
	/** Die ID der Transition, durch deren Feuern diese Markierung im ER entstand. */
	private String sourceTransitionId = null;
	
	/**
	 * Konstruktor ohne Übergabe der Quell-{@link Transition}.
	 * @param tokens Eine {@link Map}, die die IDs der Stellen auf ihre Tokenzahl mappt.
	 */
	public Marking(Map<String, Integer> tokens) {
		super(getNewId());
		this.tokens = tokens;
	}
	
	/**
	 * Konstruktor, bei dem die Quell-{@link Transition} mitgegeben werden kann.
	 * @param tokens Eine {@link Map}, die die IDs der Stellen auf ihre Tokenzahl mappt.
	 * @param sourceTransitionId Die ID der {@link Transition}, durch deren feuern im {@link PetriNet}
	 * diese Markierung entstanden ist.
	 */
	public Marking(Map<String, Integer> tokens, String sourceTransitionId) {
		super(getNewId());
		this.tokens = tokens;
		this.sourceTransitionId = sourceTransitionId; 
	}
	
	/**
	 * Registriert eine neue Markierung mittels ihrer Kante mit dieser Markierung als
	 * Ursprung.
	 * @param arc Die Kante, die diese Markierung als Ursprung und die andere Markierung
	 * zum Ziel hat.
	 */
	public void registerTarget(MarkingToMarkingArc arc) {
		if (equals(arc.getFrom())) {
			targets.add(arc);
		}
	}
	
	/**
	 * Registriert eine neue Zielmarkierung mit einer Kante verlaufend von dieser 
	 * Markierung zu <code>target</code>.
	 * @param target Die Markierung, zu der eine Kante von dieser Markierung aus führt.
	 * @param sourceTransitionId Die ID der Transition, durch deren Feuern <code>target</code>
	 * entstanden ist.
	 * @param sourceTransitionName Die Name der Transition, durch deren Feuern <code>target</code>
	 * entstanden ist.
	 */
	public void registerTarget(Marking target, String sourceTransitionId, String sourceTransitionName) {
		registerTarget(new MarkingToMarkingArc(this, target, sourceTransitionId, sourceTransitionName));
	}
	
	/**
	 * Registriert eine neue Markierung mittels ihrer Kante mit dieser Markierung als
	 * Ziel.
	 * @param arc Die Kante, die diese Markierung als Ziel und die andere Markierung
	 * zum Ursprung hat.
	 */
	public void registerOrigin(MarkingToMarkingArc arc) {
		if (equals(arc.getTo())) {
			origins.add(arc);
		}
	}
	
	/**
	 * Registriert eine neue Urpsrungsmarkierung mit einer Kante verlaufend von dieser 
	 * Markierung zu <code>origin</code>.
	 * @param origin Die Markierung, von der eine Kante zu dieser Markierung aus führt.
	 * @param sourceTransitionId Die ID der Transition, durch deren Feuern <code>origin</code>
	 * entstanden ist.
	 * @param sourceTransitionName Die Name der Transition, durch deren Feuern <code>origin</code>
	 * entstanden ist.
	 */
	public void registerOrigin(Marking origin, String sourceTransitionId, String sourceTransitionName) {
		registerOrigin(new MarkingToMarkingArc(origin, this, sourceTransitionId, sourceTransitionName));
	}
	
	/**
	 * Gibt alle Markierungen aus, von denen einen Kante zu dieser Markierung
	 * existiert.
	 * @return Die Markierungen.
	 */
	public Set<Marking> getOrigins() {
		Set<Marking> originMarkings = new HashSet<Marking>();
		origins.forEach(origin -> originMarkings.add(origin.getFrom()));
		return originMarkings;
	}
	
	/**
	 * Gibt alle Markierungen aus, zu denen eine Kante von dieser Markierung aus
	 * existiert.
	 * @return Die Markierungen.
	 */
	public Set<Marking> getTargets() {
		Set<Marking> targetMarkings = new HashSet<Marking>();
		targets.forEach(target -> targetMarkings.add(target.getTo()));
		return targetMarkings;
	}
	
	/**
	 * Gibt eine neue einzigartige für eine neue Markierung aus.
	 * @return Die neue ID.
	 */
	private static String getNewId() {
		int idNum = 1;
		String proposedId = "m1";
		while (usedIds.contains(proposedId)) {
			idNum++;
			proposedId = "m" + String.valueOf(idNum);
		}
		
		usedIds.add(proposedId);
		return proposedId;
	}
	
	/**
	 * Die Verteilung der Zahl der Tokens auf die Stellen des Graphen.
	 * @return Eine Map, die die IDs auf die Zahl der Tokens mappt.
	 */
	public Map<String, Integer> getTokenDistribution() {
		return tokens;
	}
	
	@Override
	public NodeModel copy() {
		return null;
	}
	
	/**
	 * Gibt alle Kanten aus, die diese Markierung als Endpunkt haben.
	 * @return Die Kanten.
	 */
	@Override
	public Set<Arc> getArcs() {
		Set<Arc> arcs = new HashSet<Arc>();
		arcs.addAll(origins);
		arcs.addAll(targets);
		return arcs;
	}
	
	/**
	 * Gibt alle Markierungen als {@link MarkingToMarkingArc} aus, die diese
	 * Markierung als Endpunkt haben.
	 * @return Die Kanten.
	 */
	public Set<MarkingToMarkingArc> getSpecificArcs() {
		Set<MarkingToMarkingArc> arcs = new HashSet<MarkingToMarkingArc>();
		origins.forEach(arc -> arcs.add((MarkingToMarkingArc) arc));
		targets.forEach(arc -> arcs.add((MarkingToMarkingArc) arc));
		
		return arcs;
	}
	
	/**
	 * Gibt die Kante aus, die diese ID besitzt, wenn sie diese Markierung als
	 * Endpunkt hat.
	 * @param id Die ID der gesuchten Kante.
	 * @return Die Kante.
	 * @throws NodeNotFoundException Wenn keine Kante mit dieser ID gefunden
	 * werden kann.
	 */
	public Arc getArc(String id) throws NodeNotFoundException {
		Set<Arc> arcs = getArcs();
		for (Arc arc : arcs) {
			if (arc.getId().equals(id))
				return arc;
		}
		throw new NodeNotFoundException("Konnte Kante " + id + " nicht finden.");
	}
	
	@Override
	public String getName() {
		return guiName();
	}
	
	/**
	 * Gibt den Namen aus, der in der GUI erscheinen wird.
	 * @return Der GUI-Name.
	 */
	public String guiName() {
		List<String> placeIds = new ArrayList<String>(tokens.keySet());
		Collections.sort(placeIds);
		
		String repr = "(";
		for (String placeId : placeIds) {
			repr += String.valueOf(tokens.get(placeId)) + "|";
		}
		
		repr = repr.substring(0, repr.length() - 1);
		repr += ")";
		
		return repr;
	}
	
	/** 
	 * Setzt alle Kanten zurück.
	 */
	public void seperateAll() {
		origins.clear();
		targets.clear();
	}
	
	/**
	 * Die ID der Transition, durch deren Feuern diese Markierung entstand.
	 * @return Die ID.
	 */
	public String getSourceTransitionId() {
		return sourceTransitionId != null ? sourceTransitionId : "null";
		
	}
	
	/**
	 * Setzt die ID der {@link Transition} durch deren Feuern diese Markierung
	 * entstanden ist.
	 * @param id Die ID.
	 */
	public void setSourceTransitionId(String id) {
		sourceTransitionId = id;
	}
	
	@Override
	public String toString() {
		String representation = guiName() + " Marking(id=" + id + ", [";
		for (String placeId : tokens.keySet()) {
			int numTokens = tokens.get(placeId);
			representation += placeId + "=" + String.valueOf(numTokens) + ", ";	
		}
		representation = representation.substring(0, representation.length() - 2);
		representation += "])";
		
		return representation;
	}
	
	@Override
	public String toLabel() {
		return guiName();
	}
}
