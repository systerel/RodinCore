package org.eventb.core.ast;

import java.util.List;

/**
 * Common protocol for reporting results of AST operations.
 * <p>
 * Every operation that can produce error messages, returns an instance of this
 * interface that collects the result status of the operation. This object can
 * then be queried using the methods defined in this interface.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IResult {

	/**
	 * Returns a List of all the problems encountered during the operation.
	 * 
	 * @return a list of all problems
	 */
	List<ASTProblem> getProblems();

	/**
	 * Returns whether this result is a success, i.e., doesn't contain any
	 * error problem. Consequently, a result that contains only warnings is
	 * considered as successful.
	 * 
	 * @return <code>true</code> iff the operation succeeded
	 */
	boolean isSuccess();

}