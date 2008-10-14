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


package fr.systerel.explorer.masterDetails.Statistics;



/**
 * @author Administrator
 *
 */
public class AggregateStatistics implements IStatistics{
	
	private IStatistics[] internal_statistics;
	
	public AggregateStatistics(IStatistics[] statistics){
		internal_statistics = statistics;
	}
	

	public int getAuto() {
		int result = 0;
		for (IStatistics stat : internal_statistics) {
			result += stat.getAuto();
		}
		return result;
	}

	public int getManual() {
		int result = 0;
		for (IStatistics stat : internal_statistics) {
			result += stat.getManual();
		}
		return result;
	}

	/**
	 * Aggregate statistics don't have a parentLabel.
	 */
	public String getParentLabel() {
		return null;
	}

	public int getReviewed() {
		int result = 0;
		for (IStatistics stat : internal_statistics) {
			result += stat.getReviewed();
		}
		return result;
	}

	public int getTotal() {
		int result = 0;
		for (IStatistics stat : internal_statistics) {
			result += stat.getTotal();
		}
		return result;
	}

	public int getUndischarged() {
		int result = 0;
		for (IStatistics stat : internal_statistics) {
			result += stat.getUndischarged();
		}
		return result;
	}

	public int getUndischargedRest() {
		int result = 0;
		for (IStatistics stat : internal_statistics) {
			result += stat.getUndischargedRest();
		}
		return result;
	}

	public boolean isAggregate() {
		return true;
	}


	/**
	 * Aggregates have no parent.
	 */
	public Object getParent() {
		return null;
	}


}
