/*
 * Created on 24-jun-2005
 *
 */
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

	/* (non-Javadoc)
	 * @see org.eventb.core.result.IResult#getProblems()
	 */
	public List<ASTProblem> getProblems() {
		return problems;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ast.IResult#isSuccess()
	 */
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
