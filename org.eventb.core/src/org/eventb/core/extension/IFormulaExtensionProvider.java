/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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
import org.rodinp.core.IRodinFile;

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
	 * Returns a set of formula extensions defined for the given file root.
	 * 
	 * @param root
	 *            the root element to retrieve extensions for
	 * 
	 * @return a set of extensions
	 */
	Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root);

	/**
	 * This method is never called by the Event-B Core plug-in and will
	 * disappear in Rodin 3.0.
	 * 
	 * @deprecated Not used anymore
	 */
	@Deprecated
	void setFormulaFactory(IEventBRoot root, FormulaFactory ff);

	/**
	 * Returns common files used for the computation of the factory for the
	 * given file root.
	 * 
	 * <p>
	 * Subsequently, the builder (SC, POG and POM) will consider that there is a
	 * dependency from returned files to the given one.
	 * </p>
	 * 
	 * @param root
	 *            an event-b root
	 * @return a set of rodin files
	 */
	Set<IRodinFile> getCommonFiles(IEventBRoot root);

	/**
	 * Returns project specific files used for the computation of the factory
	 * for the given file root.
	 * 
	 * <p>
	 * Subsequently, the builder (SC, POG and POM) will consider that there is a
	 * dependency from returned files to the given one.
	 * </p>
	 * 
	 * @param root
	 *            an event-b root
	 * @return a set of rodin files
	 */
	Set<IRodinFile> getProjectFiles(IEventBRoot root);
}
