/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.IGenericTest;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericSCTest <IRF extends IRodinFile, ISCRF extends IRodinFile> extends IGenericTest <IRF> {

	public ISCRF getSCComponent(IRF rodinFile) throws RodinDBException;	
	
	public void containsIdents(ISCRF rodinFile, String...strings) throws RodinDBException;
	
	public void containsTheorems(ISCRF rodinFile, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException;
	
	public void containsNonTheorems(ISCRF rodinFile, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException;
	
}
