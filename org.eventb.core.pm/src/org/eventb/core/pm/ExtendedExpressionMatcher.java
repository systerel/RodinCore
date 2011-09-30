/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * A basic implementation for an extended expression matcher.
 * <p> This class is parameterised on the type of the extension of the operator. Contributors
 * to the extension point '<code>org.eventb.core.pm.extendedExpressionMatcher</code>' should extend
 * this class.
 * <p> This class is intended to be extended by clients.
 * @author maamria
 * @since 1.0
 *
 */
public abstract class ExtendedExpressionMatcher<E extends IExpressionExtension> extends ExpressionMatcher<ExtendedExpression>{

	private Class<E> extensionClass;
	
	public ExtendedExpressionMatcher(Class<E> extensionClass) {
		super(ExtendedExpression.class);
		this.extensionClass = extensionClass;
	}

	@Override
	protected ExtendedExpression getExpression(Expression e) {
		return (ExtendedExpression) e;
	}
	
	public Class<E> getExtensionClass(){
		return extensionClass;
	}
}
