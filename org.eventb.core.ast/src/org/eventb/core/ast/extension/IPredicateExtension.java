/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.typecheck.TypeCheckResult;

/**
 * @author "Nicolas Beauger"
 *
 */
public interface IPredicateExtension extends IFormulaExtension {

	void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers,
			Expression[] childExpressions, Predicate[] childPredicates);

}
