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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.seedsearch.solver.Instantiable;
import org.eventb.internal.pp.core.provers.seedsearch.solver.InstantiationValue;
import org.eventb.internal.pp.core.provers.seedsearch.solver.LiteralSignature;
import org.eventb.internal.pp.core.provers.seedsearch.solver.SeedSearchSolver;
import org.eventb.internal.pp.core.provers.seedsearch.solver.SolverResult;
import org.eventb.internal.pp.core.provers.seedsearch.solver.VariableLink;

public class SeedSearchManager {

	// signatures - ok
	private HashMap<SignatureDescriptor, LiteralSignature> signatures;
	// links - ok
	private HashMap<LinkDescriptor, VariableLink> links;
	// map of clause-links for clause removal
	private HashMap<Clause, Set<LinkDescriptor>> clauseLinksCache;
	
	// instantiation values - ok
	private HashMap<ConstantDescriptor, InstantiationValue> instantiationValues;
	// map of clause-constants for clause removal
	private HashMap<Clause, Set<ConstantDescriptor>> clauseInstantiationValuesCache;
	
	// instantiablesCache, no need for a descriptor since instantiablesCache are unique - ok
	// map of clause-instantiablesCache for clause removal
	private Set<Instantiable> instantiables;
	private HashMap<Clause, Set<Instantiable>> instantiablesCache;
	
	private SeedSearchSolver solver;
	
	public SeedSearchManager() {
		this.clauseInstantiationValuesCache = new HashMap<Clause, Set<ConstantDescriptor>>();
		this.clauseLinksCache = new HashMap<Clause, Set<LinkDescriptor>>();
		this.signatures = new HashMap<SignatureDescriptor, LiteralSignature>();
		this.links = new HashMap<LinkDescriptor, VariableLink>();
		this.instantiationValues = new HashMap<ConstantDescriptor, InstantiationValue>();
		this.instantiables = new HashSet<Instantiable>();
		this.instantiablesCache = new HashMap<Clause, Set<Instantiable>>();
		this.solver = new SeedSearchSolver();
	}
	
	public List<SeedSearchResult> getArbitraryInstantiation(VariableContext context) {
		int currentInstantiationCount = -1;
		for (Instantiable instantiable : instantiables) {
			if (currentInstantiationCount==-1 || instantiable.getInstantiationCount() < currentInstantiationCount) {
				currentInstantiationCount = instantiable.getInstantiationCount();
			}
		}
		Set<Instantiable> currentInstantiables = new HashSet<Instantiable>();
		for (Instantiable instantiable : instantiables) {
			if (instantiable.getInstantiationCount() == currentInstantiationCount) {
				currentInstantiables.add(instantiable);
			}
		}
		
		return doArbitraryInstantiation(currentInstantiables, context);
	}
	
	private List<SeedSearchResult> doArbitraryInstantiation(Set<Instantiable> instantiables, VariableContext context) {
		List<SeedSearchResult> result = new ArrayList<SeedSearchResult>();
		for (Instantiable currentInstantiable : instantiables) {
			currentInstantiable.incrementInstantiationCount();
			// TODO change !
			Sort sort = currentInstantiable.getClause().getPredicateLiteral(
					currentInstantiable.getPredicatePosition()).getTerm(
							currentInstantiable.getPosition()).getSort();
			result.add(new SeedSearchResult(context.getNextFreshConstant(sort), currentInstantiable.getPosition(), currentInstantiable.getPredicatePosition(), currentInstantiable.getClause(), new HashSet<Clause>()));
		}
		return result;
	}
	
	
	public List<SeedSearchResult> addInstantiable(PredicateLiteralDescriptor descriptor, boolean isPositive, int predicatePosition,
			List<SimpleTerm> terms, int termPosition, Clause clause) {
		List<SolverResult> result = new ArrayList<SolverResult>();
		// if this clause exists in the instantiablesCache map, it is with the same level
		// otherwise it means there are two clauses with the same level, which is 
		// impossible since the clause dispatcher removes all clauses with a higher level
		// before adding the same clause with a lower level
		for (Clause existingClause : instantiablesCache.keySet()) {
			if (clause.equals(existingClause)) assert clause.equalsWithLevel(existingClause);
		}
		
		Set<Instantiable> existingInstantiables = instantiablesCache.get(clause);
		if (existingInstantiables == null) {
			existingInstantiables = new HashSet<Instantiable>();
			instantiablesCache.put(clause, existingInstantiables);
		}
		LiteralSignature signature = getAndAddLiteralSignature(descriptor, isPositive, termPosition);
		Instantiable instantiable = new Instantiable(signature,clause,predicatePosition);
		if (!existingInstantiables.contains(instantiable)) {
			existingInstantiables.add(instantiable);
			instantiables.add(instantiable);
			List<SolverResult> instantiableResult = solver.addInstantiable(instantiable);
			result.addAll(instantiableResult);
		}
		return compileResults(result);
	}
	
