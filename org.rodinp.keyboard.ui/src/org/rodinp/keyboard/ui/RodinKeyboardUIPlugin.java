/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added enableRodinModifyListener() method
 *     Systerel - moved the keyboard view modify listenere here
 *******************************************************************************/
package org.rodinp.keyboard.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rodinp.internal.keyboard.ui.RodinModifyListener;
import org.rodinp.internal.keyboard.ui.views.KeyboardView;
import org.rodinp.keyboard.core.RodinKeyboardCore;

/**
 * The activator class controls the plug-in life cycle
 */
public class RodinKeyboardUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rodinp.keyboard.ui";

	public static final String RODIN_KEYBOARD_VIEWER_ID = PLUGIN_ID + ".views.KeyboardView";

	//Debug properties	
	private static final String TRACE = PLUGIN_ID + "/debug";

	private static final String MATH_TRACE = PLUGIN_ID + "/debug/text";
	
	private static final String TEXT_TRACE = PLUGIN_ID + "/debug/math";

	// The shared instance
	private static RodinKeyboardUIPlugin plugin;
	
	private final static RodinModifyListener listener = new RodinModifyListener();

	public static boolean DEBUG = false;

	public static boolean TEXT_DEBUG = false;

	public static boolean MATH_DEBUG = false; 
	
	
	/**
	 * The constructor
	 */
	public RodinKeyboardUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if (isDebugging())
			configureDebugOptions();
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		DEBUG = parseOption(TRACE);
		TEXT_DEBUG = parseOption(TEXT_TRACE);
		MATH_DEBUG = parseOption(MATH_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RodinKeyboardUIPlugin getDefault() {
		return plugin;
	}

	public Text getRodinKeyboardViewWidget() {
		KeyboardView view = getKeyboardView();
		if (view != null)
			return view.getWidget();
		return null;
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
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public ModifyListener getRodinModifyListener() {
		return listener;
	}

	/**
	 * Method to enable or disable listening by the keyboard modify listener.
	 * 
	 * @param enable
	 *            <code>true</code> to enable modifications listening,
	 *            <code>false</code> to deactivate listening
	 * @since 1.1
	 */
	public void enableRodinModifyListener(boolean enable) {
		((RodinModifyListener) getRodinModifyListener()).setEnable(enable);
	}
	
	private KeyboardView getKeyboardView() {
		IWorkbenchPage page = RodinKeyboardUIPlugin.getActivePage();

		KeyboardView view = (KeyboardView) page
				.findView(RODIN_KEYBOARD_VIEWER_ID);

		if (view == null)
			try {
				view = (KeyboardView) page
						.showView(RODIN_KEYBOARD_VIEWER_ID);
			} catch (PartInitException e) {
				e.printStackTrace();
				return null;
			}
		return view;
	}
	
	public ModifyListener createRodinModifyListener() {
		return new RodinModifyListener();
	}

	public String translate(String text) {
		return RodinKeyboardCore.translate(text);
	}

}
