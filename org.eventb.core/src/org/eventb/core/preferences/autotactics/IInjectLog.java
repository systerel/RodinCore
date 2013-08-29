/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences.autotactics;

import java.util.List;

/**
 * Common protocol for results of preference map injection.
 * 
 * @see ITacticProfileCache
 * @author beauger
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IInjectLog {

	/**
	 * Returns whether this log contains one or more errors.
	 * 
	 * @return <code>true</code> if there are errors, <code>false</code>
	 *         otherwise
	 */
	boolean hasErrors();

	/**
	 * Returns the errors contained in this log
	 * 
	 * @return a list of error messages
	 */
	List<String> getErrors();

	/**
	 * Returns whether this log contains one or more warnings.
	 * 
	 * @return <code>true</code> if there are warnings, <code>false</code>
	 *         otherwise
	 */
	boolean hasWarnings();

	/**
	 * Returns the warnings contained in this log
	 * 
	 * @return a list of warning messages
	 */
	List<String> getWarnings();

}
