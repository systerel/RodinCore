/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.LinkedHashSet;

import org.eventb.internal.pp.loader.formula.AbstractLabelizableFormula;

public class LabelManager {

//	private boolean gettingDefinitions = false;
	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizeNeg = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizePos = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizeEq = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	
	public LabelManager() {
		// do nothing
	}
	
	public void addLabel(AbstractLabelizableFormula<?> formula, boolean pos) {
		if (pos) addLabel(formula, toLabelizePos);
		else addLabel(formula, toLabelizeNeg);
	}
	
	public void addEquivalenceLabel(AbstractLabelizableFormula<?> formula) {
		addLabel(formula, toLabelizeEq);
	}
	
	private void addLabel(AbstractLabelizableFormula<?> formula, LinkedHashSet<AbstractLabelizableFormula<?>> set) {
		if (!set.contains(formula)) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Adding "+formula+" to list of clauses that must be labelized");
			set.add(formula);
		}
	}
	
	public boolean hasLabel(AbstractLabelizableFormula<?> formula) {
		return toLabelizeEq.contains(formula) 
			|| toLabelizePos.contains(formula) 
			|| toLabelizeNeg.contains(formula);
	}

	private int currentIndexPos = 0;
	private int currentIndexNeg = 0;
	private int currentIndexEq = 0;
	
	private AbstractLabelizableFormula<?> nextFormula;
	private boolean isNextPositive;
	private boolean isNextEquivalence;
	public void nextLabelizableFormula() {
		nextFormula = null;
		isNextEquivalence = false;
		if (currentIndexPos != toLabelizePos.size()) {
			nextFormula = getLabelizableFormula(toLabelizePos, currentIndexPos);
			currentIndexPos++;
			isNextPositive = true;
		}
		else if (currentIndexNeg != toLabelizeNeg.size()) {
			nextFormula = getLabelizableFormula(toLabelizeNeg, currentIndexNeg);
			currentIndexNeg++;
			isNextPositive = false;
		}
		else if (currentIndexEq != toLabelizeEq.size()) {
			nextFormula = getLabelizableFormula(toLabelizeEq, currentIndexEq);
			currentIndexEq++;
			isNextEquivalence = true;
		}
	}
	
	public AbstractLabelizableFormula<?> getNextFormula() {
		return nextFormula;
	}
	
	public boolean isNextEquivalence() {
		return isNextEquivalence;
	}
	
	public boolean isNextPositive() {
		return isNextPositive;
	}
	
	private AbstractLabelizableFormula<?> getLabelizableFormula(LinkedHashSet<AbstractLabelizableFormula<?>> set, int index) {
		return set.toArray(new AbstractLabelizableFormula[set.size()])[index];
	}
	
//	public boolean isGettingDefinitions() {
//		return gettingDefinitions;
//	}
	
//	/**
//	 * Sets the value of the "getting definitions" flag.
//	 * 
//	 * This flag should be set to <code>true</code> when the normalizer
//	 * is getting the definition of a formula.
//	 * 
//	 * @param gettingDefinitions the new value for the flag
//	 */
//	public void setGettingDefinitions(boolean gettingDefinitions) {
//		this.gettingDefinitions = gettingDefinitions;
//	}
	
}
