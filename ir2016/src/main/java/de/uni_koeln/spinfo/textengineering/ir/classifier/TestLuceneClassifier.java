/**
 * Material for the course 'Text-Engineering', University of Cologne.
 * (http://www.spinfo.uni-koeln.de/spinfo-textengineering.html)
 * <p/>
 * Copyright (C) 2015 Claes Neuefeind
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_koeln.spinfo.textengineering.ir.classifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.classification.KNearestNeighborClassifier;
import org.apache.lucene.classification.SimpleNaiveBayesClassifier;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Indexer;
import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.NewsCorpus;

/**
 * Material for the course 'Text-Engineering', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textengineering.html)
 * 
 * @author Claes Neuefeind
 */
/*
 * Vergleichende Textklassifikation: Nutzung der lucene-classification-API.
 */
public class TestLuceneClassifier {
	private static final String LINE = "------------------------------------------------------------------------";
	private de.uni_koeln.spinfo.textengineering.ir.model.Corpus corpus;
	private Set<IRDocument> testSet;
	private Set<IRDocument> trainingSet;
	private ArrayList<IRDocument> goldSet;
	private TextClassifier textClassifier;

	// für Lucene:
	private static final String indexDir = "index-tm";
	Searcher searcher;// für Index-Zugriffe (hier nur für den entsprechenden
						// Test)

	@Before
	public void before() throws Exception {
		/* Hier (vor jedem Test) nur öffnen: */
		corpus = new NewsCorpus("texte/");
		Indexer indexer = new Indexer(indexDir);
		indexer.index(corpus);
		indexer.close();

		System.out.println("Korpus enthält " + corpus.getDocuments().size() + " Dokumente");
		try {
			/* ... und mittels Searcher Zugriff auf den Index gestatten: */
			searcher = new Searcher(indexDir);
			System.out.println("Index enthält " + searcher.getReader().numDocs() + " Dokumente");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(LINE);
	}

	@After
	public void after() {
		/* Hier (nach jedem Test) schliessen. */
		try {
			searcher.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void spiegel() throws IOException, ParseException, URISyntaxException {

		String query = "spiegel";
		setupData(query);

		// der LuceneAdapter delegiert die Aufgabe an die Lucene-Api:
		testEval(System.nanoTime(), query, new LuceneAdapter(new KNearestNeighborClassifier(1), indexDir, query));
		System.out.println(LINE);
		// ... in der es zwei Classifier gibt
		testEval(System.nanoTime(), query, new LuceneAdapter(new SimpleNaiveBayesClassifier(), indexDir, query));
		System.out.println(LINE);

	}

	private void setupData(final String query) throws IOException, ParseException {
		/*
		 * Im Beispiel trainieren und klassifizieren wir mit den gleichen
		 * Dokumenten und nehmen diese auch noch als Goldstandard an. Eine
		 * "richtige" Evaluation sieht getrennte Mengen vor, die mehrfach und in
		 * verschiedenen Zusammensetzungen gegen den Goldstandard getestet
		 * werden (Cross-Validation).
		 */
//		List<IRDocument> result = searcher.search("spiegel", "root", searcher.indexSize());
		
		trainingSet = new HashSet<IRDocument>(corpus.getDocuments());
		testSet = trainingSet;
		goldSet = new ArrayList<IRDocument>(testSet);

		System.out.println("Classification of documents from: " + query + "... ");
//		System.out.println(LINE);
//		System.out.println("Training set: " + trainingSet.size());
//		System.out.println("Test set: " + testSet.size());
//		System.out.println("Gold set: " + goldSet.size());
//		System.out.println(LINE);
	}

	private void testEval(final long start, final String query, final ClassifierStrategy classifier) {

		// Der Classifier ...
		System.out.print(classifier + "... ");
		// ... wird trainiert ...
		textClassifier = new TextClassifier(classifier, trainingSet);
		// ... und eingesetzt:
		Map<IRDocument, String> resultClasses = textClassifier.classify(testSet);
		// ... Ergebnis ausgeben:
		System.out.println("Result: " + resultClasses);
		Double result = textClassifier.evaluate(resultClasses, goldSet);
		Assert.assertTrue("Result must not be null", result != null);
		// ... Zeitmessung aufbereiten:
		long ns = System.nanoTime() - start;
		double ms = ns / 1000d / 1000d;
		double s = ms / 1000d;
		System.out.println(
				String.format("Correct: %1.2f (%1.2f%%); Time: %1.2f ms (%1.2f s.)", result, result * 100, ms, s));
	}

}
