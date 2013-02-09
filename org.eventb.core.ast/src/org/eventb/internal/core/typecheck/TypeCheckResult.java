/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import static org.eventb.internal.core.ast.GivenTypeHelper.isGivenSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.ast.AbstractResult;

/**
 * This class implements most of the algorithm of the type checker and provides
 * the result to clients.
 * <p>
 * Type-checking is implemented in two passes. First, traverse the formula with
 * <code>Formula.typeCheck(TypeCheckResult, BoundIdentDecl[])</code>, creating
 * fresh type variables and registering type equations. Then, attempt to solve
 * all type variables by calling {@link #solveTypeVariables()}, then traverse
 * again the formula with <code>Formula.solveType(TypeUnifier)</code>, storing
 * solved types or <code>null</code> if the type could not be inferred.
 * </p>
 * 
 * @author François Terrier
 */
public class TypeCheckResult extends AbstractResult implements ITypeCheckResult {

	// Factory to use during type checking (for creating types).
	private final FormulaFactory factory;
	
	// Initial type environment provided as input to type-check
	private final SealedTypeEnvironment initialTypeEnvironment;

	// Inferred type environment filled during type-check
	private final InferredTypeEnvironment inferredTypeEnvironment;

	// Type variables created during this type-check
	private final List<TypeVariable> typeVariables;
	
	private final TypeUnifier unifier;
	
	/**
	 * Constructs the result with the specified initial type environment
	 * 
	 * @param typeEnvironment a type environment
	 */
	public TypeCheckResult(ISealedTypeEnvironment typeEnvironment) {
		super();
		this.initialTypeEnvironment = (SealedTypeEnvironment) typeEnvironment;
		this.factory = this.initialTypeEnvironment.getFormulaFactory();
		this.unifier = new TypeUnifier(this);
		this.inferredTypeEnvironment = new InferredTypeEnvironment(
				this.initialTypeEnvironment);
		this.typeVariables = new ArrayList<TypeVariable>();
	}

	/**
	 * Returns the type associated to the given free identifier.
	 * <p>
	 * If the identifier is not registered in any environment, creates a type
	 * variable for its type.
	 * </p>
	 * 
	 * @param ident
	 *            the free identifier to lookup
	 * @return the type associated to the given identifier
	 */
	public final Type getIdentType(FreeIdentifier ident) {
		String name = ident.getName();
		Type result = initialTypeEnvironment.getType(name); 
		if (result != null) {
			return result;
		}
		result = inferredTypeEnvironment.getType(name); 
		if (result != null) {
			return result;
		}
		result = newFreshVariable(ident.getSourceLocation());
		inferredTypeEnvironment.addName(name, result);
		return result;
	}
	
	@Override
	public final IInferredTypeEnvironment getInferredEnvironment() {
		if (! isSuccess()) return null;
		return inferredTypeEnvironment;
	}

	@Override
	public final ITypeEnvironment getInitialTypeEnvironment() {
		return initialTypeEnvironment;
	}
	
	/**
	 * Returns the type unifier associated with this result.
	 * 
	 * @return the type unifier
	 */
	public final TypeUnifier getUnifier() {
		return unifier;
	}
	
	public final BooleanType makeBooleanType() {
		return factory.makeBooleanType();
	}
	
	public ParametricType makeParametricType(Type[] typePrms,
			IExpressionExtension exprExt) {
		return factory.makeParametricType(typePrms, exprExt);
	}

	public final GivenType makeGivenType(String name) {
		return factory.makeGivenType(name);
	}
	
	public final IntegerType makeIntegerType() {
		return factory.makeIntegerType();
	}

	public final PowerSetType makePowerSetType(Type base) {
		return factory.makePowerSetType(base);
	}

	public final ProductType makeProductType(Type left, Type right) {
		return factory.makeProductType(left, right);
	}

	public final PowerSetType makeRelationalType(Type left, Type right) {
		return factory.makeRelationalType(left, right);
	}

	/**
	 * Returns a new fresh variable with the corresponding source
	 * location. The source location is only specified for terminal
	 * type variable, i.e. type variables on terminal nodes.
	 * 
	 * @param location the location of the type variable's corresponding symbol
	 * @return a type variable
	 */
	public final TypeVariable newFreshVariable(SourceLocation location) {
		TypeVariable tv = new TypeVariable(this.factory, typeVariables.size(), location);
		typeVariables.add(tv);
		return tv;
	}

