package de.uni_koeln.spinfo.textengineering.ir.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;

public class Work {

	private String text;
	private String title;
	// NEU: Jedes Werk enthält einen eigenen Term-Index
	private Map<String, Integer> tf;

	public Work(String text, String title) {
		setText(text);
		setTitle(title);
		// NEU: Beim Erstellen des Work-Objekts werden die Termfrequenzen ermittelt
		this.tf = computeTf();
	}

	/**
	 * Zugriff auf den Text
	 * 
	 * @return Der Inhalt des Dokuments als String.
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Zugriff auf den Titel
	 * 
	 * @return Der Titel des Dokuments.
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return String.format("Titel: %s", title);
	}

	/*
	 * In der IR-Logik können Termfrequenzen (tf) direkt beim Indexieren gesammelt werden. Ein Beispiel ist der
	 * PositionalIndex, tf entspricht dort einfach der Länge der Positionslisten. In unserem Beispiel gehen wir davon
	 * aus, dass diese Positionslisten nicht zur Verfügung stehen, d.h. wir müssen uns die tf-Werte vorberechnen und für
	 * bequemeren Zugriff in einer Map ablegen.
	 */
	private Map<String, Integer> computeTf() {
		Map<String, Integer> termMap = new HashMap<String, Integer>();
		/* Wir zählen die Häufigkeiten der Tokens: */
		List<String> tokens = new Preprocessor().tokenize(text);
		for (String token : tokens) {
			Integer tf = termMap.get(token);
			/*
			 * Wenn der Term noch nicht vorkam, beginnen wir zu zählen (d.h. wir setzen 1)
			 */
			if (tf == null) {
				tf = 1;
			} else {// sonst zählen wir einfach bei jedem Vorkommen hoch
				tf = tf + 1;
			}
			termMap.put(token, tf);
		}
		return termMap;
	}

	/**
	 * @return Liste der Terme in diesem Dokument, benötigt bei der Indexierung.
	 */
	public List<String> getTerms() {
		return new ArrayList<String>(tf.keySet());
	}

	/**
	 * Zugriff auf Tf-Werte (für Termgewichtung).
	 * 
	 * @param t
	 * @return Die Termfrequenz (tf) für t.
	 */
	public double getTf(String t) {
		Integer integer = tf.get(t);
		return integer == null ? 0 : integer;
	}

	/**
	 * @param query
	 * @param index
	 * 
	 * @return Die Ähnlichkeit dieses Dokuments zu einer query.
	 */
	public Double similarity(Work query, RankedRetrieval index) {

		// TODO Hier muss noch die Ähnlichkeit errechnet werden ...
		
		return null;
	}

}
