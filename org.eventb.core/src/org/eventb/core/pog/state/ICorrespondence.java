/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

/**
 * A correspondence relates two arrays and should be used to accelerate search
 * of elements of one array in the other. One array contains abstract elements,
 * the other concrete elements.
 * 
 * @see IAbstractEventGuardTable
 * @see IAbstractEventActionTable
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface ICorrespondence {

	/**
	 * Given the index of an abstract element, returns the corresponding index
	 * of an identical concrete element, or <code>-1</code> if there is no such 
	 * concrete element.
	 * @param index index of an abstract element
	 * @return index of an identical concrete element, 
	 * 		or <code>-1</code> if there is no such concrete element
	 */
	int getIndexOfCorrespondingConcrete(int index);
	
	/**
	 * Given the index of a concrete element, returns the corresponding index
	 * of an identical abstract element, or <code>-1</code> if there is no such 
	 * concrete element.
	 * @param index index of a concrete element
	 * @return index of an identical abstract element, 
	 * 		or <code>-1</code> if there is no such abstract element
	 */
	int getIndexOfCorrespondingAbstract(int index);
	
}
