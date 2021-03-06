package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 
 * Beschreibt ein Petrinetz.
 */
public class PetriNet extends Net {
	/** Die Stellen des Petrinetzes. */
	private Set<Place> places;
	/** Die Transitionen des Petrinetzes. */
	private Set<Transition> transitions;
	/** Die Zuordnung der Namen der Knoten des Petrinetzes. Für Kopien. */
	private Map<String, String> namesTemplate;
	/** Die Zuordnung der Positionen der Knoten des Petrinetzes. Für Kopien. */
	private Map<String, Position> positionsTemplate;
	/** Eine Karte der Kanten des Netzes, wird ebenfalls für Kopien benötigt. */
	private Map<String, Pair<String, String>> arcsTemplate = null;
	/** Die Zuordnung der initialen Zahl der Tokens jeder Stelle. */
	private Map<String, Integer> tokensTemplate;
	
	/**
	 * Der Konstruktor des Petinetzes.
	 * @param places Die Stellen des Petrinetzes.
	 * @param transitions Die Transitionen des Petrinetzes.
	 * @param names Die Namen der Knoten des Petrinetzes.
	 * @param positions Die Positionen aller Knoten im Petrinetz.
	 * @param tokens Die Verteilung der Tokens auf die Stellen.
	 * @param arcs Die Kanten des Petrinetzes.
	 * @throws NodeNotFoundException Wenn eine Stelle nicht über ihre ID gefunden
	 * werden kann.
	 * @throws IllegalArcException Wenn eine Kante zwischen zwei Stellen oder zwei
	 * Transitionen existiert.
	 * @apiNote Ich entschuldige mich für die lange Parameterliste, aber ich wollte/musste
	 * bereits an diesem Punkt im Programm sicherstellen, dass jede Stelle und jede
	 * Transition alle Daten besitzt, die es braucht.
	 */
	public PetriNet(
		Set<Place> places, 
		Set<Transition> transitions,
		Map<String, String> names,
		Map<String, Position> positions,
		Map<String, Integer> tokens,
		Map<String, Pair<String, String>> arcs
	) throws NodeNotFoundException, IllegalArcException {
		this.places = places;
		this.transitions = transitions;
		namesTemplate = names;
		initNames(names);
		positionsTemplate = positions;
		initPositions(positions);
		positionsTemplate = positions;
		tokensTemplate = tokens;
		initTokens(tokens);
		arcsTemplate = arcs;
		initArcs(arcs);
	}
	
	/**
	 * Findet eine Stelle anhand derer ID.
	 * @param id Die ID der gesuchten Stelle.
	 * @return Die Stelle mit dieser ID.
	 * @throws NodeNotFoundException Wenn keine Stelle mit dieser ID im Petrinetz existiert.
	 */
	public Place getPlaceById(String id) throws NodeNotFoundException {
		for (Place place : places) {
			if (place.hasId(id)) {
				return place;
			}
		}
		
		throw new NodeNotFoundException("Stelle mit der ID " + id + " konnte nicht gefunden werden.");
	}
	
	/**
	 * Findet eine Transition anhand derer ID.
	 * @param id Die ID der gesuchten Transition.
	 * @return Die Transition, die diese ID besitzt.
	 * @throws NodeNotFoundException Wenn keine Transition mit dieser ID im Petrinetz existiert.
	 */
	public Transition getTransitionById(String id) throws NodeNotFoundException {
		for (Transition transition : transitions) {
			if (transition.hasId(id)) {
				return transition;
			}
		}
		
		throw new NodeNotFoundException("Transition mit der ID " + id + " konnte nicht gefunden werden.");
	}
	
	/**
	 * Findet einen Knoten im Petrinetz anhand dessen ID.
	 * @param id Die ID des Knoten.
	 * @return Den gesuchten Knoten.
	 * @throws NodeNotFoundException Wenn kein Knoten mit dieser ID im Petrinetz existiert.
	 */
	public NodeModel getNodeById(String id) throws NodeNotFoundException {
		try {
			return (NodeModel) getPlaceById(id);
		} catch (NodeNotFoundException e) {
			return (NodeModel) getTransitionById(id);
		}
	}
	
	/**
	 * Ordnet jedem Knoten einen Namen zu.
	 * @param names Die IDs, denen Namen zugeordnet sind.
	 * @throws NodeNotFoundException Wenn eine ID in names nicht im Petrinetz existiert.
	 */
	private void initNames(Map<String, String> names) throws NodeNotFoundException {
		Set<String> ids = names.keySet();
		for (String id : ids) {
			NodeModel nodeToBeNamed = getNodeById(id);
			String name = names.get(id);
			nodeToBeNamed.setName(name);
		}
	}
	
