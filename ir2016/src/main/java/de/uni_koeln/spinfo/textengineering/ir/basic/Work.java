package de.uni_koeln.spinfo.textengineering.ir.basic;

public class Work {

	private String text;
	private String title;

	public Work(String text, String title) {
		setText(text);
		setTitle(title);
	}

	/**
	 * Zugriff auf den Text
	 * 
	 * @return Der Inhalt des Dokuments als String.
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Zugriff auf den Titel
	 * 
	 * @return Der Titel des Dokuments.
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return String.format("Titel: %s", title);
	}

}
