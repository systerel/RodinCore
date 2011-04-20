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

import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;


/**
 * Describes the context of child creation and the various child type that one
 * can create.
 * 
 * @author Thomas Muller
 */
public class ChildCreationInfo extends AbstractCreationInfo {

	private final Set<IInternalElementType<? extends IInternalElement>> childTypes;

	public ChildCreationInfo(
			Set<IInternalElementType<? extends IInternalElement>> childTypes,
			ILElement parent, ILElement nextSibling) {
		super(parent, nextSibling);
		this.childTypes = childTypes;
	}

	public Set<IInternalElementType<? extends IInternalElement>> getPossibleChildTypes() {
		return childTypes;
	}

}
