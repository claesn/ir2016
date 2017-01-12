package de.uni_koeln.spinfo.textengineering.ir.ranked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.basic.Work;

/*
 * Für die Umsetzung des RankedRetrieval sind nur geringfügige Veränderungen nötig.
 * Konzeptuell bietet es sich dennoch an, hier eine eigene Klasse mit einem eigenen
 * Interface anzulegen, um die Funktionen klar getrennt zu halten.
 */
public class InvIndex implements RankedRetrieval {

	private Map<String, SortedSet<Integer>> index;
	// NEU: Korpus für direkten Zugriff auf die Werke
	private Corpus corpus;

	public InvIndex(Corpus corpus) {
		long start = System.currentTimeMillis();
		index = index(corpus);
		System.out.println("Index erstellt, Dauer: "
				+ (System.currentTimeMillis() - start) + " ms.");
		// NEU: Korpus für Zugriff auf Werke
		this.corpus = corpus;
	}

	private Map<String, SortedSet<Integer>> index(Corpus corpus) {
		HashMap<String, SortedSet<Integer>> index = new HashMap<String, SortedSet<Integer>>();
		List<Work> works = corpus.getWorks();
		for (int i = 0; i < works.size(); i++) {
			// NEU: Preprocessor wird direkt im Werk eingesetzt
			List<String> terms = works.get(i).getTerms();
			// der Rest bleibt wie bisher ...
			for (String t : terms) {
				SortedSet<Integer> postings = index.get(t);
				if (postings == null) {
					postings = new TreeSet<Integer>();
					index.put(t, postings);
				}
				postings.add(i);
			}
		}
		return index;
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
//			 result.addAll(postings);// OR-Verknüpfung
			result.retainAll(postings);// UND-Verknüpfung
		}
		return result;
	}

	/*
	 *  Alle Werke.
	 */
	@Override
	public List<Work> getWorks() {
		return corpus.getWorks();
	}

	/*
	 *  Alle Terme im Korpus.
	 */
	@Override
	public List<String> getTerms() {
		return new ArrayList<String>(index.keySet());
	}

	/*
	 *  Die Dokumentenfrequenz zu einem Term:
	 */
	@Override
	public double getDf(String t) {
		return index.get(t).size();
	}

}
