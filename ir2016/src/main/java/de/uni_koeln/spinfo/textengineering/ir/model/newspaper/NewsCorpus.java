package de.uni_koeln.spinfo.textengineering.ir.model.newspaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.util.Parser;

public class NewsCorpus implements Corpus {

	List<IRDocument> articles;

	public NewsCorpus(String folder) {
		buildCorpus(folder);
	}

	private void buildCorpus(String folder) {
		// Dateien einlesen ...
		articles = new ArrayList<>();
		File f = new File(folder);
		if (f.isDirectory()) {
			File[] listFiles = f.listFiles();
			// jede Datei einzeln in ein NewsDoc überführen:
			for (int i = 0; i < listFiles.length; i++) {
				File f2 = listFiles[i];
				NewsDocument newsDocument = Parser.parseNewsDocument(f2);
				if(newsDocument != null)
					articles.add(newsDocument);
			}
		}
	}

	@Override
	public List<IRDocument> getDocuments() {
		return articles;
	}

	@Override
	public String getText() {
		// TODO: not implemented...
		return null;
	}

}
