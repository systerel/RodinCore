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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
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
public class Specialization implements ISpecialization {

	private static class SpecializationTypeRewriter extends TypeRewriter {

		// Type substitutions
		private final Map<GivenType, Type> typeSubst;

		public SpecializationTypeRewriter(FormulaFactory ff) {
			super(ff);
			typeSubst = new HashMap<GivenType, Type>();
		}

		public SpecializationTypeRewriter(SpecializationTypeRewriter other) {
			super(other.ff);
			typeSubst = new HashMap<GivenType, Type>(other.typeSubst);
		}

		public Type get(GivenType key) {
			return typeSubst.get(key);
		}

		public Type getOrSetDefault(GivenType key) {
			Type value = typeSubst.get(key);
			if (value == null) {
				value = key.translate(ff);
				typeSubst.put(key, value);
			}
			return value;
		}

		public GivenType[] getTypes() {
			final Set<GivenType> keySet = typeSubst.keySet();
			return keySet.toArray(new GivenType[keySet.size()]);
		}

		public void put(GivenType type, Type value) {
			final Type oldValue = typeSubst.put(type, value);
			if (oldValue != null && !oldValue.equals(value)) {
				typeSubst.put(type, oldValue); // repair
				throw new IllegalArgumentException("Type substitution for "
						+ type + " already registered");
			}
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

		// For debugging purpose
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			toString(sb);
			return sb.toString();
		}

		public void toString(StringBuilder sb) {
			String sep = "";
			for (Entry<GivenType, Type> entry : typeSubst.entrySet()) {
				sb.append(sep);
				sep = " || ";
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
		}
	}

	private static class SpecializationFormulaRewriter extends Substitution {

		// Identifier substitutions
		private final Map<FreeIdentifier, Substitute<Expression>> identSubst;

		// Predicate variable substitutions
		private final Map<PredicateVariable, Substitute<Predicate>> predSubst;

		public SpecializationFormulaRewriter(SpecializationTypeRewriter typeRewriter) {
			super(typeRewriter);
			identSubst = new HashMap<FreeIdentifier, Substitute<Expression>>();
			predSubst = new HashMap<PredicateVariable, Substitute<Predicate>>();
		}

		public SpecializationFormulaRewriter(
				SpecializationFormulaRewriter other,
				SpecializationTypeRewriter otherTypeRewriter) {
			super(otherTypeRewriter);
			identSubst = new HashMap<FreeIdentifier, Substitute<Expression>>(
					other.identSubst);
			predSubst = new HashMap<PredicateVariable, Substitute<Predicate>>(
					other.predSubst);
		}

		public Expression get(FreeIdentifier ident) {
			final Substitute<Expression> subst = identSubst.get(ident);
			return subst == null ? null : subst.getSubstitute(ident, 0);
		}

		public Predicate get(PredicateVariable predVar) {
			final Substitute<Predicate> subst = predSubst.get(predVar);
			return subst == null ? null : subst.getSubstitute(predVar, 0);
		}

		public Expression getOrSetDefault(FreeIdentifier ident) {
			final Substitute<Expression> subst = identSubst.get(ident);
			if (subst != null) {
				return subst.getSubstitute(ident, getBindingDepth());
			}
			final Type type = ident.getType();
			final Type newType = typeRewriter.rewrite(type);
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

		public Predicate getOrSetDefault(PredicateVariable predVar) {
			final Substitute<Predicate> subst = predSubst.get(predVar);
			if (subst != null) {
				return subst.getSubstitute(predVar, getBindingDepth());
			}
			return super.rewrite(predVar);
		}

		public FreeIdentifier[] getFreeIdentifiers() {
			final Set<FreeIdentifier> keySet = identSubst.keySet();
			return keySet.toArray(new FreeIdentifier[keySet.size()]);
		}

		public PredicateVariable[] getPredicateVariables() {
			final Set<PredicateVariable> keySet = predSubst.keySet();
			return keySet.toArray(new PredicateVariable[keySet.size()]);
		}

		public void put(FreeIdentifier ident, Expression value) {
			final Substitute<Expression> subst = makeSubstitute(value);
			final Substitute<Expression> oldSubst = identSubst.put(ident,
					subst);
			if (oldSubst != null && !oldSubst.equals(subst)) {
				identSubst.put(ident, oldSubst); // repair
				throw new IllegalArgumentException(
						"Identifier substitution for " + ident
								+ " already registered");
			}
		}

		public boolean put(PredicateVariable predVar, Predicate value) {
			final Substitute<Predicate> subst = makeSubstitute(value);
			final Substitute<Predicate> oldSubst = predSubst.put(predVar,
					subst);
			if (oldSubst != null && !oldSubst.equals(subst)) {
				predSubst.put(predVar, oldSubst);
				return false;
			}
			return true;
		}

		@Override
		public Expression rewrite(FreeIdentifier identifier) {
			final Expression newIdent = getOrSetDefault(identifier);
			if (newIdent.equals(identifier)) {
				return super.rewrite(identifier);
			}
			return newIdent;
		}

		@Override
		public Predicate rewrite(PredicateVariable predVar) {
			final Predicate newPred = getOrSetDefault(predVar);
			if (newPred.equals(predVar)) {
				return super.rewrite(predVar);
			}
			return newPred;
		}

		// For debugging purpose
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			toString(sb);
			return sb.toString();
		}

