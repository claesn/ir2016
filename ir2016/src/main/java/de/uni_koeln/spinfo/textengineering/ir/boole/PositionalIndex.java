package de.uni_koeln.spinfo.textengineering.ir.boole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import de.uni_koeln.spinfo.textengineering.ir.basic.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;

public class PositionalIndex implements Searcher {

	private Map<String, SortedMap<Integer, List<Integer>>> index;// Unsere Zugriffsstruktur
	private Preprocessor p = new Preprocessor();
	// Zugriff auf tokens & Titel (siehe Methode printSnippets()):
	private Corpus corpus;

	public PositionalIndex(Corpus corpus) {
		Long start = System.currentTimeMillis();
		index = index(corpus);
		this.corpus = corpus;// Korpus für Ergebnisaufbereitung
		System.out.println("Indexierung in: " + (System.currentTimeMillis() - start) + " ms.");
	}

	private Map<String, SortedMap<Integer, List<Integer>>> index(Corpus corpus) {

		Map<String, SortedMap<Integer, List<Integer>>> posIndex = new HashMap<>();

		// Index aufbauen
		List<IRDocument> documents = corpus.getDocuments();
		for (int i = 0; i < documents.size(); i++) {
			IRDocument document = documents.get(i);
			List<String> terms = p.tokenize(document.getContent());
			for (int j = 0; j < terms.size(); j++) {
				String term = terms.get(j);
				// Wir holen uns jeweils die postings-Map des Terms aus dem Index:
				SortedMap<Integer, List<Integer>> postingsMap = posIndex.get(term);
				// beim ersten Vorkommen des Terms ist diese noch leer (null), also legen wir uns einfach eine neue an:
				if (postingsMap == null) {
					postingsMap = new TreeMap<Integer, List<Integer>>();
					posIndex.put(term, postingsMap);
				}
				// ebenso bei den Positionslisten:
				List<Integer> posList = postingsMap.get(i);
				if (posList == null) {
					posList = new ArrayList<>();
				}
				posList.add(j);
				/*
				 * Der Term wird indexiert, indem die Id des aktuellen Werks (= der aktuelle Zählerwert) zusammen mit
				 * der aktualisierten Positions-Liste der postings-Map hinzugefügt wird:
				 */
				postingsMap.put(i, posList);
			}
		}
		return posIndex;
	}

	/*
	 * Die 'einfache' Index-Suche: Gibt Werke zurück, die (Teil-)queries enthalten. Einziger Unterschied: Zugriff auf
	 * Postings über keySet().
	 */
	public Set<Integer> search(String query) {
		List<String> queries = p.tokenize(query);
		List<Set<Integer>> allPostings = new ArrayList<Set<Integer>>();
		for (String q : queries) {
			// Einzige Veränderung ggü der Suche im invertierten Index: Wir nehmen das keySet der Postings-Maps
			Set<Integer> postings = index.get(q).keySet();
			allPostings.add(postings);
		}
		Set<Integer> result = allPostings.get(0);
		for (Set<Integer> postings : allPostings) {
			// result.addAll(postings);// OR-Verknüpfung
			result.retainAll(postings);// UND-Verknüpfung
		}
		return result;
	}

	/*
	 * Suche mit Beschränkung durch 'Nähe'. Grundidee: Positional Index als erweiterte Indexstruktur - zuerst wie bisher
	 * die Werke ermitteln, in denen beide Terme vorkommen, dann die PositionalIntersection "zuschalten". Vorteil:
	 * einfach "einklinken", ohne den Rest zu verändern.
	 */
	SortedMap<Integer, List<Integer>> proximitySearch(String query, int abstand) {

		List<String> queries = p.tokenize(query);
		List<SortedMap<Integer, List<Integer>>> allPostingsMaps = new ArrayList<>();
		for (String q : queries) {
			SortedMap<Integer, List<Integer>> postingsMap = index.get(q);
			allPostingsMaps.add(postingsMap);
		}
		// Ergebnis ist die Schnittmenge (Intersection) der ersten Map...
		SortedMap<Integer, List<Integer>> result = allPostingsMaps.get(0);
		// ... mit allen weiteren:
		for (SortedMap<Integer, List<Integer>> postingsMap : allPostingsMaps) {
			result = intersect(result, postingsMap, abstand);
		}
		return result;
	}

	/*
	 * Proximity Intersection unter Verwendung der Java-API.
	 */
	private SortedMap<Integer, List<Integer>> intersect(Map<Integer, List<Integer>> pl1, Map<Integer, List<Integer>> pl2,
			int abstand) {

		SortedMap<Integer, List<Integer>> answer = new TreeMap<>();

		// 1. nur Dokumente behalten, die in beiden postingsMaps enthalten sind:
		pl1.keySet().retainAll(pl2.keySet());

		// 2. nun noch alle Positionen testen:
		for (Integer workId : pl1.keySet()) {
			List<Integer> poslist1 = pl1.get(workId);
			List<Integer> poslist2 = pl2.get(workId);
			List<Integer> increment = new ArrayList<>();

			// Hilfsstruktur aufbauen: Jeweils erlaubten Abstand abziehen/hinzurechnen
			for (Integer pos : poslist1) {
				for (int i = -abstand; i <= abstand; i++) {
					if (!increment.contains(pos + i)) {
						increment.add(pos + i);
					}
				}
			}
			increment.retainAll(poslist2);
			if (increment.size() > 0) {
				answer.put(workId, increment);
			}
		}
		return answer;
	}

	/*
	 * Ergebnisdarstellung: Ausgabe von Fundstellen und Werktitel
	 */
	public void printSnippets(String query, SortedMap<Integer, List<Integer>> result, int maxDistance) {
		/*
		 * Da das Ergebnis nur die Position der letzten Teilquery enthält, sollte hier sowohl die Länge der Gesamtquery
		 * als auch der maximale Abstand berücksichtigt werden, innerhalb dessen die Terme auftreten dürfen, damit alle
		 * gesuchten Terme in der Ausgabe sichtbar sind.
		 */
		int queryLength = p.tokenize(query).size();
		int range = maxDistance + queryLength;

		for (Integer docId : result.keySet()) {
			// Werk als Tokenlist für Rekonstruktion der Fundstelle:
			IRDocument document = corpus.getDocuments().get(docId);
			List<String> tokens = p.tokenize(document.getContent());
			// Die einzelnen Fundstellen:
			List<Integer> positions = result.get(docId);
			// Werktitel = erste Zeile des Werks
			String title = document.getTitle();
			System.out.println(
					String.format("'%s' %s-mal gefunden in Werk #%s (%s):", query, positions.size(), docId, title));
			for (Integer pos : positions) {
				// Textanfang und -ende abfangen (Math.max bzw. Math.min)
				int start = Math.max(0, pos - range);
				int end = Math.min(tokens.size(), pos + range);
				// Ausgabe der Position:
				System.out.print("Id " + docId + ", pos " + pos + ": ' ... ");
				for (int i = start; i <= end; i++) {
					System.out.print(tokens.get(i) + " ");
				}
				System.out.println(" ... '");
			}
		}
	}

}
