/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public class LoaderResult {

	private List<Clause> clauses;
	
	protected LoaderResult(List<Clause> clauses) {
		this.clauses = clauses;
	}
	
	/**
	 * Returns the non-unit clauses.
	 * 
	 * @return the non-unit clauses
	 */
	public List<Clause> getClauses() {
		return clauses;
	}
	
	@Override
	public String toString() {
		return clauses.toString();
	}
	
}
