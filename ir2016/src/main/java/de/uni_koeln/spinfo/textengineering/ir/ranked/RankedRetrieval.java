package de.uni_koeln.spinfo.textengineering.ir.ranked;

import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

/*
 * Information-Retrieval in der einfachsten Form: unabhängig von der
 * konkreten Implementation (z.B. LinearSearch, TermDocumentMatrix) gibt
 * 'search' die Ids aller Werke zurück, in denen der String "query"
 * vorkommt. Als Ids nehmen wir die Indexposition der Werke.
 * 
 * NEU: Hier braucht es noch ein paar Ergänzungen, damit weiterhin 'generisch' 
 * mit einem Interface gearbeitet werden kann.
 * 
 */

public interface RankedRetrieval {

	/*
	 * Wir nutzen erneut die Grundstruktur der boole'schen Suche: unabhängig von der konkreten Implementation (z.B.
	 * LinearSearch, TermDokumentMatrix) gibt 'search' die Ids aller Werke zurück, in denen der String "query" vorkommt.
	 * Als Ids nehmen wir die Indexposition der Werke.
	 */
	Set<Integer> search(String query);

	/*
	 * Neu: Ergänzungen, damit weiterhin 'generisch' mit einem Interface gearbeitet werden kann:
	 */
	List<IRDocument> getDocuments();

	List<String> getTerms();

	double getDf(String t);

}
