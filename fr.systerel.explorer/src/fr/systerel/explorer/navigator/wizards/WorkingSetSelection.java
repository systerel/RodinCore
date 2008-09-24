/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.navigator.wizards;

import java.util.Arrays;

import org.eclipse.ui.IWorkingSet;

/**
 * This class presents a selection of workingSets
 * It is used by the WorkingSetControl.
 *
 */
public class WorkingSetSelection {
	
	private IWorkingSet[] workingSets;
	
	
	public WorkingSetSelection(IWorkingSet[] workingSets) {
		this.workingSets =  workingSets;
	}
	
	@Override
	public String toString() {
		String result =  new String();
		for (IWorkingSet set : workingSets) {
			if (!result.isEmpty()){
				 result = result +", " ;
			}
			result =  result + set.getLabel();
		}
		return result;
	}
	
	public IWorkingSet[] getWorkingSets() {
		return workingSets;
	}
	
	/**
	 * Two WorkingSetSelection are considered equal, if the contain the same workingSets in the same order
	 * @param selection
	 * @return true if they are equal according to the above description, false otherwise
	 */
	public boolean equals(WorkingSetSelection selection){
		if (this == selection) {
			return true;
		}
		if (Arrays.equals(workingSets, selection.getWorkingSets())){
			return true;
		}
		return false;
	}
}
