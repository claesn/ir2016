package de.uni_koeln.spinfo.textengineering.ir.tolerant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.uni_koeln.spinfo.textengineering.ir.boole.InvertedIndex;
import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;

public class TolerantRetrieval extends InvertedIndex {

	public TolerantRetrieval(Corpus corpus) {
		super(corpus);
	}

	/*
	 * Tolerante Suche: wird für eine (Teil-)query kein Treffer gefunden, werden Alternativen ermittelt.
	 */
	public Set<Integer> searchTolerant(String query, StringSimilarity sim) {
		List<String> queries = new Preprocessor().getTerms(query);
		List<Set<Integer>> allPostings = new ArrayList<Set<Integer>>();
		for (String q : queries) {
			Set<Integer> postings = super.index.get(q);
			/*
			 * Im Unterschied zur 'normalen' Suche machen wir hier einen null-Check und ermitteln ggf. Alternativen.
			 */
			if (postings == null) {
				System.out.println("Keine Treffer für Suchwort " + q + ", suche Varianten...");
				// Wenn kein Ergebnis, dann Varianten holen - und zwar die besten
				String best = getBestVariant(q, sim);
				System.out.println("Zeige Ergebnisse für: " + best);
				postings = super.index.get(best);
			}
			allPostings.add(postings);
		}
		Set<Integer> result = allPostings.get(0);
		for (Set<Integer> postings : allPostings) {
			// result.addAll(postings);// OR-Verknüpfung
			result.retainAll(postings);// UND-Verknüpfung
		}
		return result;
	}

	private String getBestVariant(String q, StringSimilarity sim) {

		// Wir holen uns zunächst Varianten zur query (abweichend je nach konkreter Implementierung):
		List<String> variants = sim.getVariants(q, index.keySet());
		System.out.println("alle Varianten: " + variants);
		/*
		 * Und ermitteln dann die 'beste' Variante, indem wir zu jedem Element der Liste eine eigene Suche starten und
		 * das Element mit der längsten Trefferliste behalten. Hierfür müssen wir die Ergebnisse absteigend sortieren:
		 */
		Map<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
		for (String v : variants) {
			// Wir können hier die 'normale' Suche einsetzen, da wir sicher wissen, dass die Varianten im Index sind:
			Set<Integer> result = search(v);
			map.put(result.size(), v);
		}
		String best = map.values().iterator().next();
		System.out.println("Beste Alternativen: " + map);
		return best;
	}

}
