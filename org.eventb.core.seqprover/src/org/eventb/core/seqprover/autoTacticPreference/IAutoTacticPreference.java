/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used tactic combinators
 *******************************************************************************/
package org.eventb.core.seqprover.autoTacticPreference;

import java.util.Collection;
import java.util.List;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author htson
 *         <p>
 *         This defines the general interface for a tactic preference.
 *         <ul>
 *         <li>The preference allows to check if a tactic is declared to be
 *         used or not.
 *         <li>The preference contains the set of selected tactics that
 *         "composed" together using pre-defined method.
 *         <li>The preference give the default tactic that can be composed
 *         together.
 *         <li>There is a boolean flag to enable/disable the tactic preference.
 *         </ul>
 *         </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IAutoTacticPreference {

	/**
	 * Checked if a tactic is declared for using with the tactic container.
	 * 
	 * @param tacticDesc
	 *            a tactic descriptor {@link ITacticDescriptor}.
	 * @return <code>true</code> if the tactic can be used with the tactic
	 *         container. Return <code>false</code> otherwise.
	 */
	public abstract boolean isDeclared(ITacticDescriptor tacticDesc);

	/**
	 * Return the set of tactic that are declared for using with the tactic
	 * container.
	 * <p>
	 * 
	 * @return array of tactic descriptors {@link ITacticDescriptor}.
	 */
	public abstract Collection<ITacticDescriptor> getDeclaredDescriptors();

	/**
	 * Return the set of tactic that are declared for using as default with the
	 * tactic container.
	 * <p>
	 * 
	 * @return array of tactic descriptors {@link ITacticDescriptor}.
	 * @deprecated use {@link #getDefaultDescriptor()}
	 */
	@Deprecated
	public abstract List<ITacticDescriptor> getDefaultDescriptors();

	/**
	 * Returns the descriptor of the default tactic.
	 * 
	 * @return
	 * @since 2.3
	 */
	ITacticDescriptor getDefaultDescriptor();
	
	/**
	 * Enable/Disable the tactic container.
	 * 
	 * @param enabled
	 *            <code>true</code> to enable the container,
	 *            <code>false</code> to disable the container
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Check if the tactic container is currently enable or not.
	 * 
	 * @return <code>true</code> if enable, <code>false</code> otherwise.
	 */
	public boolean isEnabled();

	/**
	 * Return the tactic that is the composition of the selected tactics.
	 * <p>
	 * 
	 * @return a tactic {@link ITactic}.
	 */
	public ITactic getSelectedComposedTactic();

	/**
	 * Return the tactic that is the composition of the default tactics.
	 * 
	 * @return a tactic {@link ITactic}.
	 */
	public ITactic getDefaultComposedTactic();

	/**
	 * Sets selected descriptor to the given tactic descriptor.
	 * 
	 * @param tacticDesc
	 *            a tactic descriptor
	 * @since 2.3
	 */
	void setSelectedDescriptor(ITacticDescriptor tacticDesc);
	
	/**
	 * Set the contained tactics to be an array of tactic descriptors.
	 * 
	 * @param tacticDescs
	 *            a list of tactic descriptors {@link ITacticDescriptor}.
	 * @deprecated use {@link #setSelectedDescriptor(ITacticDescriptor)}
	 */
	@Deprecated
	public void setSelectedDescriptors(List<ITacticDescriptor> tacticDescs);

}