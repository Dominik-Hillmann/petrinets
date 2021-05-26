package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Eine gerichtete Kante zwischen zwei Markierungen.
 */
public class MarkingToMarkingArc extends Arc {
	/** Die Menge an bereits genutzten IDs, wird gebraucht, um neue zu erzeugen. */
	private static Set<String> usedIds = new HashSet<String>();
	/** ID der {@link Transition}, durch deren Feuern diese Kante entstand. */
	private final String sourceTransitionId;
	/** Der Name der {@link Transition}, durch deren Feuern diese Kante entstand. */
	private final String sourceTransitionName;
	
	/**
	 * @param from Startmarkierung dieser Kante.
	 * @param to Endmarkierung dieser Kante.
	 * @param sourceTransitionId ID der {@link Transition}, durch deren Feuern diese Kante entstand.
	 * @param sourceTransitionName Der Name der {@link Transition}, durch deren Feuern diese Kante entstand.
	 */
	public MarkingToMarkingArc(Marking from, Marking to, String sourceTransitionId, String sourceTransitionName) {
		super(getNewId(), from, to);
		this.sourceTransitionId = sourceTransitionId;
		this.sourceTransitionName = sourceTransitionName;
	}
	
	@Override
	public Marking getFrom() {
		return (Marking) from;
	}
	
	@Override
	public Marking getTo() {
		return (Marking) to;
	}
	
	/**
	 * Gibt eine neue, einzigartige ID für eine neue Kante aus.
	 * @return Die ID einer neuen Kante.
	 */
	private static String getNewId() {
		int idNum = 1;
		String proposedId = "ma1";
		while (usedIds.contains(proposedId)) {
			idNum++;
			proposedId = "ma" + String.valueOf(idNum);
		}
		
		usedIds.add(proposedId);
		return proposedId;
	}
	
	/**
	 * Gibt die ID der {@link Transition} aus, durch deren Feuern diese Kante entstand.
	 * @return Die ID.
	 */
	public String getSourceTransitionId() {
		return sourceTransitionId;
	}
	
	/**
	 * Gibt den Namen der {@link Transition} aus, durch deren Feuern diese Kante entstand.
	 * @return Der Name.
	 */
	public String getSourceTransitionName() {
		return sourceTransitionName;
	}
	
	/**
	 * Gleicht ab, ob diese Kante den übergebenen Anfangs- und Endknoten hat.
	 * @param from Anfangsknoten zum Vergleich.
	 * @param to Endknoten zum Vergleich.
	 * @return Ob Anfangs- und Endmarkierung übereinstimmen.
	 */
	public boolean hasMarkings(Marking from, Marking to) {
		Marking thisFrom = (Marking) from;
		Marking thisTo = (Marking) to;
		
		return thisFrom.equals(from) && thisTo.equals(to);
	}

}
