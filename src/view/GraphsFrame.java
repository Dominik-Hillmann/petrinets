package view;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import controller.ClickListenerLeftGraph;
import controller.ClickListenerRightGraph;
import controller.Controller;
import model.BatchAnalyzer;
import model.PetriNet;
import model.PnmlParser;
import model.ReachabilityNet;

/**
 * Das Hauptfenster der Anwendung.
 */
public class GraphsFrame extends JFrame {	
	/** Von Ecliplse generierte ID. */
	private static final long serialVersionUID = -7853267457644325182L;
	/** Der Controller, der alle Klicks in den Graphen handelt. */
	private Controller controller;
	/** Spaltung oben und unten im Fenster. */
	private JSplitPane splitPane;
	/** Startgröße des Fensters. */
	private static Dimension preferredSize = new Dimension(1000, 700);
	/** Das Panel, in dem die beiden Graphen angezeigt werden. */
	private JPanel topPanel;
	/** Die Teilung des oberen Panels für Petrinetz und ER. */
	private JSplitPane graphicsSplit;
	/** Panel des Petrinetzes. */
	private ViewPanel leftGraph;
	/** Panel des ER. */
	private ViewPanel rightGraph;
	/** Temporärer Knopf in den Netzpanels (links). */
	private JButton tempOpenBtn1;
	/** Temporärer Knopf in den Netzpanels (rechts). */
	private JButton tempOpenBtn2;
	/** Untere Hälfte des Fensters. */
	private JPanel bottomPanel;
	/** Macht den Text scrollbar. */
	private JScrollPane scrollPane;
	/** Anzeige aller Informationen. */
	private JTextArea textArea;
	/** Enthälte alle Buttons der Toolbar. */
	private JPanel toolbar;
	/** Button, der die Erreichbarkeitsanalyse anstoesst. */
	private JButton analyseBtn;
	/** Setzt Erreichbarkeitsgraphen zurück. */
	private JButton deleteBtn;
	/** Fügt markiertem Knoten einen Token hinzu. */
	private JButton addBtn;
	/** Zieht markiertem Knoten einen Token ab. */
	private JButton subBtn;
	/** Leert den Textbereich. */
	private JButton emptyTextBtn;
	/** Setzt Erreichbarkeitsgraph auf aktuelle Wurzel zurück. */
	private JButton resetBtn;
	
	/** Der Ordner, in dem die Standardbeispiele zu finden sind. */
	private final static String RELATIVE_START_DIR = "../ProPra-WS20-Basis/Beispiele";
	/** Der Filter für die PNML-Dateien. */
	private static FileNameExtensionFilter pnmlFilter = new FileNameExtensionFilter("Nur PNML-Dateien", "pnml");
	/** Der Pfad zum Ordner der zuletzt geoeffneten Datei. */
	private String lastDir = null;
	/** Die gewaehlte PNML-Datei. */
	private File chosenFile = null;
	
	/**
	 * Erstellt das Hauptfenster der Anwendung.
	 */
	public GraphsFrame() {
		super("Dominik Hillmann, Matrikelnr. 6764860");
		System.setProperty(
			"org.graphstream.ui.renderer", 
			"org.graphstream.ui.j2dviewer.J2DGraphRenderer"
		);
		splitPane = new JSplitPane(); 
		splitPane.setResizeWeight(0.5);

		topPanel = new JPanel();
		graphicsSplit = new JSplitPane(); 
		graphicsSplit.setResizeWeight(0.5);
		
		String openMsg = "Hier klicken, um eine Datei zu öffnen.";
		tempOpenBtn1 = new JButton(openMsg);
		tempOpenBtn2 = new JButton(openMsg);
		tempOpenBtn1.addActionListener(openPnml);
		tempOpenBtn2.addActionListener(openPnml);
		graphicsSplit.setTopComponent(tempOpenBtn1);
		graphicsSplit.setBottomComponent(tempOpenBtn2);

		bottomPanel = new JPanel();
		scrollPane = new JScrollPane();
		textArea = new JTextArea();
		
		// Standardgröße und Standardlayout.
		setPreferredSize(preferredSize);
		getContentPane().setLayout(new GridLayout());
		getContentPane().add(splitPane);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Einstellungen an der obersten SplitPane.
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(bottomPanel);

		// Einstellungen oberes Panel.
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(graphicsSplit);
		graphicsSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		// Einstellungen unteres Panel.
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.add(scrollPane);
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        textArea.setToolTipText("Zeigt Textinformationen zu aktuellen Ereignissen in den Graphen an.");
        for (int i = 0; i < 10; i++) {
        	addText("");
        }
		
		createToolbar();
		createMenuBar();

		pack();
		this.setLocationRelativeTo(null); // Öffnet Fenster mittig im Bildschirm.
		setVisible(true);
		setDividersHalf();
	}
	
