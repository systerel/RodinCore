package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class represents an intermediate result for formulas containing more
 * than one literal. It consists of a list of {@link IIntermediateResult}.
 *
 * @author François Terrier
 *
 */
public class IntermediateResultList implements IIntermediateResult {

	private List<IIntermediateResult> list;
	
	public IntermediateResultList(List<IIntermediateResult> list) {
		this.list = list;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.pptrans.loader.IIntermediateResult#getTerms()
	 */
	public List<TermSignature> getTerms() {
		List<TermSignature> indexes = new ArrayList<TermSignature>();
		for (IIntermediateResult res : list) {
			indexes.addAll(res.getTerms());
		}
		return indexes;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntermediateResultList) {
			IntermediateResultList temp = (IntermediateResultList) obj;
			return list.equals(temp.list);
		}
		return false;
	}

	public List<IIntermediateResult> getResultList() {
		return list;
	}

	@Override
	public String toString() {
		return list.toString();
	}

}
