/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class BasicSCTestWithFwdConfig extends BasicSCTest {
	
	private static final String FWD_CONFIG = "org.eventb.core.fwd";

	@Override
	protected IContextFile createContext(String bareName)
			throws RodinDBException {
		IContextFile ctx = super.createContext(bareName);
		ctx.setConfiguration(FWD_CONFIG, null);
		return ctx;
	}

	@Override
	protected IMachineFile createMachine(String bareName)
			throws RodinDBException {
		IMachineFile mac = super.createMachine(bareName);
		mac.setConfiguration(FWD_CONFIG, null);
		return mac;
	}

}
