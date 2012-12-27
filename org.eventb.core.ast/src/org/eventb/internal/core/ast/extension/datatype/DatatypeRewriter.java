/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
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
		super(translation.getTargetFormulaFactory());
		this.translation = translation;
	}

	@Override
	public BoundIdentDecl rewrite(BoundIdentDecl decl) {
		final Type type = decl.getType();
		final Type newType = translation.translate(type);
		if (newType == type) {
			return decl;
		}
		final String name = decl.getName();
		final SourceLocation sLoc = decl.getSourceLocation();
		return ff.makeBoundIdentDecl(name, sLoc, newType);
	}

	@Override
	public Expression rewrite(AtomicExpression expression) {
		final Type type = expression.getType();
		final Type newType = translation.translate(type);
		if (newType == type) {
			return expression;
		}
		final SourceLocation sLoc = expression.getSourceLocation();
		return ff.makeAtomicExpression(expression.getTag(), sLoc, newType);
	}

	@Override
	public Expression rewrite(BoundIdentifier identifier) {
		final Type type = identifier.getType();
		final Type newType = translation.translate(type);
		if (newType == type) {
			return identifier;
		}
		final SourceLocation sLoc = identifier.getSourceLocation();
		final int index = identifier.getBoundIndex();
		return ff.makeBoundIdentifier(index, sLoc, newType);
	}

	@Override
	public Expression rewrite(FreeIdentifier ident) {
		final Type type = ident.getType();
		final Type newType = translation.translate(type);
		if (newType == type) {
			return ident;
		}
		final SourceLocation sLoc = ident.getSourceLocation();
		final String name = ident.getName();
		return ff.makeFreeIdentifier(name, sLoc, newType);
	}

	@Override
	public Expression rewrite(SetExtension src, SetExtension expr) {
		if (expr.getChildCount() != 0) {
			return expr;
		}
		final Type type = expr.getType();
		final Type newType = translation.translate(type);
		if (newType == type) {
			return expr;
		}
		final SourceLocation sLoc = expr.getSourceLocation();
		return ff.makeEmptySetExtension(newType, sLoc);
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
			final SourceLocation sLoc = src.getSourceLocation();
			final Type type = translation.translate(src.getType());
			return ff.makeExtendedExpression(extension, newChildExprs,
					newChildPreds, sLoc, type);
		}
	}

}