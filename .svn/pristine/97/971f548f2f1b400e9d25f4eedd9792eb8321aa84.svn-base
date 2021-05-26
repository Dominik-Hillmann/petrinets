package model;

/**
 * Ich brauchte eine Klasse, die mir dabei hilft, zwei Elemente einander
 * zuzuordnen.
 * @param <ClassOne> Die Klasse des ersten Elements.
 * @param <ClassTwo> Die Klasse des zweiten Elements.
 */
public class Pair<ClassOne, ClassTwo> {
	/** Erstes Element. */
	private final ClassOne elementOne;
	/** Zweites Element. */
	private final ClassTwo elementTwo;
	
	/**
	 * Instanziert eine neue, einzelne Zuordnung.
	 * @param elementOne Erstes Element.
	 * @param elementTwo Zweites Element.
	 */
	public Pair(ClassOne elementOne, ClassTwo elementTwo) {
		this.elementOne = elementOne;
		this.elementTwo = elementTwo;
	}	
	
	/**
	 * Gibt das erste Element aus.
	 * @return Das Element.
	 */
	public ClassOne getFirst() {
		return elementOne;
	}
	
	/**
	 * Gibt das zweite Element aus.
	 * @return Das zweite Element.
	 */
	public ClassTwo getSecond() {
		return elementTwo;
	}

	@Override 
	public String toString() {
		return "(" + elementOne.toString() + ", " + elementTwo.toString() + ")";
	}
}
