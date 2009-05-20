/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IPORoot;
import org.eventb.core.tests.IGenericElementTest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericPOTest <E extends IInternalElement> extends IGenericElementTest <E> {
	
	public void addSuper(E file, E abstraction) throws RodinDBException;
	
	public IPORoot getPOFile(E file) throws RodinDBException;

}
