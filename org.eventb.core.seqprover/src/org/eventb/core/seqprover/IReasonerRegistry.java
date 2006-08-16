package org.eventb.core.seqprover;

import java.util.Set;

/**
 * Interface defining the protocol to the singeleton class that implements 
 * the Reasoner Registry.
 * 
 * <p>
 * The Reasoner Registry manages Reasoners that have been declared as extentions 
 * to the 'reasoners' extention point provided by the sequent prover.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public interface IReasonerRegistry {

	/**
	 * 
	 * 
	 * @param reasonerID
	 * @return
	 */
	IReasoner getReasonerInstance(String reasonerID);

	/**
	 * 
	 * 
	 * @param reasonerID
	 * @return
	 */
	String getReasonerName(String reasonerID);

	/**
	 * 
	 * 
	 * @return The reasoner ids of all reasoner extentions present in 
	 * 			the reasoner registry  
	 */
	Set<String> getReasonerIDs();

	/**
	 * Checks if a reasoner extention with the given reasoner id is present in the
	 * reasoner registry.
	 * 
	 * <p>
	 * Shortcut for getReasonerIDs().contains(reasonerID).
	 * </p>
	 * 
	 * @param reasonerID
	 * 				The reasoner id to check for
	 * @return <code>true</code> if a reasoner extention with the given reasoner id
	 * 			is present in the reasoner registry
	 */
	boolean isPresent(String reasonerID);

}