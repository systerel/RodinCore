/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.ProcessorModule;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGPredicate;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class UtilityModule extends ProcessorModule {

	protected List<POGPredicate> emptyPredicates;
	protected POGHint[] emptyHints;
	protected Predicate btrue;
	protected FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		factory = repository.getFormulaFactory();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
		emptyPredicates = new ArrayList<POGPredicate>(0);
		emptyHints = new POGHint[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		factory = null;
		btrue = null;
		emptyPredicates = null;
		emptyHints = null;
		super.endModule(element, repository, monitor);
	}
	
	private boolean goalIsNotRestricting(Predicate goal) {
		if (goal instanceof RelationalPredicate) {
			RelationalPredicate relGoal = (RelationalPredicate) goal;
			switch (relGoal.getTag()) {
			case Formula.IN:
			case Formula.SUBSETEQ:
				Expression expression = relGoal.getRight();
				Type type = expression.getType();
				Type baseType = type.getBaseType(); 
				if (baseType == null)
					return false;
				Expression typeExpression = baseType.toExpression(factory);
				if (expression.equals(typeExpression))
					return true;
				break;
			default:
				return false;
			}
		}
		return false;
	}

	protected boolean goalIsTrivial(Predicate goal) {
		return goal.equals(btrue) || goalIsNotRestricting(goal);
	}

}
