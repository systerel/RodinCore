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
package org.eventb.internal.pp.core.provers.seedsearch.solver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;

public class InstantiationValue {
	// one instantiation value per literal/clause/constant combination
	private final LiteralSignature signature;
	private final Constant constant;

	// TODO just here for the level, can be replaced by a set of IOrigin
	private final Set<Clause> clauses;
	
	public InstantiationValue(Constant constant, 
			LiteralSignature signature) {
		this.constant = constant;
		this.clauses = new HashSet<Clause>();
		this.signature = signature;
	}
	
	public Constant getConstant() {
		return constant;
	}

	LiteralSignature getSignature() {
		return signature;
	}
	
	public void addClause(Clause clause) {
		clauses.add(clause);
	}
	
	public Set<Clause> getClauses() {
		return clauses;
	}
	
	public void removeClause(Clause clause) {
		for (Iterator<Clause> iter = clauses.iterator(); iter.hasNext();) {
			Clause current = iter.next();
			if (current.equalsWithLevel(clause)) iter.remove();
		}
	}
	
	public void backtrack(Level level) {
		for (Iterator<Clause> iter = clauses.iterator(); iter.hasNext();) {
			Clause clause = iter.next();
			if (level.isAncestorOf(clause.getLevel())) iter.remove();
		}
	}
	
	public boolean isValid() {
		return !clauses.isEmpty();
	}
	
	@Override
	public String toString() {
		return constant.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InstantiationValue) {
			InstantiationValue temp = (InstantiationValue) obj;
			return constant.equals(temp.constant) && signature.equals(temp.signature);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 37*constant.hashCode() + signature.hashCode();
	}
	
}
