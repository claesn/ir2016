package de.uni_koeln.spinfo.textengineering.ir.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

public class Indexer {

	// das Herzstück der Lucene-Indexierung ist der sog. IndexWriter:
	private IndexWriter writer;

	public Indexer(String luceneDir) throws IOException {
		/* Das Verzeichnis, in dem der Index gespeichert wird: */
		Directory indexDir = new SimpleFSDirectory(new File(luceneDir).toPath());
		/* Der Analyzer ist für das Preprocessing zuständig (Tokenizing etc) */
		Analyzer analyzer = new StandardAnalyzer();
		/* Der IndexWriter wird mit dem Analyzer konfiguriert: */
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(indexDir, config);
	}

	/*
	 * Kapselt die Schritte A.1 bis A.4
	 */
	public void index(Corpus corpus) throws IOException {

		System.out.print("Indexing corpus ");
		/*
		 * Wir erstellen hier jedesmal einen frischen Index. Alternativ würde
		 * man zunächst schauen, ob ein Index existiert und dann vor der
		 * Indexierung eines Dokuments schauen, ob es bereits enthalten ist.
		 */
		writer.deleteAll();
		/*
		 * Schritt A.1 (acquire content) ist hier im Grunde bereits abgehakt.
		 * Dass wir hier einfach unser Corpus übergeben ist dabei ein
		 * Spezialfall. Der allgemeinere Fall: Lesen von Textdateien aus einem
		 * Verzeichnis.
		 */
		List<IRDocument> documents = corpus.getDocuments();
		for (IRDocument document : documents) {
			/*
			 * Schritt A.2: build document (alternativ könnten wir die
			 * Lucene-Documents auch schon direkt in der Corpus-Klasse
			 * erstellen, dann wären die Schritte A.1 und A.2 hier bereits
			 * abgehakt).
			 */
			Document doc = buildDocument(document);
			/*
			 * ... der Writer übernimmt dafür die Schritte A.3 und A.4 (analyze
			 * + index document)
			 */
			writer.addDocument(doc);
			System.out.print(".");
		}
		System.out.println(" " + writer.numDocs() + " Dokumente indexiert.");
	}

	/*
	 * Schritt A.2: 'build document' - Die Klasse Document ist ein Container für
	 * sog. 'Fields', welche die eigentlichen Daten kapseln. Strukturell ähnelt
	 * ein Field einer Map<Key, Value>, d.h. auf einen Key (ID) wird ein Value
	 * (textuelle Daten) abgebildet.
	 */
	private Document buildDocument(IRDocument irDoc) {

		// TODO: Use reflexion api to indicate fields

		String topic = irDoc.getTopic();
		URI uri = irDoc.getURI();
		Document document = new Document();
		document.add(new TextField("title", irDoc.getTitle(), Store.YES));
		document.add(new TextField("text", irDoc.getContent(), Store.YES));
		if (topic != null)
			document.add(new TextField("topic", topic, Store.YES));
		if (uri != null) {
			document.add(new TextField("source", uri.toString(), Store.YES));
			/*
			 * Die jeweilige Root-URL dient hier als Alternative zu einer
			 * 'contains'-Suche mit Wilcard-Queries (da diese extrem
			 * rechenintensiv ist - eine aus Lucene-Sicht sauberere Lösung wäre
			 * z.B. die Verwendung eines NGramTokenizer auf das source-Feld).
			 */
			document.add(new StringField("root", extractUrlRoot(irDoc), Store.YES));
		}
		return document;
	}

	/* Hilfsmethode zur Ermittlung der URL-root. */
	private String extractUrlRoot(IRDocument document) {
		Pattern pattern = Pattern.compile("http://[a-z]*.([^/]+?).[a-z]*/.*");
		Matcher m = pattern.matcher(document.getURI().toString());
		if (m.find()) {
			return m.group(1);
		}
		return document.getURI().toString();
	}

	/*
	 * Hilfsmethode für unseren Test.
	 */
	public int getNumberOfDocs() {
		return writer.maxDoc();
	}

	/*
	 * Beim Umgang mit Ressourcen ist es immer gut, diese explizit freizugeben.
	 */
	public void close() throws IOException {
		writer.close();
	}

	public void addAll(List<? extends IRDocument> docs) throws IOException {
		for (IRDocument document : docs) {
			writer.addDocument(buildDocument(document));
		}
	}

	public void deleteAll() throws IOException {
		writer.deleteAll();
	}

}
