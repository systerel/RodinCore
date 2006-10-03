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
	
	/**
	 * Returns the reasoner ids of all reasoner extentions present in the reasoner
	 * registry. 
	 * 
	 * @return The reasoner ids of all reasoner extentions present in 
	 * 			the reasoner registry  
	 */
	Set<String> getReasonerIDs();

	/**
	 * Returns an instance of the class implementing the reasoner extention 
	 * with the given reasoner id.
	 * <p>
	 * Ideally the presence of a reasoner extention with the given reasoner extention
	 * must be checked first.
	 * <p>
	 * </p>
	 * In case no reasoner extention with the given name is present, <code>null</code>
	 * is returned.
	 * In case there was a problem loading the class specified in the reasoner extention
	 * <code>null</code> is returned, and the reason is recorded as an exception in the 
	 * log. Possible problems loading the class include:
	 * <ul>
	 * <li>The specified class could not be loaded and instantiated
	 * <li>The specified class does not implement <code>IReasoner</code>
	 * <li>The reasoner id specified in the extention point and returned by the class instance
	 * do not match
	 * </ul>
	 * </p>
	 * 
	 * @param reasonerID
	 * 		The reasoner id concerned
	 * @return
	 * 		The name of the reasoner id as 	declared in the reasoner extention, or
	 * 		<code>null</code> if no such extention exists or if there was a problem
	 * 		loading the specified class. In the latter case an exception is logged.
	 */
	IReasoner getReasonerInstance(String reasonerID);

	/**
	 * Returns the name of the reasoner extention with the given reasoner id.
	 * 
	 * <p>
	 * Ideally the presence of a reasoner extention with the given reasoner extention
	 * must be checked first.
	 * In case no reasoner extention with the given name is present, <code>null</code>
	 * is returned.
	 * </p>
	 * 
	 * @param reasonerID
	 * 		The reasoner id concerned
	 * @return
	 * 		The name of the reasoner id as 	declared in the reasoner extention, or
	 * 		<code>null</code> if no such extention exists.
	 */
	String getReasonerName(String reasonerID);

	/**
	 * Factory method to create a dummy reasoner.
	 * 
	 * A dummy reasoner is used as a facade to a reasoner that is not currently
	 * installed so that the rule provided by it may be used in the proof tree
	 * and serialized. A dummy reasoner cannot be replayed. An attempt to call
	 * the <code>apply()</code> method on it will always return an 
	 * <code>IReasonerFailure</code>.
	 * 
	 * @see IReasoner
	 * 
	 * @param reasonerID
	 * 					The reasoner ID to be used to construct the dummy reasoner
	 * @return
	 * 			A dummy reasoner with the given reasoner ID.
	 */
	IReasoner makeDummyReasoner(String reasonerID);
	
	
	/**
	 * Checks if a given reasoner is a dummy reasoner or not.
	 * 
	 * @see makeDummyReasoner()
	 * 
	 * @param reasoner
	 * 				The reasoner to check
	 * @return <code>true</code> iff the given reasoner is a dummy reasoner
	 */
	boolean isDummyReasoner(IReasoner reasoner);

}