	/**
	 * Ordnet jedem Knoten eine grafische Position zu.
	 * @param positions Die zuzuordnenden Positionen.
	 * @throws NodeNotFoundException Wenn eine eine ID in positions nicht im 
	 * Petrinetz existiert.
	 */
	private void initPositions(Map<String, Position> positions) throws NodeNotFoundException {
		Set<String> ids = positions.keySet();
		for (String id : ids) {
			NodeModel nodeToBePositioned = getNodeById(id);
			Position position = positions.get(id);
			nodeToBePositioned.setPosition(position);
		}
	}
	
	/**
	 * Ordnet jeder Stelle ihre initialen Marken zu,
	 * @param tokens Eine Map der Stellen-IDs auf die Zahl der Marken. 
	 * @throws NodeNotFoundException Wenn eine ID in tokens nicht im 
	 * Petrinetz existiert.
	 */
	private void initTokens(Map<String, Integer> tokens) throws NodeNotFoundException {
		Set<String> ids = tokens.keySet();
		for (String id : ids) {
			Place place = getPlaceById(id);
			int numNewTokens = tokens.get(id);
			place.initNumTokens(numNewTokens);
		}
	}
	
	/**
	 * Registriert alle Verbindungen im Petrinetz bei den Knoten.
	 * @param arcs Die Verbindungen, jede ist über ihre ID identifiziert und 
	 * enthält die IDs von Start- und Endknoten.
	 * @throws IllegalArcException Wenn eine Verbindung zwischen Knoten 
	 * gleicher Farbe existiert.
	 */
	private void initArcs(Map<String, Pair<String, String>> arcs) throws IllegalArcException {
		// Festhaltung, um später rekonstruieren zu können.
		arcsTemplate = arcs;		
		Set<String> arcIds = arcs.keySet();
		
		for (String arcId : arcIds) {
			String fromId = arcs.get(arcId).getFirst();
			String toId = arcs.get(arcId).getSecond();
			
			// Als erstes testen, ob Kante vom Stelle zu Transition läuft.
			try {
				Place from = getPlaceById(fromId);
				Transition to = getTransitionById(toId);
				
				PlaceToTransitionArc arc = new PlaceToTransitionArc(arcId, from, to);
				from.register(arc);
				to.register(arc);
				
			} catch (NodeNotFoundException e1) {
				// Dann kann es nur sein, dass die Kante/arc von Transition zu Stelle läuft.
				try {
					Transition from = getTransitionById(fromId);
					Place to = getPlaceById(toId);				
					
					TransitionToPlaceArc arc = new TransitionToPlaceArc(arcId, from, to);
					from.register(arc);
					to.register(arc);
					
				// Und wenn das nicht geht, dann darf eine solche Kante nicht im Petrinetz existieren,
				// da es bipartit ist.
				} catch (NodeNotFoundException e2) {
					String m = "Kante " + arcId + " von " + fromId + " zu " + toId + ".";
					throw new IllegalArcException("Kante gefunden, die nicht von Transition " +
							"zu Stelle oder von Stelle zu Transition verläuft. Ein Petrinetz muss " + 
							"bipartit sein. " + m);
				}
			}					
		}
	}
	
	/**
	 * Findet eine Transition per ID und feuert diese.
	 * @param transitionId Die ID, mit der die Transition gesucht wird.
	 * @throws CannotFireException Wenn keine Transition mit dieser ID existiert, oder
	 * die Transition nicht feuern kann, weil es im Preset an Marken fehlt.
	 */
	public void fire(String transitionId) throws CannotFireException {
		Transition firingTransition;
		try {
			firingTransition = getTransitionById(transitionId);
		} catch (NodeNotFoundException e) {
			throw new CannotFireException(
				transitionId + " existiert nicht im Petrinetz und kann deshalb nicht feuern."
			);
		}
		
		firingTransition.fire();
	}
	
	/**
	 * Fügt der Stelle mit dieser ID eine Marke hinzu.
	 * @param id ID der gesuchten Stelle.
	 * @throws NodeNotFoundException Wenn keine Stelle mit dieser ID existiert.
	 */
	public void addToken(String id) throws NodeNotFoundException {
		Place place = getPlaceById(id);
		place.addToken();
	}
	
	/**
	 * Zieht der Stelle mit dieser ID eine Marke ab.
	 * @param id ID der gesuchten Stelle.
	 * @throws NodeNotFoundException Wenn keine Stelle mit dieser ID existiert.
	 */
	public void subtractToken(String id) throws NodeNotFoundException {
		Place place = getPlaceById(id);
		place.subtractToken();
	}
	
	/**
	 * Setzt alle Tokens der Stellen auf den Anfangszustand zurück.
	 * @throws NodeNotFoundException Wenn eine Stelle nicht über ihre gefunden
	 * werden kann.
	 */
	public void resetTokens() throws NodeNotFoundException {
		resetTokens(tokensTemplate);
	}
	
