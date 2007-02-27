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
import org.eventb.core.sc.ParseProblem;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.internal.core.sc.ParsedFormula;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class LabeledFormulaModule<F extends Formula<F>, I extends IInternalElement> 
extends LabeledElementModule {

	protected IIdentifierSymbolTable identifierSymbolTable;
	
	protected I[] formulaElements;
	protected F[] formulas;
	
	private final static Object[] NO_OBJECT = new Object[0];
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		identifierSymbolTable = 
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		formulaElements = getFormulaElements(element);

		formulas = allocateFormulas(formulaElements.length);
	}

	protected abstract F[] allocateFormulas(int size);
	
	protected abstract I[] getFormulaElements(IRodinElement element) throws CoreException;

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		super.endModule(element, repository, monitor);
	}

	protected void issueASTProblemMarkers(
			IInternalElement element, 
			IAttributeType.String attributeType, 
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
				objects = NO_OBJECT;
				break;
				
			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = NO_OBJECT;
				break;
				
			case InvalidTypeExpression:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
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
				objects = NO_OBJECT;
				break;
				
			case ParserException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
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
				objects = NO_OBJECT;			
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
				objects = NO_OBJECT;			
				break;
				
			default:
				
				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;
				
				break;
			}
			
			if (location == null) {
				createProblemMarker(element, attributeType, problem, objects);
			} else {	
				createProblemMarker(
						element, attributeType, 
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
	protected abstract F parseFormula(
			I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException;
	
	/**
	 * @param formulaElement the formula element
	 * @param formula the parsed formula
	 * @return the inferred type environment
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected ITypeEnvironment typeCheckFormula(
			I formulaElement,
			F formula,
			ITypeEnvironment environment) throws CoreException {
		
		ITypeCheckResult typeCheckResult = formula.typeCheck(environment);
		
		if (!typeCheckResult.isSuccess()) {
			issueASTProblemMarkers(formulaElement, getFormulaAttributeType(), typeCheckResult);
			
			return null;
		}
		
		return typeCheckResult.getInferredEnvironment();

	}
	
	protected abstract IAttributeType.String getFormulaAttributeType();

	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment, 
			ITypeEnvironment environment) throws CoreException {
		
		if (inferredEnvironment.isEmpty())
			return true;
		
		ITypeEnvironment.IIterator iterator = inferredEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			createProblemMarker(
					formulaElement, 
					getFormulaAttributeType(), 
					GraphProblem.UntypedIdentifierError, 
					iterator.getName());
		}
		return false;
	}

	/**
	 * @param target the target static checked container
	 * @param component the name of the component that contains the predicate elements
	 * @param repository the state repository
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected void checkAndType(
			IInternalParent target,
			String component,
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		final FormulaFactory factory = repository.getFormulaFactory();
		
		final ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		
		final Collection<FreeIdentifier> freeIdentifiers = 
			identifierSymbolTable.getFreeIdentifiers();
		
		createParsedState(repository);
		
		initFilterModules(repository, null);
		
		for (int i=0; i<formulaElements.length; i++) {
			
			I formulaElement = formulaElements[i];
			
			ILabelSymbolInfo symbolInfo = 
				fetchLabel(
					formulaElement, 
					component,
					null);
			
			F formula = parseFormula(
					formulaElement,
					freeIdentifiers,
					factory);
			
			formulas[i] = formula;
			
			boolean ok = formula != null;
			
			if (ok) {
				
				ok = symbolInfo != null;
				
				setParsedState(formula);
			
				if (!filterModules(formulaElement, repository, null)) {
					// the predicate will be rejected
					// and will not contribute to the type environment!
					ok = false;
				}
				
				ITypeEnvironment inferredEnvironment = 
					typeCheckFormula(formulaElement, formula, typeEnvironment);
				
				ok &= inferredEnvironment != null;
			
				if (ok && !inferredEnvironment.isEmpty()) {
					ok = updateIdentifierSymbolTable(
							formulaElement,
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
		
		endFilterModules(repository, null);
		
		removeParsedState(repository);
	}

	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		if (symbolInfo != null)
			symbolInfo.makeImmutable();
	}
	
	private ParsedFormula parsedFormula;
	
	private void createParsedState(ISCStateRepository repository) throws CoreException {
		parsedFormula = new ParsedFormula();
		repository.setState(parsedFormula);
	}
	
	private void setParsedState(Formula formula) throws CoreException {
		parsedFormula.setFormula(formula);
	}
	
	private void removeParsedState(ISCStateRepository repository) throws CoreException {
		repository.removeState(IParsedFormula.STATE_TYPE);
	}
	
	protected abstract void makeProgress(IProgressMonitor monitor);
	
}
