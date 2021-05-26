package view;

import org.graphstream.graph.implementations.MultiGraph;

/**
 * Basisklasse für alle visuellen Gegenstücke zu den Modell-Graphen.
 */
public abstract class VisualGraph extends MultiGraph {
	/** Lädt das CSS des Graphen. */
	protected static String CSS_FILE = "url(" + VisualGraph.class.getResource("/graph.css") + ")";
	/** Key, um das Label zu verändern. */
	protected final static String LABEL_ATTRIBUTE = "ui.label";
	/** Key, um die Klasse zu verändern. */
	protected final static String CLASS_ATTRIBUTE = "ui.class";
	/** Key, um die Position zu verändern. */
	protected final static String POSITION_ATTRIBUTE = "xy";
	
	/**
	 * Setzt die ID des Graphen.
	 */
	public VisualGraph(String id) {
		super(id);
	}
	
}
