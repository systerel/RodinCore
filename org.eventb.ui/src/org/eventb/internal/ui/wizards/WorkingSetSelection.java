/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.wizards;

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
		StringBuilder result =  new StringBuilder();
		String sep = "";
		for (IWorkingSet set : workingSets) {
			result.append(sep);
			sep = ", ";
			result.append(set.getLabel());
		}
		return result.toString();
	}
	
	public IWorkingSet[] getWorkingSets() {
		return workingSets;
	}
	
	/**
	 * Two <code>WorkingSetSelection</code> are considered equal, if they
	 * contain the same workingSets in the same order
	 * 
	 * @param other
	 *            other object to compare with
	 * @return <code>true</code> iff they are equal according to the above
	 *         description, false otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (! (other instanceof WorkingSetSelection))
			return false;
		WorkingSetSelection selection = (WorkingSetSelection) other;
		if (Arrays.equals(workingSets, selection.getWorkingSets())){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return workingSets.hashCode();
	}
	
}
