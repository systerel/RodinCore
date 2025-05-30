/*******************************************************************************
 * Copyright (c) 2013, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static java.util.Arrays.setAll;
import static java.util.Arrays.stream;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DPROD;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.TINJ;
import static org.eventb.core.ast.Formula.TSUR;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.internal.core.ast.datatype.TranslatingSetSubstitution.makeTranslatingSetSubstitution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IDatatypeTranslation;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ITypeInstantiation;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Common implementation of a translator for one datatype instance.
 * <p>
 * The translation scheme is described in {@link IDatatypeTranslation}.
 * </p>
 * <p>
 * <em>IMPORTANT NOTE:</em> As this class manipulates formulas in two different
 * versions of the mathematical language, it is very important not to mix them
 * inadvertently. To ease this, this file uses the convention that all
 * identifiers prefixed with <code>src</code> are in the source language (the
 * one containing the datatype to translate) and those prefixed with
 * <code>trg</code> are in the target language.
 * </p>
 * 
 * @author Thomas Muller
 */
public class DatatypeTranslator {

	private static final String TYPE_SUFFIX = "_Type";

	private final DatatypeTranslation translation;
	private final FormulaFactory trgFactory;

	// Types and extension of the source language
	private final ParametricType srcTypeInstance;
	private final ITypeInstantiation srcInstantiation;
	private final Type[] srcTypeParameters;
	private final TypeConstructorExtension srcTypeConstructor;
	private final IDatatype datatype;
	private final IConstructorExtension[] srcConstructors;
	private final boolean hasNoSetConstructor;
	private final boolean hasSingleConstructor;

	// Types and formulas of the target language
	private final Type[] trgTypeParameters;
	private final FreeIdentifier trgSetCons;
	private final GivenType trgDatatype;
	private final Expression trgDatatypeExpr;

	private final Map<Object, FreeIdentifier> replacements //
	= new HashMap<Object, FreeIdentifier>();

	public DatatypeTranslator(ParametricType typeInstance,
			DatatypeTranslation translation) {
		this.translation = translation;
		this.trgFactory = translation.getTargetFormulaFactory();
		this.srcTypeInstance = typeInstance;
		this.srcTypeConstructor = (TypeConstructorExtension) typeInstance.getExprExtension();
		this.srcTypeParameters = typeInstance.getTypeParameters();
		this.datatype = srcTypeConstructor.getOrigin();
		this.srcInstantiation = datatype.getTypeInstantiation(srcTypeInstance);
		this.srcConstructors = datatype.getConstructors();

		// A non-empty datatype must have at least one constructor
		assert srcConstructors.length != 0;

		this.hasNoSetConstructor = srcTypeParameters.length == 0;
		this.hasSingleConstructor = srcConstructors.length == 1;

		// The first translation must be for the type parameters to ensure
		// consistent naming for tests
		this.trgTypeParameters = translateTypeParameters();

		final String srcSymbol = srcTypeConstructor.getSyntaxSymbol();
		this.trgDatatype = getTrgDatatype(srcSymbol);
		this.trgDatatypeExpr = trgDatatype.toExpression();
		this.trgSetCons = getTrgSetConstructor(srcSymbol);
		computeReplacements();
	}

	private Type[] translateTypeParameters() {
		final int length = srcTypeParameters.length;
		final Type[] trgResult = new Type[length];
		for (int i = 0; i < length; i++) {
			trgResult[i] = translateType(srcTypeParameters[i]);
		}
		return trgResult;
	}

	/*
	 * Returns the fresh type τ.
	 */
	private GivenType getTrgDatatype(String srcSymbol) {
		final String symbol;
		if (hasNoSetConstructor) {
			symbol = srcSymbol;
		} else {
			symbol = srcSymbol + TYPE_SUFFIX;
		}
		return this.translation.solveGivenType(symbol);
	}

	/*
	 * Returns the translated set constructor, that is Γ, or null if not used.
	 */
	private FreeIdentifier getTrgSetConstructor(String srcSymbol) {
		if (hasNoSetConstructor) {
			return null;
		}
		final Type trgType = makeTrgPowConsType(trgTypeParameters);
		return translation.solveIdentifier(srcSymbol, trgType);
	}

