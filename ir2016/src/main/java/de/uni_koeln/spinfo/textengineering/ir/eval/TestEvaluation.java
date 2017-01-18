package de.uni_koeln.spinfo.textengineering.ir.eval;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.ranked.InvIndex;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;
import de.uni_koeln.spinfo.textengineering.ir.ranked.Ranker;

public class TestEvaluation {

	// Basiskomponenten des IR-Systems
	private static String query;
	private static Corpus corpus;
	private static RankedRetrieval index;
	private static Ranker ranker;

	// zu evaluierende Ergebnislisten (mit/ohne Ranking):
	private static List<Integer> unranked;
	private static List<Integer> ranked;

	// die Evaluation erfolgt gegen einen Goldstandard:
	private static List<Integer> goldstandard;
	private static Evaluation evaluation;

	@BeforeClass
	public static void setup() {
		/*
		 * Zunächst muss alles initialisiert werden:
		 */
		query = "king";
		corpus = new Corpus("pg100.txt", "1[56][0-9]{2}\n");
		index = new InvIndex(corpus);
		ranker = new Ranker(query, index);
		/*
		 * ... dann holen wir uns die Suchergebnisse ...
		 */
		Set<Integer> result = index.search(query);

		unranked = new ArrayList<Integer>(result);
		System.out.println(unranked.size() + " ungewichtete Ergebnisse für " + query);
		print(unranked);

		ranked = ranker.rank(result);
		System.out.println(ranked.size() + " gewichtete Ergebnisse für " + query);
		print(ranked);
		/*
		 * ... die gegen einen Goldstandard evaluiert werden. Für die Übung beschränken wir uns auf eine Liste von
		 * Dokumenten für eine query:
		 */
		goldstandard = GoldStandard.create(index, query);
		System.out.println("Goldstandard: " + goldstandard.size() + " relevante Dokumente für Query '" + query + "'");
		print(goldstandard);

		/* und initialisieren damit die Evaluation: */
		evaluation = new Evaluation(goldstandard);
	}

	// was jetzt noch fehlt: Precision, Recall, F-Maß...

	@Test
	public void evalUnranked() {
		/* Wir evaluieren zunächst das Ergebnis gegen den Goldstandard. */
	}

	@Test
	public void evalRanked() {
		/* Nun das gewichtete Ergebnis gegen den Goldstandard. */
	}

	@Test
	public void evalRankedTop() {
		/* Nun nur die k besten Ergebnisse ... */
	}

	@Test
	public void evalRankedMultiK() {
		System.out.println("Multiresult, ranked:");
		/*
		 * In der Praxis ist es oft nützlich, eine Art Experimentaufbau zu definieren, in der Art: 'Probiere alle k von
		 * 5 bis 15 und gib die Ergebnisse aus' (hier einfach in der Konsole, doch stattdessen könnte man das ganze auch
		 * tabellarisch in eine Datei schreiben und so verschiedene Aufbauten in verschiedenen Files speichern etc.).
		 */
	}

	@Test
	public void evalUnrankedMultiK() {
		System.out.println("Multiresult, unranked:");
	}

	/*
	 * Hilfsmethode für die Ausgabe der Ergebnislisten (inkl. Goldstandard)
	 */
	private static void print(List<Integer> resultList) {
		System.out.println("-------------------------------");
		for (Integer docId : resultList) {
			System.out.println(corpus.getWorks().get(docId));
		}
		System.out.println("-------------------------------");
	}

}
