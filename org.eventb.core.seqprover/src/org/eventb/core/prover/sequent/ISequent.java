/**
 * 
 */
package org.eventb.core.prover.sequent;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
/**
 * @author fmehta
 *
 */
public interface ISequent {
	
	public abstract ITypeEnvironment typeEnvironment();
	public abstract Set<Hypothesis> hypotheses();
	public abstract Predicate goal();
	
//	@Override
//	public String toString(){
//		return (this.getClass().toString() +
//				typeEnvironment().toString() +
//				hypotheses().toString() +
//				goal().toString());
//	}
//	
//	/* Used only to check consistency of typing environment */
//	private Set<FreeIdentifier> freeIdentifiers(){
//		Set<FreeIdentifier> identifiers = new HashSet<FreeIdentifier>(Arrays.asList(this.goal().getFreeIdentifiers()));
//		Set<Predicate> hypotheses = this.hypotheses();
//		for (Predicate h : hypotheses) identifiers.addAll(Arrays.asList(h.getFreeIdentifiers()));
//		return identifiers;
//	}
//	
//	private boolean noDanglingFreeIdentifiers(){
//		Set<FreeIdentifier> identifiers = freeIdentifiers();
//		for (FreeIdentifier i : identifiers) 
//			if (! this.typeEnvironment().contains(i)) return false;
//		return true;
//	}
//	
//	public boolean invariant(){
//		return noDanglingFreeIdentifiers();
//	}

}
