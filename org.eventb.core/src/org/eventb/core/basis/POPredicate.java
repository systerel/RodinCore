/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicate;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B PO predicate as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOPredicate</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class POPredicate extends InternalElement implements IPOPredicate {

	public POPredicate(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	public String getName() {
		return getElementName();
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public String getPredicate() throws RodinDBException {
		return getContents();
	}

	public void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException {
		setContents(predicate.toStringWithTypes(), monitor);
	}

	public void setSource(IRodinElement source, IProgressMonitor monitor) throws RodinDBException {
		CommonAttributesUtil.setSource(this, source, monitor);
	}

	public IRodinElement getSource(IProgressMonitor monitor) throws RodinDBException {
		return CommonAttributesUtil.getSource(this, monitor);
	}

	public void setPredicateString(String predicate, IProgressMonitor monitor) throws RodinDBException {
		setContents(predicate, monitor);
	}

}
