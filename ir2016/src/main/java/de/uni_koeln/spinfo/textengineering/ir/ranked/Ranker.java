package de.uni_koeln.spinfo.textengineering.ir.ranked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.textengineering.ir.basic.Work;

public class Ranker {

	/*
	 * Query und Index werden als Klassenvariablen direkt bei der Instantiierung gesetzt (siehe Konstruktor), da sie bei
	 * Aufruf der Methode rank() für jede Vergleichsoperation in sort benötigt werden, um die Cosinus-Ähnlichkeit jedes
	 * Documents des result-Sets zu ermitteln.
	 */
	private Work query;
	private RankedRetrieval index;

	public Ranker(String query, RankedRetrieval index) {
		// hier wird aus der query ein kleines Document erzeugt (text = query, title = "Query")
		this.query = new Work(query, "QUERY");
		this.index = index;
	}

	/**
	 * Ranking des Ergebnis-Sets, implementiert als einfache Sortierung nach Ähnlichkeit zur Query.
	 * 
	 * @param result
	 *            - Das unsortierte (boolesche) Ergebnis.
	 * @param query
	 *            - Die query als Work-Objekt.
	 * @return Sortierte Liste.
	 */
	public List<Integer> rank(Set<Integer> result) {

		// result wird zunächst in eine Liste von Werken umgewandelt:
		List<Work> toSort = new ArrayList<>();
		for (Integer integer : result) {
			Work work = index.getWorks().get(integer);
			toSort.add(work);
		}
		
		/*
		 * Java stellt für Collections (Listen, Maps, etc) die Methode sort() bereit, der man einen Sortierschlüssel
		 * (einen Comparator) übergeben kann. Wir wollen Dokumente anhand ihrer Ähnlichkeit zur query sortieren, deshalb
		 * müssen wir uns zunächst einen geeigneten Comparator schreiben:
		 */
		Collections.sort(toSort, new Comparator<Work>() {
			public int compare(Work w1, Work w2) {
				/*
				 * Wir sortieren alle Vektoren nach ihrer (Cosinus-) Ähnlichkeit zur Anfrage (query), dazu benötigen wir
				 * zunächst die Ähnlichkeiten von w1 zur Query und w2 zur Query:
				 */
				Double s1 = w1.similarity(query, index);
				Double s2 = w2.similarity(query, index);
				/*
				 * Anschließend sortieren wir nach diesen beiden Ähnlichkeiten. Wir wollen absteigende Ähnlichkeit, d.h.
				 * s2.compareTo(s1) statt s1.compareTo(s2) d.h. die höchsten Werte und damit besten Treffer zuerst:
				 */
				return s2.compareTo(s1);
			}
		});
		
		//jetzt die sortierte Liste wieder in Set von Integer umwandeln ...
		List<Integer> toReturn = new ArrayList<>();
		for (Work work : toSort) {
			int indexOf = index.getWorks().indexOf(work);
			toReturn.add(indexOf);
		}
		return toReturn ;
	}

}
