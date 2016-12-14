package de.uni_koeln.spinfo.textengineering.ir.tolerant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PhoneticCorrection implements StringSimilarity {

	/*
	 * Phonetischer Stringvergleich mit dem Soundex-Algorithmus, hier unter Nutzung der entsprechenden
	 * Codec-Implementation von Apache Commons.
	 * 
	 */
	public List<String> getVariants(String q, Set<String> terms) {

		List<String> variants = new ArrayList<String>();
		for (String t : terms) {
			/*
			 * TODO Soundex berechnen
			 */
		}
		return variants;
	}

}
