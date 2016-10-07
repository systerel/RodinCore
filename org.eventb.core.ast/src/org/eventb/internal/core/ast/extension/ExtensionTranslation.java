/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import static org.eventb.internal.core.ast.extension.ExtensionSignature.getSignature;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IExtensionTranslation;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.AbstractTranslation;
import org.eventb.internal.core.ast.DefaultTypeCheckingRewriter;
import org.eventb.internal.core.ast.FreshNameSolver;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.TypeRewriter;
import org.eventb.internal.core.ast.datatype.DatatypeTranslation;
import org.eventb.internal.core.ast.extension.ExtensionSignature.ExpressionExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.PredicateExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.ExpressionExtTranslator;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.PredicateExtTranslator;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.TypeExtTranslator;
import org.eventb.internal.core.ast.extension.TranslatorRegistry.ExprTranslatorRegistry;
import org.eventb.internal.core.ast.extension.TranslatorRegistry.PredTranslatorRegistry;
import org.eventb.internal.core.ast.extension.TranslatorRegistry.TypeTranslatorRegistry;
import org.eventb.internal.core.typecheck.TypeEnvironmentBuilder;

/**
 * Translation of operator extensions to function applications. We do not
 * translate extensions that come from a datatype (these are taken care by
 * {@link DatatypeTranslation}), nor extensions that are not WD-strict (as these
 * cannot be translated to mere function application).
 * 
 * We maintain associative tables from operator signatures to dedicated
 * translators.
 * 
 * @author Thomas Muller
 */
