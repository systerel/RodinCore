/*******************************************************************************
 * Copyright (c) 2013, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.ast.DefaultTypeCheckingRewriter;

/**
 * Rewriter that traverses formulas (see ITypeCheckingRewriter) to translate
 * datatypes into Event-B without datatypes mathematical extensions. The
 * translation of extended expression nodes is left to a translator retrieved
 * from the translation context.
 * <p>
 * Only particular methods of {@link DefaultTypeCheckingRewriter} are
 * overridden. These methods correspond to all cases where the type of the
 * expression needs to be translated explicitly (leaf nodes of the AST).
 * </p>
 */
public class DatatypeRewriter extends DefaultTypeCheckingRewriter {

	private final DatatypeTranslation translation;

	public DatatypeRewriter(DatatypeTranslation translation) {
		super(translation.getTypeRewriter());
		this.translation = translation;
	}

	@Override
	public Expression rewrite(ExtendedExpression src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final IExpressionExtension extension = src.getExtension();
		final Object origin = extension.getOrigin();
		if (origin instanceof IDatatype) {
			// Datatype operators should not have predicate children
			assert newChildPreds.length == 0;
			return translation.translate(src, newChildExprs);
		} else {
			// Not a datatype operator, just translate the type
			return super.rewrite(src, changed, newChildExprs, newChildPreds);
		}
	}

}
