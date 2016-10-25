package de.uni_koeln.spinfo.textengineering.ir.basic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Corpus {

	private String text;
	private List<Work> works;

	public Corpus(String location, String delimiter) {

		StringBuilder sb = new StringBuilder();
		try {
			Scanner scanner = new Scanner(new File(location));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				sb.append(line);
				sb.append("\n"); // explizit für Delimiter
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		text = sb.toString();
		works = new ArrayList<Work>();
		for (String work : text.split(delimiter)) {
			String title = (work.substring(0, work.indexOf("\n"))).trim();
			works.add(new Work(work, title));
		}
	}

	public List<Work> getWorks() {
		return works;
	}

	public String getText() {
		return text;
	}

}
