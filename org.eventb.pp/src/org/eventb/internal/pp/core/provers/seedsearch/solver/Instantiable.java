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

import org.eventb.internal.pp.core.elements.Clause;

public class Instantiable {
	// one instantiable per position/literal/clause combination

	private final LiteralSignature signature;

	private final Clause clause;
	private final int predicatePosition;
	
	private int instantiationCount = 0;
	
	public Instantiable(LiteralSignature signature,
			Clause clause, int predicatePosition) {
		this.clause = clause;
		this.signature = signature;
		this.predicatePosition = predicatePosition;
	}
	
	protected Instantiable(LiteralSignature signature) {
		this.signature = signature;
		predicatePosition = 0;
		clause = null;
	}
	
	public int getInstantiationCount() {
		return instantiationCount;
	}
	
	public void incrementInstantiationCount() {
		this.instantiationCount++;
	}
	
	LiteralSignature getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return signature.toString();
	}
	
	public int getPosition() {
		return signature.getPosition();
	}
	
	public int getPredicatePosition() {
		return predicatePosition;
	}
	
	public Clause getClause() {
		return clause;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Instantiable) {
			Instantiable temp = (Instantiable) obj;
			return clause.equalsWithLevel(temp.clause) && predicatePosition == temp.predicatePosition
				&& signature.equals(temp.signature);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 37*(37*clause.hashCode()+signature.hashCode())+predicatePosition;
	}
	
}
