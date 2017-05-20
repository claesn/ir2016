/**
 * Material for the course 'Information-Retrieval and Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textengineering.html)
 * <p/>
 * Copyright (C) 2008-2009 Fabian Steeg
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
package de.uni_koeln.spinfo.textengineering.ir.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.NewsDocument;

/*
 * Der Parser übernimmt für uns die Umwandlung einer URL in ein WebDocument, das
 * Inhalt und ausgehende Links der Seite an der URL enthält.
 */
/**
 * Parses a URL into a document representation, using NekoHTML
 * (http://nekohtml.sourceforge.net/).
 * 
 * @author Fabian Steeg, Claes Neuefeind
 */
public final class Parser {

	private Parser() {
		// Enforce non-instantiability with a private constructor.
	}

	private static StringBuilder builder;
	private static String title;

	/**
	 * @param url
	 *            The URL of the page to parse into a NewsDocument
	 * @return A document instance for the given URL
	 */
	public static NewsDocument parse(final String url) {
		/*
		 * Als Parser verwenden wir NekoHTML, einen fehlerkorrigierenden Parser
		 * (http://nekohtml.sourceforge.net/) der auf Xerces aufbaut:
		 */
		DOMParser parser = new DOMParser();
		try {
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
			/*
			 * Dem Neko-Parser können wir einfach die URL als String übergeben:
			 */
			parser.parse(url);
			/* Wir instanziieren die zu füllenden Werte: */
			builder = new StringBuilder();
			/* Und beginnen die Verarbeitung mit dem ersten Element: */
			org.w3c.dom.Document document = parser.getDocument();
			Node root = document.getFirstChild();
			process(root);

			/* für die Abbildung als XML noch ein bisschen säubern */
			String text = builder.toString().trim();
			text = text.replaceAll("&", "&#038;").replaceAll("<", "").replaceAll(">", "");
			title = title.trim().replaceAll("&", "&#038;");
			String source = url.replaceAll("&", "&#038;");

			/* Dann erzeugen wir aus den Werten unser Dokument-Objekt: */
			NewsDocument doc = new NewsDocument("UNKNOWN", title, new URI(source), text);
			return doc;

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void process(final Node node) {
		/*
		 * Wir ermitteln die Elemente über ihre Namen. Man könnte auch mit
		 * instanceof testen ob es etwa ein HTMLParagraphElement ist, allerdings
		 * ist das weniger zuverlässig, da etwa XHTML-Dokumente nicht aus
		 * solchen (HTML...), sondern aus anderen Elementen (in einem anderen
		 * Namensraum als HTML-Elemente...) bestehen.
		 */
		String name = node.getNodeName().toLowerCase();
		/* Inhalt ist für uns hier nur das, was in p-Tags steht: */
		if (name.equals("p")) {
			// builder.append("<p>");
			builder.append(node.getTextContent().trim()).append("\n\n");
		} else if (name.equals("title")) {
			title = node.getTextContent();
		}
		/*
		 * Jetzt ist der aktuelle Knoten abgearbeitet, jetzt kommt der rekursive
		 * Aufruf für den nächsten Knoten auf der gleichen Schachtelungsebene
		 * (wenn es einen gibt):
		 */
		Node sibling = node.getNextSibling();
		if (sibling != null) {
			process(sibling);
		}
		/*
		 * Nachdem jeder je seinen Nachbarn aufgerufen hat, kommt jetzt das
		 * ganze mit den untergeordneten:
		 */
		Node child = node.getFirstChild();
		if (child != null) {
			process(child);
		}
	}

	public static void print(Node node, String indent) {
		System.out.println(indent + node.getTextContent());
		Node child = node.getFirstChild();
		while (child != null) {
			print(child, indent + " ");
			child = child.getNextSibling();
		}
	}

	public static NewsDocument parseNewsDocument(File f2) {
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
			// System.out.println("Adding document: " + title);

			return new NewsDocument(topic, title, new URI(source), text);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
