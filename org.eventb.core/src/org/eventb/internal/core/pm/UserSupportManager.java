/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import static org.eventb.core.EventBPlugin.PLUGIN_ID;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;

public class UserSupportManager implements IUserSupportManager {

	private static boolean DEFAULT_CONSIDER_HIDDEN_HYPS = DefaultScope.INSTANCE
			.getNode(EventBPlugin.PLUGIN_ID).getBoolean(
					P_CONSIDER_HIDDEN_HYPOTHESES, false);

	private Collection<IUserSupport> userSupports = new ArrayList<IUserSupport>();

	private static final UserSupportManager instance = new UserSupportManager();

	private DeltaProcessor deltaProcessor;

	private UserSupportManager() {
		// Singleton: Private default constructor
		deltaProcessor = new DeltaProcessor(this);
	}

	public static UserSupportManager getDefault() {
		return instance;
	}

	@Override
	public IUserSupport newUserSupport() {
		return new UserSupport();
	}

	@Override
	public Collection<IUserSupport> getUserSupports() {
		return new ArrayList<IUserSupport>(userSupports);
	}

	@Override
	public void addChangeListener(IUserSupportManagerChangedListener listener) {
		deltaProcessor.addChangeListener(listener);
	}

	@Override
	public void removeChangeListener(IUserSupportManagerChangedListener listener) {
		deltaProcessor.removeChangeListener(listener);
	}

	public DeltaProcessor getDeltaProcessor() {
		return deltaProcessor;
	}

	public void addUserSupport(UserSupport userSupport) {
		synchronized (userSupports) {
			if (!userSupports.contains(userSupport))
				userSupports.add(userSupport);
		}
	}

	public void removeUserSupport(IUserSupport userSupport) {
		synchronized (userSupports) {
			if (userSupports.contains(userSupport))
				userSupports.remove(userSupport);
		}
	}

	@Override
	public void run(Runnable op) {
		boolean wasEnable = deltaProcessor.isEnable();
		try {
			if (wasEnable)
				deltaProcessor.setEnable(false);
			op.run();
		} finally {
			if (wasEnable)
				deltaProcessor.setEnable(true);
		}
		deltaProcessor.fireDeltas();
	}

	private static IEclipsePreferences getPrefNode() {
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}

	@Override
	public void setConsiderHiddenHypotheses(boolean value) {
		getPrefNode().putBoolean(P_CONSIDER_HIDDEN_HYPOTHESES, value);
	}

	@Override
	public boolean isConsiderHiddenHypotheses() {
		return getPrefNode().getBoolean(P_CONSIDER_HIDDEN_HYPOTHESES,
				DEFAULT_CONSIDER_HIDDEN_HYPS);
	}
}
