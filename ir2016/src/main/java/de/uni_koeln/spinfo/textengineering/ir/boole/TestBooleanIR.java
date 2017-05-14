/**
 * 
 */
package de.uni_koeln.spinfo.textengineering.ir.boole;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.basic.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.shakespeare.ShakespeareCorpus;

/**
 * @author spinfo
 *
 */
public class TestBooleanIR {

	private static Corpus corpus;
	private String query;
	private Searcher searcher;

	@BeforeClass
	public static void setUp() throws Exception {
		// Korpus einlesen und in Werke unterteilen:
		String filename = "pg100.txt";
		String delimiter = "1[56][0-9]{2}\n";
		corpus = new ShakespeareCorpus(filename, delimiter);
	}

	@Test
	public void testMatrix() {
		// Aufbau der Matrix testen
		searcher = new TermDokumentMatrix(corpus);
		((TermDokumentMatrix) searcher).printMatrix();
	}

	@Test
	public void testMatrixSearch() {
		// Testen, ob Suche in Term-Dokument-Matrix ein Ergebnis liefert:

		System.out.println();
		System.out.println("Term-Dokument-Matrix:");
		System.out.println("-------------------");

		searcher = new TermDokumentMatrix(corpus);
		query = "Brutus Caesar";

		Set<Integer> result = searcher.search(query);
		assertTrue("Ergebnis sollte nicht leer sein", result.size() > 0);
		System.out.println("OR-Ergebnis für " + query + ": " + result);

		Set<Integer> result2 = ((TermDokumentMatrix) searcher).booleanSearch(query);
		assertTrue("Ergebnis sollte kleiner sein als bei normaler Suche", result.size() > result2.size());
		System.out.println("AND-Ergebnis für " + query + ": " + result2);
	}

	@Test
	public void testInvertedIndex() {
		// Testen, ob Suche in invertiertem Index ein Ergebnis liefert

		System.out.println();
		System.out.println("Invertierter Index:");
		System.out.println("-------------------");

		searcher = new InvertedIndex(corpus);
		query = "Brutus Caesar";

		Set<Integer> result = searcher.search(query);
		assertTrue("Ergebnis sollte nicht leer sein", result.size() > 0);
		System.out.println("Ergebnis für " + query + ": " + result);
		for (Integer id : result) {
			System.out.println("id: " + id + " - " + corpus.getDocuments().get(id));
		}
	}

	@Test
	public void testPositionalIndex() {
		// Testen, ob Suche in invertiertem Index ein Ergebnis liefert

		System.out.println();
		System.out.println("Positional Index:");
		System.out.println("-------------------");
		searcher = new PositionalIndex(corpus);

		/*
		 * Standard-Suche (wie bisher):
		 */
		query = "Brutus Caesar";
		Set<Integer> result = searcher.search(query);
		assertTrue("Ergebnis sollte nicht leer sein", result.size() > 0);
		System.out.println("Ergebnis für " + query + ": " + result);
		for (Integer id : result) {
			System.out.println("id: " + id + " - " + corpus.getDocuments().get(id));
		}

		/*
		 * Proximity-Suche:
		 */
		query = "to be or not to be";
		int range = 1;
		System.out.println("-------- range = " + range + " ----------");
		SortedMap<Integer, List<Integer>> posResult = ((PositionalIndex) searcher).proximitySearch(query, range);
		assertTrue("ergebnis sollte nicht leer sein!", posResult.size() > 0);
		System.out.println("Ergebnis für " + query + ": " + posResult.keySet());
		((PositionalIndex) searcher).printSnippets(query, posResult, 1);
	}

}
