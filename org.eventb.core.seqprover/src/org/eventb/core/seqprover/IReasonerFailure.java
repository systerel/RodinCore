package org.eventb.core.seqprover;

/**
 * Common interface for reasoner outputs when the reasoner fails.
 * 
 * <p>
 * This interface is not intended to be implemented by clients. Objects of this
 * type should be generated using the factor methods provided.
 * </p>
 * 
 * @see ProverFactory
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IReasonerFailure extends IReasonerOutput{
	
	/**
	 * Returns the reason for reasoner failure as a <code>String</code>.
	 * 
	 * @return
	 * 		The reason for reasoner failure.
	 */
	String getReason();

}
