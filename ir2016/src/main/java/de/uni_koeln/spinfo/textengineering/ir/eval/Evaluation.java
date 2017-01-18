package de.uni_koeln.spinfo.textengineering.ir.eval;

import java.util.List;

/*
 * Evaluation (Precision, Recall, F-Maß) einer Query und einer Dokumentenmenge gegen einen Goldstandard.
 */
public class Evaluation {

	private List<Integer> relevant;

	public Evaluation(List<Integer> goldstandard) {
		this.relevant = goldstandard;
	}

	public EvaluationResult evaluate(List<Integer> retrieved) {
		/*
		 * TODO Für das Suchergebnis müssen Precision, Recall und F-Maß errechnet werden. Grundlage sind die "true positives"
		 * , die anhand des Goldstandards ermittelt werden.
		 */

		return null;
	}

}
