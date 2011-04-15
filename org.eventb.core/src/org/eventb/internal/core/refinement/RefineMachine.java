/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.refinement;

import static org.eventb.core.EventBAttributes.GENERATED_ATTRIBUTE;
import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Refinement participant for refining a machine.
 * 
 * @author Nicolas Beauger
 * 
 */
public class RefineMachine implements IRefinementParticipant {

	@Override
	public void process(IInternalElement refinedRoot,
			IInternalElement sourceRoot, IProgressMonitor monitor)
			throws RodinDBException {
		final IMachineRoot con = (IMachineRoot) refinedRoot;
		final IMachineRoot abs = (IMachineRoot) sourceRoot;
		con.setConfiguration(abs.getConfiguration(), null);
		createRefinesMachineClause(con, abs, monitor);
		copyChildrenOfType(con, abs, ISeesContext.ELEMENT_TYPE, monitor);
		copyChildrenOfType(con, abs, IVariable.ELEMENT_TYPE, monitor);
		createEvents(con, abs, monitor);
		removeGenerated(con, monitor);
	}

	private void createRefinesMachineClause(IMachineRoot con, IMachineRoot abs,
			IProgressMonitor monitor) throws RodinDBException {
		final IRefinesMachine refines = con.createChild(
				IRefinesMachine.ELEMENT_TYPE, null, monitor);
		refines.setAbstractMachineName(abs.getComponentName(), monitor);
	}

	private static <T extends IInternalElement> void copyChildrenOfType(
			IEventBRoot destination, IEventBRoot original,
			IInternalElementType<T> type, IProgressMonitor monitor)
			throws RodinDBException {

		final T[] elements = original.getChildrenOfType(type);
		if (elements.length == 0)
			return;
		final IEventBRoot[] containers = new IEventBRoot[] { destination };
		final IRodinDB rodinDB = destination.getRodinDB();
		rodinDB.copy(elements, containers, null, null, false, monitor);
	}

	private static void copyAttributes(IInternalElement destination,
			IInternalElement original, IProgressMonitor monitor)
			throws RodinDBException {
		for (IAttributeValue value : original.getAttributeValues()) {
			destination.setAttributeValue(value, monitor);
		}
	}

	private static void removeGenerated(IInternalElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(GENERATED_ATTRIBUTE, monitor);
		final IRodinElement[] children = element.getChildren();
		for (IRodinElement child : children) {
			removeGenerated((IInternalElement) child, monitor);
		}
	}

	private void createEvents(IMachineRoot con, IMachineRoot abs,
			IProgressMonitor monitor) throws RodinDBException {
		final IEvent[] absEvts = abs.getChildrenOfType(IEvent.ELEMENT_TYPE);
		for (IEvent absEvt : absEvts) {
			createEvent(con, absEvt, monitor);
		}
	}

	private void createEvent(IMachineRoot con, IEvent absEvt,
			IProgressMonitor monitor) throws RodinDBException {
		final String name = absEvt.getElementName();
		final String label = absEvt.getLabel();
		final IEvent conEvt = con.getEvent(name);
		conEvt.create(null, monitor);
		copyAttributes(conEvt, absEvt, monitor);
		conEvt.setExtended(true, monitor);
		createRefinesEventClause(conEvt, label, monitor);
		setConvergence(conEvt, absEvt, monitor);
	}

	private void createRefinesEventClause(IEvent conEvt, String label,
			IProgressMonitor monitor) throws RodinDBException {
		if (!label.equals(IEvent.INITIALISATION)) {
			final IRefinesEvent refines = conEvt.createChild(
					IRefinesEvent.ELEMENT_TYPE, null, monitor);
			refines.setAbstractEventLabel(label, monitor);
		}
	}

	private void setConvergence(IEvent conEvt, IEvent absEvt,
			IProgressMonitor monitor) throws RodinDBException {
		final Convergence absCvg = absEvt.getConvergence();
		final Convergence conCvg = computeRefinementConvergence(absCvg);
		conEvt.setConvergence(conCvg, monitor);
	}

	private Convergence computeRefinementConvergence(Convergence absCvg) {
		switch (absCvg) {
		case ANTICIPATED:
			return ANTICIPATED;
		case CONVERGENT:
		case ORDINARY:
			return ORDINARY;
		}
		return ORDINARY;
	}

}
