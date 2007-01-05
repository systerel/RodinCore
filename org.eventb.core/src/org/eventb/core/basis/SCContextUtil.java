/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCTheorem;
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
		return element.getChildrenOfType(ISCCarrierSet.ELEMENT_TYPE); 
	}

	static public ISCConstant[] getSCConstants(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getChildrenOfType(ISCConstant.ELEMENT_TYPE); 
	}

	static public ISCAxiom[] getSCAxioms(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getChildrenOfType(ISCAxiom.ELEMENT_TYPE);
	}

	static public ISCTheorem[] getSCTheorems(RodinElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getChildrenOfType(ISCTheorem.ELEMENT_TYPE); 
	}

}
