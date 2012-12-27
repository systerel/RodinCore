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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;

public class LiteralSignature {
	// immutable once the matching literal is set
	// one instance per descriptor
	
	private final PredicateLiteralDescriptor literal;
	private final boolean isPositive;
	private final int position;
	private LiteralSignature matchingLiteral;
	
	private final Set<VariableLink> variableLinks;
	private final Set<InstantiationValue> instantiationValues;
	private final Map<Instantiable, InstantiableContainer> instantiables;
	
	public LiteralSignature(PredicateLiteralDescriptor literal, boolean isPositive, int position) {
		this.literal = literal;
		this.isPositive = isPositive;
		this.position = position;
		this.instantiationValues = new LinkedHashSet<InstantiationValue>();
		this.variableLinks = new LinkedHashSet<VariableLink>();
		this.instantiables = new HashMap<Instantiable, InstantiableContainer>();
	}
	
	protected LiteralSignature() {
		this.literal = null;
		this.isPositive = false;
		this.position = 0;
		this.instantiationValues = new LinkedHashSet<InstantiationValue>();
		this.variableLinks = new LinkedHashSet<VariableLink>();
		this.instantiables = new HashMap<Instantiable, InstantiableContainer>();
	}
	
	LiteralSignature getMatchingLiteral() {
		return matchingLiteral;
	}
	
	public void setMatchingLiteral(LiteralSignature matchingLiteral) {
		this.matchingLiteral = matchingLiteral;
	}

	Set<VariableLink> getVariableLinks() {
		return new HashSet<VariableLink>(variableLinks);
	}
	
	void addVariableLink(VariableLink link) {
		variableLinks.add(link);
	}
	
	void removeVariableLink(VariableLink link) {
		variableLinks.remove(link);
	}
	
	boolean hasVariableLink(VariableLink link) {
		return variableLinks.contains(link);
	}

	public int getPosition() {
		return position;
	}

	void addInstantiable(Instantiable value, InstantiableContainer container) {
		instantiables.put(value, container);
	}
	
	InstantiableContainer getInstantiableContainer(Instantiable value) {
		return instantiables.get(value);
	}
	
	public Iterable<Instantiable> getInstantiables() {
		return new HashSet<Instantiable>(instantiables.keySet());
	}
	
	public void removeInstantiable(Instantiable value) {
		instantiables.remove(value);
	}
	
	void addInstantiationValue(InstantiationValue value) {
		instantiationValues.add(value);
	}
	
	Set<InstantiationValue> getInstantiationValues() {
		return new HashSet<InstantiationValue>(instantiationValues);
	}

	void removeInstantiationValue(InstantiationValue value) {
		instantiationValues.remove(value);
	}
	
	boolean hasInstantiationValue(InstantiationValue value) {
		return instantiationValues.contains(value);
	}
	
	@Override
	public String toString() {
		return literal.toString()+"("+position+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LiteralSignature) {
			LiteralSignature temp = (LiteralSignature) obj;
			return literal.equals(temp.literal) && isPositive == temp.isPositive && position == temp.position;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return literal.hashCode() * 37 + position + (isPositive?0:1);
	}
	
	public String dump() {
		StringBuilder string = new StringBuilder();
		string.append(toString());
		string.append("[");
		if (!variableLinks.isEmpty()) {
			string.append("V=");
			string.append("[");
			for (VariableLink link : variableLinks) {
				string.append(link.toString(this));
				string.append(",");
			}
			string.deleteCharAt(string.length()-1);
			string.append("],");
		}
		if (!instantiationValues.isEmpty()) {
			string.append("C=");
			string.append(instantiationValues.toString());
			string.append(",");
		}
		if (!instantiables.isEmpty()) {
			string.append("I=");
			string.append(getInstantiables().toString());
			string.append(",");
		}
		string.append("]");
		return string.toString();
	}

}
