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
package org.eventb.internal.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.preferences.autotactics.IInjectLog;

/**
 * Log for preference map injection.
 * 
 * @author beauger
 * 
 */
public class InjectLog implements IInjectLog {

	private final List<String> errors = new ArrayList<String>();
	private final List<String> warnings = new ArrayList<String>();

	@Override
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	@Override
	public List<String> getErrors() {
		return new ArrayList<String>(errors);
	}

	public void addError(String message) {
		errors.add(message);
	}

	@Override
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	@Override
	public List<String> getWarnings() {
		return new ArrayList<String>(warnings);
	}

	public void addWarning(String message) {
		warnings.add(message);
	}

}
