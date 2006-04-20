package org.eventb.internal.pp.translator.todel;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Predicate;


public class QuantLayer {
	private List<Predicate> bindings = new LinkedList<Predicate>();
	private List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	
	public QuantLayer(List<BoundIdentDecl> alreadyBound) {
		identDecls.addAll(alreadyBound);
	}
	
	public void addBinding(Predicate binding) {
		bindings.add(binding);
	}
	
	public Predicate removeBinding() {
		return bindings.get(0);
	}
	
	public List<BoundIdentDecl> getIdentDecls() {
		return identDecls;
	}
}
