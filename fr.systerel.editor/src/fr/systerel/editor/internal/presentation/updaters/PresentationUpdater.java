/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.rodinp.core.emf.api.itf.ILAttribute;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * This updater manage the notifications for the ILElements.
 * Implicit elements are treated separately by {@link ImplicitPresentationUpdater}.
 * 
 * @author "Thomas Muller"
 */
public class PresentationUpdater extends EContentAdapter {

	private final DocumentMapper mapper;
	private final RodinEditor editor;

	public PresentationUpdater(RodinEditor editor, DocumentMapper mapper) {
		this.editor = editor;
		this.mapper = mapper;
	}

	@Override
	public void notifyChanged(Notification notification) {
		if (notification.getNewValue() instanceof ILElement
				&& ((ILElement) notification.getNewValue()).isImplicit()) {
			return; // we don't care about implicit elements
		}
		if (notification.getNotifier() instanceof ILElement
				&& ((ILElement) notification.getNotifier()).isImplicit()) {
			return; // we don't care about implicit elements
		}
		/*
		 * Cases where the editor shall be completely resynchronized.
		 */
		if ((notification.getEventType() == Notification.REMOVE && notification
				.getOldValue() instanceof ILElement)
				|| (notification.getEventType() == Notification.MOVE)) {
			editor.resync(null, false);
			return;
		}
		if (notification.getEventType() == Notification.ADD
				&& notification.getNewValue() instanceof ILElement) {
			editor.resync(null, false, (ILElement) notification.getNewValue());
			return;
		}
		/*
		 * Other cases where the editor can be finely synchronized: attribute
		 * update. THIS IS A SECURITY TO MAINTAIN CONSISTENCY, IF OTHER THINGS
		 * THAT THE EDITOR MODIFY THE RODIN DB.
		 */
		final Object oldObject = notification.getOldValue();
		final Object notifier = notification.getNotifier();
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
