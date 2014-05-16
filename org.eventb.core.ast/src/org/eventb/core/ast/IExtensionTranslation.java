/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Common protocol for translating mathematical extensions to function
 * applications. Instances of this interface allow to translate several Event-B
 * formulas sharing a common type environment.
 * <p>
 * Only extensions that do not come from a datatype and that are WD-strict are
 * translated. Given that the input formulas are well-defined, then this
 * translation preserves logical validity but loses the semantics of the
 * extensions.
 * </p>
 * <p>
 * This interface should be used in the following manner:
 * <ul>
 * <li>From the common type environment, create an instance of this interface
 * with method {@link ITypeEnvironment#makeExtensionTranslation()}.</li>
 * <li>Translate as many formulas as needed using this instance by calling
 * method {@link Formula#translateExtensions(IExtensionTranslation)}.</li>
 * </ul>
 * </p>
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @since 3.1
 * @see IDatatypeTranslation
 */
public interface IExtensionTranslation {

	/**
	 * Returns a snapshot of the type environment of the formulas translated so
	 * far. The returned type environment is obtained by augmenting the initial
	 * type environment of this translation with all functions that have been
	 * created by the translation.
	 * 
	 * @return a snapshot of the translated type environment
	 */
	ISealedTypeEnvironment getTargetTypeEnvironment();

}
