package org.eventb.internal.ui.proofskeleton;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eventb.internal.ui.proofskeleton.messages"; //$NON-NLS-1$
	public static String PrfSklLabelProvider_noproof;
	public static String PrfSklLabelProvider_pendingnode;
	public static String SequentDetailsPage_turnstile;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
