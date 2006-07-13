/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This class provides a viewer sorter specifically for Rodin Element.
 */
public class RodinElementSorter extends ViewerSorter {

	// TODO This class should be extensible
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		return cat1 - cat2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
	 */
	public int category(Object obj) {
		if (obj instanceof IRodinElement) {
			IRodinElement rodinElement = (IRodinElement) obj;
			if (rodinElement instanceof ISeesContext)
				return -2;

			if (rodinElement instanceof IRefinesMachine)
				return -1;

			if (rodinElement instanceof IExtendsContext)
				return 0;

			if (rodinElement instanceof IVariable)
				return 1;

			if (rodinElement instanceof IInvariant)
				return 2;

			if (rodinElement instanceof ITheorem)
				return 4;

			if (rodinElement instanceof IEvent)
				return 5;

			if (rodinElement instanceof IGuard)
				return 2;

			if (rodinElement instanceof IAction)
				return 3;

			if (rodinElement instanceof ICarrierSet)
				return 1;

			if (rodinElement instanceof IConstant)
				return 2;

			if (rodinElement instanceof IAxiom)
				return 3;
		}
		return 0;
	}

}
