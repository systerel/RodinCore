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

import org.eclipse.core.runtime.Assert;
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
	 * 
	 */
	public static class LoopOnAllPending implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".loopOnAllPending";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() >= 1, "illegal tactics: " + tactics);
			final ITactic[] tacs = tactics.toArray(new ITactic[tactics.size()]);
			return BasicTactics.loopOnAllPending(tacs);
		}

	}

	/**
	 * The 'sequence' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static class Sequence implements ITacticCombinator {
		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".sequence";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() >= 1, "illegal tactics: " + tactics);
			// avoid concurrence issues
			final List<ITactic> copy = new ArrayList<ITactic>(tactics);
			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					boolean success = false;
					Object finalResult = "failed";
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

	/**
	 * The 'compose until success' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static class ComposeUntilSuccess implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".composeUntilSuccess";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() >= 1, "illegal tactics: " + tactics);
			final ITactic[] tacs = tactics.toArray(new ITactic[tactics.size()]);
			return BasicTactics.composeUntilSuccess(tacs);
		}

	}

	/**
	 * The 'compose until failure' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static class ComposeUntilFailure implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".composeUntilFailure";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() >= 1, "illegal tactics: " + tactics);
			// avoid concurrence issues
			final List<ITactic> copy = new ArrayList<ITactic>(tactics);
			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					boolean success = false;
					Object finalResult = "failed";
					for (ITactic tactic : copy) {
						final Object result = tactic.apply(ptNode, pm);
						if (result == null) {
							success = true;
						} else {
							finalResult = result;
							break;
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

	/**
	 * The 'loop' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static class Loop implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".loop";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() == 1, "illegal tactics: " + tactics);
			return BasicTactics.repeat(tactics.get(0));
		}

	}

	/**
	 * The 'on all pending' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static class OnAllPending implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".onAllPending";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() == 1, "illegal tactics: " + tactics);
			return BasicTactics.onAllPending(tactics.get(0));
		}

	}

	/**
	 * The 'attempt' tactic combinator.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static class Attempt implements ITacticCombinator {

		public static final String COMBINATOR_ID = SequentProver.PLUGIN_ID
				+ ".attempt";

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			Assert.isLegal(tactics.size() == 1, "illegal tactics: " + tactics);
			final ITactic tactic = tactics.get(0);

			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					if (ptNode.isClosed()) {
						return "node is closed";
					}
					tactic.apply(ptNode, pm);
					if (ptNode.getFirstOpenDescendant() == null) {
						return null;
					}
					BasicTactics.prune().apply(ptNode, pm);
					return "attempt failed";
				}
			};
		}

	}

}
