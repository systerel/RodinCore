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
package org.eventb.core.extension;

import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
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
	 * @param root
	 *            the root element to retrieve extensions for
	 * 
	 * @return a set of extensions provided by this extension provider for a
	 *         given <code>project</code>
	 */
	Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root);

	/**
	 * Sets the given formula factory on the given root. This method shall set
	 * the formula factory for a newly created root. A later call on
	 * getFormulaFactory() shall return this factory.
	 * 
	 * @param root
	 *            the root element
	 * @param ff
	 *            the formula factory to be set on the given root
	 */
	void setFormulaFactory(IEventBRoot root, FormulaFactory ff);

}