	/**
	 * Solves the type variables.getTypeEnvironment
	 * <p>
	 * Every type variable created during this type-check will be solved to a
	 * fully solved type (that is a type that do not contain any type variable).
	 * If the type variable can not be solved, it adds the corresponding problem
	 * and makes this result fail.
	 */
	public final void solveTypeVariables() {
		if (! isSuccess()) {
			return;
		}
		BitSet errorReported = new BitSet(typeVariables.size());
		boolean failed = false;
		for (int i = 0; i < typeVariables.size(); i++) {
			final TypeVariable tvi = typeVariables.get(i);
			if (tvi.getValue() == null) {
				failed = true;
				if (!errorReported.get(i) && tvi.hasSourceLocation()) {
					errorReported.set(i);
					addProblem(new ASTProblem(
							tvi.getSourceLocation(), 
							ProblemKind.TypeUnknown, ProblemSeverities.Error));
				}
				// Lookup for places where this type variable is referenced and
				// add corresponding errors.
				for (int j = 0; j < typeVariables.size(); j++) {
					final TypeVariable tvj = typeVariables.get(j);
					if (!errorReported.get(j) && 
							tvj.hasSourceLocation() &&
							unifier.occurs(tvi, tvj.getValue())) {
						// set true, to remaind us that we have already recorded an error message for this variable
						errorReported.set(j);
						addProblem(new ASTProblem(
								tvj.getSourceLocation(),
								ProblemKind.TypeUnknown, ProblemSeverities.Error));
					}
				}
			}
		}
		if (failed && isSuccess()) {
			// Couldn't root any error message on a source location
			addProblem(new ASTProblem(
					null, ProblemKind.TypeCheckFailure, ProblemSeverities.Error
			));
		}
		
		if (!isSuccess()) {
			return;
		}
		// it's time to solve the type environment
		inferredTypeEnvironment.solveVariables(unifier);
	}

	/**
	 * Adds the specified type equation (left \u2263 right) in the type equation
	 * list and unifies it with the existing type equations. A problem is added
	 * to the problem list if this equation tries to insert a conflicting type
	 * equation.
	 * 
	 * @param left
	 *            the type on the left hand side of the equation
	 * @param right
	 *            the type on the right hand side of the equation
	 * @param origin
	 *            the formula that generated this equation
	 */
	public final <T extends Formula<?>> void unify(Type left, Type right, T origin) {
		unifier.unify(left, right, origin);
	}
	
	public final FormulaFactory getFormulaFactory() {
		return factory;
	}

	public final <T extends Formula<?>> void addUnificationProblem(Type left,
			Type right, T origin) {
		final SourceLocation loc = origin.getSourceLocation();
		final int tag = origin.getTag();
		ASTProblem problem = null;
		// Special case for setminus and cartesian product
		if (left instanceof PowerSetType) { 
			if (tag == Formula.MINUS) {
				problem = new ASTProblem(
						loc,
						ProblemKind.MinusAppliedToSet,
						ProblemSeverities.Error);
			}
			else if (tag == Formula.MUL) {
				problem = new ASTProblem(
						loc,
						ProblemKind.MulAppliedToSet,
						ProblemSeverities.Error);
			}
		}
		if (problem == null) {
			// Default case with a generic error message
			problem = new ASTProblem(
					loc,
					ProblemKind.TypesDoNotMatch,
					ProblemSeverities.Error,
					left,
					right);
		}
		addProblem(problem);
	}
	
	/**
	 * Analyze the expression type to check that given sets introduced are
	 * present in the initial or inferred environment and otherwise to check
	 * their compatibility with existent environments and add them in the
	 * inferred environment.
	 * 
	 * @param expr
	 *            the expression for which the type will be checked
	 */
	public void analyzeExpression(Expression expr) {
		analyzeType(expr.getType(), expr);
	}

	/**
	 * Analyze the type to check that given sets introduced are present in the
	 * initial or inferred environment and otherwise to check their
	 * compatibility with existent environments and add them in the inferred
	 * environment.
	 * 
	 * @param type
	 *            the type that will be checked
	 * @param source
	 *            the formula from which the type has been extracted
	 */
	public void analyzeType(Type type, Formula<?> source) {
		for (GivenType given : type.getGivenTypes()) {
			add(given, source);
		}
	}

	// Check that type is compatible with type environments and add it to
	// inferred type environment if necessary
	private void add(GivenType type, Formula<?> source) {
		if (!checkGivenType(type, initialTypeEnvironment, source)
				&& !checkGivenType(type, inferredTypeEnvironment, source)) {
			inferredTypeEnvironment.addGivenSet(type.getName());
		}
	}

	// Returns true if the given type was found in the given type environment
	private boolean checkGivenType(GivenType type, TypeEnvironment typenv,
			Formula<?> source) {
		final String name = type.getName();
		final Type otherType = typenv.getType(name);
		if (otherType == null) {
			return false;
		}
		if (isGivenSet(name, otherType)) {
			// Already present as given type
			return true;
		}
		if (otherType instanceof TypeVariable) {
			// Register that this name must denote a given type
			final FormulaFactory ff = inferredTypeEnvironment
					.getFormulaFactory();
			unify(otherType, ff.makePowerSetType(type), source);
			return true;
		}
		// The name cannot denote a given type
		addProblem(new ASTProblem(source.getSourceLocation(),
				ProblemKind.TypeNameUsedForRegularIdentifier,
				ProblemSeverities.Error, name, otherType));
		return true;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("{ problems = {");
		str.append(super.toString());
		str.append("}");
		for (final TypeVariable v: typeVariables) {
			str.append("\n  ");
			str.append(v.toString());
			str.append("=");
			final Type val = v.getValue();
			str.append(val == null ? "?" : val);
		}
		str.append("}");
		return str.toString();
	}

}
