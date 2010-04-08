/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IConvergenceElement.Convergence;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ConvergenceAttributeManipulation extends
		AbstractAttributeManipulation {

	// TODO externalize strings
	public final static String ORDINARY = "ordinary";

	public final static String CONVERGENT = "convergent";

	public final static String ANTICIPATED = "anticipated";

	private IConvergenceElement asConvergence(IRodinElement element) {
		assert element instanceof IConvergenceElement;
		return (IConvergenceElement) element;
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final Convergence convergence = asConvergence(element)
				.getConvergence();
		if (convergence == Convergence.ORDINARY)
			return ORDINARY;
		if (convergence == Convergence.CONVERGENT)
			return CONVERGENT;
		if (convergence == Convergence.ANTICIPATED)
			return ANTICIPATED;
		return ORDINARY;
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final Convergence convergence;
		if (newValue.equals(ORDINARY))
			convergence = Convergence.ORDINARY;
		else if (newValue.equals(CONVERGENT))
			convergence = Convergence.CONVERGENT;
		else if (newValue.equals(ANTICIPATED))
			convergence = Convergence.ANTICIPATED;
		else
			convergence = null;
		asConvergence(element).setConvergence(convergence, null);
	}

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return new String[] { ORDINARY, CONVERGENT, ANTICIPATED };
	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asConvergence(element).removeAttribute(CONVERGENCE_ATTRIBUTE, null);
	}

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asConvergence(element).setConvergence(Convergence.ORDINARY, monitor);
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asConvergence(element).hasConvergence();
	}

}
