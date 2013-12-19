/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added formula extensions
 *******************************************************************************/
package org.eventb.internal.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.builder.IGraph;

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
	 * Creates a new core exception for this plug-in with the given message and
	 * message arguments.
	 * 
	 * @param message
	 *            a human-readable message, localized to the current locale.
	 *            Should be one of the messages defined in the {@link Messages}
	 *            class
	 * 
	 * @param args
	 *            parameters to bind with the message
	 * 
	 * @see #newCoreException(String)
	 */
	public static CoreException newCoreException(String message,
			Object... args) {

		return newCoreException(Messages.bind(message, args));
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

	public static void addExtensionDependencies(IGraph graph, IEventBRoot target)
			throws CoreException {
		final FormulaExtensionProviderRegistry extProvReg = FormulaExtensionProviderRegistry
				.getExtensionProviderRegistry();
		final Set<IRodinFile> extFiles = extProvReg
				.getAllExtensionFiles(target);
		for (IRodinFile extFile : extFiles) {
			graph.addToolDependency(extFile.getResource(),
					target.getResource(), true);
		}
	}

}
