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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


/**
 * This comparator can be used for sorting statistics by various criteria.
 *
 */
public class StatisticsDetailsComparator extends ViewerComparator {
	/**
	 * Constructor argument values that indicate to sort items by 
	 * name, total, manual., auto., reviewed or undischarged.
	 */
	public final static int NAME 			= 1;
	public final static int TOTAL 			= 2;
	public final static int AUTO 			= 3;
	public final static int MANUAL 			= 4;
	public final static int REVIEWED		= 5;
	public final static int UNDISCHARGED	= 6;
	

	private int criteria;

	/**
	 * Creates a resource sorter that will use the given sort criteria.
	 *
	 * @param criteria The sort criterion to use.
	 */
	public StatisticsDetailsComparator(int criteria) {
		super();
		this.criteria = criteria;
	}
	
	/* (non-Javadoc)
	 * Method declared on ViewerSorter.
	 */
	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof Statistics && o2 instanceof Statistics) {
			Statistics stats1 = (Statistics) o1;
			Statistics stats2 = (Statistics) o2;

			switch (criteria) {
				case NAME : 
					//ascending order
					return stats1.getParentLabel().compareTo(stats2.getParentLabel()); 
				case TOTAL :
					//descending order
					return stats1.getTotal() > stats2.getTotal() ? -1 : (stats1.getTotal() < stats2.getTotal()) ? 1 : 0;
				case AUTO :
					//descending order
					return stats1.getAuto() > stats2.getAuto() ? -1 : (stats1.getAuto() < stats2.getAuto()) ? 1 : 0;
				case MANUAL :
					//descending order
					return stats1.getManual() > stats2.getManual() ? -1 : (stats1.getManual() < stats2.getManual()) ? 1 : 0;
				case REVIEWED :
					//descending order
					return stats1.getReviewed() > stats2.getReviewed() ? -1 : (stats1.getReviewed() < stats2.getReviewed()) ? 1 : 0;
				case UNDISCHARGED :
					//descending order
					return stats1.getUndischargedRest() > stats2.getUndischargedRest() ? -1 : (stats1.getUndischargedRest() < stats2.getUndischargedRest()) ? 1 : 0;
				default:
					return 0;
			}
			
		}
		return 0;
		
	}
	
	
}
