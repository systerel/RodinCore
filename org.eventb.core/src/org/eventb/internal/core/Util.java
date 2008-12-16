/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBPlugin;
import org.rodinp.core.RodinDBException;

/**
 * This class contains miscellaneous static utility methods.
 * 
 * @author Laurent Voisin
 */
public class Util {
	
	private Util() {
		// Non-instanciable class.
	}

	/**
	 * Logs an internal plug-in error to the platform log, with the given
	 * exception and message
	 * 
	 * @param e
	 *            exception to report (may be <code>null</code>)
	 * @param message
	 *            message giving context of the reported error
	 */
	public static void log(Throwable e, String message) {
		IStatus status = new Status(
				IStatus.ERROR, 
				EventBPlugin.PLUGIN_ID, 
				Platform.PLUGIN_ERROR, 
				message, 
				e);
		EventBPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Creates a new Rodin database exception with the given message.
	 * <p>
	 * The created database exception just wraps up a core exception created
	 * with {@link #newCoreException(String)}.
	 * </p>
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 *            
	 *  @see #newCoreException(String)
	 */
	public static RodinDBException newRodinDBException(String message) {
		return new RodinDBException(newCoreException(message));
	}

	/**
	 * Creates a new Rodin database exception with the given message and message arguments.
	 * <p>
	 * The created database exception just wraps up a core exception created
	 * with {@link #newCoreException(String)}.
	 * </p>
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 *            
	 * @param args
	 * 			  parameters to bind with the message
	 *            
	 *  @see #newCoreException(String)
	 */
	public static RodinDBException newRodinDBException(String message,
			Object... args) {
		
		return newRodinDBException(Messages.bind(message, args));
	}

	/**
	 * Creates a new core exception for this plugin, with the given parameters.
	 * <p>
	 * The severity of the status associated to this exception is
	 * <code>ERROR</code>. The plugin specific code is <code>OK</code>. No
	 * nested exception is stored in the status.
	 * </p>
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 */
	public static CoreException newCoreException(String message) {

		return new CoreException(new Status(IStatus.ERROR,
				EventBPlugin.PLUGIN_ID, IStatus.OK, message, null));
	}

	/**
	 * Creates a new core exception for this plugin, with the given parameters.
	 * <p>
	 * The severity of the status associated to this exception is <code>ERROR</code>.
	 * The plugin specific code is <code>OK</code>.
	 * </p>
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not
	 *            applicable
	 */
	public static CoreException newCoreException(String message,
			Throwable exception) {

		return new CoreException(new Status(IStatus.ERROR,
				EventBPlugin.PLUGIN_ID, IStatus.OK, message, exception));
	}

}
