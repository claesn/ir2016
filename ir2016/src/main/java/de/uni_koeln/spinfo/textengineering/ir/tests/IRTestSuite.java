package de.uni_koeln.spinfo.textengineering.ir.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.uni_koeln.spinfo.textengineering.ir.basic.TestBasicIR;
import de.uni_koeln.spinfo.textengineering.ir.boole.TestBooleanIR;
import de.uni_koeln.spinfo.textengineering.ir.eval.TestEvaluation;
import de.uni_koeln.spinfo.textengineering.ir.lucene.TestLucene;
import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.TestNewsCorpus;
import de.uni_koeln.spinfo.textengineering.ir.ranked.TestRankedIR;
import de.uni_koeln.spinfo.textengineering.ir.tolerant.TestTolerantRetrieval;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestBasicIR.class, TestBooleanIR.class, TestEvaluation.class, TestLucene.class,
		TestNewsCorpus.class, TestRankedIR.class, TestTolerantRetrieval.class })
public class IRTestSuite {

}
