/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementManager;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class RefinementManager implements IRefinementManager {

	private static final RefinementManager DEFAULT_INSTANCE = new RefinementManager();

	private RefinementManager() {
		// singleton
	}

	/**
	 * @return the default instance
	 */
	public static RefinementManager getDefault() {
		return DEFAULT_INSTANCE;
	}

	@Override
	public IInternalElementType<?> getRootType(String refinementId) {
		return RefinementRegistry.getDefault().getRootType(refinementId);
	}

	@Override
	public boolean refine(IInternalElement sourceRoot,
			IInternalElement targetRoot, IProgressMonitor monitor)
			throws RodinDBException {
		return new RefinementProcessor(sourceRoot).refine(targetRoot, monitor);
	}

}