	/*
	 * Compute all fresh identifiers ɣi and δij that may appear in the translation
	 * of this datatype instance.
	 */
	private void computeReplacements() {
		for (final IConstructorExtension srcCons : srcConstructors) {
			final Type[] trgArgTypes = computeDestructorReplacements(srcCons);
			addReplacement(srcCons, srcCons.getSyntaxSymbol(),
					makeTrgConsType(trgArgTypes));
		}
	}

	/*
	 * Compute replacements for the destructors of the given constructor.
	 * Returns an array of the result types in the target environment of every
	 * destructor added.
	 */
	private Type[] computeDestructorReplacements(IConstructorExtension cons) {
		final IConstructorArgument[] args = cons.getArguments();
		final int nbArgs = args.length;
		final Type[] trgResult = new Type[nbArgs];
		for (int i = 0; i < nbArgs; i++) {
			final IConstructorArgument arg = args[i];
			final Type trgAlpha = translateType(arg.getType(srcInstantiation));
			final String symbol;
			if (arg.isDestructor()) {
				symbol = arg.asDestructor().getSyntaxSymbol();
			} else {
				symbol = "d";  // Dummy name for unnamed arguments
			}
			addReplacement(arg, symbol, mTrgRelType(trgDatatype, trgAlpha));
			trgResult[i] = trgAlpha;
		}
		return trgResult;
	}

	private void addReplacement(Object ext, String symbol, Type trgType) {
		final FreeIdentifier ident = translation.solveIdentifier(symbol,
				trgType);
		replacements.put(ext, ident);
	}

	/*
	 * Returns the type of Γ, that is "ℙ(T1) × ... × ℙ(Tn) ↔ ℙ(τ)", given T1, ...,
	 * Tn, or just "τ" if there is no type parameter.
	 */
	private Type makeTrgPowConsType(Type[] trgArgTypes) {
		if (trgArgTypes.length == 0) {
			return trgDatatype;
		}
		final Type trgProdType = mTrgProdPowType(trgArgTypes);
		return mTrgRelType(trgProdType, mTrgPowType(trgDatatype));
	}

	/*
	 * Returns the type "ℙ(T1) × ... × ℙ(Tn)", given T1, ..., Tn.
	 */
	private Type mTrgProdPowType(Type[] trgTypes) {
		Type trgProdType = mTrgPowType(trgTypes[0]);
		for (int i = 1; i < trgTypes.length; i++) {
			trgProdType = mTrgProdType(trgProdType, mTrgPowType(trgTypes[i]));
		}
		return trgProdType;
	}

	/*
	 * Returns the type of ɣi, that is "αi1 × ... × αiki ↔ τ", given αi1, ..., αiki,
	 * or just "τ" if ki is 0.
	 */
	private Type makeTrgConsType(Type[] trgArgTypes) {
		if (trgArgTypes.length == 0) {
			return trgDatatype;
		}
		final Type trgProdType = makeTrgProdType(trgArgTypes);
		return mTrgRelType(trgProdType, trgDatatype);
	}

	/*
	 * Returns the type "T1 × ... × Tn", given T1, ..., Tn.
	 */
	private Type makeTrgProdType(Type[] trgTypes) {
		Type trgProdType = trgTypes[0];
		for (int i = 1; i < trgTypes.length; i++) {
			trgProdType = mTrgProdType(trgProdType, trgTypes[i]);
		}
		return trgProdType;
	}

	/*
	 * Returns "E1 op ... op En", given the expressions E1, ..., En and the binary
	 * operator tag.
	 */
	private Expression combineTrgExpr(int tag, Expression[] trgExprs) {
		final int length = trgExprs.length;
		assert length != 0;
		Expression trgResult = trgExprs[0];
		for (int i = 1; i < length; i++) {
			trgResult = mTrgBinExpr(tag, trgResult, trgExprs[i]);
		}
		return trgResult;
	}

