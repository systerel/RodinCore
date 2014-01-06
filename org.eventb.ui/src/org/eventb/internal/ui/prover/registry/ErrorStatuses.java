/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eventb.internal.ui.prover.registry.TacticUIRegistry.PROOFTACTICS_ID;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * Error status codes for the proof tactics extension parser.
 * 
 * @author Laurent Voisin
 * 
 */
public final class ErrorStatuses {

	// Error status codes

	// Multiple errors encountered
	public static final int LOADING_ERRORS = 1000;

	public static IStatus loadingErrors(IStatus[] errors) {
		return new MultiStatus(PLUGIN_ID, LOADING_ERRORS, errors,
				"Errors encountered when loading the " + PROOFTACTICS_ID
						+ " extensions:", null);
	}

	// An unknown configuration element was contributed
	public static final int UNKNOWN_ELEMENT = 1001;

	public static IStatus unknownElement(IConfigurationElement ce) {
		return error(UNKNOWN_ELEMENT, "Unknown element '" + ce.getName() + "' "
				+ contributedBy(ce));
	}

	// A configuration element is missing the id attribute
	public static final int MISSING_ID = 1002;

	public static IStatus missingId(IConfigurationElement ce) {
		return error(MISSING_ID, "Missing id in " + ce.getName()
				+ " extension " + contributedBy(ce));
	}

	// The id attribute is ill-formed
	public static final int INVALID_ID = 1003;

	public static IStatus invalidId(IConfigurationElement ce) {
		return error(INVALID_ID, "Invalid id in " + ce.getName()
				+ " extension '" + ce.getAttribute("id") + "' "
				+ contributedBy(ce));
	}

	// The id attribute is ill-formed
	public static final int DUPLICATE_ID = 1004;

	public static IStatus duplicateId(IConfigurationElement ce) {
		return error(DUPLICATE_ID, "Duplicate id for " + ce.getName()
				+ " extension '" + ce.getAttribute("id") + "' "
				+ contributedBy(ce));
	}
	
	// The instance object is invalid (wrong interface for instance)
	public static final int INVALID_INSTANCE = 1005;
	
	public static IStatus invalidInstance(IConfigurationElement ce,
			String instanceAttribute) {
		return error(INVALID_INSTANCE, "Invalid instance for " + ce.getName()
				+ " extension '" + ce.getAttribute(instanceAttribute) + "' "
				+ contributedBy(ce));
	}

	private static IStatus error(int code, String msg) {
		return new Status(ERROR, PLUGIN_ID, code, msg, null);
	}

	private static String contributedBy(IConfigurationElement ce) {
		return "contributed by " + ce.getContributor().getName();
	}
}
