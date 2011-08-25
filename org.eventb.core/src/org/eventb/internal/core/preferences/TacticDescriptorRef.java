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

import org.eventb.core.preferences.IPreferenceUnit;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ITactic;

/**
 * @author Nicolas Beauger
 *
 */
public class TacticDescriptorRef implements ITacticDescriptorRef {

	private static final String INVALID_PREFERENCE_ENTRY = "invalid tactic reference";
	private final IPreferenceUnit<ITacticDescriptor> prefUnit;
	
	public TacticDescriptorRef(IPreferenceUnit<ITacticDescriptor> prefUnit) {
		this.prefUnit = prefUnit;
	}

	private ITacticDescriptor getDesc() {
		return prefUnit.getElement();
	}

	@Override
	public String getTacticID() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			return INVALID_PREFERENCE_ENTRY;
		}
		return desc.getTacticID();
	}

	@Override
	public String getTacticName() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			return INVALID_PREFERENCE_ENTRY;
		}
		return desc.getTacticName();
	}

	@Override
	public String getTacticDescription() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			return INVALID_PREFERENCE_ENTRY;
		}
		return desc.getTacticDescription();
	}

	@Override
	public ITactic getTacticInstance() throws IllegalArgumentException {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			throw new IllegalArgumentException(INVALID_PREFERENCE_ENTRY);
		}
		
		return desc.getTacticInstance();
	}

	@Override
	public boolean isValidReference() {
		return getDesc() != null;
	}

}