	private Type translateType(Type srcType) {
		// This test prevents infinite loop during instance initialization
		if (srcTypeInstance.equals(srcType)) {
			return trgDatatype;
		}
		return translation.translate(srcType);
	}

	/**
	 * Returns the translation of the datatype instance handled by this
	 * translator.
	 */
	public Type getTranslatedType() {
		return trgDatatype;
	}

	/**
	 * Rewrites the given extended expression.
	 * 
	 * @param ext
	 *            the extension to be translated
	 * @param trgChildExprs
	 *            the new children expressions
	 * @return a translation of the given extended expression
	 */
	public Expression rewrite(IExpressionExtension ext, Expression[] trgChildExprs) {
		if (ext.isATypeConstructor()) {
			var allTypes = stream(trgChildExprs).allMatch(Expression::isATypeExpression);
			if (allTypes) {
				return trgDatatypeExpr;
			} else {
				return mTrgFunImage(trgSetCons, trgChildExprs);
			}
		}
		final Expression trgExpr = replacements.get(ext);
		return mTrgFunImage(trgExpr, trgChildExprs);
	}

	protected Expression mTrgFunImage(Expression trgFun, Expression[] trgArgs) {
		if (trgArgs.length == 0) {
			return trgFun;
		}
		var trgMaplets = combineTrgExpr(MAPSTO, trgArgs);
		return mTrgBinExpr(FUNIMAGE, trgFun, trgMaplets);
	}

	private Expression mTrgRelImage(Expression trgRel, Expression[] trgSets) {
		final Expression trgExpr = combineTrgExpr(CPROD, trgSets);
		return mTrgBinExpr(RELIMAGE, trgRel, trgExpr);
	}

	/**
	 * Returns the axioms that specify the properties of the fresh identifiers
	 * introduced by this translator.
	 */
	public List<Predicate> getAxioms() {
		final List<Predicate> axioms = new ArrayList<Predicate>();
		for (final IConstructorExtension cons : srcConstructors) {
			addAxioms(axioms, cons);
		}
		addPartitionAxiom(axioms);
		addSetConstructorAxioms(axioms);
		return axioms;
	}

	/**
	 * Computes and adds the axioms (E) and (F)
	 */
	private void addSetConstructorAxioms(List<Predicate> axioms) {
		if (hasNoSetConstructor)
			return;
		if (datatype.isBasic()) {
			axioms.add(makeSetConstructorUnionAxiom());
		} else {
			axioms.add(makeSetConstructorFixpointAxiom());
		}
		axioms.add(makeSetConstructorCompletenessAxiom());
	}

	/**
	 * Makes the definition of the set constructor as a union of basic sets.
	 */
	private Predicate makeSetConstructorUnionAxiom() {
		final String[] names = srcTypeConstructor.getFormalNames();
		final BoundIdentDecl[] trgBIDs = mSetBIDs(names, trgTypeParameters);
		final BoundIdentifier[] trgBoundIdents = mSetBoundIdents(trgTypeParameters, 0);
		final var subst = makeTranslatingSetSubstitution(translation, srcInstantiation, null, trgBoundIdents);

		final Expression[] trgConsValueSets = stream(srcConstructors) //
				.map(cons -> makeTrgConsValueSet(cons, subst)) //
				.toArray(Expression[]::new);
		final Expression trgAllValues = mTrgUnion(trgConsValueSets);
		var lambda = mTrgLambda(trgBIDs, trgBoundIdents, trgAllValues);
		return mTrgEquals(trgSetCons, lambda);
	}

