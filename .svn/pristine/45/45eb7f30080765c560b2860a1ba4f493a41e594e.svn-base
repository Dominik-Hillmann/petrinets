package controller;

import org.graphstream.ui.view.ViewerListener;

import model.NodeNotFoundException;

/**
 * Dieser Listener hört auf Klicks im rechten Erreichbarkeitsgraphen-Segment.
 */
public class ClickListenerRightGraph implements ViewerListener {
	/** Der {@link Controller}, von dem die Klicks gehandelt werden. */
	private Controller controller;
	
	/**
	 * Konstruktor mit dem {@link Controller}, der die Klicks handelt.
	 * @param controller Der {@link Controller}, der die Klicks handelt.
	 */
	public ClickListenerRightGraph(Controller controller) {
		System.out.println("rechter Graph: Listener initialisiert");
		this.controller = controller;
	}
	
	/**
	 * Wenn das Graphenfenster geschlossen wird.
	 */
	@Override
	public void viewClosed(String viewName) { }

	/**
	 * Handelt den Klick auf einen Knoten.
	 * @param id Die ID des Knotens, auf den geklickt worden ist.
	 */
	@Override
	public void buttonPushed(String id) {
		System.out.println(id);
		try {
			controller.clickNodeInReachGraph(id);
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Wenn der Klick auf den Knoten wieder losgelassen wird.
	 * @param id Die ID des losgelassenen geklickten Knotens.
	 */
	@Override
	public void buttonReleased(String id) { }

}
