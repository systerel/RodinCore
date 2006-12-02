package org.eventb.core.seqprover;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for deserializing a reasoner input object. Serialization is
 * done by registering strings, predicates and expressions with an instance of
 * this interface. Each one is associated to a key chosen by the client. When
 * deserializing, clients just retrieve the stored information using the same
 * key.
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 */
public interface IReasonerInputReader {
	
	/**
	 * Returns the predicates that were serialized with the given key.
	 * 
	 * @param key
	 *     key to use
	 * @throws SerializeException
	 */
	Predicate[] getPredicates(String key) throws SerializeException;
	
	/**
	 * Returns the expressions that were serialized with the given key.
	 * 
	 * @param key
	 *     key to use
	 * @throws SerializeException
	 */
	Expression[] getExpressions(String key) throws SerializeException;
	
	/**
	 * Returns the string that was serialized with the given key.
	 * 
	 * @param key
	 *     key to use
	 * @throws SerializeException
	 */
	String getString(String key) throws SerializeException;

}
