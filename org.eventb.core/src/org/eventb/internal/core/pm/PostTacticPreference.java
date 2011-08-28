/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed list of default tactics
 *     Systerel - implemented getDefaultDescriptor()
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.AutoTacticPreference;
import org.eventb.internal.core.preferences.PreferenceUtils;

public class PostTacticPreference extends AutoTacticPreference {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.postTactics"</code>).
	private final static String POSTTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".postTactics";	

	private static PostTacticPreference instance;

	private PostTacticPreference() {
		// Singleton: Private default constructor
		super(POSTTACTICS_ID);
	}

	public static PostTacticPreference getDefault() {
		if (instance == null)
			instance = new PostTacticPreference();
		return instance;
	}

	private static final String [] DEFAULT_IDS= new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.falseHypTac",
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.goalDisjInHypTac",
		"org.eventb.core.seqprover.funGoalTac",
		"org.eventb.core.seqprover.InDomGoalTac",
		"org.eventb.core.seqprover.FunImgInGoalTac",
		"org.eventb.core.seqprover.finiteHypBoundedGoalTac",
		"org.eventb.core.seqprover.dtDestrWDTac",
		"org.eventb.core.seqprover.genMPTac",
		"org.eventb.core.seqprover.autoRewriteTac",
		"org.eventb.core.seqprover.NNFTac",
		"org.eventb.core.seqprover.typeRewriteTac",
		"org.eventb.core.seqprover.existsHypTac",
		"org.eventb.core.seqprover.findContrHypsTac",
		"org.eventb.core.seqprover.eqHypTac",
		"org.eventb.core.seqprover.shrinkImpHypTac",
		"org.eventb.core.seqprover.shrinkEnumHypTac",
		"org.eventb.core.seqprover.funOvrGoalTac",
		"org.eventb.core.seqprover.clarifyGoalTac",
	};

	@Override
	public ITacticDescriptor getDefaultDescriptor() {
		return PreferenceUtils.loopOnAllPending(DEFAULT_IDS, POSTTACTICS_ID
				+ ".default");
	}

}
