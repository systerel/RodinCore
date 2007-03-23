/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public enum ParseProblem implements IRodinProblem {

	BoundIdentifierIsAlreadyBoundWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_BoundIdentifierIsAlreadyBound, 
			ParseProblem.CATEGORY_LEGIBILITY),
	CircularityError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_Circularity, 
			ParseProblem.CATEGORY_TYPING),
	FreeIdentifierHasBoundOccurencesWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_FreeIdentifierHasBoundOccurences, 
			ParseProblem.CATEGORY_LEGIBILITY),
	InternalError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InternalError, 
			ParseProblem.CATEGORY_NONE),
	LexerError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_LexerError, 
			ParseProblem.CATEGORY_LEXICAL),
	SyntaxError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_SyntaxError, 
			ParseProblem.CATEGORY_SYNTAX),
	TypeCheckError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeCheckFailure, 
			ParseProblem.CATEGORY_TYPING),
	TypesDoNotMatchError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypesDoNotMatch, 
			ParseProblem.CATEGORY_TYPING),
	TypeUnknownError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeUnknown, 
			ParseProblem.CATEGORY_TYPING);
	
	public static final int CATEGORY_NONE = 0;
	public static final int CATEGORY_LEXICAL = 1;
	public static final int CATEGORY_SYNTAX = 2;
	public static final int CATEGORY_LEGIBILITY = 4;
	public static final int CATEGORY_TYPING = 8;
	
	private final String errorCode;
	
	private final String message;
	
	private final int severity;
	
	private final int category;

	private ParseProblem(int severity, String message, int category) {
		this.severity = severity;
		this.message = message;
		this.category = category;
		this.errorCode = EventBPlugin.PLUGIN_ID + "." + name();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getSeverity()
	 */
	public int getSeverity() {
		return severity;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getErrorCode()
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getLocalizedMessage(java.lang.Object[])
	 */
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

	public int getCategory() {
		return category;
	}
}
