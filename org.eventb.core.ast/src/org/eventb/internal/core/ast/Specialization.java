/*******************************************************************************
 * Copyright (c) 2010, 2017 Systerel and others.
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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
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
		
		// Log for rollback: contains the types that have been added to the
		// substitution but not yet committed
		private final Set<GivenType> typeLog;

		public SpecializationTypeRewriter(FormulaFactory ff) {
			super(ff);
			typeSubst = new HashMap<GivenType, Type>();
			typeLog = new HashSet<GivenType>();
		}

		public SpecializationTypeRewriter(SpecializationTypeRewriter other) {
			super(other.ff);
			typeSubst = new HashMap<GivenType, Type>(other.typeSubst);
			typeLog = new HashSet<GivenType>();
		}

		public Type get(GivenType key) {
			return typeSubst.get(key);
		}

		public Type getOrSetDefault(GivenType key) {
			Type value = typeSubst.get(key);
			if (value == null) {
				value = key.translate(ff);
				typeSubst.put(key, value);
				typeLog.add(key);
			}
			return value;
		}

		public GivenType[] getTypes() {
			final Set<GivenType> keySet = typeSubst.keySet();
			return keySet.toArray(new GivenType[keySet.size()]);
		}

		public void put(GivenType type, Type value) {
			final Type oldValue = typeSubst.put(type, value);
			if (oldValue == null) {
				typeLog.add(type);
				return;
			}
			if (!oldValue.equals(value)) {
				typeSubst.put(type, oldValue); // repair
				throw new IllegalArgumentException("Type substitution for "
						+ type + " already registered");
			}
		}

		public void startTransaction() {
			typeLog.clear();
		}

		public void endTransaction(boolean commit) {
			if (!commit) {
				for (final GivenType type : typeLog) {
					typeSubst.remove(type);
				}
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
			sb.append("{");
			String sep = "";
			for (Entry<GivenType, Type> entry : typeSubst.entrySet()) {
				sb.append(sep);
				sep = " || ";
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
			sb.append("}");
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
			Substitute<Predicate> subst = predSubst.get(predVar);
			if (subst != null) {
				return subst.getSubstitute(predVar, getBindingDepth());
			}
			final Predicate result = super.rewrite(predVar);
			subst = makeSubstitute(result);
			predSubst.put(predVar, subst);
			return result;
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
			sb.append("{");
			String sep = "";
			for (Entry<FreeIdentifier, Substitute<Expression>> entry : identSubst
					.entrySet()) {
				sb.append(sep);
				sep = " || ";
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
			sb.append("} + {");
			sep = "";
			for (Entry<PredicateVariable, Substitute<Predicate>> entry : predSubst
					.entrySet()) {
				sb.append(sep);
				sep = " || ";
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
			sb.append("}");
		}
	}

	// The type environment of the source language (quasi-final field to be
	// set once at the first use and never changed after).
	private ITypeEnvironmentBuilder srcTypenv;
	
	// The language of the right-hand sides of substitutions
	private final FormulaFactory ff;

	private final SpecializationTypeRewriter speTypeRewriter;

	private final SpecializationFormulaRewriter formRewriter;

	public Specialization(FormulaFactory ff) {
		this.srcTypenv = null;
		this.ff = ff;
		speTypeRewriter = new SpecializationTypeRewriter(ff);
		formRewriter = new SpecializationFormulaRewriter(speTypeRewriter);
	}

	public Specialization(Specialization other) {
		this.srcTypenv = other.srcTypenv == null ? null
				: other.srcTypenv.makeBuilder();
		this.ff = other.ff;
		speTypeRewriter = new SpecializationTypeRewriter(other.speTypeRewriter);
		formRewriter = new SpecializationFormulaRewriter(other.formRewriter,
				speTypeRewriter);
	}

	@Override
	public ISpecialization clone() {
		return new Specialization(this);
	}

	// For testing purpose, do not publish.
	public ITypeEnvironmentBuilder getSourceTypenv() {
		return srcTypenv;
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
		if (!verifySrcTypenv(type.toExpression())) {
			throw new IllegalArgumentException("Identifier " + type
					+ " already entered with a different type");
		}
		srcTypenv.add(type.toExpression());
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
		if (!verifySrcTypenv(ident)) {
			throw new IllegalArgumentException(
					"Incompatible types for " + ident);
		}
		if (!verify(ident, value, true)) {
			throw new IllegalArgumentException(
					"Incompatible types for " + ident);
		}
		srcTypenv.add(ident);
		for (final GivenType given : ident.getGivenTypes()) {
			srcTypenv.add(given.toExpression());
			formRewriter.put(given.toExpression(),
					speTypeRewriter.get(given).toExpression());
		}
		formRewriter.put(ident, value);
	}

	/*
	 * Tells whether the new substitution is compatible with existing ones. If
	 * commit is true, we also save the given sets that are now frozen and must
	 * not change afterwards.
	 */
	private boolean verify(FreeIdentifier ident, Expression value,
			boolean commit) {
		final Type identType = ident.getType();

		speTypeRewriter.startTransaction();
		final Type newType = speTypeRewriter.rewrite(identType);
		final boolean result = value.getType().equals(newType);
		speTypeRewriter.endTransaction(commit && result);

		return result;
	}

	public Type specialize(Type type) {
		prepare(type);
		return speTypeRewriter.rewrite(type);
	}

	/*
	 * Prepares the specialization of a type.
	 * 
	 * We check here that the specialization will not encounter a typing error
	 * and perform the side-effects for types not yet registered with this
	 * specialization.
	 */
	public void prepare(Type type) {
		final Set<GivenType> givens = type.getGivenTypes();

		// Ensure that type environments are compatible
		for (final GivenType given : givens) {
			if (!verifySrcTypenv(given.toExpression())) {
				throw new IllegalArgumentException("Type " + given
						+ " already entered with a different type");
			}
		}

		// Then insert the identity substitutions not already there
		for (final GivenType given : givens) {
			if (srcTypenv.getType(given.getName()) == null) {
				final FreeIdentifier ident = given.toExpression();
				srcTypenv.add(ident);
				speTypeRewriter.put(given, given.translate(ff));
				formRewriter.put(ident, ident.translate(ff));
			}
		}
	}

	/*
	 * Specializing a type environment consists in, starting from an empty type
	 * environment, adding all given sets and free identifiers that occur in the
	 * result of substitutions for identifiers from the original type
	 * environment.
	 */
	public ITypeEnvironmentBuilder specialize(TypeEnvironment typenv) {
		prepare(typenv);
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

	/*
	 * Prepares the specialization of a type environment.
	 * 
	 * We check here that the specialization will not encounter a typing error
	 * and perform the side-effects for identifiers not yet registered with this
	 * specialization.
	 */
	public void prepare(TypeEnvironment typenv) {
		// Ensure that type environments are compatible
		final IIterator iter = typenv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final FreeIdentifier ident = iter.asFreeIdentifier();
			if (!verifySrcTypenv(ident)) {
				throw new IllegalArgumentException("Identifier " + ident
						+ " already entered with a different type");
			}
		}

		// Then protect the identity substitutions not already there
		srcTypenv.addAll(typenv);
	}

	/*
	 * Prepares the specialization of an arbitrary formula. The specialization
	 * itself cannot be performed here, as it must use the non-API rewrite
	 * method of class Formula.
	 * 
	 * We check here that the specialization will not encounter a typing error
	 * and perform the side-effects for identifiers and predicate variables not
	 * yet registered with this specialization.
	 */
	public <T extends Formula<T>> void prepare(Formula<T> formula) {
		final FreeIdentifier[] localEnv = formula.getFreeIdentifiers();
		
		// Ensure that type environments are compatible
		for (final FreeIdentifier ident : localEnv) {
			if (!verifySrcTypenv(ident)) {
				throw new IllegalArgumentException("Identifier " + ident
						+ " already entered with a different type");
			}
		}
		
		// Then protect the identity substitutions not already there
		for (final FreeIdentifier ident : localEnv) {
			if (srcTypenv.getType(ident.getName()) == null) {
				srcTypenv.add(ident);
				if (ident.isATypeExpression()) {
					final GivenType given = ident.toType();
					speTypeRewriter.put(given, given.translate(ff));
				}
				formRewriter.put(ident, ident.translate(ff));
			}
		}

		// Also add identity substitutions for the predicate variables that do
		// not have a substitution yet.
		final PredicateVariable[] predVars = formula.getPredicateVariables();
		for (final PredicateVariable predVar : predVars) {
			if (formRewriter.get(predVar) == null) {
				formRewriter.put(predVar, predVar.translate(ff));
			}
		}
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
		sb.append(" + ");
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

		if (!verifySrcTypenv(type.toExpression())) {
			return false;
		}

		final Type oldValue = speTypeRewriter.get(type);
		return oldValue == null || oldValue.equals(value);
	}

	// Tells whether the given name could be added with the given type in
	// the source type environment.
	private boolean verifySrcTypenv(FreeIdentifier ident) {
		if (srcTypenv == null) {
			srcTypenv = ident.getFactory().makeTypeEnvironment();
		}
		final Type knownType = srcTypenv.getType(ident.getName());
		return knownType == null || knownType.equals(ident.getType());
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

		if (!verifySrcTypenv(ident)) {
			return false;
		}
		if (!verify(ident, value, false)) {
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
