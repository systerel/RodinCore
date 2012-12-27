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
package org.rodinp.core.tests;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.rodinp.core.IRodinProblem;

/**
 * Various problems used in tests 
 * 
 * @author Laurent Voisin
 */
public enum TestProblem implements IRodinProblem {

	err0(IMarker.SEVERITY_ERROR, "err0"),
	
	info2(IMarker.SEVERITY_INFO, "info2({0}, {1})"),
	
	warn1(IMarker.SEVERITY_WARNING, "warn1 ({0})");
	
	private final static String PREFIX = PLUGIN_ID + ".";
	
	public static TestProblem valueOfErrorCode(String errorCode) {
		if (! errorCode.startsWith(PREFIX)) {
			return null;
		}
		return valueOf(errorCode.substring(PREFIX.length()));
	}
	
	private final String errorCode;
	
	private final String message;
	
	private final int severity;

	private TestProblem(int severity, String message) {
		this.severity = severity;
		this.message = message;
		this.errorCode = PREFIX + name();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}
	
	public int getSeverity() {
		return severity;
	}
}
