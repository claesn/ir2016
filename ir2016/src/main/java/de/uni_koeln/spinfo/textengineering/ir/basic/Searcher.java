package de.uni_koeln.spinfo.textengineering.ir.basic;

import java.util.Set;

public interface Searcher {

	/*
	 * Grundstruktur der boole'schen Suche: unabhängig von der konkreten Implementation (z.B. LinearSearch,
	 * TermDokumentMatrix) gibt 'search' die Ids aller Werke zurück, in denen der String "query" vorkommt. Als Ids
	 * nehmen wir die Indexposition der Werke.
	 */
	Set<Integer> search(String query);

}
