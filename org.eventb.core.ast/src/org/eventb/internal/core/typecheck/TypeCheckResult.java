package org.eventb.internal.core.typecheck;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.AbstractResult;

/**
 * This class implements the result of the type checker.
 * 
 * @author François Terrier
 *
 */
public class TypeCheckResult extends AbstractResult implements ITypeCheckResult {

	// Factory to use during type checking (for creating types).
	private FormulaFactory factory;
	
	// Initial type environment provided as input to type-check
	private TypeEnvironment initialTypeEnvironment;

	// Inferred type environment filled during type-check
	private TypeEnvironment inferredTypeEnvironment;

	// Type variables created during this type-check
	private List<TypeVariable> typeVariables = new ArrayList<TypeVariable>();
	
	private TypeUnifier unifier;
	
	/**
	 * Constructs the result with the specified initial type environment
	 * 
	 * @param typeEnvironment a type environment
	 */
	public TypeCheckResult(ITypeEnvironment typeEnvironment) {
		super();
		this.initialTypeEnvironment = (TypeEnvironment) typeEnvironment;
		this.factory = this.initialTypeEnvironment.ff;
		this.unifier = new TypeUnifier(this);
		this.inferredTypeEnvironment = (TypeEnvironment) factory.makeTypeEnvironment();
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
	public Type getIdentType(FreeIdentifier ident) {
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
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeCheckResult#getInferredEnvironment()
	 */
	public ITypeEnvironment getInferredEnvironment() {
		if (! isSuccess()) return null;
		return inferredTypeEnvironment;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeCheckResult#getInitialTypeEnvironment()
	 */
	public ITypeEnvironment getInitialTypeEnvironment() {
		return initialTypeEnvironment;
	}
	
	/**
	 * Returns the type unifier associated with this result.
	 * 
	 * @return the type unifier
	 */
	public TypeUnifier getUnifier() {
		return unifier;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ast.TypeFactory#makeBooleanType()
	 */
	public BooleanType makeBooleanType() {
		return factory.makeBooleanType();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ast.TypeFactory#makeGivenType(java.lang.String)
	 */
	public GivenType makeGivenType(String name) {
		return factory.makeGivenType(name);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ast.TypeFactory#makeIntegerType()
	 */
	public IntegerType makeIntegerType() {
		return factory.makeIntegerType();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.TypeFactory#makePowerSetType(org.eventb.core.ast.Type)
	 */
	public PowerSetType makePowerSetType(Type base) {
		return factory.makePowerSetType(base);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.TypeFactory#makeProductType(org.eventb.core.ast.Type, org.eventb.core.ast.Type)
	 */
	public ProductType makeProductType(Type left, Type right) {
		return factory.makeProductType(left, right);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.TypeFactory#makeRelationalType(org.eventb.core.ast.Type, org.eventb.core.ast.Type)
	 */
	public PowerSetType makeRelationalType(Type left, Type right) {
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
	public TypeVariable newFreshVariable(SourceLocation location) {
		TypeVariable tv = new TypeVariable(typeVariables.size(), location);
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
	public void solveTypeVariables() {
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
				// chercher les endroits faisant référence à cette typevariable et ajouter les erreurs
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
	 * @param location
	 *            the location of the expression that generated this equation
	 */
	public void unify(Type left, Type right, SourceLocation location) {
		unifier.unify(left,right,location);
	}
	
	public final FormulaFactory getFormulaFactory() {
		return factory;
	}

}
