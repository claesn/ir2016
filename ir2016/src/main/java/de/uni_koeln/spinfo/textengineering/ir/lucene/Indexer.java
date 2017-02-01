package de.uni_koeln.spinfo.textengineering.ir.lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

import de.uni_koeln.spinfo.textengineering.ir.basic.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.basic.Work;

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
		 * Wir erstellen hier jedesmal einen frischen Index. Alternativ würde man zunächst schauen, ob ein Index
		 * existiert und dann vor der Indexierung eines Dokuments schauen, ob es bereits enthalten ist.
		 */
		writer.deleteAll();
		/*
		 * Schritt A.1 (acquire content) ist hier im Grunde bereits abgehakt. Dass wir hier einfach unser Corpus
		 * übergeben ist dabei ein Spezialfall. Der allgemeinere Fall: Lesen von Textdateien aus einem Verzeichnis.
		 */
		List<Work> works = corpus.getWorks();
		for (Work work : works) {
			/*
			 * Schritt A.2: build document (alternativ könnten wir die Lucene-Documents auch schon direkt in der
			 * Corpus-Klasse erstellen, dann wären die Schritte A.1 und A.2 hier bereits abgehakt).
			 */
			Document doc = buildDocument(work);
			/*
			 * ... der Writer übernimmt dafür die Schritte A.3 und A.4 (analyze + index document)
			 */
			writer.addDocument(doc);
			System.out.print(".");
		}
		System.out.println(" " + writer.numDocs() + " Dokumente indexiert.");
	}

	/*
	 * Schritt A.2: 'build document' - Die Klasse Document ist ein Container für sog. 'Fields', welche die eigentlichen
	 * Daten kapseln. Strukturell ähnelt ein Field einer Map<Key, Value>, d.h. auf einen Key (ID) wird ein Value
	 * (textuelle Daten) abgebildet.
	 */
	private Document buildDocument(Work work) {

		String title = work.getTitle();
		String text = work.getText();

		Document document = new Document();
		document.add(new TextField("title", title, Store.YES));
		document.add(new TextField("contents", text, Store.YES));
		// StringField wird nicht tokenisiert, gut z.B. für Sortierung
		document.add(new StringField("title.sort", title, Store.YES));

		return document;
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
}
