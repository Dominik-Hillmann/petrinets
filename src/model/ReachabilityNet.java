package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * Der Erreichbarkeitsgraph, dessen Markierungen hier verwaltet werden
 * und der entscheidet, ob er unbeschränkt ist oder nicht.
 */
public class ReachabilityNet extends Net {
	/** Die Knoten des Erreichbarkeitsgraphen. */
	private Set<Marking> markings = new HashSet<Marking>();
	/** 
	 * Wurzel des Graphen und Markierung des Petrinetzes bei 
	 * Erstellung des Erreichbarkeitsgraphen. 
	 */
	private Marking root;
	
	/** Die Wurzel des Erreichbarkeitsgraphen, die im Konstruktor genutzt wurde. */
	private final Marking ORIGINAL_ROOT;
	
	/** The transition name given to the root. */
	public static final String ROOT_NAME = "root";
	
	private PetriNet petriNet;// dann get last fired und das in die Markings mit einfügen
	// die über getMarking ausgegeben werden
	public PetriNet getPetriNet() { return petriNet; }
	
	/**
	 * Erstellt einen Erreichbarkeitsgraphen, entnimmt dem Petrinetz
	 * seine Startkonfiguration und benutzt sie als Wurzel.
	 * @param petriNet Das Petrinetz
	 */
	public ReachabilityNet(PetriNet petriNet) {
		// Initiale Markierung
		ORIGINAL_ROOT = petriNet.getMarking();
		root = petriNet.getMarking();
		markings.add(root);
		
		this.petriNet = petriNet;
	}
		
	/**
	 * Liefert die Wurzel des Knoten, der die Startkonfiguration
	 * des Petrinetzes darstellt.
	 * @return die Wurzel
	 */
	public Marking getRoot() {
		return root;
	}
	
	/**
	 * Vervollständigt den Erreichbarkeitsgraphen.
	 * @param petriNet Das zum Erreichbarkeitsgraphen gehörige Petrinetz.
	 * @throws GraphIsUnlimitedException Wenn beim Aufbau des Erreichbarkeitsgraphen ein
	 * Paar von Markierungen gefunden wird, das darauf schließen lässt, dass dieser
	 * Erreichbarkeitsgraph unbeschränkt ist.
	 */
	public void analyze(PetriNet petriNet) throws GraphIsUnlimitedException {
		Set<String> transitionNames = new HashSet<String>();
		petriNet.getTransitions().forEach(trans -> transitionNames.add(trans.getId()));
		
		// Eine Liste aller Markierungen, von denen aus alle Transitionen einmal
		// gefeuert werden.
		List<Marking> fireQueue = new ArrayList<Marking>();
		markings.forEach(marking -> fireQueue.add(marking));

		// Diese Markierungen sind bereits vollständig untersucht.
		Set<Marking> alreadyFired = new HashSet<Marking>();
				
		while (fireQueue.size() > 0) {
			Marking origin = fireQueue.get(0);
			for (String transitionId : transitionNames) {
				// Aufbau des Petrinetzes zum Feuern der Transition.
				PetriNet firingNet = null;
				try {
					firingNet = petriNet.fromMarking(origin);					
				} catch (NodeNotFoundException e) {
					System.out.println(e.getMessage());
					fireQueue.remove(origin);
					break;
				}
				
				// Feuern der Transition, Einfügen des Ergebnisses in den
				// Erreichbarkeitsgraphen.
				try {
					firingNet.fire(transitionId);
					String transitionName = null;
					try {
						transitionName = firingNet.getTransitionById(transitionId).getName();
					} catch (NodeNotFoundException e) {
						transitionName = transitionId;
					}
					
					Marking afterFire = firingNet.getMarking();
					Marking newlyCreated = registerMarking(origin, afterFire, transitionId, transitionName);
					
					afterFire.setSourceTransitionId(transitionId);
					newlyCreated.setSourceTransitionId(transitionId);
					if (!alreadyFired.contains(newlyCreated)) {
						fireQueue.add(newlyCreated);
					}
					alreadyFired.add(newlyCreated);
					
					// Test, ob sich nach dem Feuern zwei Markierungen im Graphen befinden,
					// die das Unbeschränktheitskriterium erfüllen.
					Pair<Marking, Marking> causeBeingUnlimited = isUnlimitedDueTo();
					if (causeBeingUnlimited != null) {
						Marking m = causeBeingUnlimited.getFirst();
						Marking mStroke = causeBeingUnlimited.getSecond();
						
						List<Marking> pathBetween = new ArrayList<Marking>();
						pathBetween = getPathBetween(m, mStroke, pathBetween);
						List<MarkingToMarkingArc> arcPathBetween = new ArrayList<MarkingToMarkingArc>();
						try {
							arcPathBetween = getArcsBetweenMarkings(pathBetween);
						} catch (NodeNotFoundException e) {
							e.printStackTrace();
						}
						throw new GraphIsUnlimitedException(m, mStroke, pathBetween, arcPathBetween);
					}
					
				} catch (CannotFireException e) {
					continue;
				}
			}
			
			fireQueue.remove(origin);
		}
	}
	
