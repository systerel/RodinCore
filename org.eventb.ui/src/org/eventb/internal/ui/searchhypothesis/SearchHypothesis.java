/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * 
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.prover.ProverContentOutline;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;

/**
 * @author htson
 *         <p>
 *         Implementation of the Search Hypothesis View.
 */
public class SearchHypothesis extends ProverContentOutline implements
		IPropertyChangeListener {

	private static final class SearchBox extends ToolBarContributionItem {

		protected Text text;

		protected SearchHypothesis searchHypothesis;

		public SearchBox(SearchHypothesis searchHyp) {
			super();
			searchHypothesis = searchHyp;
		}

		protected Composite createComposite(ToolBar parent) {
			final Composite composite = new Composite(parent, SWT.FLAT);
			composite.setLayout(new FillLayout());
			text = new Text(composite, SWT.SINGLE | SWT.BORDER);
			final EventBMath math = new EventBMath(text);
			final SelectionListener listener = new SelectionAdapter() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					math.translate();
					search();
				}
				
			};
			text.addSelectionListener(listener);
			return composite;
		}

		@Override
		public void fill(ToolBar toolbar, int index) {
			final Composite composite = createComposite(toolbar);
			final ToolItem ti = new ToolItem(toolbar, SWT.SEPARATOR, index);
			ti.setControl(composite);
			ti.setWidth(200);
		}

		public void setFocus() {
			text.setFocus();
		}

		public void search() {
			searchHypothesis.setSearchedHyp(text.getText());
			searchHypothesis.updateView();
			setFocus();
		}

	}

	private final IPreferenceStore store;

	private Action considerHidden;

	private Action openPreferences;

	private Action search;

	private Action refresh;

	private String searchedHyp = "";

	private SearchBox searchBox;

	/**
	 * The identifier of the Search Hypothesis View (value
	 * <code>"org.eventb.ui.views.SearchHypothesis"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.SearchHypothesis"; // $NON-NLS-1$

	/**
	 * Constructor.
	 * <p>
	 * Create a prover content outline with the default message.
	 */
	public SearchHypothesis() {
		super(Messages.searchedHypothesis_defaultMessage);
		store = EventBPreferenceStore.getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a Search Hypothesis Page.
		final Object obj = part.getAdapter(ISearchHypothesisPage.class);
		if (obj instanceof ISearchHypothesisPage) {
			final ISearchHypothesisPage page = (ISearchHypothesisPage) obj;
			initPage(page);
			page.createControl(getPageBook());
			final IActionBars actionBars = page.getSite().getActionBars();
			addTextSearch(actionBars.getToolBarManager());
			makeButtons();
			makeDropDownItems();
			fillDropDownList(actionBars.getMenuManager());
			fillMenu(actionBars.getToolBarManager());
			store.addPropertyChangeListener(this);
			return new PageRec(part, page);
		}
		// There is no Search Hypotheses Page
		return null;
	}

	private void addTextSearch(IToolBarManager toolBarManager) {
		searchBox = new SearchBox(this);
		toolBarManager.add(searchBox);
	}

	private void fillMenu(IToolBarManager toolBarManager) {
		toolBarManager.add(search);
		toolBarManager.add(refresh);
	}

	private void fillDropDownList(IMenuManager manager) {
		manager.add(considerHidden);
		manager.add(openPreferences);
	}

	/**
	 * Set the current searched hypothesis string
	 */
	public void setSearchedHyp(String input) {
		searchedHyp = input;
	}

	/**
	 * Returns the currently searched hypothesis.
	 */
	public String getSearchedHyp() {
		return searchedHyp;
	}

	/**
	 * Create the Refresh button to place in the view menu.
	 */
	private void makeButtons() {
		refresh = new Action(null, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				updateView();
			}
		};
		refresh
				.setToolTipText(Messages.searchedHypothesis_toolItem_refresh_toolTipText);
		refresh.setImageDescriptor(EventBUIPlugin.getDefault()
				.getImageRegistry().getDescriptor(
						IEventBSharedImages.IMG_INVERSE));

		final SearchBox sBox = searchBox;
		search = new Action(null, Action.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				if (getActiveProverUI() == null) {
					return;
				}
				sBox.search();
			}
		};
		search
				.setToolTipText(Messages.searchedHypothesis_toolItem_search_toolTipText);
		search.setImageDescriptor(EventBUIPlugin.getDefault()
				.getImageRegistry().getDescriptor(
						IEventBSharedImages.IMG_SH_PROVER));
	}

	/**
	 * Create the actions to place in the dropdown list.
	 */
	private void makeDropDownItems() {
		considerHidden = new Action(
				PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES,
				IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				final boolean checked = this.isChecked();
				setPreferences(checked);
			}
		};
		final boolean b = EventBPreferenceStore
				.getBooleanPreference(PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES);
		considerHidden.setChecked(b);
		setPreferences(b);

		openPreferences = new Action(
				Messages.searchedHypothesis_toolItem_preferences,
				IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				final String pageId = PreferenceConstants.PROVING_UI_PAGE_ID;
				final String[] displayedIds = new String[] { PreferenceConstants.PROVING_UI_PAGE_ID };
				final Dialog dialog = PreferencesUtil.createPreferenceDialogOn(
						null, pageId, displayedIds, null);
				dialog.open();
			}
		};
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(
				PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES)) {
			final Object newValue = event.getNewValue();
			assert newValue instanceof Boolean;
			final Boolean b = (Boolean) newValue;
			considerHidden.setChecked(b);
			setPreferences(b);
			updateView();
		}
	}

	/**
	 * Searches hypotheses with the current string to refresh the view.
	 */
	public void updateView() {
		final IUserSupport userSupport = getCurrentUserSupport();
		if (userSupport != null && userSupport.getCurrentPO() != null) {
			userSupport.searchHyps(searchedHyp);
		}
	}

	/**
	 * Sets the preferences in store and support manager.
	 */
	public void setPreferences(boolean b) {
		store.setValue(PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES, b);
	}
	
	protected static ProverUI getActiveProverUI() {
		IWorkbenchPage acPage = EventBUIPlugin.getActivePage();
		if (acPage == null) {
			return null;
		}
		IEditorPart editor = acPage.getActiveEditor();
		if (!(editor instanceof ProverUI)) {
			return null;
		}
		return ((ProverUI) editor);
	}

	protected static IUserSupport getCurrentUserSupport() {
		final ProverUI proverUI = getActiveProverUI();
		if (proverUI == null) {
			return null;
		}
		return getActiveProverUI().getUserSupport();
	}
	
}