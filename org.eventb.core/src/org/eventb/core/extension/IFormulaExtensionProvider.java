/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for providing extensions to the Event-B mathematical language
 * to be used in models and proofs.  Implementations of this interface must be
 * registered using the <code>org.eventb.core.formulaExtensionProvider</code>
 * extension point.
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
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
	 * Returns files used for the computation of the factory for the given file
	 * root.
	 * 
	 * <p>
	 * Subsequently, the builder (SC, POG and POM) will consider that there is a
	 * dependency from returned files to the given one.
	 * </p>
	 * 
	 * @param root
	 *            an event-b root
	 * @return a set of rodin files
	 * @since 3.0
	 */
	Set<IRodinFile> getFactoryFiles(IEventBRoot root);

	/**
	 * Returns the formula factory previously stored in the given element. If no
	 * formula factory was stored previously in this element, this is an error
	 * and should be reported by throwing a Rodin DB exception.
	 * 
	 * @param element
	 *            a Rodin element containing a serialized formula factory
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the contained formula factory
	 * @throws CoreException
	 *             if a problem occurs while deserializing
	 * @since 3.0
	 */
	FormulaFactory loadFormulaFactory(ILanguage element,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Serializes the given formula factory into the given element. It is
	 * guaranteed that this element is empty (no child, nor attribute) when this
	 * method is called.
	 * 
	 * @param element
	 *            a Rodin element where to serialize
	 * @param factory
	 *            a formula factory to serialize
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws RodinDBException
	 *             if a problem occurs while serializing
	 * @since 3.0
	 */
	void saveFormulaFactory(ILanguage element, FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException;

}
