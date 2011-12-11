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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.ProverUIUtils;

public abstract class TacticUIInfo extends AbstractInfo {

	/**
	 * Enumeration internalizing the target attribute of UI tactics.
	 */
	enum Target {
		hypothesis, goal, global, any;
	}

	protected final Target target;
	protected final ImageDescriptor iconDesc;
	protected final boolean interrupt;
	protected final String tooltip;
	protected final int priority;
	protected final String name;
	protected final String dropdown;
	protected final String toolbar;
	protected final boolean skipPostTactic;

	private Image icon = null;

	public TacticUIInfo(String id, Target target, ImageDescriptor iconDesc,
			boolean interrupt, String tooltip, int priority, String name,
			String dropdown, String toolbar, boolean skipPostTactic) {
		super(id);
		this.target = target;
		this.iconDesc = iconDesc;
		this.interrupt = interrupt;
		this.tooltip = tooltip;
		this.priority = priority;
		this.name = name;
		this.dropdown = dropdown;
		this.toolbar = toolbar;
		this.skipPostTactic = skipPostTactic;
	}

	public Target getTarget() {
		return target;
	}

	public Image getIcon() {
		if (icon == null) {
			icon = iconDesc.createImage();
			if (ProverUIUtils.DEBUG) {
				if (icon != null) {
					ProverUIUtils.debug("Created icon for tactic " + id);
				} else {
					ProverUIUtils.debug("Cannot create icon for tactic " + id);

				}
			}
		}
		return icon;
	}

	public String getTooltip() {
		return tooltip;
	}

	public String getDropdown() {
		return dropdown;
	}

	public boolean isInterruptable() {
		return interrupt;
	}

	public String getToolbar() {
		return toolbar;
	}

	public boolean isSkipPostTactic() {
		return skipPostTactic;
	}

	// returns a ITacticApplication or ICommandApplication if applicable
	// returns null if not applicable
	public abstract Object getGlobalApplication(IUserSupport us, String globalInput);

}