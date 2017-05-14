package de.uni_koeln.spinfo.textengineering.ir.model.newspaper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Indexer;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

public class TestNewsCorpus {

	private static NewsCorpus corpus;
	private static String luceneDir;
	//private static String query;

	@BeforeClass
	public static void setUp() throws Exception {
		// TODO: set up test case
		corpus = new NewsCorpus("texte/");
		/* Speicherort für den Lucene-Index: */
		luceneDir = "index/";
	}

	//zunächst sollte das Korpus getestet werden:

	@Test
	public void testCorpus() throws Exception {
		// Testen, ob Korpus korrekt angelegt wurde:
		List<IRDocument> documents = corpus.getDocuments();
		System.err.println(documents.size());
		assertTrue("Korpus sollte mehr als 1 Werk enthalten", documents.size() > 1);
		System.out.println("Texte im Korpus: "+ documents.size());
	}

	
	@Test
	public void testIndexer() throws IOException {
		/*
		 * Der Indexer erstellt einen Lucene-Index im angegebenen Verzeichnis.
		 */
		Indexer indexer = new Indexer(luceneDir);
		/* Wir prüfen zunächst, ob mit dem Korpus alles in Ordnung ist ... */
//		assertEquals("Corpus sollte genau 38 Dokumente enthalten", 38, corpus.getWorks().size());
		//hier braucht es eine andere assertion ....

		/* ... und erstellen uns dann einen Index für unser Korpus: */
		indexer.index(corpus);
		/* Wenn alles ok ist, sollten nun genau die 38 Docs im Index sein: */
//		assertEquals("Index sollte der Korpusgröße entsprechen", corpus.getWorks().size(), indexer.getNumberOfDocs());
		//und hier braucht es eine andere assertion ...

		indexer.close();
	}

	/*
	 * und dann könnten man auch gleich noch die suche testen...
	 */
	
	
}
