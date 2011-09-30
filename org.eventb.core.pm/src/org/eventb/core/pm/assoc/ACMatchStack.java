/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.Matcher;

/**
 * 
 * @author maamria
 *
 */
public class ACMatchStack<F extends Formula<F>> {

	private Matcher matcher;
	
	private Deque<Match<F>> matchesStack;
	private Deque<IndexedFormula<F>> usedUp;
	private Deque<List<Match<F>>> exploredMatches;
	private IBinding initialBinding;
	
	public ACMatchStack(Matcher matcher, Match<F> initialMatch){
		this.matcher = matcher;
		matchesStack = new ArrayDeque<Match<F>>();
		usedUp = new ArrayDeque<IndexedFormula<F>>();
		exploredMatches = new ArrayDeque<List<Match<F>>>();
		
		this.initialBinding = initialMatch.getBinding();
		usedUp.push(initialMatch.getIndexedFormula());
		exploredMatches.push(new ArrayList<Match<F>>());
	}
	
	public boolean push(Match<F> nextMatch){
		if (exploredMatches.contains(nextMatch)){
			return false;
		}
		if (usedUp.contains(nextMatch.getIndexedFormula())){
			return false;
		}
		if (!isMatchAcceptable(nextMatch)){
			return false;
		}
		exploredMatches.peek().add(nextMatch);
		matchesStack.push(nextMatch);
		usedUp.push(nextMatch.getIndexedFormula());
		exploredMatches.push(new ArrayList<Match<F>>());
		return true;
	}
	
	public void pop() {
		matchesStack.pop();
		usedUp.pop();
		exploredMatches.pop();
	}
	
	public IBinding getFinalBinding() {
		IBinding internalBinding = matcher.getMatchingFactory().createBinding(false, matcher.getFactory());
		internalBinding.insertBinding(initialBinding);
		Iterator<Match<F>> elements = matchesStack.descendingIterator();
		while (elements.hasNext()) {
			internalBinding.insertBinding(elements.next().getBinding());
		}
		return internalBinding;
	}
	
	public List<IndexedFormula<F>> getUsedUpFormulae(){
		return new ArrayList<IndexedFormula<F>>(usedUp);
	}
	
	private boolean isMatchAcceptable(Match<F> match) {
		IBinding internalBinding = getFinalBinding();
		return internalBinding.isBindingInsertable(match.getBinding());
	}
}
