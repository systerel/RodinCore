/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.swt.widgets.Display;
import org.rodinp.core.emf.api.itf.ILAttribute;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * This updater manages the notifications for the ILElements. Implicit elements
 * are treated separately by {@link ImplicitPresentationUpdater}.
 * <p>
 * For each notification received by {@link #notifyChanged(Notification)}, we
 * push the notification to a queue and trigger an asynchronous processing in
 * the UI thread. Consequently, several notifications can be processed at once,
 * at the cost of having asynchronous processing that does nothing (because the
 * notification has already been processed by a previous one).
 * </p>
 * 
 * @author "Thomas Muller"
 */
public class PresentationUpdater extends EContentAdapter {

	private static class NotificationProcessor implements Runnable {

		private final RodinEditor editor;
		private final DocumentMapper mapper;
		private final BlockingQueue<Notification> notifications;

		public NotificationProcessor(RodinEditor editor, DocumentMapper mapper) {
			this.editor = editor;
			this.mapper = mapper;
			this.notifications = new LinkedBlockingQueue<Notification>();
		}

		public void schedule(Notification notification) {
			notifications.add(notification);
			Display.getDefault().asyncExec(this);
		}

		@Override
		public void run() {
			Collection<Notification> currentNotifications = new ArrayList<Notification>();
			notifications.drainTo(currentNotifications);

			if (currentNotifications.isEmpty()) {
				return;
			}

			/*
			 * Cases where the editor shall be completely resynchronized.
			 */
			for (Notification notification : currentNotifications) {
				if (performFullResyncIfNeeded(notification)) {
					return;
				}
			}

			/*
			 * Other cases where the editor can be finely synchronized:
			 * attribute update. THIS IS A SECURITY TO MAINTAIN CONSISTENCY, IF
			 * OTHER THINGS THAN THE EDITOR MODIFY THE RODIN DB.
			 */
			for (Notification notification : currentNotifications) {
				processNotification(notification);
			}
		}

		private boolean performFullResyncIfNeeded(Notification notification) {
			final int eventType = notification.getEventType();
			if (eventType == Notification.MOVE //
					|| eventType == Notification.REMOVE_MANY //
					|| eventType == Notification.REMOVE) {
				editor.resync(null, false);
				return true;
			}
			if (eventType == Notification.ADD) {
				final Object newValue = notification.getNewValue();
				if (newValue instanceof ILElement) {
					editor.resync(null, false, (ILElement) newValue);
				} else {
					editor.resync(null, false);
				}
				return true;
			}
			return false;
		}

		private void processNotification(Notification notification) {
			final Object oldObject = notification.getOldValue();
			final Object notifier = notification.getNotifier();
			final boolean wasILElement = !(oldObject instanceof ILElement);
			if (notifier instanceof ILElement && wasILElement) {
				mapper.elementChanged((ILElement) notifier);
			}
			if (notifier instanceof ILAttribute) {
				mapper.elementChanged(((ILAttribute) notifier).getOwner());
			}
			if (oldObject instanceof ILElement) {
				mapper.elementChanged((ILElement) oldObject);
			}
			if (oldObject instanceof ILAttribute) {
				mapper.elementChanged(((ILAttribute) oldObject).getOwner());
			}
		}

	}

	private final NotificationProcessor processor;

	public PresentationUpdater(RodinEditor editor, DocumentMapper mapper) {
		this.processor = new NotificationProcessor(editor, mapper);
	}

	@Override
	public void notifyChanged(Notification notification) {
		// Implicit elements are processed separately
		if (!isAboutImplicitElement(notification)) {
			processor.schedule(notification);
		}
	}

	private static boolean isAboutImplicitElement(Notification notification) {
		return isImplicit(notification.getNewValue())
				|| isImplicit(notification.getNotifier());
	}

	private static boolean isImplicit(Object object) {
		if (object instanceof ILElement) {
			return ((ILElement) object).isImplicit();
		}
		return false;
	}

}
