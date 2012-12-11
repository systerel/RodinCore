/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - ensure that all AST problems are reported
 *     Systerel - ensure that all AST problems are reported
 *     Systerel - got factory from repository
 *     Systerel - adapted to parser 2.0 problem kinds
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.ParseProblem;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.ParsedFormula;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
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
	protected ILabelSymbolInfo[] symbolInfos;

	private final static Object[] NO_OBJECT = new Object[0];

	private IAccuracyInfo accuracyInfo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		accuracyInfo = getAccuracyInfo(repository);

		formulaElements = getFormulaElements(element);

		formulas = allocateFormulas(formulaElements.length);
		
		symbolInfos = new ILabelSymbolInfo[formulaElements.length];
	}

	protected abstract IAccuracyInfo getAccuracyInfo(
			ISCStateRepository repository) throws CoreException;

	protected abstract F[] allocateFormulas(int size);

	protected abstract I[] getFormulaElements(IRodinElement element)
			throws CoreException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		accuracyInfo = null;
		formulaElements = null;
		formulas = null;
		symbolInfos = null;
		super.endModule(element, repository, monitor);
	}

	// Returns whether an error was issued
	protected boolean issueASTProblemMarkers(IInternalElement element,
			IAttributeType.String attributeType, IResult result)
			throws RodinDBException {

		boolean errorIssued = false;
		for (ASTProblem parserProblem : result.getProblems()) {
			final SourceLocation location = parserProblem.getSourceLocation();
			final ProblemKind problemKind = parserProblem.getMessage();
			final Object[] args = parserProblem.getArgs();

			final IRodinProblem problem;
			final Object[] objects; // parameters for the marker

			switch (problemKind) {

			case FreeIdentifierHasBoundOccurences:
				problem = ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
				objects = new Object[] { args[0] };
				break;

			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to
				// FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				problem = ParseProblem.BoundIdentifierIsAlreadyBoundWarning;
				objects = new Object[] { args[0] };
				break;

			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = NO_OBJECT;
				break;

			case InvalidTypeExpression:
				problem = ParseProblem.InvalidTypeExpressionError;
				objects = NO_OBJECT;
				break;

			case LexerError:
				problem = ParseProblem.LexerError;
				objects = new Object[] { args[0] };
				break;

			case TypeCheckFailure:
				problem = ParseProblem.TypeCheckError;
				objects = NO_OBJECT;
				break;

			case TypesDoNotMatch:
				problem = ParseProblem.TypesDoNotMatchError;
				objects = new Object[] { args[0], args[1] };
				break;

			case TypeUnknown:
				problem = ParseProblem.TypeUnknownError;
				objects = NO_OBJECT;
				break;

			case MinusAppliedToSet:
				problem = ParseProblem.MinusAppliedToSetError;
				objects = NO_OBJECT;
				break;

			case MulAppliedToSet:
				problem = ParseProblem.MulAppliedToSetError;
				objects = NO_OBJECT;
				break;

			// syntax errors
			case BECMOAppliesToOneIdent:
			case DuplicateIdentifierInPattern:
			case ExtensionPreconditionError:
			case FreeIdentifierExpected:
			case IncompatibleIdentExprNumbers:
			case IncompatibleOperators:
			case IntegerLiteralExpected:
			case InvalidAssignmentToImage:
			case InvalidGenericType:
			case MisplacedLedOperator:
			case MisplacedNudOperator:
			case NotUpgradableError:
			case PredicateVariableNotAllowed:
			case PrematureEOF:
			case UnexpectedOftype:
			case UnexpectedSubFormulaKind:
			case UnexpectedSymbol:
			case UnknownOperator:
			case UnmatchedTokens:
			case VariousPossibleErrors:

				problem = ParseProblem.SyntaxError;
				
				objects = new Object[] { parserProblem.toString() };
				break;
			default:

				problem = ParseProblem.InternalError;
				objects = NO_OBJECT;

				break;
			}

			if (location == null) {
				createProblemMarker(element, attributeType, problem, objects);
			} else {
				createProblemMarker(element, attributeType,
						location.getStart(), location.getEnd(), problem,
						objects);
			}

			errorIssued |= problem.getSeverity() == IMarker.SEVERITY_ERROR; 
		}

		return errorIssued;
	}

	/**
	 * @param formulaElement
	 *            the formula element
	 * @param freeIdentifierContext
	 *            the free identifier context of this predicate (@see
	 *            org.eventb.core.ast.Formula#isLegible(Collection))
	 * @param factory
	 *            the formula factory to use
	 * @return parsed formula, iff the formula was successfully parsed,
	 *         <code>null</code> otherwise
	 * @throws CoreException
	 *             if there was a problem accessing the database or the symbol
	 *             table
	 */
	protected abstract F parseFormula(I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException;

	/**
	 * @param formulaElement
	 *            the formula element
	 * @param formula
	 *            the parsed formula
	 * @return the inferred type environment
	 * @throws CoreException
	 *             if there was a problem accessing the database or the symbol
	 *             table
	 */
	protected ITypeEnvironment typeCheckFormula(I formulaElement, F formula,
			ITypeEnvironment environment) throws CoreException {

		ITypeCheckResult typeCheckResult = formula.typeCheck(environment);

		if (issueASTProblemMarkers(formulaElement, getFormulaAttributeType(),
				typeCheckResult)) {
			return null;
		}

		return typeCheckResult.getInferredEnvironment();

	}

	protected abstract IAttributeType.String getFormulaAttributeType();

	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment, ITypeEnvironmentBuilder environment)
			throws CoreException {

		if (inferredEnvironment.isEmpty())
			return true;

		ITypeEnvironment.IIterator iterator = inferredEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			createProblemMarker(formulaElement, getFormulaAttributeType(),
					GraphProblem.UntypedIdentifierError, iterator.getName());
		}
		return false;
	}

	/**
	 * @param component
	 *            the name of the component that contains the predicate elements
	 * @param repository
	 *            the state repository
	 * @throws CoreException
	 *             if there was a problem accessing the database or the symbol
	 *             table
	 */
	protected void checkAndType(String component,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		final FormulaFactory factory = repository.getFormulaFactory();

		final ITypeEnvironmentBuilder typeEnvironment = repository
				.getTypeEnvironment();
				
		final Collection<FreeIdentifier> freeIdentifiers = identifierSymbolTable
				.getFreeIdentifiers();

		createParsedState(repository);

		initFilterModules(repository, null);

		for (int i = 0; i < formulaElements.length; i++) {

			I formulaElement = formulaElements[i];

			ILabelSymbolInfo symbolInfo = fetchLabel(formulaElement, component,
					null);

			F formula = parseFormula(formulaElement, freeIdentifiers, factory);

			formulas[i] = formula;

			boolean ok = formula != null;

			if (ok) {

				ok = symbolInfo != null;

				setParsedState(formula);

				if (ok && !filterModules(formulaElement, repository, null)) {
					// the predicate will be rejected
					// and will not contribute to the type environment!
					ok = false;
				}

				if (ok) {
					ITypeEnvironment inferredEnvironment = typeCheckFormula(
							formulaElement, formula, typeEnvironment);

					ok &= inferredEnvironment != null;

					if (ok && !inferredEnvironment.isEmpty()) {
						ok = updateIdentifierSymbolTable(formulaElement,
								inferredEnvironment, typeEnvironment);
					}
				}
			}

			if (!ok) {
				if (symbolInfo != null)
					symbolInfo.setError();
				formulas[i] = null;
				if (accuracyInfo != null)
					accuracyInfo.setNotAccurate();
			}
			
			symbolInfos[i] = symbolInfo;

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

	private void createParsedState(ISCStateRepository repository)
			throws CoreException {
		parsedFormula = new ParsedFormula();
		repository.setState(parsedFormula);
	}

	private void setParsedState(Formula<?> formula) throws CoreException {
		parsedFormula.setFormula(formula);
	}

	private void removeParsedState(ISCStateRepository repository)
			throws CoreException {
		repository.removeState(IParsedFormula.STATE_TYPE);
	}

	protected abstract void makeProgress(IProgressMonitor monitor);

}
