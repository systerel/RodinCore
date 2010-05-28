/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added comparator for an empty column
 *******************************************************************************/

package fr.systerel.internal.explorer.statistics;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * This comparator can be used for sorting statistics by various criteria.
 * 
 */
public abstract class StatisticsDetailsComparator extends ViewerComparator {
	
	public static final boolean ASCENDING = true;
	protected boolean order = ASCENDING;
	
	/**
	 * Constructor argument values that indicate to sort items by name, total,
	 * manual., auto., reviewed or undischarged.
	 */
	public final static StatisticsDetailsComparator NAME = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			final String parentLbl1 = stats1.getParentLabel();
			final String parentLbl2 = stats2.getParentLabel();
			final int ascending = parentLbl1.compareTo(parentLbl2);
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator TOTAL = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			final int ascending = stats1.getTotal() - stats2.getTotal();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator AUTO = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			final int ascending = stats1.getAuto() - stats2.getAuto();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator MANUAL = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			final int ascending =  stats1.getManual() - stats2.getManual();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator REVIEWED = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			final int ascending = stats1.getReviewed() - stats2.getReviewed();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator UNDISCHARGED = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			final int undisRest1 = stats1.getUndischargedRest();
			final int undisRest2 = stats2.getUndischargedRest();
			final int ascending = undisRest1 - undisRest2;
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};
	
	public final static StatisticsDetailsComparator EMPTY = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			return 0;
		}

	};
	
	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof Statistics && o2 instanceof Statistics) {
			final Statistics stats1 = (Statistics) o1;
			final Statistics stats2 = (Statistics) o2;
			return compare(stats1, stats2);
		}
		return super.compare(viewer, o1, o2);

	}

	public abstract int compare(IStatistics stats1, IStatistics stats2);
	

	public void setOrder(boolean order) {
		this.order = order;
	}

	public boolean getOrder() {
		return order;
	}
	
}
