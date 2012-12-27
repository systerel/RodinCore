/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.OpenableElementInfo
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.basis.RodinElement;


/** Element info for IOpenable elements. */
public abstract class OpenableElementInfo extends RodinElementInfo {
	
	private boolean knownStructure = false;

	public boolean hasUnsavedChanges() {
		for (RodinElement child: getChildren()) {
			Openable openable = (Openable) child;
			if (openable.hasUnsavedChanges()) {
				return true;
			}
		}
		return false;
	}

	public boolean isStructureKnown() {
		return knownStructure;
	}

	public void setIsStructureKnown(boolean isStructureKnown) {
		knownStructure = isStructureKnown;
	}
	
}
