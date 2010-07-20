/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - got factory from repository
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
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class UtilityModule extends POGProcessorModule {
	
	public static boolean DEBUG_TRIVIAL = false;

	
	protected static final IPOGSource[] NO_SOURCES = new IPOGSource[0];
	protected static final IPOGHint[] NO_HINTS = new IPOGHint[0];
	protected static final List<IPOGPredicate> emptyPredicates = new ArrayList<IPOGPredicate>(0);

	protected Predicate btrue;
	protected FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		factory = repository.getFormulaFactory();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		factory = null;
		btrue = null;
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

	protected void debugTraceTrivial(String sequentName) {
		System.out.println("POG: " + getClass().getSimpleName() + ": Filtered trivial PO: " + sequentName);
	}

}
