/**
 * 
 */
package de.uni_koeln.spinfo.textengineering.ir.boole;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;

/**
 * @author spinfo
 *
 */
public class TestBooleanIR {

	private static Corpus corpus;
	private String query;
	private TermDokumentMatrix searcher;

	@BeforeClass
	public static void setUp() throws Exception {
		// Korpus einlesen und in Werke unterteilen:
		String filename = "pg100.txt";
		String delimiter = "1[56][0-9]{2}\n";
		corpus = new Corpus(filename, delimiter);
	}

	@Test
	public void testMatrix() {
		// Aufbau der Matrix testen
		searcher = new TermDokumentMatrix(corpus);
		searcher.printMatrix();
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

		Set<Integer> result2 = searcher.booleanSearch(query);
		assertTrue("Ergebnis sollte kleiner sein als bei normaler Suche", result.size() > result2.size());
		System.out.println("AND-Ergebnis für " + query + ": " + result2);
	}
	
	@Test
	public void testInvertedIndex() {
		// TODO Testen, ob Suche in invertiertem Index ein Ergebnis liefert
		
	}

}
