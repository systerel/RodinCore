/*******************************************************************************
 * Copyright (c) 2006, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilder;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IReasonerInput;


/**
 * This is a class that stores information about free identifier renaming
 * that can be used to rename reasoner inputs in order to support refactoring
 * of free identifier names in proofs.
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public class ReplayHints {
//	 TODO : Make interface & cleanup
	
	private HashMap<FreeIdentifier, Expression> freeIdentRename;
	private final FormulaFactory factory;

	/**
	 * @since 2.0
	 */
	public ReplayHints(FormulaFactory factory) {
		this.factory = factory;
		this.freeIdentRename = new HashMap<FreeIdentifier, Expression>();
	}

	/**
	 * @since 2.0
	 */
	public ReplayHints(ReplayHints replayHints, FormulaFactory factory) {
		this.factory = factory;
		this.freeIdentRename = new HashMap<FreeIdentifier, Expression>(replayHints.freeIdentRename);
	}
	
	@Override
	public ReplayHints clone() {
		return new ReplayHints(this, factory);
	}

	public boolean isEmpty() {
		return freeIdentRename.isEmpty();
	}

	public void addHints(IAntecedent old, IAntecedent current) {
		if (old.getAddedFreeIdents().length == 0)
			return;
		for (int i = 0; i < old.getAddedFreeIdents().length; i++) {
			final FreeIdentifier oldFreeIdent = old.getAddedFreeIdents()[i];
			if ((i < current.getAddedFreeIdents().length)){
				final FreeIdentifier curFreeIdent = current.getAddedFreeIdents()[i];
				if ((!oldFreeIdent.equals(curFreeIdent))
						&& (oldFreeIdent.getType().equals(curFreeIdent.getType()))) {
					freeIdentRename.put(oldFreeIdent, curFreeIdent);
				}
			}
		}
	}

	public void applyHints(IReasonerInput reasonerInput) {
		reasonerInput.applyHints(this);
	}

	public Predicate applyHints(Predicate predicate) {
		if (predicate == null)
			return null;
		return predicate.substituteFreeIdents(freeIdentRename);
	}

	public Expression applyHints(Expression expression) {
		if (expression == null)
			return null;
		return expression.substituteFreeIdents(freeIdentRename);
	}

}
