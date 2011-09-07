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
package org.eventb.internal.core.preferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.preferences.IPreferenceCheckResult;

/**
 * @author nico
 * 
 */
public class PreferenceCheckResult implements IPreferenceCheckResult {

	private static final PreferenceCheckResult NO_ERROR = new PreferenceCheckResult();

	public static PreferenceCheckResult getNoError() {
		return NO_ERROR;
	}

	private boolean hasError = false;
	private List<String> cycle = null;
	private Set<String> unresolvedReferences = null;

	@Override
	public boolean hasError() {
		return hasError;
	}

	@Override
	public List<String> getCycle() {
		if (cycle == null) {
			return null;
		}
		return new ArrayList<String>(cycle);
	}

	public void setCycle(List<String> cycle) {
		this.cycle = cycle;
		this.hasError = true;
	}

	@Override
	public Set<String> getUnresolvedReferences() {
		if (unresolvedReferences == null) {
			return null;
		}
		return new LinkedHashSet<String>(unresolvedReferences);
	}

	public void setUnresolvedReferences(Set<String> unresolvedReferences) {
		this.unresolvedReferences = unresolvedReferences;
		this.hasError = true;
	}
}
