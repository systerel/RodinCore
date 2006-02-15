/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemSeverities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class SCParser {

	private final ITypeEnvironment typeEnvironment;
	private final FormulaFactory factory;
	private final Collection<FreeIdentifier> declaredIdentifiers;
	private IParseResult parseLexResult;
	private ITypeCheckResult typeCheckResult;
	
	public SCParser(ITypeEnvironment typeEnvironment, Collection<FreeIdentifier> declaredIdentifiers, FormulaFactory factory) {
		this.typeEnvironment = typeEnvironment;
		this.factory = factory;
		this.declaredIdentifiers = declaredIdentifiers;
	}
	
	protected boolean parseAssignment(IInternalElement element,
			ISCProblemList problemList) throws RodinDBException {
		
		parseLexResult = factory.parseAssignment(element.getContents());
		
		if(parseLexResult.isSuccess() && parseLexResult.getProblems().size() == 0) {
			
			Assignment assignment = parseLexResult.getParsedAssignment();
			
			IResult legibilityResult = assignment.isLegible(declaredIdentifiers);
			
			if(legibilityResult.isSuccess() && legibilityResult.getProblems().size() == 0) {
				
				// TODO the cloning of the type environment should happen in the AST and not here
				typeCheckResult = assignment.typeCheck(typeEnvironment);
				
				if(typeCheckResult.isSuccess()) {
					return true;
				} else {
					// TODO: proper error messages
					problemList.addProblem(element, "Type error: " + typeCheckResult.getProblems().get(0).toString(), SCProblem.SEVERITY_ERROR);
				}
			} else {
				// TODO: proper error messages
				problemList.addProblem(element, "Legibility error: " + legibilityResult.getProblems().get(0).toString(), SCProblem.SEVERITY_ERROR);
			}
		} else {
			// TODO: proper error messages
			problemList.addProblem(element, "Syntax error: " + parseLexResult.getProblems().get(0).toString(), SCProblem.SEVERITY_ERROR);
		}
		return false;
	}
	
	private void emitProblemMarkers(ISCProblemList problemList, IInternalElement element, String msgHeader, List<ASTProblem> astProblems) {
		for(ASTProblem astProblem : astProblems) {
			
			int severity = astProblem.getSeverity();
			switch (severity) {
			case ProblemSeverities.Error:
				severity = SCProblem.SEVERITY_ERROR;
				break;
			case ProblemSeverities.Ignore:
				severity = SCProblem.SEVERITY_INFO;
				break;
			case ProblemSeverities.Warning:
				severity = SCProblem.SEVERITY_WARNING;
				break;
			default:
				assert false;
				break;
			}
			
			String message = astProblem.toString();
			
			problemList.addProblem(element, msgHeader + message, severity);
		}
	}

	protected boolean parsePredicate(IInternalElement element, ISCProblemList problemList) throws RodinDBException {
		
		parseLexResult = factory.parsePredicate(element.getContents());
		
		IResult legibilityResult = null;
		
		if(parseLexResult.isSuccess() && parseLexResult.getProblems().size() == 0) {
			Predicate predicate = parseLexResult.getParsedPredicate();
			
			legibilityResult = predicate.isLegible(declaredIdentifiers);
			
			if(legibilityResult.isSuccess() && legibilityResult.getProblems().size() == 0) {
				
				// TODO the cloning of the type environment should happen in the AST and not here
				typeCheckResult = predicate.typeCheck(typeEnvironment);
				
				if(typeCheckResult.isSuccess()) {
					typeEnvironment.addAll(typeCheckResult.getInferredEnvironment());
					return true;
				}
			}
		}
		// TODO: proper error messages
		if(parseLexResult != null)
			emitProblemMarkers(problemList, element, "Syntax error: ", parseLexResult.getProblems());
		// TODO: proper error messages
		if(legibilityResult != null)
			emitProblemMarkers(problemList, element, "Legibility error: ", legibilityResult.getProblems());
		// TODO: proper error messages
		if(typeCheckResult != null)
			emitProblemMarkers(problemList, element, "Type error: ", typeCheckResult.getProblems());
		return false;
	}

	public Predicate getPredicate() {
		assert parseLexResult != null;
		return parseLexResult.getParsedPredicate();
	}
	
	public Assignment getAssignment() {
		assert parseLexResult != null;
		return parseLexResult.getParsedAssignment();
	}
	
	public ITypeEnvironment getTypeEnvironment() {
		assert typeCheckResult != null;
		return typeEnvironment;
	}
	
}
