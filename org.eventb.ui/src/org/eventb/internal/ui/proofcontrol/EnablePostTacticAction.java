/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBPreferenceStore
 *     Systerel - moved preference to EventB Core
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;
import static org.eventb.ui.IEventBSharedImages.IMG_DISABLE_POST_TACTIC_PATH;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.ui.EventBImage;

/**
 * Action for enabling / disabling the post-tactic from the proof control view.
 */
public class EnablePostTacticAction extends Action implements
		IPreferenceChangeListener {

	private static final IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(EventBPlugin.PLUGIN_ID);
	private static final ImageDescriptor IMAGE_DESCR = EventBImage
			.getImageDescriptor(IMG_DISABLE_POST_TACTIC_PATH);

	private static final boolean DEFAULT_ENABLE = DefaultScope.INSTANCE
			.getNode(EventBPlugin.PLUGIN_ID).getBoolean(P_POSTTACTIC_ENABLE,
					false);

	public EnablePostTacticAction() {
		super("Enable post-tactic", IAction.AS_CHECK_BOX);
		this.setToolTipText("Enable post-tactic");
		this.setImageDescriptor(IMAGE_DESCR);
		pref.addPreferenceChangeListener(this);
		update();
	}

	public void dispose() {
		pref.removePreferenceChangeListener(this);
	}

	private void update() {
		final boolean enable = pref.getBoolean(P_POSTTACTIC_ENABLE,
				DEFAULT_ENABLE);
		this.setChecked(enable);
	}

	@Override
	public void run() {
		final boolean checked = this.isChecked();
		pref.putBoolean(P_POSTTACTIC_ENABLE, checked);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (P_POSTTACTIC_ENABLE.equals(event.getKey())) {
			update();
		}
	}

}