package org.eventb.internal.pp.ui;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class PPUIPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eventb.pp.ui"; //$NON-NLS-1$
	
	/**
	 * debugging/tracing option names
	 */
	private static final String DEBUG = PLUGIN_ID + "/debug"; //$NON-NLS-1$

	
	public PPUIPlugin() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		configureDebugOptions();
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		if (isDebugging()) {
			String option;
			option = Platform.getDebugOption(DEBUG);
			if (option != null) {
				PPReasoner.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			}
		}
	}

}
