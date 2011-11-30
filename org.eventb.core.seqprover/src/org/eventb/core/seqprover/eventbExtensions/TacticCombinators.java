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
import org.eventb.internal.core.seqprover.Messages;

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

	private static void assertOneOrMore(List<ITactic> tactics) {
		Assert.isLegal(tactics.size() >= 1, Messages.tactic_illegalOneOrMore);
	}

	private static void assertOne(List<ITactic> tactics) {
		Assert.isLegal(tactics.size() == 1, Messages.tactic_illegalOne(tactics));
	}

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
				+ ".loopOnAllPending"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOneOrMore(tactics);
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
				+ ".sequence"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOneOrMore(tactics);
			// avoid concurrence issues
			final List<ITactic> copy = new ArrayList<ITactic>(tactics);
			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					boolean success = false;
					Object finalResult = Messages.tactic_failed;
					for (ITactic tactic : copy) {
						final Object result = tactic.apply(ptNode, pm);
						if (pm != null && pm.isCanceled()) {
							return Messages.tactic_cancelled;
						}
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
				+ ".composeUntilSuccess"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOneOrMore(tactics);
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
				+ ".composeUntilFailure"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOneOrMore(tactics);
			// avoid concurrence issues
			final List<ITactic> copy = new ArrayList<ITactic>(tactics);
			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					boolean success = false;
					Object finalResult = Messages.tactic_failed;
					for (ITactic tactic : copy) {
						final Object result = tactic.apply(ptNode, pm);
						if (pm != null && pm.isCanceled()) {
							return Messages.tactic_cancelled;
						}
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
				+ ".loop"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOne(tactics);
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
				+ ".onAllPending"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOne(tactics);
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
				+ ".attempt"; //$NON-NLS-1$

		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOne(tactics);
			final ITactic tactic = tactics.get(0);

			return new ITactic() {

				@Override
				public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
					if (ptNode.isClosed()) {
						return Messages.tactic_nodeClosed;
					}
					tactic.apply(ptNode, pm);
					if (pm != null && pm.isCanceled()) {
						return Messages.tactic_cancelled;
					}
					if (ptNode.getFirstOpenDescendant() == null) {
						return null;
					}
					BasicTactics.prune().apply(ptNode, pm);
					return Messages.tactic_attemptFailed;
				}
			};
		}

	}

	/**
	 * Applies given tactic after lasso. Equivalent to:
	 * Attempt(Sequence(lasso, OnAllPending(tactic)))
	 * 
	 * @author Nicolas Beauger
	 * @since 2.4
	 */
	public static class AttemptAfterLasso implements ITacticCombinator {
	
		@Override
		public ITactic getTactic(List<ITactic> tactics) {
			assertOne(tactics);
			return Tactics.afterLasoo(tactics.get(0));
		}
		
	}

}
