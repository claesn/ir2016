/**
 * 
 */
package de.uni_koeln.spinfo.textengineering.ir.boole;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;

/**
 * @author spinfo
 *
 */
public class TestBooleanIR {

	private static Corpus corpus;

	@BeforeClass
	public static void setUp() throws Exception {
		// Korpus einlesen und in Werke unterteilen:
		String filename = "pg100.txt";
		String delimiter = "1[56][0-9]{2}\n";
		corpus = new Corpus(filename, delimiter);
	}

	@Test
	public void testMatrix() {
		// TODO Aufbau der Matrix testen
	}

	@Test
	public void testMatrixSearch() {
		// TODO Testen, ob Suche in Term-Dokument-Matrix ein Ergebnis liefert:

	}

}
