/*******************************************************************************
 * Copyright (c) 2013, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static java.util.Arrays.stream;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DPROD;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IDatatypeTranslation;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ISetInstantiation;
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

	private static final Predicate[] NO_PREDICATES = new Predicate[0];

	private static final String TYPE_SUFFIX = "_Type";

	private final DatatypeTranslation translation;
	private final FormulaFactory srcFactory;
	private final FormulaFactory trgFactory;

	// Types and extension of the source language
	private final ParametricType srcTypeInstance;
	private final ITypeInstantiation srcInstantiation;
	private final Type[] srcTypeParameters;
	private final IExpressionExtension srcTypeConstructor;
	private final IDatatype datatype;
	private final IConstructorExtension[] srcConstructors;
	private final boolean hasDestructors;
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
		this.srcFactory = translation.getSourceFormulaFactory();
		this.trgFactory = translation.getTargetFormulaFactory();
		this.srcTypeInstance = typeInstance;
		this.srcTypeConstructor = typeInstance.getExprExtension();
		this.srcTypeParameters = typeInstance.getTypeParameters();
		this.datatype = (IDatatype) srcTypeConstructor.getOrigin();
		this.srcInstantiation = datatype.getTypeInstantiation(srcTypeInstance);
		this.srcConstructors = datatype.getConstructors();

		// A non-empty datatype must have at least one constructor
		assert srcConstructors.length != 0;

		this.hasDestructors = hasDestructors();
		this.hasNoSetConstructor = !hasDestructors
				|| srcTypeParameters.length == 0;
		this.hasSingleConstructor = srcConstructors.length == 1;

		// The first translation must be for the type parameters to ensure
		// consistent naming for tests
		this.trgTypeParameters = translateTypeParameters();

		final String srcSymbol = srcTypeConstructor.getSyntaxSymbol();
		this.trgDatatype = getTrgDatatype(srcSymbol);
		this.trgDatatypeExpr = toTrgExpr(trgDatatype);
		this.trgSetCons = getTrgSetConstructor(srcSymbol);
		computeReplacements();
	}

	private boolean hasDestructors() {
		for (final IConstructorExtension cons : datatype.getConstructors()) {
			if (cons.hasArguments())
				return true;
		}
		return false;
	}

	private Expression toTrgExpr(Type trgType) {
		return trgType.toExpression();
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
		final Type trgProdType = makeTrgPowProdType(trgArgTypes);
		return mTrgRelType(trgProdType, mTrgPowerSetType(trgDatatype));
	}

	/*
	 * Returns the type "ℙ(T1) × ... × ℙ(Tn)", given T1, ..., Tn.
	 */
	private Type makeTrgPowProdType(Type[] trgTypes) {
		Type trgProdType = mTrgPowerSetType(trgTypes[0]);
		for (int i = 1; i < trgTypes.length; i++) {
			trgProdType = mTrgProdType(trgProdType, mTrgPowerSetType(trgTypes[i]));
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
	 * @param src
	 *            the extended expression to be translated
	 * @param trgChildExprs
	 *            the new children expressions
	 * @return a translation of the given extended expression
	 */
	public Expression rewrite(ExtendedExpression src, Expression[] trgChildExprs) {
		final IExpressionExtension ext = src.getExtension();
		if (ext.isATypeConstructor()) {
			if (hasNoSetConstructor || src.isATypeExpression()) {
				return trgDatatypeExpr;
			} else {
				final Expression trgMaplets = combineTrgExpr(MAPSTO, trgChildExprs);
				return mTrgBinExpr(FUNIMAGE, trgSetCons, trgMaplets);
			}
		}
		final Expression trgExpr = replacements.get(ext);
		if (trgChildExprs.length == 0) {
			return trgExpr;
		}
		final Expression trgMaplets = combineTrgExpr(MAPSTO, trgChildExprs);
		return mTrgBinExpr(FUNIMAGE, trgExpr, trgMaplets);
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
		if (stream(srcConstructors).allMatch(IConstructorExtension::isBasic)) {
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
		final List<Expression> trgParts = new ArrayList<>();
		final Expression[] srcBoundIdents = makeSrcBoundIdentifiers();
		final ISetInstantiation setInst = datatype.getSetInstantiation(makeSrcSet(srcBoundIdents));
		for (final IConstructorExtension cons : srcConstructors) {
			Expression part = makeTrgSetConstructorPart(cons, setInst);
			if (part.getTag() == FREE_IDENT) {
				part = mTrgSingleton(part);
			}
			trgParts.add(part);
		}
		Expression values;
		if (trgParts.size() == 1) {
			values = trgParts.get(0);
		} else {
			values = trgFactory.makeAssociativeExpression(BUNION, trgParts, null);
		}
		var lambda = makeSetConstructorLambda(srcBoundIdents, values);
		return mTrgEquals(trgSetCons, lambda);
	}

	/**
	 * Makes the definition of the set constructor as a fixpoint.
	 */
	private Predicate makeSetConstructorFixpointAxiom() {
		final List<Predicate> trgParts = new ArrayList<>();
		final Expression[] srcBoundIdents = makeSrcBoundIdentifiers();
		final Type trgDatatypePowerSet = mTrgPowerSetType(trgDatatype);
		final BoundIdentifier trgFPIdent = trgFactory.makeBoundIdentifier(0, null, trgDatatypePowerSet);
		final ExtendedExpression srcSet = makeSrcSet(srcBoundIdents);
		final var trgSet = (BinaryExpression) srcSet.translateDatatype(translation).shiftBoundIdentifiers(1);
		final ISetInstantiation setInst = datatype.getSetInstantiation(srcSet);
		for (final IConstructorExtension cons : srcConstructors) {
			trgParts.add(makeConstructorFixpointPart(cons, setInst, trgSet, trgFPIdent));
		}
		final Predicate trgFPPred = mTrgAnd(trgParts);
		final var trgFPDecl = new BoundIdentDecl[] { mTrgBoundIdentDecl(trgSetCons.getName(), trgDatatypePowerSet) };
		var fixPoint = trgFactory.makeQuantifiedExpression(QINTER, trgFPDecl, trgFPPred, trgFPIdent, null, Implicit);
		var lambda = makeSetConstructorLambda(srcBoundIdents, fixPoint);
		return mTrgEquals(trgSetCons, lambda);
	}

	private Predicate makeConstructorFixpointPart(IConstructorExtension cons, ISetInstantiation setInst,
			BinaryExpression trgSet, BoundIdentifier trgFPIdent) {
		Expression part = makeTrgSetConstructorPart(cons, setInst);
		// Part will go in a quantified intersection with one quantified variable
		part = part.shiftBoundIdentifiers(1);
		// Replace the datatype set translation with the fix point's bound identifier
		part = part.rewrite(makeBinExpRewriter(trgSet, trgFPIdent));
		// Two possibilities:
		// - the constructor is a constant, the predicate is "cons : Set"
		// - the constructor has arguments, the predicate is "cons(...) <: Set"
		int relOp = part.getTag() == FREE_IDENT ? IN : SUBSETEQ;
		return trgFactory.makeRelationalPredicate(relOp, part, trgFPIdent, null);
	}

	private Expression makeSetConstructorLambda(Expression[] srcBoundIdents, Expression fixPoint) {
		var arguments = combineTrgExpr(MAPSTO, translate(srcBoundIdents));
		return trgFactory.makeQuantifiedExpression(CSET, makeTrgBoundIdentDecls(), mTrgTrue(),
				mTrgBinExpr(MAPSTO, arguments, fixPoint), null, Lambda);
	}

	// Makes a rewriter replacing binary expression "from" with expression "to"
	private IFormulaRewriter makeBinExpRewriter(BinaryExpression from, Expression to) {
		return new DefaultRewriter(false) {
			@Override
			public Expression rewrite(BinaryExpression expression) {
				if (expression.equals(from)) {
					return to;
				}
				return super.rewrite(expression);
			}
		};
	}

	/**
	 * Makes the set constructor completeness axiom, e.g., "Set(Params...) = Type"
	 */
	private Predicate makeSetConstructorCompletenessAxiom() {
		var typeParamsAsExpr = stream(trgTypeParameters).map(Type::toExpression).toArray(Expression[]::new);
		return mTrgEquals(mTrgBinExpr(FUNIMAGE, trgSetCons, combineTrgExpr(MAPSTO, typeParamsAsExpr)), trgDatatypeExpr);
	}

	private Expression[] makeSrcBoundIdentifiers() {
		final int nbIdents = srcTypeParameters.length;
		final Expression[] idents = new Expression[nbIdents];
		// De Bruijn indexes are counted backwards
		int boundIndex = nbIdents - 1;
		for (int i = 0; i < nbIdents; i++) {
			final Type srcType = srcTypeParameters[i];
			final Type srcBoundType = mSrcPowerSetType(srcType);
			idents[i] = mSrcBoundIdent(boundIndex, srcBoundType);
			boundIndex--;
		}
		return idents;
	}

	private Expression[] translate(Expression[] srcExprs) {
		final int length = srcExprs.length;
		final Expression[] trgResult = new Expression[length];
		for (int i = 0; i < length; i++) {
			trgResult[i] = srcExprs[i].translateDatatype(translation);
		}
		return trgResult;
	}

	private Expression makeTrgSetConstructorPart(IConstructorExtension cons,
			ISetInstantiation setInst) {
		final Expression trgCons = replacements.get(cons);
		if (cons.hasArguments()) {
			final Expression[] trgArgSets = mTrgArgSets(cons, setInst);
			return mTrgRelImage(trgCons, trgArgSets);
		} else {
			return trgCons;
		}
	}

	private Expression[] mTrgArgSets(IConstructorExtension cons,
			ISetInstantiation setInst) {
		final IConstructorArgument[] args = cons.getArguments();
		final Expression[] trgSets = new Expression[args.length];
		for (int i = 0; i < trgSets.length; i++) {
			final Expression srcSet = args[i].getSet(setInst);
			trgSets[i] = srcSet.translateDatatype(translation);
		}
		return trgSets;
	}

	private ExtendedExpression makeSrcSet(Expression[] srcExprs) {
		return srcFactory.makeExtendedExpression(srcTypeConstructor, srcExprs,
				NO_PREDICATES, null, null);
	}

	private BoundIdentDecl[] makeTrgBoundIdentDecls() {
		final int nbTypeParams = trgTypeParameters.length;
		final BoundIdentDecl[] trgResult = new BoundIdentDecl[nbTypeParams];
		final String[] typeParamsNames = datatype.getTypeConstructor()
				.getFormalNames();
		for (int i = 0; i < nbTypeParams; i++) {
			final Type trgType = mTrgPowerSetType(trgTypeParameters[i]);
			final String declName = typeParamsNames[i];
			trgResult[i] = mTrgBoundIdentDecl(declName, trgType);
		}
		return trgResult;
	}

	private void addAxioms(List<Predicate> axioms, IConstructorExtension cons) {
		if (!cons.hasArguments()) {
			return;
		}
		final Expression trgCons = replacements.get(cons);
		final Expression trgDomain = toTrgExpr(trgCons.getType().getSource());
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
					toTrgExpr(trgType)));
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

	private Expression mSrcBoundIdent(int i, Type srcType) {
		return srcFactory.makeBoundIdentifier(i, null, srcType);
	}

	private Type mSrcPowerSetType(Type srcType) {
		return srcFactory.makePowerSetType(srcType);
	}

	private Type mTrgPowerSetType(Type trgType) {
		return trgFactory.makePowerSetType(trgType);
	}

	private BoundIdentDecl mTrgBoundIdentDecl(String name, Type trgType) {
		return trgFactory.makeBoundIdentDecl(name, null, trgType);
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
		return trgFactory.makeRelationalPredicate(IN, trgRel, trgSet, null);
	}

	private Expression mTrgSingleton(Expression expression) {
		return trgFactory.makeSetExtension(expression, null);
	}

	private Expression mTrgUnaryExpr(int tag, Expression constructor) {
		return trgFactory.makeUnaryExpression(tag, constructor, null);
	}

	private Predicate mTrgAnd(List<Predicate> preds) {
		switch (preds.size()) {
		case 0:
			return mTrgTrue();
		case 1:
			return preds.get(0);
		default:
			return trgFactory.makeAssociativePredicate(LAND, preds, null);
		}
	}

	private Predicate mTrgTrue() {
		return trgFactory.makeLiteralPredicate(BTRUE, null);
	}

}
