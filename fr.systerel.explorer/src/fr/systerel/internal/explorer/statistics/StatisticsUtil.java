/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
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
import org.eventb.core.IEventBRoot;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;

/**
 * Provides some static utility methods for statistics
 * 
 */
public class StatisticsUtil {
	private static final String PROJECTS = "projects"; //$NON-NLS-1$
	private static final String MACH_CONT = "machines contexts"; //$NON-NLS-1$
	private static final String NODES = "nodes"; //$NON-NLS-1$
	private static final String ELEMS = "elements"; //$NON-NLS-1$
	private static final String POS = "proof obligations"; //$NON-NLS-1$
	
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
		String level = ""; //$NON-NLS-1$
		//describes the level of the current element.
		String new_level;

		for (Object el : elements) {
			if (el instanceof IProject) {
				new_level = PROJECTS;
				final IRodinProject proj = RodinCore.valueOf((IProject) el);

				if (proj.exists()) {
					final ModelProject modelproject = ModelController
							.getProcessedProject(proj);
					if (modelproject == null) {
						return Messages.getString("statistics.expandAtLeastOnce"); //$NON-NLS-1$
					}
				} else {
					return Messages.getString("statistics.mustBeRodinProject"); //$NON-NLS-1$
				}

			} else if (el instanceof IMachineRoot || el instanceof IContextRoot) {
				new_level = MACH_CONT;
				
			} else if (el instanceof IElementNode) {
				final IInternalElementType<?> type = ((IElementNode) el)
						.getChildrenType();
				if (type == IVariable.ELEMENT_TYPE
						|| type == ICarrierSet.ELEMENT_TYPE
						|| type == IConstant.ELEMENT_TYPE) {
					return Messages.getString("statistics.noStatisticsForThisSelection"); //$NON-NLS-1$
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
					|| el instanceof IAxiom) {
				new_level = ELEMS;
			} else
				return Messages.getString("statistics.noStatisticsForThisSelection"); //$NON-NLS-1$
			
			// check the levels. all elements must be of the same level for the
			// selection to be valid
			if (level.length() == 0) {
				level = new_level;
			} else if (level != new_level) {
				return Messages.getString("statistics.selectionIsNotValidCombinationOfElements"); //$NON-NLS-1$
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

	public static String getParentLabelOf(Object parent) {
		IRodinElement internal_parent = null;
		if (parent instanceof IElementNode) {
			return ((IElementNode) parent).getLabel();
		}
		if (parent instanceof IModelElement) {
			internal_parent = ((IModelElement) parent).getInternalElement();
		}
		if (internal_parent instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) internal_parent).getLabel();
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting label for " +internal_parent);
			}
		}
		if (internal_parent instanceof IEventBRoot) {
			return ((IEventBRoot) internal_parent).getComponentName();
		}
		if (internal_parent != null) {
			return internal_parent.getElementName();
		}
		return parent.toString();
	}
	
	/**
	 * Indicates whether elements of the given type can possibly have proof obligations 
	 * associated with them.
	 * @param type The type of the elements
	 * @return true, if they can have proof obligations, false otherwise.
	 */
	public static boolean canHavePOs(IInternalElementType<?> type) {
		if (type == IInvariant.ELEMENT_TYPE) {
			return true;
		}
		if (type == IAxiom.ELEMENT_TYPE) {
			return true;
		}
		if (type == IEvent.ELEMENT_TYPE) {
			return true;
		}
		if (type == IPSStatus.ELEMENT_TYPE) {
			return true;
		}
		return false;
	}

}
