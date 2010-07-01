/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added dropdown list, buttons and search field
 *     Systerel - removed direct access to 'consider hidden hyps' preference
 ******************************************************************************/
package org.eventb.internal.ui.searchhypothesis;

import static org.eclipse.jface.action.IAction.AS_CHECK_BOX;
import static org.eclipse.jface.action.IAction.AS_PUSH_BUTTON;
import static org.eventb.internal.ui.preferences.PreferenceConstants.PROVING_UI_PAGE_ID;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;
import static org.eventb.internal.ui.utils.Messages.searchedHypothesis_toolItem_preferences;
import static org.eventb.internal.ui.utils.Messages.searchedHypothesis_toolItem_refresh_toolTipText;
import static org.eventb.internal.ui.utils.Messages.searchedHypothesis_toolItem_search_toolTipText;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
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
		ISearchHypothesisPage, IPropertyChangeListener {

	private final IPreferenceStore store;

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
		store = EventBPreferenceStore.getPreferenceStore();
		store.addPropertyChangeListener(this);
	}

	@Override
	public HypothesisComposite getHypypothesisCompsite() {
		return new SearchHypothesisComposite(userSupport, proverUI);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		considerHidden = new Action(P_CONSIDER_HIDDEN_HYPOTHESES, AS_CHECK_BOX) {
			@Override
			public void run() {
				setPreferences(this.isChecked());
			}
		};
		final boolean b = EventBPreferenceStore
				.getBooleanPreference(P_CONSIDER_HIDDEN_HYPOTHESES);
		considerHidden.setChecked(b);
		setPreferences(b);
		manager.add(considerHidden);

		final Action openPreferences = new Action(
				searchedHypothesis_toolItem_preferences, AS_PUSH_BUTTON) {
			@Override
			public void run() {
				final String pageId = PROVING_UI_PAGE_ID;
				final String[] displayedIds = new String[] { PROVING_UI_PAGE_ID };
				final Dialog dialog = PreferencesUtil.createPreferenceDialogOn(
						null, pageId, displayedIds, null);
				dialog.open();
			}
		};
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
		final Action search = new Action(null, AS_PUSH_BUTTON) {
			@Override
			public void run() {
				if (userSupport == null) {
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
	public void setPattern(String input) {
		searchBox.setSearchedText(input);
	}

	/**
	 * Returns the currently searched hypothesis.
	 */
	public String getPattern() {
		return searchBox.getSearchedText();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(P_CONSIDER_HIDDEN_HYPOTHESES)) {
			final Object newValue = event.getNewValue();
			assert newValue instanceof Boolean;
			final Boolean b = (Boolean) newValue;
			if (considerHidden != null)
				considerHidden.setChecked(b);
			setPreferences(b);
			updatePage();
		}
	}

	/**
	 * Searches hypotheses with the current string to refresh the view.
	 */
	public void updatePage() {
		if (userSupport != null && userSupport.getCurrentPO() != null) {
			userSupport.searchHyps(getPattern());
		}
	}

	/**
	 * Sets the preferences in store and support manager.
	 */
	public void setPreferences(boolean b) {
		store.setValue(P_CONSIDER_HIDDEN_HYPOTHESES, b);
	}

}
