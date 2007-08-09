/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.seedsearch.solver;


public class SolverResult {

	private Instantiable instantiable;
	private InstantiationValue value;
	
	SolverResult(Instantiable instantiable, InstantiationValue value) {
		this.instantiable = instantiable;
		this.value = value;
	}
	
	public Instantiable getInstantiable() {
		return instantiable;
	}
	
	public InstantiationValue getInstantiationValue() {
		return value;
	}
	
}
