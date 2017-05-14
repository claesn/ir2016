package de.uni_koeln.spinfo.textengineering.ir.model;

import java.util.List;

public interface Corpus {

	public List<IRDocument> getDocuments();

	public String getText();

}
