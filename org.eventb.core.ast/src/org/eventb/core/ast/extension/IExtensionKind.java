/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
// FIXME: add comments in whole class
/**
 * @since 2.0
 * @author Nicolas Beauger
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExtensionKind {

	IOperatorProperties getProperties();
	
	boolean checkPreconditions(Expression[] childExprs, Predicate[] childPreds);

}