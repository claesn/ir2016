package de.uni_koeln.spinfo.textengineering.ir.ranked;

import de.uni_koeln.spinfo.textengineering.ir.basic.Work;

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
	public static double tfIdf(String t, Work work, RankedRetrieval index) {
		/*
		 * Abweichend zu den Folien im Seminar verzichten wir hier auf das sog. 'Add-one-smoothing' und auf den
		 * Logarithmus, beides greift erst bei größeren Sammlungen:
		 */
		double tf = work.getTf(t);
		double df = index.getDf(t);
		double n = index.getWorks().size();
		double idf = Math.log(n / df);
		double tfidf = tf * idf;

		return tfidf;
	}

}
