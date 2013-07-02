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

import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * A reference to another tactic descriptor in a preference map.
 * 
 * @author Nicolas Beauger
 */
public class TacticDescriptorRef implements ITacticDescriptorRef {

	private static final String INVALID_REFERENCE = "INVALID REFERENCE: ";
	private final IPrefMapEntry<ITacticDescriptor> prefMapEntry;

	public TacticDescriptorRef(IPrefMapEntry<ITacticDescriptor> prefUnit) {
		this.prefMapEntry = prefUnit;
	}

	private ITacticDescriptor getDesc() {
		return prefMapEntry.getValue();
	}

	// entry key must be accessed dynamically (may change)
	private String makeInvalidReference() {
		final StringBuilder sb = new StringBuilder();
		sb.append(INVALID_REFERENCE);
		sb.append(prefMapEntry.getKey());
		return sb.toString();
	}

	@Override
	public String getTacticID() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			return makeInvalidReference();
		}
		return desc.getTacticID();
	}

	@Override
	public String getTacticName() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			return makeInvalidReference();
		}
		return prefMapEntry.getKey();
	}

	@Override
	public String getTacticDescription() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			return makeInvalidReference();
		}
		return desc.getTacticDescription();
	}

	@Override
	public boolean isInstantiable() {
		return true;
	}

	@Override
	public ITactic getTacticInstance() {
		final ITacticDescriptor desc = getDesc();
		if (desc == null) {
			throw new IllegalStateException(makeInvalidReference());
		}

		return desc.getTacticInstance();
	}

	@Override
	public boolean isValidReference() {
		return getDesc() != null;
	}

	@Override
	public IPrefMapEntry<ITacticDescriptor> getPrefEntry() {
		return prefMapEntry;
	}
}
