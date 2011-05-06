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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * Class allowing to move or remove an Rodin element from the database as its
 * 'Light' counterpart has been moved or suppressed.
 * 
 * @author Thomas Muller
 */
public class ElementMoveAndRemovalAdapter extends AdapterImpl {

	@Override
	public void notifyChanged(Notification notification) {
		final Object notifier = notification.getNotifier();
		final int notificationType = notification.getEventType();
		if (notification.isTouch()) {
			return;
		}
		if (!(notifier instanceof LightElement)) {
			return;
		}
		if (notificationType == Notification.REMOVE) {
			try {
				final Object o = notification.getOldValue();
				if (o instanceof InternalElement) {
					final InternalElement e = (InternalElement) o;
					final IRodinElement rElement = (IRodinElement) e
							.getERodinElement();
					if (rElement instanceof IInternalElement) {
						((IInternalElement) rElement).delete(true, null);
					}
				}
			} catch (RodinDBException e) {
				System.out.println("Could not delete the Rodin element for:"
						+ notifier.toString());
			}
			return;
		}
		if (notificationType == Notification.MOVE) {
			final Object o = notification.getNewValue();
			if (!(o instanceof LightElement)) {
				return;
			}
			final LightElement e = (LightElement) o;
			final IRodinElement rElement = (IRodinElement) e.getERodinElement();
			if (!(rElement instanceof IInternalElement)) {
				return;
			}
			final IRodinElement parent = rElement.getParent();
			if (!(parent instanceof IInternalElement)) {
				return;
			}
			
			try {
				final IRodinElement[] children = ((IInternalElement) parent)
						.getChildren();

				final int oldPos = indexOf(children, rElement);
				if (oldPos < 0)
					return;
				IInternalElement nextSibling = (IInternalElement) children[notification
						.getPosition()];
				if (oldPos < notification.getPosition()) {
					nextSibling = nextSibling.getNextSibling();
				}
				((IInternalElement) rElement).move(parent, nextSibling, null,
						false, null);
			} catch (RodinDBException e1) {
				System.out.println("Could not move the Rodin element for "
						+ notifier.toString() + e1.getMessage());
			}
		}
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return type == ElementMoveAndRemovalAdapter.class;
	}

	private static int indexOf(IRodinElement[] elements, IRodinElement element) {
		for (int i = 0; i < elements.length; i++) {
			if (element.equals(elements[i]))
				return i;
		}
		return -1;
	}

}
