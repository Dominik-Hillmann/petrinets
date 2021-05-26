package model;

import java.util.ArrayList;
import java.util.List;
/**
 * Wird geworfen, wenn ein Erreichbarkeitsgraph nicht beschränkt ist.
 */
public class GraphIsUnlimitedException extends Exception {	
	/** Von Eclipse generierte ID. */
	private static final long serialVersionUID = -3596817224615912974L;
	/** Der Startknoten des kritschen Pfades (m). */
	private Marking start;
	/** Der Endknoten des kritischen Pfades (m'). */
	private Marking end;
	/** Pfad der Knoten zwischen m und m'. */
	private List<Marking> nodePath;
	/** Pfad der Kanten zwischen m und m'. */
	private List<MarkingToMarkingArc> arcPath;
	
	/**
	 * @param start m.
	 * @param end m'.
	 * @param nodePath Pfad von Knoten zwischen m und m'.
	 * @param arcPath Pfad von Kanten zwischen m und m'.
	 */
	public GraphIsUnlimitedException(Marking start, Marking end, List<Marking> nodePath, List<MarkingToMarkingArc> arcPath) {
		super("Es wurde ein Paar von Markierungen gefunden, die das Unbeschränktheitskriterium " +
				"erfüllen: " + start.toString() + " und " + end.toString() + ".");
		this.start = start;
		this.end = end;
		this.nodePath = nodePath;
		this.arcPath = arcPath;
	}
	
	/**
	 * Gibt die geordnete Liste der Knoten zwischen m und m' aus.
	 * @return Der Pfad.
	 */
	public List<Marking> getFullNodePath() {
		return nodePath;
	}
	
	/**
	 * Gibt den Pfad der Kanten zwischen m und m' aus.
	 * @return Der Pfad.
	 */
	public List<MarkingToMarkingArc> getFullArcPath() {
		return arcPath;
	}
	
	/**
	 * Gibt die kritischen Pfade als Stringrepräsentationen aus.
	 * @return Die kritischen Pfade.
	 */
	public List<String> getFormattedTransitionPath() {
		List<String> reversedPath = new ArrayList<String>();
		for (int i = nodePath.size() - 1; i >= 0; i--) {
			reversedPath.add(nodePath.get(i).getSourceTransitionId());
		}
		
		if (reversedPath.get(0).equals("null")) {
			reversedPath.remove(0);
		}
		
		boolean firstIsRoot = reversedPath.get(0).equals(ReachabilityNet.ROOT_NAME);
		if (firstIsRoot) {
			reversedPath.remove(ReachabilityNet.ROOT_NAME);
		}
		
		return reversedPath;
	}
	
	/**
	 * Gibt den Anfang des kritischen Pfades m aus.
	 * @return m.
	 */
	public Marking getStart() {
		return start;
	}
	
	/**
	 * Gibt das Ende des kritischen Pfades m' aus.
	 * @return m'.
	 */
	public Marking getEnd() {
		return end;
	}
}
