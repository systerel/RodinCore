/*******************************************************************************
 * Copyright (c) 2005, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added origin
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added support for mathematical extensions
 *     Systerel - added support for specialization
 *     Systerel - store factory used to build a formula or type
 *******************************************************************************/
package org.eventb.core.ast;

import static java.util.Collections.emptyMap;
import static org.eventb.core.ast.Formula.NO_TAG;
import static org.eventb.internal.core.ast.FactoryHelper.toBIDArray;
import static org.eventb.internal.core.ast.FactoryHelper.toExprArray;
import static org.eventb.internal.core.ast.FactoryHelper.toIdentArray;
import static org.eventb.internal.core.ast.FactoryHelper.toPredArray;
import static org.eventb.internal.core.ast.FactoryHelper.toTypeArray;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.IDENT;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.PRED_VAR;
import static org.eventb.internal.core.parser.BMathV1.B_MATH_V1;
import static org.eventb.internal.core.parser.BMathV2.B_MATH_V2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IGrammar;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPredicateExtension2;
import org.eventb.core.ast.extension.ITypeAnnotation;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.datatype.DatatypeBuilder;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.internal.core.ast.extension.ExtnUnicityChecker;
import org.eventb.internal.core.lexer.GenLexer;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.ExtendedGrammar;
import org.eventb.internal.core.parser.GenParser;
import org.eventb.internal.core.parser.ParseResult;
import org.eventb.internal.core.typecheck.TypeEnvironmentBuilder;
import org.eventb.internal.core.upgrade.UpgradeResult;
import org.eventb.internal.core.upgrade.VersionUpgrader;

