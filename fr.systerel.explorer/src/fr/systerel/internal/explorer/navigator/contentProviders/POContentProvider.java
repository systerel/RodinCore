/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;

/**
 * 
 * The content provider for proof obligations
 * 
 */
public class POContentProvider extends AbstractContentProvider {

	// proof obligations can have multiple parents. return none at all.
	public Object getParent(Object element) {
		return null;
	}

	@Override
	protected IInternalElementType<?> getElementType() {
		return IPSStatus.ELEMENT_TYPE;
	}

}
