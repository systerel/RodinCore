/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericTest <IRF extends IRodinFile>{

	public void addTheorems(IRF rodinFile, String[] names, String[] theorems) throws RodinDBException;

	public void addNonTheorems(IRF rodinFile, String[] names, String[] axioms) throws RodinDBException;
	
	public void addIdents(IRF rodinFile, String... names) throws RodinDBException;
	
	public void addSuper(IRF rodinFile, String name) throws RodinDBException;
	
	public IRF createComponent(String bareName, IRF dummy) throws RodinDBException;

}
