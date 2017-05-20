package de.uni_koeln.spinfo.textengineering.ir.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.NewsCorpus;

public class TestLuceneWithNewsCorpus {

	private static Corpus corpus;
	private static String query;
	// Das Verzeichnis, in das Lucene den Index speichert und von wo aus er
	// abgefragt werden kann:
	private static String luceneDir;

	@BeforeClass
	public static void setUp() throws Exception {
		// A.1 Acquire content
		corpus = new NewsCorpus("texte/");
		/* Speicherort für den Lucene-Index: */
		luceneDir = "index-news/";
		/* Erstellen des Index: */
		Indexer indexer = new Indexer(luceneDir);
		/* ... und erstellen uns dann einen Index für unser Korpus: */
		indexer.index(corpus);
		indexer.close();
	}

	@Before
	public void printSkip() {
		System.out.println();
	}

	@Test
	public void testSearcher() throws IOException, ParseException {

		// für den Funktionstest suchen wir nach "Spiegel" 
		query = "spiegel";
		/*
		 * Der Searcher ist das Gegenstück zum Indexer. Die Searcher-Klasse
		 * enthält einen IndexSearcher, der auf den oben erstellten (bzw. einen
		 * beliebigen) Index im entsprechenden Verzeichnis angesetzt wird:
		 */
		Searcher searcher = new Searcher(luceneDir);
		/* Wir prüfen zunächst, ob mit dem Index alles in Ordnung ist ... */
		assertEquals("Index sollte alle Dokumente des Korpus enthalten", corpus.getDocuments().size(),
				searcher.indexSize());
		/*
		 * ... und schicken dann die Suche ab, wobei wir die maximale
		 * Trefferzahl mit angeben:
		 */
		List<IRDocument> result = searcher.search(query, 5);
		/* Wenn alles ok ist, sollten nun genau die 38 Docs im Index sein: */
		assertTrue("Suchergebnis sollte nicht leer sein", searcher.totalHits() > 0);
		for (IRDocument irDocument : result) {
			System.out.println(irDocument);
		}
		searcher.close();
	}

	@Test
	public void testIndex() throws ParseException, IOException {

		System.out.println("Beispiele für spezialisierte Suchanfragen:");
		System.out.println("------------------------------------------");

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
