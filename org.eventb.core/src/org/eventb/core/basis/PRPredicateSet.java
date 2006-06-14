/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.sequent.Hypothesis;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */

public class PRPredicateSet extends InternalElement implements IPRPredicateSet {

	public PRPredicateSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public String getName() {
		return getElementName();
	}

	public Set<Predicate> getPredicateSet() throws RodinDBException {
		HashSet<Predicate> result = new HashSet<Predicate>();
		IRodinElement[] children = this.getChildrenOfType(IPRPredicate.ELEMENT_TYPE);
		for (IRodinElement element : children) {
			result.add(((IPRPredicate)element).getPredicate());
		}
		return result;
	}


	public void setPredicateSet(Set<Predicate> predSet) throws RodinDBException {
		// TODO Maybe make a common type environment for a tighter coding
		//      Problem : maybe two preds have clashing typ envs!
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		// write out each predicate in the predicate set
		for (Predicate predicate : predSet) {
			((IPRPredicate)this.createInternalElement(IPRPredicate.ELEMENT_TYPE,"",null,null))
			.setPredicate(predicate);
		}
		return;
	}
	
	public void setHypSet(Set<Hypothesis> hypSet) throws RodinDBException {
		// TODO Maybe make a common type environment for a tighter coding
		//      Problem : maybe two preds have clashing typ envs!
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		// write out each predicate in the predicate set
		for (Hypothesis hyp : hypSet) {
			((IPRPredicate)this.createInternalElement(IPRPredicate.ELEMENT_TYPE,"",null,null))
			.setPredicate(hyp.getPredicate());
		}
		return;
	}


}
