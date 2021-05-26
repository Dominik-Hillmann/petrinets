package controller;

import org.graphstream.ui.view.ViewerListener;

import model.NodeNotFoundException;

/**
 * Dieser Listener hört auf Klicks im linken Petrigraphen-Segment.
 */
public class ClickListenerLeftGraph implements ViewerListener {

	/** Controller der Anwendung */
	private Controller controller;

	/**
	 * Der Konstruktor, der die Klicks an den {@link Controller} weitergibt.
	 * @param controller Der Controller, an den die Klicks weitergegeben werden.
	 */
	public ClickListenerLeftGraph(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Wenn der View geschlossen wird.
	 */
	@Override
	public void viewClosed(String viewName) {
		System.out.println("ClickListener - viewClosed: " + viewName);
	}
	
	/**
	 * Gibt die Klicks im Grafen an den {@literal Controller} weiter.
	 */
	@Override
	public void buttonPushed(String id) {
		if (controller == null) {
			return;
		}
		
		try {
			controller.clickNodeInGraph(id);
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wird ausgelöst, wenn der Klick wieder losgelassen wird.
	 */
	@Override
	public void buttonReleased(String id) {
		System.out.println("Button release triggered");
	}
}