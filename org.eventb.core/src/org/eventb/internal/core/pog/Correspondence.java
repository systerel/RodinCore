/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.List;

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
	@Override
	public int getIndexOfCorrespondingAbstract(int index) {
		return indexOfAbstract[index];
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.ICorrespondence#getIndexOfCorrespondingConcrete(int)
	 */
	@Override
	public int getIndexOfCorrespondingConcrete(int index) {
		return indexOfConcrete[index];
	}
	
	public Correspondence(List<C> conFormulas, List<C> absFormulas) {
		indexOfConcrete = new int[absFormulas.size()];
		indexOfAbstract = new int[conFormulas.size()];
		
		for (int k=0; k<conFormulas.size(); k++)
			indexOfAbstract[k] = absFormulas.indexOf(conFormulas.get(k));
		
		for (int k=0; k<absFormulas.size(); k++)
			indexOfConcrete[k] = conFormulas.indexOf(absFormulas.get(k));
	}

}
