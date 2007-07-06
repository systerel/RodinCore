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
