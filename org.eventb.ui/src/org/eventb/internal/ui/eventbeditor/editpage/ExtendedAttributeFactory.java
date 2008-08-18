/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
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
 *         methods for extended attribute of events.
 */
public class ExtendedAttributeFactory implements IAttributeFactory {

	/**
	 * Constant string for TRUE (i.e. extended).
	 */
	private final String TRUE = Messages.attributeFactory_extended_true;

	/**
	 * Constant string for FALSE (i.e. non-extended).
	 */
	private final String FALSE = Messages.attributeFactory_extended_false;

	/**
	 * Gets the string representation of the value of the extended attribute,
	 * i.e. either {@link #TRUE} or {@link #FALSE}.
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getValue(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element;
		return event.isExtended() ? TRUE : FALSE;
	}

	/**
	 * Sets the value of the extended attribute according to the input string
	 * representation. The new extended value is <code>true</code> if the
	 * input string is {@link #TRUE} and the value is <code>false</code>
	 * otherwise. The extended value is changed only if the extended attribute
	 * did not exist before or the old value is different from the new value. If
	 * the value is changed, the event also changes accordingly:
	 * <ul>
	 * <li>If the new value is <code>true</code>, i.e. the event becomes
	 * extended, then the parameters, guards and actions of the abstraction are
	 * removed from the extending event.
	 * <li>If the new value is <code>false</code>, i.e. the event becomes
	 * non-extended, then the contents of the latest non-extended abstract event
	 * corresponding to the event (if any) will be copied to this event,
	 * together with all extensions along the path down from that event.
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
			final boolean extended = newValue.equalsIgnoreCase(TRUE);
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pMonitor) throws CoreException {
					event.setExtended(extended, pMonitor);
					if (extended) {
						// TODO Removed duplicate parameters, guards and actions.
						
					} else {
						// Find the latest non-extended abstract event.
						IEvent abstractEvent = EventBUtils
								.getNonExtendedAbstractEvent(event);
						// Copy the body of the abstract event, this includes
						// PARAMETERS, GUARDS, WITNESS, ACTIONS.
						if (abstractEvent != null) {
							final IParameter[] parameters = abstractEvent
									.getParameters();
							for (IParameter parameter : parameters) {
								parameter.copy(event, null, null, false, pMonitor);
							}
							final IGuard[] guards = abstractEvent.getGuards();
							for (IGuard guard : guards) {
								guard.copy(event, null, null, false, pMonitor);
							}
							final IWitness[] witnesses = abstractEvent.getWitnesses();
							for (IWitness witness : witnesses) {
								witness.copy(event, null, null, false, pMonitor);
							}
							final IAction[] actions = abstractEvent.getActions();
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
		element.removeAttribute(EventBAttributes.EXTENDED_ATTRIBUTE, monitor);
	}

	/**
	 * Default value for extended attribute is <code>false</code>, i.e.
	 * non-extended.
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
		event.setExtended(false, monitor);
	}

}
