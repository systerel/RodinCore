package org.eventb.core.seqprover;


/**
 * Common interface for the output of a reasoner.
 * 
 * <p>
 * This interface is not intended to be implemented by clients. Objects of this
 * type should be generated using the factor methods provided for their subclasses.
 * </p>
 * 
 * @see ProverFactory
 * @see IReasonerFailure
 * @see IProofRule
 * 
 * @author Farhad Mehta
 *
 */
public interface IReasonerOutput {

	/**
	 * Returns the reasoner that was used to generate this reasoner output
	 * 
	 * @return
	 *	 	the reasoner that was used to generate this reasoner output
	 *
	 */
	IReasoner generatedBy();

	/**
	 * Returns the reasoner input that was used to generate this reasoner output
	 * 
	 * @return
	 * 		the reasoner input that was used to generate this reasoner output
	 */
	IReasonerInput generatedUsing();

}