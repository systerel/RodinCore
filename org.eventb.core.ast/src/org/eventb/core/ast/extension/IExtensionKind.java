package org.eventb.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * @since 2.0
 * @author Nicolas Beauger
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExtensionKind {

	IOperatorProperties getProperties();
	
	boolean checkPreconditions(Expression[] childExprs, Predicate[] childPreds);

	// FIXME clarify relation with the associativity property, set through
	// addCompatibilities; maybe remove this method
	boolean isFlattenable();

}