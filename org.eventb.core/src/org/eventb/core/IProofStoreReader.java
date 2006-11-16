/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public interface IProofStoreReader {
	
	FormulaFactory getFormulaFactory();
	ITypeEnvironment getBaseTypeEnv(IProgressMonitor monitor) throws RodinDBException;
	Predicate getPredicate(String name,IProgressMonitor monitor) throws RodinDBException;
	Expression getExpression(String ref, IProgressMonitor monitor)  throws RodinDBException;

}
