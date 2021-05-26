package model;

/** 
 * Eine {@link Exception}, die anzeigt, dass ein Knoten über die ID nicht gefunden 
 * wurde. 
 */
public class NodeNotFoundException extends Exception {
	/** Von Java generierte ID. */
	private static final long serialVersionUID = -2025395310172579530L;
	
	/**
	 * Konstruktor, dem die Nachricht der ursprünglichen {@link Exception}
	 * übergeben wird.
	 * @param errorMessage Die ursprüngliche errorMessage.
	 */
	public NodeNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
