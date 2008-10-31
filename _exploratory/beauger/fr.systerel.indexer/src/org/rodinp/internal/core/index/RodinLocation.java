/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IRodinLocation;

/**
 * @author Nicolas Beauger
 *
 */
public class RodinLocation implements IRodinLocation {

	private final IRodinElement element;
	
	
	
	public RodinLocation(IRodinElement element) {
		this.element = element;
	}

	public IRodinElement getElement() {
		return element; 
	}

	public IRodinFile getRodinFile() {
		if (element instanceof IRodinFile) {
		return (IRodinFile) element;
		} else if (element instanceof IInternalElement) {
			return ((IInternalElement) element).getRodinFile();
		}
		return null;
	}

}
