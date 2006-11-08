/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IMachine extends IRodinFile {
	
	public static final IFileElementType ELEMENT_TYPE =
		RodinCore.getFileElementType("org.rodinp.core.tests.machine");
	
	ISCMachine getCheckedVersion();

	IContext[] getUsedContexts() throws RodinDBException;
	
	ISCMachine getReferencedMachine() throws RodinDBException;

}
