package controller;

import java.util.Map;

import model.CannotFireException;
import model.GraphIsUnlimitedException;
import model.Marking;
import model.NodeModel;
import model.NodeNotFoundException;
import model.PetriNet;
import model.Place;
import model.ReachabilityNet;
import model.Transition;
//import view.VisualPetriGraph;
//import mypackage.view.MyFrame;
import view.GraphsFrame;
import view.VisualPetriGraph;
import view.VisualReachabilityGraph;

/**
 * Handelt die Ereignisse in den beiden Graphen.
 */
public class Controller {
	/** Hauptfenster der Anwendung */
	private GraphsFrame frame;
	/** Angezeigtes Petrinetz. */
	private VisualPetriGraph petriGraph;
	/** Angezeigter Erreichbarkeitsgraph. */
	private VisualReachabilityGraph reachGraph;
	/** Modell des Petrinetzes. */
	private PetriNet petriNet;
	/** Modell des Erreichbarkeitsgraphen. */
	private ReachabilityNet reachNet;
	
	/**
	 * Konstruktor des zentralen {@link Controller}s.
	 * @param frame Das Fenster, in dem die Anwendung gezeigt wurde.
	 * @param petriNet Das Modell des Petrinetzes, auf die Anwendung gerade operiert.
	 * @param reachNet Das Modell des Erreichbarkeitsgraphen, auf dem die Anwendung operiert.
	 */
	public Controller(GraphsFrame frame, PetriNet petriNet, ReachabilityNet reachNet) {
		this.frame = frame;	
		
		// Initialisierung der Modelle der Netze.
		this.petriNet = petriNet;
		petriNet.getMarking();
		this.reachNet = reachNet;
		
		// Initialisierung der visuellen Graphen.
		petriGraph = new VisualPetriGraph(petriNet);
		reachGraph = new VisualReachabilityGraph(reachNet);
		petriGraph.update();
		reachGraph.update(reachNet);
	}
	
	/**
	 * Handelt den Klick auf einen Knoten im {@link PetriGraph}.
	 * @param id Die ID des Knotens, auf den geklickt wurde.
	 * @throws NodeNotFoundException Wenn kein Knoten mit dieser ID existiert.
	 */
	public void clickNodeInGraph(String id) throws NodeNotFoundException {
		NodeModel clickedModelNode = petriNet.getNodeById(id);
		
		if (clickedModelNode instanceof Place) {
			clickPlaceInGraph(id);
		} else {
			clickTransitionInGraph(id);
		}
	}
	
	
	/**
	 * Handelt den Klick auf eine Stelle im {@link PetriGraph}.
	 * @param id Die ID der Stelle, auf die geklickt wurde.
	 * @throws NodeNotFoundException Wenn keine Stelle mit dieser ID existiert.
	 */
	private void clickPlaceInGraph(String id) throws NodeNotFoundException {
		petriGraph.markChosenPlace(id);
	}
	
	/**
	 * Handelt den Klick auf einen Knoten im {@link PetriGraph}.
	 * @param id Die ID des Knotens, auf den geklickt wurde.
	 * @throws NodeNotFoundException Wenn kein Knoten mit dieser ID existiert.
	 */
	private void clickTransitionInGraph(String id) throws NodeNotFoundException {
		// Update Petrinetz
		Marking before = petriNet.getMarking();
		try {
			petriNet.fire(id);
			petriGraph.update();
			frame.addText("Pertinetz konnte feuern und Änderung wird angezeigt.");
		} catch (CannotFireException e) {
			frame.addText("Das Petrinetz kann die Transition " + id + " nicht feuern.");
			return;
		}
		
		// Update Erreichbarkeitsgraph
		Marking after = petriNet.getMarking();
		after.setSourceTransitionId(id);
		Transition fired = petriNet.getTransitionById(id);
		reachNet.registerMarking(before, after, id, fired.getName());
		reachGraph.update(reachNet);
		reachGraph.markNewestAddition(before, after);
	}
	
	/**
	 * Handelt den Klick auf eine Markierung im {@link RechabilityGraph}.
	 * @param id Die ID der Markierung, auf die geklickt wurde.
	 * @throws NodeNotFoundException Wenn kein Knoten mit dieser ID existiert.
	 */
	public void clickNodeInReachGraph(String id) throws NodeNotFoundException {
		// Wenn ein Knoten im Erreichbarkeitsgraph geklickt wird, dann soll
		// der PetriGraph die Tokenverteilung annehmen.
		Marking clicked = reachNet.getMarking(id);
		Map<String, Integer> distribution = clicked.getTokenDistribution();
		try {
			petriNet.resetTokens(distribution);
			frame.addText("Das Petrinetz wurde auf die Markierung " + clicked.toLabel() + " gesetzt.");
		} catch (NodeNotFoundException e) {
			frame.addText("Fehler: es wurde eine Stelle nicht gefunden: " + e.getMessage());
		}
		petriGraph.update();
	}
	
