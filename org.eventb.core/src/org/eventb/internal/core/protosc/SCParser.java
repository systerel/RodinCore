/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.Collection;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
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
		
		if(parseLexResult.isSuccess()) {
			
			Assignment assignment = parseLexResult.getParsedAssignment();
			
			IResult legibilityResult = assignment.isLegible(declaredIdentifiers);
			
			if(legibilityResult.isSuccess()) {
				
				// TODO the cloning of the type environment should happen in the AST and not here
				typeCheckResult = assignment.typeCheck(typeEnvironment);
				
				if(typeCheckResult.isSuccess()) {
					typeEnvironment.addAll(typeCheckResult.getInferredEnvironment());
					return true;
				} else {
					// TODO: proper error messages
					problemList.addProblem(element, "Type error.", SCProblem.SEVERITY_ERROR);
				}
			} else {
				// TODO: proper error messages
				problemList.addProblem(element, "Legibility error.", SCProblem.SEVERITY_ERROR);
			}
		} else {
			// TODO: proper error messages
			problemList.addProblem(element, "Syntax error.", SCProblem.SEVERITY_ERROR);
		}
		return false;
	}

	protected boolean parsePredicate(IInternalElement element, ISCProblemList problemList) throws RodinDBException {
		
		parseLexResult = factory.parsePredicate(element.getContents());
		
		if(parseLexResult.isSuccess()) {
			Predicate predicate = parseLexResult.getParsedPredicate();
			
			IResult legibilityResult = predicate.isLegible(declaredIdentifiers);
			
			if(legibilityResult.isSuccess()) {
				
				// TODO the cloning of the type environment should happen in the AST and not here
				typeCheckResult = predicate.typeCheck(typeEnvironment);
				
				if(typeCheckResult.isSuccess()) {
					typeEnvironment.addAll(typeCheckResult.getInferredEnvironment());
					return true;
				} else {
					// TODO: proper error messages
					problemList.addProblem(element, "Type error.", SCProblem.SEVERITY_ERROR);
				}
			} else {
				// TODO: proper error messages
				problemList.addProblem(element, "Legibility error.", SCProblem.SEVERITY_ERROR);
			}
		} else {
			// TODO: proper error messages
			problemList.addProblem(element, "Syntax error.", SCProblem.SEVERITY_ERROR);
		}
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