	public List<SeedSearchResult> addConstant(PredicateLiteralDescriptor descriptor, boolean isPositive, List<SimpleTerm> terms, Clause clause) {
		List<SolverResult> result = new ArrayList<SolverResult>();
		for (int i = 0; i < terms.size(); i++) {
			SimpleTerm term = terms.get(i);
			if (term.isConstant() && !term.isQuantified()) {
				LiteralSignature signature = getAndAddLiteralSignature(descriptor, isPositive, i);
				InstantiationValue value = getAndAddInstantiationValue(signature, (Constant)term, clause);
				value.addClause(clause);
				List<SolverResult> valueResult = solver.addInstantiationValue(value);
				result.addAll(valueResult); 
			}
		}
		return compileResults(result);
	}
	
	public List<SeedSearchResult> addVariableLink(PredicateLiteralDescriptor descriptor1, boolean isPositive1,
			PredicateLiteralDescriptor descriptor2, boolean isPositive2,
			List<SimpleTerm> terms1, List<SimpleTerm> terms2, Clause clause) {
		List<SolverResult> result = new ArrayList<SolverResult>();
		for (int i = 0; i < terms1.size(); i++) {
			SimpleTerm term1 = terms1.get(i);
			if (!term1.isConstant()) {
				for (int j = 0; j < terms2.size(); j++) {
					SimpleTerm term2 = terms2.get(j);
					if (term1 == term2) {
						// add a link
						LiteralSignature signature1 = getAndAddLiteralSignature(descriptor1, isPositive1, i);
						LiteralSignature signature2 = getAndAddLiteralSignature(descriptor2, isPositive2, j);
						VariableLink link = getAndAddVariableLink(signature1, signature2, i, j, clause);
						link.addClause(clause);
						List<SolverResult> linkResult = solver.addVariableLink(link);
						result.addAll(linkResult);
					}
				}
			}
		}
		return compileResults(result);
	}
	
	private List<SeedSearchResult> compileResults(List<SolverResult> solverResults) {
		List<SeedSearchResult> result = new ArrayList<SeedSearchResult>();
		for (SolverResult solverResult : solverResults) {
			Instantiable instantiable = solverResult.getInstantiable();
			InstantiationValue value = solverResult.getInstantiationValue();
			instantiable.incrementInstantiationCount();
			result.add(new SeedSearchResult(value.getConstant(), instantiable.getPosition(), instantiable.getPredicatePosition(), instantiable.getClause(), value.getClauses()));
		}
		return result;
	}
	
	private LiteralSignature getAndAddLiteralSignature(PredicateLiteralDescriptor literalDescriptor, boolean isPositive, int position) {
		SignatureDescriptor descriptor = new SignatureDescriptor(literalDescriptor,isPositive,position);
		LiteralSignature result = signatures.get(descriptor);
		if (result == null) {
			result = new LiteralSignature(literalDescriptor,isPositive,position);
			LiteralSignature inverse = new LiteralSignature(literalDescriptor,!isPositive,position);
			result.setMatchingLiteral(inverse);
			inverse.setMatchingLiteral(result);
			signatures.put(descriptor, result);
			signatures.put(new SignatureDescriptor(literalDescriptor,!isPositive,position), inverse);
		}
		return result;
	}
	
	private InstantiationValue getAndAddInstantiationValue(LiteralSignature signature, Constant constant, Clause clause) {
		ConstantDescriptor constantDescriptor = new ConstantDescriptor(constant, signature);
		InstantiationValue value = instantiationValues.get(constantDescriptor);
		if (value == null) {
			value = new InstantiationValue(constant, signature);
			instantiationValues.put(constantDescriptor, value);
		}
		addToCache(constantDescriptor, clause, clauseInstantiationValuesCache);
		return value;
	}
	
	private VariableLink getAndAddVariableLink(LiteralSignature signature1,
			LiteralSignature signature2, int position1, int position2, Clause clause) {
		LinkDescriptor descriptor = new LinkDescriptor(signature1,signature2);
		VariableLink link = links.get(descriptor);
		if (link == null) {
			link = new VariableLink(signature1,signature2);
			links.put(descriptor, link);
		}
		addToCache(descriptor, clause, clauseLinksCache);
		return link; 
	}
	
