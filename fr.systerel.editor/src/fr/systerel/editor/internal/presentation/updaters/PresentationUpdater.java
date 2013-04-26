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

	private final DocumentMapper mapper;
	private final RodinEditor editor;

	private final BlockingQueue<Notification> notifications = new LinkedBlockingQueue<Notification>();
	
	private final Runnable processNotifications = new Runnable() {
		
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
			 * attribute update. THIS IS A SECURITY TO MAINTAIN CONSISTENCY,
			 * IF OTHER THINGS THAN THE EDITOR MODIFY THE RODIN DB.
			 */
			for (Notification notification : currentNotifications) {
				final Object oldObject = notification.getOldValue();
				final Object notifier = notification.getNotifier();
				final boolean wasILElement = !(oldObject instanceof ILElement);
				if (notifier instanceof ILElement && wasILElement) {
					mapper.elementChanged((ILElement) notifier);
				}
				if (notifier instanceof ILAttribute) {
					mapper.elementChanged(((ILAttribute) notifier)
							.getOwner());
				}
				if (oldObject instanceof ILElement) {
					mapper.elementChanged((ILElement) oldObject);
				}
				if (oldObject instanceof ILAttribute) {
					mapper.elementChanged(((ILAttribute) oldObject)
							.getOwner());
				}
			}
		}
	};

	public PresentationUpdater(RodinEditor editor, DocumentMapper mapper) {
		this.editor = editor;
		this.mapper = mapper;
	}
	
	private static boolean isAboutImplicitElement(Notification notification) {
		if (notification.getNewValue() instanceof ILElement
				&& ((ILElement) notification.getNewValue()).isImplicit()) {
			return true;
		}
		if (notification.getNotifier() instanceof ILElement
				&& ((ILElement) notification.getNotifier()).isImplicit()) {
			return true;
		}
		return false;
	}
	
	private boolean performFullResyncIfNeeded(Notification notification) {
		if ((notification.getEventType() == Notification.REMOVE && notification
				.getOldValue() instanceof ILElement)
				|| (notification.getEventType() == Notification.MOVE)) {
			editor.resync(null, false);
			return true;
		}
		if (notification.getEventType() == Notification.ADD
				&& notification.getNewValue() instanceof ILElement) {
			editor.resync(null, false, (ILElement) notification.getNewValue());
			return true;
		}
		return false;
	}

	@Override
	public void notifyChanged(Notification notification) {
		
		if (isAboutImplicitElement(notification)) {
			return; // we don't care about implicit elements
		}

		notifications.add(notification);
		
		Display.getDefault().asyncExec(processNotifications);
	}

}
