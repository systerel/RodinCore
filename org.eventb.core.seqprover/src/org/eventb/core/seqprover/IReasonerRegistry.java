package org.eventb.core.seqprover;


/**
 * Interface defining the protocol to the singleton class that implements the
 * Reasoner Registry.
 * <p>
 * The Reasoner Registry manages Reasoners that have been declared as extensions
 * to the 'reasoners' extension point provided by the sequent prover.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 */
public interface IReasonerRegistry {

	/**
	 * Checks if a reasoner extension with the given reasoner id is present in
	 * the reasoner registry.
	 * <p>
	 * This is fully equivalent to
	 * <code>Arrays.asList(getReasonerIDs()).contains(reasonerID)</code>,
	 * although implemented in a more efficient way.
	 * </p>
	 * 
	 * @param reasonerID
	 *            the reasoner id to check for
	 * @return <code>true</code> iff the given reasoner id is known to the
	 *         reasoner registry
	 */
	boolean isPresent(String reasonerID);
	
	/**
	 * Returns the ids of all reasoner extensions present in the reasoner
	 * registry.
	 * <p>
	 * The returned set is immutable. Any attempt to modify it will raise an
	 * {@link UnsupportedOperationException}.
	 * </p>
	 * <p>
	 * A reasoner id is present in the registry if any of the following holds
	 * (exhaustive list):
	 * <ul>
	 * <li>It has been registered through the <code>reasoners</code>
	 * extension point.</li>
	 * <li>Its name has been requested by way of {@link #getReasonerName()}.</li>
	 * <li>Its instance has been requested by way of
	 * {@link #getReasonerInstance()}.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return an array of all known reasoner ids
	 */
	String[] getReasonerIDs();

	/**
	 * Returns an instance of the reasoner extension with the given id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered, or
	 * if there is a problem instantiating the reasoner class, a dummy reasoner
	 * instance is returned. Subsequently, the reasoner is considered as
	 * registered.
	 * </p>
	 * 
	 * @see #isDummyReasoner(IReasoner)
	 *
	 * @param reasonerID
	 *            the id of the reasoner
	 * @return an instance of the reasoner (might be a dummy one in case of
	 *         error)
	 */
	IReasoner getReasonerInstance(String reasonerID);

	/**
	 * Returns the name of the reasoner extension with the given id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered, a
	 * placeholder string is returned, stating the problem. Subsequently, the
	 * reasoner is considered as registered.
	 * </p>
	 * 
	 * @param reasonerID
	 *            the id of the reasoner
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
