/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.prover.ProverUIUtils;
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
		final ImageDescriptor iconDesc = getImageDesc();
		final boolean interrupt = configuration.getAttribute("interrupt")
				.equalsIgnoreCase("true");
		final String tooltip = configuration.getAttribute("tooltip"); //$NON-NLS-1$
		final int priority = getPriority();
		final String name = configuration.getAttribute("name");
		final String dropdown = getOptionalAttribute("dropdown");
		final String toolbar = getOptionalAttribute("toolbar");
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

		final String instanceAttribute = tacticProvider == null ? pcAttribute
				: tpAttribute;
		final TacticUIInfo result;
		try {
			final Object candidate = configuration
					.createExecutableExtension(instanceAttribute);

			if (tacticProvider != null) {
				final ITacticProvider appliProvider = getAppliProvider(
						candidate, id, tooltip, iconDesc);
				if (appliProvider == null) {
					result = null;
				} else {
					result = new TacticProviderInfo(id, iconDesc, interrupt,
							tooltip, priority, name, dropdown, toolbar,
							skipPostTactic, appliProvider);
				}

			} else {
				if (!(candidate instanceof IProofCommand)) {
					result = null;
				} else {
					result = new ProofCommandInfo(id, iconDesc, interrupt,
							tooltip, priority, name, dropdown, toolbar,
							skipPostTactic, (IProofCommand) candidate);
				}
			}
			printDebugInfo(tacticProvider, result, id);
			return result;
		} catch (CoreException e) {
			if (ProverUIUtils.DEBUG) {
				ProverUIUtils.debug("Cannot instantiate class from "
						+ instanceAttribute + " for tactic " + id);
				e.printStackTrace();
			}
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

	private ImageDescriptor getImageDesc() {
		IContributor contributor = configuration.getContributor();
		String iconName = configuration.getAttribute("icon"); //$NON-NLS-1$
		return EventBImage.getImageDescriptor(contributor.getName(), iconName);
	}

	private static ITacticProvider getAppliProvider(Object candidate,
			String id, String tip, ImageDescriptor iconDesc) {
		if (candidate instanceof ITacticProvider) {
			return (ITacticProvider) candidate;
		}
		return null;
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