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
package org.eventb.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticCombinator;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * This class contains internal classes for tactic combinators.
 * 
 * <p>
 * They extend the 'tactic combinators' extension point.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * 
 */
public class TacticCombinators {
	
	private TacticCombinators() {
		// not intended to be instantiated
	}
	
	/**
	 * The 'loop on all pending' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * @since 2.3
	 * 
	 */
	public static class LoopOnAllPending implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".loopOnAllPending";
		
		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			final ITactic[] tacs = tactics.toArray(new ITactic[tactics.size()]);
			return BasicTactics.loopOnAllPending(tacs);
		}

	}

	public static class Sequence implements ITacticCombinator {
		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".sequence";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			// avoid concurrence issues
			final List<ITactic> copy = new ArrayList<ITactic>(tactics);
			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					boolean success = false;
					Object finalResult = null;
					for (ITactic tactic : copy) {
						final Object result = tactic.apply(ptNode, pm);
						if (result == null) {
							success = true;
						} else {
							finalResult = result;
						}
					}
					if (success) {
						return null;
					} else {
						return finalResult;
					}
				}
			};
		}
		
	}
	
}