	/**
	 * Analysiert den aktuellen Erreichbarkeitsgraphen auf Beschränktheit.
	 * @return <code>true</code>, wenn der Graph beschränkt ist, sonst
	 * <code>false</code>.
	 */
	public boolean analyze() {
		try {
			reachNet.analyze(petriNet);
			reachGraph.update(reachNet);			
			return true;
		} catch (GraphIsUnlimitedException e) {
			frame.addText("Petrinetz ist unbeschränkt.\nm: " + e.getStart().guiName() + "\nm': " + e.getEnd().guiName());
			frame.addText("Der Pfad: ");
			e.getFullNodePath().forEach(marking -> frame.addText(marking.toString()));
			
			reachGraph.update(reachNet);
			reachGraph.markAbortCriterion(e.getFullNodePath());			
			return false;
		}
	}
	
	/**
	 * Erhöht die Zahl der Tokens in der markierten Stelle um 1.
	 */
	public void markedPlusOne() {
		try {
			petriGraph.markedPlusOne();
			Marking newRoot = petriGraph.getPetriNet().getMarking();
			ReachabilityNet reachNet = reachGraph.getReachNet();
			// Hier ist wichtig, dass resetRoot auf die aktuell hinterlegte Wurzel zurücksetzt,
			// während resetOriginalRoot auf die Wurzel zurücksetzt, die dem Konstruktor übergeben
			// worden ist.
			reachNet.resetRoot(newRoot);
			reachNet.reset();
			reachGraph.update(reachNet);			
		} catch (NodeNotFoundException e) {
			frame.addText(e.getMessage());
		}
	}
	
	/**
	 * Zieht einen Token von der markierten Stelle ab bis minimal 0 Tokens 
	 * verbleiben.
	 */
	public void markedMinusOne() {
		try {
			petriGraph.markedMinusOne();
			Marking newRoot = petriGraph.getPetriNet().getMarking();
			ReachabilityNet reachNet = reachGraph.getReachNet();
			
			reachNet.resetRoot(newRoot);
			reachNet.reset();
			reachGraph.update(reachNet);
		} catch (NodeNotFoundException e) {
			frame.addText(e.getMessage());
		}
	}
	
	/** 
	 * Setzt den Erreichbarkeitsgraphen auf seinen aktuellen Wurzelknoten zurück.
	 */
	public void resetReachToCurrentRoot() {
		reachNet.reset();
		reachGraph.update(reachNet);
	}
	
	/**
	 * Setzt den EG auf seine aktuelle Wurzel zurück und das Petrinetz auf die
	 * Markierung dieser Wurzel.
	 */
	public void resetPetriGraphToCurrentReachabilityRoot() {
		Marking root = reachNet.getRoot();
		PetriNet newRootNet;
		try {
			newRootNet = petriNet.fromMarking(root);
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		this.petriNet = newRootNet;
		this.petriGraph = frame.setPetriNet(newRootNet);
		petriGraph.update();
		System.out.println("Reset Btn erfolgreich");
	}
	
	/**
	 * Setzt den das Petrinetz und den angezeigten Graphen in den Originalzustand
	 * zurück, d.h. zur anfänglichen Besetzung von Marken zu ihren Stellen.
	 */
	public void resetPetriGraph() {		
		try {
			petriNet.resetTokens();
		} catch (NodeNotFoundException e) {
			frame.addText("Fehler: es wurde eine Stelle nicht gefunden: " + e.getMessage());
		}
		petriGraph.update();
	}
	
	/**
	 * Setzt den Erreichbarkeitsgraphen zurück in seine Ursprungsform, d.h.
	 * der Graph, der nur aus seinem ursprünglichen Wurzelknoten besteht.
	 */
	public void resetReachGraphToOriginalRoot() {
		reachNet.resetOriginalRoot();
		reachNet.reset();
		reachGraph.update(reachNet);
	}
	
		
	/**
	 * Gibt den Graphen des Petrinetzes aus, das von diesem Controller aus
	 * kontrolliert wird.
	 * @return Der Graph des Petrinetzes.
	 */
	public VisualPetriGraph getPetriGraph() {
		return petriGraph;		
	}
	
	/**
	 * Gibt den Graphen des Erreichbarkeitsgraphen aus, der von diesem
	 * Controller aus kontrolliert wird.
	 * @return Der Erreichbarkeitsgraph.
	 */
	public VisualReachabilityGraph getReachGraph() {
		return reachGraph;
	}	
}