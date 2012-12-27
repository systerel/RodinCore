/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class ClauseResult {

	private static final ClauseFactory cf = ClauseFactory.getDefault();
	
	private List<List<Literal<?, ?>>> literalLists;
	private boolean isEquivalence;
	
	public ClauseResult() {
		this.literalLists = new ArrayList<List<Literal<?, ?>>>();
	}
	
	private ClauseResult(List<List<Literal<?, ?>>> literalLists) {
		this.literalLists = literalLists;
	}
	
	public void setEquivalence(boolean isEquivalence) {
		this.isEquivalence = isEquivalence;
	}
	
	private void initializeList() {
		if (literalLists.isEmpty()) literalLists.add(new ArrayList<Literal<?,?>>());
	}
	
	public void addLiteralToAllLists(Literal<?,?> literal) {
		initializeList();
		for (List<Literal<?,?>> list : literalLists) {
			list.add(literal);
		}
	}
	
	public void prefixLiteralToAllLists(Literal<?, ?> literal) {
		for (List<Literal<?, ?>> positiveClause : literalLists) {
			positiveClause.add(0, literal);
		}
	}
	
	public void addAll(ClauseResult list) {
		this.literalLists.addAll(list.literalLists);
	}
	
	public ClauseResult getCopy() {
		List<List<Literal<?,?>>> result = new ArrayList<List<Literal<?,?>>>();
		for (List<Literal<?,?>> list : literalLists) {
			result.add(new ArrayList<Literal<?,?>>(list));
		}
		return new ClauseResult(result);
	}
	
	public List<Clause> getClauses(IOrigin origin, VariableContext context) {
		List<Clause> result = new ArrayList<Clause>();
		for (List<Literal<?,?>> literalList : literalLists) {
			if (isEquivalence && literalList.size() > 1) result.add(cf.makeEquivalenceClauseWithNewVariables(origin, literalList, context));
			else result.add(cf.makeDisjunctiveClauseWithNewVariables(origin, literalList, context));
		}
		return result;
	}
	
}
