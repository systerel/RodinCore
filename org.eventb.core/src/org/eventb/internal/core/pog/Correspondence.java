/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.pog.state.ICorrespondence;

/**
 * @author Stefan Hallerstede
 *
 */
public class Correspondence<C extends Object> implements ICorrespondence {

	private final int[] indexOfAbstract;
	private final int[] indexOfConcrete;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.ICorrespondence#getIndexOfCorrespondingAbstract(int)
	 */
	public int getIndexOfCorrespondingAbstract(int index) {
		return indexOfAbstract[index];
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.ICorrespondence#getIndexOfCorrespondingConcrete(int)
	 */
	public int getIndexOfCorrespondingConcrete(int index) {
		return indexOfConcrete[index];
	}
	
	private int indexOf(C[] array, C object) {
		for (int i=0; i<array.length; i++) {
			if (array[i].equals(object))
				return i;
		}
		return -1;
	}

	public Correspondence(C[] conFormulas, C[] absFormulas) {
		indexOfConcrete = new int[absFormulas.length];
		indexOfAbstract = new int[conFormulas.length];
		
		for (int k=0; k<conFormulas.length; k++)
			indexOfAbstract[k] = indexOf(absFormulas, conFormulas[k]);
		
		for (int k=0; k<absFormulas.length; k++)
			indexOfConcrete[k] = indexOf(conFormulas, absFormulas[k]);
	}

}
