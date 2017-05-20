package de.uni_koeln.spinfo.textengineering.ir.model.newspaper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.util.IRUtils;

public class NewsDocument implements IRDocument {

	private String topic;
	private String title;
	private URI uri;
	private String content;
	private Map<String, Integer> tf;

	public NewsDocument(String topic, String title, URI uri, String content) {
		this.setTopic(topic);
		this.title = title;
		this.uri = uri;
		this.content = content;
		this.tf = IRUtils.computeTf(content);
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public URI getURI() {
		return this.uri;
	}

	@Override
	public void setURI(URI uri) {
		this.uri = uri;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	@Override
	public double getTf(String term) {
		Integer integer = tf.get(term);
		return integer == null ? 0 : integer;
	}
	
	@Override
	public List<String> getTerms() {
		return new ArrayList<String>(tf.keySet());
	}

	@Override
	public String toString() {
		return "NewsDocument [topic=" + topic + ", title=" + title + ", uri=" + uri + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewsDocument other = (NewsDocument) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}


}
