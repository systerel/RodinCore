/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added method hasProblem()
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.IResult;

/**
 * AbstractResult is the base class for all the results returned by components that have to deal with
 * formulae.
 * 
 * @author François Terrier
 *
 */
public abstract class AbstractResult implements IResult {
	
	private List<ASTProblem> problems = new ArrayList<ASTProblem>();
	private boolean success = true;
	
	/**
	 * Adds a problem to this result.
	 * 
	 * @param problem the problem to be added
	 */
	public void addProblem(ASTProblem problem) {
		if (problem.isError()) {
			success = false;
		}
		problems.add(problem);
	}

	@Override
	public boolean hasProblem() {
		return !problems.isEmpty();
	}
	
	@Override
	public List<ASTProblem> getProblems() {
		return problems;
	}
	
	@Override
	public boolean isSuccess() {
		return success;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (ASTProblem prob : problems) {
			str.append(prob.toString()+"\n");
		}
		return str.toString();
	}
}
