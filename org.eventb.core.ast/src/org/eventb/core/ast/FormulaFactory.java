/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.LanguageVersion.V1;
import static org.eventb.internal.core.ast.FactoryHelper.toBIDArray;
import static org.eventb.internal.core.ast.FactoryHelper.toExprArray;
import static org.eventb.internal.core.ast.FactoryHelper.toIdentArray;
import static org.eventb.internal.core.ast.FactoryHelper.toPredArray;
import static org.eventb.internal.core.ast.FactoryHelper.toTypeArray;
import static org.eventb.internal.core.parser.BMathV1.B_MATH_V1;
import static org.eventb.internal.core.parser.BMathV2.B_MATH_V2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IGrammar;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.internal.core.ast.extension.ExtnUnicityChecker;
import org.eventb.internal.core.ast.extension.datatype.DatatypeExtensionComputer;
import org.eventb.internal.core.lexer.GenLexer;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.ExtendedGrammar;
import org.eventb.internal.core.parser.GenParser;
import org.eventb.internal.core.parser.ParseResult;
import org.eventb.internal.core.typecheck.TypeEnvironmentBuilder;
import org.eventb.internal.core.upgrade.UpgradeResult;
import org.eventb.internal.core.upgrade.UpgraderFactory;
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
	
	// already computed datatype extensions
	private final Map<IDatatypeExtension, IDatatype> datatypeCache = new HashMap<IDatatypeExtension, IDatatype>();
	
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
	 * Return an instance of a formula factory supporting the given extensions
	 * (and only them) in addition to the regular event-B mathematical language.
	 * 
	 * @param extensions
	 *            mathematical extensions to support
	 * @return a formula factory supporting the given extensions
	 * @since 2.0
	 */
	public static FormulaFactory getInstance(Set<IFormulaExtension> extensions) {
		final Set<IFormulaExtension> actualExtns = new LinkedHashSet<IFormulaExtension>();
		actualExtns.addAll(extensions);

		synchronized (ALL_EXTENSIONS) {
			final FormulaFactory cached = INSTANCE_CACHE.get(actualExtns);
			if (cached != null) {
				return cached;
			}
			checkSymbols(actualExtns);
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
	 * Return an instance of a formula factory supporting the given extensions
	 * (and only them) in addition to the regular event-B mathematical language.
	 * 
	 * @param extensions
	 *            mathematical extensions to support
	 * @return a formula factory supporting the given extensions
	 * @since 2.3
	 */
	public static FormulaFactory getInstance(IFormulaExtension... extensions) {
		return getInstance(new LinkedHashSet<IFormulaExtension>(
				Arrays.asList(extensions)));
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
	 * @param symbol
	 *            symbol to test
	 * @return <code>true</code> iff the given symbol is valid
	 * @since 2.0
	 */
	public static boolean checkSymbol(String symbol) {
		if (GenLexer.isIdent(symbol)) {
			return true;
		}
		return !symbol.isEmpty() && GenLexer.isSymbol(symbol);
	}

	/**
	 * Makes a new formula factory that recognizes extensions of this factory
	 * plus given extensions.
	 * 
	 * <p>
	 * Adding already recognized extensions has no effect. The resulting
	 * recognized extensions are a union of known extensions and given
	 * extensions.
	 * </p>
	 * 
	 * @param addedExtns
	 *            a set of extensions to add
	 * @return a new factory
	 * @since 2.0
	 */
	public FormulaFactory withExtensions(Set<IFormulaExtension> addedExtns) {
		final Set<IFormulaExtension> newExtns = new LinkedHashSet<IFormulaExtension>(
				extensions.values());
		newExtns.addAll(addedExtns);
		return getInstance(newExtns);
	}

	/**
	 * Computes extensions out of the given data type.
	 * <p>
	 * It is possible to have the data type make references to other parametric
	 * types (through argument types or return types) provided that these other
	 * types are known by this factory.
	 * </p>
	 * <p>
	 * 
	 * </p>
	 * @since 2.0
	 */
	public synchronized IDatatype makeDatatype(IDatatypeExtension extension) {
		IDatatype cached = datatypeCache.get(extension);
		if (cached == null) {
			cached = new DatatypeExtensionComputer(extension, this).compute();
			datatypeCache.put(extension, cached);
		}
		return cached;
	}
	
	// for V1_INSTANCE only
	private FormulaFactory(BMath grammar) {
		this.extensions = Collections.<Integer, IFormulaExtension>emptyMap();
		this.grammar = grammar;
	}
	
	// for all V2 instances
	/**
	 * @since 2.0
	 */
	protected FormulaFactory(Map<Integer, IFormulaExtension> extMap) {
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
	 * this extension is indeed known and supported by this formula factory
	 */
	private int getExtensionTag(IFormulaExtension extension) {
		final Integer result = ALL_EXTENSIONS.get(extension);
		if (result == null) {
			throw new IllegalArgumentException("Unknown formula extension "
					+ extension.getId());
		}
		if (!extension.equals(extensions.get(result))) {
			throw new IllegalArgumentException("Formula extension "
					+ extension.getId() + " is not supported by this factory");
		}
		return result;
	}

	/**
	 * @since 2.0
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location, Type type) {
		final int tag = getExtensionTag(extension);
		return new ExtendedExpression(tag, expressions.clone(),
				predicates.clone(), location, this, extension, type);
	}

	/**
	 * @since 2.0
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location) {
		return makeExtendedExpression(extension, expressions, predicates,
				location, null);
	}

	/**
	 * @since 3.0
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location, Type type) {
		final int tag = getExtensionTag(extension);
		return new ExtendedExpression(tag, toExprArray(expressions),
				toPredArray(predicates), location, this, extension, type);
	}

	/**
	 * @since 2.0
	 */
	public ExtendedExpression makeExtendedExpression(
			IExpressionExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location) {
		return makeExtendedExpression(extension, expressions, predicates,
				location, null);
	}

	/**
	 * @since 2.0
	 */
	public ExtendedPredicate makeExtendedPredicate(
			IPredicateExtension extension, Expression[] expressions,
			Predicate[] predicates, SourceLocation location) {
		final int tag = getExtensionTag(extension);
		return new ExtendedPredicate(tag, expressions.clone(),
				predicates.clone(), location, this, extension);
	}

	/**
	 * @since 2.0
	 */
	public ExtendedPredicate makeExtendedPredicate(
			IPredicateExtension extension, Collection<Expression> expressions,
			Collection<Predicate> predicates, SourceLocation location) {
		final int tag = getExtensionTag(extension);
		return new ExtendedPredicate(tag, toExprArray(expressions),
				toPredArray(predicates), location, this, extension);
	}

	/**
	 * Returns the conditional expression extension.
	 * 
	 * @return an expression extension
	 * @since 2.0
	 */
	public static IExpressionExtension getCond() {
		return Cond.getCond();
	}

	/**
	 * @since 2.0
	 */
	public static int getTag(IFormulaExtension extension) {
		return ALL_EXTENSIONS.get(extension);
	}
	
	/**
	 * @since 2.0
	 */
	public IFormulaExtension getExtension(int tag) {
		return extensions.get(tag);
	}
	
	/**
	 * Returns all the extensions of the current formula factory.
	 * @since 2.0
	 */
	public Set<IFormulaExtension> getExtensions() {
		final Set<IFormulaExtension> result = new LinkedHashSet<IFormulaExtension>();
		for (IFormulaExtension ext : extensions.values()) {
			result.add(ext);
		}
		return result;
	}
	
	/**
	 * Returns a new associative expression
	 * <p>
	 * {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
	 * @param tag
	 *            the tag of the associative expression
	 * @param children
	 *            the children of the associative expression
	 * @param location
	 *            the location of the associative expression
	 * 
	 * @return a new associative expression
	 */
	public AssociativeExpression makeAssociativeExpression(
			int tag, Expression[] children, SourceLocation location) {
		return new AssociativeExpression(children.clone(), tag, location, this);
	}

	/**
	 * Returns a new associative expression
	 * <p>
	 * {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
	 * </p>
	 * @param tag
	 *            the tag of the associative expression
	 * @param children
	 *            the children of the associative expression
	 * @param location
	 *            the location of the associative expression
	 * 
	 * @return a new associative expression
	 */
	public AssociativeExpression makeAssociativeExpression(
			int tag, Collection<Expression> children, SourceLocation location) {
		return new AssociativeExpression(toExprArray(children), tag, location, this);
	}
	
	/**
	 * Returns a new associative predicate
	 * <p>
	 * {LAND, LOR}
	 * </p>
	 * @param tag
	 *            the tag of the associative predicate
	 * @param predicates
	 *            the children of the associative predicate
	 * @param location
	 *            the location of the associative predicate
	 * 
	 * @return a new associative predicate
	 */
	public AssociativePredicate makeAssociativePredicate(
			int tag, Collection<Predicate> predicates, SourceLocation location) {
		return new AssociativePredicate(toPredArray(predicates), tag, location,
				this);
	}

	/**
	 * Returns a new associative predicate
	 * <p>
	 * {LAND, LOR}
	 * </p>
	 * @param tag
	 *            the tag of the associative predicate
	 * @param predicates
	 *            the children of the associative predicate
	 * @param location
	 *            the location of the associative predicate
	 * 
	 * @return a new associative predicate
	 */
	public AssociativePredicate makeAssociativePredicate(
			int tag, Predicate[] predicates, SourceLocation location) {
		return new AssociativePredicate(predicates.clone(), tag, location, this);
	}

	/**
	 * Returns a new atomic expression
	 * <p>
	 * {INTEGER, NATURAL, NATURAL1, BOOL, TRUE, FALSE, EMPTYSET, KPRED, KSUCC,
	 * KPRJ1_GEN, KPRJ2_GEN, KID_GEN}
	 * 
	 * @param tag
	 *            the tag of the atomic expression
	 * @param location
	 *            the location of the atomic expression
	 * @return a new atomic expression
	 */
	public AtomicExpression makeAtomicExpression(int tag,
			SourceLocation location) {
		return new AtomicExpression(tag, location, null, this);
	}

	/**
	 * Returns a new typed atomic expression.
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
	 * 
	 * @param tag
	 *            the tag of the atomic expression
	 * @param location
	 *            the location of the atomic expression
	 * @param type
	 *            type of this expression. Must be <code>null</code> or
	 *            compatible with the tag
	 * @return a new atomic expression
	 * @since 1.0
	 */
	public AtomicExpression makeAtomicExpression(int tag,
			SourceLocation location, Type type) {
		return new AtomicExpression(tag, location, type, this);
	}

	/**
	 * Returns a new empty set expression.
	 * 
	 * @param type
	 *            the type of the empty set. Must be <code>null</code> or a
	 *            power set type
	 * @param location
	 *            the location of the empty set
	 * 
	 * @return a new empty set expression
	 */
	public AtomicExpression makeEmptySet(Type type, SourceLocation location) {
		return new AtomicExpression(Formula.EMPTYSET, location, type, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * <p>
	 * 
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param value
	 *            after-value for this identifier (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes equal to" assignment
	 */
	public BecomesEqualTo makeBecomesEqualTo(FreeIdentifier ident,
			Expression value, SourceLocation location) {
		return new BecomesEqualTo(new FreeIdentifier[] { ident },
				new Expression[] { value }, location, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * <p>
	 * @param idents
	 *            array of identifier which are assigned to (left-hand side)
	 * @param values
	 *            array of after-values for these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes equal to" assignment
	 */
	public BecomesEqualTo makeBecomesEqualTo(FreeIdentifier[] idents,
			Expression[] values, SourceLocation location) {
		return new BecomesEqualTo(idents.clone(), values.clone(), location,
				this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * <p>
	 * @param idents
	 *            list of identifier which are assigned to (left-hand side)
	 * @param values
	 *            list of after-values for these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes equal to" assignment
	 */
	public BecomesEqualTo makeBecomesEqualTo(Collection<FreeIdentifier> idents,
			Collection<Expression> values, SourceLocation location) {
		return new BecomesEqualTo(toIdentArray(idents), toExprArray(values),
				location, this);
	}

	/**
	 * Returns a new "becomes member of" assignment.
	 * <p>
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param setExpr
	 *            set to which the after-value belongs (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes member of" assignment
	 */
	public BecomesMemberOf makeBecomesMemberOf(FreeIdentifier ident,
			Expression setExpr, SourceLocation location) {
		return new BecomesMemberOf(ident, setExpr, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * <p>
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param primedIdent
	 *            bound identifier declaration for primed identifier (after values)
	 * @param condition
	 *            condition on before and after values of this identifier (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes such that" assignment
	 */
	public BecomesSuchThat makeBecomesSuchThat(FreeIdentifier ident,
			BoundIdentDecl primedIdent, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(new FreeIdentifier[] { ident },
				new BoundIdentDecl[] { primedIdent }, condition, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * <p>
	 * @param idents
	 *            array of identifiers that are assigned to (left-hand side)
	 * @param primedIdents
	 *            array of bound identifier declarations for primed identifiers (after values)
	 * @param condition
	 *            condition on before and after values of these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes such that" assignment
	 */
	public BecomesSuchThat makeBecomesSuchThat(FreeIdentifier[] idents,
			BoundIdentDecl[] primedIdents, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(idents.clone(), primedIdents.clone(),
				condition, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * <p>
	 * @param idents
	 *            list of identifiers that are assigned to (left-hand side)
	 * @param primedIdents
	 *            list of bound identifier declarations for primed identifiers (after values)
	 * @param condition
	 *            condition on before and after values of these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes such that" assignment
	 */
	public BecomesSuchThat makeBecomesSuchThat(Collection<FreeIdentifier> idents,
			Collection<BoundIdentDecl> primedIdents, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(toIdentArray(idents),
				toBIDArray(primedIdents), condition, location, this);
	}

	/**
	 * Returns a new binary expression
	 * <p>
	 * {MAPSTO, REL, TREL, SREL, STREL, PFUN, TFUN, PINJ, TINJ, PSUR, TSUR,
	 * TBIJ, SETMINUS, CPROD, DPROD, PPROD, DOMRES, DOMSUB, RANRES, RANSUB,
	 * UPTO, MINUS, DIV, MOD, EXPN, FUNIMAGE, RELIMAGE}
	 * @param tag
	 *            the tag of the binary expression
	 * @param left
	 *            the left child of the binary expression
	 * @param right
	 *            the right child of the binary expression
	 * @param location
	 *            the location of the binary expression
	 * 
	 * @return a new binary expression
	 */
	public BinaryExpression makeBinaryExpression(int tag,
			Expression left, Expression right, SourceLocation location) {
		return new BinaryExpression(left, right, tag, location, this);
	}

	/**
	 * Returns a new binary predicate
	 * <p>
	 * {LIMP, LEQV}
	 * @param tag
	 *            the tag of the binary predicate
	 * @param left
	 *            the left child of the binary predicate
	 * @param right
	 *            the right child of the binary predicate
	 * @param location
	 *            the location of the binary predicate
	 * 
	 * @return a new binary predicate
	 */
	public BinaryPredicate makeBinaryPredicate(int tag,
			Predicate left, Predicate right, SourceLocation location) {
		return new BinaryPredicate(left, right, tag, location, this);
	}

	/**
	 * Returns a new "bool" expression.
	 * <p>
	 * {KBOOL}
	 * 
	 * @param child
	 *            the child of the bool expression
	 * @param location
	 *            the location of the bool expression
	 * @return a new bool expression
	 */
	public BoolExpression makeBoolExpression(Predicate child, SourceLocation location) {
		return new BoolExpression(child, Formula.KBOOL, location, this);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier,
	 * using as model a free occurrence of the same identifier.
	 * 
	 * @param ident
	 *            a free identifier occurrence
	 * @return a bound identifier declaration
	 */
	@Deprecated
	public BoundIdentDecl makeBoundIdentDecl(FreeIdentifier ident) {
		return new BoundIdentDecl(ident.getName(), Formula.BOUND_IDENT_DECL,
				ident.getSourceLocation(), ident.getType(), this);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifier declaration
	 * @return a bound identifier declaration
	 * 
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public BoundIdentDecl makeBoundIdentDecl(String name,
			SourceLocation location) {
		return new BoundIdentDecl(name, Formula.BOUND_IDENT_DECL, location,
				null, this);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifier declaration
	 * @param type
	 *            the type of this identifier. Can be <code>null</code>.
	 * @return a bound identifier declaration
	 * 
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public BoundIdentDecl makeBoundIdentDecl(String name,
			SourceLocation location, Type type) {
		return new BoundIdentDecl(name, Formula.BOUND_IDENT_DECL, location,
				type, this);
	}

	/**
	 * Returns a new bound occurrence of an identifier.
	 * 
	 * @param index
	 *            the index in the De Bruijn notation. Must be non-negative.
	 * @param location
	 *            the source location of this identifier occurrence
	 * @return a bound identifier occurrence
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 */
	public BoundIdentifier makeBoundIdentifier(int index,
			SourceLocation location) {
		return new BoundIdentifier(index, Formula.BOUND_IDENT, location, null, this);
	}

	/**
	 * Returns a new bound occurrence of an identifier.
	 * 
	 * @param index
	 *            the index in the De Bruijn notation. Must be non-negative.
	 * @param location
	 *            the source location of this identifier occurrence
	 * @param type
	 *            the type of this identifier. Can be <code>null</code>.
	 * @return a bound identifier occurrence
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 */
	public BoundIdentifier makeBoundIdentifier(int index,
			SourceLocation location, Type type) {
		return new BoundIdentifier(index, Formula.BOUND_IDENT, location, type, this);
	}

	/**
	 * Creates a new node representing a free occurrence of an identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifier occurrence
	 * @return a free identifier
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public FreeIdentifier makeFreeIdentifier(String name,
			SourceLocation location) {
		return new FreeIdentifier(name, Formula.FREE_IDENT, location, null, this);
	}
	
	/**
	 * Creates a new node representing a free occurrence of an identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty
	 *            string.
	 * @param location
	 *            the source location of this identifier occurrence
	 * @param type
	 *            the type of this identifier. Can be <code>null</code>. If the
	 *            type contains a given set implying that the name of the free
	 *            identifier is incompatible, then the final type of the free
	 *            identifier will be <code>null</code>.
	 * @return a free identifier
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public FreeIdentifier makeFreeIdentifier(String name,
			SourceLocation location, Type type) {
		return new FreeIdentifier(name, Formula.FREE_IDENT, location, type, this);
	}
	
	/**
	 * Creates a node that contains a primed free identifier.
	 * 
	 * @param identifier An unprimed identifier that is the template for the primed one.
	 * An exception is thrown if it is already primed.
	 * @return The identifier passed as parameter with a prime appended.
	 */
	@Deprecated
	public FreeIdentifier makePrimedFreeIdentifier(FreeIdentifier identifier) {
		
		String name = identifier.getName();
		assert name.charAt(name.length()-1) != '\'';
		
		FreeIdentifier primedIdentifier = makeFreeIdentifier(
				name + "'",
				identifier.getSourceLocation(),
				identifier.getType());
		
		return primedIdentifier;
	}

	/**
	 * Returns a new integer literal.
	 * 
	 * @param literal
	 *            the integer value for this literal
	 * @param location
	 *            the source location of the literal
	 * @return a new integer literal
	 */
	public IntegerLiteral makeIntegerLiteral(BigInteger literal,
			SourceLocation location) {
		return new IntegerLiteral(literal, Formula.INTLIT, location, this);
	}

	/**
	 * Returns a new literal predicate
	 * <p>
	 * {BTRUE, BFALSE}
	 * 
	 * @param tag
	 *            the tag of the predicate
	 * @param location
	 *            the location of the predicate
	 * @return a new literal predicate
	 */
	public LiteralPredicate makeLiteralPredicate(int tag,
			SourceLocation location) {
		return new LiteralPredicate(tag, location, this);
	}

	/**
	 * Returns a new PredicateVariable
	 * 
	 * @param name
	 *            the name of the predicate variable. Must start with '$'.
	 * @param location
	 *            the location of the predicate
	 * @return a new predicate variable
	 * @since 1.2
	 */
	public PredicateVariable makePredicateVariable(String name,
			SourceLocation location) {
		return new PredicateVariable(name, location, this);
	}
	
	/**
	 * Returns a new quantified expression
	 * <p>
	 * {QUNION, QINTER, CSET}
	 * </p>
	 * @param tag
	 *            the tag of the quantified expression
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param expr
	 *            the corresponding expression
	 * @param location
	 *            the location of the quantified expression
	 * @param form
	 *            the written form of the quantified expression
	 * @return a new quantified expression
	 */
	// TODO: maybe make different creators for every form
	public QuantifiedExpression makeQuantifiedExpression(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate pred, Expression expr,
			SourceLocation location, QuantifiedExpression.Form form) {
		return new QuantifiedExpression(expr, pred, boundIdentifiers.clone(),
				tag, location, form, this);
	}

	/**
	 * Returns a new quantified expression
	 * <p>
	 * {QUNION, QINTER, CSET}
	 * </p>
	 * @param tag
	 *            the tag of the quantified expression
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param expr
	 *            the corresponding expression
	 * @param location
	 *            the location of the quantified expression
	 * @param form
	 *            the written form of the quantified expression
	 * @return a new quantified expression
	 */
	public QuantifiedExpression makeQuantifiedExpression(int tag,
			Collection<BoundIdentDecl> boundIdentifiers, Predicate pred, Expression expr,
			SourceLocation location, QuantifiedExpression.Form form) {
		return new QuantifiedExpression(expr, pred,
				toBIDArray(boundIdentifiers), tag, location, form, this);
	}

	/**
	 * Returns a new quantified predicate
	 * <p>
	 * {FORALL, EXISTS}
	 * @param tag
	 *            the tag of the quantified predicate
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param location
	 *            the location of the quantified predicate
	 * @return a new quantified predicate
	 */
	public QuantifiedPredicate makeQuantifiedPredicate(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate pred,
			SourceLocation location) {
		return new QuantifiedPredicate(pred, boundIdentifiers.clone(), tag,
				location, this);
	}

	/**
	 * Returns a new quantified predicate
	 * <p>
	 * {FORALL, EXISTS}
	 * @param tag
	 *            the tag of the quantified predicate
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param location
	 *            the location of the quantified predicate
	 * @return a new quantified predicate
	 */
	public QuantifiedPredicate makeQuantifiedPredicate(int tag,
			Collection<BoundIdentDecl> boundIdentifiers, Predicate pred,
			SourceLocation location) {
		return new QuantifiedPredicate(pred, toBIDArray(boundIdentifiers), tag,
				location, this);
	}
	
	/**
	 * Returns a new relational predicate
	 * <p>
	 * {EQUAL, NOTEQUAL, LT, LE, GT, GE, IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ,
	 * NOTSUBSETEQ}
	 * @param tag
	 *            the tag of the relational predicate
	 * @param left
	 *            the left child of the relational predicate
	 * @param right
	 *            the right child of the relational predicate
	 * @param location
	 *            the location of the relational predicate
	 * 
	 * @return a new relational predicate
	 */
	public RelationalPredicate makeRelationalPredicate(int tag,
			Expression left, Expression right, SourceLocation location) {
		return new RelationalPredicate(left, right, tag, location, this);
	}

	/**
	 * Returns a new singleton set.
	 * <p>
	 * {SETEXT}
	 * </p>
	 * @param expression
	 *            the unique member of this singleton set
	 * @param location
	 *            the location of the set extension
	 * @return a new set extension
	 */
	public SetExtension makeSetExtension(Expression expression,
			SourceLocation location) {
		return new SetExtension(new Expression[] { expression }, location,
				this, null);
	}

	/**
	 * Returns a new set extension
	 * <p>
	 * {SETEXT}
	 * </p>
	 * @param expressions
	 *            the children of the set extension
	 * @param location
	 *            the location of the set extension
	 * @return a new set extension
	 */
	public SetExtension makeSetExtension(Expression[] expressions,
			SourceLocation location) {
		return new SetExtension(expressions.clone(), location, this, null);
	}

	/**
	 * Returns a new set extension containing no child, but having the given
	 * type. This method is hardly useful to end-users, except when writing
	 * tests. One should prefer using
	 * {@link #makeEmptySet(Type, SourceLocation)}.
	 * <p>
	 * {SETEXT}
	 * </p>
	 * 
	 * @param type
	 *            the type of the set extension
	 * @param location
	 *            the location of the set extension
	 * @return a new empty set extension of the given type
	 * @since 2.6
	 */
	public SetExtension makeEmptySetExtension(Type type, SourceLocation location) {
		return new SetExtension(NO_EXPRESSIONS, location, this, type);
	}

	/**
	 * Returns a new set extension
	 * <p>
	 * {SETEXT}
	 * </p>
	 * 
	 * @param expressions
	 *            the children of the set extension
	 * @param location
	 *            the location of the set extension
	 * @return a new set extension
	 */
	public SetExtension makeSetExtension(Collection<Expression> expressions,
			SourceLocation location) {
		return new SetExtension(toExprArray(expressions), location, this, null);
	}

	/**
	 * Returns a new simple predicate
	 * <p>
	 * {KFINITE}
	 * @param tag
	 *            the tag of the simple predicate
	 * @param child
	 *            the child of the simple predicate
	 * @param location
	 *            the location of the simple predicate
	 * 
	 * @return a new simple predicate
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
	 * Returns a new unary expression
	 * <p>
	 * {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID, KMIN,
	 * KMAX, CONVERSE, UNMINUS}
	 * @param tag
	 *            the tag of the unary expression
	 * @param child
	 *            the child of the unary expression
	 * @param location
	 *            the location of the unary expression
	 * 
	 * @return a new unary expression
	 */
	public UnaryExpression makeUnaryExpression(int tag, Expression child,
			SourceLocation location) {
		return new UnaryExpression(child, tag, location, this);
	}

	/**
	 * Returns a new unary predicate
	 * <p>
	 * {NOT}
	 * @param tag
	 *            the tag of the unary predicate
	 * @param child
	 *            the child of the unary predicate
	 * @param location
	 *            the location of the unary predicate
	 * 
	 * @return a new unary predicate
	 */
	public UnaryPredicate makeUnaryPredicate(int tag, Predicate child,
			SourceLocation location) {
		return new UnaryPredicate(child, tag, location, this);
	}
	
	/**
	 * Returns a new multiple predicate.
	 * <p>
	 * {KPARTITION}
	 * 
	 * @param tag
	 *            the tag of the multiple predicate
	 * @param children
	 *            the children of the multiple predicate
	 * @param location
	 *            the location of the multiple predicate
	 * 
	 * @return a new multiple predicate
	 * @since 1.0
	 */
	public MultiplePredicate makeMultiplePredicate(int tag,
			Expression[] children, SourceLocation location) {
		return new MultiplePredicate(children.clone(), tag, location, this);
	}

	/**
	 * Returns a new multiple predicate.
	 * <p>
	 * {KPARTITION}
	 * 
	 * @param tag
	 *            the tag of the multiple predicate
	 * @param children
	 *            the children of the multiple predicate
	 * @param location
	 *            the location of the multiple predicate
	 * 
	 * @return a new multiple predicate
	 * @since 1.0
	 */
	public MultiplePredicate makeMultiplePredicate(int tag,
			Collection<Expression> children, SourceLocation location) {
		return new MultiplePredicate(toExprArray(children), tag, location, this);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 * @deprecated Since the introduction of mathematical language versions,
	 *             clients should use the similar method taking a language
	 *             version as argument
	 * @see #parseAssignment(String, LanguageVersion, Object)
	 */
	@Deprecated
	public IParseResult parseAssignment(String formula) {
		return parseGeneric(formula, V1, null, Assignment.class, false);
	}
	
	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param origin the origin to be traced to the built AST
	 * @return the result of the parse
	 * @deprecated Since the introduction of mathematical language versions,
	 *             clients should use the similar method taking a language
	 *             version as argument
	 * @see #parseAssignment(String, LanguageVersion, Object)
	 */
	@Deprecated
	public IParseResult parseAssignment(String formula, Object origin) {
		return parseGeneric(formula, V1, origin, Assignment.class, false);
	}
	
	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param version the version of the math language used in the formula
	 * @param origin the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 1.0
	 */
	public IParseResult parseAssignment(String formula, LanguageVersion version,
			Object origin) {
		return parseGeneric(formula, version, origin, Assignment.class, false);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 * @deprecated Since the introduction of mathematical language versions,
	 *             clients should use the similar method taking a language
	 *             version as argument
	 * @see #parseExpression(String, LanguageVersion, Object)
	 */
	@Deprecated
	public IParseResult parseExpression(String formula) {
		return parseGeneric(formula, V1, null, Expression.class, false);
	}
	
	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param origin the origin to be traced to the built AST
	 * @return the result of the parse
	 * @deprecated Since the introduction of mathematical language versions,
	 *             clients should use the similar method taking a language
	 *             version as argument
	 * @see #parseExpression(String, LanguageVersion, Object)
	 */
	@Deprecated
	public IParseResult parseExpression(String formula, Object origin) {
		return parseGeneric(formula, V1, origin, Expression.class, false);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param version the version of the math language used in the formula
	 * @param origin the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 1.0
	 */
	public IParseResult parseExpression(String formula, LanguageVersion version,
			Object origin) {
		return parseGeneric(formula, version, origin, Expression.class, false);
	}

	/**
	 * Parses the specified formula and returns the corresponding result. This
	 * method allows predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param version
	 *            the version of the math language used in the formula
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 1.2
	 */
	public IParseResult parseExpressionPattern(String formula,
			LanguageVersion version, Object origin) {
		return parseGeneric(formula, version, origin, Expression.class, true);
	}
	
	/**
	 * Parses the specified predicate and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 * @deprecated Since the introduction of mathematical language versions,
	 *             clients should use the similar method taking a language
	 *             version as argument
	 * @see #parsePredicate(String, LanguageVersion, Object)
	 */
	@Deprecated
	public IParseResult parsePredicate(String formula) {
		return parseGeneric(formula, V1, null, Predicate.class, false);
	}

	/**
	 * Parses the specified predicate and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param origin the origin to be traced to the built AST
	 * @return the result of the parse
	 * @deprecated Since the introduction of mathematical language versions,
	 *             clients should use the similar method taking a language
	 *             version as argument
	 * @see #parsePredicate(String, LanguageVersion, Object)
	 */
	@Deprecated
	public IParseResult parsePredicate(String formula, Object origin) {
		return parseGeneric(formula, V1, origin, Predicate.class, false);
	}
	
	/**
	 * Parses the specified predicate and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param version the version of the math language used in the formula
	 * @param origin the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 1.0
	 */
	public IParseResult parsePredicate(String formula, LanguageVersion version,
			Object origin) {
		return parseGeneric(formula, version, origin, Predicate.class, false);
	}

	/**
	 * Parses the specified predicate and returns the corresponding result.
	 * This method allows predicate variables.
	 * 
	 * @param formula
	 *            the formula to be parsed
	 * @param version
	 *            the version of the math language used in the formula
	 * @param origin
	 *            the origin to be traced to the built AST
	 * @return the result of the parse
	 * @since 1.2
	 */
	public IParseResult parsePredicatePattern(String formula,
			LanguageVersion version, Object origin) {
		return parseGeneric(formula, version, origin, Predicate.class, true);
	}
	
	
	/**
	 * Parses the specified type and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 * @deprecated use {@link #parseType(String, LanguageVersion)}
	 */
	@Deprecated
	public IParseResult parseType(String formula) {
		return parseGeneric(formula, V1, null, Type.class, false);
	}
	
	/**
	 * Parses the specified type and returns the corresponding result. This
	 * method does not allow predicate variables.
	 * 
	 * @param formula the formula to be parsed
	 * @param version the version of the math language used in the formula
	 * @return the result of the parse
	 * @since 1.0
	 */
	public IParseResult parseType(String formula, LanguageVersion version) {
		return parseGeneric(formula, version, null, Type.class, false);
	}

	private final <T> IParseResult parseGeneric(String formula,
			LanguageVersion version, Object origin, Class<T> clazz,
			boolean withPredVars) {
		// TODO consider removing version argument and publishing instance
		// construction methods with a version parameter
		final FormulaFactory factory;
		if (grammar.getVersion() == version) {
			factory = this;
		} else {
			factory = getFactory(version);
		}
		final ParseResult result = new ParseResult(factory, version, origin);
		final Scanner scanner = new Scanner(formula, result, factory.getGrammar());
		final GenParser parser = new GenParser(clazz, scanner, result, withPredVars);
		parser.parse();
		return parser.getResult();
	}

	private static FormulaFactory getFactory(LanguageVersion version) {
		switch (version) {
		case V1:
			return V1_INSTANCE;
		case V2:
			return DEFAULT_INSTANCE;
		default:
			throw new IllegalArgumentException("unknown language version: "
					+ version);
		}
	}

	/**
	 * Returns the type which corresponds to the set of booleans.
	 * 
	 * @return the predefined boolean type
	 */
	public BooleanType makeBooleanType() {
		return new BooleanType();
	}

	/**
	 * Returns the instance of the parametric type of the given constructor with
	 * the given type parameters.
	 * 
	 * @since 2.0
	 */
	public ParametricType makeParametricType(List<Type> typePrms,
			IExpressionExtension typeConstructor) {
		getExtensionTag(typeConstructor);
		return new ParametricType(typeConstructor, toTypeArray(typePrms));
	}

	/**
	 * Returns the instance of the parametric type of the given constructor with
	 * the given type parameters.
	 * 
	 * @since 2.1
	 */
	public ParametricType makeParametricType(Type[] typePrms,
			IExpressionExtension typeConstructor) {
		getExtensionTag(typeConstructor);
		return new ParametricType(typeConstructor, typePrms.clone());
	}

	/**
	 * Returns the type which corresponds to the carrier-set with the given
	 * name.
	 * 
	 * @param name
	 *            name of the type
	 * @return a given type with the given name
	 */
	public GivenType makeGivenType(String name) {
		return new GivenType(name, this);
	}

	/**
	 * Returns the type which corresponds to the set of all integers.
	 * 
	 * @return the predefined integer type
	 */
	public IntegerType makeIntegerType() {
		return new IntegerType();
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
		return new Position(image);
	}

	/**
	 * Returns the type corresponding to the power set of the given type.
	 * 
	 * @param base
	 *            the base type to build upon
	 * @return the power set type of the given type
	 */
	public PowerSetType makePowerSetType(Type base) {
		return new PowerSetType(base);
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
	 */
	public ProductType makeProductType(Type left, Type right) {
		return new ProductType(left, right);
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
	 * Returns whether the given name is a valid identifier name (that is an
	 * identifier name which is not used as a keyword in event-B concrete
	 * syntax).
	 * 
	 * @param name
	 *            the name to test
	 * @return <code>true</code> if the given name is a valid name for an
	 *         identifier
	 */
	public boolean isValidIdentifierName(String name) {
		return Scanner.isValidIdentifierName(this, name);
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

	/**
	 * Upgrades the given assignment string to the given language version.
	 * 
	 * @param input
	 *            an assignment string assumed to be parsable in the language
	 *            version immediately preceding the target version
	 * @param targetVersion
	 *            the desired version after upgrade
	 * @return the result of the upgrade
	 */
	public IUpgradeResult<Assignment> upgradeAssignment(String input,
			LanguageVersion targetVersion) {
		final UpgradeResult<Assignment> result = new UpgradeResult<Assignment>(
				this);
		final VersionUpgrader upgrader = UpgraderFactory.getUpgrader(
				targetVersion, this);
		upgrader.upgradeAssignment(input, result);
		return result;
	}

	/**
	 * Upgrades the given expression string to the given language version.
	 * 
	 * @param input
	 *            an expression string assumed to be parsable in the language
	 *            version immediately preceding the target version
	 * @param targetVersion
	 *            the desired version after upgrade
	 * @return the result of the upgrade
	 */
	public IUpgradeResult<Expression> upgradeExpression(String input,
			LanguageVersion targetVersion) {
		final UpgradeResult<Expression> result = new UpgradeResult<Expression>(
				this);
		final VersionUpgrader upgrader = UpgraderFactory.getUpgrader(
				targetVersion, this);
		upgrader.upgradeExpression(input, result);
		return result;
	}

	/**
	 * Upgrades the given predicate string to the given language version.
	 * 
	 * @param input
	 *            a predicate string assumed to be parsable in the language
	 *            version immediately preceding the target version
	 * @param targetVersion
	 *            the desired version after upgrade
	 * @return the result of the upgrade
	 */
	public IUpgradeResult<Predicate> upgradePredicate(String input,
			LanguageVersion targetVersion) {
		final UpgradeResult<Predicate> result = new UpgradeResult<Predicate>(
				this);
		final VersionUpgrader upgrader = UpgraderFactory.getUpgrader(
				targetVersion, this);
		upgrader.upgradePredicate(input, result);
		return result;
	}

	@Override
	public int hashCode() {
		return 31 + extensions.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FormulaFactory)) {
			return false;
		}
		FormulaFactory other = (FormulaFactory) obj;
		return extensions.equals(other.extensions);
	}

}
