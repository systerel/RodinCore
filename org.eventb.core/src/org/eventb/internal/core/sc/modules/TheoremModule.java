/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class TheoremModule extends PredicateModule<ITheorem> {

	private static String THEOREM_NAME_PREFIX = "THM";

	protected void checkAndSaveTheorems(IInternalParent target, int offset,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		checkAndType(target.getElementName(), repository, monitor);

		saveTheorems(target, offset, null);
	}

	protected abstract ISCTheorem getSCTheorem(IInternalParent target,
			String elementName);

	private void saveTheorems(IInternalParent parent, int offset,
			IProgressMonitor monitor) throws CoreException {
		createSCPredicates(parent, THEOREM_NAME_PREFIX, offset, monitor);
	}

}
