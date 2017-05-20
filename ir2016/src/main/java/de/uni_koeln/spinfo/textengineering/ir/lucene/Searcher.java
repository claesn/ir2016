package de.uni_koeln.spinfo.textengineering.ir.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.NewsDocument;

public class Searcher {

	private IndexSearcher searcher;
	private DirectoryReader reader;
	int totalHits;

	public Searcher(String luceneDir) throws IOException {
		/* Das Verzeichnis, in dem sich der Index befindet: */
		Directory indexDir = new SimpleFSDirectory(new File(luceneDir).toPath());
		/*
		 * Der Reader ist für den Lese-Zugriff auf das Index-Verzeichnis
		 * zuständig:
		 */
		reader = DirectoryReader.open(indexDir);
		/* Der IndexSearcher ist im Wesentlichen ein Wrapper um den Reader: */
		searcher = new IndexSearcher(reader);
	}

	public List<IRDocument> search(String searchPhrase, int noOfHits) throws IOException, ParseException {

		/*
		 * B.2: build query - Analog zur Indexierung können wir hier einen
		 * Preprocessor nutzen (QueryParser):
		 */

		QueryParser parser = new QueryParser("text", new StandardAnalyzer());
		Query query = parser.parse(searchPhrase);
		/*
		 * ... oder uns eine Query selbst bauen (Lucene stellt eine Reihe von
		 * versch. Query-Typen bereit):
		 */
		// query = new TermQuery(new Term("contents",searchPhrase));
		// query = new PrefixQuery(new Term("contents",searchPhrase));
		// query = new FuzzyQuery(new Term("contents",searchPhrase), 1);
		// ...

		System.out.println("Search for Lucene-Query: " + query);
		/*
		 * B.3: Search query - Lucene stellt verschiedene search-Methoden
		 * bereit, die in der Regel ein gewichtetes Ergebnis zurückgeben.
		 */
		TopDocs topDocs = searcher.search(query, noOfHits);
		totalHits = topDocs.totalHits;// für Assertions in Tests
		// System.out.println(totalHits + " Treffer für Suche nach " +
		// searchPhrase);
		/*
		 * B.4: Render results - Das Gegenstück zur buildDocument()-Methode beim
		 * Indexieren: Je nachdem, was dort definiert wurde, können hier die
		 * Felder einzeln angesprochen und ausgelesen werden.
		 */
		return renderResults(topDocs);
	}

	private List<IRDocument> renderResults(TopDocs topDocs) throws IOException {
		/*
		 * Analog zum Erstellen von Dokumenten im Indexer können wir hier die
		 * enthaltenen Felder ausgeben:
		 */
		List<IRDocument> rendered = new ArrayList<IRDocument>();
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			Document doc = searcher.doc(scoreDoc.doc);
			// System.out.println("Titel: " + doc.get("title"));
			try {
				/*
				 * FIXME Wenn Felder nichts enthalten, kommt null zurück - hier
				 * kaschiert durch leere Strings
				 */
				String topic = doc.get("topic") != null ? doc.get("topic") : "";
				URI uri = doc.get("source") != null ? new URI(doc.get("source")) : new URI("");
				rendered.add(new NewsDocument(topic, doc.get("title"), uri, doc.get("text")));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return rendered;
	}

	/*
	 * Beim Umgang mit Ressourcen ist es wichtig, diese explizit freizugeben.
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

	public DirectoryReader getReader() {
		return reader;
	}

	/*
	 * Anders als im Seminar wird das Index-Feld mit übergeben, um gezielt nach
	 * 'topic' oder 'source' suchen zu können.
	 */
	public List<IRDocument> search(String searchPhrase, String field, int noOfHitsToDisplay)
			throws ParseException, IOException {

		/* Die searchPhrase muss in ein Query-Objekt überführt werden: */
		Query query;
		/*
		 * Im 'Normalfall' können wir hierfür eine einfache TermQuery erstellen
		 * - eine Ausnahme ist das Feld 'source': Um hier eine
		 * "contains()"-Anfrage machen zu können, nutzen wir einen QueryParser,
		 * um die searchPhrase mit 'Wildcards' anzureichern (Achtung: 'leading
		 * wildcards' sind extrem rechenintensiv!).
		 */
		if (!field.equals("source")) {
			query = new TermQuery(new Term(field, searchPhrase));
		} else {
			QueryParser parser = new QueryParser(field, new StandardAnalyzer());
			parser.setAllowLeadingWildcard(true);// Problem hier: Sehr
													// rechenintensiv!
			searchPhrase = "*" + searchPhrase + "*";
			query = parser.parse(searchPhrase);
		}
		System.out.println("query: " + query);

		/*
		 * Anschließend geben wir das Query-Objekt an eine Lucene-eigene
		 * search-Methode weiter:
		 */
		TopDocs topDocs = searcher.search(query, noOfHitsToDisplay);
		totalHits = topDocs.totalHits;
		/* ... und geben das Ergebnis aus: */
		System.out.println(totalHits + " Treffer für " + searchPhrase + " (zeige erste "
				+ Math.min(totalHits, noOfHitsToDisplay) + "):");
		return renderResults(topDocs);
	}

}
