package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class represents an intermediate result for a single literal.
 * See {@link IntermediateResultList} for the class representing a formula, i.e.
 * a list of intermediate result.
 * 
 * @see IntermediateResultList
 * 
 * @author François Terrier
 */
public class IntermediateResult implements IIntermediateResult {

//	@SuppressWarnings("unused")
//	private Comparator<TermSignature> orderer; 
	
	private List<TermSignature> results = new ArrayList<TermSignature>();
	
//	public void orderList() {
////		Collections.sort(results);
//	}
//	
//	public void reverse() {
//		Collections.reverse(results);
//	}
	
	public IntermediateResult(/*Comparator<TermSignature> orderer*/) {
//		this.orderer = orderer;
	}
	
	public IntermediateResult(List<TermSignature> list /*, Comparator<TermSignature> orderer */ ) {
//		this.orderer = orderer;
		this.results.addAll(list);
	}
	

	
	public void addResult(TermSignature signature) {
		this.results.add(signature);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.pptrans.loader.IIntermediateResult#getTerms()
	 */
	public List<TermSignature> getTerms() {
		return results;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntermediateResult) {
			IntermediateResult temp = (IntermediateResult) obj;
			return results.equals(temp.results);
		}
		return false;
	}

	public List<IIntermediateResult> getResultList() {
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>();
		result.add(this);
		return result;
	}

	@Override
	public String toString() {
		return results.toString();
	}
	
}
