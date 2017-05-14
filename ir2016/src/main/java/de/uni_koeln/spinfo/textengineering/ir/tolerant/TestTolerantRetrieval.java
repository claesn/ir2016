/**
 * 
 */
package de.uni_koeln.spinfo.textengineering.ir.tolerant;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.shakespeare.ShakespeareCorpus;
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
		corpus = new ShakespeareCorpus(filename, delimiter);
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

		// Distanz berechnen
		int levenshteinDistance = StringUtils.getLevenshteinDistance(s1, s2);
		System.out.println("Distanz zwischen " + s1 + " und " + s2 + ": " + levenshteinDistance);

		List<String> terms = new Preprocessor().getTerms(corpus.getText());
		List<String> variants = new EditDistance().getVariants(s1, new HashSet<String>(terms));
		System.out.println("Varianten für " + s1 + ": " + variants);
	}

	/*
	 * Phonetischer Stringvergleich mit dem Soundex-Algorithmus (Apache Commons Codec).
	 */
	@Test
	public void testSoundex() throws EncoderException {

		System.out.println("Soundex");
		System.out.println("------------");

		String s1 = "weather";
		String s2 = "wether";

		// Distanz berechnen
		Soundex soundex = new Soundex();
		int difference = soundex.difference(s1, s2);
		System.out.println("Soundex difference zwischen " + s1 + " und " + s2 + ": " + difference);

		List<String> terms = new Preprocessor().getTerms(corpus.getText());
		List<String> variants = new PhoneticCorrection().getVariants(s1, new HashSet<String>(terms));
		System.out.println("Varianten für: " + s1 + ": " + variants);
	}

	/*
	 * Testen, ob auch inkorrekte queries ein Ergebnis liefern:
	 */
	@Test
	public void testTolerantRetrieval() {

		System.out.println();
		System.out.println("Tolerant Retrieval:");
		System.out.println("-------------------");

		TolerantRetrieval ir = new TolerantRetrieval(corpus);
		Set<Integer> result = null;

		query = "bruttus";

		result = ir.searchTolerant(query, new EditDistance());
		assertTrue("Mindestens ein Treffer erwartet", result.size() >= 1);
		System.out.println("Ergebnis für " + query + ": " + result);
		System.out.println("-------------------");

		result = ir.searchTolerant(query, new PhoneticCorrection());
		assertTrue("Mindestens ein Treffer erwartet", result.size() >= 1);
		System.out.println("Ergebnis für " + query + ": " + result);
		System.out.println("-------------------");

		query = "caezar";

		result = ir.searchTolerant(query, new EditDistance());
		assertTrue("Mindestens ein Treffer erwartet", result.size() >= 1);
		System.out.println("Ergebnis für " + query + ": " + result);
		System.out.println("-------------------");

		result = ir.searchTolerant(query, new PhoneticCorrection());
		assertTrue("Mindestens ein Treffer erwartet", result.size() >= 1);
		System.out.println("Ergebnis für " + query + ": " + result);
	}

	/*
	 * Alternativen zu Soundex. Für Metaphone/ColognePhonetic gibt es keine Funktion "difference", diese müsste selbst
	 * programmiert werden.
	 */
	@Test
	public void testPhoneticCorrection() {

		System.out.println("------------");

		String s1 = "spears";
		String s2 = "superzicke";

		Soundex s = new Soundex();
		System.out.println(String.format("soundex of '%s': '%s'", s1, s.soundex(s1)));
		System.out.println(String.format("soundex of '%s': '%s'", s2, s.soundex(s2)));

		// alternatives Codec zu Soundex:
		Metaphone m = new Metaphone();
		System.out.println(String.format("metaphone of '%s': '%s'", s1, m.metaphone(s1)));
		System.out.println(String.format("metaphone of '%s': '%s'", s2, m.metaphone(s2)));

		// "Kölner Phonetik" speziell für das Deutsche:
		ColognePhonetic cp = new ColognePhonetic();
		System.out.println(String.format("colognePhonetic of '%s': '%s'", s1, cp.colognePhonetic(s1)));
		System.out.println(String.format("colognePhonetic of '%s': '%s'", s2, cp.colognePhonetic(s2)));
	}
}
