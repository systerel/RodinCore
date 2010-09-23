/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Extracted categories in separate class
 *     Systerel - added InvalidTypeExpressionError
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
 * @since 1.0
 */
public enum ParseProblem implements IRodinProblem {

	BoundIdentifierIsAlreadyBoundWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_BoundIdentifierIsAlreadyBound, 
			ParseProblemCategory.CATEGORY_LEGIBILITY),
	CircularityError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_Circularity, 
			ParseProblemCategory.CATEGORY_TYPING),
	FreeIdentifierHasBoundOccurencesWarning(
			IMarker.SEVERITY_WARNING, 
			Messages.scuser_FreeIdentifierHasBoundOccurences, 
			ParseProblemCategory.CATEGORY_LEGIBILITY),
	InternalError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InternalError, 
			ParseProblemCategory.CATEGORY_NONE),
	/**
	 * @since 2.0
	 */
	InvalidTypeExpressionError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_InvalidTypeExpression, 
			ParseProblemCategory.CATEGORY_TYPING),
	LexerError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_LexerError, 
			ParseProblemCategory.CATEGORY_LEXICAL),
	SyntaxError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_SyntaxError, 
			ParseProblemCategory.CATEGORY_SYNTAX),
	TypeCheckError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeCheckFailure, 
			ParseProblemCategory.CATEGORY_TYPING),
	TypesDoNotMatchError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypesDoNotMatch, 
			ParseProblemCategory.CATEGORY_TYPING),
	TypeUnknownError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_TypeUnknown, 
			ParseProblemCategory.CATEGORY_TYPING),
	MinusAppliedToSetError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MinusAppliedToSet, 
			ParseProblemCategory.CATEGORY_TYPING),
	MulAppliedToSetError(
			IMarker.SEVERITY_ERROR, 
			Messages.scuser_MulAppliedToSet, 
			ParseProblemCategory.CATEGORY_TYPING),
	;
	
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

	@Override
	public int getSeverity() {
		return severity;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

	public int getCategory() {
		return category;
	}
}
