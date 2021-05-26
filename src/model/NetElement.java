package model;

/** 
 * Repräsentiert ein beliebiges Element, das in einem Petrinetz oder dem ER
 * vorkommen kann.
 */
public class NetElement {
	/** Die in der PNML enthaltene ID jedes Elements (Arcs, Places, Transitions). */
	protected String id;
	
	/**
	 * Erstellt ein Element eines Netzes mit einer einzigartigen ID.
	 * @param id Die ID.
	 */
	protected NetElement(String id) {
		this.id = id;
	}
	
	/**
	 * Gibt die ID des des Elements aus.
	 * @return Die ID.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Gibt aus, ob der Knoten diese ID besitzt.
	 * @param compareId Die ID, mit der die ID dieses Elements verglichen wird.
	 * @return Ob sie gleich sind.
	 */
	public boolean hasId(String compareId) {
		return compareId.equals(id);
	}
	
	
	/** 
	 * Überprüft Gleichheit von Petrinetzelementen über die ID.
	 */
	@Override
	public boolean equals(Object obj) {
		NetElement other;
		try {
			other = (NetElement) obj;
		} catch (Exception e) {
			return false;
		}
		
		return id.equals(other.getId());
	}
}
