package de.uni_koeln.spinfo.textengineering.ir.basic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

public class LinearSearch {

	private List<IRDocument> documents;

	public LinearSearch(Corpus corpus) {
		documents = corpus.getDocuments();
	}

	public Set<Integer> search(String query) {

		long start = System.currentTimeMillis();
		Set<Integer> result = new HashSet<>();
		// Anstelle eines Query-Parsers hier ein einfaches split:
		List<String> queries = Arrays.asList(query.split("\\s+"));

		for (String q : queries) {
			for (IRDocument doc : documents) {
				String text = doc.getContent();
				List<String> tokens = Arrays.asList(text.split("\\s+"));
				for (String t : tokens) {
					if (t.compareTo(q) == 0) {
						result.add(documents.indexOf(doc));
						break;
					}
				}
			}
		}
		System.out.println("Dauer: " + (System.currentTimeMillis() - start) + " ms.");
		return result;
	}

}
