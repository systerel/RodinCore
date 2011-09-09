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

import java.util.List;
import java.util.Set;

/**
 * Common protocol for preference check results.
 * 
 * <p>
 * Instances of this class are the result of calls to
 * {@link CachedPreferenceMap#preAddCheck(String, Object)}.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IPreferenceCheckResult {

	/**
	 * Returns whether this result has errors.
	 * 
	 * @return <code>true</code> iff this result has errors
	 */
	boolean hasError();

	/**
	 * Returns a list of entry keys involved in a cycle.
	 * 
	 * @return a list of keys, or <code>null</code> if no cycle has been found
	 */
	List<String> getCycle();

	/**
	 * Returns a list of unresolved entry key references.
	 * 
	 * @return a list of keys, on <code>null</code> if no unresolved references
	 *         have been found
	 */
	Set<String> getUnresolvedReferences();
}
