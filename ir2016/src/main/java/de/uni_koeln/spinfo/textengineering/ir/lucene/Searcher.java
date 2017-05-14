package de.uni_koeln.spinfo.textengineering.ir.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class Searcher {

	private IndexSearcher searcher;
	private DirectoryReader reader;
	int totalHits;

	public Searcher(String luceneDir) throws IOException {
		/* Das Verzeichnis, in dem sich der Index befindet: */
		Directory indexDir = new SimpleFSDirectory(new File(luceneDir).toPath());
		/* Der Reader ist für den Lese-Zugriff auf das Index-Verzeichnis zuständig: */
		reader = DirectoryReader.open(indexDir);
		/* Der IndexSearcher ist im Wesentlichen ein Wrapper um den Reader: */
		searcher = new IndexSearcher(reader);
	}

	public void search(String searchPhrase, int noOfHits) throws IOException, ParseException {

		System.out.println("Search for " + searchPhrase);
		/*
		 * B.2: build query - Analog zur Indexierung können wir hier einen Preprocessor nutzen (QueryParser):
		 */
		
		QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
		Query query = parser.parse(searchPhrase);
		/*
		 * ... oder uns eine Query selbst bauen (Lucene stellt eine Reihe von versch. Query-Typen bereit):
		 */
		// query = new TermQuery(new Term("contents",searchPhrase));
		// query = new PrefixQuery(new Term("contents",searchPhrase));
		// query = new FuzzyQuery(new Term("contents",searchPhrase), 1);
		// ...

		System.out.println("Lucene-Query: " + query);
		/*
		 * B.3: Search query - Lucene stellt verschiedene search-Methoden bereit, die in der Regel ein gewichtetes
		 * Ergebnis zurückgeben.
		 */
		TopDocs topDocs = searcher.search(query, noOfHits);
		totalHits = topDocs.totalHits;// für Assertions in Tests
		System.out.println(totalHits + " Treffer für Suche nach " + searchPhrase);
		/*
		 * B.4: Render results - Das Gegenstück zur buildDocument()-Methode beim Indexieren: Je nachdem, was dort
		 * definiert wurde, können hier die Felder einzeln angesprochen und ausgelesen werden.
		 */
		renderResults(topDocs);
	}

	private void renderResults(TopDocs topDocs) throws IOException {
		/*
		 * Analog zum Erstellen von Dokumenten im Indexer können wir hier die enthaltenen Felder ausgeben:
		 */
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("Titel: " + doc.get("title"));
		}
	}

	/*
	 * Beim Umgang mit Ressourcen ist es immer gut, diese explizit freizugeben.
	 */
	public void close() throws IOException {
		reader.close();
	}

	/*
	 * Hilfsmethode für Assertions in unseren Tests
	 */
	public int indexSize() {
		return reader.maxDoc();
	}

	/*
	 * Hilfsmethode für Assertions in unseren Tests
	 */
	public int totalHits() {
		return totalHits;
	}

}
