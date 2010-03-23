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
package org.eventb.core.ast.extension.notation;


/**
 * @author Nicolas Beauger
 * @since 2.0
 * 
 */
public interface INotation extends Iterable<INotationElement> {

	boolean isFlattenable();

	boolean mapTo(int numExpressions, int numPredicates);
	
//	/**
//	 * Adds the given element to this notation.
//	 * <p>
//	 * Must not be called while iterating over this notation;
//	 * </p>
//	 * 
//	 * @param element
//	 *            a notation element
//	 */
//	void add(INotationElement element);
}