		public void toString(StringBuilder sb) {
			String sep = sb.length() == 0 ? "" : " || ";
			for (Entry<FreeIdentifier, Substitute<Expression>> entry : identSubst
					.entrySet()) {
				sb.append(sep);
				sep = " || ";
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
			for (Entry<PredicateVariable, Substitute<Predicate>> entry : predSubst
					.entrySet()) {
				sb.append(sep);
				sep = " || ";
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
		}
	}

	// The language of the right-hand sides of substitutions
	private final FormulaFactory ff;

	private final SpecializationTypeRewriter speTypeRewriter;

	private final SpecializationFormulaRewriter formRewriter;

	public Specialization(FormulaFactory ff) {
		this.ff = ff;
		speTypeRewriter = new SpecializationTypeRewriter(ff);
		formRewriter = new SpecializationFormulaRewriter(speTypeRewriter);
	}

	public Specialization(Specialization other) {
		this.ff = other.ff;
		speTypeRewriter = new SpecializationTypeRewriter(other.speTypeRewriter);
		formRewriter = new SpecializationFormulaRewriter(other.formRewriter,
				speTypeRewriter);
	}

	@Override
	public ISpecialization clone() {
		return new Specialization(this);
	}

	@Override
	public FormulaFactory getFactory() {
		return ff;
	}

	public ITypeCheckingRewriter getFormulaRewriter() {
		return formRewriter;
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
		speTypeRewriter.put(type, value);
		formRewriter.put(type.toExpression(), value.toExpression());
	}

	@Override
	public Type get(GivenType key) {
		return speTypeRewriter.get(key);
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
		formRewriter.put(ident, value);
	}

	/*
	 * Checks that a new substitution is compatible. We also save the given sets
	 * that are now frozen and must not change afterwards.
	 */
	private void verify(FreeIdentifier ident, Expression value) {
		final Type identType = ident.getType();
		final Type newType = speTypeRewriter.rewrite(identType);
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
			final Expression expr = formRewriter.getOrSetDefault(ident);
			for (final FreeIdentifier free : expr.getFreeIdentifiers()) {
				result.add(free);
			}
		}
		return result;
	}

	@Override
	public Expression get(FreeIdentifier ident) {
		return formRewriter.get(ident);
	}

	// For debugging purposes
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		speTypeRewriter.toString(sb);
		formRewriter.toString(sb);
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
		final Type oldValue = speTypeRewriter.get(type);
		return oldValue == null || oldValue.equals(value);
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
		final Expression oldValue = formRewriter.get(ident);
		return oldValue == null || oldValue.equals(value);
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

		return formRewriter.put(predVar, value);
	}

	@Override
	public Predicate get(PredicateVariable predVar) {
		return formRewriter.get(predVar);
	}

	@Override
	public GivenType[] getTypes() {
		return speTypeRewriter.getTypes();
	}

	@Override
	public FreeIdentifier[] getFreeIdentifiers() {
		return formRewriter.getFreeIdentifiers();
	}

	@Override
	public PredicateVariable[] getPredicateVariables() {
		return formRewriter.getPredicateVariables();
	}

}
