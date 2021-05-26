package model;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import propra.pnml.PNMLWopedParser;

/**
 * Implementierter Parser, der vom vorgegebenen Parser erbt.
 */
public class PnmlParser extends PNMLWopedParser {
	/** Gelesenes Petrinetz. */
	private PetriNet petriNet;
	/** Gelesene Stellen. */
	private Set<Place> places = new HashSet<Place>();
	/** Gelesene Transitionen. */
	private Set<Transition> transitions = new HashSet<Transition>();
	/** Zuordnung der IDs zu den angzeigten Namen. */
	private Map<String, String> names = new HashMap<String, String>();
	/** Zuordnung der IDs zu den grafischen Positionen. */
	private Map<String, Position> positions = new HashMap<String, Position>();
	/** Zuordnung der Stellen-IDs zu der gegebenen Anzahl der Tokens. */
	private Map<String, Integer> tokens = new HashMap<String, Integer>();
	/** Zuordnung der Kantenenden. */
	private Map<String, Pair<String, String>> arcs = new HashMap<String, Pair<String, String>>();
	
	/** 
	 * Erstellt eine Petrinetz aus einer PNML-Datei. 
	 * @param file Die PNML-Datei.
	 */
	public PnmlParser(final File pnml) {
		super(pnml);
		initParser();
		parse();
		
		// Die Methoden, die die Elemente der PNML handeln sind so implementiert,
		// dass alle Elemente zunächste gesammelt werden, um dann geordnet dem Petrinetz
		// zugeordnet werden.		
		// Auch führen hier bestimmte Fehler immer zum Exit, da man nicht weiter mit dem
		// Petrinetz arbeiten kann, wenn es diese Annahmen nicht erfüllt sind.
		try {
			petriNet = new PetriNet(places, transitions, names, positions, tokens, arcs);
		} catch (NodeNotFoundException e) {
			System.out.println("In der PNML wurde eine Eigenschaft einem Knoten zugeordnet, " +
					"der nicht im Petrinetz existiert. " + e.getMessage());
		} catch (IllegalArcException e) {
			System.out.println("Nicht zulässige Zuordnung in den Kanten: " + e.getMessage());
		}
	}
	
	/**
	 * Gibt das verarbeitete Petrinetz aus. 
	 * @return Das Petrinetz.
	 */
	public PetriNet getPetriNet() {
		return petriNet;		
	}
	
	@Override
	public void newTransition(final String id) {
		transitions.add(new Transition(id));
	}
	
	@Override
	public void newPlace(final String id) {
		places.add(new Place(id));
	}
	
	@Override
	public void setPosition(final String id, final String x, final String y) {
		int parsedX = Integer.parseInt(x);
		int parsedY = Integer.parseInt(y);
		positions.put(id, new Position(parsedX, parsedY));
	}

	@Override
	public void setName(final String id, final String name) {
		names.put(id, name);
	}

	@Override
	public void setTokens(final String id, final String tokens) {
		int parsedNumTokens = Integer.parseInt(tokens);
		this.tokens.put(id, parsedNumTokens);
	}
	
	@Override
	public void newArc(final String id, final String source, final String target) {
		Pair<String, String> assignment = new Pair<String, String>(source, target);
		arcs.put(id, assignment);
	}
}
