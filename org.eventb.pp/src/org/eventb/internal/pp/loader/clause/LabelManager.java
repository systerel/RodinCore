/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.LinkedHashSet;

import org.eventb.internal.pp.loader.formula.ILabelizableFormula;

public class LabelManager {

	private boolean gettingDefinitions = false;
	private boolean forceLabelize = false;
	private LinkedHashSet<ILabelizableFormula> toLabelizeNeg = new LinkedHashSet<ILabelizableFormula>();
	private LinkedHashSet<ILabelizableFormula> toLabelizePos = new LinkedHashSet<ILabelizableFormula>();
	private LinkedHashSet<ILabelizableFormula> toLabelizeEq = new LinkedHashSet<ILabelizableFormula>();
	
	public LabelManager() {}
	
	public void addLabel(ILabelizableFormula formula, boolean pos) {
		if (pos) addLabel(formula, toLabelizePos);
		else addLabel(formula, toLabelizeNeg);
	}
	
	public void addEquivalenceLabel(ILabelizableFormula formula) {
		addLabel(formula, toLabelizeEq);
	}
	
	private void addLabel(ILabelizableFormula formula, LinkedHashSet<ILabelizableFormula> set) {
		if (!set.contains(formula)) {
			ClauseBuilder.debug("Adding "+formula+" to list of clauses that must be labelized");
			set.add(formula);
		}
	}

	private int currentIndexPos = 0;
	private int currentIndexNeg = 0;
	private int currentIndexEq = 0;
	
	private ILabelizableFormula nextFormula;
	private boolean isNextPositive;
//	private boolean isNextEquivalence;
	public void nextLabelizableFormula() {
		nextFormula = null;
		if (currentIndexPos != toLabelizePos.size()) {
			nextFormula = getLabelizableFormula(toLabelizePos, currentIndexPos);
			currentIndexPos++;
			isNextPositive = true;
//			isNextEquivalence = false;
		}
		else if (currentIndexNeg != toLabelizeNeg.size()) {
			nextFormula = getLabelizableFormula(toLabelizeNeg, currentIndexNeg);
			currentIndexNeg++;
//			isNextEquivalence = false;
			isNextPositive = false;
		}
		else if (currentIndexEq != toLabelizeEq.size()) {
			nextFormula = getLabelizableFormula(toLabelizeEq, currentIndexEq);
			currentIndexEq++;
//			isNextEquivalence = true;
		}
	}
	
	public ILabelizableFormula getNextFormula() {
		return nextFormula;
	}
	
//	public boolean isNextEquivalence() {
//		return isNextEquivalence;
//	}
	
	public boolean isNextPositive() {
		return isNextPositive;
	}
	
	private ILabelizableFormula getLabelizableFormula(LinkedHashSet<ILabelizableFormula> set, int index) {
		return set.toArray(new ILabelizableFormula[set.size()])[index];
	}
	
//	/**
//	 * Sets the flag "force labelize" to the specified value.
//	 * 
//	 * This flag is set to <code>true</code> by a certain formula to enforce
//	 * the labelization of its children. This is useful to enforce labelizing what
//	 * follows an existential literal for instance.
//	 * 
//	 * It should be set to <code>false</code> each time a new formula is normalized.
//	 * 
//	 * @see LabelManager#isForceLabelize()
//	 * @param forceLabelize the new value to set
//	 */
//	public void setForceLabelize(boolean forceLabelize) {
//		this.forceLabelize = forceLabelize;
//	}
	
//	/**
//	 * Returns the value of the "force labelize" flag.
//	 * 
//	 * @return the value of the "force labelize" flag	
//	 */
//	public boolean isForceLabelize() {
//		return this.forceLabelize;
//	}
//	
	/**
	 * @return
	 */
	public boolean isGettingDefinitions() {
		return gettingDefinitions;
	}
	
	/**
	 * Sets the value of the "getting definitions" flag.
	 * 
	 * This flag should be set to <code>true</code> when the normalizer
	 * is getting the definition of a formula.
	 * 
	 * @param gettingDefinitions the new value for the flag
	 */
	public void setGettingDefinitions(boolean gettingDefinitions) {
		this.gettingDefinitions = gettingDefinitions;
	}
	
}
