/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IPOFile;
import org.eventb.core.tests.IGenericTest;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IGenericPOTest <IRF extends IRodinFile> extends IGenericTest <IRF> {
	
	public IPOFile getPOFile(IRF rodinFile) throws RodinDBException;

}
