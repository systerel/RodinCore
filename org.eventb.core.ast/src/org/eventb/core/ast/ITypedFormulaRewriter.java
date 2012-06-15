/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * 
 * Interface used to specify the replacement checks that should be performed
 * once the formula has been rewritten.
 * 
 * @author Thomas Muller
 * @since 2.6
 */
public interface ITypedFormulaRewriter extends IFormulaRewriter2 {

	Predicate checkReplacement(Predicate current, Predicate replacement);

	Expression checkReplacement(Expression current, Expression replacement);

	BoundIdentDecl checkReplacement(BoundIdentDecl current,
			BoundIdentDecl replacement);

}
