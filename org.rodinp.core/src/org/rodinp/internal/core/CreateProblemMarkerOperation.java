/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IMarker.SEVERITY;
import static org.rodinp.core.IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST;
import static org.rodinp.core.IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_ATTRIBUTE_KIND;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_MARKER_LOCATION;
import static org.rodinp.core.RodinMarkerUtil.ARGUMENTS;
import static org.rodinp.core.RodinMarkerUtil.ATTRIBUTE_ID;
import static org.rodinp.core.RodinMarkerUtil.CHAR_END;
import static org.rodinp.core.RodinMarkerUtil.CHAR_START;
import static org.rodinp.core.RodinMarkerUtil.ELEMENT;
import static org.rodinp.core.RodinMarkerUtil.ERROR_CODE;
import static org.rodinp.core.RodinMarkerUtil.RODIN_PROBLEM_MARKER;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.internal.core.util.Messages;

/**
 * Creation of a problem marker on a Rodin element.
 * <p>
 * This operation creates a Rodin problem marker on any element of the database.
 * The marker is attached on the resource that corresponds or contains (if
 * internal) the Rodin element to mark.
 * 
 * <p>
 * Required Attributes:
 * <ul>
 * <li>The Rodin element to mark
 * <li>The severity of the problem
 * <li>The description of the problem
 * <li>The id of the problem
 * <li>The arguments of the problem
 * </ul>
 * 
 * <p>
 * Optional Attributes:
 * <ul>
 * <li>The id of the attribute that contains the problem
 * <li>The location in the attribute (start .. end)
 * </ul>
 */
public class CreateProblemMarkerOperation extends RodinDBOperation {

	private final IRodinProblem problem;
	private final Object[] args;
	private final IAttributeType attrType;
	private final int charStart;
	private final int charEnd;
	
	/**
	 * When executed, this operation will create a problem marker on the given
	 * Rodin element.
	 */
	public CreateProblemMarkerOperation(IRodinElement element,
			IRodinProblem problem, Object[] args, IAttributeType attrType,
			int charStart, int charEnd) {
		super(element);
		this.problem = problem;
		this.args = args;
		this.attrType = attrType;
		this.charStart = charStart;
		this.charEnd = charEnd;
	}

	private IResource getResourceToMark() {
		return elementsToProcess[0].getUnderlyingResource();
	}

	/**
	 * Creates a new marker.
	 * 
	 * @exception RodinDBException
	 *                if unable to create or set the marker
	 */
	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_createProblemMarkerProgress, 1);
			final IResource resource = getResourceToMark();
			IMarker marker = resource.createMarker(RODIN_PROBLEM_MARKER);
			marker.setAttribute(SEVERITY, problem.getSeverity());
			marker.setAttribute(MESSAGE, problem.getLocalizedMessage(args));
			marker.setAttribute(ERROR_CODE, problem.getErrorCode());
			marker.setAttribute(ARGUMENTS, encodeArgs());
			final IRodinElement element = elementsToProcess[0];
			if (element instanceof IInternalElement) {
				marker.setAttribute(ELEMENT, element.getHandleIdentifier());
				if (attrType != null) {
					marker.setAttribute(ATTRIBUTE_ID, attrType.getId());
					if (charStart >= 0) {
						marker.setAttribute(CHAR_START, charStart);
						marker.setAttribute(CHAR_END, charEnd);
					}
				}
			}
		} catch (CoreException ce) {
			throw new RodinDBException(ce);
		} finally {
			done();
		}
	}

	private String encodeArgs() {
		if (args.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Object arg: args) {
			final String string = arg.toString();
			builder.append(string.length());
			builder.append(':');
			builder.append(string);
		}
		return builder.toString();
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		IResource resource = getResourceToMark();
		IWorkspace workspace = resource.getWorkspace();
		return workspace.getRuleFactory().markerRule(resource);
	}


	@Override
	public boolean modifiesResources() {
		return true;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the element supplied to the operation is
	 * <code>null</code>.
	 * <li>ELEMENT_DOES_NOT_EXIST - <code>element</code> does not exist.
	 * <li>ATTRIBUTE_DOES_NOT_EXIST - attribute <code>attrType</code>is
	 * not defined for <code>element</code>
	 * <li>INVALID_ATTRIBUTE_KIND - a location is given and the attribute is
	 * not of kind <code>string</code>
	 * <li>INVALID_MARKER_LOCATION - location doesn't fulfill contraints.
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		final IRodinDBStatus status = super.verify();
		if (! status.isOK()) {
			return status;
		}
		final IRodinElement element = elementsToProcess[0];
		if (! element.exists()) {
			return new RodinDBStatus(ELEMENT_DOES_NOT_EXIST, element);
		}
		if (attrType != null) {
			return verifyLocation(element);
		}
		return verifyNoLocation();
	}

	private IRodinDBStatus verifyLocation(IRodinElement element) {
		if (! (element instanceof InternalElement)) {
			return errorStatus("Attribute id allowed only for internal elements");
		}
		IRodinDBStatus status =
			verifyAttributeId((InternalElement) element, charStart >= 0);
		if (! status.isOK()) {
			return status;
		}
		if (charStart >= 0) {
			if (charStart >= charEnd) {
				return errorStatus("End position is before start position");
			}
		} else if (charEnd >= 0) {
			return errorStatus("End position without a start position");
		}
		return RodinDBStatus.VERIFIED_OK;
	}

	private IRodinDBStatus verifyAttributeId(InternalElement element,
			boolean withCharPos) {
		
		// Check that attribute exists
		try {
			if (withCharPos && !element.hasAttribute(attrType)) {
				return new RodinDBStatus(ATTRIBUTE_DOES_NOT_EXIST, element,
						attrType.getId());
			}
		} catch (RodinDBException rde) {
			return rde.getRodinDBStatus();
		}
		if (withCharPos) {
			// Check that it's an attribute of kind String
			if (! (attrType instanceof IAttributeType.String)) {
				return new RodinDBStatus(INVALID_ATTRIBUTE_KIND, attrType.getId());
			}
		}
		return RodinDBStatus.VERIFIED_OK;
	}

	private IRodinDBStatus verifyNoLocation() {
		if (charStart >= 0) {
			return errorStatus("Start position without an attribute id");
		}
		if (charEnd >= 0) {
			return errorStatus("End position without an attribute id");
		}
		return RodinDBStatus.VERIFIED_OK;
	}
	
	private IRodinDBStatus errorStatus(String message) {
		final Exception exc = new IllegalArgumentException(message);
		return new RodinDBStatus(INVALID_MARKER_LOCATION, exc);
	}
	
}
