package org.eventb.internal.core.pom;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBPlugin;

public class Util {

	private Util() {
		// this class should not be instantiated
	}

	
	protected void logMessage(Exception e, String message) {
		Plugin plugin = EventBPlugin.getDefault();
		IStatus status = new Status(IMarker.SEVERITY_ERROR, EventBPlugin.PLUGIN_ID, Platform.PLUGIN_ERROR, message, e);
		plugin.getLog().log(status);
	}
}
