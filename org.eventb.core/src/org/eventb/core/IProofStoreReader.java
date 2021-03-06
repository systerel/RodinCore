/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved used reasoners to proof root
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerDesc;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IProofStoreReader {
	
	FormulaFactory getFormulaFactory();
	/**
	 * @since 3.0
	 */
	ISealedTypeEnvironment getBaseTypeEnv() throws CoreException;
	Predicate getPredicate(String name) throws CoreException;
	Expression getExpression(String ref)  throws CoreException;

	/**
	 * @since 2.2
	 */
	IReasonerDesc getReasoner(String ref) throws RodinDBException;
}
