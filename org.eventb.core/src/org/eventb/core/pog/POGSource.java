/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
class POGSource implements IPOGSource {
	
	private final String role;
	private final IRodinElement source;
	
	POGSource(String role, IRodinElement source) throws RodinDBException {
		this.role = role;
		this.source = source;
	}
	
	@Override
	public String getRole() {
		return role;
	}
	
	@Override
	public IRodinElement getSource() {
		return source;
	}

}
