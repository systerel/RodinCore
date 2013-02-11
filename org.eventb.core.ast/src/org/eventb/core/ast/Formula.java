/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract accept method for ISimpleVisitor
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - added support for mathematical extensions
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *     Systerel - added support for specialization
 *     Systerel - add given sets to free identifier cache
 *     Systerel - store factory used to build a formula
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.BindingSubstitution;
import org.eventb.internal.core.ast.BoundIdentifierShifter;
import org.eventb.internal.core.ast.DefaultTypeCheckingRewriter;
import org.eventb.internal.core.ast.FilteringInspector;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.SameTypeRewriter;
import org.eventb.internal.core.ast.SimpleSubstitution;
import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.ast.extension.datatype.DatatypeTranslation;
import org.eventb.internal.core.ast.wd.WDComputer;
import org.eventb.internal.core.ast.wd.WDImprover;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Formula is the abstract base class for all nodes of an event-B formula AST
 * (Abstract Syntax Tree).
 * <p>
 * To instantiate sub-classes of this class, use {@link FormulaFactory}.
 * <p>
 * <b>Important Remark</b>: All AST nodes are immutable. Once a node has been
 * constructed, its contents can't be changed anymore. Except for its type. The
 * type of an AST node must be changed by the type-checker.
 * 
 * @author Laurent Voisin
 * 
 * @param <T>
 *            TODO comment type parameter
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Formula<T extends Formula<T>> {

	// The tag for this AST node.
	private final int tag;

	// The factory that created this node
	// Must include factories of all children
	private final FormulaFactory fac;

	// The source location of this AST node.
	private final SourceLocation location;

	// Hash code for this formula
	private final int hashCode;
	
	// Array of predicate variables occurring in this formula.
	// This is a quasi-final field, it must only be set in a constructor (but
	// not necessarily the Formula constructor).
	private PredicateVariable[] predVars;

	// Sorted array of free identifiers occurring in this formula.
	// This is a quasi-final field, it must only be set in a constructor (but
	// not necessarily the Formula constructor).
	protected FreeIdentifier[] freeIdents;
	
	// Sorted array of bound identifiers occurring in this formula.
	// This is a quasi-final field, it must only be set in a constructor (but
	// not necessarily the Formula constructor).
	protected BoundIdentifier[] boundIdents;
	
	// True iff this formula has been type-checked. When true, any type
	// information associated to this formula is frozen. When false, type
	// information is either inexistant (pure syntactical formula) or transitory
	// (during type-check).
	protected boolean typeChecked;

	/**
	 * <code>NO_TAG</code> is used as a placeholder when one needs to indicate
	 * that a tag value is invalid or absent. It is different from all valid
	 * tags.
	 */
	public final static int NO_TAG = 0;

	/**
	 * <code>FREE_IDENT</code> represents a free occurence of an identifer.
	 * Can only be a FreeIdentifier AST node.
	 */
	public final static int FREE_IDENT = 1;

	/**
	 * <code>BOUND_IDENT_DECL</code> represents a declaration of a bound
	 * identifer (within a quantified formula). Can only be a BoundIdentDecl AST
	 * node.
	 */
	public final static int BOUND_IDENT_DECL = 2;

	/**
	 * <code>BOUND_IDENT</code> represents a bound occurence of an identifer.
	 * Can only be a BoundIdentifier AST node.
	 */
	public final static int BOUND_IDENT = 3;

	/**
	 * <code>INTLIT</code> represents an integer. Can only be an
	 * IntegerLiteral AST node.
	 */
	public final static int INTLIT = 4;

	/**
	 * <code>SETEXT</code> represents a set in extension {E1,E2,E3}. Can only
	 * be a SetExtension AST node.
	 */
	public final static int SETEXT = 5;

	/**
	 * <code>BECOMES_EQUAL_TO</code> represents a "becomes equal to"
	 * assignment, e.g., <code>x ≔ x + 1</code>. Can only be a
	 * BecomesEqualTo AST node.
	 */
	public final static int BECOMES_EQUAL_TO = 6;

	/**
	 * <code>BECOMES_MEMBER_OF</code> represents a "becomes member of"
	 * assignment, e.g., <code>x :∈ S</code>. Can only be a
	 * BecomesMemberOf AST node.
	 */
	public final static int BECOMES_MEMBER_OF = 7;

	/**
	 * <code>BECOMES_SUCH_THAT</code> represents a "becomes such that"
	 * assignment, e.g., <code>x :| x < x'</code>. Can only be a
	 * BecomesSuchThat AST node.
	 */
	public final static int BECOMES_SUCH_THAT = 8;

	/**
	 * <code>PREDICATE_VARIABLE</code> represents a predicate variable.
	 * 
	 * @see PredicateVariable
	 * @since 1.2
	 */
	public final static int PREDICATE_VARIABLE = 9;

	/**
	 * First tag for a relational predicate.
	 * 
	 * @see RelationalPredicate
	 */
	public final static int FIRST_RELATIONAL_PREDICATE = 101;

	/**
	 * <code>EQUAL</code> represents equality.
	 * 
	 * @see RelationalPredicate
	 */
	public final static int EQUAL = FIRST_RELATIONAL_PREDICATE + 0;

	/**
	 * <code>NOTEQUAL</code> represents difference
	 * 
	 * @see RelationalPredicate
	 */
	public final static int NOTEQUAL = FIRST_RELATIONAL_PREDICATE + 1;

	/**
	 * <code>LT</code> represents <
	 * 
	 * @see RelationalPredicate
	 */
	public final static int LT = FIRST_RELATIONAL_PREDICATE + 2;

	/**
	 * <code>LE</code> represents <= Can only be used in a RelationalPredicate
	 * AST node.
	 */
	public final static int LE = FIRST_RELATIONAL_PREDICATE + 3;

	/**
	 * <code>GT</code> represents > Can only be used in a RelationalPredicate
	 * AST node.
	 */
	public final static int GT = FIRST_RELATIONAL_PREDICATE + 4;

	/**
	 * <code>GE</code> represents >= Can only be used in a RelationalPredicate
	 * AST node.
	 */
	public final static int GE = FIRST_RELATIONAL_PREDICATE + 5;

	/**
	 * <code>IN</code> represents \u2208 Can only be used in a
	 * RelationalPredicate AST node.
	 */
	public final static int IN = FIRST_RELATIONAL_PREDICATE + 6;

	/**
	 * <code>NOTIN</code> represents \u2209 Can only be used in a
	 * RelationalPredicate AST node.
	 */
	public final static int NOTIN = FIRST_RELATIONAL_PREDICATE + 7;

	/**
	 * <code>SUBSET</code> represents \u2282 Can only be used in a
	 * RelationalPredicate AST node.
	 */
	public final static int SUBSET = FIRST_RELATIONAL_PREDICATE + 8;

	/**
	 * <code>NOTSUBSET</code> represents \u2284 Can only be used in a
	 * RelationalPredicate AST node.
	 */
	public final static int NOTSUBSET = FIRST_RELATIONAL_PREDICATE + 9;

	/**
	 * <code>SUBSETEQ</code> represents \u2286 Can only be used in a
	 * RelationalPredicate AST node.
	 */
	public final static int SUBSETEQ = FIRST_RELATIONAL_PREDICATE + 10;

	/**
	 * <code>NOTSUBSETEQ</code> represents \u2288 Can only be used in a
	 * RelationalPredicate AST node.
	 */
	public final static int NOTSUBSETEQ = FIRST_RELATIONAL_PREDICATE + 11;

	/**
	 * First tag for a non-associative binary expression.
	 * 
	 * @see BinaryExpression
	 */
	public final static int FIRST_BINARY_EXPRESSION = 201;

	/**
	 * <code>MAPSTO</code> represents \u21a6
	 * 
	 * @see BinaryExpression
	 */
	public final static int MAPSTO = FIRST_BINARY_EXPRESSION + 0;

	/**
	 * <code>REL</code> represents \u2194
	 * 
	 * @see BinaryExpression
	 */
	public final static int REL = FIRST_BINARY_EXPRESSION + 1;

	/**
	 * <code>TREL</code> represents \ue100
	 * 
	 * @see BinaryExpression
	 */
	public final static int TREL = FIRST_BINARY_EXPRESSION + 2;

	/**
	 * <code>SREL</code> represents \ue101
	 * 
	 * @see BinaryExpression
	 */
	public final static int SREL = FIRST_BINARY_EXPRESSION + 3;

	/**
	 * <code>STREL</code> represents \ue102
	 * 
	 * @see BinaryExpression
	 * 
	 */
	public final static int STREL = FIRST_BINARY_EXPRESSION + 4;

	/**
	 * <code>PFUN</code> represents \u21f8
	 * 
	 * @see BinaryExpression
	 */
	public final static int PFUN = FIRST_BINARY_EXPRESSION + 5;

	/**
	 * <code>TFUN</code> represents \u2192
	 * 
	 * @see BinaryExpression
	 */
	public final static int TFUN = FIRST_BINARY_EXPRESSION + 6;

	/**
	 * <code>PINJ</code> represents \u2914
	 * 
	 * @see BinaryExpression
	 */
	public final static int PINJ = FIRST_BINARY_EXPRESSION + 7;

	/**
	 * <code>TINJ</code> represents \u21a3
	 * 
	 * @see BinaryExpression
	 */
	public final static int TINJ = FIRST_BINARY_EXPRESSION + 8;

	/**
	 * <code>PSUR</code> represents \u2900
	 * 
	 * @see BinaryExpression
	 */
	public final static int PSUR = FIRST_BINARY_EXPRESSION + 9;

	/**
	 * <code>TSUR</code> represents \u21a0
	 * 
	 * @see BinaryExpression
	 */
	public final static int TSUR = FIRST_BINARY_EXPRESSION + 10;

	/**
	 * <code>TBIJ</code> represents \u2916
	 * 
	 * @see BinaryExpression
	 */
	public final static int TBIJ = FIRST_BINARY_EXPRESSION + 11;

	/**
	 * <code>SETMINUS</code> represents \u2216
	 * 
	 * @see BinaryExpression
	 */
	public final static int SETMINUS = FIRST_BINARY_EXPRESSION + 12;

	/**
	 * <code>CPROD</code> represents \u00d7
	 * 
	 * @see BinaryExpression
	 */
	public final static int CPROD = FIRST_BINARY_EXPRESSION + 13;

	/**
	 * <code>DPROD</code> represents \u2297
	 * 
	 * @see BinaryExpression
	 */
	public final static int DPROD = FIRST_BINARY_EXPRESSION + 14;

	/**
	 * <code>PPROD</code> represents \u2225
	 * 
	 * @see BinaryExpression
	 */
	public final static int PPROD = FIRST_BINARY_EXPRESSION + 15;

	/**
	 * <code>DOMRES</code> represents \u25c1
	 * 
	 * @see BinaryExpression
	 */
	public final static int DOMRES = FIRST_BINARY_EXPRESSION + 16;

	/**
	 * <code>DOMSUB</code> represents \u2a64
	 * 
	 * @see BinaryExpression
	 */
	public final static int DOMSUB = FIRST_BINARY_EXPRESSION + 17;

	/**
	 * <code>RANRES</code> represents \u25b7
	 * 
	 * @see BinaryExpression
	 */
	public final static int RANRES = FIRST_BINARY_EXPRESSION + 18;

	/**
	 * <code>RANSUB</code> represents \u2a65
	 * 
	 * @see BinaryExpression
	 */
	public final static int RANSUB = FIRST_BINARY_EXPRESSION + 19;

	/**
	 * <code>UPTO</code> represents \u2025
	 * 
	 * @see BinaryExpression
	 */
	public final static int UPTO = FIRST_BINARY_EXPRESSION + 20;

	/**
	 * <code>MINUS</code> represents - Can only be used in a AST node.
	 */
	public final static int MINUS = FIRST_BINARY_EXPRESSION + 21;

	/**
	 * <code>DIV</code> represents integer division.
	 * 
	 * @see BinaryExpression
	 */
	public final static int DIV = FIRST_BINARY_EXPRESSION + 22;

	/**
	 * <code>MOD</code> represents mod
	 * 
	 * @see BinaryExpression
	 */
	public final static int MOD = FIRST_BINARY_EXPRESSION + 23;

	/**
	 * <code>EXPN</code> represents integer exponentiation.
	 * 
	 * @see BinaryExpression
	 */
	public final static int EXPN = FIRST_BINARY_EXPRESSION + 24;

	/**
	 * <code>FUNIMAGE</code> represents function application.
	 * 
	 * @see BinaryExpression
	 */
	public final static int FUNIMAGE = FIRST_BINARY_EXPRESSION + 25;

	/**
	 * <code>RELIMAGE</code> represents relational image []
	 * 
	 * @see BinaryExpression
	 */
	public final static int RELIMAGE = FIRST_BINARY_EXPRESSION + 26;

	/**
	 * First tag for a non-associative binary predicate.
	 * 
	 * @see BinaryPredicate
	 */
	public final static int FIRST_BINARY_PREDICATE = 251;

	/**
	 * <code>LIMP</code> represents \u21d2
	 * 
	 * @see BinaryPredicate
	 */
	public final static int LIMP = FIRST_BINARY_PREDICATE + 0;

	/**
	 * <code>LEQV</code> represents \u21d4
	 * 
	 * @see BinaryPredicate
	 */
	public final static int LEQV = FIRST_BINARY_PREDICATE + 1;

	/**
	 * First tag for an associative binary expression.
	 * 
	 * @see AssociativeExpression
	 */
	public final static int FIRST_ASSOCIATIVE_EXPRESSION = 301;

	/**
	 * <code>BUNION</code> represents \u222a
	 * 
	 * @see AssociativeExpression
	 */
	public final static int BUNION = FIRST_ASSOCIATIVE_EXPRESSION + 0;

	/**
	 * <code>BINTER</code> represents \u2229
	 * 
	 * @see AssociativeExpression
	 */
	public final static int BINTER = FIRST_ASSOCIATIVE_EXPRESSION + 1;

	/**
	 * <code>BCOMP</code> represents \u2218
	 * 
	 * @see AssociativeExpression
	 */
	public final static int BCOMP = FIRST_ASSOCIATIVE_EXPRESSION + 2;

	/**
	 * <code>FCOMP</code> represents \u003b
	 * 
	 * @see AssociativeExpression
	 */
	public final static int FCOMP = FIRST_ASSOCIATIVE_EXPRESSION + 3;

	/**
	 * <code>OVR</code> represents \ue103
	 * 
	 * @see AssociativeExpression
	 */
	public final static int OVR = FIRST_ASSOCIATIVE_EXPRESSION + 4;

	/**
	 * <code>PLUS</code> represents +
	 * 
	 * @see AssociativeExpression
	 */
	public final static int PLUS = FIRST_ASSOCIATIVE_EXPRESSION + 5;

	/**
	 * <code>MUL</code> represents *
	 * 
	 * @see AssociativeExpression
	 */
	public final static int MUL = FIRST_ASSOCIATIVE_EXPRESSION + 6;

	/**
	 * First tag for an associative binary predicate.
	 * 
	 * @see AssociativePredicate
	 */
	public final static int FIRST_ASSOCIATIVE_PREDICATE = 351;

	/**
	 * <code>LAND</code> represents \u2227
	 * 
	 * @see AssociativePredicate
	 */
	public final static int LAND = FIRST_ASSOCIATIVE_PREDICATE + 0;

	/**
	 * <code>LOR</code> represents \u2228
	 * 
	 * @see AssociativePredicate
	 */
	public final static int LOR = FIRST_ASSOCIATIVE_PREDICATE + 1;

	/**
	 * First tag for an atomic expression.
	 * 
	 * @see AtomicExpression
	 */
	public final static int FIRST_ATOMIC_EXPRESSION = 401;

	/**
	 * <code>INTEGER</code> represents \u2124
	 * 
	 * @see AtomicExpression
	 */
	public final static int INTEGER = FIRST_ATOMIC_EXPRESSION + 0;

	/**
	 * <code>NATURAL</code> represents \u2115
	 * 
	 * @see AtomicExpression
	 */
	public final static int NATURAL = FIRST_ATOMIC_EXPRESSION + 1;

	/**
	 * <code>NATURAL1</code> represents \u21151
	 * 
	 * @see AtomicExpression
	 */
	public final static int NATURAL1 = FIRST_ATOMIC_EXPRESSION + 2;

	/**
	 * <code>BOOL</code> represents BOOL
	 * 
	 * @see AtomicExpression
	 */
	public final static int BOOL = FIRST_ATOMIC_EXPRESSION + 3;

	/**
	 * <code>TRUE</code> represents TRUE
	 * 
	 * @see AtomicExpression
	 */
	public final static int TRUE = FIRST_ATOMIC_EXPRESSION + 4;

	/**
	 * <code>FALSE</code> represents FALSE
	 * 
	 * @see AtomicExpression
	 */
	public final static int FALSE = FIRST_ATOMIC_EXPRESSION + 5;

	/**
	 * <code>EMPTYSET</code> represents \u2205
	 * 
	 * @see AtomicExpression
	 */
	public final static int EMPTYSET = FIRST_ATOMIC_EXPRESSION + 6;

	/**
	 * <code>KPRED</code> represents pred
	 * 
	 * @see AtomicExpression
	 */
	public final static int KPRED = FIRST_ATOMIC_EXPRESSION + 7;

	/**
	 * <code>KSUCC</code> represents succ
	 * 
	 * @see AtomicExpression
	 */
	public final static int KSUCC = FIRST_ATOMIC_EXPRESSION + 8;

	/**
	 * <code>KPRJ1_GEN</code> represents prj1
	 * 
	 * @see AtomicExpression
	 * @since 1.0
	 */
	public static final int KPRJ1_GEN = FIRST_ATOMIC_EXPRESSION + 9;

	/**
	 * <code>KPRJ2_GEN</code> represents prj2
	 * 
	 * @see AtomicExpression
	 * @since 1.0
	 */
	public static final int KPRJ2_GEN = FIRST_ATOMIC_EXPRESSION + 10;

	/**
	 * <code>KID_GEN</code> represents id
	 * 
	 * @see AtomicExpression
	 * @since 1.0
	 */
	public static final int KID_GEN = FIRST_ATOMIC_EXPRESSION + 11;

	/**
	 * <code>KBOOL</code> represents a <code>bool</code> expression.
	 * 
	 * @see BoolExpression
	 */
	public final static int KBOOL = 601;

	/**
	 * First tag for a literal predicate.
	 * 
	 * @see LiteralPredicate
	 */
	public final static int FIRST_LITERAL_PREDICATE = 610;

	/**
	 * <code>BTRUE</code> represents \u22a4
	 * 
	 * @see LiteralPredicate
	 */
	public final static int BTRUE = FIRST_LITERAL_PREDICATE + 0;

	/**
	 * <code>BFALSE</code> represents \u22a5
	 * 
	 * @see LiteralPredicate
	 */
	public final static int BFALSE = FIRST_LITERAL_PREDICATE + 1;

	/**
	 * First tag for a simple predicate.
	 * 
	 * @see SimplePredicate
	 */
	public final static int FIRST_SIMPLE_PREDICATE = 620;

	/**
	 * <code>KFINITE</code> represents a "finite" predicate.
	 * 
	 * @see SimplePredicate
	 */
	public final static int KFINITE = FIRST_SIMPLE_PREDICATE + 0;

	/**
	 * First tag for a unary predicate.
	 * 
	 * @see UnaryPredicate
	 */
	public final static int FIRST_UNARY_PREDICATE = 701;

	/**
	 * <code>NOT</code> represents \u00ac
	 * 
	 * @see UnaryPredicate
	 */
	public final static int NOT = FIRST_UNARY_PREDICATE + 0;

	/**
	 * First tag for a unary expression.
	 * 
	 * @see UnaryExpression
	 */
	public final static int FIRST_UNARY_EXPRESSION = 751;

	/**
	 * <code>KCARD</code> represents card
	 * 
	 * @see UnaryExpression
	 */
	public final static int KCARD = FIRST_UNARY_EXPRESSION + 0;

	/**
	 * <code>POW</code> represents \u2119
	 * 
	 * @see UnaryExpression
	 */
	public final static int POW = FIRST_UNARY_EXPRESSION + 1;

	/**
	 * <code>POW1</code> represents \u21191
	 * 
	 * @see UnaryExpression
	 */
	public final static int POW1 = FIRST_UNARY_EXPRESSION + 2;

	/**
	 * <code>KUNION</code> represents union
	 * 
	 * @see UnaryExpression
	 */
	public final static int KUNION = FIRST_UNARY_EXPRESSION + 3;

	/**
	 * <code>KINTER</code> represents inter
	 * 
	 * @see UnaryExpression
	 */
	public final static int KINTER = FIRST_UNARY_EXPRESSION + 4;

	/**
	 * <code>KDOM</code> represents dom
	 * 
	 * @see UnaryExpression
	 */
	public final static int KDOM = FIRST_UNARY_EXPRESSION + 5;

	/**
	 * <code>KRAN</code> represents ran
	 * 
	 * @see UnaryExpression
	 */
	public final static int KRAN = FIRST_UNARY_EXPRESSION + 6;

	/**
	 * <code>KPRJ1</code> represents prj1
	 * 
	 * @see UnaryExpression
	 * @see #KPRJ1_GEN
	 * @deprecated This operator has become generic in version 2 of the
	 *             mathematical language
	 */
	@Deprecated
	public final static int KPRJ1 = FIRST_UNARY_EXPRESSION + 7;

	/**
	 * <code>KPRJ2</code> represents prj2
	 * 
	 * @see UnaryExpression
	 * @see #KPRJ2_GEN
	 * @deprecated This operator has become generic in version 2 of the
	 *             mathematical language
	 */
	@Deprecated
	public final static int KPRJ2 = FIRST_UNARY_EXPRESSION + 8;

	/**
	 * <code>KID</code> represents id
	 * 
	 * @see UnaryExpression
	 * @see #KID_GEN
	 * @deprecated This operator has become generic in version 2 of the
	 *             mathematical language
	 */
	@Deprecated
	public final static int KID = FIRST_UNARY_EXPRESSION + 9;

	/**
	 * <code>KMIN</code> represents min
	 * 
	 * @see UnaryExpression
	 */
	public final static int KMIN = FIRST_UNARY_EXPRESSION + 10;

	/**
	 * <code>KMAX</code> represents max
	 * 
	 * @see UnaryExpression
	 */
	public final static int KMAX = FIRST_UNARY_EXPRESSION + 11;

	/**
	 * <code>CONVERSE</code> represents ~
	 * 
	 * @see UnaryExpression
	 */
	public final static int CONVERSE = FIRST_UNARY_EXPRESSION + 12;

	/**
	 * <code>UNMINUS</code> represents the unary minus -
	 * 
	 * @see UnaryExpression
	 */
	public final static int UNMINUS = FIRST_UNARY_EXPRESSION + 13;

	/**
	 * First tag for a quantified expression.
	 * 
	 * @see QuantifiedExpression
	 */
	public final static int FIRST_QUANTIFIED_EXPRESSION = 801;

	/**
	 * <code>QUNION</code> represents \u22c3
	 * 
	 * @see QuantifiedExpression
	 */
	public final static int QUNION = FIRST_QUANTIFIED_EXPRESSION + 0;

	/**
	 * <code>QINTER</code> represents \u22c2
	 * 
	 * @see QuantifiedExpression
	 */
	public final static int QINTER = FIRST_QUANTIFIED_EXPRESSION + 1;

	/**
	 * <code>CSET</code> represents a comprehension set (either { E | P } or {
	 * L \u00b7 P | E } or \u03bb M \u00b7 P | E)
	 * 
	 * @see QuantifiedExpression
	 */
	public final static int CSET = FIRST_QUANTIFIED_EXPRESSION + 2;

	/**
	 * First tag for a quantified predicate.
	 * 
	 * @see QuantifiedPredicate
	 */
	public final static int FIRST_QUANTIFIED_PREDICATE = 851;

	/**
	 * <code>FORALL</code> represents \u2200
	 * 
	 * @see QuantifiedPredicate
	 */
	public final static int FORALL = FIRST_QUANTIFIED_PREDICATE + 0;

	/**
	 * <code>EXISTS</code> represents \u2203
	 * 
	 * @see QuantifiedPredicate
	 */
	public final static int EXISTS = FIRST_QUANTIFIED_PREDICATE + 1;
	
	/**
	 * First tag for a multiple predicate.
	 * 
	 * @see MultiplePredicate
	 * @since 1.0
	 */
	public final static int FIRST_MULTIPLE_PREDICATE = 901;

	/**
	 * <code>KPARTITION</code> represents a "partition" predicate.
	 * 
	 * @see MultiplePredicate
	 * @since 1.0
	 */
	public final static int KPARTITION = FIRST_MULTIPLE_PREDICATE + 0;

	/**
	 * First tag for extended formulae.
	 * 
	 * @see ExtendedPredicate
	 * @see ExtendedExpression
	 * @since 2.0
	 */
	public static final int FIRST_EXTENSION_TAG = 1000;

	protected final static BoundIdentDecl[] NO_BOUND_IDENT_DECL =
		new BoundIdentDecl[0];
	
	protected final static FreeIdentifier[] NO_FREE_IDENT =
		new FreeIdentifier[0];

	protected final static BoundIdentifier[] NO_BOUND_IDENT =
		new BoundIdentifier[0];
	
	private final static PredicateVariable[] NO_PRED_VAR =
		new PredicateVariable[0];

	protected final static String[] NO_STRING = new String[0];

	// Internal constructor for derived classes (with location).
	/**
	 * @since 3.0
	 */
	protected Formula(int tag, FormulaFactory fac, SourceLocation location,
			int hashCode) {
		this.tag = tag;
		this.fac = fac;
		this.location = location;
		this.hashCode = combineHashCodes(hashCode, tag);
	}
	
	/**
	 * Returns the combination of two hash codes.
	 * 
	 * @param hash1
	 *            a hash code
	 * @param hash2
	 *            another hash code
	 * @return a combination of the two hash codes
	 */
	protected static int combineHashCodes(int hash1, int hash2) {
		return hash1 * 17 + hash2;
	}

	/**
	 * Returns the combination of three hash codes.
	 * 
	 * @param hash1
	 *            a hash code
	 * @param hash2
	 *            another hash code
	 * @param hash3
	 *            yet another hash code
	 * @return a combination of the three hash codes
	 */
	protected static int combineHashCodes(int hash1, int hash2, int hash3) {
		return combineHashCodes(combineHashCodes(hash1, hash2), hash3);
	}

	/**
	 * Returns the combination of some objects' hash codes.
	 * 
	 * @param objects
	 *            some objects
	 * @return a combination of the objects' hash codes
	 */
	protected static int combineHashCodes(Object[] objects) {
		int result = 0;
		for (final Object object: objects) {
			result = combineHashCodes(result, object.hashCode());
		}
		return result;
	}

	/**
	 * Returns the combination of some objects' hash codes.
	 * 
	 * @param xs
	 *            some objects
	 * @param ys
	 *            some objects
	 * @return a combination of the objects' hash codes
	 */
	protected static int combineHashCodes(Object[] xs, Object[] ys) {
		return combineHashCodes(combineHashCodes(xs), combineHashCodes(ys));
	}

	/**
	 * Returns the combination of some formulas' hash codes.
	 * 
	 * @param formulas
	 *            some formulas
	 * @return a combination of the formulas' hash codes
	 */
	protected static <T extends Formula<T>> int combineHashCodes(
			Collection<? extends T> formulas) {
		int result = 0;
		for (T formula: formulas) {
			result = combineHashCodes(result, formula.hashCode());
		}
		return result;
	}
	
	private static <S extends Formula<?>> ArrayList<FreeIdentifier[]> getFreeIdentifiers(
			S... formulas) {
		final ArrayList<FreeIdentifier[]> lists = new ArrayList<FreeIdentifier[]>(
				formulas.length);
		for (Formula<?> formula : formulas) {
			final FreeIdentifier[] freeIdents = formula.freeIdents;
			if (freeIdents.length != 0)
				lists.add(freeIdents);
		}
		return lists;
	}

	/**
	 * Merges the list of free identifiers of the given formulas.
	 * 
	 * @param formulas
	 *            formulas whose free identifiers need to be merged
	 * @return a sorted merged array of identifiers or <code>null</code> if an
	 *         error occurred
	 */
	protected static <S extends Formula<?>> IdentListMerger mergeFreeIdentifiers(
			S... formulas) {
		
		ArrayList<FreeIdentifier[]> lists = getFreeIdentifiers(formulas);
		// Ensure the list is not empty.
		if (lists.size() == 0) {
			lists.add(NO_FREE_IDENT);
		}
		return IdentListMerger.makeMerger(lists);
	}

	private static <S extends Formula<?>> ArrayList<BoundIdentifier[]> getBoundIdentifiers(
			S[] formulas) {
		ArrayList<BoundIdentifier[]> lists = new ArrayList<BoundIdentifier[]>(
				formulas.length);
		for (Formula<?> formula : formulas) {
			final BoundIdentifier[] boundIdents = formula.boundIdents;
			if (boundIdents.length != 0)
				lists.add(boundIdents);
		}
		return lists;
	}

	/**
	 * Merges the list of bound identifiers of the given formulas.
	 * 
	 * @param formulas
	 *            formulas whose bound identifiers need to be merged
	 * @return a merger for arrays of identifiers
	 */
	protected static <S extends Formula<?>> IdentListMerger mergeBoundIdentifiers(
			S[] formulas) {
		
		ArrayList<BoundIdentifier[]> lists = getBoundIdentifiers(formulas);
		// Ensure the list is not empty.
		if (lists.size() == 0) {
			lists.add(NO_BOUND_IDENT);
		}
		return IdentListMerger.makeMerger(lists);
	}
	
	/**
	 * Computes the cache of predicate variable occurring in this formula.
	 * 
	 * @param children
	 *            children of this formula whose predicate variable caches need
	 *            to be merged
	 * @since 1.2
	 */
	protected void setPredicateVariableCache(Formula<?>... children) {
		if (children.length == 0) {
			predVars = NO_PRED_VAR;
			return;
		}

		if (children.length == 1) {
			final Formula<?> child = children[0];
			if (child.getTag() == PREDICATE_VARIABLE) {
				predVars = new PredicateVariable[] { (PredicateVariable) child };
			} else {
				predVars = child.predVars;
			}
			return;
		}

		final List<PredicateVariable> result = new ArrayList<PredicateVariable>();
		for (final Formula<?> child : children) {
			for (final PredicateVariable predVar : child.predVars) {
				if (!result.contains(predVar)) {
					result.add(predVar);
				}
			}
		}
		predVars = result.toArray(new PredicateVariable[result.size()]);
	}

	/**
	 * Ensures that the formula factory of the given type is the same as the
	 * formula factory of this formula. Throws an
	 * {@link IllegalArgumentException} otherwise.
	 * 
	 * @param type
	 *            type to check
	 * @throws IllegalArgumentException
	 *             if the given type has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Type type) {
		if (type == null) {
			return;
		}
		final FormulaFactory typeFactory = type.getFactory();
		if (this.fac != typeFactory) {
			throw new IllegalArgumentException("The type " + type
					+ " has an incompatible factory: " + typeFactory
					+ " instead of: " + this.fac);
		}
	}

	/**
	 * Ensures that the formula factory of each of the given formulas is the
	 * same as the formula factory of this formula. Throws an
	 * {@link IllegalArgumentException} otherwise.
	 * 
	 * @param children
	 *            formulas to check
	 * @throws IllegalArgumentException
	 *             if any of the given formulas has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Formula<?>[] children) {
		for (final Formula<?> child : children) {
			ensureSameFactory(child);
		}
	}

	/**
	 * Ensures that the formula factory of each of the given formulas is the
	 * same as the formula factory of this formula. Throws an
	 * {@link IllegalArgumentException} otherwise.
	 * 
	 * @param left
	 *            formula to check
	 * @param right
	 *            formula to check
	 * @throws IllegalArgumentException
	 *             if any of the given formulas has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Formula<?> left, Formula<?> right) {
		ensureSameFactory(left);
		ensureSameFactory(right);
	}

	/**
	 * Ensures that the formula factory of the given formulas is the same as the
	 * formula factory of this formula. Throws an
	 * {@link IllegalArgumentException} otherwise.
	 * 
	 * @param child
	 *            formula to check
	 * @throws IllegalArgumentException
	 *             if the given formulas has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Formula<?> child) {
		final FormulaFactory childFactory = child.getFactory();
		if (this.fac != childFactory) {
			throw new IllegalArgumentException("The child " + child
					+ " has an incompatible factory: " + childFactory
					+ " instead of: " + this.fac);
		}
	}

	/**
	 * Ensures that the formula factory of this formula and the factory of this
	 * type environment are the same. If it is not the case an
	 * {@link IllegalArgumentException} exception is raised.
	 */
	private void ensureSameFactory(ITypeEnvironment typEnv) {
		final FormulaFactory otherFactory = typEnv.getFormulaFactory();
		if (this.fac != otherFactory) {
			throw new IllegalArgumentException("The environment " + typEnv
					+ " has an incompatible factory: " + otherFactory
					+ " instead of: " + this.fac);
		}
	}

	/**
	 * Returns the tag of this AST node.
	 * <p>
	 * Each node has an attached tag that represents the operator associated to
	 * it. This tag is immutable. The tags can be found in class {@link Formula}
	 * 
	 * @return Returns the tag.
	 */
	public final int getTag() {
		return tag;
	}

	/**
	 * Returns the source location of this AST node.
	 * <p>
	 * Each node parsed from a string has an attached source location that
	 * describes where it is located in the original string. This location is
	 * immutable.
	 * </p>
	 * <p>
	 * If the formula was not created when parsing a string, the location is a
	 * <code>null</code> reference.
	 * </p>
	 * 
	 * @return Returns the source location or <code>null</code>.
	 */
	public final SourceLocation getSourceLocation() {
		return location;
	}

	/**
	 * Returns a string representation of the formula.
	 * <p>
	 * The string contains as many parenthesis as possible.
	 * </p>
	 * 
	 * @return a string representation of this formula.
	 */
	public final String toStringFullyParenthesized() {
		final StringBuilder builder = new StringBuilder();
		final ToStringFullParenMediator strMed = new ToStringFullParenMediator(this, 
				getFactory(), builder, NO_STRING, false);
		strMed.forward(getTypedThis());
		return builder.toString();
	}

	/**
	 * Returns the string representation of the formula.
	 * <p>
	 * The string contains a minimal number of parenthesis, that is only
	 * parenthesis that are needed for the formula to be parsed again into the
	 * same AST.
	 * </p>
	 * <p>
	 * The string representation doesn't contain any type information.
	 * </p>
	 * 
	 * @return Returns the string representation of this formula.
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		final ToStringMediator strMed = new ToStringMediator(this, getFactory(),
				builder, NO_STRING, false, false);
		strMed.forward(getTypedThis());
		return builder.toString();
	}

	/**
	 * Returns the string representation of the formula, including some type
	 * information.
	 * <p>
	 * The string representation is the same as that returned by
	 * {@link #toString()}, but with type information added to generic atomic
	 * expressions (empty sets and bound identifier declarations).
	 * </p>
	 * 
	 * @return Returns the string representation of this formula with type
	 *         information.
	 */
	public final String toStringWithTypes() {
		final StringBuilder builder = new StringBuilder();
		final ToStringMediator strMed = new ToStringMediator(this, getFactory(),
				builder, NO_STRING, true, false);
		strMed.forward(getTypedThis());
		return builder.toString();
	}
	
	/**
	 * @since 2.0
	 */
	protected abstract void toString(IToStringMediator mediator);

	/**
	 * @since 2.0
	 */
	protected abstract int getKind(KindMediator mediator);
	
	/**
	 * Returns the formula factory used to build this formula.
	 * 
	 * @return the formula factory used to build this formula
	 * 
	 * @since 3.0
	 */
	public final FormulaFactory getFactory() {
		return fac;
	}
	
	/**
	 * Indicates whether some other formula is identical to this one.
	 * <p>
	 * Comparison is done on the recursive structure of both formulas (deep
	 * equality). However, equality is considered modulo alpha-conversion (names
	 * of bound identifiers are ignored). Also, all forms of the same quantified
	 * expressions are considered equals. Finally, source locations are
	 * considered as pure decorations and are therefore not taken into account.
	 * </p>
	 * <p>
	 * For instance, the following formulas are considered equal (as they are
	 * alpha-equivalent):
	 * <pre>
	 *       ∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ x + y ∈ ℕ
	 *       ∀z,t·z ∈ ℕ ∧ t ∈ ℕ ⇒ z + t ∈ ℕ
	 * </pre>
	 * </p>
	 * <p>
	 * Similarly, the following formulas are considered equal (as they are
	 * various forms of the same quantified expression);
	 * <pre>
	 *       λx·x ∈ ℕ | x + 1
	 *       {x ↦ (x + 1) | x ∈ ℕ}
	 *       {x·x ∈ ℕ | x ↦ (x + 1)}
	 * </pre>
	 * </p>
	 * <p>
	 * On the contrary, the following formulas are not equal (as <code>x</code>
	 * and <code>y</code> are interchanged in the consequent):
	 * <pre>
	 *       ∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ x + y ∈ ℕ
	 *       ∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ y + x ∈ ℕ
	 * </pre>
	 * </p>
	 * <p>
	 * If either formula has been type-checked, then the comparison takes into
	 * account the types of formulas.  Otherwise, the comparison is only done
	 * at a syntactical level.
	 * </p>
	 * <p>
	 * For instance, two copies of the formula <code>x = ∅</code> are considered
	 * as equal when they are not typed.  However, if one is typed (where
	 * <code>x</code> is a set of booleans) and not the other, they're not equal
	 * anymore.  Similarly, if one has been type-checked with <code>x</code> being
	 * a set of booleans and the other with <code>x</code> being a set of integers,
	 * they are not equal.
	 * </p>
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> iff this formula is the same as the given argument
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Formula<?> other = (Formula<?>) obj;
		// Must be done here since this check is expected by sub-classes
		if (this.tag != other.tag) {
			return false;
		}
		if (this.hashCode != other.hashCode) {
			return false;
		}
		return equalsInternal(other);
	}

	/**
	 * Returns the flattened form of this formula.
	 * <p>
	 * Flattening consists in performing the following operations on the
	 * formula:
	 * <ul>
	 * <li>Expanding inline the children of an associative expression which are
	 * of the same kind. For instance, "(x+x)+x" becomes "x+x+x".</li>
	 * <li>Removing unused bound identifier declarations. For instance
	 * "∀x,y·x=a" becomes "∀x·x=a".
	 * <li>Regrouping quantifiers of quantified predicated of the same kind.
	 * For instance "∀x·∀y·x=y" becomes "∀x,y·x=y".
	 * <li>Replacing empty set extensions by empty sets: expression "{}"
	 * becomes "∅".</li>
	 * <li>Replacing an integer literal preceded by an unary minus by an
	 * integer literal with the opposite value.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * If this formula was already flattened, then a reference to this formula
	 * is returned (rather than a copy of this formula).
	 * </p>
	 * <p>
	 * Flattening is not supported for assignments.
	 * </p>
	 * 
	 * @param factory
	 *            a formula factory
	 * @return the formula in its flattened form
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment.
	 */
	public final T flatten(FormulaFactory factory) {
		IFormulaRewriter rewriter = new DefaultRewriter(true);
		return rewrite(rewriter);
	}

	/**
	 * Returns a list of all identifiers that occur free in this formula.
	 * <p>
	 * This method uses directly the type-checker cache, so that it doesn't have
	 * to traverse the formula. Only one instance of each identifier occurring
	 * free in this formula is reported, hence all elements of the returned
	 * array are different.
	 * </p>
	 * <p>
	 * Since 3.0, all identifiers of given types occurring within this formula
	 * are also included in the result.
	 * </p>
	 * <p>
	 * Clients having special requirements on the order of identifiers should
	 * rather use {@link #getSyntacticallyFreeIdentifiers()} to compute a sorted
	 * list of free identifiers of this formula (that latter method will indeed
	 * traverse the formula).
	 * </p>
	 * 
	 * @return an array of all free identifiers occurring in this formula
	 * 
	 * @see #getSyntacticallyFreeIdentifiers()
	 */
	public final FreeIdentifier[] getFreeIdentifiers() {
		return freeIdents.clone();
	}

	/**
	 * Returns a list of all identifiers that occur bound and are not declared
	 * within this formula.
	 * <p>
	 * This method uses directly the type-checker cache, so that it doesn't have
	 * to traverse the formula.
	 * </p>
	 * <p>
	 * The identifiers returned are the identifiers with dangling de Bruijn
	 * indices. If the same identifier occurs more than once, only one
	 * occurrence is reported. Thus, all elements of the returned array are
	 * different.
	 * </p>
	 * 
	 * @return an array of all identifiers that occur bound and are not declared
	 *         within this formula
	 */
	public final BoundIdentifier[] getBoundIdentifiers() {
		return boundIdents.clone();
	}

	/**
	 * Returns a list of all identifiers that occur free in this formula.
	 * <p>
	 * The actual elements of this list are the first (leftmost) free occurrence
	 * of each identifier. It is sorted in left-to-right order when reading the
	 * formula.
	 * </p>
	 * <p>
	 * If they have no requirements on the order of the free identifiers
	 * returned, clients should rather use {@link #getFreeIdentifiers()} to
	 * compute the array of free identifiers.
	 * </p>
	 * 
	 * @return an array of all first free occurrences of identifiers.
	 * 
	 * @see #getFreeIdentifiers()
	 */
	public final FreeIdentifier[] getSyntacticallyFreeIdentifiers() {
		LinkedHashSet<FreeIdentifier> freeIdentSet = new LinkedHashSet<FreeIdentifier>();
		collectFreeIdentifiers(freeIdentSet);
		FreeIdentifier[] model = new FreeIdentifier[freeIdentSet.size()];
		return freeIdentSet.toArray(model);
	}

	/**
	 * Returns a list of all predicate variables that occur within this formula.
	 * <p>
	 * This method uses a cache, so that it doesn't have to traverse this formula
	 * each time it is called.
	 * </p>
	 * 
	 * @return an array of all predicate variables that occur within this
	 *         formula
	 * @since 1.2
	 */
	public PredicateVariable[] getPredicateVariables() {
		return predVars.clone();
	}

	/**
	 * Tells whether this formula contains predicate variables or not.
	 * 
	 * @return <code>true</code> iff this formula contains some predicate
	 *         variable
	 * @since 1.2
	 */
	public boolean hasPredicateVariable(){
	  return predVars.length > 0;	
	}

	/**
	 * Internal method.
	 * <p>
	 * Collect the first (leftmost) free occurrence of all identifiers in this
	 * formula. These occurrences are stored by side-effect in set
	 * <code>freeIdents</code>.
	 * </p>
	 * 
	 * @param freeIdentSet
	 *            freeIdentifiers collected so far
	 */
	protected abstract void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet);

	/**
	 * Internal method.
	 * <p>
	 * Collect all names that occur in this formula and do not correspond to a
	 * locally bound identifier. These names are stored by side-effect in set
	 * <code>names</code>.
	 * </p>
	 * <p>
	 * For instance when called with <code>boundIdents</code> being
	 * <code>"x", "y"</code> and <code>offset</code> being <code>10</code>
	 * on the formula
	 * 
	 * <pre>
	 *          { z . z : INTEGER | z + t + [[5]] } \/ [[11]] 
	 * </pre>
	 * 
	 * this method does the following:
	 * <ul>
	 * <li><code>"z"</code> is not added to <code>names</code>, as it is
	 * locally bound by the comprehensive set.</li>
	 * <li><code>"t"</code> is added to <code>names</code>, as it occurs
	 * free.</li>
	 * <li>The bound identifier with index <code>5</code> is ignored (its
	 * index is less than the offset, so it means it is bound below the root of
	 * the global formula and thus not relevant to us).</li>
	 * <li>The bound identifier with index <code>11</code> corresponds to
	 * name <code>"x"</code> (once the offset subtracted from the index) and
	 * is thus added to <code>names</code>.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param names
	 *            identifier names collected so far (extended by side-effect)
	 * @param boundNames
	 *            names that we consider bound globally
	 * @param offset
	 *            offset introduced by local quantifiers so far
	 */
	protected abstract void collectNamesAbove(Set<String> names,
			String[] boundNames, int offset);

	/**
	 * Returns the left-aligned string representation of the syntax tree.
	 * <p>
	 * This method is for debugging purpose. It returns a string that represents
	 * the AST of this formula.
	 * </p>
	 * 
	 * @return Returns the printable syntax tree.
	 */
	public final String getSyntaxTree() {
		// TODO use a mediator instead
		return getSyntaxTree(NO_STRING, "");
	}

	/**
	 * Internal method that is used to contruct the printable syntax tree.
	 * <p>
	 * Method {@link Formula#getSyntaxTree()} in fact calls this method with an
	 * empty Identifier array and an empty string as parameters.
	 * 
	 * @param boundNames
	 *            the array of the quantified identifier in the scope of the
	 *            node
	 * @param tabs
	 *            the number of tabs that are displayed before the string for
	 *            the current node
	 * @return Returns the string of the syntax tree.
	 */
	protected abstract String getSyntaxTree(String[] boundNames,
			String tabs);

	// old Default implementation
	// {
	// return tabs+this.getClass().getSimpleName()+"\n";
	// }
	//
	// new default implementation will be
	// {
	// tabs + getHeader() + "\n" + getBody(tabs + "\t") + "\n";
	// }

	// // Header for displaying a node kind in the syntax tree.
	// protected String getHeader() {
	// return this.getClass().getSimpleName();
	// }
	//	
	// // Body for displaying a node in the syntax tree.
	// protected abstract String getBody(String tabs);

	/**
	 * Checks whether a formula is legible.
	 * <p>
	 * For a formula to be legible, a bound identifier must not appear free in
	 * the formula, and a bound identifier can be bound at exactly one place.
	 * </p>
	 * 
	 * @param context
	 *            collection of identifiers occurring free in the context of
	 *            this formula or <code>null</code>.
	 * 
	 * @return the result of the legibility checker
	 */
	public final IResult isLegible(Collection<FreeIdentifier> context) {
		LegibilityResult result = new LegibilityResult(context);
		isLegible(result);
		return result;
	}
	
	/**
	 * Checks whether this formula is well-formed.
	 * <p>
	 * A formula is called well-formed iff all bound identifiers occurring in it
	 * are declared within that same formula. In other words, the formula
	 * contains no dangling de Bruijn indices.
	 * </p>
	 * 
	 * @return <code>true</code> iff the formula is well-formed
	 */
	public final boolean isWellFormed() {
		return boundIdents.length == 0;
	}

	/**
	 * Statically type-checks this well-formed formula. The type-check procedure
	 * traverses this formula and tries to infer the types of all
	 * sub-expressions. The type of some free identifiers can be specified by
	 * passing a non-empty type environment.
	 * <p>
	 * Type-checking fails if some type could not be inferred or if some
	 * inconsistency is detected during the traversal (e.g., an illegal
	 * combination of child types or inconsistent typing of identifiers). In
	 * this case, all sub-formulas for which a type could be inferred are then
	 * type-checked, but not this formula.
	 * </p>
	 * <p>
	 * If this formula is already type-checked, the type-checking can
	 * nevertheless fail if the type of some free identifier of this formula is
	 * not compatible with the contents of the given type environment. However,
	 * the type-checked status of this formula will not be modified.
	 * </p>
	 * <p>
	 * Returns the {@link ITypeCheckResult} containing all information about the
	 * type-check run.
	 * </p>
	 * 
	 * @param environment
	 *            an initial type environment
	 * @return the result of the type checker
	 * @throws IllegalStateException
	 *             is this formulas is not well-formed
	 * @see #isWellFormed()
	 * @see #isTypeChecked()
	 * @see Expression#getType()
	 * @see BoundIdentDecl#getType()
	 */
	public final ITypeCheckResult typeCheck(ITypeEnvironment environment) {
		if (!isWellFormed()) {
			throw new IllegalStateException(
					"Cannot typecheck ill-formed formula: " + this);
		}
		ensureSameFactory(environment);
		TypeCheckResult result = new TypeCheckResult(environment.makeSnapshot());
		typeCheck(result, NO_BOUND_IDENT_DECL);
		result.solveTypeVariables();
		solveType(result.getUnifier());
		// Ensure that we are consistent:
		// Success reported implies that this node is type-checked
		assert !result.isSuccess() || typeChecked;
		return result;
	}

	@Override
	public final int hashCode() {
		return this.hashCode;
	}

	/**
	 * Returns a copy of this formula where all identifiers that occur free in
	 * it become bound at the root. The list of these identifiers is stored in
	 * <code>boundIdent</code> as a side effect. It then can be used to create
	 * a quantified Formula.
	 * <p>
	 * For instance, when given as input the predicate:
	 * <pre>
	 *      ∀x·x = y ∧ y = z ∧ y = { z | z ∈ y }
	 * </pre>
	 * the list of identifiers to bind is
	 * <pre>
	 *      y, z
	 * </pre>
	 * and the result formula is (De Bruijn numbers are shown in square bracket
	 * after each bound identifier):
	 * <pre>
	 *      !x.x[0] = y[2] ∧ y[2] = z[1] ∧ y[2] = { z[0] | z[0] ∈ y[3] } 
	 * </pre>
	 * </p><p>
	 * The order in the list <code>boundIdent</code> is determined by the
	 * first occurence of each identifier, when reading the formula from left to
	 * right.
	 * </p>
	 * <p>
	 * This operation is not supported by assignments.
	 * </p>
	 * 
	 * @param factory
	 *            the formula factory to use for creating the result.
	 * @param boundIdentDecls
	 *            initially an empty list. Upon return, the list is filled with
	 *            the declaration of the bound identifiers that correspond to
	 *            all free identifiers that have been bound in the resut.
	 * 
	 * @return a copy of this formula where all free identifiers became bound.
	 */
	public final T bindAllFreeIdents(List<BoundIdentDecl> boundIdentDecls,
			FormulaFactory factory) {
		
		assert boundIdentDecls.size() == 0;
		LinkedHashSet<FreeIdentifier> list = new LinkedHashSet<FreeIdentifier>();
		collectFreeIdentifiers(list);
		for (FreeIdentifier ident: list) {
			boundIdentDecls.add(ident.asDecl(factory));
		}
		return bindTheseIdents(list, factory);
	}

	/**
	 * Returns a copy of this formula where all occurrences of identifiers in
	 * <code>identsToBind</code> have been bound.
	 * <p>
	 * The collection of identifiers passed as parameter is <em>ordered</em>.
	 * The order is defined by the iterator associated to the collection. The
	 * exact same order must be used to create the quantified formula that will
	 * contain the result.
	 * </p>
	 * <p>
	 * If the result formula would be the same as this formula (none of the
	 * given identifiers occurred free in this formula), a reference to this
	 * formula is returned (rather than a copy of this formula).
	 * </p>
	 * <p>
	 * This operation is not supported by assignments.
	 * </p>
	 * 
	 * @param identsToBind
	 *            ordered collection of free identifier to bind
	 * @param factory
	 *            factory to use to create the result
	 * @return a copy of this formula with the specified identifiers bound
	 */
	public final T bindTheseIdents(Collection<FreeIdentifier> identsToBind,
			FormulaFactory factory) {

		// Fast return when there is nothing to change.
		if (identsToBind.size() == 0) {
			return getTypedThis();
		}
		
		Substitution subst = new BindingSubstitution(identsToBind, factory);
		return rewrite(subst);
	}

	// Needed by the restricted genericity of Java 5
	protected abstract T getTypedThis();

	/**
	 * Applies the given assignment to this formula.
	 * <p>
	 * This method builds a new formula where each occurrence of a free
	 * identifier which is assigned to, is replaced by the corresponding
	 * expression in the assignment. All substitutions are done in parallel.
	 * </p>
	 * <p>
	 * For instance, applying the assignment <code>x := 0</code> to the
	 * formula <code>x + y</code> gives the result formula <code>0 + y</code>.
	 * Similarly, applying <code>x := x + 1</code> to <code>x + y</code>
	 * gives <code>(x + 1) + y</code>. Finally, applying
	 * <code>x,y := y,x</code> to <code>x + y</code> gives
	 * <code>y + x</code>.
	 * </p>
	 * <p>
	 * This operation is not supported by assignments.
	 * </p>
	 * 
	 * @param assignment
	 *            the assignment to apply
	 * @param ff
	 *            factory to use for building the result
	 * @return this formula with the given assignment applied to it
	 * @see #applyAssignments(Iterable, FormulaFactory)
	 */
	public final T applyAssignment(BecomesEqualTo assignment, FormulaFactory ff) {
		Map<FreeIdentifier, Expression> map =
			new HashMap<FreeIdentifier, Expression>();
		addAssignmentToMap(map, assignment);
		return substituteFreeIdents(map, ff);
	}
	
	/**
	 * Applies in parallel the given assignments to this formula.
	 * <p>
	 * This method builds a new formula where each occurrence of a free
	 * identifier which is assigned to, is replaced by the corresponding
	 * expression in the assignment. All substitutions are done in parallel.
	 * </p>
	 * <p>
	 * The assignments given as input must not assign twice the same identifier:
	 * their left-hand sides must be pairwise disjoint.
	 * </p>
	 * <p>
	 * This operation is not supported by assignments.
	 * </p>
	 * 
	 * @param assignments
	 *            the assignments to apply
	 * @param ff
	 *            factory to use for building the result
	 * @return this formula with the given assignments applied to it
	 * @see #applyAssignment(BecomesEqualTo, FormulaFactory)
	 */
	public final T applyAssignments(Iterable<BecomesEqualTo> assignments,
			FormulaFactory ff) {
		
		Map<FreeIdentifier, Expression> map =
			new HashMap<FreeIdentifier, Expression>();
		for (BecomesEqualTo assignment: assignments) {
			addAssignmentToMap(map, assignment);
		}
		return substituteFreeIdents(map, ff);
	}
	
	private static void addAssignmentToMap(Map<FreeIdentifier, Expression> map,
			BecomesEqualTo assignment) {
		
		final FreeIdentifier[] idents = assignment.getAssignedIdentifiers();
		final Expression[] exprs = assignment.getExpressions();
		final int length = idents.length;
		for (int i = 0; i < length; i++) {
			final FreeIdentifier ident = idents[i];
			assert ! map.containsKey(ident);
			map.put(ident, exprs[i]);
		}
	}
	
	/**
	 * Rewrites this formula using the given rewriter. The rewriting operation
	 * is performed in a depth-first, post-order traversal. This means that the
	 * formula tree is traversed depth-first and the rewriter is called for each
	 * node of the formula. The calls to the rewriter are done in post-order:
	 * children are rewritten before their parent.
	 * <p>
	 * Additionaly, each time a quantified formula is traversed, method
	 * {@link IFormulaRewriter#enteringQuantifier(int)} (resp.
	 * {@link IFormulaRewriter#leavingQuantifier(int)}) is called just before
	 * (resp. after) processing the children of the quantified formula.
	 * </p>
	 * <p>
	 * If no rewrite was performed on this formula (that is all rewrite calls
	 * on sub-formulas returned an identical sub-formula), then a reference to
	 * this formula is returned (rather than a copy of this formula). This
	 * allows to test efficiently (using <code>==</code>) whether rewriting
	 * made any change.
	 * </p>
	 * </p>
	 * This operation is not supported for assignments and bound identifier
	 * declarations. The returned formula is type-checked if this formula is
	 * type-checked.
	 * </p>
	 * 
	 * @param rewriter
	 *            the rewriter to apply
	 * @return this formula with the given rewriter applied to it
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment or a bound identifier
	 *             declaration.
	 * @throws IllegalArgumentException
	 *             if, at any point, the sub-formula returned by the rewriter is
	 *             incompatible with the original sub-formula:
	 *             <ul>
	 *             <li>the original sub-formula was type-checked and the new
	 *             sub-formula is not type-checked.</li>
	 *             <li>the new subformula bears a different type from the
	 *             original sub-formula.</li>
	 *             </ul>
	 * @see IFormulaRewriter
	 */
	public T rewrite(IFormulaRewriter rewriter) {
		return rewrite(new SameTypeRewriter(this.fac, rewriter));
	}
	
	/**
	 * This method is not part part of the published API of the AST library and
	 * shall not be called by clients. However, interface
	 * <code>ITypeCheckingRewriter</code> is not part of the API on purpose.
	 * 
	 * @param rewriter
	 *            some type-checking rewriter
	 * @return the rewritten formula
	 */
	protected abstract T rewrite(ITypeCheckingRewriter rewriter);
	
	/**
	 * Substitutes all occurrences of some free identifiers by their
	 * corresponding expressions as specified by the given map.
	 * <p>
	 * For each entry of the given map, the free identifier and the replacement
	 * expression must both be typed and bear the same type.
	 * </p>
	 * <p>
	 * If no change where performed on this formula (none of the identifier
	 * occurred free in this formula), then a reference to this formula is
	 * returned (rather than a copy of this formula).
	 * </p>
	 * <p>
	 * This operation is not supported by assignments.
	 * </p>
	 * 
	 * @param map
	 *            The substitution to be carried out
	 * @param ff
	 *            formula factory to use for building the result
	 * @return this formula after application of the substitution
	 */
	public T substituteFreeIdents(Map<FreeIdentifier, Expression> map, FormulaFactory ff) {
		SimpleSubstitution subst = new SimpleSubstitution(map, ff);
		return rewrite(subst);
	}
	
	/**
	 * Tells whether two formulae are equal modulo alpha-conversion. In other
	 * terms, whether the two formulae have the same tree, where the names of
	 * bound identifier declarations are ignored. Tags are already known to be
	 * the same. This method must only be called by
	 * {@link Formula#equals(Object)}, and never directly by recursive calls.
	 * 
	 * @param other
	 *            the formula to be compared to
	 * @return <code>true</code> if both objects are equal
	 * @since 3.0
	 */
	protected abstract boolean equalsInternal(Formula<?> other);

	/**
	 * Internal methods that statically type-checks the formula.
	 * 
	 * @param result
	 *            the result which is modified throughout the formula.
	 * @param quantifiedIdentifiers
	 *            a list of bound identifier declarations above this formula.
	 */
	protected abstract void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers);

	/**
	 * Internal method that checks whether a formula is well-formed.
	 * <p>
	 * For a formula to be well-formed, a bound identifier must not appear free
	 * in the formula, and a bound identifier can be bound at exactly one place.
	 * <p>
	 * Method {@link Formula#isLegible(Collection)} in fact calls this method with a
	 * new result and an empty array.
	 * 
	 * @param result
	 *            the LegibilityResult that is used to return information about
	 *            the errors found. Can not be <code>null</code>
	 * @since 2.0
	 */
	protected abstract void isLegible(LegibilityResult result);

	/**
	 * Computes the Well-Definedness predicate for this formula.
	 * <p>
	 * The following optimizations are implemented:
	 * <ul>
	 * <li>"⊤" is eliminated from formulas where possible</li>
	 * <li>duplicate occurrences of the same predicate are eliminated</li>
	 * <li>implications are replaced by conjunctions</li>
	 * </ul>
	 * according to the equivalences
	 * 
	 * <pre>
	 * 		(⊤ ∧ A) ⇔ A
	 * 		(A ∧ ⊤) ⇔ A
	 * 		(A ∧ A) ⇔ A
	 * 		(A ∨ ⊤) ⇔ ⊤
	 * 		(⊤ ∨ A) ⇔ ⊤
	 * 		(A ⇒ (B ⇒ C)) ⇔ (A ∧ B ⇒ C)
	 * 		(A ⇒ ⊤) ⇔ ⊤
	 * 		(⊤ ⇒ A) ⇔ A
	 * 		(A ⇒ A) ⇔ ⊤
	 * 		(∀x·⊤) ⇔ ⊤
	 * 		(∃x·⊤) ⇔ ⊤
	 * 		(∀x·A) ⇔ A provided x nfin A
	 * 		(∃x·A) ⇔ A provided x nfin A
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * This formula must be type-checked before <code>getWDPredicate()</code>
	 * can be invoked.
	 * </p>
	 * 
	 * @param formulaFactory
	 *            factory to use for creating the WD predicate
	 * @return the well-definedness predicate for this formula.
	 */
	public final Predicate getWDPredicate(FormulaFactory formulaFactory) {
		ensureTypeChecked();
		final WDComputer wdComputer = new WDComputer(formulaFactory);
		final Predicate wdLemma = wdComputer.getWDLemma(this);
		final WDImprover wdImprover = new WDImprover(formulaFactory);
		return wdImprover.improve(wdLemma);
	}

	/**
	 * Returns whether this formula node is WD strict. This node is WD strict if
	 * its well-definedness implies the well-definedness of all of its children,
	 * for all WD syntactic transformations (D or L). For instance, addition is
	 * WD strict, while conjunction is not.
	 * 
	 * @return <code>true</code> iff this node is WD strict
	 * @since 2.0
	 */
	public abstract boolean isWDStrict();

	/**
	 * Internal method used by the type-checker to set the type of the formula
	 * after a type-check has been executed.
	 * 
	 * @param unifier
	 *            a type unifier
	 * @since 3.0
	 */
	protected abstract void solveType(TypeUnifier unifier);

	/**
	 * Traverses this formula with the given visitor. In this complex version,
	 * the accept method also manages stepping through AST children nodes.
	 * 
	 * @param visitor
	 *            the visitor to call back during traversal
	 * @return <code>true</code> to continue traversal, <code>false</code>
	 *         to take some shortcut.
	 * 
	 * @see IVisitor
	 */
	public abstract boolean accept(IVisitor visitor);
	
	/**
	 * Accept the visit of this formula with the given simple visitor. In this
	 * simple version, this method only calls the visit method of the visitor
	 * corresponding to the dynamic class of the formula. Ast traversal is not
	 * managed by this method and shall be implemented in the visit methods of
	 * the client.
	 * 
	 * @param visitor
	 *            the visitor to call back during traversal
	 * 
	 * @see ISimpleVisitor
	 */
	public abstract void accept(ISimpleVisitor visitor);

	/**
	 * Returns whether this formula has been type-checked. A formula can be
	 * type-checked by construction (e.g., an integer literal) or by running the
	 * type checker. If this formula is marked as type-checked, then it is also
	 * the case of all its descendants. Once a formula has been type-checked,
	 * this method will always return <code>true</code>.
	 * 
	 * @return <code>true</code> iff this formula has been type-checked
	 * @see #typeCheck(ITypeEnvironment)
	 * @see Expression#typeCheck(ITypeEnvironment, Type)
	 */
	public final boolean isTypeChecked() {
		return typeChecked;
	}
	
	/**
	 * Returns a copy of this formula where all externally bound identifier
	 * indexes have been shifted by the given offset.
	 * <p>
	 * If no change where performed on this formula, then a reference to this
	 * formula is returned (rather than a copy of this formula).
	 * </p>
	 * 
	 * @param offset
	 *            offset to apply to bound identifier indexes. Use the number of
	 *            bound identifier declarations that you plan to add just atop
	 *            this formula. If you plan to remove some bound identifier
	 *            declarations, then use a negative offset
	 * @param factory
	 *            factory to use for building the result
	 * @return a copy of this formula with all bound identifiers shifted by the
	 *         given offset
	 */
	public T shiftBoundIdentifiers(int offset, FormulaFactory factory) {
		if (offset == 0) {
			return getTypedThis();
		}
		final Substitution subst = new BoundIdentifierShifter(offset, factory);
		return rewrite(subst);
	}
	
	/**
	 * Returns a set of all given types which are used for typing this formula.
	 * This method uses the free identifier cache of the formula to extract the
	 * given types used.
	 * 
	 * @return a set containing all given types which are used in this formula
	 *         types
	 */
	public final Set<GivenType> getGivenTypes() {
		final Set<GivenType> result = new HashSet<GivenType>();
		for (FreeIdentifier ident : getFreeIdentifiers()) {
			if (ident.isATypeExpression()) {
				result.add((GivenType) ident.getType().getBaseType());
			}
		}
		return result;
	}

	/**
	 * Returns the sub-formula at the given position in this formula, or
	 * <code>null</code> if the given position does not correspond to any
	 * sub-formula of this formula.
	 * 
	 * @param position
	 *            the position of the sub-formula to retrieve
	 * @return the sub-formula at the given position in this formula, or
	 *         <code>null</code> if there is none
	 */
	public final Formula<?> getSubFormula(IPosition position) {
		Formula<?> formula = this;
		for (int index : ((Position) position).indexes) {
			if (index >= formula.getChildCount())
				return null;
			formula = formula.getChild(index);
		}
		return formula;
	}

	/**
	 * Returns whether the given position is WD strict in this formula. A
	 * position is WD strict if the well-definedness of this formula implies the
	 * well-definedness of the subformula occurring at the given position. If
	 * the given position does not correspond to any sub-formula of this
	 * formula, then this method returns <code>false</code>.
	 * 
	 * @param position
	 *            position to consider in this formula
	 * @return <code>true</code> iff the given position is WD strict in this
	 *         formula
	 * @since 2.0
	 */
	public boolean isWDStrict(IPosition position) {
		Formula<?> formula = this;
		for (int index : ((Position) position).indexes) {
			if (!formula.isWDStrict())
				return false;
			if (index >= formula.getChildCount())
				return false;
			formula = formula.getChild(index);
		}
		return true;
	}

	/**
	 * Returns the child at the given index. Child indexes are counted from
	 * <code>0</code> for the first child to <code>getChildCount() - 1</code>.
	 * for the last one.
	 * <p>
	 * This method is not applicable to assignments.
	 * </p>
	 * 
	 * @param index
	 *            index of the child to retrieve
	 * 
	 * @return the child at the given index
	 * @throws IllegalArgumentException
	 *             is the index is not in the appropriate range
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment
	 * @since 2.1
	 */
	public abstract Formula<?> getChild(int index);

	/**
	 * Returns the number of children of this formula.
	 * <p>
	 * This method is not applicable to assignments.
	 * </p>
	 * 
	 * @return the number of children
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment
	 * @since 2.1
	 */
	public abstract int getChildCount();

	/**
	 * @since 2.1
	 */
	protected final void checkChildIndex(int index) {
		if (index < 0 || index >= getChildCount()) {
			throw invalidIndex(index);
		}
	}

	/**
	 * @since 2.1
	 */
	protected static IllegalArgumentException invalidIndex(int index) {
		return new IllegalArgumentException("Invalid child index " + index);
	}

	/**
	 * Returns the positions of all sub-formulas of this formula that satisfy
	 * the given criterion.
	 * <p>
	 * The positions are computed by calling the filter on each node of the
	 * formula tree, traversed in pre-order. Consequently, the returned list is
	 * always sorted lexicographically.
	 * </p>
	 * <p>
	 * This method is not applicable to assignments.
	 * </p>
	 * 
	 * @param filter
	 *            filter implementing the criterion to test for
	 * 
	 * @return a list of the positions of all sub-formulas that satisfy the
	 *         given criterion
	 */
	public final List<IPosition> getPositions(IFormulaFilter filter) {
		return inspect(new FilteringInspector(filter));
	}

	/**
	 * Returns the findings for all sub-formulas of this formula that are
	 * reported by the given inspector.
	 * <p>
	 * The findings are obtained by calling the inspector on each node of the
	 * formula tree, traversed in pre-order. The inspector can also skip some
	 * sub-formulas.
	 * </p>
	 * <p>
	 * This method is not applicable to assignments.
	 * </p>
	 * 
	 * @param <F>
	 *            type of the findings to report
	 * @param inspector
	 *            the inspector used for analyzing sub-formulas
	 * 
	 * @return a list of all the findings reported by the inspector during
	 *         traversal
	 * @since 2.0
	 */
	public final <F> List<F> inspect(IFormulaInspector<F> inspector) {
		if (this instanceof Assignment) {
			throw new IllegalArgumentException(
					"Inspection not available for assignments.");
		}
		final FindingAccumulator<F> acc = new FindingAccumulator<F>(inspector);
		inspect(acc);
		return acc.getFindings();
	}
	
	/**
	 * @since 2.0
	 */
	protected abstract <F> void inspect(FindingAccumulator<F> acc);
	
	/**
	 * Returns the position of the deepest sub-formula of this formula that
	 * contains the given source location.
	 * <p>
	 * In case there are several sub-formulas that contain the given source
	 * location, and if they are not in a ancester-descendant relationship (that
	 * case being tackled with by picking up the descendant), the position
	 * returned is that of the sub-formulas which is encountered first in a
	 * top-down traversal of this formula.
	 * <p>
	 * This method is not applicable to assignments.
	 * </p>
	 * 
	 * @param sloc
	 *            source location to search
	 * 
	 * @return the position of the deepest sub-formula that contains the given
	 *         source location, or <code>null</code> if there is none
	 */
	public final IPosition getPosition(SourceLocation sloc) {
		return getPosition(sloc, new IntStack());
	}
	
	protected final IPosition getPosition(SourceLocation sloc, IntStack indexes) {
		if (contains(sloc)) {
			return getDescendantPos(sloc, indexes);
		}
		return null;
	}

	protected abstract IPosition getDescendantPos(SourceLocation sloc, IntStack indexes);

	/**
	 * Tells whether this formula spans the given source location. In other
	 * words, returns <code>true</code> if the source location associated to
	 * this formula is not <code>null</code> and contains the given source
	 * location, <code>false</code> otherwise.
	 * 
	 * @param sloc
	 *            another source location
	 * @return <code>true</code> iff this formula spans the given location
	 * @see SourceLocation#contains(SourceLocation)
	 */
	public final boolean contains(SourceLocation sloc) {
		return this.location != null && this.location.contains(sloc);
	}

	/**
	 * Returns a new formula obtained from this formula by replacing the
	 * sub-formula at the given position by the given new formula.
	 * <p>
	 * The given position must designate a sub-formula. The replaced and new
	 * sub-formula must be of the same kind (bound identifier declaration,
	 * expression or predicate), and be both type-checked. Moreover, they must
	 * bear the same type (except for predicates which do not bear a type).
	 * <p>
	 * </p>
	 * This operation is not supported for assignments, nor untyped formulas.
	 * The returned formula is type-checked. </p>
	 * 
	 * @param position
	 *            the position of the sub-formula to rewrite
	 * @param newFormula
	 *            the new sub-formula to replace with
	 * @param factory
	 *            factory to use for building the result
	 * @return a copy of this formula where the sub-formula at the given
	 *         position has been replaced by the given new sub-formula
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment.
	 * @throws IllegalStateException
	 *             if this formula is not type-checked.
	 * @throws IllegalArgumentException
	 *             in the following cases:
	 *             <ul>
	 *             <li>the position does not lie inside this formula,</li>
	 *             <li>the replaced and new sub-formulas are incompatible
	 *             (different kind or different type).</li>
	 *             </ul>
	 */
	public final T rewriteSubFormula(IPosition position, Formula<?> newFormula,
			FormulaFactory factory) {

		ensureTypeChecked();
		if (position == null)
			throw new NullPointerException("Null position");
		if (! newFormula.isTypeChecked())
			throw new IllegalArgumentException("New sub-formula is not type-checked.");
		if (factory == null)
			throw new NullPointerException("Null factory");
		final SingleRewriter rewriter =
			new SingleRewriter(position, newFormula, factory);
		T result = rewriter.rewrite(this);
		assert result.isTypeChecked();
		return result;
	}
	
	/*
	 * Rewrite the child at the given index.
	 */
	protected abstract T rewriteChild(int index, SingleRewriter rewriter);

	protected abstract T getCheckedReplacement(SingleRewriter rewriter);

	protected final void ensureTypeChecked() {
		if (!this.isTypeChecked())
			throw new IllegalStateException("Formula should be type-checked");
	}
	
	/**
	 * Returns the type-checked formula obtained by applying the given
	 * specialization to this formula.
	 * <p>
	 * If the specialization does not change the formula, then a reference to
	 * this formula is returned (rather than a copy of it). This allows to test
	 * efficiently (using <code>==</code>) whether specialization made any
	 * change.
	 * </p>
	 * </p>This operation is not supported for assignments. This formula must be
	 * type-checked.</p>
	 * 
	 * @param specialization
	 *            the specialization to apply
	 * @return the formula obtained by applying the given specialization to this
	 *         formula
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment
	 * @throws IllegalStateException
	 *             if this formula is not type-checked.
	 * 
	 * @since 2.6
	 */
	public T specialize(ISpecialization specialization) {
		ensureTypeChecked();
		final Specialization spec = (Specialization) specialization;
		return rewrite(spec);
	}

	/**
	 * Returns the type-checked formula obtained after translating out all the
	 * datatypes occurring within this formula using the given datatype
	 * translation.
	 * 
	 * @param translation
	 *            some datatype translation instance
	 * @return a type-checked formula where datatypes have been translated out
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment
	 * @throws IllegalStateException
	 *             if this formula is not type-checked
	 * @see ITypeEnvironment#makeDatatypeTranslation()
	 * @since 2.7
	 */
	public T translateDatatype(IDatatypeTranslation translation) {
		ensureTypeChecked();
		final DatatypeTranslation real = (DatatypeTranslation) translation;
		return rewrite(real.getFormulaRewriter());
	}
	
	/**
	 * Returns the formula built by using the given formula factory.
	 * <p>
	 * If the translation does not change the formula, which means that given
	 * factory and formula factory are the same, then a reference to this
	 * formula is returned (rather than a copy of it).
	 * </p>
	 * </p>This operation is not supported for assignments.</p>
	 * 
	 * @param ff
	 *            the formula factory to use for rebuilding the formula.
	 * @return the formula build with the given formula factory
	 * 
	 * @throws UnsupportedOperationException
	 *             if this formula is an assignment
	 * @since 3.0
	 */
	public T translate(FormulaFactory ff){
		return rewrite(new DefaultTypeCheckingRewriter(ff));
	}

}
