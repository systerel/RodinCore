package org.eventb.core.seqprover;

import java.util.Set;

/**
 * Interface defining the protocol to the singeleton class that implements 
 * the Reasoner Registry.
 * 
 * <p>
 * The Reasoner Registry manages Reasoners that have been declared as extensions 
 * to the 'reasoners' extension point provided by the sequent prover.
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
	 * Checks if a reasoner extension with the given reasoner id is present in
	 * the reasoner registry.
	 * 
	 * <p>
	 * Shortcut for getReasonerIDs().contains(reasonerID).
	 * </p>
	 * 
	 * @param reasonerID
	 *            The reasoner id to check for
	 * @return <code>true</code> if a reasoner extension with the given id is
	 *         present in the reasoner registry
	 */
	boolean isPresent(String reasonerID);
	
	/**
	 * Returns the ids of all reasoner extensions present in the reasoner
	 * registry.
	 * 
	 * @return the ids of all known reasoner extensions
	 */
	Set<String> getReasonerIDs();

	/**
	 * Returns an instance of the reasoner extension with the given reasoner id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered, or
	 * if there is a problem instantiating the reasoner class, a dummy reasoner
	 * instance is returned.
	 * </p>
	 * 
	 * @param reasonerID
	 *            the id of the reasoner
	 * @return an instance of the reasoner (might be a dummy one in case of
	 *         error)
	 * @see #isDummyReasoner(IReasoner)
	 */
	IReasoner getReasonerInstance(String reasonerID);

	/**
	 * Returns the name of the reasoner extension with the given id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered, a
	 * placeholder string is returned, stating the problem.
	 * </p>
	 * 
	 * @param reasonerID
	 *            the reasoner id concerned
	 * @return the name of the reasoner with the given id
	 */
	String getReasonerName(String reasonerID);

	/**
	 * Checks whether a given reasoner is a dummy reasoner.
	 * <p>
	 * A dummy reasoner is used as a facade to a reasoner that is not currently
	 * installed so that the rule provided by it may be used in the proof tree
	 * and serialized. A dummy reasoner cannot be replayed. An attempt to call
	 * the <code>apply()</code> method on it will always return an
	 * <code>IReasonerFailure</code>.
	 * </p>
	 * 
	 * @param reasoner
	 *            the reasoner to check
	 * @return <code>true</code> iff the given reasoner is a dummy reasoner
	 */
	boolean isDummyReasoner(IReasoner reasoner);

}
