/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common implementation for Event-B elements.
 * <p>
 * This implementation is intended to be sub-classed by clients.
 * </p>
 * 
 * @author htson
 * 
 */
public abstract class EventBElement extends InternalElement {

	public EventBElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IAssignmentElement#getAssignment()
	 */
	public String getAssignmentString() throws RodinDBException {
		return getContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IAssignmentElement#setAssignment(java.lang.String)
	 */
	public void setAssignmentString(String assignment) throws RodinDBException {
		setContents(assignment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ILabeledElement#setLabel(java.lang.String)
	 */
	public void setLabel(String label, IProgressMonitor monitor)
			throws RodinDBException {
		CommonAttributesUtil.setLabel(this, label, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ILabeledElement#getLabel()
	 */
	public String getLabel(IProgressMonitor monitor) throws RodinDBException {
		return CommonAttributesUtil.getLabel(this, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#setComment(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		CommonAttributesUtil.setComment(this, comment, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#getComment(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getComment(IProgressMonitor monitor) throws RodinDBException {
		return CommonAttributesUtil.getComment(this, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPredicateElement#getPredicateString()
	 */
	public String getPredicateString() throws RodinDBException {
		return getContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPredicateElement#setPredicateString(java.lang.String)
	 */
	public void setPredicateString(String predicate) throws RodinDBException {
		setContents(predicate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IExpressionElement#getExpressionString()
	 */
	public String getExpressionString() throws RodinDBException {
		return getContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IExpressionElement#setExpressionString(java.lang.String)
	 */
	public void setExpressionString(String expression) throws RodinDBException {
		setContents(expression);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IIdentifierElement#getIdentifierString()
	 */
	public String getIdentifierString() throws RodinDBException {
		return getContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IIdentifierElement#setIdentifierString(java.lang.String)
	 */
	public void setIdentifierString(String identifier) throws RodinDBException {
		setContents(identifier);
	}
}
