package de.uni_koeln.spinfo.textengineering.ir.model.shakespeare;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.util.IRUtils;

public class Work implements IRDocument {

	private String text;
	private String title;
	// NEU: Jedes Werk enthält einen eigenen Term-Index
	private Map<String, Integer> tf;

	public Work(String content, String title) {
		setContent(content);
		setTitle(title);
		// NEU: Beim Erstellen des Work-Objekts werden die Termfrequenzen ermittelt
		this.tf = IRUtils.computeTf(content);
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

	/**
	 * Zugriff auf den Text
	 * 
	 * @return Der Inhalt des Dokuments als String.
	 */
	@Override
	public String getContent() {
		return text;
	}

	@Override
	public void setContent(String content) {
		this.text = content;
	}

	@Override
	public String getTopic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTopic(String topic) {
		// TODO Auto-generated method stub

	}

	@Override
	public URI getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setURI(URI uri) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		return String.format("Titel: %s", title);
	}

	/*
	 * In der IR-Logik können Termfrequenzen (tf) direkt beim Indexieren
	 * gesammelt werden. Ein Beispiel ist der PositionalIndex, tf entspricht
	 * dort einfach der Länge der Positionslisten. In unserem Beispiel gehen wir
	 * davon aus, dass diese Positionslisten nicht zur Verfügung stehen, d.h.
	 * wir müssen uns die tf-Werte vorberechnen und für bequemeren Zugriff in
	 * einer Map ablegen.
	 */
//	private Map<String, Integer> computeTf() {
//		Map<String, Integer> termMap = new HashMap<String, Integer>();
//		/* Wir zählen die Häufigkeiten der Tokens: */
//		List<String> tokens = new Preprocessor().tokenize(text);
//		for (String token : tokens) {
//			Integer tf = termMap.get(token);
//			/*
//			 * Wenn der Term noch nicht vorkam, beginnen wir zu zählen (d.h. wir
//			 * setzen 1)
//			 */
//			if (tf == null) {
//				tf = 1;
//			} else {
//				// sonst zählen wir einfach bei jedem Vorkommen hoch
//				tf = tf + 1;
//			}
//			termMap.put(token, tf);
//		}
//		return termMap;
//	}

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
	 * Die Cosinus-Ähnlichkeit dieses Werks zu einer query. Die eigentliche
	 * Ähnlichkeitsberechnung delegieren wir an eine Vergleichstrategie,
	 * implementiert in der Klasse VectorComparison.
	 * 
	 * @param query
	 * @param index
	 * 
	 * @return Die Cosinus-Ähnlichkeit dieses Dokuments zu einer query.
	 */
//	public Double similarity(Work query, RankedRetrieval index) {
//
//		// Ähnlichkeit zwischen Query und Dokument (Werk) erhalten wir über den
//		// Vergleich der Vektoren
//		List<Double> docVector = this.computeVector(index);
//		List<Double> queryVector = query.computeVector(index);
//
//		// optionale Ausgabe:
//		double sim = VectorComparison.compare(docVector, queryVector);
//		// System.out.println("Similarity von " + this + " zu " + query + ": " +
//		// sim);
//		return sim;
//	}

//	public List<Double> computeVector(RankedRetrieval index) {
//
//		List<String> terms = index.getTerms();
//		/*
//		 * Ein Vektor für dieses Werk ist eine Liste (Länge = Anzahl Terme
//		 * insgesamt)
//		 */
//		List<Double> vector = new ArrayList<Double>(terms.size());
//		Double tfIdf;
//		/* ...und dieser Vektor enthält für jeden Term im Vokabular... */
//		for (String t : terms) {
//			/*
//			 * ...den tfIdf-Wert des Terms (Berechnung in einer eigenen Klasse):
//			 */
//			tfIdf = TermWeighting.tfIdf(t, this, index);
//			vector.add(tfIdf);
//		}
//		return vector;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((tf == null) ? 0 : tf.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Work other = (Work) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (tf == null) {
			if (other.tf != null)
				return false;
		} else if (!tf.equals(other.tf))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	

}
