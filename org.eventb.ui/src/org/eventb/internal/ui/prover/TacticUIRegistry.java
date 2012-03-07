/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static java.util.Collections.unmodifiableList;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.registry.AbstractInfo;
import org.eventb.internal.ui.prover.registry.ExtensionParser;
import org.eventb.internal.ui.prover.registry.PositionApplicationProxy;
import org.eventb.internal.ui.prover.registry.PositionApplicationProxy.PositionApplicationFactory;
import org.eventb.internal.ui.prover.registry.PredicateApplicationProxy;
import org.eventb.internal.ui.prover.registry.PredicateApplicationProxy.PredicateApplicationFactory;
import org.eventb.internal.ui.prover.registry.TacticProviderInfoList;
import org.eventb.internal.ui.prover.registry.ToolbarInfo;

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
	private final TacticProviderInfoList goalTactics;

	private final TacticProviderInfoList hypothesisTactics;

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

		goalTactics = new TacticProviderInfoList(parser.getGoalTactics());
		hypothesisTactics = new TacticProviderInfoList(
				parser.getHypothesisTactics());
		toolbars = parser.getToolbars();

		if (ProverUIUtils.DEBUG) {
			show(goalTactics, "goalTactics");
			show(hypothesisTactics, "hypothesisTactics");
			show(toolbars, "toolbars");
		}
	}

	private void show(Iterable<? extends AbstractInfo> list, String name) {
		System.out.println("Contents of registry : " + name + ":");
		for (final AbstractInfo info : list) {
			System.out.println("\t" + info.getID());
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
		final TacticProviderInfoList tactics = getLocalTactics(hyp);
		return tactics.getTacticApplications(us, hyp, predAppFactory);
	}

	public List<PositionApplicationProxy> getPositionApplications(
			IUserSupport us, Predicate hyp) {
		final TacticProviderInfoList tactics = getLocalTactics(hyp);
		return tactics.getTacticApplications(us, hyp, posAppFactory);
	}

	private TacticProviderInfoList getLocalTactics(Predicate hyp) {
		if (hyp == null) {
			return goalTactics;
		} else {
			return hypothesisTactics;
		}
	}

	public List<ToolbarInfo> getToolbars() {
		return unmodifiableList(toolbars);
	}

}
