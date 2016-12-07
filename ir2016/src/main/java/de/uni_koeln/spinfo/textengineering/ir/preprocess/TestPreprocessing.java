/**
 * 
 */
package de.uni_koeln.spinfo.textengineering.ir.preprocess;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * @author spinfo
 *
 */
public class TestPreprocessing {

	private String testString = 
			  "To be, or not to be- that is the question: "
			+ "Whether 'tis nobler in the mind to suffer " 
			+ "The slings and arrows of outrageous fortune "
			+ "Or to take arms against a sea of troubles, " 
			+ "And by opposing end them.";

	@Test
	public void testPreprocessing() {
		System.out.println("Test Tokenizer:");
		System.out.println("-------------------");

		Preprocessor p = new Preprocessor();
		List<String> tokens = p.tokenize(testString);
		List<String> terms = p.getTerms(testString);
		List<String> stems = p.getStems(testString);
		
		// Assertions, z.B.
		assertTrue("Mehr Tokens als Terms erwartet!", tokens.size() >= terms.size());
		System.out.println("tokens:\t"+tokens);
		System.out.println("terms:\t"+terms);
		
		assertTrue("Mehr Terme als Stems erwartet!", terms.size() >= stems.size());
		System.out.println("stems:\t"+stems);
	}

}
