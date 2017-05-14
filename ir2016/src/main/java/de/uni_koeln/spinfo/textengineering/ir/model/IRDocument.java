package de.uni_koeln.spinfo.textengineering.ir.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;
import de.uni_koeln.spinfo.textengineering.ir.ranked.TermWeighting;

public interface IRDocument {

	public String getContent();

	public void setContent(String content);

	public String getTopic();

	public void setTopic(String topic);

	public String getTitle();

	public void setTitle(String title);

	public URI getURI();

	public void setURI(URI uri);

	public List<String> getTerms();

	public double getTf(String term);

	public default List<Double> computeVector(RankedRetrieval index) {
		List<String> terms = index.getTerms();
		List<Double> vector = new ArrayList<Double>(terms.size());
		Double tfIdf;
		for (String term : terms) {
			tfIdf = TermWeighting.tfIdf(term, this, index);
			vector.add(tfIdf);
		}
		return vector;
	}

}
