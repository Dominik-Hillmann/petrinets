package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Nimmt eine Menge von {@link File}s entgegen und analysiert sie.
 * Dann gibt es die Möglichkeit, die Ergebnisse der Verarbeitung formatiert 
 * ausgeben zu lassen.
 */
public class BatchAnalyzer {
	/** Name der Dateinamen-Spalpte. */
	private static final String[] FILE_NAME_HEADING = {"", "Dateiname "};
	/** Name der Beschränktheitsspalte. */
	private static final String[] IS_RESTRICTED_HEADING = {"", " beschränkt "};
	/** Name der letzten Spalte. */
	private static final String[] PATH_LENGTH_HEADING = {" Knoten / Kanten bzw.", " Pfadlänge:Pfad; m, m'"};
	/** Anzeige, wenn ER beschränkt ist. */
	private static final String IS_LIMITED_STR = " ja";
	/** Anzeige, wenn ER nicht beschränkt ist. */
	private static final String IS_NOT_LIMITED_STR = " nein";	
	/** Zahl der überprüften PNML-Dateien. */
	private final int NUM_FILES;
	/** Die Erreichbarkeitsgraphen. */
	private List<ReachabilityNet> reachNets = new ArrayList<ReachabilityNet>();
	/** Die Petrinetze überprüfter Dateien. */
	private List<PetriNet> petriNets = new ArrayList<PetriNet>();
	/** Namen der überorüften Dateien. */
	private List<String> fileNames = new ArrayList<String>();
	/** Ob der ER der Datei beschränkt ist. */
	private List<Boolean> isLimited = new ArrayList<Boolean>();
	/** Kritische Pfade, wenn die ERs unbeschränkt sind. */
	private List<List<String>> criterionPaths = new ArrayList<List<String>>();
	/** Stringrepräsentationen der kritischen Pfade. */
	private List<String> criterionPathStrings = new ArrayList<String>();
	/** Die Startknoten der kritischen Pfade. */
	private List<Marking> ms = new ArrayList<Marking>();
	/** Die Endknoten der kritischen Pfade. */
	private List<Marking> mStrokes = new ArrayList<Marking>();
	
	/**
	 * Konstruktor, der die {@link File}s analysiert.
	 * @param pnmls Die Dateien, die analysiert werden.
	 */
	public BatchAnalyzer(File[] pnmls) {
		NUM_FILES = pnmls.length;
		List<File> sortedPnmls = orderAlphabetically(Arrays.asList(pnmls));
		for (File pnml : sortedPnmls) {
			fileNames.add(pnml.getName() + " ");			
			PetriNet petriNet = new PnmlParser(pnml).getPetriNet();
			petriNets.add(petriNet);
			ReachabilityNet reachNet = new ReachabilityNet(petriNet);
			reachNets.add(reachNet);			
		}
		
		analyzeNets();
	}
	
	/**
	 * Ordnet die Eingangsdateien alphabetisch.
	 * @param files Die Dateien, die geordnet werden sollen.
	 * @return Die geordnete Liste.
	 */
	private static List<File> orderAlphabetically(List<File> files) {
		Collections.sort(files, (fileA, fileB) -> {
			String nameA = fileA.getName();
			String nameB = fileB.getName();
			return nameA.compareTo(nameB);
		});
		return files;
	}	
	
	/**
	 * Analysiert die Eingangsdateien.
	 */
	private void analyzeNets() {
		for (int i  = 0; i < NUM_FILES; i++) {
			PetriNet petriNet = petriNets.get(i);
			ReachabilityNet reachNet = reachNets.get(i);
			
			try {
				reachNet.analyze(petriNet);
				// Graph garantiert beschraenkt ab hier.
				isLimited.add(true);
				ms.add(null);
				mStrokes.add(null);
				criterionPaths.add(null);
				
			} catch (GraphIsUnlimitedException e) {
				// Graph ist garantiert unbeschraenkt ab hier.
				isLimited.add(false);
				ms.add(e.getStart());
				mStrokes.add(e.getEnd());
				criterionPaths.add(e.getFormattedTransitionPath());
				criterionPathStrings.add(pathToStr(e.getFormattedTransitionPath()));
			}
		}
	}
	
