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
package org.eventb.internal.pp.core.provers.seedsearch;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.Constant;

public final class SeedSearchResult {

	private final Constant constant;
	private final int predicatePosition;
	private final Clause clause;
//	private final Set<Clause> causingClauses;
	private final int position;
	
	public SeedSearchResult(Constant constant, int position,
			int predicatePosition, Clause clause, Set<Clause> causingClauses) {
		this.constant = constant;
		this.predicatePosition = predicatePosition;
		this.clause = clause;
		this.position = position;
//		this.causingClauses = causingClauses;
	}
	
	public Constant getConstant() {
		return constant;
	}

	public Clause getInstantiableClause() {
		return clause;
	}

	public int getPredicatePosition() {
		return predicatePosition;
	}

	public int getPosition() {
		return position;
	}

}
