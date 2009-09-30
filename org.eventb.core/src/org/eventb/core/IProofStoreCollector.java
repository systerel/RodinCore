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
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * Collects formulae for the proof store
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IProofStoreCollector {
	
	
	String putPredicate(Predicate pred) throws RodinDBException;
	String putExpression(Expression expr) throws RodinDBException;
	
	void writeOut(IPRProof prProof, IProgressMonitor monitor) throws RodinDBException;

	
	
}
