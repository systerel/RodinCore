/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added dropdown list, buttons and search field
 *     Systerel - removed direct access to 'consider hidden hyps' preference
 *******************************************************************************/
package org.eventb.internal.ui.searchhypothesis;

import static org.eclipse.jface.action.IAction.AS_CHECK_BOX;
import static org.eclipse.jface.action.IAction.AS_PUSH_BUTTON;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;
import static org.eventb.internal.ui.preferences.PreferenceConstants.PROVING_UI_PAGE_ID;
import static org.eventb.internal.ui.utils.Messages.searchedHypothesis_toolItem_preferences;
import static org.eventb.internal.ui.utils.Messages.searchedHypothesis_toolItem_refresh_toolTipText;
import static org.eventb.internal.ui.utils.Messages.searchedHypothesis_toolItem_search_toolTipText;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.HypothesisPage;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;

/**
 * This class extends the default implementation {@link HypothesisPage} to
 * implement a Search Hypothesis 'page'. This implementation uses a
 * {@link SearchHypothesisComposite} for displaying the searched hypotheses. It
 * also adds a search box for entering the search pattern, a refresh button and
 * a view menu contribution for related preferences.
 * 
 * @author htson
 * @author Thomas Muller
 */
public class SearchHypothesisPage extends HypothesisPage implements
		ISearchHypothesisPage, IPreferenceChangeListener {

	static final IUserSupportManager USM = EventBPlugin.getUserSupportManager();

	private static class PrefAction extends Action {
		
		public PrefAction() {
			super(searchedHypothesis_toolItem_preferences, AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			final String pageId = PROVING_UI_PAGE_ID;
			final String[] displayedIds = new String[] { PROVING_UI_PAGE_ID };
			final Dialog dialog = PreferencesUtil.createPreferenceDialogOn(
					null, pageId, displayedIds, null);
			dialog.open();
		}
	}

	private final IEclipsePreferences store;

	private Action considerHidden;

	SearchBox searchBox;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this searched hypothesis
	 *            page. This must not be <code>null</code>.
	 * @param proverUI
	 *            the main prover editor ({@link ProverUI}). This must not be
	 *            <code>null</null>.
	 */
	public SearchHypothesisPage(IUserSupport userSupport, ProverUI proverUI) {
		super(userSupport, proverUI);
		USM.addChangeListener(this);
		store = InstanceScope.INSTANCE.getNode(EventBPlugin.PLUGIN_ID);
		store.addPreferenceChangeListener(this);
	}

	@Override
	public HypothesisComposite getHypypothesisCompsite() {
		return new SearchHypothesisComposite(userSupport, proverUI);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		considerHidden = new Action("Consider hidden hypotheses in search", AS_CHECK_BOX) {
			@Override
			public void run() {
				USM.setConsiderHiddenHypotheses(this.isChecked());
			}
		};
		considerHidden.setChecked(USM.isConsiderHiddenHypotheses());
		manager.add(considerHidden);

		final Action openPreferences = new PrefAction();
		manager.add(openPreferences);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		searchBox = new SearchBox(this);
		manager.add(searchBox);
		final Action refresh = new Action(null, AS_PUSH_BUTTON) {
			@Override
			public void run() {
				updatePage();
			}
		};
		refresh.setToolTipText(searchedHypothesis_toolItem_refresh_toolTipText);
		refresh.setImageDescriptor(EventBUIPlugin.getDefault()
				.getImageRegistry().getDescriptor(
						IEventBSharedImages.IMG_INVERSE));
		manager.add(refresh);

		final SearchBox sBox = searchBox;
		final IUserSupport us = userSupport;
		final Action search = new Action(null, AS_PUSH_BUTTON) {
			@Override
			public void run() {
				if (us == null) {
					return;
				}
				sBox.search();
			}
		};
		search.setToolTipText(searchedHypothesis_toolItem_search_toolTipText);
		search.setImageDescriptor(EventBUIPlugin.getDefault()
				.getImageRegistry().getDescriptor(
						IEventBSharedImages.IMG_SH_PROVER));
		manager.add(search);
	}

	/**
	 * Set the current searched hypothesis string
	 */
	@Override
	public void setPattern(String input) {
		searchBox.setSearchedText(input);
	}

	/**
	 * Returns the currently searched hypothesis.
	 */
	public String getPattern() {
		return searchBox.getSearchedText();
	}

	/**
	 * Searches hypotheses with the current string to refresh the view.
	 */
	public void updatePage() {
		if (userSupport != null && userSupport.getCurrentPO() != null) {
			userSupport.searchHyps(getPattern());
		}
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (event.getKey().equals(P_CONSIDER_HIDDEN_HYPOTHESES)) {
			if (considerHidden != null) {
				considerHidden.setChecked(USM.isConsiderHiddenHypotheses());
			}
			updatePage();
		}
	}

}
