/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCTheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;

/**
 * This class contains some utility methods for SC contexts.
 * <p>
 * These methods correspond (roughly) to the interface  {@link ISCContext}.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
@Deprecated
abstract class SCContextUtil {

	static public ISCCarrierSet[] getSCCarrierSets(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] elements = element.getChildrenOfType(ISCCarrierSet.ELEMENT_TYPE);
		return (ISCCarrierSet[]) elements; 
	}

	static public ISCConstant[] getSCConstants(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = element.getFilteredChildrenList(ISCConstant.ELEMENT_TYPE);
		SCConstant[] constants = new SCConstant[list.size()];
		list.toArray(constants);
		return constants; 
	}

	static public ISCAxiom[] getSCAxioms(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = element.getFilteredChildrenList(ISCAxiom.ELEMENT_TYPE);
		SCAxiom[] axioms = new SCAxiom[list.size()];
		list.toArray(axioms);
		return axioms; 
	}

	static public ISCTheorem[] getSCTheorems(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = element.getFilteredChildrenList(ISCTheorem.ELEMENT_TYPE);
		SCTheorem[] theorems = new SCTheorem[list.size()];
		list.toArray(theorems);
		return theorems; 
	}

}
