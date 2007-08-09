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

	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizeNeg = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizePos = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	
	public LabelManager() {
		// do nothing
	}
	
	public void addLabel(AbstractLabelizableFormula<?> formula, boolean pos) {
		if (pos) addLabel(formula, toLabelizePos);
		else addLabel(formula, toLabelizeNeg);
	}
	
	private void addLabel(AbstractLabelizableFormula<?> formula, LinkedHashSet<AbstractLabelizableFormula<?>> set) {
		if (!set.contains(formula)) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Adding "+formula+" to list of clauses that must be labelized");
			set.add(formula);
		}
	}
	
	public boolean hasLabel(AbstractLabelizableFormula<?> formula) {
		return toLabelizePos.contains(formula) 
			|| toLabelizeNeg.contains(formula);
	}

	private int currentIndexPos = 0;
	private int currentIndexNeg = 0;
	
	private AbstractLabelizableFormula<?> nextFormula;
	private boolean isNextPositive;
	public void nextLabelizableFormula() {
		nextFormula = null;
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
	}
	
	public AbstractLabelizableFormula<?> getNextFormula() {
		return nextFormula;
	}
	
	public boolean isNextPositive() {
		return isNextPositive;
	}
	
	private AbstractLabelizableFormula<?> getLabelizableFormula(LinkedHashSet<AbstractLabelizableFormula<?>> set, int index) {
		return set.toArray(new AbstractLabelizableFormula[set.size()])[index];
	}
	
}
