package view;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import model.Arc;
import model.NodeNotFoundException;
import model.PetriNet;
import model.Place;
import model.Transition;

/**
 * Visuelles Gegenstück zum Petrinetz aus {@link model} ({@link PetriNet}).
 */
public class VisualPetriGraph extends VisualGraph {
	/** Das Petrinetz, dessen Zustand durch den visuellen Graphen angzeigt wird. */
	private PetriNet sourceNet;
	/** Die aktuelle markierte Stelle. */
	private Node marked = null;
	
	/**
	 * @param petriNet Das Petrinetz, dessen Zustand durch den visuellen Graphen angzeigt wird.
	 */
	public VisualPetriGraph(PetriNet petriNet) {
		super("Petrinetz");		
		sourceNet = petriNet;
		// Hier wird der Stylesheet angewandt.
		addAttribute("ui.stylesheet", CSS_FILE);
		createGraphNodes();		
	}
	
	/**
	 * Setzt die Knoten.
	 */
	private void createGraphNodes() {
		for (Place place : sourceNet.getPlaces()) {
			Node placeGraphNode = addNode(place.getId());
			placeGraphNode.addAttribute(LABEL_ATTRIBUTE, place.toLabel());
			placeGraphNode.addAttribute(CLASS_ATTRIBUTE, "place");
			placeGraphNode.addAttribute(CLASS_ATTRIBUTE, getTokenCssClass(place.getNumTokens()));
			int x = place.getPosition().getX();
			int y = -place.getPosition().getY();
			placeGraphNode.addAttribute(POSITION_ATTRIBUTE, x, y);
		}
				
		for (Transition transition : sourceNet.getTransitions()) {
			Node transitionGraphNode = addNode(transition.getId());
			transitionGraphNode.addAttribute(LABEL_ATTRIBUTE, transition.toLabel());
			transitionGraphNode.addAttribute(CLASS_ATTRIBUTE, "transition");
			int x = transition.getPosition().getX();
			int y = -transition.getPosition().getY();
			transitionGraphNode.addAttribute(POSITION_ATTRIBUTE, x, y);
		}
		
		for (Arc arc : sourceNet.getArcs()) {
			String fromId = arc.getFrom().getId();
			String toId = arc.getTo().getId();
			Node from = getNode(fromId);
			Node to = getNode(toId);
			Edge edge = addEdge(arc.getId(), from, to, true);
			edge.addAttribute(LABEL_ATTRIBUTE, arc.toLabel());
		}
	}
	
	/**
	 * Gibt an das {@link PetriNet} weiter, welche Stelle eine Marke mehr bekommen soll.
	 * @throws NodeNotFoundException Wenn die Stelle nicht existiert.
	 */
	public void markedPlusOne() throws NodeNotFoundException {
		String markedId = marked.getId();
		try {
			sourceNet.addToken(markedId);
			update();
			markChosenPlace(markedId);
		} catch (NodeNotFoundException e) {
			throw new NodeNotFoundException("Konnte ID " + markedId + " nicht finden, um Token um 1 zu erhöhen.");
		}
	}
	
	/**
	 * Gibt an das {@link PetriNet} weiter, welche Stelle eine Marke weniger bekommen soll.
	 * @throws NodeNotFoundException Wenn die Stelle nicht existiert.
	 */
	public void markedMinusOne() throws NodeNotFoundException {
		String markedId = marked.getId();
		try {
			sourceNet.subtractToken(markedId);
			update();
			markChosenPlace(markedId);
		} catch (NodeNotFoundException e) {
			throw new NodeNotFoundException("Konnte ID " + markedId + " nicht finden, um Token um 1 zu erniedrigen.");
		}
	}
	
	/**
	 * Markiert den Knoten mit dieser ID visuell.
	 * @param id Die ID des zu markierenden Knoten.
	 */
	public void markChosenPlace(String id) {
		if (marked != null) {
			Place modelMarked;
			try {
				modelMarked = sourceNet.getPlaceById(marked.getId());	
			} catch (NodeNotFoundException e) {
				System.out.println(e.getMessage());
				return;
			}
			
			marked.removeAttribute(CLASS_ATTRIBUTE);
			marked.addAttribute(CLASS_ATTRIBUTE, getTokenCssClass(modelMarked.getNumTokens()));
		}
		
		Place modelMarked = null;
		try {
			modelMarked = sourceNet.getPlaceById(id); 
		} catch (NodeNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		if (modelMarked == null) {
			return;
		}
		
		marked = getNode(id);
		marked.removeAttribute(CLASS_ATTRIBUTE);
		marked.addAttribute(CLASS_ATTRIBUTE, getMarkedTokenCssClass(modelMarked.getNumTokens()));
	}
	
	/**
	 * Passt die visuelle Repräsentation den Daten des Modells im {@link PetriNet} an.
	 */
	public void update() {
		for (Place place : sourceNet.getPlaces()) {
			String placeId = place.getId();
			
			Node visualPlace = this.getNode(placeId);
			visualPlace.removeAttribute(LABEL_ATTRIBUTE);
			visualPlace.addAttribute(LABEL_ATTRIBUTE, place.toLabel());
			
			visualPlace.removeAttribute(CLASS_ATTRIBUTE);
			visualPlace.addAttribute(CLASS_ATTRIBUTE, "place");
			visualPlace.addAttribute(CLASS_ATTRIBUTE, getTokenCssClass(place.getNumTokens()));
		}
	}
	
	/**
	 * Ermittelt die passende CSS-Klasse einer Stelle anhand der Zahl der Marken.
	 * @param numTokens Zahl der Marken.
	 * @return Name der passenden CSS-Klasse.
	 */
	private static String getTokenCssClass(int numTokens) {
		if (numTokens <= 0) {
			return "has0Tokens";
		} else if (numTokens < 10) {
			return "has" + String.valueOf(numTokens) + "Tokens";
		} else {
			return "hasMoreTokens";
		}
	}
	
	/**
	 * Ermittelt die passende CSS-Klasse einer markierten Stelle anhand der Zahl der Marken.
	 * @param numTokens Zahl der Marken.
	 * @return Name der passenden CSS-Klasse.
	 */
	private static String getMarkedTokenCssClass(int numTokens) {
		if (numTokens <= 0) {
			return "markedhas0Tokens";
		} else if (numTokens < 10) {
			return "markedhas" + String.valueOf(numTokens) + "Tokens";
		} else {
			return "markedhasMoreTokens";
		}
	}
	
	/**
	 * Das Petrinetz, auf dem die visuelle Repräsentation operiert.
	 * @return Das Petrinetz.
	 */
	public PetriNet getPetriNet() {
		return sourceNet;
	}

}
