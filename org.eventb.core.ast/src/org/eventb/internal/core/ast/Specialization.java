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

import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypedFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;

/**
 * Common implementation for specializations.
 * 
 * @author Laurent Voisin
 */
public class Specialization extends DefaultRewriter implements ISpecialization, ITypedFormulaRewriter {

	private final Map<GivenType, Type> typeSubst;
	private final Map<FreeIdentifier, Expression> identSubst;

	public Specialization(FormulaFactory ff) {
		super(false, ff);
		typeSubst = new HashMap<GivenType, Type>();
		identSubst = new HashMap<FreeIdentifier, Expression>();
	}

	@Override
	public void put(GivenType key, Type value) {
		if (key == null)
			throw new NullPointerException("Null given type");
		if (value == null)
			throw new NullPointerException("Null type");
		if (typeSubst.containsKey(key) && !typeSubst.get(key).equals(value))
			throw new IllegalArgumentException("Key " + key
					+ " is already registered");
		typeSubst.put(key, value);
		verify();
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
		if (!value.isTypeChecked())
			throw new IllegalArgumentException("Untyped value");
		identSubst.put(ident, value);
		verify();
	}

	public Expression get(FreeIdentifier ident) {
		final Expression value = identSubst.get(ident);
		if (value == null) {
			final Type specializedType = ident.getType().specialize(this);
			return ff.makeFreeIdentifier(ident.getName(),
					ident.getSourceLocation(), specializedType);
		}
		return value;
	}

	/**
	 * Verifies that both substitutions are compatible.
	 */
	private void verify() {
		for (Entry<FreeIdentifier, Expression> entry : identSubst.entrySet()) {
			final FreeIdentifier ident = entry.getKey();
			final Type newType = ident.getType().specialize(this);
			final Expression value = entry.getValue();
			if (!value.getType().equals(newType)) {
				throw new IllegalArgumentException("Incompatible types for "
						+ ident);
			}
		}
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		if (identifier.isATypeExpression())
			return get(ff.makeGivenType(identifier.getName())).toExpression(ff);
		return get(identifier);
	}
	
	@Override
	public Expression rewrite(BoundIdentifier identifier) {
		return ff.makeBoundIdentifier(identifier.getBoundIndex(), identifier
				.getSourceLocation(), identifier.getType().specialize(this));
	}
	
	@Override
	public Expression rewrite(QuantifiedExpression expression) {
		return ff.makeQuantifiedExpression(expression.getTag(),
				getSpecializedDecls(expression.getBoundIdentDecls()),
				expression.getPredicate(), expression.getExpression(),
				expression.getSourceLocation(), expression.getForm());
	}
	
	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		return ff.makeQuantifiedPredicate(predicate.getTag(),
				getSpecializedDecls(predicate.getBoundIdentDecls()),
				predicate.getPredicate(), predicate.getSourceLocation());
	}
	
	private BoundIdentDecl[] getSpecializedDecls(BoundIdentDecl[] decls) {
		final BoundIdentDecl[] result = new BoundIdentDecl[decls.length];
		for (int i = 0; i < decls.length; i++) {
			result[i] = decls[i].specialize(this);
		}
		return result;
	}
	
	@Override
	public Expression rewrite(AtomicExpression expression) {
		final SourceLocation sl = expression.getSourceLocation();
		final Type type = expression.getType();
		if (type == null)
			return expression;
		final Type specializedType = type.specialize(this);
		switch (expression.getTag()) {
		case Formula.EMPTYSET:
			return ff.makeEmptySet(specializedType, sl);
		case Formula.KID_GEN:
			return ff.makeAtomicExpression(KID_GEN, sl, specializedType);
		case Formula.KPRJ1_GEN:
			return ff.makeAtomicExpression(KPRJ1_GEN, sl, specializedType);
		case Formula.KPRJ2_GEN:
			return ff.makeAtomicExpression(KPRJ2_GEN, sl, specializedType);
		default:
			return expression;
		}
	}

	public BoundIdentDecl rewrite(BoundIdentDecl decl) {
		return ff.makeBoundIdentDecl(decl.getName(), decl.getSourceLocation(),
				decl.getType().specialize(this));
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
	
}
