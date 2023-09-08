/*******************************************************************************
 * Copyright (c) 2005, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored getRodinDatabase()
 *     Systerel - added ColorManager
 *     Systerel - used EventBPreferenceStore
 *     Systerel - installation of EditorManager
 *     Systerel - used a preference observer
 *     University of Southampton - Remove the usage of deprecated methods
 *******************************************************************************/
package org.eventb.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.ColorManager;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.cachehypothesis.CacheHypothesisUtils;
import org.eventb.internal.ui.eventbeditor.EditorManager;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.goal.GoalUtils;
import org.eventb.internal.ui.preferences.ToggleAutoTacticPreference;
import org.eventb.internal.ui.preferences.UIPreferenceObserver;
import org.eventb.internal.ui.proofSkeletonView.ProofSkeletonView;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.internal.ui.proofinformation.ProofInformationUtils;
import org.eventb.internal.ui.prooftreeui.ProofTreeUIUtils;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.handlers.CheckExternalProvers;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesisUtils;
import org.eventb.ui.itemdescription.IElementDescRegistry;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         The main plugin class for Event-B UI.
 *         </p>
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class EventBUIPlugin extends AbstractUIPlugin {

	/**
	 * The identifier of the Event-B UI plug-in (value
	 * <code>"org.eventb.ui"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.ui"; //$NON-NLS-1$

	/**
	 * The identifier of the Proof Tree UI View (value
	 * <code>"org.eventb.ui.views.ProofTreeUI"</code>).
	 */
	public static final String PROOF_TREE_VIEW_ID = PLUGIN_ID
			+ ".views.ProofTreeUI";

	/**
	 * The identifier of the Proof Control View (value
	 * <code>"org.eventb.ui.views.ProofControl"</code>).
	 */
	public static final String PROOF_CONTROL_VIEW_ID = PLUGIN_ID + ".views.ProofControl";
	
	/**
	 * The identifier of the Rodin Problem View (value
	 * <code>"org.eventb.ui.views.RodinProblemView"</code>).
	 */
	public static final String RODIN_PROBLEM_VIEW_ID = PLUGIN_ID
			+ ".views.RodinProblemView"; 		
	
	public static final String PROVING_PERSPECTIVE_ID = "org.eventb.ui.perspective.proving";

	public static final String NAVIGATOR_VIEW_ID = "fr.systerel.explorer.navigator.view";

	// Trace Options
	private static final String GLOBAL_TRACE = PLUGIN_ID + "/debug"; //$NON-NLS-1$

	private static final String EVENTBEDITOR_TRACE = PLUGIN_ID
			+ "/debug/eventbeditor"; //$NON-NLS-1$

	private static final String PROVERUI_TRACE = PLUGIN_ID + "/debug/proverui"; //$NON-NLS-1$

	private static final String PROVERUI_PERF = PLUGIN_ID
			+ "/debug/proverui/perf"; //$NON-NLS-1$

	private static final String PROOFCONTROL_TRACE = PLUGIN_ID
			+ "/debug/proofcontrol"; //$NON-NLS-1$

	private static final String PROOFTREEUI_TRACE = PLUGIN_ID
			+ "/debug/prooftreeui"; //$NON-NLS-1$

	private static final String PROOFINFORMATION_TRACE = PLUGIN_ID
			+ "/debug/proofinformation"; //$NON-NLS-1$

	private static final String SEARCHHYPOTHESIS_TRACE = PLUGIN_ID
			+ "/debug/searchhypothesis"; //$NON-NLS-1$

	private static final String CACHEDHYPOTHESIS_TRACE = PLUGIN_ID
			+ "/debug/cachedhypothesis"; //$NON-NLS-1$

	private static final String GOAL_TRACE = PLUGIN_ID
			+ "/debug/goal"; //$NON-NLS-1$

	private static final String PROOFSKELETON_DEBUG = PLUGIN_ID
	+ "/debug/proofskeleton"; //$NON-NLS-1$

	//Helpers for the creation of element default values
	/**
	 * @since 2.0
	 * @deprecated use {@link #getPrd_Default()}
	 */
	@Deprecated
	public static String getPrd_Default(FormulaFactory ff) {
		return ff.makeLiteralPredicate(Formula.BTRUE, null).toString();
	}

	/**
	 * @since 2.0
	 * @deprecated use {@link #getInv_Default()}
	 */
	@Deprecated
	public static String getInv_Default(FormulaFactory ff){
		return getPrd_Default(ff);
	}

	/**
	 * @since 2.0
	 * @deprecated use {@link #getAxm_Default()}
	 */
	@Deprecated
	public static String getAxm_Default(FormulaFactory ff) {
		return getPrd_Default(ff);
	}

	/**
	 * @since 2.0
	 * @deprecated use {@link #getThm_Default()}
	 */
	@Deprecated
	public static String getThm_Default(FormulaFactory ff) {
		return getPrd_Default(ff);
	}

	/**
	 * @since 2.0
	 * @deprecated use {@link #getGrd_Default()}
	 */
	@Deprecated
	public static String getGrd_Default(FormulaFactory ff) {
		return getPrd_Default(ff);
	}

	/**
	 * @since 2.0
	 * @deprecated use {@link #getSub_Default()}
	 */
	@Deprecated
	public static String getSub_Default(FormulaFactory ff) {
		return "";
	}

	/**
	 * @since 3.8
	 */
	public static String getPrd_Default() {
		/*
		 * Since we only want the string representation of a literal predicate, using
		 * the default factory is enough. Formula extensions that could exist in another
		 * factory would not change the result.
		 */
		return FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null).toString();
	}

	/**
	 * @since 3.8
	 */
	public static String getInv_Default(){
		return getPrd_Default();
	}

	/**
	 * @since 3.8
	 */
	public static String getAxm_Default() {
		return getPrd_Default();
	}

	/**
	 * @since 3.8
	 */
	public static String getThm_Default() {
		return getPrd_Default();
	}

	/**
	 * @since 3.8
	 */
	public static String getGrd_Default() {
		return getPrd_Default();
	}

	/**
	 * @since 3.8
	 */
	public static String getSub_Default() {
		return "";
	}

	// The shared instance.
	private static EventBUIPlugin plugin;

	/**
	 * The constructor, also store the database instance of the current
	 * Workspace.
	 */
	public EventBUIPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the registry of UI descriptions of elements and attributes in the
	 * Rodin datatbase.
	 * 
	 * @since 3.0
	 */
	public static IElementDescRegistry getElementDescRegistry() {
		return ElementDescRegistry.getInstance();
	}

	/**
	 * Returns the Rodin database element.
	 * 
	 * @return the Rodin database
	 */
	public static IRodinDB getRodinDatabase() {
		return RodinCore.getRodinDB();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		if (isDebugging())
			configureDebugOptions();
		
		initializePreferences();
		
		ToggleAutoTacticPreference.registerListener();
		
		RodinCore.addElementChangedListener(new EditorManager());

		CheckExternalProvers.checkExternalProversLazily();
	}

	/**
	 * Reads the value of the preferences and initialize various components
	 */
	private void initializePreferences() {
		final IPreferenceStore store = getPreferenceStore();
		final UIPreferenceObserver prefObs = new UIPreferenceObserver();
		store.addPropertyChangeListener(prefObs);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		UIUtils.DEBUG = parseOption(GLOBAL_TRACE);
		EventBEditorUtils.DEBUG = parseOption(EVENTBEDITOR_TRACE);
		ProverUIUtils.DEBUG = parseOption(PROVERUI_TRACE);
		HypothesisComposite.PERF = parseOption(PROVERUI_PERF);
		ProofControlUtils.DEBUG = parseOption(PROOFCONTROL_TRACE);
		ProofTreeUIUtils.DEBUG = parseOption(PROOFTREEUI_TRACE);
		ProofInformationUtils.DEBUG = parseOption(PROOFINFORMATION_TRACE);
		SearchHypothesisUtils.DEBUG = parseOption(SEARCHHYPOTHESIS_TRACE);
		CacheHypothesisUtils.DEBUG = parseOption(CACHEDHYPOTHESIS_TRACE);
		GoalUtils.DEBUG = parseOption(GOAL_TRACE);
		ProofSkeletonView.DEBUG = parseOption(PROOFSKELETON_DEBUG);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			ColorManager.getDefault().dispose();
			plugin = null;
		} finally {
			super.stop(context);
		}		
	}

	/**
	 * Returns the shared instance of this plug-in.
	 * 
	 * @returns the shared instance of this plug-in.
	 */
	public static EventBUIPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		EventBImage.initializeImageRegistry(reg);
		super.initializeImageRegistry(reg);
	}

	/**
	 * Get the active workbench page.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * Return the active workbench window
	 * <p>
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
}
