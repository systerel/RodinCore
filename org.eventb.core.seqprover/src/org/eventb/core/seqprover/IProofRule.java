package org.eventb.core.seqprover;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;


/**
 * Common protocol for a proof rule for the sequent prover.
 * <p>
 * A proof rule contains a goal, needed hypotheses, and a possibly empty array of
 * anticidents.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. Objects of this type 
 * are typically generated inside reasoners by calling a factory method.
 * </p>
 * @see IAntecedent
 * @see org.eventb.core.seqprover.IReasoner
 * @see org.eventb.core.seqprover.ProverFactory
 * 
 * @author Farhad Mehta
 */
public interface IProofRule extends IReasonerOutput{

	
	/**
	 * Returns the goal of this proof rule as returned by the reasoner.
	 * 
	 * @return the goal of this proof rule
	 */
	Predicate getGoal();

	
	/**
	 * Returns the needed hypotheses of this proof rule as returned by the reasoner.
	 * 
	 * @return the needed hypotheses of this proof rule
	 */
	Set<Predicate> getNeededHyps();

	/**
	 * Returns the confidence of this proof rule as returned by the reasoner.
	 * 
	 * @return the confidence of this proof rule (see {@see IConfidence})
	 */
	int getConfidence();
	
	/**
	 * Returns the name of this proof rule this should be used for display.
	 * 
	 * @return the display name of this proof rule
	 */
	String getDisplayName();
	
	/**
	 * Returns the anticidents of this proof rule as returned by the reasoner.
	 * 
	 * @return the anticidents of this proof rule (see {@see IAntecedent})
	 */
	IAntecedent[] getAntecedents();
	

	/**
	 * Common protocol for an anticident for a proof rule.
	 * <p>
	 * An anticident contains a goal, added hypotheses, added free identifiers, and hypothesis
	 * selection information.
	 * </p>
	 * <p>
	 * Typically, an andicident records the changes made by a reasoner on the sequent
	 * to be proven. 
	 * </p>
	 * <p>
	 * This interface is not intended to be implemented by clients. Objects of this type 
	 * are typically generated inside reasoners by calling a factory method.
	 * </p>
	 * @see org.eventb.core.seqprover.IReasoner
	 * @see org.eventb.core.seqprover.ProverFactory
	 * 
	 * @author Farhad Mehta
	 */
	public interface IAntecedent {
		
		/**
		 * Returns the goal of this anticident.
		 * 
		 * @return the goal of this anticident
		 */
		Predicate getGoal();
		
		/**
		 * Returns the added hypotheses of this anticident.
		 * <p>
		 * Added hyps are by default selected.
		 * </p>
		 * @return the added hypotheses of this anticident
		 */
		Set<Predicate> getAddedHyps();
		
		/**
		 * Returns the added free identifiers of this anticident.
		 *
		 * @return the added free identifiers of this anticident
		 */
		FreeIdentifier[] getAddedFreeIdents();
		
		/**
		 * Returns hypotheses selection information for this anticident.
		 * <p>
		 * Added hyps are by default selected. The hypAction should not contain 
		 * added hypotheses. (simlifier constraint)
		 * </p>
		 * @return the hypotheses selection information for this anticident
		 */
		List<IHypAction> getHypAction();
	}	

}