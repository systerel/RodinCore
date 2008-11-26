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

package fr.systerel.explorer.statistics;

import org.eclipse.core.resources.IProject;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * Provides some static utility methods for statistics
 * 
 */
public class StatisticsUtil {
	/**
	 * Decides, if a given selection is valid for statistics
	 * 
	 * @param elements
	 *            The selected elements
	 * @return <code>null</code>, if the selection is valid, otherwise a
	 *         String describing why it is not valid.
	 */
	public static String isValidSelection(Object[] elements) {
		int level = 0;
		int projects = 1;
		int machConts = 2;
		int nodes = 3;
		int invs = 4;
		int pos = 5;

		for (Object el : elements) {
			String selection = "Selection is not valid.";
			if (el instanceof IProject) {
				IRodinProject proj = RodinCore.valueOf((IProject) el);

				if (proj.exists()) {
					ModelProject modelproject = ModelController
							.getProject(proj);
					if (modelproject != null) {
						if (level == 0) {
							level = projects;
						} else if (level != projects) {
							return selection;
						}
					} else
						return "Expand the projects at least once to see the statistics.";
				} else
					return "Must be a Rodin Project and not closed.";
				
			} else if (el instanceof IMachineRoot || el instanceof IContextRoot) {
				if (level == 0) {
					level = machConts;
				} else if (level != machConts) {
					return selection;
				}
			} else if (el instanceof IElementNode) {
				IInternalElementType<?> type = ((IElementNode) el)
						.getChildrenType();
				if (type == IVariable.ELEMENT_TYPE) {
					return "No statistics for this selection.";
				}
				if (type == ICarrierSet.ELEMENT_TYPE) {
					return "No statistics for this selection.";
				}
				if (type == IConstant.ELEMENT_TYPE) {
					return "No statistics for this selection.";
				}
				// for the proof obligation node only other proof obligations
				// nodes are allowed
				// otherwise we may count some proof obligations twice
				if (type == IPSStatus.ELEMENT_TYPE) {
					if (level == 0) {
						level = pos;
					} else if (level != pos) {
						return selection;
					}
					// all other nodes (invariants, events, theorems, axioms)
				} else {
					if (level == 0) {
						level = nodes;
					} else if (level != nodes) {
						return selection;
					}
				}
			} else if (el instanceof IInvariant || el instanceof IEvent
					|| el instanceof ITheorem || el instanceof IAxiom) {
				if (level == 0) {
					level = invs;
				} else if (level != invs) {
					return selection;
				}
			} else
				return "No statistics for this selection.";
		}
		return null;
	}

	/**
	 * Decides, if the details view is required for a given selection
	 * 
	 * @param elements
	 *            The selected elements
	 * @return <code>true</code>, if the details view is required,
	 *         <code>false</code> otherwise
	 */
	public static boolean detailsRequired(Object[] elements) {
		// the selection entered here is never empty
		if (elements.length > 1) {
			return true;
		}
		if (elements[0] instanceof IProject
				|| elements[0] instanceof IMachineRoot
				|| elements[0] instanceof IContextRoot
				|| elements[0] instanceof IElementNode) {
			return true;
		}

		return false;
	}

}
