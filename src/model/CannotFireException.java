package model;

/**
 * Eine Exception, die geworfen wird, wenn ein Petrinetz eine {@link Transition} 
 * nicht feuern kann.
 */
public class CannotFireException extends Exception {
	/** Von Eclipse generierte ID. */
	private static final long serialVersionUID = 7538543532330877578L;
	
	/**
	 * Konstruktor, der genutzt wird, wenn bekannt ist, welche Stelle ({@link Place})
	 * keine Marken mehr besitzt.
	 * @param transition Die Transition, die nicht feuern kann.
	 * @param origin Die Stelle, die keine Marken mehr besitzt.
	 */
	public CannotFireException(Transition transition, Place origin) {
		super(
			transition.toString() + " kann nicht feuern, " +
			"weil " + origin.toString() + " keine Marke besitzt."
		);
	}
	
	/**
	 * Konstruktor für den Fall, dass die Stelle, die keine Marken mehr besitzt,
	 * nicht bekannt ist.
	 * @param reason Der Grund dafür, dass nicht gefeuert werden kann.
	 */
	public CannotFireException(String reason) {
		super(reason);
	}
}
