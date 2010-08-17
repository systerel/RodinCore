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

import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * <p>
 * The formula extension provider define formula extensions that can be
 * retrieved according to a given project scope.
 * </p>
 * This interface is intended to be implemented by clients.
 * 
 * @author Thomas Muller
 * @since 2.0
 */
public interface IFormulaExtensionProvider {

	/**
	 * Returns the id of the current formula extension provider.
	 * 
	 * @return the id of this provider
	 */
	String getId();

	/**
	 * Returns a set of formula extensions defined for a given project.
	 * 
	 * @param project
	 *            the project to retrieve extensions for
	 * 
	 * @return a set of extensions provided by this extension provider for a
	 *         given <code>project</code>
	 */
	Set<IFormulaExtension> getFormulaExtensions(IEventBProject project);

}
