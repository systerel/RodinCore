/**
 * 
 */
package org.eventb.core.seqprover.simplifier;

import org.eventb.core.seqprover.IProofRule;


/**
 * Interface implimenting filters
 * 
 * 
 * @author fmehta
 *
 */
public interface ISimplifier {
	
	// Predicate apply (Predicate pred);
	// IProverSequent apply(IProverSequent sequent);
	IProofRule apply(IProofRule rule);

}
