package de.uni_koeln.spinfo.textengineering.ir.eval;

import java.util.ArrayList;
import java.util.List;

import de.uni_koeln.spinfo.textengineering.ir.basic.Work;
import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;

/*
 * Erstellung eines Dummy-Goldstandards auf Grundlage unseres Shakespeare-Korpus
 */
public class GoldStandard {

	public static List<Integer> create(RankedRetrieval index, String query) {

		List<Integer> result = new ArrayList<Integer>();
		Preprocessor p = new Preprocessor();
		List<String> q = p.tokenize(query);

		int docId = 0;
		for (Work d : index.getWorks()) {
			/*
			 * Für unsere Experimente mit P, R und F betrachten wir ein Dokument immer dann als relevant, wenn ein Term
			 * der Anfrage im Titel des Dokuments enthalten ist:
			 */
			if (containsAny(d.getTitle(), q)) {
				result.add(docId);
			}
			docId++;
		}
		return result;
	}

	private static boolean containsAny(String title, List<String> query) {
		for (String q : query) {
			/* Wir geben true zurück wenn ein Element der Anfrage im Titel enthalten ist: */
			if (title.toLowerCase().contains(q.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
