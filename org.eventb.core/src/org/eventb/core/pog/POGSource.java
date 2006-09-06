/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGSource {
	
	private final String key;
	private final IRodinElement source;
	
	public POGSource(String key, IRodinElement source) {
		this.key = key;
		this.source = source;
	}
	
	public String getRoleKey() {
		return key;
	}
	
	public IRodinElement getSource() {
		return source;
	}

}
