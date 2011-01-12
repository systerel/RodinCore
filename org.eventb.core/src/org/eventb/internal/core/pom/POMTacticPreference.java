/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed list of default tactics
 ******************************************************************************/
package org.eventb.internal.core.pom;

import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.autoTacticPreference.AutoTacticPreference;

public class POMTacticPreference extends AutoTacticPreference {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.pomTactics"</code>).
	private final static String AUTOTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".pomTactics";	

	private static POMTacticPreference instance;

	private POMTacticPreference() {
		// Singleton: Private default constructor
		super(AUTOTACTICS_ID);
	}

	public static POMTacticPreference getDefault() {
		if (instance == null)
			instance = new POMTacticPreference();
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sequenprover.tacticPreference.TacticPreference#getDefaultIDs()
	 */
	@Override
	protected String [] getDefaultIDs() {
		return new String[] {
	            "org.eventb.core.seqprover.trueGoalTac",
	            "org.eventb.core.seqprover.falseHypTac",
	            "org.eventb.core.seqprover.goalInHypTac",
	            "org.eventb.core.seqprover.funGoalTac",
	            "org.eventb.core.seqprover.finiteHypBoundedGoalTac",
	            "org.eventb.core.seqprover.partitionRewriteTac",
	            "org.eventb.core.seqprover.autoRewriteTac",
	            "org.eventb.core.seqprover.typeRewriteTac",
	            "org.eventb.core.seqprover.findContrHypsTac",
	            "org.eventb.core.seqprover.shrinkImpHypTac",
	            "org.eventb.core.seqprover.funOvrGoalTac",
	            "org.eventb.core.seqprover.clarifyGoalTac",
	            "org.eventb.core.seqprover.onePointGoalTac",
	            "org.eventb.core.seqprover.funOvrHypTac",
	            "org.eventb.core.seqprover.funImgSimpTac",
	            "org.eventb.core.seqprover.onePointHypTac",
	            "org.eventb.core.seqprover.eqHypTac",
	            "org.eventb.core.seqprover.InDomGoalTac",
	            "org.eventb.core.seqprover.FunImgInGoalTac",
	            "com.clearsy.atelierb.provers.core.ml",
	            "com.clearsy.atelierb.provers.core.p0",
	            "org.eventb.core.seqprover.dtDestrWDTac",
		};
	}

}
