package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IContext;

public class QuantifiedDescriptor extends IndexedDescriptor {

	private List<TermSignature> definingTerms;
	
//	public QuantifiedDescriptor(IContext context, List<IIntermediateResult> termList, int index, List<TermSignature> definingTerms) {
//		super(context, termList, index);
//		this.definingTerms = definingTerms;
//	}

	public QuantifiedDescriptor(IContext context, int index, List<TermSignature> definingTerms) {
		super(context, index);
		this.definingTerms = definingTerms;
	}
	
	public List<TermSignature> getDefiningTerms() {
		return definingTerms;
	}
	
	@Override
	public String toString() {
		return "Q"+index+definingTerms;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode()*31+definingTerms.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QuantifiedDescriptor) {
			QuantifiedDescriptor temp = (QuantifiedDescriptor) obj;
			return super.equals(temp) && definingTerms.equals(temp.definingTerms);
		}
		return false;
	}
	
}
