/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IIdentifierSymbolTable;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IParsedFormula;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ParsedFormula;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class LabeledFormulaModule extends LabeledElementModule {

	protected IIdentifierSymbolTable identifierSymbolTable;
	protected ITypingState typingState;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		identifierSymbolTable = 
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		typingState = 
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		typingState = null;
		super.endModule(element, repository, monitor);
	}

	protected void issueASTProblemMarkers(int severity, IRodinElement element, IResult result) {
		
		for (ASTProblem problem : result.getProblems()) {
			SourceLocation location = problem.getSourceLocation();
			ProblemKind problemKind = problem.getMessage();
			Object[] args = problem.getArgs();
			
			String message;
			Object[] objects; // parameters for the marker
			
			switch (problemKind) {
			
			case FreeIdentifierHasBoundOccurences:
				message = Messages.scuser_FreeIdentifierHasBoundOccurences;
				objects = new Object[] {
					args[0]
				};				
				break;
				
			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				message = Messages.scuser_BoundIdentifierIsAlreadyBound;
				objects = new Object[] {
					args[0]
				};
				break;
				
			case BoundIdentifierIndexOutOfBounds:
				// internal error
				message = Messages.scuser_InternalError;
				objects = new Object[0];
				break;
				
			case Circularity:
				message = Messages.scuser_Circularity;
				objects = new Object[0];
				break;
				
			case InvalidTypeExpression:
				// internal error
				message = Messages.scuser_InternalError;
				objects = new Object[0];
				break;
				
			case LexerError:
				message = Messages.scuser_LexerError;
				objects = new Object[] {
						args[0]
				};			
				break;
				
			case LexerException:
				// internal error
				message = Messages.scuser_InternalError;
				objects = new Object[0];
				break;
				
			case ParserException:
				// internal error
				message = Messages.scuser_InternalError;
				objects = new Object[0];
				break;
				
			case SyntaxError:
				
				// TODO: prepare detailed error messages "args[0]" obtained from the parser for 
				//       internationalisation
				
				message = Messages.scuser_SyntaxError;
				objects = new Object[] {
						args[0]
				};						
				break;
				
			case TypeCheckFailure:
				message = Messages.scuser_TypeCheckFailure;
				objects = new Object[0];			
				break;
				
			case TypesDoNotMatch:
				message = Messages.scuser_TypesDoNotMatch;
				objects = new Object[] {
						args[0],
						args[1]
				};						
				break;
				
			case TypeUnknown:
				message = Messages.scuser_TypeUnknown;
				objects = new Object[0];			
				break;
				
			default:
				
				message = Messages.scuser_InternalError;
				objects = new Object[0];
				
				break;
			}
			
			if (location == null) {
				issueMarker(
						severity, 
						element, 
						message, 
						objects);
			} else {	
				issueMarkerWithLocation(
						severity, 
						element, 
						message, 
						location.getStart(), 
						location.getEnd(), 
						objects);
			}
		}
	}

	/**
	 * @param index the index of the formula to be parsed
	 * @param formulaElements the formula elements
	 * @param formulas the parsed formulas
	 * @param freeIdentifierContext the free identifier context of this predicate
	 * (@see org.eventb.core.ast.Formula#isLegible(Collection))
	 * @param factory the formula factory to use 
	 * @return <code>true</code>, iff the formula was successfully parsed
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected abstract boolean parseFormula(
			int index,
			IInternalElement[] formulaElements,
			Formula[] formulas,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException;
	
	/**
	 * @param index the index of the formula to be parsed
	 * @param formulaElements the formula elements
	 * @param formulas the parsed formulas
	 * @return the inferred type environment
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected ITypeEnvironment typeCheckFormula(
			int index,
			IInternalElement[] formulaElements,
			Formula[] formulas,
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		ITypeCheckResult typeCheckResult = formulas[index].typeCheck(typeEnvironment);
		
		if (!typeCheckResult.isSuccess()) {
			issueASTProblemMarkers(IMarkerDisplay.SEVERITY_ERROR, formulaElements[index], typeCheckResult);
			
			return null;
		}
		
		return typeCheckResult.getInferredEnvironment();

	}

	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		if (inferredEnvironment.isEmpty())
			return true;
		
		ITypeEnvironment.IIterator iterator = inferredEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			issueMarker(
					IMarkerDisplay.SEVERITY_ERROR, 
					formulaElement, 
					Messages.scuser_UntypedIdentifierError, 
					iterator.getName());
		}
		return false;
	}

		/**
	 * @param formulaElements the formula elements
	 * @param target the target static checked container
	 * @param formulas the array of successfully parsed formulas. The array must be of the length
	 * as <code>predicateElements</code> and all fields initialised to <code>null</code>.
	 * @param modules additional rules for the predicate elements
	 * @param component the name of the component that contains the predicate elements
	 * @param repository the state repository
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected void checkAndType(
			IInternalElement[] formulaElements,
			IInternalParent target,
			Formula[] formulas,
			IAcceptorModule[] modules,
			String component,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		assert formulaElements.length == formulas.length;
		
		final FormulaFactory factory = repository.getFormulaFactory();
		
		final ITypeEnvironment typeEnvironment = typingState.getTypeEnvironment();
		
		final Collection<FreeIdentifier> freeIdentifiers = 
			identifierSymbolTable.getFreeIdentifiers();
		
		createParsedState(repository);
		
		initAcceptorModules(modules, repository, null);
		
		for (int i=0; i<formulaElements.length; i++) {
			
			ILabelSymbolInfo symbolInfo = 
				fetchLabel(
					formulaElements[i], 
					component,
					null);
			
			boolean ok = parseFormula(
					i,
					formulaElements,
					formulas,
					freeIdentifiers,
					factory);
			
			if (ok) {
				
				ok = symbolInfo != null;
				
				setParsedState(formulas[i]);
			
				if (!acceptModules(modules, formulaElements[i], repository, null)) {
					// the predicate will be rejected
					// and will not contribute to the type environment!
					ok = false;
				}
				
				ITypeEnvironment inferredEnvironment = 
					typeCheckFormula(i, formulaElements, formulas, typeEnvironment);
				
				ok &= inferredEnvironment != null;
			
				if (ok && !inferredEnvironment.isEmpty()) {
					ok = updateIdentifierSymbolTable(
							formulaElements[i],
							inferredEnvironment, 
							typeEnvironment);
				}
			}
			
			if (!ok) {
				if (symbolInfo != null)
					symbolInfo.setError();
				formulas[i] = null;
			}
			
			if (symbolInfo != null)
				symbolInfo.setImmutable();
			
			makeProgress(monitor);
			
		}
		
		endAcceptorModules(modules, repository, null);
		
		removeParsedState(repository);
	}
	
	private IParsedFormula parsedFormula;
	
	private void createParsedState(IStateRepository repository) throws CoreException {
		parsedFormula = new ParsedFormula();
		repository.setState(parsedFormula);
	}
	
	private void setParsedState(Formula formula) throws CoreException {
		parsedFormula.setFormula(formula);
	}
	
	private void removeParsedState(IStateRepository repository) throws CoreException {
		repository.removeState(IParsedFormula.STATE_TYPE);
	}
	
	protected abstract void makeProgress(IProgressMonitor monitor);
	
}
