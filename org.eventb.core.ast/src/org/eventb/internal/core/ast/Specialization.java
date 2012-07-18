/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.ITypedFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.typecheck.TypeEnvironment;

/**
 * Common implementation for specializations. To ensure compatibility of the
 * type and identifier substitution, we check that no substitution is entered
 * twice and we also remember, for each identifier substitution the list of
 * given types that must not change afterwards. A type substitution is also
 * doubled as an identifier substitution.
 * 
 * @author Laurent Voisin
 */
public class Specialization extends DefaultRewriter implements ISpecialization,
		ITypedFormulaRewriter {

	// Type substitutions
	private final Map<GivenType, Type> typeSubst;

	// Identifier substitutions
	private final Map<FreeIdentifier, Expression> identSubst;

	public Specialization(FormulaFactory ff) {
		super(false, ff);
		typeSubst = new HashMap<GivenType, Type>();
		identSubst = new HashMap<FreeIdentifier, Expression>();
	}

	public Specialization(Specialization other) {
		super(false, other.ff);
		typeSubst = new HashMap<GivenType, Type>(other.typeSubst);
		identSubst = new HashMap<FreeIdentifier, Expression>(other.identSubst);
	}

	@Override
	public ISpecialization clone() {
		return new Specialization(this);
	}

	@Override
	public void put(GivenType type, Type value) {
		if (type == null)
			throw new NullPointerException("Null given type");
		if (value == null)
			throw new NullPointerException("Null type");
		final Type oldValue = typeSubst.put(type, value);
		if (oldValue != null && !oldValue.equals(value)) {
			typeSubst.put(type, oldValue); // repair
			throw new IllegalArgumentException("Type substitution for " + type
					+ " already registered");
		}
		identSubst.put(type.toExpression(ff), value.toExpression(ff));
	}

	public Type get(GivenType key) {
		final Type value = typeSubst.get(key);
		if (value == null)
			return key;
		return value;
	}

	public Collection<Type> getSubstitutionTypes() {
		return typeSubst.values();
	}

	public Map<GivenType, Type> getTypeSubstitutions() {
		return typeSubst;
	}

	public Map<FreeIdentifier, Expression> getIndentifierSubstitutions() {
		return identSubst;
	}

	@Override
	public void put(FreeIdentifier ident, Expression value) {
		if (ident == null)
			throw new NullPointerException("Null identifier");
		if (!ident.isTypeChecked())
			throw new IllegalArgumentException("Untyped identifier");
		if (value == null)
			throw new NullPointerException("Null value");
		if (!value.isWellFormed())
			throw new IllegalArgumentException("Ill-formed value");
		if (!value.isTypeChecked())
			throw new IllegalArgumentException("Untyped value");
		verify(ident, value);
		final Expression oldValue = identSubst.put(ident, value);
		if (oldValue != null && !oldValue.equals(value)) {
			identSubst.put(ident, oldValue); // repair
			throw new IllegalArgumentException("Identifier substitution for "
					+ ident + " already registered");
		}
	}

	/*
	 * Checks that a new substitution is compatible. We also save the given sets
	 * that are now frozen and must not change afterwards.
	 */
	private void verify(FreeIdentifier ident, Expression value) {
		final Type identType = ident.getType();
		final Type newType = identType.specialize(this);
		if (!value.getType().equals(newType)) {
			throw new IllegalArgumentException("Incompatible types for "
					+ ident);
		}
		freezeSetsFor(identType);
	}

	/*
	 * To freeze a set, we just add a substitution to itself, so that it cannot
	 * be substituted to something else afterwards.
	 */
	private void freezeSetsFor(Type identType) {
		for (final GivenType gt : identType.getGivenTypes()) {
			if (!typeSubst.containsKey(gt)) {
				typeSubst.put(gt, gt);
			}
		}
	}

	/*
	 * Specializing a type environment consists in, starting from an empty type
	 * environment, adding all given sets and free identifiers that occur in the
	 * result of substitutions for identifiers from the original type
	 * environment.
	 */
	public ITypeEnvironment specialize(TypeEnvironment typenv) {
		final ITypeEnvironment result = ff.makeTypeEnvironment();
		final IIterator iter = typenv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final FreeIdentifier ident = ff.makeFreeIdentifier(iter.getName(),
					null, iter.getType());
			final Expression expr = this.get(ident);
			for (final GivenType gt : expr.getGivenTypes()) {
				result.addGivenSet(gt.getName());
			}
			for (final FreeIdentifier free : expr.getFreeIdentifiers()) {
				result.add(free);
			}
		}
		return result;
	}

	public Expression get(FreeIdentifier ident) {
		final Expression value = identSubst.get(ident);
		if (value != null) {
			return value;
		}
		final Type type = ident.getType();
		final Type newType = type.specialize(this);
		final Expression result;
		if (newType == type) {
			result = ident;
		} else {
			result = ff.makeFreeIdentifier(ident.getName(),
					ident.getSourceLocation(), newType);
		}
		identSubst.put(ident, result);
		return result;
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		final Expression newIdent = get(identifier);
		if (newIdent.equals(identifier)) {
			return identifier;
		}
		return newIdent;
	}

	public BoundIdentDecl rewrite(BoundIdentDecl decl) {
		final Type type = decl.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return decl;
		}
		final String name = decl.getName();
		final SourceLocation sloc = decl.getSourceLocation();
		return ff.makeBoundIdentDecl(name, sloc, newType);
	}

	@Override
	public Expression rewrite(BoundIdentifier identifier) {
		final Type type = identifier.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return identifier;
		}
		return ff.makeBoundIdentifier(identifier.getBoundIndex(),
				identifier.getSourceLocation(), newType);
	}

	@Override
	public Expression rewrite(QuantifiedExpression expression) {
		final BoundIdentDecl[] decls = expression.getBoundIdentDecls();
		final BoundIdentDecl[] newDecls = specialize(decls);
		if (newDecls == decls) {
			return expression;
		}
		return ff.makeQuantifiedExpression(expression.getTag(), newDecls,
				expression.getPredicate(), expression.getExpression(),
				expression.getSourceLocation(), expression.getForm());
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
		final BoundIdentDecl[] newDecls = specialize(decls);
		if (newDecls == decls) {
			return predicate;
		}
		return ff.makeQuantifiedPredicate(predicate.getTag(), newDecls,
				predicate.getPredicate(), predicate.getSourceLocation());
	}

	private BoundIdentDecl[] specialize(BoundIdentDecl[] decls) {
		final BoundIdentDecl[] result = new BoundIdentDecl[decls.length];
		boolean changed = false;
		for (int i = 0; i < decls.length; i++) {
			result[i] = rewrite(decls[i]);
			changed |= result[i] != decls[i];
		}
		if (!changed) {
			return decls;
		}
		return result;
	}

	@Override
	public Expression rewrite(AtomicExpression expression) {
		final Type type = expression.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return expression;
		}
		final SourceLocation loc = expression.getSourceLocation();
		return ff.makeAtomicExpression(expression.getTag(), loc, newType);
	}

	public Expression rewrite(ExtendedExpression expr, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final Type type = expr.getType();
		final Type newType = type.specialize(this);
		if (!changed && newType == type) {
			return expr;
		}
		final IExpressionExtension extension = expr.getExtension();
		final SourceLocation loc = expr.getSourceLocation();
		return ff.makeExtendedExpression(extension, newChildExprs,
				newChildPreds, loc, newType);
	}

	/*
	 * For a set extension, the only special case is that of an empty extension,
	 * where we have to specialize the type.
	 */
	@Override
	public Expression rewrite(SetExtension expression) {
		if (expression.getChildCount() != 0) {
			return expression;
		}
		final Type type = expression.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return expression;
		}
		final SourceLocation sloc = expression.getSourceLocation();
		return ff.makeEmptySetExtension(newType, sloc);
	}

	@Override
	public Predicate checkReplacement(Predicate current, Predicate replacement) {
		return replacement;
	}

	@Override
	public Expression checkReplacement(Expression current,
			Expression replacement) {
		return replacement;
	}

	@Override
	public BoundIdentDecl checkReplacement(BoundIdentDecl current,
			BoundIdentDecl replacement) {
		return replacement;
	}

	// For debugging purposes
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		for (Entry<GivenType, Type> entry : typeSubst.entrySet()) {
			sb.append(sep);
			sep = " || ";
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		for (Entry<FreeIdentifier, Expression> entry : identSubst.entrySet()) {
			sb.append(sep);
			sep = " || ";
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		return sb.toString();
	}

}
