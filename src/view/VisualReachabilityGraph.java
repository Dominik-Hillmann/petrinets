package view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import model.Arc;
import model.Marking;
import model.MarkingToMarkingArc;
import model.NodeNotFoundException;
import model.Pair;
import model.ReachabilityNet;
import model.Transition;

/**
 * Visuelles Gegenstück zum Erreichbarkeitsgraphen aus {@link model} ({@link RechabilityNet}).
 */
public class VisualReachabilityGraph extends VisualGraph {
	/** Der ER, dessen Zustand diese Klasse visuell darstellt. */
	private ReachabilityNet reachNet;
	/** Name der CSS-Klasse einer Markierung um kritischen Pfad zwischen m und m'. */
	private static final String ABORT_CRITERION_CLASS_NAME = "abort";
	/** Name der CSS-Klasse einer Markierung, die gerade hinzugefügt wurde. */
	private static final String NEWLY_ADDED_CLASS_NAME = "newlyadded";
	/** Name der CSS-Klasse einer Markierung. */
	private static final String MARKING_CLASS_NAME = "marking";
	/** Name der CSS-Klasse der Wurzel des Graphen. */
	private static final String ROOT_CLASS_NAME = "markingroot";
	/** Name der CSS-Klasse einer Wurzel, die sich im kritischen Pfad zwischen m und m' befindet. */
	private static final String ROOT_IN_ABORT_NAME = "rootinabort";
	
	/**
	 * @param reach Der ER, dessen Zustand diese Klasse visuell darstellt.
	 */
	public VisualReachabilityGraph(ReachabilityNet reach) {
		super("Erreichbarkeitsgraph");
		reachNet = reach;		
		addAttribute("ui.stylesheet", CSS_FILE);
		createGraphNodes();
	}
	
	/**
	 * Setzt die Knoten.
	 */
	private void createGraphNodes() {
		for (Marking marking : reachNet.getMarkings()) {
			Node markingGraphicalNode = addNode(marking.getId());
			markingGraphicalNode.addAttribute(CLASS_ATTRIBUTE, "marking");
			markingGraphicalNode.addAttribute(LABEL_ATTRIBUTE, marking.getName());
			
			if (marking.equals(reachNet.getRoot())) {
				markingGraphicalNode.addAttribute(CLASS_ATTRIBUTE, "root");
			}
		}
		
		Set<String> alreadyAddedIds = new HashSet<String>();
		for (Arc arc : reachNet.getArcs()) {
			String fromId = arc.getFrom().getId();
			String toId = arc.getTo().getId();
			Pair<String, String> idPair = new Pair<String, String>(fromId, toId);
			
			Node from = getNode(fromId);
			Node to = getNode(toId);
			boolean idAlreadyUsed = alreadyAddedIds.contains(idPair.toString());
			if (idAlreadyUsed) {
				continue;
			} else {
				addEdge(arc.getId(), from, to, true);
				alreadyAddedIds.add(idPair.toString());
			}	
		}
	}
	
