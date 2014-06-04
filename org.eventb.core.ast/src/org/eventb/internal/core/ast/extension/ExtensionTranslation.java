/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IExtensionTranslation;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.AbstractTranslation;
import org.eventb.internal.core.ast.DefaultTypeCheckingRewriter;
import org.eventb.internal.core.ast.FreshNameSolver;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.datatype.DatatypeTranslation;
import org.eventb.internal.core.ast.extension.ExtensionSignature.ExpressionExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.PredicateExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.ExpressionExtTranslator;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.PredicateExtTranslator;
import org.eventb.internal.core.ast.extension.TranslatorRegistry.ExprTranslatorRegistry;
import org.eventb.internal.core.ast.extension.TranslatorRegistry.PredTranslatorRegistry;

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

	private final ExprTranslatorRegistry exprTranslators //
	= new ExprTranslatorRegistry(this);
	private final PredTranslatorRegistry predTranslators //
	= new PredTranslatorRegistry(this);

	private ITypeCheckingRewriter rewriter;

	public ExtensionTranslation(ISealedTypeEnvironment srcTypenv) {
		super(srcTypenv);
		this.trgFactory = computeTargetFactory(srcTypenv.getFormulaFactory());
		this.trgTypenv = srcTypenv.translate(trgFactory).makeBuilder();
		this.nameSolver = new FreshNameSolver(trgTypenv);
		this.rewriter = new ExtensionRewriter(trgFactory, this);
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

	public FormulaFactory getTargetFactory() {
		return trgFactory;
	}

	@Override
	public ISealedTypeEnvironment getTargetTypeEnvironment() {
		return trgTypenv.makeSnapshot();
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

	public FreeIdentifier makeFunction(ExtensionSignature signature) {
		final String baseName = makeBaseName(signature);
		final String name = nameSolver.solve(baseName);
		final Type type = signature.getFunctionalType().translate(trgFactory);
		final FreeIdentifier ident = trgFactory.makeFreeIdentifier(name, null,
				type);
		trgTypenv.add(ident);
		return ident;
	}

	/*
	 * Ensures that we will be able to create a fresh identifier, that is that
	 * we start with something that looks like an identifier, otherwise the
	 * fresh name solver will loop forever.
	 */
	private String makeBaseName(ExtensionSignature signature) {
		final IFormulaExtension extension = signature.getExtension();
		final String id = extension.getId();
		if (trgFactory.isValidIdentifierName(id)) {
			return id;
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
	
	private static class ExtensionRewriter extends DefaultTypeCheckingRewriter {

		private ExtensionTranslation translation;

		public ExtensionRewriter(FormulaFactory targetFactory,
				ExtensionTranslation translation) {
			super(targetFactory);
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