/**
 * This class is the factory class for all the AST nodes of an event-B formula.
 * <p>
 * Use this class to instantiate all types of AST nodes used in an event-B
 * formula. Tags used in this method are the constants defined in class {@link Formula}.
 * </p>
 *  
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class FormulaFactory {

	/*
	 * IMPLEMENTATION NOTES:
	 *
	 * We use a cache to ensure that each instance of this class has a unique
	 * set of extensions. Therefore, equality of factories is reduced to
	 * identity of references and we do not override the equals() and hashcode()
	 * methods inherited from Object.
	 */

	private static final Expression[] NO_EXPRESSIONS = new Expression[0];

	private static final Map<IFormulaExtension, Integer> ALL_EXTENSIONS = Collections
			.synchronizedMap(new HashMap<IFormulaExtension, Integer>());

	private static final Map<Set<IFormulaExtension>, FormulaFactory> INSTANCE_CACHE = new HashMap<Set<IFormulaExtension>, FormulaFactory>();
	
	private static final ExtnUnicityChecker EXTN_UNICITY_CHECKER = new ExtnUnicityChecker(B_MATH_V2);

	private static final FormulaFactory DEFAULT_INSTANCE = getInstance(Collections
			.<IFormulaExtension> emptySet());

	private static final FormulaFactory V1_INSTANCE = new FormulaFactory(
			B_MATH_V1);
	
	private static volatile int nextExtensionTag = Formula.FIRST_EXTENSION_TAG;
	
	// tags of extensions managed by this formula factory
	private final Map<Integer, IFormulaExtension> extensions;
	
	private final BMath grammar;
	
	/**
	 * Returns the default instance of the formula factory which corresponds to
	 * the current version of the mathematical language.
	 * 
	 * @return the default instance of this class for language V2
	 */
	public static FormulaFactory getDefault() {
		return DEFAULT_INSTANCE;
	}

	/**
	 * Returns the default instance of the formula factory for the obsolete
	 * version V1 of the mathematical language.
	 * 
	 * @return the single instance of this class for language V1
	 * @since 2.7
	 */
	public static FormulaFactory getV1Default() {
		return V1_INSTANCE;
	}

	/**
	 * Returns an instance of a formula factory supporting the given extensions
	 * (and only them) in addition to the regular event-B mathematical language.
	 * The given set of extensions must contain all extensions of any datatype
	 * present, including the datatype dependencies (i.e., other datatypes
	 * directly or indirectly referenced in the datatype definitions).
	 * 
	 * @param extensions
	 *            set of mathematical extensions to support
	 * @return a formula factory supporting the given extensions
	 * @throws IllegalArgumentException
	 *             if the given set of extensions is not datatype complete
	 * @throws IllegalArgumentException
	 *             if one of the given extensions is a type constructor that
	 *             does not fulfill its contract
	 * @since 2.0
	 * @since 3.0
	 * @see IExpressionExtension#isATypeConstructor()
	 */
	public static FormulaFactory getInstance(Set<IFormulaExtension> extensions) {
		final Set<IFormulaExtension> actualExtns;
		actualExtns = new LinkedHashSet<IFormulaExtension>(extensions);

		synchronized (ALL_EXTENSIONS) {
			final FormulaFactory cached = INSTANCE_CACHE.get(actualExtns);
			if (cached != null) {
				return cached;
			}
			checkSymbols(actualExtns);
			checkDatatypeComplete(actualExtns);
			checkTypeConstructorArity(actualExtns);
			EXTN_UNICITY_CHECKER.checkUnicity(actualExtns);
			final Map<Integer, IFormulaExtension> extMap = new LinkedHashMap<Integer, IFormulaExtension>();
			for (IFormulaExtension extension : actualExtns) {
				Integer tag = ALL_EXTENSIONS.get(extension);
				if (tag == null) {
					tag = nextExtensionTag;
					nextExtensionTag++;
					ALL_EXTENSIONS.put(extension, tag);
				}
				extMap.put(tag, extension);
			}
			final FormulaFactory factory = new FormulaFactory(extMap);
			INSTANCE_CACHE.put(actualExtns, factory);
			return factory;
		}
	}

	/**
	 * Returns an instance of a formula factory supporting the given extensions
	 * (and only them) in addition to the regular event-B mathematical language.
	 * The given set of extensions must contain all extensions of any datatype
	 * present, including the datatype dependencies (i.e., other datatypes
	 * directly or indirectly referenced in the datatype definitions).
	 * 
	 * @param extensions
	 *            the mathematical extensions to support
	 * @return a formula factory supporting the given extensions
	 * @throws IllegalArgumentException
	 *             if the given set of extensions is not datatype complete
	 * @throws IllegalArgumentException
	 *             if one of the given extensions is a type constructor that
	 *             does not fulfill its contract
	 * @since 2.3
	 * @since 3.0
	 * @see IExpressionExtension#isATypeConstructor()
	 */
	public static FormulaFactory getInstance(IFormulaExtension... extensions) {
		return getInstance(new LinkedHashSet<IFormulaExtension>(
				Arrays.asList(extensions)));
	}

	private static void checkDatatypeComplete(Set<IFormulaExtension> extns) {
		final Set<IDatatype> datatypes = getDatatypes(extns);
		for (final IDatatype datatype : datatypes) {
			if (!extns.containsAll(datatype.getExtensions())) {
				throw new IllegalArgumentException("Incomplete datatype "
						+ datatype);
			}
			if (!extns.containsAll(datatype.getBaseFactory().getExtensions())) {
				throw new IllegalArgumentException(
						"Missing dependencies for datatype " + datatype);
			}
		}
	}

	private static Set<IDatatype> getDatatypes(Set<IFormulaExtension> extns) {
		final Set<IDatatype> result = new HashSet<IDatatype>();
		for (final IFormulaExtension extn : extns) {
			final Object origin = extn.getOrigin();
			if (origin instanceof IDatatype) {
				result.add((IDatatype) origin);
			}
		}
		return result;
	}

	private static void checkTypeConstructorArity(Set<IFormulaExtension> extns) {
		for (final IFormulaExtension extn : extns) {
			if (!(extn instanceof IExpressionExtension)) {
				continue;
			}
			final IExpressionExtension exprExtn = (IExpressionExtension) extn;
			if (!exprExtn.isATypeConstructor()) {
				continue;
			}
			final IExtensionKind kind = exprExtn.getKind();
			final IOperatorProperties props = kind.getProperties();
			if (props.getChildTypes().getPredArity().getMax() != 0) {
				throw new IllegalArgumentException(
						"Type constructor takes predicate parameters "
								+ exprExtn.getId());
			}
		}
	}

	private static void checkSymbols(Set<IFormulaExtension> actualExtns) {
		for (IFormulaExtension extn : actualExtns) {
			final String symbol = extn.getSyntaxSymbol();
			if (!checkSymbol(symbol)) {
				throw new IllegalArgumentException("Invalid symbol '" + symbol
						+ "' in extension " + extn.getId());
			}
		}
	}

	/**
	 * Checks that a symbol is appropriate for the generic scanner. Such a
	 * symbol must either be an identifier, or must be not empty and purely
	 * symbolic (i.e., not contain any character allowable in an identifier).
	 * 
	 * Identifier symbols with a prime are rejected by this method.
	 *
	 * Note that the symbol is tested with the generic scanner. This method does not
	 * check if the symbol is used by the extensions of a particular factory. To
	 * check this, use {@link #isValidExtensionSymbol(String)}.
	 *
	 * @param symbol
	 *            symbol to test
	 * @return <code>true</code> iff the given symbol is valid
	 * @since 2.0
	 * @deprecated use {@link #isValidExtensionSymbol(String)} instead
	 */
	@Deprecated(since="3.8")
	public static boolean checkSymbol(String symbol) {
		if (GenLexer.isIdent(symbol)) {
			return true;
		}
		return !symbol.isEmpty() && GenLexer.isSymbol(symbol);
	}

	/**
	 * Returns a new formula factory that supports the extensions of this
	 * factory, together with the given extensions.
	 * <p>
	 * Adding already supported extensions has no effect. The resulting
	 * supported extensions are a union of known extensions and given
	 * extensions.
	 * </p>
	 * <p>
	 * The resulting set of extensions must contain all extensions of any
	 * datatype present, including the datatype dependencies (i.e., other
	 * datatypes directly or indirectly referenced in the datatype definitions).
	 * </p>
	 * 
	 * @param addedExtns
	 *            a set of extensions to add
	 * @return a new factory supporting additional extensions
	 * @throws IllegalArgumentException
	 *             if the resulting set of extensions is not datatype complete
	 * @since 2.0
	 * @since 3.0
	 */
	public FormulaFactory withExtensions(Set<IFormulaExtension> addedExtns) {
		final Set<IFormulaExtension> newExtns = new LinkedHashSet<IFormulaExtension>(
				extensions.values());
		newExtns.addAll(addedExtns);
		return getInstance(newExtns);
	}

	/**
	 * Returns a new datatype builder for the datatype with the given name and
	 * type parameters. The type parameters are specified as given types.
	 * 
	 * @param name
	 *            the datatype name
	 * @param parameters
	 *            the formal type parameters of the datatype
	 * @return a datatype builder
	 * @throws IllegalArgumentException
	 *             if the datatype name is not a valid identifier in this
	 *             factory
	 * @throws IllegalArgumentException
	 *             if the type parameters names are in conflict with each other
	 *             or with the datatype name
	 * @throws IllegalArgumentException
	 *             if one of the type parameters was built with another factory
	 * @since 3.0
	 */
	public IDatatypeBuilder makeDatatypeBuilder(String name,
			List<GivenType> parameters) {
		return new DatatypeBuilder(this, name, parameters, null);
	}

	/**
	 * Returns a new datatype builder for the datatype with the given name and
	 * type parameters. The type parameters are specified as given types.
	 * 
	 * @param name
	 *            the datatype name
	 * @param parameters
	 *            the formal type parameters of the datatype
	 * @return a datatype builder
	 * @throws IllegalArgumentException
	 *             if the datatype name is not a valid identifier in this
	 *             factory
	 * @throws IllegalArgumentException
	 *             if the type parameters names are in conflict with each other
	 *             or with the datatype name
	 * @throws IllegalArgumentException
	 *             if one of the type parameters was built with another factory
	 * @since 3.0
	 */
	public IDatatypeBuilder makeDatatypeBuilder(String name,
			GivenType... parameters) {
		return new DatatypeBuilder(this, name, Arrays.asList(parameters), null);
	}

	/**
	 * Returns a new datatype builder for the datatype with the given name and
	 * type parameters. The type parameters are specified as given types. The
	 * given origin object will be returned by {@link IDatatype#getOrigin()} in
	 * the finalized datatype.
	 * 
	 * @param name
	 *            the datatype name
	 * @param parameters
	 *            the formal type parameters of the datatype
	 * @param origin
	 *            the origin of the datatype, or <code>null</code>
	 * @return a datatype builder
	 * @throws IllegalArgumentException
	 *             if the datatype name is not a valid identifier in this
	 *             factory
	 * @throws IllegalArgumentException
	 *             if the type parameters names are in conflict with each other
	 *             or with the datatype name
	 * @throws IllegalArgumentException
	 *             if one of the type parameters was built with another factory
	 * @since 3.1
	 */
	public IDatatypeBuilder makeDatatypeBuilder(String name,
			List<GivenType> parameters, Object origin) {
		return new DatatypeBuilder(this, name, parameters, origin);
	}

	@SuppressWarnings("deprecation")
	private boolean isV1Specific(int tag) {
		return tag == Formula.KPRJ1 //
				|| tag == Formula.KPRJ2 //
				|| tag == Formula.KID;
	}

	private boolean isV2Specific(int tag) {
		return tag == Formula.KPARTITION //
				|| tag == Formula.KPRJ1_GEN //
				|| tag == Formula.KPRJ2_GEN //
				|| tag == Formula.KID_GEN;
	}

	// for V1_INSTANCE only
	private FormulaFactory(BMath grammar) {
		this.extensions = emptyMap();
		this.grammar = grammar;
	}
	
	// for all V2 instances
	private FormulaFactory(Map<Integer, IFormulaExtension> extMap) {
		this.extensions = extMap;
		this.grammar = new ExtendedGrammar(
				new LinkedHashSet<IFormulaExtension>(extMap.values()));
		this.grammar.init();
	}

	/**
	 * FIXME should either not be published, or return an interface
	 * @since 2.0
	 */
	public AbstractGrammar getGrammar() {
		return grammar;
	}
	
	/**
	 * Returns a view of the underlying grammar associated to this factory,
	 * through its operator groups.
	 * 
	 * @return a list of operator groups
	 * @see IOperatorGroup
	 * @since 2.0
	 */
	public IGrammar getGrammarView() {
		return grammar.asExternalView();
	}
	
	/*
	 * Returns the tag of the given extension. As a side-effect, verifies that
	 * this extension is indeed known and supported by this formula factory.
	 * Throws an IllegalArgumentException if it is not the case.
	 */
	private int getCheckedExtensionTag(IFormulaExtension extension) {
		final int tag = getTag(extension);
		if (tag == NO_TAG) {
			throw new IllegalArgumentException("Unknown formula extension "
					+ extension.getId());
		}
		if (!hasExtension(tag)) {
			throw new IllegalArgumentException("Formula extension "
					+ extension.getId() + " is not supported by this factory");
		}
		return tag;
	}

	/**
	 * Returns a new extended expression.
	 * 
	 * @param extension
	 *            the expression extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @param type
	 *            the type of the extended expression or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if the given type is not valid
	 * @throws IllegalArgumentException
	 *             if the given type or some given child has been built with a
	 *             different factory
	 * @since 2.0
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location, Type type) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedExpression(tag, expressions.clone(),
				predicates.clone(), location, this, extension, type);
	}

	/**
	 * Returns a new extended expression.
	 * 
	 * @param extension
	 *            the expression extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 2.0
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location) {
		return makeExtendedExpression(extension, expressions, predicates,
				location, (Type) null);
	}

	/**
	 * Returns a new extended expression with a type annotation.
	 * <p>
	 * The type annotation is not checked here, but will be used later when
	 * type-checking the returned expression.
	 *
	 * @param extension      the expression extension
	 * @param expressions    the children expressions
	 * @param predicates     the children predicates
	 * @param location       the source location or <code>null</code>
	 * @param typeAnnotation the type annotation
	 * @return a new extended expression
	 * @throws IllegalArgumentException if the extension is not supported by this
	 *                                  factory
	 * @throws IllegalArgumentException if the preconditions of the extension on
	 *                                  children are not verified
	 * @throws IllegalArgumentException if the given type annotation or some given
	 *                                  child has been built with a different
	 *                                  factory
	 * @since 3.9
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location, ITypeAnnotation typeAnnotation) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedExpression(tag, expressions.clone(),
				predicates.clone(), location, this, extension, typeAnnotation);
	}

	/**
	 * Returns a new extended expression.
	 * 
	 * @param extension
	 *            the expression extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @param type
	 *            the type of the extended expression or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if the given type is not valid
	 * @throws IllegalArgumentException
	 *             if the given type or some given child has been built with a
	 *             different factory
	 * @since 3.0
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location, Type type) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedExpression(tag, toExprArray(expressions),
				toPredArray(predicates), location, this, extension, type);
	}

	/**
	 * Returns a new extended expression.
	 * 
	 * @param extension
	 *            the expression extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 2.0
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location) {
		return makeExtendedExpression(extension, expressions, predicates,
				location, (Type) null);
	}

	/**
	 * Returns a new extended expression with a type annotation.
	 * <p>
	 * The type annotation is not checked here, but will be used later when
	 * type-checking the returned expression.
	 *
	 * @param extension      the expression extension
	 * @param expressions    the children expressions
	 * @param predicates     the children predicates
	 * @param location       the source location or <code>null</code>
	 * @param typeAnnotation the type annotation
	 * @return a new extended expression
	 * @throws IllegalArgumentException if the extension is not supported by this
	 *                                  factory
	 * @throws IllegalArgumentException if the preconditions of the extension on
	 *                                  children are not verified
	 * @throws IllegalArgumentException if the given type annotation or some given
	 *                                  child has been built with a different
	 *                                  factory
	 * @since 3.9
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location,
			ITypeAnnotation typeAnnotation) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedExpression(tag, toExprArray(expressions),
				toPredArray(predicates), location, this, extension, typeAnnotation);
	}

	/**
	 * Returns a new extended predicate.
	 * 
	 * @param extension
	 *            the predicate extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 2.0
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedPredicate makeExtendedPredicate(
			IPredicateExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedPredicate(tag, expressions.clone(),
				predicates.clone(), location, this, extension);
	}

	/**
	 * Returns a new extended predicate.
	 * 
	 * @param extension
	 *            the predicate extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 3.6
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedPredicate makeExtendedPredicate(
			IPredicateExtension2 extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedPredicate(tag, expressions.clone(),
				predicates.clone(), location, this, extension);
	}

	/**
	 * Returns a new extended predicate.
	 * 
	 * @param extension
	 *            the predicate extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 2.0
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedPredicate makeExtendedPredicate(
			IPredicateExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedPredicate(tag, toExprArray(expressions),
				toPredArray(predicates), location, this, extension);
	}

	/**
	 * Returns a new extended predicate.
	 * 
	 * @param extension
	 *            the predicate extension
	 * @param expressions
	 *            the children expressions
	 * @param predicates
	 *            the children predicates
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new extended expression
	 * @throws IllegalArgumentException
	 *             if the extension is not supported by this factory
	 * @throws IllegalArgumentException
	 *             if the preconditions of the extension on children are not
	 *             verified
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 3.6
	 * @see IExtensionKind#checkPreconditions(Expression[], Predicate[])
	 */
	public ExtendedPredicate makeExtendedPredicate(
			IPredicateExtension2 extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location) {
		final int tag = getCheckedExtensionTag(extension);
		return new ExtendedPredicate(tag, toExprArray(expressions),
				toPredArray(predicates), location, this, extension);
	}

	/**
	 * Returns the conditional expression extension. This extension takes one
	 * predicate and two expression children. Its value is the first expression
	 * if the predicate holds, the second expression otherwise.
	 * 
	 * @return the expression extension of the <code>COND</code> operator
	 * @since 2.0
	 */
	public static IExpressionExtension getCond() {
		return Cond.getCond();
	}

	/**
	 * Returns the integer tag corresponding to the given formula extension, or
	 * <code>NO_TAG</code> if the given extension is unknown to the AST library
	 * (i.e., has never been used for any factory).
	 * 
	 * @return the tag associated to the given extension or
	 *         {@link Formula#NO_TAG} if unknown
	 * @since 2.0
	 */
	public static int getTag(IFormulaExtension extension) {
		final Integer tag = ALL_EXTENSIONS.get(extension);
		return tag == null ? NO_TAG : tag;
	}

	/**
	 * Returns the formula extension corresponding to the given tag, or
	 * <code>null</code> if the given extension is not supported by this
	 * factory.
	 * 
	 * @return the extension associated to the given tag or <code>null</code> if
	 *         unsupported
	 * @since 2.0
	 */
	public IFormulaExtension getExtension(int tag) {
		return extensions.get(tag);
	}

	/**
	 * Tells whether the given tag corresponds to a formula extension supported
	 * by this factory. Consequently, returns <code>false</code> if the given
	 * tag does not correspond to a formula extension.
	 * <p>
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * factory.getExtension(tag) != null
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return <code>true</code> iff the given tag corresponds to a formula
	 *         extension supported by this factory
	 * @since 3.0
	 */
	public boolean hasExtension(int tag) {
		return getExtension(tag) != null;
	}

	/**
	 * Tells whether the given formula extension is supported by this factory.
	 * 
	 * @return <code>true</code> iff the given formula extension is supported by
	 *         this factory
	 * @since 3.0
	 */
	public boolean hasExtension(IFormulaExtension extension) {
		final int tag = getTag(extension);
		return hasExtension(tag);
	}

	/**
	 * Returns all the extensions of the current formula factory.
	 * 
	 * @return all extensions supported by this factory
	 * @since 2.0
	 */
	public Set<IFormulaExtension> getExtensions() {
		return new LinkedHashSet<IFormulaExtension>(extensions.values());
	}
	
	/**
	 * Returns a new associative expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#BUNION} set union</li>
	 * <li>{@link Formula#BINTER} set intersection</li>
	 * <li>{@link Formula#BCOMP} backward composition of relations</li>
	 * <li>{@link Formula#FCOMP} forward composition of relations</li>
	 * <li>{@link Formula#OVR} functional overriding</li>
	 * <li>{@link Formula#PLUS} addition</li>
	 * <li>{@link Formula#MUL} multiplication</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the associative expression
	 * @param children
	 *            the children of the associative expression (at least two)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new associative expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid associative expression tag
	 * @throws IllegalArgumentException
	 *             if the children collection contains less than two elements
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public AssociativeExpression makeAssociativeExpression(
			int tag, Expression[] children, SourceLocation location) {
		return new AssociativeExpression(children.clone(), tag, location, this);
	}

	/**
	 * Returns a new associative expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#BUNION} set union</li>
	 * <li>{@link Formula#BINTER} set intersection</li>
	 * <li>{@link Formula#BCOMP} backward composition of relations</li>
	 * <li>{@link Formula#FCOMP} forward composition of relations</li>
	 * <li>{@link Formula#OVR} functional overriding</li>
	 * <li>{@link Formula#PLUS} addition</li>
	 * <li>{@link Formula#MUL} multiplication</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the associative expression
	 * @param children
	 *            the children of the associative expression (at least two)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new associative expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid associative expression tag
	 * @throws IllegalArgumentException
	 *             if the children collection contains less than two elements
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public AssociativeExpression makeAssociativeExpression(
			int tag, Collection<Expression> children, SourceLocation location) {
		return new AssociativeExpression(toExprArray(children), tag, location, this);
	}
	
	/**
	 * Returns a new associative predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#LAND} conjunction</li>
	 * <li>{@link Formula#LOR} disjunction</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the associative predicate
	 * @param predicates
	 *            the children of the associative predicate (at least two)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new associative predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid associative predicate tag
	 * @throws IllegalArgumentException
	 *             if the children collection contains less than two elements
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public AssociativePredicate makeAssociativePredicate(
			int tag, Collection<Predicate> predicates, SourceLocation location) {
		return new AssociativePredicate(toPredArray(predicates), tag, location,
				this);
	}

	/**
	 * Returns a new associative predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#LAND} conjunction</li>
	 * <li>{@link Formula#LOR} disjunction</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the associative predicate
	 * @param predicates
	 *            the children of the associative predicate (at least two)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new associative predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid associative predicate tag
	 * @throws IllegalArgumentException
	 *             if the children collection contains less than two elements
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public AssociativePredicate makeAssociativePredicate(
			int tag, Predicate[] predicates, SourceLocation location) {
		return new AssociativePredicate(predicates.clone(), tag, location, this);
	}

	/**
	 * Returns a new atomic expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#INTEGER} the set of integers</li>
	 * <li>{@link Formula#NATURAL} the set of natural numbers</li>
	 * <li>{@link Formula#NATURAL1} the set of non-null natural numbers</li>
	 * <li>{@link Formula#BOOL} the set of Boolean values</li>
	 * <li>{@link Formula#TRUE} the Boolean value <code>TRUE</code></li>
	 * <li>{@link Formula#FALSE} the Boolean value <code>FALSE</code></li>
	 * <li>{@link Formula#EMPTYSET} the empty set</li>
	 * <li>{@link Formula#KPRED} the predecessor relation on integers</li>
	 * <li>{@link Formula#KSUCC} the successor relation on integers</li>
	 * <li>{@link Formula#KPRJ1_GEN} the generic first projection</li>
	 * <li>{@link Formula#KPRJ2_GEN} the generic second projection</li>
	 * <li>{@link Formula#KID_GEN} the generic identity relation</li>
	 * </ul>
	 * <p>
	 * The last three tags are not supported by a V1 factory.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the atomic expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new atomic expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid atomic expression tag
	 */
	public AtomicExpression makeAtomicExpression(int tag,
			SourceLocation location) {
		if (this == V1_INSTANCE && isV2Specific(tag)) {
			throw new IllegalArgumentException("Unsupported tag in V1: " + tag);
		}
		return new AtomicExpression(tag, location, null, this);
	}

	/**
	 * Returns a new atomic expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#INTEGER} the set of integers</li>
	 * <li>{@link Formula#NATURAL} the set of natural numbers</li>
	 * <li>{@link Formula#NATURAL1} the set of non-null natural numbers</li>
	 * <li>{@link Formula#BOOL} the set of Boolean values</li>
	 * <li>{@link Formula#TRUE} the Boolean value <code>TRUE</code></li>
	 * <li>{@link Formula#FALSE} the Boolean value <code>FALSE</code></li>
	 * <li>{@link Formula#EMPTYSET} the empty set</li>
	 * <li>{@link Formula#KPRED} the predecessor relation on integers</li>
	 * <li>{@link Formula#KSUCC} the successor relation on integers</li>
	 * <li>{@link Formula#KPRJ1_GEN} the generic first projection</li>
	 * <li>{@link Formula#KPRJ2_GEN} the generic second projection</li>
	 * <li>{@link Formula#KID_GEN} the generic identity relation</li>
	 * </ul>
	 * <p>
	 * The allowed type patterns are given below for each tag:
	 * <ul>
	 * <li><code>INTEGER</code>, <code>NATURAL</code>, <code>NATURAL1</code>:
	 * <code>ℙ(ℤ)</code></li>
	 * <li><code>BOOL</code>: <code>ℙ(BOOL)</code></li>
	 * <li><code>TRUE</code>, <code>FALSE</code>: <code>BOOL</code></li>
	 * <li><code>EMPTYSET</code>: <code>ℙ(alpha)</code></li>
	 * <li><code>KPRED</code>, <code>KSUCC</code>: <code>ℙ(ℤ)</code></li>
	 * <li><code>KPRJ1_GEN</code>: <code>ℙ(alpha × beta × alpha)</code></li>
	 * <li><code>KPRJ2_GEN</code>: <code>ℙ(alpha × beta × beta)</code></li>
	 * <li><code>KID_GEN</code>: <code>ℙ(alpha × alpha)</code></li>
	 * </ul>
	 * </p>
	 * <p>
	 * The last three tags are not supported by a V1 factory.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the atomic expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new atomic expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid atomic expression tag
	 * @throws IllegalArgumentException
	 *             if the given type is not valid
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different factory
	 * @since 1.0
	 */
	public AtomicExpression makeAtomicExpression(int tag,
			SourceLocation location, Type type) {
		if (this == V1_INSTANCE && isV2Specific(tag)) {
			throw new IllegalArgumentException("Unsupported tag in V1: " + tag);
		}
		return new AtomicExpression(tag, location, type, this);
	}

	/**
	 * Returns a new empty set expression.
	 * 
	 * @param type
	 *            the type of the empty set. Must be <code>null</code> or a
	 *            power set type
	 * @param location
	 *            the source location or <code>null</code>
	 * 
	 * @return a new empty set expression
	 * @throws IllegalArgumentException
	 *             if the given type is not valid
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different factory
	 */
	public AtomicExpression makeEmptySet(Type type, SourceLocation location) {
		return new AtomicExpression(Formula.EMPTYSET, location, type, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * 
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param value
	 *            after-value for this identifier (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * 
	 * @return a new "becomes equal to" assignment
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesEqualTo makeBecomesEqualTo(FreeIdentifier ident,
			Expression value, SourceLocation location) {
		return new BecomesEqualTo(new FreeIdentifier[] { ident },
				new Expression[] { value }, location, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * 
	 * @param idents
	 *            the identifiers which are assigned to (left-hand side)
	 * @param values
	 *            the after-values for these identifiers (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new "becomes equal to" assignment
	 * @throws IllegalArgumentException
	 *             if there is no assigned identifier
	 * @throws IllegalArgumentException
	 *             if the number of identifiers and after-values are not equal
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesEqualTo makeBecomesEqualTo(FreeIdentifier[] idents,
			Expression[] values, SourceLocation location) {
		return new BecomesEqualTo(idents.clone(), values.clone(), location,
				this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * 
	 * @param idents
	 *            the identifiers which are assigned to (left-hand side)
	 * @param values
	 *            the after-values for these identifiers (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new "becomes equal to" assignment
	 * @throws IllegalArgumentException
	 *             if there is no assigned identifier
	 * @throws IllegalArgumentException
	 *             if the number of identifiers and after-values are not equal
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesEqualTo makeBecomesEqualTo(Collection<FreeIdentifier> idents,
			Collection<Expression> values, SourceLocation location) {
		return new BecomesEqualTo(toIdentArray(idents), toExprArray(values),
				location, this);
	}

	/**
	 * Returns a new "becomes member of" assignment.
	 * 
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param setExpr
	 *            set to which the after-value belongs (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new "becomes member of" assignment
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesMemberOf makeBecomesMemberOf(FreeIdentifier ident,
			Expression setExpr, SourceLocation location) {
		return new BecomesMemberOf(ident, setExpr, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * 
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param primedIdent
	 *            bound identifier declaration for primed identifier (after
	 *            values)
	 * @param condition
	 *            condition on before and after values of this identifier
	 *            (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new "becomes such that" assignment
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesSuchThat makeBecomesSuchThat(FreeIdentifier ident,
			BoundIdentDecl primedIdent, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(new FreeIdentifier[] { ident },
				new BoundIdentDecl[] { primedIdent }, condition, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * 
	 * @param idents
	 *            identifiers that are assigned to (left-hand side)
	 * @param primedIdents
	 *            bound identifier declarations for primed identifiers (after
	 *            values)
	 * @param condition
	 *            condition on before and after values of these identifiers
	 *            (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new "becomes such that" assignment
	 * @throws IllegalArgumentException
	 *             if there is no assigned identifier
	 * @throws IllegalArgumentException
	 *             if the number of identifiers and primed identifiers are not
	 *             equal
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesSuchThat makeBecomesSuchThat(FreeIdentifier[] idents,
			BoundIdentDecl[] primedIdents, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(idents.clone(), primedIdents.clone(),
				condition, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * 
	 * @param idents
	 *            identifiers that are assigned to (left-hand side)
	 * @param primedIdents
	 *            bound identifier declarations for primed identifiers (after
	 *            values)
	 * @param condition
	 *            condition on before and after values of these identifiers
	 *            (right-hand side)
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new "becomes such that" assignment
	 * @throws IllegalArgumentException
	 *             if there is no assigned identifier
	 * @throws IllegalArgumentException
	 *             if the number of identifiers and primed identifiers are not
	 *             equal
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BecomesSuchThat makeBecomesSuchThat(Collection<FreeIdentifier> idents,
			Collection<BoundIdentDecl> primedIdents, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(toIdentArray(idents),
				toBIDArray(primedIdents), condition, location, this);
	}

	/**
	 * Returns a new binary expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#MAPSTO} the maplet operator</li>
	 * <li>{@link Formula#REL} the set of all relations</li>
	 * <li>{@link Formula#TREL} the set of all total relations</li>
	 * <li>{@link Formula#SREL} the set of all surjective relations</li>
	 * <li>{@link Formula#STREL} the set of all total and surjective relations</li>
	 * <li>{@link Formula#PFUN} the set of all partial functions</li>
	 * <li>{@link Formula#TFUN} the set of all total functions</li>
	 * <li>{@link Formula#PINJ} the set of all partial injections</li>
	 * <li>{@link Formula#TINJ} the set of all total injections</li>
	 * <li>{@link Formula#PSUR} the set of all partial surjections</li>
	 * <li>{@link Formula#TSUR} the set of all total surjections</li>
	 * <li>{@link Formula#TBIJ} the set of all total bijections</li>
	 * <li>{@link Formula#SETMINUS} set subtraction</li>
	 * <li>{@link Formula#CPROD} Cartesian product</li>
	 * <li>{@link Formula#DPROD} direct product</li>
	 * <li>{@link Formula#PPROD} parallel product</li>
	 * <li>{@link Formula#DOMRES} domain restriction</li>
	 * <li>{@link Formula#DOMSUB} domain subtraction</li>
	 * <li>{@link Formula#RANRES} range restriction</li>
	 * <li>{@link Formula#RANSUB} range subtraction</li>
	 * <li>{@link Formula#UPTO} integer interval</li>
	 * <li>{@link Formula#MINUS} integer subtraction</li>
	 * <li>{@link Formula#DIV} integer division</li>
	 * <li>{@link Formula#MOD} integer modulo</li>
	 * <li>{@link Formula#EXPN} integer exponentiation</li>
	 * <li>{@link Formula#FUNIMAGE} functional image</li>
	 * <li>{@link Formula#RELIMAGE} relational image</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the binary expression
	 * @param left
	 *            the left-hand child of the binary expression
	 * @param right
	 *            the right-hand child of the binary expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new binary expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid binary expression tag
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BinaryExpression makeBinaryExpression(int tag,
			Expression left, Expression right, SourceLocation location) {
		return new BinaryExpression(left, right, tag, location, this);
	}

	/**
	 * Returns a new binary predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#LIMP} logical implication</li>
	 * <li>{@link Formula#LEQV} logical equivalence</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the binary predicate
	 * @param left
	 *            the left-hand child of the binary predicate
	 * @param right
	 *            the right-hand child of the binary predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new binary predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid binary predicate tag
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public BinaryPredicate makeBinaryPredicate(int tag,
			Predicate left, Predicate right, SourceLocation location) {
		return new BinaryPredicate(left, right, tag, location, this);
	}

	/**
	 * Returns a new "bool" expression. Its tag will be {@link Formula#KBOOL}.
	 * 
	 * @param child
	 *            the predicate of the bool expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new bool expression
	 * @throws IllegalArgumentException
	 *             if the given child has been built with a different factory
	 */
	public BoolExpression makeBoolExpression(Predicate child, SourceLocation location) {
		return new BoolExpression(child, location, this);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier. Its
	 * tag will be {@link Formula#BOUND_IDENT_DECL}.
	 * <p>
	 * As the given name is just for convenience (it is not really part of the
	 * formula), it is only required to be valid in the default factory. This
	 * means that a reserved name introduced by a mathematical extension is
	 * accepted and will be replaced by an arbitrary identifier name when
	 * converting to String form.
	 * </p>
	 * 
	 * @param name
	 *            the name of the identifier
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a bound identifier declaration
	 * @throws IllegalArgumentException
	 *             if the name is not a valid identifier name in the default
	 *             factory
	 * @see #isValidIdentifierName(String)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public BoundIdentDecl makeBoundIdentDecl(String name,
			SourceLocation location) {
		return new BoundIdentDecl(name, location, null, this);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier. Its
	 * tag will be {@link Formula#BOUND_IDENT_DECL}.
	 * <p>
	 * As the given name is just for convenience (it is not really part of the
	 * formula), it is only required to be valid in the default factory. This
	 * means that a reserved name introduced by a mathematical extension is
	 * accepted and will be replaced by an arbitrary identifier name when
	 * converting to String form.
	 * </p>
	 * 
	 * @param name
	 *            the name of the identifier
	 * @param location
	 *            the source location or <code>null</code>
	 * @param type
	 *            the type of the identifier or <code>null</code>
	 * @return a bound identifier declaration
	 * @throws IllegalArgumentException
	 *             if the name is not a valid identifier name in the default
	 *             factory
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different factory
	 * @see #isValidIdentifierName(String)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public BoundIdentDecl makeBoundIdentDecl(String name,
			SourceLocation location, Type type) {
		return new BoundIdentDecl(name, location, type, this);
	}

	/**
	 * Returns a new bound occurrence of an identifier. Its tag will be
	 * {@link Formula#BOUND_IDENT}.
	 * 
	 * @param index
	 *            the index in De Bruijn notation. Must be non-negative
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a bound identifier occurrence
	 * @throws IllegalArgumentException
	 *             if the index is negative
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 */
	public BoundIdentifier makeBoundIdentifier(int index,
			SourceLocation location) {
		return new BoundIdentifier(index, location, null, this);
	}

	/**
	 * Returns a new bound occurrence of an identifier. Its tag will be
	 * {@link Formula#BOUND_IDENT}.
	 * 
	 * @param index
	 *            the index in the De Bruijn notation. Must be non-negative
	 * @param location
	 *            the source location or <code>null</code>
	 * @param type
	 *            the type of this identifier or <code>null</code>
	 * @return a bound identifier occurrence
	 * @throws IllegalArgumentException
	 *             if the index is negative
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different factory
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 */
	public BoundIdentifier makeBoundIdentifier(int index,
			SourceLocation location, Type type) {
		return new BoundIdentifier(index, location, type, this);
	}

	/**
	 * Creates a new node representing a free occurrence of an identifier. Its tag will be
	 * {@link Formula#FREE_IDENT}.
	 * 
	 * @param name
	 *            the name of the identifier
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a free identifier
	 * @throws IllegalArgumentException
	 *             if the name is not a valid identifier name
	 * @see #isValidIdentifierName(String)
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public FreeIdentifier makeFreeIdentifier(String name,
			SourceLocation location) {
		return new FreeIdentifier(name, location, null, this);
	}
	
	/**
	 * Creates a new node representing a free occurrence of an identifier. Its
	 * tag will be {@link Formula#FREE_IDENT}.
	 * <p>
	 * The name of the identifier must not be used to denote a given type in the
	 * type parameter, except when the identifier denotes a given set. In other
	 * words, the only case where the given name can occur in the type parameter
	 * is when the type is exactly <code>ℙ(name)</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the identifier
	 * @param location
	 *            the source location or <code>null</code>
	 * @param type
	 *            the type of this identifier or <code>null</code>
	 * @return a free identifier
	 * @throws IllegalArgumentException
	 *             if the name is not a valid identifier name
	 * @throws IllegalArgumentException
	 *             if the given type is not valid
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different factory
	 * @see #isValidIdentifierName(String)
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public FreeIdentifier makeFreeIdentifier(String name,
			SourceLocation location, Type type) {
		return new FreeIdentifier(name, location, type, this);
	}

	/**
	 * Returns a new integer literal. Its tag will be {@link Formula#INTLIT}.
	 * 
	 * @param literal
	 *            the integer value for this literal
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new integer literal
	 */
	public IntegerLiteral makeIntegerLiteral(BigInteger literal,
			SourceLocation location) {
		return new IntegerLiteral(literal, location, this);
	}

	/**
	 * Returns a new literal predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#BTRUE} truth</li>
	 * <li>{@link Formula#BFALSE} falsity</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new literal predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a literal predicate tag
	 */
	public LiteralPredicate makeLiteralPredicate(int tag,
			SourceLocation location) {
		return new LiteralPredicate(tag, location, this);
	}

	/**
	 * Returns a new PredicateVariable. Its tag will be
	 * {@link Formula#PREDICATE_VARIABLE}.
	 * <p>
	 * Predicate variables are named with a special prefix (
	 * {@link PredicateVariable#LEADING_SYMBOL}) followed by an identifier
	 * name.  As predicate variables live in a scope different from regular
	 * identifiers, their suffix can be an otherwise reserved name (e.g., the
	 * name <code>$id</code> is valid).
	 * </p>
	 * 
	 * @param name
	 *            the name of the predicate variable
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new predicate variable
	 * @throws IllegalArgumentException
	 *             if the name does not start with
	 *             {@link PredicateVariable#LEADING_SYMBOL}
	 * @throws IllegalArgumentException
	 *             if the name without the
	 *             {@link PredicateVariable#LEADING_SYMBOL} prefix is not a
	 *             valid identifier
	 * @since 1.2
	 * @see #isValidPredicateName(String)
	 */
	public PredicateVariable makePredicateVariable(String name,
			SourceLocation location) {
		return new PredicateVariable(name, location, this);
	}
	
	/**
	 * Returns a new quantified expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#QUNION} quantified set union</li>
	 * <li>{@link Formula#QINTER} quantified set intersection</li>
	 * <li>{@link Formula#CSET} set comprehension</li>
	 * </ul>
	 * <p>
	 * One can also specify the written form of the expression. The allowed
	 * values are (in decreasing order of generality)
	 * <ul>
	 * <li>{@link Form#Explicit} explicit declaration of quantifiers</li>
	 * <li>{@link Form#Implicit} implicit declaration of quantifiers</li>
	 * <li>{@link Form#Lambda} lambda abstraction</li>
	 * </ul>
	 * Lambda abstraction is only meaningful for set comprehension. If the given
	 * form is not compatible with the structure of the quantified expression,
	 * it will automatically be upgraded it to the least general that fits.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the quantified expression
	 * @param boundIdentifiers
	 *            the bound identifier declarations
	 * @param pred
	 *            the child predicate
	 * @param expr
	 *            the child expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @param form
	 *            the written form of the quantified expression
	 * @return a new quantified expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a quantified expression tag
	 * @throws IllegalArgumentException
	 *             if there is no bound identifier declaration
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	// TODO: maybe make different creators for every form
	public QuantifiedExpression makeQuantifiedExpression(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate pred, Expression expr,
			SourceLocation location, Form form) {
		return new QuantifiedExpression(expr, pred, boundIdentifiers.clone(),
				tag, location, form, this);
	}

	/**
	 * Returns a new quantified expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#QUNION} quantified set union</li>
	 * <li>{@link Formula#QINTER} quantified set intersection</li>
	 * <li>{@link Formula#CSET} set comprehension</li>
	 * </ul>
	 * <p>
	 * One can also specify the written form of the expression. The allowed
	 * values are (in decreasing order of generality)
	 * <ul>
	 * <li>{@link Form#Explicit} explicit declaration of quantifiers</li>
	 * <li>{@link Form#Implicit} implicit declaration of quantifiers</li>
	 * <li>{@link Form#Lambda} lambda abstraction</li>
	 * </ul>
	 * Lambda abstraction is only meaningful for set comprehension. If the given
	 * form is not compatible with the structure of the quantified expression,
	 * it will automatically be upgraded it to the least general that fits.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the quantified expression
	 * @param boundIdentifiers
	 *            the bound identifier declarations
	 * @param pred
	 *            the child predicate
	 * @param expr
	 *            the child expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @param form
	 *            the written form of the quantified expression
	 * @return a new quantified expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a quantified expression tag
	 * @throws IllegalArgumentException
	 *             if there is no bound identifier declaration
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public QuantifiedExpression makeQuantifiedExpression(int tag,
			Collection<BoundIdentDecl> boundIdentifiers, Predicate pred, Expression expr,
			SourceLocation location, Form form) {
		return new QuantifiedExpression(expr, pred,
				toBIDArray(boundIdentifiers), tag, location, form, this);
	}

	/**
	 * Returns a new quantified predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#FORALL} universal quantification</li>
	 * <li>{@link Formula#EXISTS} existential quantification</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the quantified predicate
	 * @param boundIdentifiers
	 *            the bound identifier declarations
	 * @param pred
	 *            the corresponding predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new quantified predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a quantified predicate tag
	 * @throws IllegalArgumentException
	 *             if there is no bound identifier declaration
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public QuantifiedPredicate makeQuantifiedPredicate(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate pred,
			SourceLocation location) {
		return new QuantifiedPredicate(pred, boundIdentifiers.clone(), tag,
				location, this);
	}

	/**
	 * Returns a new quantified predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#FORALL} universal quantification</li>
	 * <li>{@link Formula#EXISTS} existential quantification</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the quantified predicate
	 * @param boundIdentifiers
	 *            the bound identifier declarations
	 * @param pred
	 *            the corresponding predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new quantified predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a quantified predicate tag
	 * @throws IllegalArgumentException
	 *             if there is no bound identifier declaration
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public QuantifiedPredicate makeQuantifiedPredicate(int tag,
			Collection<BoundIdentDecl> boundIdentifiers, Predicate pred,
			SourceLocation location) {
		return new QuantifiedPredicate(pred, toBIDArray(boundIdentifiers), tag,
				location, this);
	}
	
	/**
	 * Returns a new relational predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#EQUAL} equality</li>
	 * <li>{@link Formula#NOTEQUAL} disequality</li>
	 * <li>{@link Formula#LT} less than</li>
	 * <li>{@link Formula#LE} less than or equal to</li>
	 * <li>{@link Formula#GT} greater than</li>
	 * <li>{@link Formula#GE} greater than or equal to</li>
	 * <li>{@link Formula#IN} membership</li>
	 * <li>{@link Formula#NOTIN} non membership</li>
	 * <li>{@link Formula#SUBSET} strict subset</li>
	 * <li>{@link Formula#NOTSUBSET} not strict subset</li>
	 * <li>{@link Formula#SUBSETEQ} subset or equal to</li>
	 * <li>{@link Formula#NOTSUBSETEQ} neither subset nor equal to</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the relational predicate
	 * @param left
	 *            the left-hand child of the relational predicate
	 * @param right
	 *            the right-hand child of the relational predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new relational predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a valid relational predicate tag
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 */
	public RelationalPredicate makeRelationalPredicate(int tag,
			Expression left, Expression right, SourceLocation location) {
		return new RelationalPredicate(left, right, tag, location, this);
	}

	/**
	 * Returns a new singleton set. Its tag will be {@link Formula#SETEXT}.
	 * 
	 * @param expression
	 *            the unique member of this singleton set
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new set extension
	 * @throws IllegalArgumentException
	 *             if the given member has been built with a different factory
	 */
	public SetExtension makeSetExtension(Expression expression,
			SourceLocation location) {
		return new SetExtension(new Expression[] { expression }, location,
				this, null);
	}

	/**
	 * Returns a new set extension. Its tag will be {@link Formula#SETEXT}.
	 * 
	 * @param members
	 *            the members of the set extension
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new set extension
	 * @throws IllegalArgumentException
	 *             if some given member has been built with a different factory
	 */
	public SetExtension makeSetExtension(Expression[] members,
			SourceLocation location) {
		return new SetExtension(members.clone(), location, this, null);
	}

	/**
	 * Returns a new set extension containing no child, but having the given
	 * type. Its tag will be {@link Formula#SETEXT}.
	 * <p>
	 * This method is hardly useful to end-users, except when writing tests. One
	 * should prefer using {@link #makeEmptySet(Type, SourceLocation)}.
	 * </p>
	 * 
	 * @param type
	 *            the type of the set extension
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new empty set extension of the given type
	 * @throws IllegalArgumentException
	 *             if the given type is not a powerset type
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different factory
	 * @since 2.6
	 */
	public SetExtension makeEmptySetExtension(Type type, SourceLocation location) {
		return new SetExtension(NO_EXPRESSIONS, location, this, type);
	}

	/**
	 * Returns a new set extension. Its tag will be {@link Formula#SETEXT}.
	 * 
	 * @param members
	 *            the members of the set extension
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new set extension
	 * @throws IllegalArgumentException
	 *             if some given member has been built with a different factory
	 */
	public SetExtension makeSetExtension(Collection<Expression> members,
			SourceLocation location) {
		return new SetExtension(toExprArray(members), location, this, null);
	}

	/**
	 * Returns a new simple predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#KFINITE} finiteness</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the simple predicate
	 * @param child
	 *            the child of the simple predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new simple predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a simple predicate tag
	 * @throws IllegalArgumentException
	 *             if the given child has been built with a different factory
	 */
	public SimplePredicate makeSimplePredicate(int tag, Expression child,
			SourceLocation location) {
		return new SimplePredicate(child, tag, location, this);
	}

	/**
	 * Returns a new empty type environment.
	 * 
	 * @return a new empty type environment
	 * @since 3.0
	 */
	public ITypeEnvironmentBuilder makeTypeEnvironment() {
		return new TypeEnvironmentBuilder(this);
	}

	/**
	 * Returns a new unary expression. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#KCARD} set cardinality</li>
	 * <li>{@link Formula#POW} powerset</li>
	 * <li>{@link Formula#POW1} powerset without empty set</li>
	 * <li>{@link Formula#KUNION} generalized union</li>
	 * <li>{@link Formula#KINTER} generalized intersection</li>
	 * <li>{@link Formula#KDOM} domain</li>
	 * <li>{@link Formula#KRAN} range</li>
	 * <li>{@link Formula#KMIN} minimum</li>
	 * <li>{@link Formula#KMAX} maximum</li>
	 * <li>{@link Formula#CONVERSE} converse of a relation</li>
	 * <li>{@link Formula#UNMINUS} opposite</li>
	 * </ul>
	 * <p>
	 * For backward compatibility with the mathematical language V1, the
	 * following additional tags are also accepted by a V1 factory
	 * <ul>
	 * <li>{@link Formula#KPRJ1} unary first projection</li>
	 * <li>{@link Formula#KPRJ2} unary second projection</li>
	 * <li>{@link Formula#KID} unary identity</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the unary expression
	 * @param child
	 *            the child of the unary expression
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new unary expression
	 * @throws IllegalArgumentException
	 *             if the tag is not a unary expression tag
	 * @throws IllegalArgumentException
	 *             if the given child has been built with a different factory
	 */
	@SuppressWarnings("javadoc")
	public UnaryExpression makeUnaryExpression(int tag, Expression child,
			SourceLocation location) {
		if (this != V1_INSTANCE && isV1Specific(tag)) {
			throw new IllegalArgumentException("Unsupported V1 tag: " + tag);
		}
		return new UnaryExpression(child, tag, location, this);
	}

	/**
	 * Returns a new unary predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#NOT} logical negation</li>
	 * </ul>
	 * 
	 * @param tag
	 *            the tag of the unary predicate
	 * @param child
	 *            the child of the unary predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new unary predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a unary predicate tag
	 * @throws IllegalArgumentException
	 *             if the given child has been built with a different factory
	 */
	public UnaryPredicate makeUnaryPredicate(int tag, Predicate child,
			SourceLocation location) {
		return new UnaryPredicate(child, tag, location, this);
	}
	
	/**
	 * Returns a new multiple predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#KPARTITION} partition</li>
	 * </ul>
	 * <p>
	 * This method is not supported by a V1 factory.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the multiple predicate
	 * @param children
	 *            the children of the multiple predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new multiple predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a multiple predicate tag
	 * @throws IllegalArgumentException
	 *             if there is no child
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 1.0
	 */
	public MultiplePredicate makeMultiplePredicate(int tag,
			Expression[] children, SourceLocation location) {
		if (this == V1_INSTANCE) {
			throw new IllegalArgumentException("Unsupported in V1");
		}
		return new MultiplePredicate(children.clone(), tag, location, this);
	}

	/**
	 * Returns a new multiple predicate. The allowed tag values are
	 * <ul>
	 * <li>{@link Formula#KPARTITION} partition</li>
	 * </ul>
	 * <p>
	 * This method is not supported by a V1 factory.
	 * </p>
	 * 
	 * @param tag
	 *            the tag of the multiple predicate
	 * @param children
	 *            the children of the multiple predicate
	 * @param location
	 *            the source location or <code>null</code>
	 * @return a new multiple predicate
	 * @throws IllegalArgumentException
	 *             if the tag is not a multiple predicate tag
	 * @throws IllegalArgumentException
	 *             if there is no child
	 * @throws IllegalArgumentException
	 *             if some given child has been built with a different factory
	 * @since 1.0
	 */
	public MultiplePredicate makeMultiplePredicate(int tag,
			Collection<Expression> children, SourceLocation location) {
		if (this == V1_INSTANCE) {
			throw new IllegalArgumentException("Unsupported in V1");
		}
		return new MultiplePredicate(toExprArray(children), tag, location, this);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 3.0
	 */
	public IParseResult parseAssignment(String formula, Object origin) {
		return parseGeneric(formula, origin, Assignment.class, false);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 3.0
	 */
	public IParseResult parseExpression(String formula, Object origin) {
		return parseGeneric(formula, origin, Expression.class, false);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method allows predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 3.0
	 */
	public IParseResult parseExpressionPattern(String formula, Object origin) {
		return parseGeneric(formula, origin, Expression.class, true);
	}

	/**
	 * Parses the specified predicate and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 3.0
	 */
	public IParseResult parsePredicate(String formula, Object origin) {
		return parseGeneric(formula, origin, Predicate.class, false);
	}

	/**
	 * Parses the specified predicate and returns the corresponding result. This
	 * method allows predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 3.0
	 */
	public IParseResult parsePredicatePattern(String formula, Object origin) {
		return parseGeneric(formula, origin, Predicate.class, true);
	}

	/**
	 * Parses the specified type and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @return the result of the parse
	 * @since 3.0
	 */
	public IParseResult parseType(String formula) {
		return parseGeneric(formula, null, Type.class, false);
	}

	private final <T> IParseResult parseGeneric(String formula, Object origin, Class<T> clazz,
			boolean withPredVars) {
		final ParseResult result = new ParseResult(this, origin);
		final Scanner scanner = new Scanner(formula, result, grammar);
		final GenParser parser = new GenParser(clazz, scanner, result, withPredVars);
		parser.parse();
		return parser.getResult();
	}

	/**
	 * Returns the type which corresponds to the set of booleans.
	 * 
	 * @return the predefined boolean type
	 */
	public BooleanType makeBooleanType() {
		return new BooleanType(this);
	}

	/**
	 * Returns the instance of the parametric type of the given constructor with
	 * the given type parameters.
	 * @param typeConstructor
	 *            the constructor of the parametric type
	 * @param typePrms
	 *            the parameters of the parametric type
	 * 
	 * @return a new parametric type
	 * @throws IllegalArgumentException
	 *             if the given extension is not supported by this factory or is
	 *             not a type constructor
	 * @throws IllegalArgumentException
	 *             if the number of type parameters does not correspond to the
	 *             type constructor specification
	 * @throws IllegalArgumentException
	 *             if some type parameter has been built with a different
	 *             formula factory
	 * @since 3.0
	 * @see IExpressionExtension#isATypeConstructor()
	 * @see IExtensionKind#checkTypePreconditions(Type[])
	 */
	public ParametricType makeParametricType(
			IExpressionExtension typeConstructor, List<Type> typePrms) {
		getCheckedExtensionTag(typeConstructor);
		return new ParametricType(this, typeConstructor, toTypeArray(typePrms));
	}

	/**
	 * Returns the instance of the parametric type of the given constructor with
	 * the given type parameters.
	 * @param typeConstructor
	 *            the constructor of the parametric type
	 * @param typePrms
	 *            the parameters of the parametric type
	 * 
	 * @return a new parametric type
	 * @throws IllegalArgumentException
	 *             if the given extension is not supported by this factory or is
	 *             not a type constructor
	 * @throws IllegalArgumentException
	 *             if the number of type parameters does not correspond to the
	 *             type constructor specification
	 * @throws IllegalArgumentException
	 *             if some type parameter has been built with a different
	 *             formula factory
	 * @since 3.0
	 * @see IExpressionExtension#isATypeConstructor()
	 * @see IExtensionKind#checkTypePreconditions(Type[])
	 */
	public ParametricType makeParametricType(
			IExpressionExtension typeConstructor, Type... typePrms) {
		getCheckedExtensionTag(typeConstructor);
		return new ParametricType(this, typeConstructor, typePrms.clone());
	}

	/**
	 * Returns the type which corresponds to the carrier-set with the given
	 * name.
	 * 
	 * @param name
	 *            name of the type
	 * @return a given type with the given name
	 * @throws IllegalArgumentException
	 *             if the name is not a valid identifier name
	 * @see #isValidIdentifierName(String)
	 */
	public GivenType makeGivenType(String name) {
		return new GivenType(this, name);
	}

	/**
	 * Returns the type which corresponds to the carrier-set named after the
	 * given free identifier.
	 * <p>
	 * The free identifier argument must be typed as a valid given type, i.e its
	 * type must be a powerset over a given type of its name. That is, for an
	 * identifier named 'S', its type must be POW(S).
	 * </p>
	 * <p>
	 * Calling {@link GivenType#toExpression()} on the returned type will return
	 * the given free identifier. Thus, in particular, the source location (if
	 * any) is retained.
	 * </p>
	 * 
	 * @param freeIdentifier
	 *            a free identifier typed as a given set
	 * @return a new given type with given identifier name
	 * @throws IllegalArgumentException
	 *             if the given free identifier is not typed as a given set.
	 * @since 3.0
	 */
	public GivenType makeGivenType(FreeIdentifier freeIdentifier) {
		return new GivenType(this, freeIdentifier);
	}

	/**
	 * Returns the type which corresponds to the set of all integers.
	 * 
	 * @return the predefined integer type
	 */
	public IntegerType makeIntegerType() {
		return new IntegerType(this);
	}

	/**
	 * Returns the formula position corresponding to the given string image.
	 * 
	 * @param image
	 *            string image of the position, should have been obtained from
	 *            {@link IPosition#toString()}.
	 * @return the position denoted by the given string
	 * @throws IllegalArgumentException
	 *             if the given string doesn't denote a formula position
	 */
	public static IPosition makePosition(String image) {
		if (image.length() == 0) {
			return IPosition.ROOT;
		}
		return new Position(image);
	}

	/**
	 * Returns the type corresponding to the power set of the given type.
	 * 
	 * @param base
	 *            the base type to build upon
	 * @return the power set type of the given type
	 * @throws IllegalArgumentException
	 *             if the given type has been built with a different formula
	 *             factory
	 */
	public PowerSetType makePowerSetType(Type base) {
		return new PowerSetType(this, base);
	}

	/**
	 * Returns the type corresponding to the Cartesian product of the two given
	 * types.
	 * 
	 * @param left
	 *            the first component of the Cartesian product
	 * @param right
	 *            the second component of the Cartesian product
	 * @return the product type of the two given types
	 * @throws IllegalArgumentException
	 *             if one of the the given types has been built with a different
	 *             formula factory
	 */
	public ProductType makeProductType(Type left, Type right) {
		return new ProductType(this, left, right);
	}

	/**
	 * Returns the type corresponding to the set of all relations between the
	 * two given types.
	 * <p>
	 * This is a handy method fully equivalent to the call
	 * 
	 * <pre>
	 * makePowerSetType(makeProductType(left, right));
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param left
	 *            the domain of the relations
	 * @param right
	 *            the range of the relations
	 * @return the relational type between the two given types
	 * @throws IllegalArgumentException
	 *             if one of the the given types has been built with a different
	 *             formula factory
	 */
	public PowerSetType makeRelationalType(Type left, Type right) {
		return makePowerSetType(makeProductType(left, right));
	}
	
	/**
	 * Creates a new empty specialization.
	 * 
	 * @return a fresh empty specialization
	 * @since 2.6
	 */
	public ISpecialization makeSpecialization() {
		return new Specialization(this);
	}

	/**
	 * Returns whether the given symbol is valid for an extension.
	 *
	 * This will check that the symbol is an identifier or is purely symbolic and
	 * that it does not conflict with existing extensions. Note that, contrary to
	 * {@link #isValidIdentifierName(String)}, this rejects identifiers with a
	 * prime.
	 *
	 * @param symbol symbol to test
	 * @return whether the given symbol is valid or not
	 * @since 3.8
	 */
	public boolean isValidExtensionSymbol(String symbol) {
		return checkSymbol(symbol) && !EXTN_UNICITY_CHECKER.isUsedSymbol(symbol, getExtensions());
	}

	/**
	 * Returns whether the given id is valid for an extension.
	 *
	 * This will check that the id does not conflict with existing extensions.
	 *
	 * @param id id to test
	 * @return whether the given id is valid or not
	 * @since 3.8
	 */
	public boolean isValidExtensionId(String id) {
		return !EXTN_UNICITY_CHECKER.isUsedId(id, getExtensions());
	}

	/**
	 * Returns whether the given name is a valid identifier name (that is an
	 * identifier name which is not used as a keyword in event-B concrete
	 * syntax).
	 * 
	 * A name with a prime at the end is considered to be a valid identifier by this
	 * method. On the other hand, {@link #isValidExtensionSymbol(String)} only
	 * accepts identifiers without a prime (but it also accepts purely symbolic
	 * strings).
	 *
	 * @param name
	 *            the name to test
	 * @return <code>true</code> if the given name is a valid name for an
	 *         identifier
	 */
	public boolean isValidIdentifierName(String name) {
		return Scanner.isToken(this, name, IDENT);
	}

	/**
	 * Returns whether the given name is a valid predicate variable name (that
	 * is any identifier name prefixed by a <code>$</code>).
	 * 
	 * @param name
	 *            the name to test
	 * @return <code>true</code> if the given name is a valid name for a
	 *         predicate variable
	 * @since 3.2
	 */
	public boolean isValidPredicateName(String name) {
		return Scanner.isToken(this, name, PRED_VAR);
	}

	/**
	 * Returns whether the given char is a white space according to the Event-B
	 * lexical specification.
	 * 
	 * @param c
	 *            the char to test
	 * @return <code>true</code> if the given char is a white space
	 */
	public static boolean isEventBWhiteSpace(char c) {
		return isEventBWhiteSpace((int) c);
	}

	/**
	 * Returns whether the given code point is a white space according to the
	 * Event-B lexical specification.
	 * 
	 * @param codePoint
	 *            the code point to test
	 * @return <code>true</code> if the given char is a white space
	 * @since 2.1
	 */
	public static boolean isEventBWhiteSpace(int codePoint) {
		return Character.isSpaceChar(codePoint)
				|| ('\u0009' <= codePoint && codePoint <= 0x000d)
				|| ('\u001c' <= codePoint && codePoint <= '\u001F');
	}

	private void ensuresFactoryV2() {
		if (this == V1_INSTANCE) {
			throw new IllegalArgumentException(
					"The current factory with version V1 cannot be used to upgrade assignment from V1 to V2");
		}
	}

	/**
	 * Upgrades the given assignment string from language version V1 to the V2
	 * using the current factory.
	 * 
	 * @param input
	 *            an assignment string assumed to be parsable in the language
	 *            version V1
	 * @return the result of the upgrade
	 * @throws IllegalArgumentException
	 *             if the current factory is a V1 version
	 * @since 3.0
	 */
	public IUpgradeResult<Assignment> upgradeAssignment(String input) {
		ensuresFactoryV2();
		final UpgradeResult<Assignment> result = new UpgradeResult<Assignment>(
				this);
		final VersionUpgrader upgrader = new VersionUpgraderV1V2();
		upgrader.upgradeAssignment(input, result);
		return result;
	}

	/**
	 * Upgrades the given expression string from language version V1 to the V2
	 * using the current factory.
	 * 
	 * @param input
	 *            an expression string assumed to be parsable in the language
	 *            version immediately preceding the target version
	 * @return the result of the upgrade
	 * @throws IllegalArgumentException
	 *             if the current factory is a V1 version
	 * @since 3.0
	 */
	public IUpgradeResult<Expression> upgradeExpression(String input) {
		ensuresFactoryV2();
		final UpgradeResult<Expression> result = new UpgradeResult<Expression>(
				this);
		final VersionUpgrader upgrader = new VersionUpgraderV1V2();
		upgrader.upgradeExpression(input, result);
		return result;
	}

	/**
	 * Upgrades the given predicate string from language version V1 to the V2
	 * using the current factory.
	 * 
	 * @param input
	 *            a predicate string assumed to be parsable in the language
	 *            version immediately preceding the target version
	 * @return the result of the upgrade
	 * @since 3.0
	 */
	public IUpgradeResult<Predicate> upgradePredicate(String input) {
		final UpgradeResult<Predicate> result = new UpgradeResult<Predicate>(
				this);
		final VersionUpgrader upgrader = new VersionUpgraderV1V2();
		upgrader.upgradePredicate(input, result);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		char sep = '{';
		sb.append("FFactory(");
		sb.append(this.grammar);
		sb.append(")");
		for (IFormulaExtension extension : this.extensions.values()) {
			sb.append(sep);
			sep = ';';
			sb.append(extension.getId());
		}
		if (sep != '{') {
			sb.append('}');
		}
		return sb.toString();
	}

}
