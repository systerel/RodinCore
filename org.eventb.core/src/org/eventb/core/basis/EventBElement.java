/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
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
	public String getAssignmentString(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.ASSIGNMENT_ATTRIBUTE, monitor);
	}

	@Deprecated
	public String getAssignmentString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.ASSIGNMENT_ATTRIBUTE, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IAssignmentElement#setAssignment(java.lang.String)
	 */
	public void setAssignmentString(String assignment, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.ASSIGNMENT_ATTRIBUTE, assignment, monitor);
	}

	@Deprecated
	public void setAssignmentString(String assignment) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.ASSIGNMENT_ATTRIBUTE, assignment, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ILabeledElement#setLabel(java.lang.String)
	 */
	public void setLabel(String label, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.LABEL_ATTRIBUTE, label, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ILabeledElement#getLabel()
	 */
	public String getLabel(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.LABEL_ATTRIBUTE, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#setComment(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, comment, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#getComment(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getComment(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPredicateElement#getPredicateString()
	 */
	public String getPredicateString(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, monitor);
	}

	@Deprecated
	public String getPredicateString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPredicateElement#setPredicateString(java.lang.String)
	 */
	public void setPredicateString(String predicate, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, predicate, monitor);
	}

	@Deprecated
	public void setPredicateString(String predicate) throws RodinDBException {
		setAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, predicate, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IExpressionElement#getExpressionString()
	 */
	public String getExpressionString(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.EXPRESSION_ATTRIBUTE, monitor);
	}

	@Deprecated
	public String getExpressionString() throws RodinDBException {
		return getExpressionString(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IExpressionElement#setExpressionString(java.lang.String)
	 */
	public void setExpressionString(String expression, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.EXPRESSION_ATTRIBUTE, expression, monitor);
	}

	@Deprecated
	public void setExpressionString(String expression) 
	throws RodinDBException {
		setExpressionString(expression, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IIdentifierElement#getIdentifierString()
	 */
	public String getIdentifierString(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.IDENTIFIER_ATTRIBUTE, monitor);
	}

	@Deprecated
	public String getIdentifierString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.IDENTIFIER_ATTRIBUTE, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IIdentifierElement#setIdentifierString(java.lang.String)
	 */
	public void setIdentifierString(String identifier, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.IDENTIFIER_ATTRIBUTE, identifier, monitor);
	}
	
	@Deprecated
	public void setIdentifierString(String identifier) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.IDENTIFIER_ATTRIBUTE, identifier, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IConvergenceElement#setConvergence(int, IProgressMonitor)
	 */
	public void setConvergence(int value, IProgressMonitor monitor) throws RodinDBException {
		if (value < 0 || value > 2)
			throw Util.newRodinDBException(
					Messages.database_EventSetInvalidConvergenceFailure,
					this
			);
		setAttributeValue(EventBAttributes.CONVERGENCE_ATTRIBUTE, value, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IConvergenceElement#getConvergence(IProgressMonitor)
	 */
	public int getConvergence(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.CONVERGENCE_ATTRIBUTE, monitor);
	}

	public void setSource(IRodinElement source, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE, source, monitor);
	}

	public IRodinElement getSource(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE, monitor);
	}

	public void setForbidden(boolean value, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.FORBIDDEN_ATTRIBUTE, value, monitor);
	}

	public boolean isForbidden(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.FORBIDDEN_ATTRIBUTE, monitor);
	}

	public void setPreserved(boolean value, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PRESERVED_ATTRIBUTE, value, monitor);
	}

	public boolean isPreserved(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.PRESERVED_ATTRIBUTE, monitor);
	}

	@Deprecated
	public void setForbidden(boolean value) throws RodinDBException {
		setForbidden(value, null);
	}

	@Deprecated
	public boolean isForbidden() throws RodinDBException {
		return isForbidden(null);
	}


}