	/**
	 * Passt die visuelle Anzeige des ER dem Zustand des im Konstruktor mitgegebenen
	 * {@link ReachabilityNet} an.
	 * @param reachNet Der mitgegebene ER.
	 */
	public void update(ReachabilityNet reachNet) {
		// Ermittlung der Unterschiede der Graphen
		Set<String> idsToBeAdded = getIdsToBeAdded(reachNet);
		Set<String> idsToBeRemoved = getIdsToBeRemoved(reachNet);
		
		// Hier werden zunächst nur die Stellen dem visuelle Graphen hinzugefügt.
		for (String addedId : idsToBeAdded) {
			// Ermittle Stelle im Modell.
			Marking marking;
			try {
				marking = reachNet.getMarking(addedId);
			} catch (NodeNotFoundException e) {
				continue;
			}			
			// Füge ermittelte Stelle dem visuellen Modell hinzu (ohne Kanten).
			Node markingVisual = addNode(addedId);
			markingVisual.addAttribute(LABEL_ATTRIBUTE, marking.guiName());
			markingVisual.addAttribute(CLASS_ATTRIBUTE, MARKING_CLASS_NAME);		
		}	
		
		Set<String> visualIds = new HashSet<String>();
		getEachNode().forEach(node -> visualIds.add(node.getId()));
				
		for (String addedId : idsToBeAdded) {
			Marking marking;
			try {
				marking = reachNet.getMarking(addedId);
			} catch (NodeNotFoundException e) {
				System.out.println("Unable to find marking");
				continue;
			}
			
			// Finde Kanten im Modell.
			Set<String> modelArcIds = new HashSet<String>();
			marking.getArcs().forEach(arc -> modelArcIds.add(arc.getId()));
			
			// Finde heraus, welche Kanten bereits im Modell sind.
			Set<String> visualArcIds = new HashSet<String>();
			getEachEdge().forEach(edge -> visualArcIds.add(edge.getId()));
			
			// Ermittle alle Kanten-IDs, die hinzugefügt werden müssen.
			Set<String> arcsToBeAdded = new HashSet<String>();
			modelArcIds.forEach(id -> {
				if (!visualArcIds.contains(id)) {
					arcsToBeAdded.add(id);
				}
			});
			
			// Füge jede ermittelte Kante dem visuellen Graphen hinzu.
			for (String arcIdToBeAdded : arcsToBeAdded) {
				MarkingToMarkingArc modelArc;
				try {
					modelArc = (MarkingToMarkingArc) marking.getArc(arcIdToBeAdded);
				} catch (NodeNotFoundException e) {
					System.out.println("Die ID " + arcIdToBeAdded + " konnte nicht gefunden werden.");
					continue;
				}
				String startNodeId = modelArc.getFrom().getId();
				Node start = getNode(startNodeId);
				String endNodeId = modelArc.getTo().getId();
				Node end = getNode(endNodeId);
				
				Marking endMarking = null;
				try {
					endMarking = reachNet.getMarking(endNodeId);
				} catch (NodeNotFoundException e) {
					e.printStackTrace();
				}
				
				if (!edgeAlreadyAdded(start, end)) {				
					Edge edge = addEdge(modelArc.getId(), start, end, true);
					if (endMarking != null)
						edge.addAttribute(LABEL_ATTRIBUTE, modelArc.getSourceTransitionName());
				}
			}
		}
		
		// Anpassung der Kanten
		Set<MarkingToMarkingArc> modelEdgeIds = new HashSet<MarkingToMarkingArc>();
		reachNet.getArcs().forEach(edge -> modelEdgeIds.add((MarkingToMarkingArc) edge));
		
		Set<String> visualEdgeIds = new HashSet<String>();
		getEdgeSet().forEach(edge -> visualEdgeIds.add(edge.getId()));
		
		Set<MarkingToMarkingArc> edgeIdsToBeAdded = new HashSet<MarkingToMarkingArc>();
		modelEdgeIds.forEach(edge -> {
			if (!visualEdgeIds.contains(edge.getId())) {
				edgeIdsToBeAdded.add(edge);
			}
		});
		
		for (MarkingToMarkingArc edgeToBeAdded : edgeIdsToBeAdded) {
			Node start = getNode(edgeToBeAdded.getFrom().getId());
			Node end = getNode(edgeToBeAdded.getTo().getId());
			Marking endMarking = null;
			try {
				endMarking = reachNet.getMarking(edgeToBeAdded.getTo().getId());
			} catch (NodeNotFoundException | NullPointerException e) { }
			
			if (!edgeAlreadyAdded(start, end)) {
				Edge edge = addEdge(edgeToBeAdded.getId(), start, end, true);
				if (endMarking != null)
					edge.addAttribute(LABEL_ATTRIBUTE, edgeToBeAdded.getSourceTransitionName());
			}
		}		
		
		for (String removalId : idsToBeRemoved) {
			Node removalNode = getNode(removalId);
			removeNode(removalNode);
		}
		
		markRoot();
	}
	
	/**
	 * Gibt der Wurzel ihre spezielle CSS-Klasse.
	 */
	private void markRoot() {
		Marking root = reachNet.getRoot();
		Node visualRoot = getNode(root.getId());
		visualRoot.addAttribute(CLASS_ATTRIBUTE, ROOT_CLASS_NAME);
	}
	
	/**
	 * Wenn eine {@link Transition} geklickt wird, dann sollen alle Knoten und Kanten
	 * im Erreichbarkeitsgraphen markiert werden, die dadurch entstanden sind.
	 * Sollte nur im Handler für den Klick einer Transition genutzt werden.
	 */
	public void markNewestAddition(Marking beforeUnchecked, Marking afterUnchecked) {
		Marking before = reachNet.getSameMarking(beforeUnchecked);
		Marking after = reachNet.getSameMarking(afterUnchecked);
		
		// zuerst allen die "normale" Markierung geben
		for (Node marking : getNodeSet()) {
			String cssClassName = marking.getAttribute(CLASS_ATTRIBUTE).toString();
			if (cssClassName == null) {
				continue;
			}
			
			boolean nodeIsRoot = cssClassName.equals(ROOT_CLASS_NAME);
			boolean nodeBelongsToAbortCriterion = cssClassName.equals(ABORT_CRITERION_CLASS_NAME);
			if (nodeIsRoot || nodeBelongsToAbortCriterion) {
				continue;
			}
			
			marking.addAttribute(CLASS_ATTRIBUTE, MARKING_CLASS_NAME);
		}
		
		// Farbe der Kanten zurücksetzen
		for (Edge edge : getEdgeSet()) {
			String cssClassName = null;
			try {
				cssClassName = edge.getAttribute(CLASS_ATTRIBUTE).toString();	
			} catch (NullPointerException e) {
				continue;
			}			
			
			if (cssClassName.equals(ABORT_CRITERION_CLASS_NAME)) {
				continue;
			}
			
			edge.removeAttribute(CLASS_ATTRIBUTE);
		}
		
		// dann werden die neu hinzugefügten ermittelt und ihnen die Klasse ermittelt
		try {
			Node nodeToBeMarked = getNode(after.getId());
			nodeToBeMarked.addAttribute(CLASS_ATTRIBUTE, NEWLY_ADDED_CLASS_NAME);
		} catch (NullPointerException e) { }
		try {
			Edge edgeToBeMarked = getEdgeBetween(after.getId(), before.getId());
			edgeToBeMarked.addAttribute(CLASS_ATTRIBUTE, NEWLY_ADDED_CLASS_NAME);
		} catch (NullPointerException e) {
			System.out.println("Konnte keine Kante finden: " + before.getId() + ", " + after.getId() + " " + getNodeSet().toString());
		}
	}
	