public class ExtensionTranslation extends AbstractTranslation implements
		IExtensionTranslation {

	private final FormulaFactory trgFactory;
	private final ITypeEnvironmentBuilder trgTypenv;
	private final FreshNameSolver nameSolver;

	private final TypeTranslatorRegistry typeTranslators //
	= new TypeTranslatorRegistry(this);
	private final ExprTranslatorRegistry exprTranslators //
	= new ExprTranslatorRegistry(this);
	private final PredTranslatorRegistry predTranslators //
	= new PredTranslatorRegistry(this);

	private TypeRewriter typeRewriter;

	private ITypeCheckingRewriter rewriter;

	public ExtensionTranslation(ISealedTypeEnvironment srcTypenv) {
		super(srcTypenv);
		this.trgFactory = computeTargetFactory(srcTypenv.getFormulaFactory());
		final Set<String> usedNames = new HashSet<String>(srcTypenv.getNames());
		this.nameSolver = new FreshNameSolver(usedNames, trgFactory);
		this.typeRewriter = new ExtensionTypeRewriter(trgFactory, this);
		this.rewriter = new ExtensionRewriter(typeRewriter, this);
		this.trgTypenv = new TypeEnvironmentBuilder(trgFactory);
		populateTargetTypenv();
	}

	private static FormulaFactory computeTargetFactory(FormulaFactory fac) {
		final Set<IFormulaExtension> keptExtensions;
		final Set<IFormulaExtension> extensions = fac.getExtensions();
		keptExtensions = new LinkedHashSet<IFormulaExtension>();
		for (final IFormulaExtension extension : extensions) {
			if (extension.getOrigin() instanceof IDatatype) {
				// Keep datatype extensions
				keptExtensions.add(extension);
			}
			if (!extension.conjoinChildrenWD()) {
				// Keep extensions that are not WD-strict
				keptExtensions.add(extension);
			}
		}
		return FormulaFactory.getInstance(keptExtensions);
	}

	private void populateTargetTypenv() {
		final IIterator iter = srcTypenv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String name = iter.getName();
			final Type srcType = iter.getType();
			final Type trgType = typeRewriter.rewrite(srcType);
			trgTypenv.addName(name, trgType);
		}
	}

	public FormulaFactory getTargetFactory() {
		return trgFactory;
	}

	@Override
	public ISealedTypeEnvironment getTargetTypeEnvironment() {
		return trgTypenv.makeSnapshot();
	}

	public Type translate(ParametricType src) {
		final ExpressionExtSignature signature = getSignature(src);
		final TypeExtTranslator translator = typeTranslators.get(signature);
		return translator.translate();
	}

	public Expression translate(ExtendedExpression src,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final ExpressionExtSignature signature = getSignature(src);
		final ExpressionExtTranslator translator = exprTranslators
				.get(signature);
		return translator.translate(newChildExprs, newChildPreds);
	}

	public Predicate translate(ExtendedPredicate src,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final PredicateExtSignature signature = getSignature(src);
		final PredicateExtTranslator translator = predTranslators
				.get(signature);
		return translator.translate(newChildExprs, newChildPreds);
	}

	public GivenType makeType(ExtensionSignature signature) {
		assert(signature.isATypeConstructor());
		final String baseName = makeBaseName(signature);
		final String name = nameSolver.solveAndAdd(baseName);
		final GivenType trgType = trgFactory.makeGivenType(name);
		trgTypenv.add(trgType.toExpression());
		return trgType;
	}

	public FreeIdentifier makeFunction(ExtensionSignature signature) {
		final String baseName = makeBaseName(signature);
		final String name = nameSolver.solveAndAdd(baseName);

		final FreeIdentifier ident;
		if (signature.isATypeConstructor()) {
			ident = trgFactory.makeGivenType(name).toExpression();
		} else {
			final FunctionalTypeBuilder builder;
			builder = new FunctionalTypeBuilder(typeRewriter);
			final Type type = signature.getFunctionalType(builder);
			ident = trgFactory.makeFreeIdentifier(name, null, type);
		}

		trgTypenv.add(ident);
		return ident;
	}

	/*
	 * Ensures that we will be able to create a fresh identifier, that is that
	 * we start with something that looks like an identifier, otherwise the
	 * fresh name solver will loop forever.
	 */
	private String makeBaseName(ExtensionSignature signature) {
		final String symbol = signature.getSymbol();
		if (trgFactory.isValidIdentifierName(symbol)) {
			return symbol;
		}
		// Use some arbitrary name which can be used for identifiers
		return "ext";
	}

	@Override
	public ITypeCheckingRewriter getFormulaRewriter() {
		return rewriter;
	}

	@Override
	public String toString() {
		return "Extension Translation for factory: "
				+ srcTypenv.getFormulaFactory() + " in type environment: "
				+ srcTypenv;
	}

	private static class ExtensionTypeRewriter extends TypeRewriter {

		private ExtensionTranslation translation;

		public ExtensionTypeRewriter(FormulaFactory targetFactory,
				ExtensionTranslation translation) {
			super(targetFactory);
			this.translation = translation;
		}

		@Override
		public void visit(ParametricType type) {
			if (type.getExprExtension().getOrigin() instanceof IDatatype) {
				super.visit(type);
				return;
			}

			result = translation.translate(type);
		}

	}

	private static class ExtensionRewriter extends DefaultTypeCheckingRewriter {

		private ExtensionTranslation translation;

		public ExtensionRewriter(TypeRewriter typeRewriter,
				ExtensionTranslation translation) {
			super(typeRewriter.getFactory(), typeRewriter);
			this.translation = translation;
		}

		@Override
		public Expression rewrite(ExtendedExpression src, boolean changed,
				Expression[] newChildExprs, Predicate[] newChildPreds) {
			if (!changed && ff == src.getFactory()) {
				return src;
			}
			if (src.getExtension().getOrigin() instanceof IDatatype) {
				return super.rewrite(src, changed, newChildExprs, newChildPreds);
			}
			return translation.translate(src, newChildExprs, newChildPreds);
		}

		@Override
		public Predicate rewrite(ExtendedPredicate src, boolean changed,
				Expression[] newChildExprs, Predicate[] newChildPreds) {
			if (!changed && ff == src.getFactory()) {
				return src;
			}
			return translation.translate(src, newChildExprs, newChildPreds);
		}

	}

}
