package org.eventb.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * @since 2.0
 * @author Nicolas Beauger
 */
public interface IExtensionKind {

	IOperatorProperties getProperties();
	
	boolean checkPreconditions(Expression[] childExprs, Predicate[] childPreds);

	boolean isFlattenable();

}