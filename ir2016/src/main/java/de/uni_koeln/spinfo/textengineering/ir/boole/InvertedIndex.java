package de.uni_koeln.spinfo.textengineering.ir.boole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.basic.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.basic.Work;

public class InvertedIndex implements Searcher {

	protected Map<String, SortedSet<Integer>> index;//Unsere Zugriffsstruktur

	public InvertedIndex(Corpus corpus) {
		Long start = System.currentTimeMillis();
		index = index(corpus);
		System.out.println("Indexierung in: " + (System.currentTimeMillis() - start) + " ms.");
	}

	private Map<String, SortedSet<Integer>> index(Corpus corpus) {
		HashMap<String, SortedSet<Integer>> invIndex = new HashMap<String, SortedSet<Integer>>();
		// Index aufbauen
		List<Work> works = corpus.getWorks();
		for (int i = 0; i < works.size(); i++) {
			Work work = works.get(i);
			List<String> terms = Arrays.asList(work.getText().split("\\s+"));
			for (String term : terms) {
				// Wir holen uns jeweils die postings-Liste des Terms aus dem Index:
				SortedSet<Integer> postings = invIndex.get(term);
				/*
				 * beim ersten Vorkommen des Terms ist diese noch leer (null), also legen wir uns einfach eine neue an:
				 */
				if (postings == null) {
					postings = new TreeSet<>();
					invIndex.put(term, postings);
				}
				/*
				 * Der Term wird indexiert, indem die Id des aktuellen Werks (= der aktuelle Zählerwert) der
				 * postings-list hinzugefügt wird:
				 */
				postings.add(i);
			}
		}
		return invIndex;
	}

	@Override
	public Set<Integer> search(String query) {
		// Suchen im Index
		List<String> queries = Arrays.asList(query.split("\\s+"));
		/*
		 * Wir holen uns zunächst die Postings-Listen der Teilqueries:
		 */
		List<Set<Integer>> allPostings = new ArrayList<Set<Integer>>();
		for (String q : queries) {
			SortedSet<Integer> postings = index.get(q);
			allPostings.add(postings);
		}
		// Ergebnis ist die Schnittmenge (Intersection) der ersten Liste...
		Set<Integer> result = allPostings.get(0);
		// ... mit allen weiteren:
		for (Set<Integer> postings : allPostings) {
			// result.addAll(postings);// OR-Verknüpfung
			result.retainAll(postings);// UND-Verknüpfung
		}
		return result;
	}

}
