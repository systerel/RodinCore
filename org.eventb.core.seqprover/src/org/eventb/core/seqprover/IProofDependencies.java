package org.eventb.core.seqprover;

import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

/**
 * Interface encapsulating information that can be used to decide if a proof
 * is reusable with another sequent.
 * 
 * A proof is reusable for another sequent if:
 * <ul>
 * <li> The goal is identical to the goal of the sequent and</li>
 * <li> All used hypotheses are contained in the hypotheses of the sequent and</li>
 * <li> All used free identifiers (with identical types) are contained in the 
 * type environment of the sequent and</li>
 * <li> No introduced free identifiers (ignoring their types) are present in the
 * type environment of the sequent</li>
 * </ul>
 * 
 * @author Farhad Mehta
 *
 */
public interface IProofDependencies{
	
	/**
	 * Returns the goal predicate of the proof.
	 * 
	 * @return the goal predicate of the proof.
	 */
	Predicate getGoal();
	
	/**
	 * Returns the hypotheses used in a proof.
	 * 
	 * @return the hypotheses used in a proof.
	 * 
	 */
	Set<Hypothesis> getUsedHypotheses();
	
	/**
	 * Returns the type environment corresponding to the used free
	 * identifiers in a proof.
	 * 
	 * @return the type environment corresponding to the used free
	 *  		identifiers in a proof.
	 */
	ITypeEnvironment getUsedFreeIdents();
	
	/**
	 * Returns the type environment corresponding to the free
	 * identifiers introduced in a proof.
	 * 
	 * @return the type environment corresponding to the free
	 *  		identifiers introduced in a proof.
	 */
	ITypeEnvironment getIntroducedFreeIdents();

}