	/**
	 * Findet die IDs der Knoten, die sich noch nicht grafisch dargestellt werden.
	 * @param reachNet Der Erreichbarkeitsgraph, mit dem verglichen wird.
	 * @return Die IDs.
	 */
	private Set<String> getIdsToBeAdded(ReachabilityNet reachNet) {
		Set<String> modelIds = new HashSet<String>(); 
		reachNet.getMarkings().forEach(marking -> modelIds.add(marking.getId()));
		
		Set<String> visualIds = new HashSet<String>();
		getEachNode().forEach(node -> visualIds.add(node.getId()));
		
		Set<String> idsToBeAdded = new HashSet<String>();
		modelIds.forEach(id -> {
			if (!visualIds.contains(id))
				idsToBeAdded.add(id);
		});
		
		return idsToBeAdded;
	}
	
	/**
	 * Findet die IDs der Knoten, die nicht mehr grafisch dargestellt werden.
	 * @param reachNet Der Erreichbarkeitsgraph, mit dem verglichen wird.
	 * @return Die IDs.
	 */
	private Set<String> getIdsToBeRemoved(ReachabilityNet reachNet) {
		Set<String> modelIds = new HashSet<String>(); 
		reachNet.getMarkings().forEach(marking -> modelIds.add(marking.getId()));
		
		Set<String> visualIds = new HashSet<String>();
		getEachNode().forEach(node -> visualIds.add(node.getId()));
		
		Set<String> idsToBeRemoved = new HashSet<String>();
		visualIds.forEach(id -> {
			if (!modelIds.contains(id)) {
				idsToBeRemoved.add(id);
			}
		});
		
		return idsToBeRemoved;
	}
	
	/**
	 * Markiert das Abbruchkriterium im visuellen Graphen.
	 * @param path Der Pfad von Markierungen zwischen m und m'.
	 */
	public void markAbortCriterion(List<Marking> path) {
		// Knoten markieren
		Marking root = reachNet.getRoot();
		for (Marking marking : path) {
			if (root.equals(marking)) {
				Node visualRoot = getNode(marking.getId());
				visualRoot.addAttribute(CLASS_ATTRIBUTE, ROOT_IN_ABORT_NAME);
			} else {
				Node visualMarking = getNode(marking.getId());
				visualMarking.addAttribute(CLASS_ATTRIBUTE, ABORT_CRITERION_CLASS_NAME);
			}
		}
		
		// Kanten markieren
		for (int i = 0; i < path.size() - 1; i++) {
			Marking start = path.get(i);
			Marking end = path.get(i + 1);
			Edge edgeBetween = getEdgeBetween(start.getId(), end.getId());
			
			if (edgeBetween != null) {
				edgeBetween.addAttribute(CLASS_ATTRIBUTE, ABORT_CRITERION_CLASS_NAME);
			}
		}
	}
	
	/**
	 * Findet einen Pfad zwischen den Markierungen mit diesen IDs.
	 * Wenn kein Pfad existiert, so wird {@link null} zurückgeliefert.
	 * @param startId Die ID von m.
	 * @param endId Die ID von m'
	 * @return Der Pfad oder {@link null}.
	 */
	private Edge getEdgeBetween(String startId, String endId) {
		for (Edge edge : this.getEdgeSet()) {
			Node end = edge.getNode0();
			Node start = edge.getNode1();
			boolean startSame = start.getId().equals(startId);
			boolean endSame = end.getId().equals(endId);
			
			if (startSame && endSame) {
				return edge;
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt den Modellgraphen aus, auf dem der visuelle Graph operiert.
	 * @return Der Modellgraph.
	 */
	public ReachabilityNet getReachNet() {
		return reachNet;
	}
	
	/**
	 * Zeigt, ob sich bereits eine Kante zwischen diesen beiden {@link Node}s befindet.
	 * @param start Startknoten.
	 * @param end Endknoten.
	 * @return Ob zwischen Start- und Endknoten eine Kante existiert.
	 */
	private boolean edgeAlreadyAdded(Node start, Node end) {
		String startId = start.getId();
		String endId = end.getId();
		
		for (Edge edge : getEdgeSet()) {
			boolean startSame = edge.getNode0().getId().equals(startId);
			boolean endSame = edge.getNode1().getId().equals(endId);
			if (startSame && endSame) {
				return true;
			}
		}
		return false;
	}
}
