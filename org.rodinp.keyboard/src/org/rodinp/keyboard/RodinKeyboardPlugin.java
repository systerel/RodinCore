package org.rodinp.keyboard;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rodinp.internal.keyboard.KeyboardUtils;
import org.rodinp.internal.keyboard.RodinModifyListener;
import org.rodinp.internal.keyboard.Text2MathTranslator;
import org.rodinp.internal.keyboard.views.KeyboardView;

/**
 * The activator class controls the plug-in life cycle
 */
public class RodinKeyboardPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.rodinp.keyboard";

	public static final String RODIN_KEYBOARD_VIEWER_ID = PLUGIN_ID + ".views.KeyboardView";

	private static final String TRACE = PLUGIN_ID + "/debug";

	private static final String MATH_TRACE = PLUGIN_ID + "/debug/text";
	
	private static final String TEXT_TRACE = PLUGIN_ID + "/debug/math";

	// The shared instance
	private static RodinKeyboardPlugin plugin;
	
	/**
	 * The constructor
	 */
	public RodinKeyboardPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		configureDebugOptions();
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		if (isDebugging()) {
			String option = Platform.getDebugOption(TRACE);
			if (option != null)
				KeyboardUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(TEXT_TRACE);
			if (option != null)
				KeyboardUtils.TEXT_DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(MATH_TRACE);
			if (option != null)
				KeyboardUtils.MATH_DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

		}
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
	public static RodinKeyboardPlugin getDefault() {
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
		KeyboardView view = getKeyboardView();
		if (view != null)
			return view.getListener();
		return null;
	}

	private KeyboardView getKeyboardView() {
		IWorkbenchPage page = RodinKeyboardPlugin.getActivePage();

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
		return Text2MathTranslator.translate(text);
	}

}