	/**
	 * Makes the definition of the set constructor as a fixpoint (predicate (E) in
	 * {@link IDatatypeTranslation}).
	 */
	private Predicate makeSetConstructorFixpointAxiom() {
		final String[] names = srcTypeConstructor.getFormalNames();
		final BoundIdentDecl[] trgSetBIDs = mSetBIDs(names, trgTypeParameters);
		final BoundIdentifier[] trgSetBIs = mSetBoundIdents(trgTypeParameters, 1);

		final BoundIdentDecl trgGammaBID = mSetBID(trgSetCons.getName(), trgDatatype);
		final BoundIdentifier trgGammaBI = mSetBoundIdent(0, trgDatatype);

		final var subst = makeTranslatingSetSubstitution(translation, srcInstantiation, trgGammaBI, trgSetBIs);

		final Predicate[] trgConsValuePreds = stream(srcConstructors) //
				.map(cons -> makeTrgConsValuePred(cons, subst, trgGammaBI)) //
				.toArray(Predicate[]::new);
		final Predicate trgFPPred = mTrgAnd(trgConsValuePreds);
		var trgFixPoint = mTrgQInter(trgGammaBID, trgFPPred, trgGammaBI);

		// Need to shift outside of the fix point
		setAll(trgSetBIs, i -> trgSetBIs[i].shiftBoundIdentifiers(-1));
		var trgLambda = mTrgLambda(trgSetBIDs, trgSetBIs, trgFixPoint);
		return mTrgEquals(trgSetCons, trgLambda);
	}

	/**
	 * Makes the set constructor completeness axiom, e.g., "Set(Params...) = Type"
	 */
	private Predicate makeSetConstructorCompletenessAxiom() {
		var typeParamsAsExpr = stream(trgTypeParameters).map(Type::toExpression).toArray(Expression[]::new);
		return mTrgEquals(mTrgFunImage(trgSetCons, typeParamsAsExpr), trgDatatypeExpr);
	}

	/*
	 * Returns a target expression corresponding to the set of all values than can
	 * be created with the given constructor when we limit ourselves to the sets
	 * embedded in the given set substitution.
	 *
	 * This corresponds to <code>ɣ1[ϕ11 × … × ϕ1k1]</code> or <code>{ɣ1}</code> in
	 * {@link IDatatypeTranslation}
	 */
	private Expression makeTrgConsValueSet(IConstructorExtension cons, TranslatingSetSubstitution subst) {
		final Expression trgCons = replacements.get(cons);
		if (cons.hasArguments()) {
			final Expression[] trgArgSets = makeTrgArgSets(cons, subst);
			return mTrgRelImage(trgCons, trgArgSets);
		} else {
			return mTrgSingleton(trgCons);
		}
	}

	/*
	 * Returns a target predicate stating that the set of all values than can be
	 * created with the given constructor is included in gamma, when we limit
	 * ourselves to the sets embedded in the given set substitution.
	 *
	 * This corresponds to <code>ɣ1[ϕ11 × … × ϕ1k1] ⊆ Γ</code> or <code>ɣ1 ∈
	 * Γ</code> in {@link IDatatypeTranslation}
	 */
	private Predicate makeTrgConsValuePred(IConstructorExtension cons, TranslatingSetSubstitution subst,
			Expression gamma) {

		final Expression trgCons = replacements.get(cons);
		if (cons.hasArguments()) {
			final Expression[] trgArgSets = makeTrgArgSets(cons, subst);
			return mTrgSubsetEq(mTrgRelImage(trgCons, trgArgSets), gamma);
		} else {
			return mTrgIn(trgCons, gamma);
		}
	}

	private Expression[] makeTrgArgSets(IConstructorExtension cons, TranslatingSetSubstitution subst) {
		final IConstructorArgument[] args = cons.getArguments();
		final Expression[] trgSets = new Expression[args.length];
		for (int i = 0; i < trgSets.length; i++) {
			final var arg = (ConstructorArgument) args[i];
			trgSets[i] = subst.getSet(arg);
		}
		return trgSets;
	}

	private void addAxioms(List<Predicate> axioms, IConstructorExtension cons) {
		if (!cons.hasArguments()) {
			return;
		}
		final Expression trgCons = replacements.get(cons);
		final Expression trgDomain = trgCons.getType().getSource().toExpression();
		final Expression trgRange = trgDatatypeExpr;
		final int tag = hasSingleConstructor ? TBIJ : TINJ;
		axioms.add(mTrgInRelationalSet(trgCons, tag, trgDomain, trgRange));
		final Expression[] trgDest = getTrgDestructors(cons);
		addDestructorAxioms(axioms, cons, trgDest);
		addConstructorInverseAxiom(axioms, cons, trgDest);
	}

