/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Common protocol for accessing the formula extension provider registry.
 * <p>
 * The formula extension provider registry manages formula extension provider
 * ids that are known to the Event-B core. A provider id gets registered if it
 * has been registered through the <code>formulaExtensionProviders</code>
 * extension point.
 * </p>
 * 
 * This interface is not intended to be implemented by clients.
 * 
 * @noimplement
 * @noextend
 * @author Thomas Muller
 * @since 2.0
 */
public interface IFormulaExtensionProviderRegistry {

	/**
	 * Checks if a formula extension provider with the given provider id is
	 * present in the provider registry.
	 * <p>
	 * This is fully equivalent to
	 * <code>Arrays.asList(getReasonerIDs()).contains(providerID)</code>,
	 * although implemented in a more efficient way.
	 * </p>
	 * 
	 * @param providerID
	 *            the formula extension provider id to check for
	 * @return <code>true</code> iff the given provider id is known to the
	 *         formula extension provider registry
	 */
	boolean isRegistered(String providerID);

	/**
	 * Returns the ids of all formula extension providers that have been
	 * registered.
	 * 
	 * @return an array of all registered formula extension provider ids
	 */
	String[] getRegisteredIDs();

	/**
	 * Returns the extensions contributed by all extension providers on a given
	 * project.
	 * 
	 * @param project
	 *            the project for which formula extensions are relevant
	 * @return the aggregate extensions defined by extension providers for a
	 *         project or an empty set if no formula extensions are found
	 */
	Set<IFormulaExtension> getFormulaExtensions(IEventBProject project);

	/**
	 * Returns the formula factory computed from the formula extensions provided
	 * for a given project
	 * 
	 * @param project
	 *            the project for which the formula factory should be calculated
	 * 
	 * @return the formula factory calculated from formula extensions that
	 *         concern the given project
	 */
	FormulaFactory getFormulaFactory(IEventBProject project);

}
