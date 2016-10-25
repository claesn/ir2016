package de.uni_koeln.spinfo.textengineering.ir.basic;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestBasicIR {

	private static Corpus corpus;

	@BeforeClass
	public static void setUp() throws Exception {
		// Korpus einlesen und in Werke unterteilen:
		String filename = "pg100.txt";
		String delimiter = "1[56][0-9]{2}\n";
		corpus = new Corpus(filename, delimiter);
	}

	@Test
	public void testCorpus() throws Exception {
		// Testen, ob Korpus korrekt angelegt wurde:
		List<Work> works = corpus.getWorks();
		assertTrue("Korpus sollte mehr als 1 Werk enthalten", works.size() > 1);
	}

	// TODO Suche
}