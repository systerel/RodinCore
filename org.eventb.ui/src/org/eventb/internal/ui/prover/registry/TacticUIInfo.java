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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.pm.IUserSupport;

public abstract class TacticUIInfo extends AbstractUIInfo {

	/**
	 * Enumeration internalizing the target attribute of UI tactics.
	 */
	public static enum Target {
		hypothesis, goal, global, any;
	}

	protected final Target target;
	protected final boolean interrupt;
	protected final String tooltip;
	protected final int priority;
	protected final String dropdown;
	protected final String toolbar;
	protected final boolean skipPostTactic;

	/**
	 * @param id
	 *            the id of the tactic
	 * @param target
	 *            the target of the tactic
	 * @param iconDesc
	 *            the descriptor of the icon of the tactic, or <code>null</code>
	 * @param interrupt
	 *            <code>true</code> if the tactic is interruptible,
	 *            <code>false</code> otherwise
	 * @param tooltip
	 *            the tooltip of the tactic
	 * @param priority
	 *            the priority used in menus to sort tactics
	 * @param name
	 *            the name o the tactic
	 * @param dropdown
	 *            the id of the dropdown menu that displays the tactic, if any;
	 *            <code>null</code> if the tactic is local
	 * @param toolbar
	 *            the id of the toolbar that displays the tactic, if any;
	 *            <code>null</code> if the tactic is local
	 * @param skipPostTactic
	 *            <code>true</code> to skip post tactic application,
	 *            <code>false</code> otherwise
	 */
	public TacticUIInfo(String id, Target target, ImageDescriptor iconDesc,
			boolean interrupt, String tooltip, int priority, String name,
			String dropdown, String toolbar, boolean skipPostTactic) {
		super(id, name, iconDesc);
		this.target = target;
		this.interrupt = interrupt;
		this.tooltip = tooltip;
		this.priority = priority;
		this.dropdown = dropdown;
		this.toolbar = toolbar;
		this.skipPostTactic = skipPostTactic;
	}

	/**
	 * Returns the target application of the tactic.
	 * 
	 * @return a target
	 */
	public Target getTarget() {
		return target;
	}

	/**
	 * Returns the tooltip associated to the tactic.
	 * 
	 * @return a tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Returns the id of the dropdown menu through which the tactic is
	 * available.
	 * <p>
	 * In particular, local tactics are never displayed in dropdown menus.
	 * </p>
	 * 
	 * @return a dropdown id, or <code>null</code> if the tactic is not in a
	 *         dropdown menu
	 */
	public String getDropdown() {
		return dropdown;
	}

	/**
	 * Returns whether the tactic is interruptible or not.
	 * 
	 * @return <code>true</code> if interruptible, <code>false</code> otherwise
	 */
	public boolean isInterruptable() {
		return interrupt;
	}

	/**
	 * Returns the id of the toolbar through which the tactic is available.
	 * <p>
	 * In particular, local tactics are never displayed in toolbars.
	 * </p>
	 * 
	 * @return a toolbar id, or <code>null</code> if the tactic is not in a
	 *         toolbar
	 */
	public String getToolbar() {
		return toolbar;
	}

	/**
	 * Returns whether to skips post tactic after tactic application.
	 * 
	 * @return <code>true</code> to skip post tactic, <code>false</code>
	 *         otherwise
	 */
	public boolean isSkipPostTactic() {
		return skipPostTactic;
	}

	/**
	 * Returns a global tactic application if applicable, or <code>null</code>.
	 * 
	 * @param us
	 *            the User Support, see {@link org.eventb.core.pm.IUserSupport}
	 * @param globalInput
	 *            the global tactic input
	 * @return a ITacticApplication or ICommandApplication if applicable, else
	 *         <code>null</code>
	 */
	public abstract Object getGlobalApplication(IUserSupport us,
			String globalInput);

}