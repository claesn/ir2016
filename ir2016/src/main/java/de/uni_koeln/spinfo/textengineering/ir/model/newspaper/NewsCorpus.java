package de.uni_koeln.spinfo.textengineering.ir.model.newspaper;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import de.uni_koeln.spinfo.textengineering.ir.model.Corpus;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

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
				NewsDocument newsDocument = parseDocument(f2);
				if(newsDocument != null)
					articles.add(newsDocument);
			}
		}
	}

	private NewsDocument parseDocument(File f2) {
		// konvertieren des Dokuments: Einlesen und parsen ...

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(f2);

			XPath xPath = XPathFactory.newInstance().newXPath();

			String topic = xPath.evaluate("//webdoc/topic", doc.getDocumentElement());
			String title = xPath.evaluate("//webdoc/title", doc.getDocumentElement());
			String source = xPath.evaluate("//webdoc/source", doc.getDocumentElement());
			String text = xPath.evaluate("//webdoc/text", doc.getDocumentElement());
			System.out.println("Adding document: " + title);
			
			return new NewsDocument(topic, title, new URI(source), text);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
