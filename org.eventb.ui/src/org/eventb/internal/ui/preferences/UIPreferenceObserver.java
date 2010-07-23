/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.preferences.PreferenceConstants.P_AUTOTACTICS;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_POSTTACTICS;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_POSTTACTIC_ENABLE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;

/**
 * This class is responsible for reflecting the UI preference store towards
 * preference instances (such as {@link IAutoTacticPreference}).
 * <p>
 * It listens to preference store changes and automatically updates
 * corresponding preferences. Thus, preference setters should not directly
 * access preference instances, but rather set preferences in the preference
 * store.
 * </p>
 * <p>
 * Observed preferences are:
 * <li>P_POSTTACTICS</li>
 * <li>P_POSTTACTIC_ENABLE</li>
 * <li>P_AUTOTACTICS</li>
 * <li>P_AUTOTACTIC_ENABLE</li>
 * <li>P_CONSIDER_HIDDEN_HYPOTHESES</li>
 * </p>
 * <p>
 * This class is intended to be used in the following way during preference
 * initialisation:
 * 
 * <pre>
 * final IPreferenceStore store = getPreferenceStore();
 * final UIPreferenceObserver prefObs = new UIPreferenceObserver(store);
 * prefObs.initPreferences();
 * store.addPropertyChangeListener(prefObs);
 * </pre>
 * 
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class UIPreferenceObserver implements IPropertyChangeListener {

	static void logBadPropertyType(Object actualValue, String property,
			Class<?> expectedClass) {
		UIUtils.log(null, "Autotactic property: unexpected value for property "
				+ property + ": expected a " + expectedClass.getName()
				+ " but was a " + actualValue.getClass().getName());
	}

	private static abstract class TacticPrefSetter {
		protected final IPreferenceStore store;
		protected final String property;

		public TacticPrefSetter(IPreferenceStore store,
				String property) {
			this.store = store;
			this.property = property;
		}

		public void initPreference() {
			final Object storeValue = getStoreValue();
			checkAndUpdate(storeValue);
		}
		
		public abstract void checkAndUpdate(Object newValue);

		protected abstract Object getStoreValue();
	}
	
	private static abstract class BooleanSetter extends TacticPrefSetter {

		public BooleanSetter(IPreferenceStore store,
				String property) {
			super(store, property);
		}

		@Override
		public void checkAndUpdate(Object newValue) {
			if (!(newValue instanceof Boolean)) {
				logBadPropertyType(newValue, property, Boolean.class);
				return;
			}
			doUpdate((Boolean) newValue);
		}

		@Override
		protected Object getStoreValue() {
			return store.getBoolean(property);
		}
		
		protected abstract void doUpdate(Boolean newValue);
	}
	
	private static class TacticBooleanSetter extends BooleanSetter {
		
		private final IAutoTacticPreference tacticPref;
		
		public TacticBooleanSetter(IPreferenceStore store,
				IAutoTacticPreference tacticPref, String property) {
			super(store, property);
			this.tacticPref = tacticPref;
		}

		@Override
		protected void doUpdate(Boolean newValue) {
			tacticPref.setEnabled(newValue.booleanValue());
		}
	}
	
	private static class TacticStringListSetter extends TacticPrefSetter {

		private final IAutoTacticPreference tacticPref;

		public TacticStringListSetter(IPreferenceStore store,
				IAutoTacticPreference tacticPref, String property) {
			super(store, property);
			this.tacticPref = tacticPref;
		}

		@Override
		public void checkAndUpdate(Object newValue) {
			if (!(newValue instanceof String)) {
				logBadPropertyType(newValue, property, String.class);
				return;
			}
			final String[] tacticIDs = UIUtils.parseString((String) newValue);
			tacticPref.setSelectedDescriptors(ProverUIUtils
					.stringsToTacticDescriptors(tacticPref, tacticIDs));
		}

		@Override
		protected Object getStoreValue() {
			return store.getString(property);
		}
	}
	
	private final Map<String, TacticPrefSetter> prefSetters = new HashMap<String, TacticPrefSetter>();
	
	public UIPreferenceObserver(IPreferenceStore store) {
		final IAutoTacticPreference postTacticPref = EventBPlugin
				.getPostTacticPreference();
		final IAutoTacticPreference autoTacticPref = EventBPlugin
				.getAutoTacticPreference();

		prefSetters.put(P_POSTTACTICS,
				new TacticStringListSetter(store, postTacticPref, P_POSTTACTICS));
		prefSetters.put(P_POSTTACTIC_ENABLE,
				new TacticBooleanSetter(store, postTacticPref, P_POSTTACTIC_ENABLE));
		prefSetters.put(P_AUTOTACTICS,
				new TacticStringListSetter(store, autoTacticPref, P_AUTOTACTICS));
		prefSetters.put(P_AUTOTACTIC_ENABLE,
				new TacticBooleanSetter(store, autoTacticPref, P_AUTOTACTIC_ENABLE));
		prefSetters.put(P_CONSIDER_HIDDEN_HYPOTHESES,
				new BooleanSetter(store, P_CONSIDER_HIDDEN_HYPOTHESES) {
					@Override
					protected void doUpdate(Boolean newValue) {
						EventBPlugin.getUserSupportManager()
								.setConsiderHiddenHypotheses(newValue.booleanValue());
					}
				});
	}

	/**
	 * Initialises prover preferences by reading the preference store.
	 */
	public void initPreferences() {
		for (TacticPrefSetter prefSetter : prefSetters.values()) {
			prefSetter.initPreference();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		final Object newValue = event.getNewValue();
		final String property = event.getProperty();
		final TacticPrefSetter prefSetter = prefSetters.get(property);
		if (prefSetter != null) {
			prefSetter.checkAndUpdate(newValue);
		}
	}
}