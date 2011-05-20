/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.presentation.updaters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.rodinp.core.emf.api.itf.ILAttribute;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.editors.RodinEditor;

public class PresentationUpdater extends EContentAdapter {

	private final DocumentMapper mapper;
	private final RodinEditor editor;
	
	private Notification backupNotification;

	public PresentationUpdater(RodinEditor editor, DocumentMapper mapper) {
		this.editor = editor;
		this.mapper = mapper;
	}

	@Override
	public void notifyChanged(Notification notification) {
		final Object oldObject = notification.getOldValue();
		final Object notifier = notification.getNotifier();
		final Object newObject = notification.getNewValue();
		
		// Don't process the same notification again.
		if (backupNotification != null
				&& backupNotification.getNewValue() != null
				&& backupNotification.getNewValue().equals(
						notification.getNewValue())) {
			return;
		}
		backupNotification = notification;
		if (notification.getEventType() == Notification.ADD && newObject instanceof ILElement) {
			editor.resync(null);
			return;
		}
		if (notification.isTouch() && !(notifier instanceof ILAttribute)) {
			return;
		}
		final boolean isILElement = !(oldObject instanceof ILElement);
		if (notifier instanceof ILElement && isILElement) {
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
