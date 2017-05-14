package de.uni_koeln.spinfo.textengineering.ir.ranked;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

public class TermWeighting {

	/**
	 * Umsetzung der tfIdf-Formel aus dem Seminar (siehe Folien).
	 * 
	 * @param t
	 * @param work
	 * @param index
	 * 
	 * @return Der tf-idf-Wert für t in Werk work.
	 */
	public static double tfIdf(String term, IRDocument document, RankedRetrieval index) {
		/*
		 * Abweichend zu den Folien im Seminar verzichten wir hier auf das sog. 'Add-one-smoothing' und auf den
		 * Logarithmus, beides greift erst bei größeren Sammlungen:
		 */
		double tf = document.getTf(term);
		double df = index.getDf(term);
		double n = index.getDocuments().size();
		double idf = Math.log(n / df);
		double tfidf = tf * idf;

		return tfidf;
	}

}
