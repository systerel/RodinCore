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
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.ParseProblem;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.state.ITypingState;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.ParsedFormula;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

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
			IStateRepository<IStateSC> repository, 
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
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		typingState = null;
		super.endModule(element, repository, monitor);
	}

	protected void issueASTProblemMarkers(
			IInternalElement element, 
			String attributeId, 
			IResult result) throws RodinDBException {
		
		for (ASTProblem parserProblem : result.getProblems()) {
			SourceLocation location = parserProblem.getSourceLocation();
			ProblemKind problemKind = parserProblem.getMessage();
			Object[] args = parserProblem.getArgs();
			
			IRodinProblem problem;
			Object[] objects; // parameters for the marker
			
			switch (problemKind) {
			
			case FreeIdentifierHasBoundOccurences:
				problem = ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
				objects = new Object[] {
					args[0]
				};				
				break;
				
			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				problem = ParseProblem.BoundIdentifierIsAlreadyBoundWarning;
				objects = new Object[] {
					args[0]
				};
				break;
				
			case BoundIdentifierIndexOutOfBounds:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = new Object[0];
				break;
				
			case InvalidTypeExpression:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case LexerError:
				problem = ParseProblem.LexerError;
				objects = new Object[] {
						args[0]
				};			
				break;
				
			case LexerException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case ParserException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case SyntaxError:
				
				// TODO: prepare detailed error messages "args[0]" obtained from the parser for 
				//       internationalisation
				
				problem = ParseProblem.SyntaxError;
				objects = new Object[] {
						args[0]
				};						
				break;
				
			case TypeCheckFailure:
				problem = ParseProblem.TypeCheckError;
				objects = new Object[0];			
				break;
				
			case TypesDoNotMatch:
				problem = ParseProblem.TypesDoNotMatchError;
				objects = new Object[] {
						args[0],
						args[1]
				};						
				break;
				
			case TypeUnknown:
				problem = ParseProblem.TypeUnknownError;
				objects = new Object[0];			
				break;
				
			default:
				
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				
				break;
			}
			
			if (location == null) {
				createProblemMarker(element, attributeId, problem, objects);
			} else {	
				createProblemMarker(
						element, attributeId, 
						location.getStart(), 
						location.getEnd(), problem, objects);
			}
		}
	}

	/**
	 * @param formulaElement the formula element
	 * @param freeIdentifierContext the free identifier context of this predicate
	 * (@see org.eventb.core.ast.Formula#isLegible(Collection))
	 * @param factory the formula factory to use 
	 * @return parsed formula, iff the formula was successfully parsed, <code>null</code> otherwise
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected abstract Formula parseFormula(
			IInternalElement formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException;
	
	/**
	 * @param formulaElement the formula element
	 * @param formula the parsed formula
	 * @return the inferred type environment
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected ITypeEnvironment typeCheckFormula(
			IInternalElement formulaElement,
			Formula formula,
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		ITypeCheckResult typeCheckResult = formula.typeCheck(typeEnvironment);
		
		if (!typeCheckResult.isSuccess()) {
			issueASTProblemMarkers(formulaElement, getFormulaAttributeId(), typeCheckResult);
			
			return null;
		}
		
		return typeCheckResult.getInferredEnvironment();

	}
	
	protected abstract String getFormulaAttributeId();

	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		if (inferredEnvironment.isEmpty())
			return true;
		
		ITypeEnvironment.IIterator iterator = inferredEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			createProblemMarker(
					formulaElement, 
					getFormulaAttributeId(), 
					GraphProblem.UntypedIdentifierError, 
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
			IStateRepository<IStateSC> repository,
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
			
			formulas[i] = parseFormula(
					formulaElements[i],
					freeIdentifiers,
					factory);
			
			boolean ok = formulas[i] != null;
			
			if (ok) {
				
				ok = symbolInfo != null;
				
				setParsedState(formulas[i]);
			
				if (!acceptModules(modules, formulaElements[i], repository, null)) {
					// the predicate will be rejected
					// and will not contribute to the type environment!
					ok = false;
				}
				
				ITypeEnvironment inferredEnvironment = 
					typeCheckFormula(formulaElements[i], formulas[i], typeEnvironment);
				
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
			
			setImmutable(symbolInfo);
			
			makeProgress(monitor);
			
		}
		
		endAcceptorModules(modules, repository, null);
		
		removeParsedState(repository);
	}

	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		if (symbolInfo != null)
			symbolInfo.setImmutable();
	}
	
	private IParsedFormula parsedFormula;
	
	private void createParsedState(IStateRepository<IStateSC> repository) throws CoreException {
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
