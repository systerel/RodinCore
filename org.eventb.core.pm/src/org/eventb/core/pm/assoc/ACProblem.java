/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.pm.IBinding;

/**
 * A basic implementation of an AC matching problem.
 * 
 * @author maamria
 * 
 */
public abstract class ACProblem<F extends Formula<F>> extends AssociativityProblem<F> {

	public ACProblem(int tag, F[] formulae, F[] patterns, IBinding existingBinding) {
		super(tag, formulae, patterns, existingBinding);
	}

	public IBinding solve(boolean acceptPartialMatch) {
		if (!isSolvable) {
			return null;
		}
		IBinding initialBinding = existingBinding.clone();
		boolean searchSpaceEmpty = searchSpace.size() == 0;
		// trace consumed formulae
		List<IndexedFormula<F>> usedUpFormulae = new ArrayList<IndexedFormula<F>>();
		if (!searchSpaceEmpty) {
			MatchEntry<F> matchEntry = searchSpace.get(0);
			List<Match<F>> matchesList = matchEntry.getMatches();
			boolean solved = false;
			for (Match<F> match : matchesList) {
				ACMatchStack<F> matchStack = new ACMatchStack<F>(matcher, match);
				if (explore(1, matchStack)) {
					IBinding matchBinding = matchStack.getFinalBinding();
					matchBinding.makeImmutable();
					// if we cannot insert the match binding in the original binding
					if(!initialBinding.isBindingInsertable(matchBinding)){
						continue;
					}
					solved = initialBinding.insertBinding(matchBinding);
					usedUpFormulae = matchStack.getUsedUpFormulae();
					break;
				}
			}
			if (!solved) {
				return null;
			}
		}
		if(!mapVariables(usedUpFormulae, initialBinding)){
			return null;
		}
		List<IndexedFormula<F>> leftFormulae = new ArrayList<IndexedFormula<F>>(indexedFormulae);
		leftFormulae.removeAll(usedUpFormulae);
		if (leftFormulae.size() > 0){
			if(!acceptPartialMatch){
				return null;
			}
			addAssociativeComplement(leftFormulae, initialBinding);
		}
		return initialBinding;
	}

	protected boolean explore(int patternIndex, ACMatchStack<F> matchStack) {
		if (patternIndex < 1) {
			// we backtracked too much
			return false;
		}
		if (patternIndex == searchSpace.size()) {
			// finished all of them
			return true;
		}
		MatchEntry<F> matchEntry = searchSpace.get(patternIndex);
		List<Match<F>> matchesList = matchEntry.getMatches();
		for (Match<F> match : matchesList) {
			if (matchStack.push(match)) {
				return explore(patternIndex + 1, matchStack);
			}
		}
		matchStack.pop();
		return explore(patternIndex - 1, matchStack);

	}
	
	/**
	 * Maps the rest of the variables to finish the mapping process.
	 * @param usedUpFormulae the formulae not available to draw matches from
	 * @param initialBinding the binding to fill
	 * @return whether all variables have been mapped successfully
	 */
	protected abstract boolean mapVariables(List<IndexedFormula<F>> usedUpFormulae, IBinding initialBinding);
	
	/**
	 * Sets the associative complement consisting of the given formulae.
	 * @param formulae the formulae
	 * @param binding the target binding
	 */
	protected abstract void addAssociativeComplement(List<IndexedFormula<F>> formulae, IBinding binding);
}
