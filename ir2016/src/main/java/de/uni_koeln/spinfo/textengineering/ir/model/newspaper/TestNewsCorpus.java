package de.uni_koeln.spinfo.textengineering.ir.model.newspaper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Indexer;
import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

public class TestNewsCorpus {

	private static NewsCorpus corpus;
	private static String luceneDir;
	private static String query;

	@BeforeClass
	public static void setUp() throws Exception {
		// set up corpus
		corpus = new NewsCorpus("texte/");
		/* Speicherort für den Lucene-Index: */
		luceneDir = "index/";
		query = "Cannes";
	}

	@Test
	public void testCorpus() throws Exception {
		// Wir Testen, ob das Korpus korrekt angelegt wurde:
		List<IRDocument> documents = corpus.getDocuments();
		assertTrue("Korpus sollte mehr als 1 Werk enthalten", documents.size() > 1);
		System.err.println("Texte im Korpus: " + documents.size());
	}

	@Test
	public void testIndexer() throws Exception {
		/* Der Indexer erstellt einen Lucene-Index im angeg. Verzeichnis. */
		Indexer indexer = new Indexer(luceneDir);
		/* Wir prüfen zunächst, ob mit dem Korpus alles in Ordnung ist ... */
		assertTrue("Corpus sollte mindestens 1 Dokument enthalten", corpus.getDocuments().size() > 0);
		/* ... und erstellen uns dann einen Index für unser Korpus: */
		indexer.index(corpus);
		/* Index sollte genau der Korpusgröße entsprechen: */
		assertEquals("Index sollte der Korpusgröße entsprechen", corpus.getDocuments().size(),
				indexer.getNumberOfDocs());
		System.err.println("Texte im Index: " + indexer.getNumberOfDocs());
		indexer.close();
	}

	@Test
	public void testSearcher() throws IOException, ParseException {
		/*
		 * Der Searcher ist das Gegenstück zum Indexer. Die Searcher-Klasse
		 * enthält einen IndexSearcher, der auf den oben erstellten (bzw. einen
		 * beliebigen) Index im entsprechenden Verzeichnis angesetzt wird:
		 */
		Searcher searcher = new Searcher(luceneDir);
		/* Wir prüfen zunächst, ob mit dem Index alles in Ordnung ist ... */
		assertEquals("Index sollte der Korpusgröße entsprechen", corpus.getDocuments().size(), searcher.indexSize());
		/* ... und geben dann bei der Suche die maximale Trefferzahl mit an */
		searcher.search(query, 20);
		assertTrue("Suchergebnis sollte nicht leer sein", searcher.totalHits() > 0);
		System.err.println(searcher.totalHits() + " Treffer für Suche nach '" + query+"'");
		searcher.close();
	}

}
