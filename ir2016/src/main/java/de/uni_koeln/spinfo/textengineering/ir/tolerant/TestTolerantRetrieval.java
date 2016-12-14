/**
 * 
 */
package de.uni_koeln.spinfo.textengineering.ir.tolerant;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.preprocess.Preprocessor;

/**
 * @author spinfo
 *
 */
public class TestTolerantRetrieval {

	private static Corpus corpus;
	private String query;

	@BeforeClass
	public static void setUp() throws Exception {
		String filename = "pg100.txt";
		String delimiter = "1[56][0-9]{2}\n";
		corpus = new Corpus(filename, delimiter);
	}

	/*
	 * Levenshtein-Distanz in der StringUtils-Implementation (Apache Commons).
	 */
	@Test
	public void testLevenshtein() {

		System.out.println("Levenshtein");
		System.out.println("------------");

		String s1 = "weather";
		String s2 = "wether";
		
		// TODO Distanz berechnen
		
		List<String> terms = new Preprocessor().getTerms(corpus.getText());
		List<String> variants = new EditDistance().getVariants(s1, new HashSet<String>(terms));
		System.out.println("Varianten für " + s1 + ": " + variants);
	}

	/*
	 * Phonetischer Stringvergleich mit dem Soundex-Algorithmus (Apache Commons Codec).
	 */
	@Test
	public void testSoundex(){

		System.out.println("Soundex");
		System.out.println("------------");

		String s1 = "weather";
		String s2 = "wether";

		// TODO Distanz berechnen
		
		List<String> terms = new Preprocessor().getTerms(corpus.getText());
		List<String> variants = new PhoneticCorrection().getVariants(s1, new HashSet<String>(terms));
		System.out.println("Varianten für: " + s1 + ": " + variants);
	}

	/*
	 *  Testen, ob auch inkorrekte queries ein Ergebnis liefern:
	 */
	@Test
	public void testTolerantRetrieval() {

		System.out.println();
		System.out.println("Tolerant Retrieval:");
		System.out.println("-------------------");

		TolerantRetrieval ir = new TolerantRetrieval(corpus);
		Set<Integer> result = null;

		query = "caezar";
		query = "bruttus";

		result = ir.searchTolerant(query, new EditDistance());
		assertTrue("Mindestens ein Treffer erwartet", result.size() >= 1);
		System.out.println("Ergebnis für " + query + ": " + result);
		System.out.println("-------------------");

		result = ir.searchTolerant(query, new PhoneticCorrection());
		assertTrue("Mindestens ein Treffer erwartet", result.size() >= 1);
		System.out.println("Ergebnis für " + query + ": " + result);
		System.out.println("-------------------");
}


	
}
