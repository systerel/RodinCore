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
import static org.eclipse.core.runtime.preferences.InstanceScope.INSTANCE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.ProverFactory.makeSequent;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.osgi.framework.FrameworkUtil.getBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IAutoTacticCheckResult;
import org.eventb.core.seqprover.IDynTacticProvider;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.TacticDescriptors.DynTacticProviderRef;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Allows to check that auto tactics seem to work (see bug #779).
 * 
 * @author Laurent Voisin
 */
public class AutoTacticChecker {

	public static boolean DEBUG = false;

	private static final Bundle SEQUENT_PROVER_BUNDLE = SequentProver.getDefault().getBundle();

	// Node name in the preferences
	private static final String NODE_ID = "autoTacticChecker";// $NON-NLS-0$

	/**
	 * Checks all auto tactics provided by other plugins.
	 * 
	 * @param ignoreCache if <code>true</code>, ignores the cache and runs all
	 *                    tactics again
	 * @param monitor     progress monitor, may be {@code null}
	 */
	public static List<IAutoTacticCheckResult> checkAutoTactics(boolean ignoreCache, IProgressMonitor monitor) {
		final SubMonitor progress = SubMonitor.convert(monitor, 100);
		final AutoTacticChecker checker = new AutoTacticChecker(ignoreCache);
		checker.addRegularTactics(progress.split(5));
		checker.addDynamicTactics(progress.split(10));
		checker.checkTactics(progress.split(80));
		checker.flushCache(progress.split(5));
		return checker.getResults();
	}

	static boolean isExternalBundle(Bundle bundle) {
		return bundle != null && bundle != SEQUENT_PROVER_BUNDLE;
	}

	private static class ExternalTactic {

		private final ITacticDescriptor descriptor;
		private final Bundle origin;

		public ExternalTactic(ITacticDescriptor descriptor, Bundle origin) {
			this.descriptor = descriptor;
			this.origin = origin;
		}

		public ExternalTactic(ITacticDescriptor descriptor) {
			this.descriptor = descriptor;
			this.origin = getTacticBundle();
		}

		/*
		 * Attempts at finding the bundle that contributed this tactic descriptor. We
		 * first try the descriptor, then an instance if the descriptor comes from the
		 * sequent prover itself. Can return null.
		 */
		private Bundle getTacticBundle() {
			final Bundle bundle = getBundle(descriptor.getClass());
			if (isExternalBundle(bundle)) {
				return bundle;
			}
			final ITactic instance = getInstance();
			if (instance == null) {
				return null;
			}
			return getBundle(instance.getClass());
		}

		/*
		 * Returns the tactic or null in case of error.
		 */
		private ITactic getInstance() {
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

		public boolean isExternal() {
			return isExternalBundle(origin);
		}

		public String getName() {
			return descriptor.getTacticName();
		}

		public String getPrefKey() {
			return descriptor.getTacticID();
		}

		/*
		 * In the preference cache, we store the contributing plugin ID + version. This
		 * is deemed enough to consider that if this information has not changed, then
		 * the auto tactic shall still work.
		 */
		public String getPrefValue() {
			return origin.getSymbolicName() + ":" + origin.getVersion();
		}

		public AutoTacticCheckResult check(boolean isCached) {
			trace("");
			trace("Found tactic: " + descriptor.getTacticID());
			trace("        name: " + descriptor.getTacticName());
			trace(" provided by: " + origin.getSymbolicName());
			trace("with version: " + origin.getVersion());

			if (isCached) {
				trace("      status: OK (cached)");
				return new AutoTacticCheckResult(descriptor);
			}

			final Object result = runTactic();
			trace("      status: " + (result == null ? "OK" : result.toString()));
			return new AutoTacticCheckResult(descriptor, result);
		}

		private Object runTactic() {
			final ITactic instance = getInstance();
			if (instance == null) {
				return "Cannot instantiate the tactic";
			}

			final IProofTreeNode node = makeTrivialNode();
			try {
				return instance.apply(node, null);
			} catch (Throwable t) {
				return t;
			}
		}

		private IProofTreeNode makeTrivialNode() {
			final FormulaFactory ff = FormulaFactory.getDefault();
			final ITypeEnvironment typenv = ff.makeTypeEnvironment();
			final Predicate goal = ff.makeLiteralPredicate(BTRUE, null);
			final IProverSequent sequent = makeSequent(typenv, emptyList(), goal);
			final IProofTree tree = makeProofTree(sequent, this);
			return tree.getRoot();
		}

	}

	private final AutoTacticRegistry registry;
	private final Preferences prefNode;
	private boolean prefNodeChanged;

	private final List<ExternalTactic> tactics;
	private final List<IAutoTacticCheckResult> results;

	private AutoTacticChecker(boolean ignoreCache) {
		this.registry = (AutoTacticRegistry) getAutoTacticRegistry();

		final IEclipsePreferences root = INSTANCE.getNode(SequentProver.PLUGIN_ID);
		this.prefNode = root.node(NODE_ID);
		this.prefNodeChanged = false;
		if (ignoreCache) {
			try {
				prefNode.clear();
				prefNodeChanged = true;
			} catch (BackingStoreException e) {
				Util.log(e, "clearing the cache for autoTacticChecker");
			}
		}

		this.tactics = new ArrayList<>();
		this.results = new ArrayList<>();
	}

	/*
	 * Adds regular tactics.
	 */
	private void addRegularTactics(SubMonitor progress) {
		progress.setTaskName("Extracting regular tactics");
		for (final String id : registry.getRegisteredIDs()) {
			final ITacticDescriptor descriptor = registry.getTacticDescriptor(id);
			addTactic(new ExternalTactic(descriptor));
		}
	}

	/*
	 * Adds dynamic tactics.
	 */
	private void addDynamicTactics(SubMonitor progress) {
		final Collection<DynTacticProviderRef> refs = registry.getDynTacticProviderRefs();
		progress.setWorkRemaining(refs.size());
		for (final DynTacticProviderRef providerRef : refs) {
			progress.setTaskName("Extracting tactics contributed by " + providerRef.getID());

			// The bundle must be fetched from the class contributed by the client plug-in.
			final IDynTacticProvider provider = providerRef.getProvider();
			final Bundle bundle = getBundle(provider.getClass());

			for (final ITacticDescriptor descriptor : providerRef.getDynTactics()) {
				addTactic(new ExternalTactic(descriptor, bundle));
			}
			progress.worked(1);
		}
	}

	private void addTactic(ExternalTactic tactic) {
		if (tactic.isExternal()) {
			tactics.add(tactic);
		}
	}

	/*
	 * Checks all external tactics.
	 */
	private void checkTactics(SubMonitor progress) {
		progress.setWorkRemaining(tactics.size());
		for (final ExternalTactic tactic : tactics) {
			if (progress.isCanceled()) {
				return;
			}
			progress.setTaskName("Checking " + tactic.getName());
			checkTactic(tactic);
			progress.worked(1);
		}
	}

	private void checkTactic(ExternalTactic tactic) {
		final AutoTacticCheckResult result = tactic.check(isCached(tactic));
		if (result == null) {
			return;
		}
		if (result.getStatus().isOK()) {
			setCached(tactic);
		}
		results.add(result);
	}

	private boolean isCached(ExternalTactic tactic) {
		final String key = tactic.getPrefKey();
		final String value = prefNode.get(key, null);
		if (tactic.getPrefValue().equals(value)) {
			return true;
		}
		return false;
	}

	private void setCached(ExternalTactic tactic) {
		final String key = tactic.getPrefKey();
		final String value = tactic.getPrefValue();
		prefNode.put(key, value);
		prefNodeChanged = true;
	}

	private void flushCache(SubMonitor progress) {
		if (!prefNodeChanged) {
			return;
		}
		try {
			prefNode.flush();
		} catch (BackingStoreException e) {
			Util.log(e, "Saving cache of autoTacticChecker");
		}
	}

	public List<IAutoTacticCheckResult> getResults() {
		return results;
	}

	static final void trace(String message) {
		if (DEBUG) {
			System.out.println(message);
		}
	}

}
