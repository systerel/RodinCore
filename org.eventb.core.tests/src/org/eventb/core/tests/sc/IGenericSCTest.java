/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.IGenericElementTest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericSCTest <E extends IRodinElement, SCE extends IRodinElement> 
extends IGenericElementTest <E> {
	
	public void save(E element) throws RodinDBException;
	
	public void containsMarkers(E element, boolean yes) throws CoreException;

	public SCE getSCElement(E element) throws RodinDBException;	
	
	public IRodinElement[] getIdents(E element) throws RodinDBException;
	
	public IRodinElement[] getPredicates(E element) throws RodinDBException;
	
	public void containsIdents(SCE element, String...strings) throws RodinDBException;
	
	public void containsPredicates(SCE element, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException;
	
}
