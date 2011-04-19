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
package org.eventb.internal.ui.refine;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Registry for refinement UI data.
 * 
 * @author Nicolas Beauger
 * 
 */
public class RefinementUIRegistry {

	public static class RefinementUI {
		public final String label;
		public final String title;
		public final String message;

		public RefinementUI(String label, String title, String message) {
			this.label = label;
			this.title = title;
			this.message = message;
		}

	}

	private final Map<IInternalElementType<?>, RefinementUI> refinementUIs = new HashMap<IInternalElementType<?>, RefinementUI>();

	private RefinementUIRegistry() {
		// singleton
	}

	private static final RefinementUIRegistry DEFAULT_INSTANCE = new RefinementUIRegistry();
	private static final String REFINEMENT_UI_EXTENSION_POINT_ID = "org.eventb.ui.refinementUI";
	private static final String REFINEMENT_ID = "refinement-id";
	private static final String LABEL = "label";
	private static final String TITLE = "title";
	private static final String MESSAGE = "message";

	public static RefinementUIRegistry getDefault() {
		return DEFAULT_INSTANCE;
	}

	public RefinementUI getRefinementUI(IInternalElementType<?> elType) {
		load();
		return refinementUIs.get(elType);
	}

	private void load() {
		if (!refinementUIs.isEmpty()) {
			return;
		}
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(REFINEMENT_UI_EXTENSION_POINT_ID);
		for (IConfigurationElement element : extensions) {
			final String extensionId = element.getDeclaringExtension()
					.getUniqueIdentifier();
			try {
				final String refinementId = element.getAttribute(REFINEMENT_ID);
				final IInternalElementType<?> rootType = RodinCore
						.getRefinementManager().getRootType(refinementId);
				if (rootType == null) {
					UIUtils.log(null, "invalid refinement id: " + refinementId);
					continue;
				}
				final String label = element.getAttribute(LABEL);
				final String title = element.getAttribute(TITLE);
				final String message = element.getAttribute(MESSAGE);
				refinementUIs.put(rootType, new RefinementUI(label, title,
						message));
			} catch (InvalidRegistryObjectException e) {
				UIUtils.log(e, "while loading refinement UI extension "
						+ extensionId);
				// continue
			}
		}

	}

}
