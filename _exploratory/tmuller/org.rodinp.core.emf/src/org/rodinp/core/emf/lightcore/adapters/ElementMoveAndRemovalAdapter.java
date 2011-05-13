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

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
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
		if (notificationType == Notification.ADD) {
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

			final LightElement lightTarget = (LightElement) notifier;
			final IInternalElement target = lightTarget
					.getElement();
			if (rElement.exists() || target.equals(parent)) { // simple addition
				return;
			}
			// actually, this is a move of an existing element
			final int pos = notification.getPosition();
			final IInternalElement nextSibling;
			try {
				if (pos == Notification.NO_INDEX || pos == 0) {
					nextSibling = null;
				} else {
					nextSibling = (IInternalElement) target.getChildren()[pos];
				}
				final IInternalElement iElement = (IInternalElement) rElement;
				
				System.err.println(iElement + " exists ? -" + iElement.exists());
				System.err.println("unable to add " + iElement);

				final IRodinElement targetParent = target.getParent();
				if (!(targetParent instanceof IInternalElement)) {
					return;
				}
				// FIXME how to add this element that does not exist in RodinDB ?
				// solution #1:
				// fails if renaming is required AND in any case because target does not exist
//				iElement.move(target, nextSibling,null,false,null);
				// solution #2:
				// copying from internal element fails because it does not exist
				// solution #3:
				// copying from light element works BUT
				// the following generates a notification that eventually makes the copy twice
//				copy(e, (IInternalElement) targetParent, nextSibling);
				// => ?!!??!!!
			} catch (RodinDBException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void copy(ILElement source, IInternalElement targetParent,
			IInternalElement nextSibling) throws RodinDBException {
		final IInternalElement target = targetParent.createChild(
				source.getElementType(), nextSibling, null);
		final List<IAttributeValue> attributes = source.getAttributes();
		for (IAttributeValue value : attributes) {
			target.setAttributeValue(value, null);
		}

		final List<? extends ILElement> children = source.getChildren();
		for (ILElement child : children) {
			copy(child, target, null);
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
