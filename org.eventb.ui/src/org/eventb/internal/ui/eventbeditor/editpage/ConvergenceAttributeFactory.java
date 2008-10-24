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
package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class ConvergenceAttributeFactory implements
		IAttributeFactory<IConvergenceElement> {

	public final static String ORDINARY = "ordinary";

	public final static String CONVERGENT = "convergent";

	public final static String ANTICIPATED = "anticipated";

	public String getValue(IConvergenceElement element, IProgressMonitor monitor)
			throws RodinDBException {
		Convergence convergence = element.getConvergence();
		if (convergence == Convergence.ORDINARY)
			return ORDINARY;
		if (convergence == Convergence.CONVERGENT)
			return CONVERGENT;
		if (convergence == Convergence.ANTICIPATED)
			return ANTICIPATED;
		return ORDINARY;
	}

	public void setValue(IConvergenceElement element, String newValue,
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
		element.setConvergence(convergence, null);
	}

	public String[] getPossibleValues(IConvergenceElement element,
			IProgressMonitor monitor) {
		return new String[] { ORDINARY, CONVERGENT, ANTICIPATED };
	}

	public void removeAttribute(IConvergenceElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.CONVERGENCE_ATTRIBUTE,
				new NullProgressMonitor());
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IConvergenceElement element, IProgressMonitor monitor)
			throws RodinDBException {
		element.setConvergence(IConvergenceElement.Convergence.ORDINARY,
				monitor);
	}

	public boolean hasValue(IConvergenceElement element,
			IProgressMonitor monitor) throws RodinDBException {
		return element.hasConvergence();
	}
}
