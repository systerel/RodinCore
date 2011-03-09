/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Dusseldorf - added theorem attribute
 *     Systerel - added method getEventBProject()
 *     Systerel - added methods for generated elements
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IConvergenceElement;
import org.eventb.internal.core.EventBProject;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
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
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public abstract class EventBElement extends InternalElement {

	public EventBElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Deprecated
	protected < T extends IRodinElement> T getSingletonChild(
			IElementType<T> elementType,
			String message) throws RodinDBException {

		return EventBUtil.getSingletonChild(this, elementType, message);
	}
	
	public boolean hasAssignmentString() throws RodinDBException {
		return hasAttribute(EventBAttributes.ASSIGNMENT_ATTRIBUTE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IAssignmentElement#getAssignment()
	 */
	public String getAssignmentString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.ASSIGNMENT_ATTRIBUTE);
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
	
	public boolean hasLabel() throws RodinDBException {
		return hasAttribute(EventBAttributes.LABEL_ATTRIBUTE);
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
	public String getLabel() throws RodinDBException {
		return getAttributeValue(EventBAttributes.LABEL_ATTRIBUTE);
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
	 * @see org.eventb.core.ICommentedElement#hasComment()
	 */
	public boolean hasComment() throws RodinDBException {
		return hasAttribute(EventBAttributes.COMMENT_ATTRIBUTE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#getComment(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getComment() throws RodinDBException {
		return getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE);
	}
	
	public boolean hasPredicateString() throws RodinDBException {
		return hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE);
	}

	public boolean isAccurate() throws RodinDBException {
		return getAttributeValue(EventBAttributes.ACCURACY_ATTRIBUTE);
	}
	
	public void setAccuracy(boolean accurate, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.ACCURACY_ATTRIBUTE, accurate, monitor);
	}
	
	public boolean hasPOStamp() throws RodinDBException {
		return hasAttribute(EventBAttributes.POSTAMP_ATTRIBUTE);
	}
	
	public long getPOStamp() throws RodinDBException {
		return getAttributeValue(EventBAttributes.POSTAMP_ATTRIBUTE);
	}
	
	public void setPOStamp(long stamp, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.POSTAMP_ATTRIBUTE, stamp, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPredicateElement#getPredicateString()
	 */
	public String getPredicateString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE);
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
	
	public boolean hasExpressionString() throws RodinDBException {
		return hasAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IExpressionElement#getExpressionString()
	 */
	public String getExpressionString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.EXPRESSION_ATTRIBUTE);
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

	public boolean hasIdentifierString() throws RodinDBException {
		return hasAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IIdentifierElement#getIdentifierString()
	 */
	public String getIdentifierString() throws RodinDBException {
		return getAttributeValue(EventBAttributes.IDENTIFIER_ATTRIBUTE);
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
	
	public boolean hasConvergence() throws RodinDBException {
		return hasAttribute(EventBAttributes.CONVERGENCE_ATTRIBUTE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IConvergenceElement#setConvergence(int, IProgressMonitor)
	 */
	public void setConvergence(IConvergenceElement.Convergence value, IProgressMonitor monitor) throws RodinDBException {
		int intValue = value.getCode();
		setAttributeValue(EventBAttributes.CONVERGENCE_ATTRIBUTE, intValue, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IConvergenceElement#getConvergence(IProgressMonitor)
	 */
	public IConvergenceElement.Convergence getConvergence() throws RodinDBException {
		int intValue = getAttributeValue(EventBAttributes.CONVERGENCE_ATTRIBUTE);
		try {
			return IConvergenceElement.Convergence.valueOf(intValue);
		} catch (IllegalArgumentException e) {
			throw Util.newRodinDBException(
					Messages.database_EventInvalidConvergenceFailure, this);
		}
	}

	public void setSource(IRodinElement source, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE, source, monitor);
	}

	public IRodinElement getSource() throws RodinDBException {
		return getAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE);
	}

	public boolean hasTheorem() throws RodinDBException {
		return true;
	}

	public boolean isTheorem() throws RodinDBException {
		final IAttributeType.Boolean aType = EventBAttributes.THEOREM_ATTRIBUTE;
		return hasAttribute(aType) && getAttributeValue(aType);
	}

	public void setTheorem(boolean newValue, IProgressMonitor monitor)
			throws RodinDBException {
		final IAttributeType.Boolean aType = EventBAttributes.THEOREM_ATTRIBUTE;
		if (newValue) {
			setAttributeValue(aType, newValue, monitor);
		} else {
			removeAttribute(aType, monitor);
		}
	}

	public boolean hasGenerated() throws RodinDBException {
		return true;
	}

	public boolean isGenerated() throws RodinDBException {
		final IAttributeType.Boolean aType = EventBAttributes.GENERATED_ATTRIBUTE;
		if (hasAttribute(aType) && getAttributeValue(aType)) {
			return true;
		}
		if (parent instanceof EventBElement) {
			return ((EventBElement) parent).isGenerated();
		}
		return false;
	}

	public void setGenerated(boolean newValue, IProgressMonitor monitor)
			throws RodinDBException {
		final IAttributeType.Boolean aType = EventBAttributes.GENERATED_ATTRIBUTE;
		if (newValue) {
			setAttributeValue(aType, newValue, monitor);
		} else {
			removeAttribute(aType, monitor);
		}
	}

	public final EventBProject getEventBProject() {
		return new EventBProject(getRodinProject());
	}

}
