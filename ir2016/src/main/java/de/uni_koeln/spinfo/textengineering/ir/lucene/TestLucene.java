package de.uni_koeln.spinfo.textengineering.ir.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.shakespeare.ShakespeareCorpus;

public class TestLucene {

	private static Corpus corpus;
	private static String query;
	// Das Verzeichnis, in das Lucene den Index speichert und von wo aus er abgefragt werden kann:
	private static String luceneDir;

	@BeforeClass
	public static void setUp() throws Exception {
		// A.1 Acquire content
		corpus = new ShakespeareCorpus("pg100.txt", "1[56][0-9]{2}\n");
		/* Speicherort für den Lucene-Index: */
		luceneDir = "index/";
		/* Das Suchwort: */
		query = "brutus";
	}

	@Before
	public void printSkip() {
		System.out.println();
	}

	@Test
	public void testIndexer() throws IOException {
		/*
		 * Der Indexer erstellt einen Lucene-Index im angegebenen Verzeichnis.
		 */
		Indexer indexer = new Indexer(luceneDir);
		/* Wir prüfen zunächst, ob mit dem Korpus alles in Ordnung ist ... */
		assertEquals("Corpus sollte genau 38 Dokumente enthalten", 38, corpus.getDocuments().size());
		/* ... und erstellen uns dann einen Index für unser Korpus: */
		indexer.index(corpus);
		/* Wenn alles ok ist, sollten nun genau die 38 Docs im Index sein: */
		assertEquals("Index sollte der Korpusgröße entsprechen", corpus.getDocuments().size(), indexer.getNumberOfDocs());
		indexer.close();
	}

	@Test
	public void testSearcher() throws IOException, ParseException {
		/*
		 * Der Searcher ist das Gegenstück zum Indexer. Die Searcher-Klasse enthält einen IndexSearcher, der auf den
		 * oben erstellten (bzw. einen beliebigen) Index im entsprechenden Verzeichnis angesetzt wird:
		 */
		Searcher searcher = new Searcher(luceneDir);
		/* Wir prüfen zunächst, ob mit dem Index alles in Ordnung ist ... */
		assertEquals("Index sollte genau 38 Dokumente enthalten", 38, searcher.indexSize());
		/* ... und schicken dann die Suche ab, wobei wir die maximale Trefferzahl mit angeben: */
		searcher.search(query, 20);
		/* Wenn alles ok ist, sollten nun genau die 38 Docs im Index sein: */
		assertTrue("Suchergebnis sollte nicht leer sein", searcher.totalHits() > 0);
		searcher.close();
	}

	@Test
	public void testIndex() throws ParseException, IOException {

		Searcher searcher = new Searcher(luceneDir);
		int noOfHits = 10;// Länge der Ergebnisliste

		/* Suche nach einem String: */
		searcher.search("heute", "text", noOfHits);
		assertTrue("Das Suchergebnis sollte nicht leer sein.", searcher.totalHits() > 0);

		/* Topic-Suche: */
		searcher.search("sport", "topic", noOfHits);
		assertTrue("Das Suchergebnis sollte nicht leer sein.", searcher.totalHits() > 0);

		/* Suche nach Dokumenten aus einer bestimmten Quelle: */
		searcher.search("spiegel", "source", noOfHits);
		assertTrue("Das Suchergebnis sollte nicht leer sein.", searcher.totalHits() > 0);

		/*
		 * Source-Suche unter Verwendung der beim Indexieren extrahierten
		 * root-URL:
		 */
		searcher.search("spiegel", "root", noOfHits);
		assertTrue("Das Suchergebnis sollte nicht leer sein.", searcher.totalHits() > 0);
	}

}
