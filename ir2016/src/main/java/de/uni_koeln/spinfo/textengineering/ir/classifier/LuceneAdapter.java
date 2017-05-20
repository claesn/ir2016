/**
 *  Material for the course 'Text-Engineering', University of Cologne.
 * (http://www.spinfo.uni-koeln.de/spinfo-textengineering.html)
 * <p/>
 * Copyright (C) 2015 Claes Neuefeind
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_koeln.spinfo.textengineering.ir.classifier;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.classification.ClassificationResult;
import org.apache.lucene.classification.Classifier;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;


/**
 * Adapter for Lucene classifiers.
 * 
 * @author Claes Neuefeind
 */
public class LuceneAdapter implements ClassifierStrategy {
	/* Adapter für das Lucene-eigene Interface 'Classifier': */
	private Classifier<BytesRef> classifier;
	/* Lucene-Classifier operieren immer auf einem Index: */
	private String indexDir;

	/**
	 * @param luceneClassifier
	 *            the Lucene classifier to adapt
	 * @param indexDir
	 *            the relative path to index dir
	 * @throws IOException
	 */
	public LuceneAdapter(Classifier<BytesRef> classifier, String indexdir) throws IOException {
		this.classifier = classifier;
		this.indexDir = indexdir;
		// Das Training erfolgt auf dem gesamten Index:
		trainClassifier();
	}

	/**
	 * @param luceneClassifier
	 *            the Lucene classifier to adapt
	 * @param indexDir
	 *            the relative path to index dir
	 * @param filterQuery
	 *            searchPhrase to filter training documents
	 * @throws IOException
	 * @throws ParseException
	 */
	public LuceneAdapter(Classifier<BytesRef> classifier, String indexdir, String filterQuery) throws IOException {
		this.classifier = classifier;
		this.indexDir = indexdir;
		// Training auf allen Dokumenten, die auf query matchen:
		trainClassifier(filterQuery);
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.textengineering.tm.classification.ClassifierStrategy#train(de.uni_koeln.spinfo.textengineering.tm.document.Document)
	 */
	public ClassifierStrategy train(IRDocument document) {
		/*
		 * Leere Dummy-Methode: Training erfolgt in Lucene nicht dokumentweise, sondern auf dem Index.
		 */
		return this;
	}
	
	private void trainClassifier() throws IOException{
		Searcher searcher = new Searcher(indexDir);
		LeafReader reader = SlowCompositeReaderWrapper.wrap(searcher.getReader());
		System.out.println(this+", training with " + searcher.getReader().numDocs() + " docs...");
		classifier.train(reader, "text", "topic", new StandardAnalyzer());
	}

	private void trainClassifier(String filterQuery) throws IOException{
		Searcher searcher = new Searcher(indexDir);
		LeafReader reader = SlowCompositeReaderWrapper.wrap(searcher.getReader());
		Query q = new TermQuery(new Term("root",filterQuery));
		int totalHits = new IndexSearcher(reader).search(q, reader.numDocs()).totalHits;
		System.out.println(this+", training with " + totalHits + " docs filtered by query: " + q);
		classifier.train(reader, "text", "topic", new StandardAnalyzer(), q);
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.textengineering.tm.classification.ClassifierStrategy#classify(de.uni_koeln.spinfo.textengineering.tm.document.Document)
	 */
	public String classify(IRDocument document) {
		try {
			ClassificationResult<BytesRef> result = classifier.assignClass(document.getContent());
			BytesRef assignedClass = result.getAssignedClass();
			printAssignments(document, result);//optional
			return assignedClass.utf8ToString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void printAssignments(IRDocument document, ClassificationResult<BytesRef> c) {
		System.out.println("doc: "+document.getURI());
		System.out.println("class: "+c.getAssignedClass().utf8ToString());
		System.out.println("score: "+c.getScore());
		System.out.println("---------");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("%s for %s", getClass().getSimpleName(), classifier.getClass().getSimpleName());
	}

}
