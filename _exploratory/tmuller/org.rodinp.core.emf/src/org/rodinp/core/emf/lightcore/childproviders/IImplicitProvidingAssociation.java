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
package org.rodinp.core.emf.lightcore.childproviders;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ICoreImplicitChildProvider;

public interface IImplicitProvidingAssociation {

	/**
	 * @return the type of the children associated to the current provider (i.e.
	 *         the type of the provided children)
	 */
	public IInternalElementType<? extends IInternalElement> getType();

	/**
	 * @return the implicit child provider for the current child type
	 */
	public ICoreImplicitChildProvider getProvider();

}