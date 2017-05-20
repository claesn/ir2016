package de.uni_koeln.spinfo.textengineering.ir.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;
import de.uni_koeln.spinfo.textengineering.ir.ranked.VectorComparison;

public final class IRUtils {

	/**
	 * In der IR-Logik können Termfrequenzen (tf) direkt beim Indexieren
	 * gesammelt werden. Ein Beispiel ist der PositionalIndex, tf entspricht
	 * dort einfach der Länge der Positionslisten. In unserem Beispiel gehen wir
	 * davon aus, dass diese Positionslisten nicht zur Verfügung stehen, d.h.
	 * wir müssen uns die tf-Werte vorberechnen und für bequemeren Zugriff in
	 * einer Map ablegen.
	 * 
	 * @param content
	 * @return
	 */
	public static Map<String, Integer> computeTf(String content) {
		Map<String, Integer> termMap = new HashMap<String, Integer>();
		List<String> tokens = new Preprocessor().tokenize(content);
		for (String token : tokens) {
			Integer tf = termMap.get(token);
			tf = tf == null ? 1 : ++tf;
			termMap.put(token, tf);
		}
		return termMap;
	}

	/**
	 * Die Cosinus-Ähnlichkeit dieses Werks zu einer query. Die eigentliche
	 * Ähnlichkeitsberechnung delegieren wir an eine Vergleichstrategie,
	 * implementiert in der Klasse VectorComparison.
	 * 
	 * @param query
	 *            document
	 * @param index
	 * 
	 * @return Die Cosinus-Ähnlichkeit dieses Dokuments zu einer query.
	 */
	public static Double similarity(IRDocument queryDoc, IRDocument refDoc, RankedRetrieval index) {
		List<Double> queryVector = queryDoc.computeVector(index);
		List<Double> docVector = refDoc.computeVector(index);
		double sim = VectorComparison.compare(queryVector, docVector);
		return sim;
	}

	/**
	 * Gibt zu einem geg. Dokument die ähnlichsten zurück.
	 * @param doc
	 * @param index
	 * @param n Anzahl
	 * @return Die ähnlichsten Dokumente zu doc
	 */
	public static List<IRDocument> getMostSimilar(IRDocument doc, RankedRetrieval index, int n) {

		SortedMap<Double, IRDocument> map = new TreeMap<Double, IRDocument>(Collections.reverseOrder());
		List<IRDocument> documents = index.getDocuments();
		for (IRDocument irDocument : documents) {
			map.put(similarity(doc, irDocument, index), irDocument);
		}
		List<IRDocument> result = new ArrayList<IRDocument>();
		Iterator<Double> iterator = map.keySet().iterator();
		int count = 0;
		while(iterator.hasNext()&&count<n){
			Double sim = iterator.next();
			result.add(map.get(sim));
			System.out.println(sim + " - " + map.get(sim));
			count++;
		}
		return result;
		//alternativ:
//		return new ArrayList<IRDocument>(map.values()).subList(0,n);
	}

}