	// Returns the replacements of the destructors of the given constructor
	private Expression[] getTrgDestructors(IConstructorExtension cons) {
		final IConstructorArgument[] args = cons.getArguments();
		final int nbArgs = args.length;
		final Expression[] trgResult = new Expression[nbArgs];
		for (int i = 0; i < nbArgs; i++) {
			trgResult[i] = replacements.get(args[i]);
		}
		return trgResult;
	}

	private void addDestructorAxioms(List<Predicate> axioms,
			IConstructorExtension constructor, Expression[] trgDests) {
		final Expression trgPart = makeTrgPartitionPart(constructor);
		for (final Expression trgDest : trgDests) {
			final Type trgType = trgDest.getType().getTarget();
			axioms.add(mTrgInRelationalSet(trgDest, TSUR, trgPart,
					trgType.toExpression()));
		}
	}

	private Expression makeTrgPartitionPart(IConstructorExtension cons) {
		final Expression trgGamma = replacements.get(cons);
		if (cons.hasArguments()) {
			return mTrgUnaryExpr(KRAN, trgGamma);
		} else {
			return mTrgSingleton(trgGamma);
		}
	}

	private void addConstructorInverseAxiom(List<Predicate> axioms,
			IExpressionExtension constructor, Expression[] trgDests) {
		final Expression trgDProd = combineTrgExpr(DPROD, trgDests);
		final Expression trgCons = replacements.get(constructor);
		final Expression trgConv = mTrgUnaryExpr(CONVERSE, trgCons);
		axioms.add(mTrgEquals(trgDProd, trgConv));
	}

	private void addPartitionAxiom(List<Predicate> axioms) {
		if (hasSingleConstructorWithArguments()) {
			// Partition predicate is useless
			return;
		}
		final List<Expression> trgParts = new ArrayList<Expression>();
		trgParts.add(trgDatatypeExpr);
		for (final IConstructorExtension cons : srcConstructors) {
			trgParts.add(makeTrgPartitionPart(cons));
		}
		axioms.add(mTrgPartition(trgParts));
	}

	private boolean hasSingleConstructorWithArguments() {
		return hasSingleConstructor && srcConstructors[0].hasArguments();
	}

	private Type mTrgPowType(Type trgType) {
		return trgFactory.makePowerSetType(trgType);
	}

	private Expression mTrgBinExpr(final int tag, final Expression e1,
			final Expression e2) {
		return trgFactory.makeBinaryExpression(tag, e1, e2, null);
	}

	private Predicate mTrgPartition(final List<Expression> parts) {
		return trgFactory.makeMultiplePredicate(KPARTITION, parts, null);
	}

	private Type mTrgProdType(Type t1, Type t2) {
		return trgFactory.makeProductType(t1, t2);
	}

	private Type mTrgRelType(Type t1, Type t2) {
		return trgFactory.makeRelationalType(t1, t2);
	}

	private Predicate mTrgEquals(Expression trgLeft, Expression trgRight) {
		return trgFactory.makeRelationalPredicate(EQUAL, trgLeft, trgRight,
				null);
	}

	private Predicate mTrgInRelationalSet(Expression trgRel, int tag,
			Expression trgDomain, Expression trgRange) {
		final Expression trgSet = mTrgBinExpr(tag, trgDomain, trgRange);
		return mTrgIn(trgRel, trgSet);
	}

	private Predicate mTrgIn(Expression trgMember, Expression trgSet) {
		return trgFactory.makeRelationalPredicate(IN, trgMember, trgSet, null);
	}

	private Predicate mTrgSubsetEq(Expression trgLeft, Expression trgRight) {
		return trgFactory.makeRelationalPredicate(SUBSETEQ, trgLeft, trgRight, null);
	}

	private Expression mTrgLambda(BoundIdentDecl[] trgBIDs, BoundIdentifier[] trgBIs, Expression trgValue) {
		final var trgArgs = combineTrgExpr(MAPSTO, trgBIs);
		final var trgExpr = mTrgBinExpr(MAPSTO, trgArgs, trgValue);
		return trgFactory.makeQuantifiedExpression(CSET, trgBIDs, mTrgTrue(), trgExpr, null, Lambda);
	}

