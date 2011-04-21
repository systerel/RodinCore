/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters;

import static org.rodinp.core.emf.lightcore.LightCorePlugin.DEBUG;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * Updates the Rodin element attributes when a SET operation occurred on a
 * {@link LightElement} attribute.
 * 
 * @author Thomas Muller
 */
public class AttributeUpdateAdapter extends AdapterImpl {

	private boolean isUpdating = false;

	@Override
	public void notifyChanged(Notification notification) {
		// if the modification does not modify the state of the model
		if (notification.isTouch()) {
			return;
		}
		if (isUpdating) {			
			return;
		}
		final Object notifier = notification.getNotifier();
		final int notificationType = notification.getEventType();

		// If the operation is to set an attribute value
		if (notifier instanceof Attribute
				&& notificationType == Notification.SET) {
			try {
				isUpdating = true;
				final Attribute attribute = (Attribute) notifier;
				final Object rElement = (IRodinElement) attribute.getEOwner()
						.getERodinElement();
				if (rElement instanceof IInternalElement) {
					if (attribute.getType() instanceof IAttributeType) {
						final IInternalElement iElem = (IInternalElement) rElement;
						setValue(iElem, attribute, notification.getNewValue());						
					}
					
					if (DEBUG) {
						System.out.println("AttributeUpdateAdapter: new value"
								+ notification.getNewValue() + " for "
								+ attribute.getKey());
					}
				}
			} catch (RodinDBException e) {
				System.out.println("An error occured when setting"
						+ " the value of the attribute: " + e.getMessage());
			} finally {
				isUpdating = false;
			}
		}
	}

	private static void setValue(IInternalElement element, Attribute att,
			Object value) throws RodinDBException {
		final IAttributeType type = (IAttributeType) att.getType();
		if (type instanceof IAttributeType.Boolean && value instanceof Boolean) {
			element.setAttributeValue((IAttributeType.Boolean) type,
					((Boolean) value).booleanValue(), null);
		}
		if (type instanceof IAttributeType.Handle
				&& value instanceof IRodinElement) {
			element.setAttributeValue((IAttributeType.Handle) type,
					(IRodinElement) value, null);
		}
		if (type instanceof IAttributeType.Integer && value instanceof Integer) {
			element.setAttributeValue((IAttributeType.Integer) type,
					((Integer) value).intValue(), null);
		}
		if (type instanceof IAttributeType.Long && value instanceof Long) {
			element.setAttributeValue((IAttributeType.Long) type,
					((Long) value).longValue(), null);
		}
		if (type instanceof IAttributeType.String && value instanceof String) {
			element.setAttributeValue((IAttributeType.String) type,
					(String) value, null);
		}
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return type == AttributeUpdateAdapter.class;
	}

}
