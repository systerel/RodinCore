/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import static java.util.Collections.unmodifiableList;
import static org.eventb.internal.ui.prover.ProverUIUtils.debug;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.registry.PositionApplicationProxy.PositionApplicationFactory;
import org.eventb.internal.ui.prover.registry.PredicateApplicationProxy.PredicateApplicationFactory;
import org.eventb.internal.ui.prover.registry.TacticApplicationProxy.TacticApplicationFactory;

/**
 * Registry of all tactic and proof command contributions to the prover UI.
 * <p>
 * This registry is implemented as a singleton immutable class, which ensures
 * thread-safety. The extension point is analyzed when this class gets loaded by
 * the JVM, which happens the first time that {{@link #getDefault()} is called.
 * </p>
 * 
 * @author Thai Son Hoang
 */
public class TacticUIRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.ui.proofTactics"</code>).
	public static final String PROOFTACTICS_ID = PLUGIN_ID + ".proofTactics"; //$NON-NLS-1$

	// The static instance of this singleton class
	private static final TacticUIRegistry instance = new TacticUIRegistry();

	// Factories for creating tactic application proxys
	private static final PredicateApplicationFactory predAppFactory = new PredicateApplicationFactory();
	private static final PositionApplicationFactory posAppFactory = new PositionApplicationFactory();

	// The registry stored Element UI information
	private final List<TacticProviderInfo> goalTactics;

	private final List<TacticProviderInfo> hypothesisTactics;

	private final List<ToolbarInfo> toolbars;

	/**
	 * The unique instance of this class is created when initializing the static
	 * final field "instance", thus in a thread-safe manner.
	 */
	private TacticUIRegistry() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(PROOFTACTICS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();

		final ExtensionParser parser = new ExtensionParser();
		parser.parse(configurations);
		final IStatus status = parser.getStatus();
		if (!status.isOK()) {
			UIUtils.log(status);
		}

		goalTactics = parser.getGoalTactics();
		hypothesisTactics = parser.getHypothesisTactics();
		toolbars = parser.getToolbars();

		if (ProverUIUtils.DEBUG) {
			show(goalTactics, "goalTactics");
			show(hypothesisTactics, "hypothesisTactics");
			show(toolbars, "toolbars");
		}
	}

	private void show(Iterable<? extends AbstractInfo> list, String name) {
		debug("Contents of registry : " + name + ":");
		for (final AbstractInfo info : list) {
			debug("\t" + info.getID());
		}
	}

	/**
	 * Returns the unique instance of this registry. The instance of this class
	 * is lazily constructed at class loading time.
	 * 
	 * @return the unique instance of this registry
	 */
	public static TacticUIRegistry getDefault() {
		return instance;
	}

	public List<PredicateApplicationProxy> getPredicateApplications(
			IUserSupport us, Predicate hyp) {
		final List<TacticProviderInfo> tactics = getLocalTactics(hyp);
		return getTacticApplications(tactics, us, hyp, predAppFactory);
	}

	public List<PositionApplicationProxy> getPositionApplications(
			IUserSupport us, Predicate hyp) {
		final List<TacticProviderInfo> tactics = getLocalTactics(hyp);
		return getTacticApplications(tactics, us, hyp, posAppFactory);
	}

	private List<TacticProviderInfo> getLocalTactics(Predicate hyp) {
		if (hyp == null) {
			return goalTactics;
		} else {
			return hypothesisTactics;
		}
	}

	private <T extends TacticApplicationProxy<?>> List<T> getTacticApplications(
			List<TacticProviderInfo> infos, IUserSupport us, Predicate hyp,
			TacticApplicationFactory<T> factory) {
		final List<T> result = new ArrayList<T>();
		for (final TacticProviderInfo info : infos) {
			result.addAll(info.getLocalApplications(us, hyp, factory));
		}
		return result;
	}

	public List<ToolbarInfo> getToolbars() {
		return unmodifiableList(toolbars);
	}

}
