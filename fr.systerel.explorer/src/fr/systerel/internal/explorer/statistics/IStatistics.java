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
package fr.systerel.internal.explorer.statistics;

/**
 * This describes the statistics of an element that is associated with proof obligations.
 * The element that it is associated with can be reached via <code>getParent</code>.
 * It is possible that this statistics is an aggregate of several statistics. 
 * In that case there is no parent element.
 *
 */
public interface IStatistics {
	public int getTotal();

	public int getUndischarged();
	
	public int getManual();

	public int getAuto();
	
	public int getReviewed();
	
	/**
	 * 
	 * @return the number of Proof Obligations that are undischarged but not reviewed
	 */
	public int getUndischargedRest();
	
	/**
	 * 
	 * @return a Label for the parent Element of this Statistics (e.g. the machine, project, invariant...)
	 *			or null if this is an Aggregate Statistics
	 */			
	public String getParentLabel();

	/**
	 * 
	 * @return the parent Element of this Statistics (e.g. the machine, project, invariant...)
	 *			or null if this is an Aggregate Statistics
	 */			
	public Object getParent();
	
	public boolean isAggregate();
	
	public void buildCopyString (StringBuilder builder, boolean copyLabel, Character separator);
}
