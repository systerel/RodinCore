/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class TheoremModule extends PredicateModule<ITheorem> {

	private static String THEOREM_NAME_PREFIX = "THM";

	protected void checkAndSaveTheorems(IInternalElement target, int offset,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		checkAndType(target.getElementName(), repository, monitor);

		saveTheorems(target, offset, null);
	}

	protected abstract ISCTheorem getSCTheorem(IInternalElement target,
			String elementName);

	private void saveTheorems(IInternalElement parent, int offset,
			IProgressMonitor monitor) throws CoreException {
		createSCPredicates(parent, THEOREM_NAME_PREFIX, offset, monitor);
	}

}