	/**
	 * Initialisiert den Graph des Petrinetzes.
	 */
	private void initPetriGraph() {
		Viewer viewer = new Viewer(controller.getPetriGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.disableAutoLayout();
		leftGraph = viewer.addDefaultView(false);
		leftGraph.setToolTipText("Das Petrinetz.");
		
		ViewerPipe viewerPipe = viewer.newViewerPipe();
		ClickListenerLeftGraph clickListener = new ClickListenerLeftGraph(controller);
		viewerPipe.addViewerListener(clickListener);

		leftGraph.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();
			}
		});
	}
	
	/**
	 * Setzt das Petrinetz neu ein, um es grafisch anzuzeigen.
	 * @param petriNet Das Petrinetz, das angezeigt werden soll.
	 * @return Der zum Petrinetz gehörige PetriGraph.
	 */
	public VisualPetriGraph setPetriNet(PetriNet petriNet) {
		VisualPetriGraph petriGraph = new VisualPetriGraph(petriNet);
		Viewer viewer = new Viewer(petriGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.disableAutoLayout();
		leftGraph = viewer.addDefaultView(false);
		leftGraph.setToolTipText("Das Petrinetz.");
		graphicsSplit.setLeftComponent(leftGraph);
		
		ViewerPipe viewerPipe = viewer.newViewerPipe();
		ClickListenerLeftGraph clickListener = new ClickListenerLeftGraph(controller);
		viewerPipe.addViewerListener(clickListener);

		leftGraph.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();
			}
		});
		
		return petriGraph;
	}
	
	/**
	 * Initialisiert den Erreichbarkeitsgraphen.
	 */
	private void initReachGraph() {
		Viewer viewer = new Viewer(controller.getReachGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		rightGraph = viewer.addDefaultView(false);
		rightGraph.setToolTipText("Der Erreichbarkeitsgraph (ER).");
		
		ViewerPipe viewerPipe = viewer.newViewerPipe();
		ClickListenerRightGraph clickListener = new ClickListenerRightGraph(controller);
		viewerPipe.addViewerListener(clickListener);
		
		rightGraph.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				System.out.println("clicked");
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();
			}
		});
	}

	/**
	 * Initialisiert den Controller, indem dem Controller die Graphen gegeben werden,
	 * auf denen er operiert.
	 * @param petriNet Das Petrinetz, auf dem der Controller operiert.
	 * @pddaram reachNet Der Erreichbarkeitsgraph, auf dem der Controller operiert.
	 */
	private void initController(PetriNet petriNet, ReachabilityNet reachNet) {
		controller = new Controller(this, petriNet, reachNet);
		initPetriGraph();
		initReachGraph();		
		// In der oberen Teilung die Graphen anzeigen lassen.
		graphicsSplit.setLeftComponent(leftGraph);
		graphicsSplit.setRightComponent(rightGraph);
	}
	
	/**
	 * Fügt dem Textbereich eine neue Zeile hinzu. 
	 * @param text Der hinzugefügte Text.
	 */
	public void addText(String text) {
		textArea.append(text + "\n");
	}
	
	/**
	 * Initialisiert die untere Toolbar.
	 */
	private void createToolbar() {
		toolbar = new JPanel();		
		// Analyse
		analyseBtn = new JButton("Analysiere");
		analyseBtn.addActionListener(analyze);
		analyseBtn.setToolTipText("Analysiert das aktuelle Petrinetz und zeigt es in der rechten Fläche an.");
		toolbar.add(analyseBtn);		
		// Lösche EG
		deleteBtn = new JButton("Lösche EG");
		deleteBtn.addActionListener(resetGraphs);
		deleteBtn.setToolTipText("Setzt das Petrinetz und den ER auf ihren Startzustand zurück.");
		toolbar.add(deleteBtn);		
		// Add Button		
		addBtn = new JButton("+");
		addBtn.addActionListener(event -> {
			if (chosenFile == null) {
				return;
			}
			controller.markedPlusOne();
		});
		addBtn.setToolTipText("Fügt der aktuell markierten Stelle eine Marke hinzu.");
		toolbar.add(addBtn);		
		// Minus Button		
		subBtn = new JButton("-");
		subBtn.addActionListener(event -> {
			if (chosenFile == null) {
				return;
			}
			controller.markedMinusOne();
		});
		subBtn.setToolTipText("Zieht der aktuell markierten Stelle eine Marke ab.");
		toolbar.add(subBtn);		
		// Textbereich leeren
		emptyTextBtn = new JButton("Textbereich leeren");
		emptyTextBtn.addActionListener(resetTextArea);
		emptyTextBtn.setToolTipText("Leert den Textbereich.");
		toolbar.add(emptyTextBtn);		
		// Auf aktuelle Markierung zurueksetzen
		resetBtn = new JButton("Reset");
		resetBtn.addActionListener(resetPetriGraphToCurrentReachabilityRoot);
		resetBtn.setToolTipText("Setzt EG auf den Wurzelknoten zurück.");
		toolbar.add(resetBtn);
		
		bottomPanel.add(toolbar);
	}
	
	/**
	 * Initialisiert die obere Menübar.
	 */
	private void createMenuBar() {
		// Menübar und Unterpunkt "Datei".
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Datei");
		fileMenu.setToolTipText("Optionen bezogen auf das Programm oder geöffnete Dateien.");
		
		// Unterpunkt, der neue PNML öffnet
		JMenuItem open = new JMenuItem("Öffnen");
		open.addActionListener(openPnml);
		open.setToolTipText("Öffnet eine neue PNML-Datei im oberen linken Fenster.");
		fileMenu.add(open);
		
		// Unterpunkt für Verarbeitung vieler Dateien.
		JMenuItem openMany = new JMenuItem("Analyse mehrer Dateien");
		openMany.addActionListener(openManyPnmls);
		openMany.setToolTipText("Analysiert eine oder mehrere ERs der Petrinetze aus PNML-Dateien auf Beschränktheit und gibt das Ergebnis im Textbereich aus.");
		fileMenu.add(openMany);
		
		JMenuItem reOpen = new JMenuItem("Neu laden");
		reOpen.addActionListener(reloadGraphs);
		reOpen.setToolTipText("Lädt die PNML-Datei erneut.");
		fileMenu.add(reOpen);
		
		JMenuItem exit = new JMenuItem("Beenden");
		exit.addActionListener(event -> System.exit(0));
		exit.setToolTipText("Beendet das Programm.");
		fileMenu.add(exit);

		menuBar.add(fileMenu);
		
		JMenu displayMenu = new JMenu("Anzeige");
		displayMenu.setToolTipText("Optionen bezogen auf die grafische Darstellung.");
		
		JMenuItem to5050Partition = new JMenuItem("Gleichmäßige Aufteilung");
		to5050Partition.addActionListener(event -> setDividersHalf());
		to5050Partition.setToolTipText("Erzeugt eine gleichflächige Aufteilung.");
		displayMenu.add(to5050Partition);
		
		JMenuItem focusPetriGraphItem = new JMenuItem("Fokussiere Petrinetz");
		focusPetriGraphItem.addActionListener(event -> focusPetriGraph());
		focusPetriGraphItem.setToolTipText("Fokussiert auf das Petrinetz.");
		displayMenu.add(focusPetriGraphItem);
		
		JMenuItem focusReachGraphItem = new JMenuItem("Fokussiere ER");
		focusReachGraphItem.addActionListener(event -> focusReachGraph());
		focusReachGraphItem.setToolTipText("Fokussiert auf den ER.");
		displayMenu.add(focusReachGraphItem);
		
		JMenuItem focusTextItem = new JMenuItem("Fokussiere Textfeld");
		focusTextItem.addActionListener(event -> focusText());
		focusTextItem.setToolTipText("Fokussiert das Textfeld.");
		displayMenu.add(focusTextItem);
		
		JMenuItem currentFileName = new JMenuItem("Zeige Dateinamen");
		currentFileName.addActionListener(showCurrentFileName);
		currentFileName.setToolTipText("Gibt den Namen der aktuell geladenen PNML-Datei im Textfeld aus.");
		displayMenu.add(currentFileName);	
				
		menuBar.add(displayMenu);
				
		setJMenuBar(menuBar);
	}

	/**
	 * Listener, der die Analyse des aktuellen Erreichbarkeitsgraphen auslöst.
	 */
	private final ActionListener analyze = event -> {
		// Kann nicht ausgeführt werden, wenn keine
		if (chosenFile == null) {
			return;
		}
		
		controller.resetReachToCurrentRoot();
		boolean isLimited = controller.analyze();
		String answer = "Das Petrinetz ist " + (isLimited ? "beschränkt" : "unbeschränkt") + ".";
		JOptionPane.showMessageDialog(this, answer, "Beschränktheitsanalyse des Petrinetzes", JOptionPane.PLAIN_MESSAGE);
		addText("Beschränktheitsanalyse: " + answer);
	};
	
	/**
	 * Listener, der den Reset auslöst.
	 */
	private final ActionListener resetPetriGraphToCurrentReachabilityRoot = event -> {
		if (chosenFile == null) {
			return;
		}
		controller.resetPetriGraphToCurrentReachabilityRoot();
	};
	
	/**
	 * Listener, der den Textbereich leert.
	 */
	private final ActionListener resetTextArea = event -> {
		textArea.setText(null);
	};
	
	/**
	 * Listener, der den aktuellen Namen der geladenen Datei anzeigt.
	 */
	private final ActionListener showCurrentFileName = event -> {
		try {
			String fileName = chosenFile.getName();
			addText("Der Name der aktuellen Datei ist " + fileName + ".");
		} catch (NullPointerException e) {
			addText("Es wurde noch keine Datei geladen. Es kann kein Name angezeigt werden.");
		}
	};
	
	
	/**
	 * Listener, der die beiden Graphen zurücksetzt.
	 */
	private final ActionListener resetGraphs = event -> {
		// Kann nicht durchgeführt werden, wenn kein Petrinetz geladen ist.
		if (chosenFile == null) {
			return;
		}
		
		addText("Setze den Erreichbarkeitsgraphen auf seinen Anfangszustand zurück und das Petrinetz auf dessen Anfangsmarkierung.");
		controller.resetPetriGraphToCurrentReachabilityRoot();
		controller.resetReachToCurrentRoot();
	};
	
	/**
	 * Lädt beide Graphen komplett neu aus der Datei.
	 */
	private final ActionListener reloadGraphs = event -> {
		if (chosenFile == null) {
			return;
		}
		
		PetriNet parsedPetriNet = new PnmlParser(chosenFile).getPetriNet();
		ReachabilityNet reachNet = new ReachabilityNet(parsedPetriNet);
		initController(parsedPetriNet, reachNet);
	};

	/**
	 * Listener, der mehrere PNML-Dateien in die Batchverarbeitung schickt und
	 * wieder im Textbereich ausgibt.
	 */
	private final ActionListener openManyPnmls = event -> {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(pnmlFilter);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setCurrentDirectory(new File(lastDir == null ? RELATIVE_START_DIR : lastDir));
					
		int choice = fileChooser.showOpenDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			BatchAnalyzer analyzer = new BatchAnalyzer(fileChooser.getSelectedFiles());
			addText(analyzer.summary());
		}

		lastDir = fileChooser.getCurrentDirectory().getAbsolutePath().toString();
	};	

	/**
	 * Listener, der eine einzelne PNML-Datei öffnet und die Graphen anzeigt.
	 */
	private ActionListener openPnml = event -> {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(pnmlFilter);
		fileChooser.setCurrentDirectory(new File(lastDir == null ? RELATIVE_START_DIR : lastDir));
					
		int choice = fileChooser.showOpenDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			chosenFile = fileChooser.getSelectedFile();
			// Petrinetz einbetten
			PetriNet parsedPetriNet = new PnmlParser(chosenFile).getPetriNet();
			ReachabilityNet reachNet = new ReachabilityNet(parsedPetriNet);
			initController(parsedPetriNet, reachNet);
			addText("Lade " + fileChooser.getSelectedFile().getName() + ".");
		} else {
			addText("Diese Datei kann nicht verarbeitet werden.");
		}
		
		lastDir = fileChooser.getCurrentDirectory().getAbsolutePath().toString();
	};

	/**
	 * Richtet die Trennungen wieder auf den Anfangszustand ein.
	 */
	private void setDividersHalf() {
		Rectangle bounds = getBounds();
		int currentWidth = bounds.width;
		int halfWidth = currentWidth / 2;
		int currentHeight = bounds.height;
		int halfHeight = currentHeight / 2;
		
		graphicsSplit.setDividerLocation(halfWidth);
		splitPane.setDividerLocation(halfHeight);
	}
	
	/**
	 * Macht die Fläche des Petrinetzes am größten.
	 */
	private void focusPetriGraph() {
		Rectangle bounds = getBounds();
		int currentWidth = bounds.width;
		int wantedWidth = 2 * currentWidth / 3;
		int currentHeight = bounds.height;
		int wantedHeight = 2 * currentHeight / 3;
		
		graphicsSplit.setDividerLocation(wantedWidth);
		splitPane.setDividerLocation(wantedHeight);
	}
	
	/**
	 * Macht die Fläche des ER am größten.
	 */
	private void focusReachGraph() {
		Rectangle bounds = getBounds();
		int currentWidth = bounds.width;
		int wantedWidth = currentWidth / 3;
		int currentHeight = bounds.height;
		int wantedHeight = 2 * currentHeight / 3;
		
		graphicsSplit.setDividerLocation(wantedWidth);
		splitPane.setDividerLocation(wantedHeight);		
	}
	
	/**
	 * Macht das Textfeld am größten.
	 */
	private void focusText() {
		Rectangle bounds = getBounds();
		int currentHeight = bounds.height;
		int wantedHeight = currentHeight / 5;
		
		splitPane.setDividerLocation(wantedHeight);
	}
}
