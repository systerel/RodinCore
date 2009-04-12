/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added creation of new internal element child
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.basis.RodinElement;


public class InternalElementInfo extends RodinElementInfo {
	
	public InternalElementInfo() {
		super();
	}

	private NameGenerator generator;
	
	public String getFreshName() {
		if (generator == null) {
			initializeGenerator();
		}
		return generator.advance();
	}

	private void initializeGenerator() {
		generator = new NameGenerator();
		for (RodinElement child: getChildren()) {
			generator.addUsedName(child.getElementName());
		}
	}
	
	@Override
	protected void childAdded(RodinElement newChild) {
		if (generator != null)
			generator.addUsedName(newChild.getElementName());
	}

	@Override
	protected void childrenSet() {
		generator = null;
	}

}
