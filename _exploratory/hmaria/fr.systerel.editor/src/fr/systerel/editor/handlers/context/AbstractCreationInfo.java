/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.handlers.context;

import org.rodinp.core.emf.api.itf.ILElement;

/**
 * Describes the contextual information needed to handle the creation of an
 * element.
 * 
 * @author Thomas Muller
 */
public abstract class AbstractCreationInfo {

	private final ILElement parent;
	private final ILElement nextSibling;

	public AbstractCreationInfo(ILElement parent, ILElement nextSibling) {
		this.parent = parent;
		this.nextSibling = nextSibling;
	}

	public ILElement getParent() {
		return parent;
	}

	public ILElement getNextSibling() {
		return nextSibling;
	}

}
