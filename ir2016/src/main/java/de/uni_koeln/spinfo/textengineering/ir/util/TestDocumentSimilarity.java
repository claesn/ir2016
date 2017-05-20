package de.uni_koeln.spinfo.textengineering.ir.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.classification.KNearestNeighborClassifier;
import org.apache.lucene.classification.SimpleNaiveBayesClassifier;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.textengineering.ir.classifier.LuceneAdapter;
import de.uni_koeln.spinfo.textengineering.ir.lucene.Indexer;
import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.NewsCorpus;
import de.uni_koeln.spinfo.textengineering.ir.ranked.InvIndex;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;

public class TestDocumentSimilarity {

	private static Corpus corpus;
	/* Speicherort für den Lucene-Index: */
	private static String luceneDir = "index/";
	private static RankedRetrieval index;
	private static IRDocument refDoc;

	@BeforeClass
	public static void setUp() throws Exception {
		// set up corpus & index
		corpus = new NewsCorpus("texte/");
		index = new InvIndex(corpus);
		Indexer indexer = new Indexer(luceneDir);
		indexer.index(corpus);
		indexer.close();
	}

	@Before
	public void printSep(){
		System.out.println("-------");
	}
	
	@Test
	public void testSimilarity() {

		IRDocument queryDoc = Parser.parseNewsDocument(new File("texte/10-politik.xml"));
		IRDocument refDoc = Parser.parseNewsDocument(new File("texte/100-karriere.xml"));

		System.out.println(queryDoc);
		System.out.println(refDoc);

		Double similarity = IRUtils.similarity(queryDoc, refDoc, index);
		System.out.println("cosine similarity: "+similarity);
	}

	@Test
	public void testMoreLikeThis() {

		// wir können ein Dokument aus dem Korpus nehmen:
		refDoc = corpus.getDocuments().get(0);
		// oder gezielt aus dem verzeichnis holen:
		refDoc = Parser.parseNewsDocument(new File("texte/119-auto.xml"));
		//oder eine (fast) beliebige URL parsen:
		refDoc = Parser.parse("https://www.welt.de/politik/ausland/article164754026/Polens-neue-Ausrede-in-der-Fluechtlingsfrage.html");

		System.out.println(refDoc);

		// und für dieses Dokument die ähnlichsten ermitteln
		List<IRDocument> mostSimilar = IRUtils.getMostSimilar(refDoc, index, 10);
		for (IRDocument d : mostSimilar) {
			System.out.println(d);
		}
	}

	@Test
	public void testTopicAssignment() throws IOException {

		// ... für ein Dokument aus dem Korpus:
		refDoc = corpus.getDocuments().get(0);
		// oder aus dem verzeichnis:
		refDoc = Parser.parseNewsDocument(new File("texte/119-auto.xml"));
		//oder indem wir eine (fast) beliebige URL parsen:
		refDoc = Parser.parse("https://www.welt.de/politik/ausland/article164754026/Polens-neue-Ausrede-in-der-Fluechtlingsfrage.html");
		
		// wir trainieren den classifier auf allen dokumenten
		LuceneAdapter luceneAdapter = new LuceneAdapter(new KNearestNeighborClassifier(3), luceneDir);
		String topic = luceneAdapter.classify(refDoc);

		// oder wir trainieren nur auf Spiegel-Dokumenten
		luceneAdapter = new LuceneAdapter(new KNearestNeighborClassifier(3), luceneDir, "spiegel");
		// ... und dann das topic geben lassen
		topic = luceneAdapter.classify(refDoc);

		// und nochmal mit einem anderen classifier:
		luceneAdapter = new LuceneAdapter(new SimpleNaiveBayesClassifier(), luceneDir, "spiegel");
		// ... und wieder das topic geben lassen
		topic = luceneAdapter.classify(refDoc);
	}
}
