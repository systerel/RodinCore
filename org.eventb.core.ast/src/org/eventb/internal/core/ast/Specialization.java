/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southamtpon - added support for predicate varialbes.
 *******************************************************************************/
package org.eventb.internal.core.ast;

import static org.eventb.internal.core.ast.Substitute.makeSubstitute;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.SetExtension;
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
 * @author htson - added support for predicate variables.
 */
public class Specialization extends Substitution implements ISpecialization {

	private class SpecializationTypeRewriter extends TypeRewriter {

		public SpecializationTypeRewriter(FormulaFactory ff) {
			super(ff);
		}

		@Override
		public void visit(GivenType type) {
			final Type rewritten = getOrSetDefault(type);
			// If the given type is not rewritten, use the super algorithm
			// (this implements factory translation)
			if (type.equals(rewritten)) {
				super.visit(type);
			} else {
				result = rewritten;
			}
		}

	}

	// Type substitutions
	private final Map<GivenType, Type> typeSubst;

	// Identifier substitutions
	private final Map<FreeIdentifier, Substitute<Expression>> identSubst;
	
	// Predicate variable substitutions
	private final Map<PredicateVariable, Substitute<Predicate>> predSubst;
	
	private final TypeRewriter speTypeRewriter;

	public Specialization(FormulaFactory ff) {
		super(ff);
		typeSubst = new HashMap<GivenType, Type>();
		identSubst = new HashMap<FreeIdentifier, Substitute<Expression>>();
		predSubst = new HashMap<PredicateVariable, Substitute<Predicate>>();
		speTypeRewriter = new SpecializationTypeRewriter(ff);
	}

	public Specialization(Specialization other) {
		super(other.ff);
		typeSubst = new HashMap<GivenType, Type>(other.typeSubst);
		identSubst = new HashMap<FreeIdentifier, Substitute<Expression>>(
				other.identSubst);
		predSubst = new HashMap<PredicateVariable, Substitute<Predicate>>(
				other.predSubst);
		speTypeRewriter = new SpecializationTypeRewriter(other.ff);
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
		if (ff != value.getFactory()) {
			throw new IllegalArgumentException("Wrong factory for value: "
					+ value.getFactory() + ", should be " + ff);
		}
		final Type oldValue = typeSubst.put(type, value);
		if (oldValue != null && !oldValue.equals(value)) {
			typeSubst.put(type, oldValue); // repair
			throw new IllegalArgumentException("Type substitution for " + type
					+ " already registered");
		}
		final Substitute<Expression> subst = makeSubstitute(value.toExpression());
		identSubst.put(type.toExpression(), subst);
	}

	@Override
	public Type get(GivenType key) {
		return typeSubst.get(key);
	}

	public Type getOrSetDefault(GivenType key) {
		Type value = get(key);
		if (value == null) {
			value = key.translate(ff);
			put(key,  value);
		}
		return value;
	}

	@Override
	public void put(FreeIdentifier ident, Expression value) {
		if (ident == null)
			throw new NullPointerException("Null identifier");
		if (!ident.isTypeChecked())
			throw new IllegalArgumentException("Untyped identifier");
		if (value == null)
			throw new NullPointerException("Null value");
		if (ff != value.getFactory()) {
			throw new IllegalArgumentException("Wrong factory for value: "
					+ value.getFactory() + ", should be " + ff);
		}
		if (!value.isTypeChecked())
			throw new IllegalArgumentException("Untyped value");
		verify(ident, value);
		final Substitute<Expression> subst = makeSubstitute(value);
		final Substitute<Expression> oldSubst = identSubst.put(ident, subst);
		if (oldSubst != null && !oldSubst.equals(subst)) {
			identSubst.put(ident, oldSubst); // repair
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
	}

	public Type specialize(Type type) {
		return speTypeRewriter.rewrite(type);
	}

	/*
	 * Specializing a type environment consists in, starting from an empty type
	 * environment, adding all given sets and free identifiers that occur in the
	 * result of substitutions for identifiers from the original type
	 * environment.
	 */
	public ITypeEnvironmentBuilder specialize(TypeEnvironment typenv) {
		final ITypeEnvironmentBuilder result = ff.makeTypeEnvironment();
		final IIterator iter = typenv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final FreeIdentifier ident = iter.asFreeIdentifier();
			final Expression expr = this.getOrSetDefault(ident);
			for (final FreeIdentifier free : expr.getFreeIdentifiers()) {
				result.add(free);
			}
		}
		return result;
	}

	@Override
	public Expression get(FreeIdentifier ident) {
		final Substitute<Expression> subst = identSubst.get(ident);
		return subst == null ? null : subst.getSubstitute(ident, 0);
	}

	private Expression getOrSetDefault(FreeIdentifier ident) {
		final Substitute<Expression> subst = identSubst.get(ident);
		if (subst != null) {
			return subst.getSubstitute(ident, getBindingDepth());
		}
		final Type type = ident.getType();
		final Type newType = type.specialize(this);
		final Expression result;
		if (newType == type) {
			result = super.rewrite(ident);
		} else {
			result = ff.makeFreeIdentifier(ident.getName(),
					ident.getSourceLocation(), newType);
		}
		identSubst.put(ident, makeSubstitute(result));
		return result;
	}

