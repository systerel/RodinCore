/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBPreferenceStore
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;
import static org.eventb.internal.ui.preferences.EventBPreferenceStore.getPreferenceStore;
import static org.eventb.ui.IEventBSharedImages.IMG_DISABLE_POST_TACTIC_PATH;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eventb.internal.ui.EventBImage;

/**
 * Action for enabling / disabling the post-tactic from the proof control view.
 */
public class EnablePostTacticAction extends Action implements
		IPropertyChangeListener {

	private static final IPreferenceStore store = getPreferenceStore();
	private static final ImageDescriptor IMAGE_DESCR = EventBImage
			.getImageDescriptor(IMG_DISABLE_POST_TACTIC_PATH);

	public EnablePostTacticAction() {
		super("Enable post-tactic", IAction.AS_CHECK_BOX);
		this.setToolTipText("Enable post-tactic");
		this.setImageDescriptor(IMAGE_DESCR);
		store.addPropertyChangeListener(this);
		update();
	}

	public void dispose() {
		store.removePropertyChangeListener(this);
	}

	private void update() {
		boolean b = store.getBoolean(P_POSTTACTIC_ENABLE);
		this.setChecked(b);
	}

	@Override
	public void run() {
		final boolean checked = this.isChecked();
		store.setValue(P_POSTTACTIC_ENABLE, checked);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (P_POSTTACTIC_ENABLE.equals(event.getProperty())) {
			update();
		}
	}

}