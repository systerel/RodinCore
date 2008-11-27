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

package fr.systerel.internal.explorer.statistics;

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

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;

/**
 * Provides some static utility methods for statistics
 * 
 */
public class StatisticsUtil {
	private static final String PROJECTS = "projects";
	private static final String MACH_CONT = "machines contexts";
	private static final String NODES = "nodes";
	private static final String ELEMS = "elements";
	private static final String POS = "proof obligations";
	
	/**
	 * Decides, if a given selection is valid for statistics
	 * 
	 * @param elements
	 *            The selected elements
	 * @return <code>null</code>, if the selection is valid, otherwise a
	 *         String describing why it is not valid.
	 */
	public static String isValidSelection(Object[] elements) {
		//describes the level of previous elements.
		String level = "";
		//describes the level of the current element.
		String new_level;

		for (Object el : elements) {
			if (el instanceof IProject) {
				new_level = PROJECTS;
				IRodinProject proj = RodinCore.valueOf((IProject) el);

				if (proj.exists()) {
					ModelProject modelproject = ModelController
							.getProject(proj);
					if (modelproject == null) {
						return "Expand the projects at least once to see the statistics.";
					}
				} else {
					return "Must be a Rodin Project and not closed.";
				}

			} else if (el instanceof IMachineRoot || el instanceof IContextRoot) {
				new_level = MACH_CONT;
				
			} else if (el instanceof IElementNode) {
				IInternalElementType<?> type = ((IElementNode) el)
						.getChildrenType();
				if (type == IVariable.ELEMENT_TYPE
						|| type == ICarrierSet.ELEMENT_TYPE
						|| type == IConstant.ELEMENT_TYPE) {
					return "No statistics for this selection.";
				}
				// for the proof obligation node only other proof obligations
				// nodes are allowed
				// otherwise we may count some proof obligations twice
				if (type == IPSStatus.ELEMENT_TYPE) {
					new_level = POS;
					// all other NODES (invariants, events, theorems, axioms)
				} else {
					new_level = NODES;
				}
			} else if (el instanceof IInvariant || el instanceof IEvent
					|| el instanceof ITheorem || el instanceof IAxiom) {
				new_level = ELEMS;
			} else
				return "No statistics for this selection.";
			
			// check the levels. all elements must be of the same level for the
			// selection to be valid
			if (level.length() == 0) {
				level = new_level;
			} else if (level != new_level) {
				return "Selection is not a valid combination of elements.";
			}
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
