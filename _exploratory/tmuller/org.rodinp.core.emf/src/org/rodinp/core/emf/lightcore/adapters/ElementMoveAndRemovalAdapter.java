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
			try {
				final IRodinElement found = findValueInDB(rElement.getParent(),
						notification.getPosition());
				if (found != null)
					((IInternalElement) rElement).move(rElement.getParent(),
							found, null, false, null);
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

	final IRodinElement findValueInDB(IRodinElement container, int position)
			throws RodinDBException {
		if (!(container instanceof IInternalElement)) {
			return null;
		}
		final IRodinElement[] children = ((IInternalElement) container)
				.getChildren();
		if (position >= 0 && position < children.length) {
			return children[position];
		}
		return null;
	}

}