	private Expression mTrgQInter(BoundIdentDecl trgBID, Predicate trgPred, Expression trgExpr) {
		final var trgBIDs = new BoundIdentDecl[] { trgBID };
		return trgFactory.makeQuantifiedExpression(QINTER, trgBIDs, trgPred, trgExpr, null, Implicit);
	}

	private Expression mTrgUnion(Expression[] trgSets) {
		if (trgSets.length == 1) {
			return trgSets[0];
		}
		return trgFactory.makeAssociativeExpression(BUNION, trgSets, null);
	}

	private Expression mTrgSingleton(Expression expression) {
		return trgFactory.makeSetExtension(expression, null);
	}

	private Expression mTrgUnaryExpr(int tag, Expression constructor) {
		return trgFactory.makeUnaryExpression(tag, constructor, null);
	}

	private Predicate mTrgAnd(Predicate[] trgPreds) {
		switch (trgPreds.length) {
		case 0:
			return mTrgTrue();
		case 1:
			return trgPreds[0];
		default:
			return trgFactory.makeAssociativePredicate(LAND, trgPreds, null);
		}
	}

	private Predicate mTrgTrue() {
		return trgFactory.makeLiteralPredicate(BTRUE, null);
	}

	/**
	 * Returns an array of bound identifier declarations with the given names and
	 * the power set of the given types.
	 *
	 * @param names names of the identifier declarations (must not be empty)
	 * @param types types to use (must have same length)
	 * @return an array of bound identifier declarations
	 * @see #mSetBoundIdents(Type[], int)
	 */
	private static BoundIdentDecl[] mSetBIDs(String[] names, Type[] types) {
		final int nbBIDs = names.length;
		assert nbBIDs != 0 && nbBIDs == types.length;
		final BoundIdentDecl[] bids = new BoundIdentDecl[nbBIDs];
		for (int i = 0; i < nbBIDs; i++) {
			bids[i] = mSetBID(names[i], types[i]);
		}
		return bids;
	}

	/**
	 * Returns a bound identifier declaration with the given name and the power set
	 * of the given type.
	 *
	 * @param name name of the identifier declaration
	 * @param type type to use
	 * @return a bound identifier declaration
	 * @see #mSetBoundIdents(Type[], int)
	 */
	private static BoundIdentDecl mSetBID(String name, Type type) {
		final FormulaFactory fac = type.getFactory();
		final Type pow = fac.makePowerSetType(type);
		return fac.makeBoundIdentDecl(name, null, pow);
	}

	/**
	 * Returns an array of bound identifiers carrying the power set of the given
	 * types. The identifiers are numbered from <code>N + offset - 1</code> to
	 * <code>offset</code> in decreasing order.
	 *
	 * @param offset index of last bound identifier
	 * @param types  types to use (must not be empty)
	 * @return an array of bound identifiers
	 * @see #mSetBIDs(String[], Type[])
	 */
	private static BoundIdentifier[] mSetBoundIdents(Type[] types, int offset) {
		final int nbIdents = types.length;
		assert nbIdents != 0;
		final BoundIdentifier[] idents = new BoundIdentifier[nbIdents];
		int boundIndex = nbIdents + offset - 1;
		for (int i = 0; i < nbIdents; i++) {
			idents[i] = mSetBoundIdent(boundIndex, types[i]);
			boundIndex--;
		}
		return idents;
	}

	/**
	 * Returns a bound identifier with the given index and the power set of the
	 * given type.
	 *
	 * @param boundIndex index of the bound identifier
	 * @param type       type to use
	 * @return a bound identifier
	 */
	private static BoundIdentifier mSetBoundIdent(int boundIndex, Type type) {
		final FormulaFactory fac = type.getFactory();
		final Type pow = fac.makePowerSetType(type);
		return fac.makeBoundIdentifier(boundIndex, null, pow);
	}

}
