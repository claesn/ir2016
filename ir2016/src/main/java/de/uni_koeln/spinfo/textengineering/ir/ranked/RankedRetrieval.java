package de.uni_koeln.spinfo.textengineering.ir.ranked;

import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.textengineering.ir.basic.Work;

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

	Set<Integer> search(String query);

	List<Work> getWorks();

}
