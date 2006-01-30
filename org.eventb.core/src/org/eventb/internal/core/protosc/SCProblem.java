/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import org.eclipse.core.resources.IMarker;
import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IInternalElement;

/**
 * @author halstefa
 *
 */
public class SCProblem {

	public final static int SEVERITY_ERROR = IMarker.SEVERITY_ERROR;
	public final static int SEVERITY_WARNING = IMarker.SEVERITY_WARNING;
	public final static int SEVERITY_INFO = IMarker.SEVERITY_INFO;
	
	private final IInternalElement element;
	private final String message;
	private final int severity;
	
	private final SourceLocation location = null;
	private final Object[] arguments = null; 
	
	/**
	 * 
	 */
	public SCProblem(IInternalElement element, String message, int severity) {
		this.element = element;
		this.message = message;
		this.severity = severity;
	}

	/**
	 * @return Returns the element.
	 */
	public IInternalElement getElement() {
		return element;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return Returns the severity.
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * @return Returns the arguments.
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * @return Returns the location.
	 */
	public SourceLocation getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return element.getElementName() + ":" + message;
	}
	
}