	private Predicate getOrSetDefault(PredicateVariable predVar) {
		final Substitute<Predicate> subst = predSubst.get(predVar);
		if (subst != null) {
			return subst.getSubstitute(predVar, getBindingDepth());
		}
		return super.rewrite(predVar);
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		final Expression newIdent = getOrSetDefault(identifier);
		if (newIdent.equals(identifier)) {
			return super.rewrite(identifier);
		}
		return newIdent;
	}

	/**
	 * Rewrite the predicate variable.
	 * 
	 * @author htson
	 */
	@Override
	public Predicate rewrite(PredicateVariable predVar) {
		final Predicate newPred = getOrSetDefault(predVar);
		if (newPred.equals(predVar)) {
			return super.rewrite(predVar);
		}
		return newPred;
	}

	@Override
	public BoundIdentDecl rewrite(BoundIdentDecl decl) {
		final Type type = decl.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return super.rewrite(decl);
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
			return super.rewrite(identifier);
		}
		return ff.makeBoundIdentifier(identifier.getBoundIndex(),
				identifier.getSourceLocation(), newType);
	}

	@Override
	public Expression rewrite(AtomicExpression expression) {
		final Type type = expression.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return super.rewrite(expression);
		}
		final SourceLocation loc = expression.getSourceLocation();
		return ff.makeAtomicExpression(expression.getTag(), loc, newType);
	}

	@Override
	public Expression rewrite(ExtendedExpression expr, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final Type type = expr.getType();
		final Type newType = type.specialize(this);
		if (!changed && newType == type) {
			return super.rewrite(expr, changed, newChildExprs, newChildPreds);
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
	public Expression rewrite(SetExtension src, SetExtension expr) {
		if (expr.getChildCount() != 0) {
			return expr;
		}
		final Type type = expr.getType();
		final Type newType = type.specialize(this);
		if (newType == type) {
			return super.rewrite(src, expr);
		}
		return ff.makeEmptySetExtension(newType, expr.getSourceLocation());
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
		for (Entry<FreeIdentifier, Substitute<Expression>> entry : identSubst.entrySet()) {
			sb.append(sep);
			sep = " || ";
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		for (Entry<PredicateVariable, Substitute<Predicate>> entry : predSubst.entrySet()) {
			sb.append(sep);
			sep = " || ";
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		return sb.toString();
	}

	@Override
	public boolean canPut(GivenType type, Type value) {
		if (type == null)
			throw new NullPointerException("Null given type");
		if (value == null)
			throw new NullPointerException("Null type");
		if (ff != value.getFactory()) {
			throw new IllegalArgumentException("Wrong factory for value: "
					+ value.getFactory() + ", should be " + ff);
		}
		Type oldValue = typeSubst.get(type);
		if (oldValue != null && !oldValue.equals(value)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean canPut(FreeIdentifier ident, Expression value) {
		if (ident == null)
			throw new NullPointerException("Null identifier");
		if (!ident.isTypeChecked())
			throw new IllegalArgumentException("Untyped identifier");
		if (value == null)
			throw new NullPointerException("Null value");
		if (!value.isTypeChecked())
			throw new IllegalArgumentException("Untyped value");
		if (ff != value.getFactory()) {
			throw new IllegalArgumentException("Wrong factory for value: "
					+ value.getFactory() + ", should be " + ff);
		}
		try {
			// @htson This is an awkward way to reuse verify(..) method 
			verify(ident, value);
		} catch (IllegalArgumentException e) {
			return false;
		}
		final Substitute<Expression> subst = makeSubstitute(value);
		final Substitute<Expression> oldSubst = identSubst.get(ident);
		if (oldSubst != null && !oldSubst.equals(subst)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean put(PredicateVariable predVar, Predicate value) {
		if (predVar == null)
			throw new NullPointerException("Null predicate variable");
		if (value == null)
			throw new NullPointerException("Null value");
		if (!value.isTypeChecked())
			throw new IllegalArgumentException("Untyped value");
		if (ff != value.getFactory()) {
			throw new IllegalArgumentException("Wrong factory for value: "
					+ value.getFactory() + ", should be " + ff);
		}
		
		final Substitute<Predicate> subst = makeSubstitute(value);
		final Substitute<Predicate> oldSubst = predSubst.put(predVar, subst);
		if (oldSubst != null && !oldSubst.equals(subst)) {
			predSubst.put(predVar, oldSubst);
			return false;
		}
		return true;
	}

	@Override
	public Predicate get(PredicateVariable predVar) {
		final Substitute<Predicate> subst = predSubst.get(predVar);
		return subst == null ? null : subst.getSubstitute(predVar, 0);
	}

	@Override
	public GivenType[] getTypes() {
		Set<GivenType> keySet = typeSubst.keySet();
		return keySet.toArray(new GivenType[keySet.size()]);
	}

	@Override
	public FreeIdentifier[] getFreeIdentifiers() {
		Set<FreeIdentifier> keySet = identSubst.keySet();
		return keySet.toArray(new FreeIdentifier[keySet.size()]);
	}

	@Override
	public PredicateVariable[] getPredicateVariables() {
		Set<PredicateVariable> keySet = predSubst.keySet();
		return keySet.toArray(new PredicateVariable[keySet.size()]);
	}

}
