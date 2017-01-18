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

	// für Ranking: k und eine "range"
	int k = 10;
	private int K_START = 5;
	private int K_END = 20;

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
		EvaluationResult evaluate = evaluation.evaluate(unranked);
		assertTrue("F-Maß darf nicht 0 sein!", evaluate.f > 0);
		System.out.println("Unranked, alle: " + evaluate);
	}

	@Test
	public void evalRanked() {
		/* Nun das gewichtete Ergebnis gegen den Goldstandard. */
		EvaluationResult evaluate = evaluation.evaluate(ranked);
		assertTrue("F-Maß darf nicht 0 sein!", evaluate.f > 0);
		System.out.println(evaluate);
	}

	@Test
	public void evalRankedTop() {
		/* Nun nur die k besten Ergebnisse ... */
		EvaluationResult evalRanked = evaluation.evaluate(ranked.subList(0, k));
		EvaluationResult evalUnranked = evaluation.evaluate(unranked.subList(0, k));

		assertTrue("Evaluation der besten k sollte das Ergebnis verbessern.", evalRanked.f > evalUnranked.f);

		System.out.println("Unranked, top: " + evalUnranked);
		System.out.println("Ranked, top: " + evalRanked);
	}

	@Test
	public void evalRankedMultiK() {
		System.out.println("Multiresult, ranked:");
		/*
		 * In der Praxis ist es oft nützlich, eine Art Experimentaufbau zu definieren, in der Art: 'Probiere alle k von
		 * 5 bis 15 und gib die Ergebnisse aus' (hier einfach in der Konsole, doch stattdessen könnte man das ganze auch
		 * tabellarisch in eine Datei schreiben und so verschiedene Aufbauten in verschiedenen Files speichern etc.).
		 */
		for (int i = K_START; i < K_END; i++) {
			EvaluationResult evalRanked = evaluation.evaluate(ranked.subList(0, i));
			System.out.println(evalRanked + " k=" + i);
		}
	}

	@Test
	public void evalUnrankedMultiK() {
		System.out.println("Multiresult, unranked:");
		/* Für jedes k von kStart bis kEnd evaluieren und das Ergebnis ausgeben: */
		for (int i = K_START; i < K_END; i++) {
			EvaluationResult evalRanked = evaluation.evaluate(unranked.subList(0, i));
			System.out.println("unranked, top: " + evalRanked + " k=" + i);
		}
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
