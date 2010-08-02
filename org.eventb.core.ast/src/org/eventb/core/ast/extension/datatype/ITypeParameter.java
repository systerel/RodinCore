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
package org.eventb.core.ast.extension.datatype;

/**
 * Common protocol for type parameters.
 * <p>
 * Instances of this interface are intended to be obtained by calling
 * {@link IConstructorMediator#getTypeParameter(String)} in the implementation
 * of {@link IDatatypeExtension#addConstructors(IConstructorMediator)}.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeParameter {

	/**
	 * Returns the name of this type parameter.
	 * 
	 * @return a String
	 */
	String getName();

}