	/**
	 * Ermittelt einen Pfad zwischen zwei Knoten im Graphen. Gibt null aus, wenn 
	 * dieser nichte existiert.
	 * @param start Der Startknoten.
	 * @param end Der Endknoten.
	 * @param pathSoFar Der bisherige Pfad, an den neue Knoten während der Operation
	 * gehaengt werden.
	 * @return Der Pfad zwischen den Knoten, ist <code>null</code>, wenn dieser nicht
	 * existiert.
	 */
	private List<Marking> getPathBetween(Marking start, Marking end, List<Marking> pathSoFar) {
		Set<Marking> endPredecessors = end.getOrigins();
		Set<Marking> predecessorsOnPath = new HashSet<Marking>();
		endPredecessors.forEach(endPredecessor -> {
			boolean startReachesPredecessor = markingsReaching(endPredecessor).contains(start);
			if (startReachesPredecessor) {
				predecessorsOnPath.add(endPredecessor);
			}
		});
		
		for (Marking predecessorOnPath : predecessorsOnPath) {
			if (predecessorOnPath.equals(start)) {
				pathSoFar.add(end);
				pathSoFar.add(start);
				return pathSoFar;
			} else {
				List<Marking> newPathSoFar = new ArrayList<Marking>();
				newPathSoFar.addAll(pathSoFar);
				newPathSoFar.add(end);
				return getPathBetween(start, predecessorOnPath, newPathSoFar);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Erstellt eine Menge an Markierungen, von denen aus diese Markierung erreichbar ist.
	 * @param target Zielmarkierung, die erreichbar ist.
	 * @return Alle Markierungen, die <code>target</code> erreichen können.
	 */
	public Set<Marking> markingsReaching(Marking target) {
		Set<Marking> canReachTarget = new HashSet<Marking>();
		canReachTarget.add(target);
			
		for (Marking predecessor : target.getOrigins()) {
			if (!canReachTarget.contains(predecessor)) {
				canReachTarget.add(predecessor);
				canReachTarget.addAll(markingsReaching(predecessor, canReachTarget));
			}
		}
		
		return canReachTarget;
	}

	/**
	 * Erstellt eine Menge an Markierungen, von denen aus diese Markierung erreichbar ist.
	 * @param target Die Markierung, die erreichbar sein soll.
	 * @param canReachTarget Eine Menge von Markierungen, die <code>target</code> bereits
	 * erreichen können.
	 * @return Eine möglicherweise erweiterte Menge von Markierungen, die das <code>target</code>
	 * erreichen können.
	 */
	private Set<Marking> markingsReaching(Marking target, Set<Marking> canReachTarget) {
		for (Marking predecessor : target.getOrigins()) {
			if (!canReachTarget.contains(predecessor)) {
				canReachTarget.add(predecessor);
				canReachTarget.addAll(markingsReaching(predecessor, canReachTarget));
			}
		}
		
		return canReachTarget;
	}
	
	/**
	 * Fügt dem Erreichbarkeitsgraphen eine neue Markierung und eine Kante
	 * hinzu, die vom <code>origin</code> zum <code>target</code> verläuft.
	 * @param origin Start der Kante
	 * @param target Ende der Kante
	 * @param sourceTransitionId Die ID der {@link Transition}, durch die <code>target</code>
	 * entstand.
	 * @param sourceTransitionName Der Name der {@link Transition}, durch die 
	 * <code>target</code> entstand.
	 * @return Das Ende der Kante, das kein Duplikat ist und mit dem außerhalb
	 * der Methode weitergearbeitet werden kann.
	 */
	public Marking registerMarking(Marking origin, Marking target, String sourceTransitionId, String sourceTransitionName) {
		Marking nonDuplicateOrigin = getSameMarking(origin);
		Marking nonDuplicateTarget = getSameMarking(target);
			
		nonDuplicateOrigin.registerTarget(nonDuplicateTarget, sourceTransitionId, sourceTransitionName);
		nonDuplicateTarget.registerOrigin(nonDuplicateOrigin, sourceTransitionId, sourceTransitionName);
		// Vorteile des Sets: es werden keine Duplikate eingetragen
		markings.add(nonDuplicateOrigin);
		markings.add(nonDuplicateTarget);
		// Zuletzt Verweis auf das Ziel ausgegeben, mit dem weitergearbeitet werden kann.
		return nonDuplicateTarget;
	}
	
	/**
	 * Liefert eine Markierung mit den gleichen Tokens auf der gleichen Stelle
	 * zurück, wenn sie eine solche schon im Erreichbarkeitsgraphen befindet.
	 * Ansonsten wird der Parameter zurückgeliefert.
	 * Alle Markierungen, die auf irgendeine Art und Weise in den Erreichbarkeitsgraphen
	 * mit eingebunden werden könnten, sollten diese Methode durchlaufen, um Duplikate zu 
	 * vermeiden.
	 * @param comparison Markierung, nach deren Markenverteilung im Graphen nach einer bereits
	 * bestehenden Markierung gesucht wird.
	 * @return Die im Erreichbarkeitsgraphen einzigartige Markierung.
	 */
	public Marking getSameMarking(Marking comparison) {
		for (Marking known : markings) {
			if (haveSameTokenDistribution(known, comparison)) {
				return known;
			}
		}
		
		return comparison;
	}
	
	/**
	 * Liefert true, wenn beide Markierungen die gleiche Zahl an Tokens
	 * an der gleichen Stelle besitzen.
	 * @param m1 Markierung, die mit m2 verglichen wird
	 * @param m2 Markierung, die mit m1 verglichen wird
	 * @return Ob beide Markierungen auf der gleichen Stelle die gleiche Zahl
	 * an Marken haben
	 */
	private static boolean haveSameTokenDistribution(Marking m1, Marking m2) {
		Map<String, Integer> tokens1 = m1.getTokenDistribution();
		Map<String, Integer> tokens2 = m2.getTokenDistribution();
		for (String key : tokens1.keySet()) {
			if (tokens1.get(key) != tokens2.get(key)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Durchsucht alle im Erreichbarkeitsgraphen befindlichen Markierungen
	 * nach einem Paar, das das Unbeschränkheitskriterium erfüllt.
	 * @return Das zuerst gefunden Paar von Markierungen, die das Kriterium 
	 * zur Unbeschränkheit erfüllen. <code>null</code>, wenn kein solches
	 * existiert.
	 */
	private Pair<Marking, Marking> isUnlimitedDueTo() {
		for (Marking marking : markings) {
			Marking causesUnlimitation = predecessorIndicatingIsUnlimited(marking);
			if (causesUnlimitation != null) {
				return new Pair<Marking, Marking>(causesUnlimitation, marking);
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt eine Markierung zurück, von der aus <code>target</code> erreicht werden, sodass
	 * die Rückgabemarkierung als m und <code>target</code> als m' das Kriterium zur Unbeschränktheit
	 * erfüllen.
	 * @param target Markierung, die in der Aufgabenstellung als m' bezeichnet wird 
	 * und von der aus ein m gesucht wird, das zusammen mit m' das Unbeschränkheitskriterium
	 * erfüllt. 
	 * @return Eine Markierung/<code>Marking</code>, wenn eine solche gefunden wurde und <code>null</code>,
	 * wenn keine solche gefunden wurde.
	 */
	private Marking predecessorIndicatingIsUnlimited(Marking target) {
		Set<Marking> predecessors = markingsReaching(target);
		for (Marking predecessor : predecessors) {
			if (indicateIsUnlimited(predecessor, target))
				return predecessor;
		}
		
		return null;
	}
	
	/**
	 * Überprüft, ob dieses Paar an Markierungen das Kriterium der Unbeschränktheit
	 * erfüllt.
	 * @param marking Ausgangsmarkierung. Wird in der Aufgabenstellung als m bezeichnet.
	 * @param reachableFromMarking Die Markierung, die von <code>marking</code>
	 * aus erreichbar sein muss und verglichen wird. Wird in der Aufgabenstellung
	 * als m' bezeichnet.
	 * @return Ob die Markierungen alle Bedingungen erfüllen, die den Erreichbarkeitsgraphen
	 * unebschränkt sein lassen.
	 */
	private boolean indicateIsUnlimited(Marking marking, Marking reachableFromMarking) {
		boolean areConnected = markingsReaching(reachableFromMarking).contains(marking);
		if (!areConnected) {
			// Marken müssen nicht überprüft werden, wenn m' nicht von m aus erreichbar ist, 
			// da das laut Aufgabenstellung 
			System.out.println(reachableFromMarking.toString() + " von " + marking.toString() + " aus nicht erreichbar.");
			return false;
		}
		
		Map<String, Integer> markingTokens = marking.getTokenDistribution();
		Map<String, Integer> reachableMarkingTokens = reachableFromMarking.getTokenDistribution();
		Set<String> placeIds = markingTokens.keySet();
		
		boolean atLeastOneBiggerEqual = false;
		for (String placeId : placeIds) {
			int markingNum = markingTokens.get(placeId);
			int reachableNum = reachableMarkingTokens.get(placeId);

			// Zur Unbeschränktheit muss min. ein Stelle in m' mehr Marken haben als m.
			if (reachableNum > markingNum) {
				atLeastOneBiggerEqual = true;
			}

			// Unbeschränkt, wenn jeder Stelle in m' min. so viele wie in m zugeordnet werden.
			// <==> Beschränkt, wenn eine oder mehr Stellen in m' weniger Marken besitzen.
			if (reachableNum < markingNum) {
				return false;
			}
		}
		
		return atLeastOneBiggerEqual; 
	}
	
	/**
	 * Finde die Kanten, die sich zwischen den Markierungen befinden.
	 * @param markingPath Die Markierungen
	 * @return
	 */
	private List<MarkingToMarkingArc> getArcsBetweenMarkings(List<Marking> markingPath) throws NodeNotFoundException {
		List<MarkingToMarkingArc> arcsPath = new ArrayList<MarkingToMarkingArc>();
		Set<MarkingToMarkingArc> allArcs = getSpecificArcs();
		
		for (int i = 0; i < markingPath.size() - 1; i++) {
			Marking from = markingPath.get(i);
			Marking to = markingPath.get(i + 1);
			
			boolean hasAdded = false;
			for (MarkingToMarkingArc arc : allArcs) {
				if (arc.hasMarkings(from, to)) {
					arcsPath.add(arc);
					hasAdded = true;
					break;
				}
			}
			
			if (!hasAdded) {
				throw new NodeNotFoundException("Ein Kantenpfad für Markierungen " + markingPath.toString() + " wurde nicht gefunden.");
			}
		}
		
		return arcsPath;
	}
	
		
	/**
	 * Setzt den Erreichbarkeitsgraphen in den Zustand zurück, in dem nur sein
	 * aktueller Wurzelknoten angezeigt wird,
	 */
	public void reset() {
		Set<Marking> onlyRootMarking = new HashSet<Marking>();
		onlyRootMarking.add(root);
		root.seperateAll();
		markings = onlyRootMarking;
	}
	
	/**
	 * Setzt <code>newRoot</code> als neuen Wurzelknoten ein.
	 * WICHTIG: es passiert nur das, es findet keine weitere Rücksetzung statt.
	 * Dazu muss die <code>reset()</code>-Methode aufgerufen werden.
	 * @param newRoot Der Knoten, der die neue Wurzel sein soll.
	 */
	public void resetRoot(Marking newRoot) {
		root = newRoot;
	}
	
	/**
	 * Setzt den Knoten als Wurzel ein, der dem Knostruktor als Wurzel kenntlich
	 * gemacht worde ist.
	 * WICHTIG: es passiert nur das, es findet keine weitere Rücksetzung statt.
	 * Dazu muss die <code>reset()</code>-Methode aufgerufen werden.
	 */
	public void resetOriginalRoot() {
		root = ORIGINAL_ROOT;
	}
	
	/**
	 * Liefert alle Markierungen des Erreichbarkeitsgraphen.
	 * @return Die Markierungen
	 */
	public Set<Marking> getMarkings() {
		return markings;
	}
	
	/**
	 * Gibt die Markierung aus, die diese ID besitzt.
	 * @param id Die ID, mit der gesucht wird.
	 * @return Die gesuchte Markierung.
	 * @throws NodeNotFoundException Wenn kein Knoten mit dieser ID existiert.
	 */
	public Marking getMarking(String id) throws NodeNotFoundException {
		for (Marking marking : markings) {
			if (marking.getId().equals(id)) {
//				Transition lastFired = getPetriNet().getLastFired();
//				if (lastFired != null) {
//					marking.setSourceTransitionId(lastFired.getId());
//				}
				return marking;
			}
		}
		throw new NodeNotFoundException("Konnte im Erreichbarkeitsgraph Knoten mit ID " + id + " nicht finden.");
	}
		
	/**
	 * Gibt alle Kanten des Graphen zurück.
	 * @return Die Kanten.
	 */
	public Set<Arc> getArcs() {
		Set<Arc> arcs = new HashSet<Arc>();
		markings.forEach(marking -> arcs.addAll(marking.getArcs()));
		return arcs;
	}
	
	public int getNumUniqueArcs() {
		// Idee schauen, welche Knoten unterschiedliche Endpunkte haben
		Set<MarkingToMarkingArc> arcs = getSpecificArcs();
		Set<MarkingToMarkingArc> uniqueArcs = new HashSet<MarkingToMarkingArc>();
		
		for (MarkingToMarkingArc arc : arcs) {
			Marking from = arc.getFrom();
			Marking to = arc.getTo();
			
			boolean alreadyContained = false;
			for (MarkingToMarkingArc uniqueArc : uniqueArcs) {
				Marking uniqueFrom = uniqueArc.getFrom();
				Marking uniqueTo = uniqueArc.getTo();
				String sourceTrans1 = uniqueArc.getSourceTransitionId();
				String sourceTrans2 = arc.getSourceTransitionId();
				if (from.equals(uniqueFrom) && to.equals(uniqueTo) && sourceTrans1 == sourceTrans2)
					alreadyContained = true;
			}
			
			if (!alreadyContained) {
				uniqueArcs.add(arc);
			}
		}
		
		return uniqueArcs.size();
	}
	
	public Set<MarkingToMarkingArc> getSpecificArcs() {
		Set<MarkingToMarkingArc> arcs = new HashSet<MarkingToMarkingArc>();
		markings.forEach(marking -> arcs.addAll(marking.getSpecificArcs()));
		return arcs;
	}
	
	/**
	 * Übersichtliche Darstellung als String, genutzt, um Netze ohne
	 * GUI halbwegs visualisieren zu können. 
	 * @return Die Übersicht.
	 */
	@Override
	public String toString() {
		String repr = "";
		for (Marking marking : markings) {
			repr += marking.toString() + "\n";
			for (Marking target : marking.getTargets())
				repr += "   zu  " + target.toString() + "\n";
			for (Marking origin : marking.getOrigins())
				repr += "   von " + origin.toString() + "\n";			
		}
		
		return repr;
	}
	
	@Override
	public Set<NodeModel> getNodes() {
		Set<NodeModel> nodes = new HashSet<NodeModel>();
		nodes.addAll(markings);
		return nodes;
	}
}
