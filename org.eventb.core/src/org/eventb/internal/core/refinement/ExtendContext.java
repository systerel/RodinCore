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
package org.eventb.internal.core.refinement;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.RodinDBException;

/**
 * Refinement participant for extending a context.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ExtendContext implements IRefinementParticipant {

	@Override
	public void process(IInternalElement refinedRoot,
			IInternalElement sourceRoot, IProgressMonitor monitor)
			throws RodinDBException {
		final IContextRoot con = (IContextRoot) refinedRoot;
		final IContextRoot abs = (IContextRoot) sourceRoot;
		con.setConfiguration(abs.getConfiguration(), null);
		createExtendsContextClause(con, abs, monitor);
		// FIXME should remove generated, just like machine refinement
	}

	private void createExtendsContextClause(IInternalElement con,
			IContextRoot abs, IProgressMonitor monitor) throws RodinDBException {
		final IExtendsContext refines = con.createChild(
				IExtendsContext.ELEMENT_TYPE, null, monitor);
		refines.setAbstractContextName(abs.getComponentName(), monitor);
	}

}
