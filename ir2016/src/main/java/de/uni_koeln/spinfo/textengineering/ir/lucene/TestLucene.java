package de.uni_koeln.spinfo.textengineering.ir.lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;

public class TestLucene {

	private Corpus corpus;
	private String query;
	// Das Verzeichnis, in das Lucene den Index speichert und von wo aus er abgefragt werden kann:
	private String luceneDir;

	@BeforeClass
	public void setUp() throws Exception {
		// A.1 Acquire content
		corpus = new Corpus("pg100.txt", "1[56][0-9]{2}\n");
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
		assertEquals("Corpus sollte genau 38 Dokumente enthalten", 38, corpus.getWorks().size());
		/* ... und erstellen uns dann einen Index für unser Korpus: */
		indexer.index(corpus);
		/* Wenn alles ok ist, sollten nun genau die 38 Docs im Index sein: */
		assertEquals("Index sollte der Korpusgröße entsprechen", corpus.getWorks().size(), indexer.getNumberOfDocs());
	}
	
	@Test
	public void testSearcher(){
		/*
		 * TODO Suche im Index
		 */
		
	}

	
}
