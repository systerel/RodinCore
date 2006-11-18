package org.eventb.core.seqprover;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

public class Hypothesis{

	private final Predicate predicate;
	
	public Hypothesis(Predicate predicate){
		assert predicate.isTypeChecked();
		this.predicate = predicate;
	}
	
	public static Set<Hypothesis> Hypotheses(Set<Predicate> predicates){
		Set<Hypothesis> hypotheses = new HashSet<Hypothesis>(predicates.size());
		for(Predicate predicate : predicates){
			hypotheses.add(new Hypothesis(predicate));
		}
		return hypotheses;
	}
	
	public static Set<Hypothesis> Hypotheses(Predicate... predicates){
		Set<Hypothesis> hypotheses = new HashSet<Hypothesis>(predicates.length);
		for(Predicate predicate : predicates){
			hypotheses.add(new Hypothesis(predicate));
		}
		return hypotheses;
	}
	
	public static Set<Hypothesis> Hypotheses(Hypothesis... hypotheses){
		return new HashSet<Hypothesis>(Arrays.asList(hypotheses));
	}
	
	public static Set<Predicate> Predicates(Set<Hypothesis> hypotheses){
		Set<Predicate> predicates = new HashSet<Predicate>(hypotheses.size());
		for(Hypothesis hypothesis : hypotheses){
			predicates.add(hypothesis.getPredicate());
		}
		return predicates;
	}
	
	public static Set<Hypothesis> Hypotheses(){
		return new HashSet<Hypothesis>();
	}
	
	public static boolean containsPredicate(Set<Hypothesis> hypotheses,Predicate P){
		Hypothesis PH = new Hypothesis(P);
		return hypotheses.contains(PH);
	}
	
	public static Set<Hypothesis> textSearch(Set<Hypothesis> hypotheses,String str){
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (Hypothesis hypothesis:hypotheses){
			if (hypothesis.getPredicate().toString().contains(str)) result.add(hypothesis);
		}
		return result;
	}
	
	// TODO : make more efficient
	public static Set<Hypothesis> freeIdentsSearch(Set<Hypothesis> hypotheses,FreeIdentifier[] freeIdents){
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		final List<FreeIdentifier> freeIdentsAsList = Arrays.asList(freeIdents);
		for (Hypothesis hypothesis:hypotheses){
			if (! Collections.disjoint(
					Arrays.asList(hypothesis.getPredicate().getFreeIdentifiers()),
					freeIdentsAsList))				
				result.add(hypothesis);
		}
		return result;
	}
	
//	 TODO : make more efficient
	public static Set<Hypothesis> freeIdentsSearch(Set<Hypothesis> hypotheses,Set<FreeIdentifier> freeIdents){
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (Hypothesis hypothesis:hypotheses){
			if (! Collections.disjoint(
					Arrays.asList(hypothesis.getPredicate().getFreeIdentifiers()),
					freeIdents))				
				result.add(hypothesis);
		}
		return result;
	}
	
	public static Set<Hypothesis> textSearchExactS(Set<Hypothesis> hypotheses,Set<String> strings){
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (Hypothesis hypothesis:hypotheses){
			String hypAsStr = hypothesis.getPredicate().toString();
			for (String str:strings){
				if (hypAsStr.equals(str)) result.add(hypothesis);
			}
		}
		return result;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.prover.sequent.IHypothesis#getPredicate()
	 */
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.sequent.IHypothesis#getPredicate()
	 */
	public Predicate getPredicate(){
		return this.predicate;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.prover.sequent.IHypothesis#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (! (obj instanceof Hypothesis)) return false;
		return predicate.equals(((Hypothesis)obj).getPredicate());
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.prover.sequent.IHypothesis#hashCode()
	 */
	@Override
	public int hashCode(){
		return this.predicate.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.prover.sequent.IHypothesis#toString()
	 */
	@Override
	public String toString(){
		return predicate.toString();
	}
}
