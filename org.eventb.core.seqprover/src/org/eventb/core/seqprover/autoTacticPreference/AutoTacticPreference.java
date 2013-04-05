/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used tactic combinators
 *******************************************************************************/
package org.eventb.core.seqprover.autoTacticPreference;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.Util;

/**
 * @since 1.0
 */
public abstract class AutoTacticPreference implements IAutoTacticPreference {

	private boolean enabled = false;

	private ITactic selectedComposedTactic;

	private ITactic defaultComposedTactic = null;
	
	private ITacticDescriptor selectedDescriptor;
	
	private final ITacticDescriptor defaultDescriptor;
	
	/**
	 * @since 3.0
	 */
	public AutoTacticPreference() {
		this.defaultDescriptor = getDefaultDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	private static ITactic logAndMakeFailure(Throwable t, String logMessage,
			String failTacMessage) {
		Util.log(t, logMessage);
		return BasicTactics.failTac(failTacMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sequenprover.tacticPreference.ITacticPreference#getSelectedComposedTactic()
	 */
	public ITactic getSelectedComposedTactic() {
		if (selectedComposedTactic == null) {
			try {
				selectedComposedTactic = selectedDescriptor.getTacticInstance();
			} catch (Exception e) {
				return logAndMakeFailure(e, "while making selected tactic "
						+ selectedDescriptor.getTacticID(),
						"failed to create selected tactic "
								+ selectedDescriptor.getTacticName());
			}
		}
		return selectedComposedTactic;
	}

	/**
	 * @since 2.3
	 */
	@Override
	public void setSelectedDescriptor(ITacticDescriptor tacticDesc) {
		selectedDescriptor = tacticDesc;
		selectedComposedTactic = null;
	}
	
	public ITactic getDefaultComposedTactic() {
		if (defaultComposedTactic == null) {
			defaultComposedTactic = defaultDescriptor.getTacticInstance();
		}
		return defaultComposedTactic;
	}

}
