package de.uni_koeln.spinfo.textengineering.ir.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

/*
 * Ein sehr einfacher Preprocessor: splittet und gibt Tokens oder sortierte Types zurück.
 */

public final class Preprocessor {

	/*
	 * Ein Unicode-wirksamer Ausdruck für "Nicht-Buchstabe", der auch Umlaute berücksichtigt; die einfache (ASCII)
	 * Version ist: "\\W"
	 */
	private static final String UNICODE_AWARE_DELIMITER = "[^\\p{L}]";

	public List<String> tokenize(String text) {
		/* Einheitliches lower-casing */
		text = text.toLowerCase();
		List<String> result = new ArrayList<String>();
		/* splitten, leere Strings filtern: */
		List<String> tokens = Arrays.asList(text.toLowerCase().split(UNICODE_AWARE_DELIMITER));
		for (String s : tokens) {
			if (s.trim().length() > 0) {
				result.add(s.trim());
			}
		}
		return result;
	}

	/*
	 * Gibt eine Liste der Terme zurück
	 */
	public List<String> getTerms(String text) {
		SortedSet<String> terms = new TreeSet<String>(tokenize(text));
		return new ArrayList<String>(terms);
	}

	public List<String> getStems(String testString) {
		//Stemming
		List<String> stems = new ArrayList<>();
		List<String> terms = getTerms(testString);
		SnowballStemmer stemmer = new englishStemmer();//weitere Sprachvarianten siehe API
		for (String term : terms) {
			//Benutzung siehe TestApp.java in der Snowball-API
			stemmer.setCurrent(term);
			stemmer.stem();
			String stem = stemmer.getCurrent();
			stems.add(stem);
		}
		SortedSet<String> result = new TreeSet<>(stems);
		return new ArrayList<>(result);
	}


}
