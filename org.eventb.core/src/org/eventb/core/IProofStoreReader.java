/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IProofStoreReader {
	
	FormulaFactory getFormulaFactory();
	ITypeEnvironment getBaseTypeEnv() throws RodinDBException;
	Predicate getPredicate(String name) throws RodinDBException;
	Expression getExpression(String ref)  throws RodinDBException;

}
