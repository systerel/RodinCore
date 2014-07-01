/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
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
public class PresentationUpdater extends EContentAdapter implements
		IPropertyChangeListener {

	private static class NotificationProcessor implements Runnable {

		private final BlockingQueue<Notification> notifications;
		private final PresentationUpdater updater;

		public NotificationProcessor(PresentationUpdater updater) {
			this.updater = updater;
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
				final EditorResynchronizer snzr = getSynchronizer(notification);
				if (snzr != null) {
					snzr.resynchronize();
					return;
				}
			}

		}

		private EditorResynchronizer getSynchronizer(Notification notification) {
			final int eventType = notification.getEventType();
			final RodinEditor editor = updater.getEditor();
			switch (eventType) {
			case Notification.ADD:
				if (notification.getNewValue() instanceof ILElement) {
					return new EditorResynchronizer(editor, null,
							(ILElement) notification.getNewValue());
				}
			case Notification.MOVE:
			case Notification.REMOVE:
			case Notification.REMOVE_MANY:
				return new EditorResynchronizer(editor, null);
			}
			return null;
		}

	}

	private final RodinEditor editor;
	private final NotificationProcessor processor;

	public PresentationUpdater(RodinEditor editor, DocumentMapper mapper) {
		this.editor = editor;
		this.processor = new NotificationProcessor(this);
	}

	public RodinEditor getEditor() {
		return editor;
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
	
	/**
	 * Asynchronously refreshes the editor and both repositions the caret at its
	 * place if such place is still legal, and restore selection if selection is
	 * still valid.
	 */
	public void resync(final IProgressMonitor monitor) {
		new EditorResynchronizer(editor, monitor).resynchronize();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			final Font font = JFaceResources.getFont(RODIN_MATH_FONT);
			final StyledText styledText = editor.getStyledText();
			if (styledText == null || styledText.isDisposed()) {
				return;
			}
			styledText.setFont(font);
			resync(null);
		}
	}

}