	/**
	 * Gibt die Zusammenfassung als String aus, die später im Textfeld
	 * erscheinen wird.
	 * @return Die Zusammenfassung.
	 */
	public String summary() {
		// Laengen Dateinamen
		List<String> fileNameColItems = new ArrayList<String>();
		fileNameColItems.add(FILE_NAME_HEADING[1]);
		fileNameColItems.addAll(fileNames);
		int widthFileNameCol = getLongestStringLen(fileNameColItems);
		// Laengen der "beschraenkt"-Spalte
		String[] limitColNames = {IS_LIMITED_STR, IS_NOT_LIMITED_STR, IS_RESTRICTED_HEADING[1]};
		int widthIsLimitedCol = getLongestStringLen(limitColNames);
		// Laengen der Pfade
		int widthNodePathCol = getLongestStringLen(criterionPathStrings);
		// m-Laengen
		List<String> mGuiNames = new ArrayList<String>();
		ms.forEach(marking -> {
			try {
				mGuiNames.add(marking.guiName() + ", ");				
			} catch (Exception e) { }
		});
		int widthMCol = getLongestStringLen(mGuiNames);		
		// m'-Laengen
		List<String> mStrokeGuiNames = new ArrayList<String>();
		mStrokes.forEach(marking -> {
			try {
				mStrokeGuiNames.add(marking.guiName());
			} catch (Exception e) { }
		});
		int widthMStrokeCol = getLongestStringLen(mStrokeGuiNames);
				
		String summary = getHeader(widthFileNameCol, widthIsLimitedCol, widthNodePathCol, widthMCol, widthMStrokeCol);
		// Anfuegen der einzelnen Zeilen
		for (int i = 0; i < NUM_FILES; i++) {
			String line = "";
			String fileName = fileNames.get(i);
			String limited = isLimited.get(i) ? IS_LIMITED_STR : IS_NOT_LIMITED_STR;
			
			String lastCol;
			if (isLimited.get(i)) {
				ReachabilityNet reachNet = reachNets.get(i);
				int numMarkings = reachNet.getMarkings().size();
				
				String prePad = numMarkings < 10 ? "  " : " ";
				lastCol = prePad + String.valueOf(reachNet.getMarkings().size()) + 
						" / " + String.valueOf(reachNet.getNumUniqueArcs());
			} else {
				List<String> path = criterionPaths.get(i);
				Marking m = ms.get(i);
				Marking mStroke = mStrokes.get(i);
				
				String pathStr = pathToStr(path);
				
				lastCol = pad(pathStr, widthNodePathCol) + 
						pad(m.guiName() + ", ", widthMCol) +
						pad(mStroke.guiName(), widthMStrokeCol);
			}
			
			line += pad(fileName, widthFileNameCol) + 
					"|" + pad(limited, widthIsLimitedCol) +
					"|" + lastCol;
			summary += line + "\n";
		}
		
		return summary;
	}
	
	/**
	 * Gibt die Kopfzeile der Tabelle aus, die die Zusammenfassung bildet.
	 * @param widthFileNameCol Breite der Spalte, in der Dateiennamen gezeigt werden.
	 * @param widthIsLimitedCol Breite der Spalte, die zeigt, ob das ER beschränkt ist.
	 * @param widthNodePathCol Breite der letzten Spalte in Zeichen.
	 * @param widthMCol Breite der Unterspalte, in der die Anfangsknoten des kritischen Pfades
	 * bei unbeschränktem ER ausgegeben werden.
	 * @param widthMStrokeCol Breite der Unterspalte, in der die Endknoten des kritischen Pfades
	 * bei unbeschränktem ER ausgegeben werden.
	 * @return Die Kopfzeile.
	 */
	private String getHeader(int widthFileNameCol, int widthIsLimitedCol, int widthNodePathCol, int widthMCol, int widthMStrokeCol) {
		String headerLine0 = rep(" ", widthFileNameCol) + 
				"|" + rep(" ", widthIsLimitedCol) + 
				"|" + PATH_LENGTH_HEADING[0] + 
				"\n";
		String headerLine1 = pad(FILE_NAME_HEADING[1], widthFileNameCol) +
				"|" + pad(IS_RESTRICTED_HEADING[1], widthIsLimitedCol) +
				"|" + PATH_LENGTH_HEADING[1] +
				"\n";
		String headerPartitionLine = rep("-", widthFileNameCol) +
				"|" + rep("-", widthIsLimitedCol) +
				"|" + rep("-", Math.max(widthNodePathCol + widthMCol + widthMStrokeCol, PATH_LENGTH_HEADING[1].length())) +
				"\n";
		return headerLine0 + headerLine1 + headerPartitionLine;
	}
	
	/**
	 * Setzt so lange Leerzeichen vor den {@link String}, bis dieser eine gewisse
	 * Länge hat.
	 * @param s Der zu verändernde String.
	 * @param numPads Die Länge des Zielstrings.
	 * @return Der bearbeitete String.
	 */
	private static String pad(String s, int numPads) {
		return String.format("%1$-" + String.valueOf(numPads) + "s", s);
	}
	
	/**
	 * Wiederholt einen {@link String}.
	 * @param s Der String, der wiederholt wird.
	 * @param times Die Zahl der Wiederholungen.
	 * @return Der wiederholte String.
	 */
	private static String rep(String s, int times) {
		String collector = "";
		for (int i = 0; i < times; i++) {
			collector += s;
		}
		
		return collector;
	}
	
	/**
	 * Wandelt einen kritschen Pfad in einen {@link String} um.
	 * @param path Der kritische Pfad.
	 * @return Die Stringrepräsentation.
	 */
	private static String pathToStr(List<String> path) {
		int size = path.size();
		String preSize = size < 10 ? "  " : " ";
		
		String pathStr = preSize + String.valueOf(size) + ":(";
		for (String transitionName : path) {
			pathStr += transitionName + ",";
		}
		pathStr = pathStr.substring(0, pathStr.length() - 1) + "); ";
		
		return pathStr;
	}
	
	/**
	 * Ermittelt die Länge des längsten {@link String}s.
	 * @param strings Die Strings, deren maximale Länge ermittelt wird.
	 * @return Länge des längsten Strings.
	 */
	private static int getLongestStringLen(List<String> strings) {
		int longest = 0;
		for (String string : strings) {
			if (string.length() > longest)
				longest = string.length();
		}
		
		return longest;
	}

	/**
	 * Ermittelt die Länge des längsten {@link String}s.
	 * @param strings Die Strings, deren maximale Länge ermittelt wird.
	 * @return Länge des längsten Strings.
	 */
	private static int getLongestStringLen(String[] strings) {
		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < strings.length; i++) {
			stringList.add(strings[i]);
		}
		
		return getLongestStringLen(stringList);
	}
}
