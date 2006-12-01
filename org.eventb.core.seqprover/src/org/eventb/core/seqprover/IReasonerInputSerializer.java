package org.eventb.core.seqprover;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for serializing and deserializing a reasoner input object.
 * Serialization is done by registering strings, predicates and expressions with
 * an instance of this interface. Each one is associated to a key chosen by the
 * client. For deserializing, clients just retrieve the stored information using
 * the same key.
 * <p>
 * Keys starting with a dot character are reserved for the implementation.  Any
 * attempt to use seach a key throws an {@link IllegalArgumentException}. 
 * </p>
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 */
public interface IReasonerInputSerializer {
	
	/**
	 * Exception thrown upon a serialization error.
	 */
	class SerializeException extends Exception {
		
		private static final long serialVersionUID = 1764122645237679016L;
		
		public SerializeException(Throwable cause){
			super(cause);
		}
	}

	/**
	 * Serializes the given predicates with the given key.
	 * 
	 * @param key
	 *     key to use
	 * @param predicates
	 *     predicates to serialize
	 * @throws SerializeException
	 */
	void putPredicates(String key, Predicate... predicates) throws SerializeException;
	
	/**
	 * Serializes the given expressions with the given key.
	 * 
	 * @param key
	 *     key to use
	 * @param expressions
	 *     predicates to serialize
	 * @throws SerializeException
	 */
	void putExpressions(String key, Expression... expressions) throws SerializeException;
	
	/**
	 * Serializes the given string with the given key.
	 * 
	 * @param key
	 *     key to use
	 * @param string
	 *     predicates to serialize
	 * @throws SerializeException
	 */
	void putString(String key, String string) throws SerializeException;
	
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
