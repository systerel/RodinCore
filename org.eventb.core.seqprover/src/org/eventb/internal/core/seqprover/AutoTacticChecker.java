/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static java.util.Collections.emptyList;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.ProverFactory.makeSequent;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.osgi.framework.FrameworkUtil.getBundle;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IDynTacticProvider;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.TacticDescriptors.DynTacticProviderRef;
import org.osgi.framework.Bundle;

/**
 * Allows to check that auto tactics seem to work (see bug #779).
 * 
 * @author Laurent Voisin
 */
public class AutoTacticChecker {

	public static boolean DEBUG = false;

	/**
	 * Checks all auto tactics provided by other plugins.
	 */
	public static void checkAutoTactics() {
		final AutoTacticChecker checker = new AutoTacticChecker();
		checker.checkRegularTactics();
		checker.checkDynamicTactics();
	}

	private final AutoTacticRegistry registry;
	private final Bundle sequentProverBundle;

	private AutoTacticChecker() {
		this.registry = (AutoTacticRegistry) getAutoTacticRegistry();
		this.sequentProverBundle = SequentProver.getDefault().getBundle();
	}

	/*
	 * Checks regular tactics.
	 */
	private void checkRegularTactics() {
		for (final String id : registry.getRegisteredIDs()) {
			checkTacticDescriptor(registry.getTacticDescriptor(id));
		}
	}

	/*
	 * Checks dynamic tactics.
	 */
	private void checkDynamicTactics() {
		for (final DynTacticProviderRef providerRef : registry.getDynTacticProviderRefs()) {
			// The bundle must be fetched from the class contributed by the client plug-in.
			final IDynTacticProvider provider = providerRef.getProvider();
			final Bundle bundle = getBundle(provider.getClass());

			for (final ITacticDescriptor descriptor : providerRef.getDynTactics()) {
				checkTacticDescriptor(descriptor, bundle);
			}
		}
	}

	/*
	 * Checks a tactic descriptor with yet unknown bundle
	 */
	private void checkTacticDescriptor(ITacticDescriptor descriptor) {
		final Bundle bundle = getTacticBundle(descriptor);
		checkTacticDescriptor(descriptor, bundle);
	}

	/*
	 * Attempts at finding the bundle that contributed this tactic descriptor. We
	 * first try the descriptor, then an instance if the descriptor comes from the
	 * sequent prover itself. Can return null.
	 */
	private Bundle getTacticBundle(ITacticDescriptor descriptor) {
		final Bundle bundle = getBundle(descriptor.getClass());
		if (bundle != sequentProverBundle) {
			return bundle;
		}
		final ITactic tactic = getTactic(descriptor);
		if (tactic == null) {
			return null;
		}
		return getBundle(tactic.getClass());
	}

	/*
	 * Checks a tactic descriptor from a known bundle.
	 */
	private void checkTacticDescriptor(ITacticDescriptor descriptor, Bundle bundle) {
		if (bundle == null || bundle == sequentProverBundle) {
			return;
		}

		if (DEBUG) {
			trace("");
			trace("Found tactic: " + descriptor.getTacticID());
			trace("        name: " + descriptor.getTacticName());
			trace(" provided by: " + bundle.getSymbolicName());
			trace("with version: " + bundle.getVersion());
		}

		final ITactic tactic = getTactic(descriptor);
		if (tactic == null) {
			return;
		}
		final Object result = runTactic(tactic);

		if (DEBUG) {
			if (result != null) {
				trace("tactic " + descriptor.getTacticID() + " is broken");
			} else {
				trace("      status: " + "OK");
			}
		}
	}

	private Object runTactic(ITactic tactic) {
		final IProofTreeNode node = makeTrivialNode();
		try {
			return tactic.apply(node, null);
		} catch (Throwable t) {
			return t;
		}
	}

	/*
	 * Returns the tactic or null in case of error.
	 */
	private static ITactic getTactic(ITacticDescriptor descriptor) {
		try {
			if (descriptor.isInstantiable()) {
				return descriptor.getTacticInstance();
			}
		} catch (Throwable t) {
			Util.log(t, "while instantiating the auto tactic " + descriptor.getTacticID());
		}
		// No chance to run this tactic
		return null;
	}

	private IProofTreeNode makeTrivialNode() {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final ITypeEnvironment typenv = ff.makeTypeEnvironment();
		final Predicate goal = ff.makeLiteralPredicate(BTRUE, null);
		final IProverSequent sequent = makeSequent(typenv, emptyList(), goal);
		final IProofTree tree = makeProofTree(sequent, this);
		return tree.getRoot();
	}

	private static final void trace(String message) {
		if (DEBUG) {
			System.out.println(message);
		}
	}

}
