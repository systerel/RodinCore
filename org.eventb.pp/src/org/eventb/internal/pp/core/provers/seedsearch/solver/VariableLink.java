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

public final class VariableLink {
	// mutable, one variable link per pair of signature
	// needs a descriptor containing the pair of signature - one instance per descriptor
	
	private Set<Clause> clauses;
	private final LiteralSignature signature1, signature2;
	
	public VariableLink(LiteralSignature signature1, LiteralSignature signature2) {
		this.signature1 = signature1;
		this.signature2 = signature2;
		this.clauses = new HashSet<Clause>();
	}
	
	public void addClause(Clause clause) {
		clauses.add(clause);
	}
	
	LiteralSignature getSignature1() {
		return signature1;
	}
	
	LiteralSignature getSignature2() {
		return signature2;
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
		return signature1.toString()+"<->"+signature2.toString()+"("+clauses+")";
	}
	
	protected String toString(LiteralSignature signature) {
		assert signature == signature1 || signature == signature2;
		
		StringBuilder string = new StringBuilder();
		string.append("->");
		if (signature1==signature) string.append(signature2);
		else string.append(signature1);
		return string.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VariableLink) {
			VariableLink temp = (VariableLink) obj;
			return (signature1.equals(temp.signature2) && signature2.equals(temp.signature2))
				|| (signature2.equals(temp.signature1) && signature1.equals(temp.signature1));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return signature1.hashCode() + signature2.hashCode();
	}
}