	private <T> void addToCache(T descriptor, Clause clause, HashMap<Clause, Set<T>> cache) {
		Set<T> descriptors = cache.get(clause);
		if (descriptors == null) {
			descriptors = new HashSet<T>();
			cache.put(clause, descriptors);
		}
		descriptors.add(descriptor);
	}
	
	public void removeClause(Clause clause) {
		// TODO refactor
		
		// 2) for instantiablesCache
		// we use the clause -> instantiable hash table
		Set<Instantiable> instantiableDescriptors = instantiablesCache.remove(clause);
		if (instantiableDescriptors != null) {
			for (Instantiable instantiable : instantiableDescriptors) {
//				solver.removeInstantiable(instantiable);
				removeInstantiable(instantiable);
				instantiables.remove(instantiable);
			}
		}
		
//		assertNoMoreInstantiables(clause);
		
		// 3) for constants
		// reconstruct constant descriptor
		// cache of constant descriptors per clause
		Set<ConstantDescriptor> constantDescriptors = clauseInstantiationValuesCache.remove(clause);
		if (constantDescriptors != null) {
			for (ConstantDescriptor descriptor : constantDescriptors) {
				InstantiationValue value = instantiationValues.get(descriptor);
				value.removeClause(clause);
				if (!value.isValid()) {
					solver.removeInstantiationValue(value);
					instantiationValues.remove(descriptor);
				}
			}
		}
		
		// 1) for variable links
		// reconstruct link descriptor + remove clause
		// we use a cache of link descriptors per clause
		Set<LinkDescriptor> linkDescriptors = clauseLinksCache.remove(clause);
		if (linkDescriptors != null) {
			for (LinkDescriptor descriptor : linkDescriptors) {
				VariableLink link = links.get(descriptor);
				link.removeClause(clause);
				if (!link.isValid()) {
					solver.removeVariableLink(link);
					links.remove(descriptor);
				}
			}
		}
		
	}
	
	private void removeInstantiable(Instantiable instantiable) {
		for (LiteralSignature signature : signatures.values()) {
			signature.removeInstantiable(instantiable);
		}
	}
	
	@SuppressWarnings("unused")
	private void assertNoMoreInstantiables(Clause clause) {
		for (LiteralSignature signature : signatures.values()) {
			for (Instantiable instantiable : signature.getInstantiables()) {
				assert !instantiable.getClause().equals(clause);
			}
		}
	}

	public void backtrack(Level level) {
		// no need for backtracking, we let removeClause do the job
	}
	
	@Override
	public String toString() {
		return dump().toString();
	}
	
	public Set<String> dump() {
		Set<String> result = new HashSet<String>();
		for (LiteralSignature signature : signatures.values()) {
			result.add(signature.dump());
		}
		return result;
	}
	
	private static class SignatureDescriptor {
		PredicateLiteralDescriptor descriptor;
		boolean isPositive;
		int position;
		SignatureDescriptor(PredicateLiteralDescriptor descriptor, boolean isPositive, int position) {
			this.position = position;
			this.descriptor = descriptor;
			this.isPositive = isPositive;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SignatureDescriptor) {
				SignatureDescriptor tmp = (SignatureDescriptor) obj;
				return position == tmp.position && isPositive == tmp.isPositive && descriptor.equals(tmp.descriptor);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return descriptor.hashCode() * 37 + position + (isPositive?0:1);
		}
		@Override
		public String toString() {
			return descriptor.toString()+"("+position+")";
		}
	}
	
	private static class LinkDescriptor {
		LiteralSignature signature1, signature2;
		LinkDescriptor(LiteralSignature signature1, LiteralSignature signature2) {
			this.signature1 = signature1;
			this.signature2 = signature2;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof LinkDescriptor) {
				LinkDescriptor temp = (LinkDescriptor) obj;
				return (signature1.equals(temp.signature1) && signature2.equals(temp.signature2))
					|| (signature1.equals(temp.signature2) && signature2.equals(temp.signature1));
			}
			return false;
		}
		@Override
		public int hashCode() {
			return signature1.hashCode() + signature2.hashCode();
		}
	}

	private static class ConstantDescriptor {
		Constant constant;
		LiteralSignature signature;
		ConstantDescriptor(Constant constant, LiteralSignature signature) {
			this.constant = constant;
			this.signature = signature;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConstantDescriptor) {
				ConstantDescriptor temp = (ConstantDescriptor) obj;
				return constant.equals(temp.constant) && signature.equals(temp.signature);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return constant.hashCode()*37 + signature.hashCode();
		}
	}
}
