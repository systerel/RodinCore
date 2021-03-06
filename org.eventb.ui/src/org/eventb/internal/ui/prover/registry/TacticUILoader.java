/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
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

import static org.eventb.internal.ui.prover.registry.ExtensionParser.fetchIcon;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.registry.TacticUIInfo.Target;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;

public class TacticUILoader {

	// Element id
	private final String id;

	// Configuration information related to the element
	private final IConfigurationElement configuration;

	public TacticUILoader(String id, IConfigurationElement configuration) {
		this.id = id;
		this.configuration = configuration;
	}

	public TacticUIInfo load() {
		final Target target = getTarget();
		if (target == null) {
			return null;
		}
		final ImageDescriptor iconDesc = fetchIcon(configuration);
		final boolean interrupt = configuration.getAttribute("interrupt")
				.equalsIgnoreCase("true");
		final String tooltip = configuration.getAttribute("tooltip"); //$NON-NLS-1$
		final int priority = getPriority();
		final String name = configuration.getAttribute("name");
		String dropdown = getOptionalAttribute("dropdown");
		String toolbar = getOptionalAttribute("toolbar");
		final String skipPostTacticStr = configuration
				.getAttribute("skipPostTactic");
		final boolean skipPostTactic = (skipPostTacticStr != null && skipPostTacticStr
				.equalsIgnoreCase("true"));

		final String tpAttribute = "tacticProvider";
		final String pcAttribute = "proofCommand";
		final String tacticProvider = getOptionalAttribute(tpAttribute);
		final String proofCommand = getOptionalAttribute(pcAttribute);

		if (!(tacticProvider != null ^ proofCommand != null)) {
			ProverUIUtils
					.debug("Either a tactic provider or a proof command should be set for extension: "
							+ id);
			return null;
		}
		if (proofCommand != null && target != Target.global) {
			ProverUIUtils.debug("A proof command must have a global target: "
					+ id);
			return null;
		}
		if (dropdown != null && target != Target.global) {
			ProverUIUtils.debug("Ignored dropdown attribute of local tactic: "
					+ id);
			dropdown = null;
		}
		if (toolbar != null && target != Target.global) {
			ProverUIUtils.debug("Ignored toolbar attribute of local tactic: "
					+ id);
			toolbar = null;
		}

		final String instanceAttribute = tacticProvider == null ? pcAttribute
				: tpAttribute;
		final Object candidate ;
		final TacticUIInfo result;
		try {
			candidate = configuration
					.createExecutableExtension(instanceAttribute);
		} catch (CoreException e) {
			if (ProverUIUtils.DEBUG) {
				ProverUIUtils.debug("Cannot instantiate class from "
						+ instanceAttribute + " for tactic " + id);
				e.printStackTrace();
			}
			return null;
		}

		if (tacticProvider != null) {
			if (candidate instanceof ITacticProvider) {
				result = new TacticProviderInfo(id, target, iconDesc,
						interrupt, tooltip, priority, name, dropdown, toolbar,
						skipPostTactic, (ITacticProvider) candidate);
			} else {
				result = null;
			}
		} else {
			if (candidate instanceof IProofCommand) {
				result = new ProofCommandInfo(id, target, iconDesc, interrupt,
						tooltip, priority, name, dropdown, toolbar,
						skipPostTactic, (IProofCommand) candidate);
			} else {
				result = null;
			}
		}
		printDebugInfo(tacticProvider, result, id);
		return result;
	}

	private Target getTarget() {
		final String target = configuration.getAttribute("target");
		try {
			return Target.valueOf(target);
		} catch (IllegalArgumentException e) {
			ProverUIUtils.debug("Invalid target :" + target
					+ " for extension " + id);
			return null;
		}
	}

	private String getOptionalAttribute(String attribute) {
		final String value = configuration.getAttribute(attribute);
		if (value == null || value.length() == 0) {
			return null;
		}
		return value;
	}

	private int getPriority() {
		final String priorityStr = configuration.getAttribute("priority");
		try {
			return Integer.parseInt(priorityStr);
		} catch (NumberFormatException e) {
			ProverUIUtils.debug("Invalid integer :" + priorityStr
					+ " for extension " + id);
			// lowest priority
			return Integer.MAX_VALUE;
		}
	}

	private static void printDebugInfo(String tacticProvider,
			TacticUIInfo result, String id) {
		if (ProverUIUtils.DEBUG) {
			if (result == null) {
				ProverUIUtils
						.debug("Cannot instantiate class for tactic " + id);
			} else if (tacticProvider != null) {
				ProverUIUtils.debug("Instantiated tactic provider for tactic "
						+ id);
			} else {
				ProverUIUtils.debug("Instantiated proof command for tactic "
						+ id);
			}
		}
	}

}