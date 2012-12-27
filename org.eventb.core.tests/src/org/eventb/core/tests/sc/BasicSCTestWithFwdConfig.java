/*******************************************************************************
 * Copyright (c) 2008, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class BasicSCTestWithFwdConfig extends BasicSCTest {
	
	private static final String FWD_CONFIG = "org.eventb.core.fwd";

	@Override
	protected IContextRoot createContext(String bareName)
			throws RodinDBException {
		IContextRoot ctx = super.createContext(bareName);
		ctx.setConfiguration(FWD_CONFIG, null);
		return ctx;
	}


	@Override
	protected IMachineRoot createMachine(String bareName)
			throws RodinDBException {
		IMachineRoot mac = super.createMachine(bareName);
		mac.setConfiguration(FWD_CONFIG, null);
		return mac;
	}

}
