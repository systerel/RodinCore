/*******************************************************************************
 * Copyright (c) 2007-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of {@link IAttributeFactory} providing the factory
 *         methods for inherited attribute of events.
 */
public class InheritedAttributeFactory implements IAttributeFactory {

	/**
	 * Constant string for TRUE (i.e. inherited).
	 */
	private final String TRUE = Messages.attributeFactory_inherited_true;

	/**
	 * Constant string for FALSE (i.e. non-inherited).
	 */
	private final String FALSE = Messages.attributeFactory_inherited_false;

	/**
	 * Gets the string representation of the value of the inherited attribute,
	 * i.e. either {@link #TRUE} or {@link #FALSE}.
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getValue(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element;
		return event.isInherited() ? TRUE : FALSE;
	}

	/**
	 * Sets the value of the inherited attribute according to the input string
	 * representation. The new inherited value is <code>true</code> if the
	 * input string is {@link #TRUE} and the value is <code>false</code>
	 * otherwise. The inherited value is changed only if the inherit attribute
	 * did not exist before or the old value is different from the new value. If
	 * the value is changed, the event also changed accordingly:
	 * <ul>
	 * <li>If the new value is <code>true</code>, i.e. the event becomes
	 * inherited, then the content of the event is removed. This includes
	 * REFINES EVENT, PARAMETERS, GUARDS and ACTIONS attributes.
	 * <li>If the new value is <code>false</code>, i.e. the event becomes
	 * non-inherited, then the REFINES EVENT attribute is set to point to the
	 * event with the same name, then the content of the latest non-inherited
	 * abstract event corresponding to the event (if any) will be copied to this
	 * event.
	 * </ul>
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setValue(org.rodinp.core.IAttributedElement,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IEvent;

		final IEvent event = (IEvent) element;
		String value;
		try {
			value = getValue(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			final boolean inherited = newValue.equalsIgnoreCase(TRUE);
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pMonitor) throws CoreException {
					event.setInherited(inherited, pMonitor);
					if (inherited) {
						// Remove the body of the event, this includes REFINES_EVENT,
						// PARAMETERS, GUARDS, WITNESS, ACTIONS.
						IRefinesEvent[] refinesEvents = event.getRefinesClauses();
						for (IRefinesEvent refinesEvent : refinesEvents) {
							refinesEvent.delete(true, pMonitor);
						}
						IVariable[] parameters = event.getVariables();
						for (IVariable parameter : parameters) {
							parameter.delete(true, pMonitor);
						}
						IGuard[] guards = event.getGuards();
						for (IGuard guard : guards) {
							guard.delete(true, pMonitor);
						}
						IWitness[] witnesses = event.getWitnesses();
						for (IWitness witness : witnesses) {
							witness.delete(true, pMonitor);
						}
						IAction[] actions = event.getActions();
						for (IAction action : actions) {
							action.delete(true, pMonitor);
						}
					}
					else {
						// Remove the old content or assume the content is empty before?
						
						// Set the REFINES EVENT attribute to the event with the
						// same name.
						String name = EventBUtils.getFreeChildName(event,
								IRefinesEvent.ELEMENT_TYPE,
								"internal_refinesEvent"); //$NON-NLS-1$
						String abs_label = event.getLabel();
						IRefinesEvent newRefEvt = event.getInternalElement(
										IRefinesEvent.ELEMENT_TYPE,
										name);
						assert !newRefEvt.exists();
						newRefEvt.create(null, pMonitor);
						newRefEvt.setAbstractEventLabel(abs_label, null);
						
						// Find the latest non-inherited abstract event.
						IEvent abstractEvent = EventBUtils
								.getNonInheritedAbstractEvent(event);
						// Copy the body of the abstract event, this includes
						// PARAMETERS, GUARDS, WITNESS, ACTIONS.
						if (abstractEvent != null) {
							IVariable[] parameters = abstractEvent.getVariables();
							for (IVariable parameter : parameters) {
								parameter.copy(event, null, null, false, pMonitor);
							}
							IGuard[] guards = abstractEvent.getGuards();
							for (IGuard guard : guards) {
								guard.copy(event, null, null, false, pMonitor);
							}
							IWitness[] witnesses = abstractEvent.getWitnesses();
							for (IWitness witness : witnesses) {
								witness.copy(event, null, null, false, pMonitor);
							}
							IAction[] actions = abstractEvent.getActions();
							for (IAction action : actions) {
								action.copy(event, null, null, false, pMonitor);
							}
						}
					}
				}
				
			}, monitor);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getPossibleValues(org.rodinp.core.IAttributedElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		return new String[] { TRUE, FALSE };
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#removeAttribute(org.rodinp.core.IAttributedElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.INHERITED_ATTRIBUTE, monitor);
	}

	/**
	 * Default value for inherited attribute is <code>false</code>, i.e.
	 * non-inherited.
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setDefaultValue(org.eventb.ui.eventbeditor.IEventBEditor,
	 *      org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IEvent)) {
			return;
		}
		IEvent event = (IEvent) element;
		event.setInherited(false, monitor);
	}

}
