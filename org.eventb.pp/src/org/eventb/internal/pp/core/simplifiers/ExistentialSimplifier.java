/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;

/**
 * This simplifier removes the existential variables appearing in literals
 * without variables and replaces them by fresh constants.
 *
 * @author François Terrier
 *
 */
public class ExistentialSimplifier extends AbstractSimplifier {

	private final VariableContext context;
	
	public ExistentialSimplifier(VariableContext context) {
		this.context = context;
	}
	
	@Override
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		simplifyExistentialHelper(predicates);
		simplifyExistentialHelper(equalities);
		simplifyExistentialHelper(arithmetic);
		simplifyExistentialHelper(conditions);
		Clause result = cf.makeDisjunctiveClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	@Override
	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		simplifyExistentialHelper(conditions);
		Clause result = cf.makeEquivalenceClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	public <T extends Literal<T,?>> T simplifyExistential(Literal<T,?> literal) {
		Set<LocalVariable> existentials = new HashSet<LocalVariable>();
		literal.collectLocalVariables(existentials);
		Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (SimpleTerm variable : existentials) {
			map.put(variable, context.getNextFreshConstant(variable.getSort()));
		}
		return literal.substitute(map);
	}

	protected <T extends Literal<T,?>> void simplifyExistentialHelper(List<T> list) {
		ArrayList<T> tmp1 = new ArrayList<T>();
		for (T literal : list) {
			if (literal.isConstant()) {
				tmp1.add(simplifyExistential(literal));
			}
			else {
				tmp1.add(literal);
			}
		}
		list.clear();
		list.addAll(tmp1);
	}

	@Override
	public boolean canSimplify(Clause clause) {
		return true;
	}
	
}
