package de.uni_koeln.spinfo.textengineering.ir.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinearSearch {

	private List<Work> works;

	public LinearSearch(Corpus corpus) {
		works = corpus.getWorks();
	}

	public List<Integer> search(String query) {
		
		List<Integer> result = new ArrayList<>(); 
		
		for (Work work : works) {
			String text = work.getText();
			List<String> tokens = Arrays.asList(text.split(" "));
			for (String t : tokens) {
				if(t.compareTo(query) == 0){
					result.add(works.indexOf(work));
				}
				
			}
			
		}
		
		return result;
	}

}
