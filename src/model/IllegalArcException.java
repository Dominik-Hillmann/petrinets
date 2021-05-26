package model;
/**
 * Wird geworfen, wenn eine Kante zwischen zwei {@link Transition}s oder zwei
 * {@link Place}s gespannt wird.
 */
public class IllegalArcException extends Exception {
	/** Von Eclipse generiert Version ID. */
	private static final long serialVersionUID = 3995673909159013956L;
	
	/**
	 * Konstruktor mit Fehlermeldung.
	 * @param errorMessage Die Fehlermeldung.
	 */
	public IllegalArcException(String errorMessage) {
		super(errorMessage);
	}
}
