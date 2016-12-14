package de.uni_koeln.spinfo.textengineering.ir.tolerant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EditDistance implements StringSimilarity {

	/*
	 * Stringvergleich mittels Levenshtein-Distanz, hier unter Verwendung der StringUtils (Apache Commons).
	 * 
	 */
	public List<String> getVariants(String q, Set<String> terms) {

		List<String> variants = new ArrayList<String>();
		for (String t : terms) {
			/*
			 * TODO Levenshtein-Distanz
			 */
		}
		return variants;
	}

}
