package de.uni_koeln.spinfo.textengineering.ir.basic;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.model.shakespeare.ShakespeareCorpus;

public class TestBasicIR {

	private static Corpus corpus;
	private LinearSearch searcher;
	private String query;

	@BeforeClass
	public static void setUp() throws Exception {
		// Korpus einlesen und in Werke unterteilen:
		String filename = "pg100.txt";
		String delimiter = "1[56][0-9]{2}\n";
		corpus = new ShakespeareCorpus(filename, delimiter);
	}

	@Test
	public void testCorpus() throws Exception {
		// Testen, ob Korpus korrekt angelegt wurde:
		List<IRDocument> documents = corpus.getDocuments();
		assertTrue("Korpus sollte mehr als 1 Werk enthalten", documents.size() > 1);
	}

	@Test
	public void testSearch() throws Exception {
		// Testen, ob die Suche funktioniert

		System.out.println();
		System.out.println("Lineare Suche:");
		System.out.println("-------------------");

		searcher = new LinearSearch(corpus);
		query = "HAMLET,";
		Set<Integer> result = searcher.search(query);
		assertTrue("Es sollte genau ein Treffer sein, es sind jedoch " + result.size(), result.size() == 1);
		int id = result.iterator().next();
		IRDocument document = corpus.getDocuments().get(id);
		assertTrue("Titel sollte '" + query + "' enthalten", document.getTitle().contains(query));
		System.out.println(result);
		System.out.println(document);
	}
	
	@Test
	public void testMultiWordSearch() throws Exception {
		// Testen für mehrere Wörter

		LinearSearch searcher = new LinearSearch(corpus);
		String query = "Brutus Caesar";
		Set<Integer> result = searcher.search(query);
		assertTrue("Ergebnis darf nicht leer sein", result.size() > 0);
		System.out.println(result);
	}

}