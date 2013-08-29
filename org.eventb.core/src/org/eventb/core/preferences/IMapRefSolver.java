/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

import org.eventb.core.preferences.autotactics.IInjectLog;

/**
 * Common protocol for preference references solvers.
 * <p>
 * Preference translators first deserialize references using placeholders; then
 * these references are resolved when the whole preference map has been
 * deserialized.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * 
 */
public interface IMapRefSolver<T> {

	/**
	 * Replaces the reference placeholders, contained in the given preference,
	 * by actual references obtained from the given map.
	 * 
	 * @param map
	 *            a preference map
	 * @param log
	 *            a log to add warnings about unresolved references
	 * @since 3.0
	 */
	void resolveReferences(CachedPreferenceMap<T> map, IInjectLog log);

}