	/**
	 * Setzt alle Tokens wie in <code>tokensDistribution</code> beschrieben.
	 * @param tokensDistribution Ein Mapping der IDs auf die neue Zahl an Marken.
	 * @throws NodeNotFoundException Wenn eine ID einer Stelle in der Map nicht existiert.
	 */
	public void resetTokens(Map<String, Integer> tokensDistribution) throws NodeNotFoundException {
		Map<String, Integer> resetDistribution = tokensDistribution;
		
		Set<String> allIds = new HashSet<String>();
		places.forEach(place -> allIds.add(place.getId()));
		Set<String> zeroIds = new HashSet<String>();
		Set<String> nonZeroIds = resetDistribution.keySet();
		
		for (String placeId : allIds) {
			if (!nonZeroIds.contains(placeId)) {
				zeroIds.add(placeId);
			}
		}
		
		for (String zeroId : zeroIds) {
			Place zeroPlace = getPlaceById(zeroId);
			zeroPlace.initNumTokens(0);
		}
		
		for (String nonZeroId : nonZeroIds) {
			Place nonZeroPlace = getPlaceById(nonZeroId);
			nonZeroPlace.initNumTokens(resetDistribution.get(nonZeroId));
		}
	}
	
	/**
	 * Übersichtliche Darstellung als String.
	 * Habe ich oft benutzt, um mir einen Überblick den Zustand
	 * des Petrinetz zu machen.
	 */
	@Override
 	public String toString() {
		String representation = "";
		for (Place place : places) {
			representation += place.toString() + "\n";
			for (Transition target : place.getTargets())
				representation += "  zu  " + target.toString() + "\n";
			for (Transition origin : place.getOrigins())
				representation += "  von " + origin.toString() + "\n";
		}
		
		for (Transition transition : transitions) {
			representation += transition.toString() + "\n";
			for (Place prePlace : transition.getPreset())
				representation += "  von " + prePlace.toString() + "\n";
			for (Place postPlace : transition.getPostset())
				representation += "  zu  " + postPlace.toString() + "\n";
		}
		return representation;
	}
	
	/**
	 * Gibt die Stellen des Petrinetzes aus.
	 * @return Die Stellen.
	 */
	public Set<Place> getPlaces() {
		return places;
	}
	
	/**
	 * Gibt die Transitionen des Petrinetzes aus.
	 * @return Die Transitionen.
	 */
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	/**
	 * Erstellt eine Kopie des Petrinetzes mit der durch marking gegebenen
	 * Verteilung an Marken.
	 * @param marking Die Markierung, Verteilung der Marken auf Stellen.
	 * @return Die Kopie des Petrinetzes, die Marken verteilt wie in marking.
	 * @throws NodeNotFoundException Wenn im Marking eine ID einer Stelle existiert,
	 * die im Petrinetz nicht vorkommt.
	 */
	public PetriNet fromMarking(Marking marking) throws NodeNotFoundException {
		// Stellen und Transitionen müssen einzeln kopiert werden, da diese
		// möglicherweise editiert werden können.
		Set<Place> copiedPlaces = new HashSet<Place>();
		places.forEach(place -> copiedPlaces.add((Place) place.copy()));
		Set<Transition> copiedTransitions = new HashSet<Transition>();
		transitions.forEach(trans -> copiedTransitions.add((Transition) trans.copy()));
		
		// Alles andere wird nicht editiert und kann deshalb übernommen werden.
		Map<String, Integer> tokenDistribution = marking.getTokenDistribution();
		PetriNet copyNet = null;
		try {
			copyNet = new PetriNet(
				copiedPlaces, 
				copiedTransitions, 
				namesTemplate, 
				positionsTemplate, 
				tokenDistribution,
				arcsTemplate
			);
		} catch (IllegalArcException e) {
			System.out.println(e.toString());
			System.exit(0);
		}
		
		return copyNet;
	}
	
	/**
	 * Gibt die Kanten des Petrinetzes aus.
	 * @return Die Kanten.
	 */
	public Set<Arc> getArcs() {
		Set<Arc> arcs = new HashSet<Arc>();
		places.forEach(place -> arcs.addAll(place.getArcs()));
		transitions.forEach(trans -> arcs.addAll(trans.getArcs()));
		return arcs;
	}
	
	/**
	 * Gibt die aktuelle Markierung des Petrinetzes aus, ohne das
	 * Petrinetz zu modifizieren.
	 * @return Die Markierung.
	 */
	public Marking getMarking() {
		Map<String, Integer> tokenDistribution = new HashMap<String, Integer>();
		places.forEach(place -> tokenDistribution.put(place.getId(), place.getNumTokens()));
		return new Marking(tokenDistribution);
	}
	
	@Override
	public Set<NodeModel> getNodes() {
		Set<NodeModel> nodes = new HashSet<NodeModel>();
		nodes.addAll(places);
		nodes.addAll(transitions);
		return nodes;
	}
}
