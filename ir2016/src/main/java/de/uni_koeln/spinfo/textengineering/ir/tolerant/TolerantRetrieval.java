package de.uni_koeln.spinfo.textengineering.ir.tolerant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.boole.InvertedIndex;
import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;

public class TolerantRetrieval extends InvertedIndex {

	public TolerantRetrieval(Corpus corpus) {
		super(corpus);
	}

	public Set<Integer> searchTolerant(String query, StringSimilarity sim) {
		List<String> queries = new Preprocessor().getTerms(query);
		List<Set<Integer>> allPostings = new ArrayList<Set<Integer>>();
		for (String q : queries) {
			Set<Integer> postings = super.index.get(q);
			/*
			 * Im Unterschied zur 'normalen' Suche machen wir hier einen null-Check und ermitteln ggf. Alternativen.
			 */
			if (postings == null) {
				
				/*
				 * TODO Wenn kein Ergebnis, dann Varianten holen - und zwar die besten
				 * 
				 */
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

}
