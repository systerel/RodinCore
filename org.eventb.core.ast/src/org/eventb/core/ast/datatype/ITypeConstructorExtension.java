/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.datatype;

import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Common protocol for datatype type constructor extension.
 * 
 * <p>
 * A type constructor extension is the implementation of the datatype type
 * constructor.
 * </p>
 * <p>
 * The {@link #getSyntaxSymbol()} method return the datatype symbol which is the
 * datatype name and the {@link #getFormalNames()} returns the name of the
 * formal type parameters that were used to define the datatype.
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITypeConstructorExtension extends IExpressionExtension {

	/**
	 * Returns the name of the formal type parameters used to define the
	 * datatype. These names are purely decorative but form a good basis for
	 * defining variables after them.
	 * 
	 * @return the names of the formal type parameters
	 */
	String[] getFormalNames();

	/**
	 * Returns the datatype to which this type constructor extension belongs.
	 * 
	 * @return the datatype of this type constructor extension
	 */
	@Override
	IDatatype getOrigin();

}
