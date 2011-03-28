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

public class ImplicitProvidingAssociation implements
		IImplicitProvidingAssociation {

	final IInternalElementType<? extends IInternalElement> type;
	final ICoreImplicitChildProvider provider;

	public ImplicitProvidingAssociation(ICoreImplicitChildProvider provider,
			IInternalElementType<? extends IInternalElement> type) {
		this.provider = provider;
		this.type = type;
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getType() {
		return type;
	}

	@Override
	public ICoreImplicitChildProvider getProvider() {
		return provider;
	}

}
