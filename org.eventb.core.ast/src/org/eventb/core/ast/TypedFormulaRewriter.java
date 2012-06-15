/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Common implementation to encapsulate an IFormulaRewriter
 * 
 * @author Thomas Muller
 * @since 2.6
 */
public class TypedFormulaRewriter implements ITypedFormulaRewriter {

	private static final String NULL_REWRITER_EXCEPTION = "Can not perform rewriting with a null rewriter";
	private final IFormulaRewriter rewriter;

	private TypedFormulaRewriter() {
		this.rewriter = null;
	}

	public TypedFormulaRewriter(IFormulaRewriter rewriter) {
		assert (rewriter != null);
		this.rewriter = rewriter;
	}

	public static TypedFormulaRewriter getDefault() {
		return new TypedFormulaRewriter();
	}

	@Override
	public Predicate rewrite(PredicateVariable predVar) {
		if (!(rewriter instanceof IFormulaRewriter2)) {
			throw new IllegalArgumentException(
					"The given rewriter shall support predicate variables");
		}
		return checkReplacement(predVar,
				((IFormulaRewriter2) rewriter).rewrite(predVar));
	}

	@Override
	public Predicate checkReplacement(Predicate current, Predicate replacement) {
		if (current == replacement)
			return current;
		if (current.isTypeChecked() && !replacement.isTypeChecked())
			throw new IllegalStateException(
					"Rewritten formula should be type-checked");
		return replacement;
	}

	@Override
	public Expression checkReplacement(Expression current,
			Expression replacement) {
		if (current == replacement)
			return current;
		final Type type = current.getType();
		if (type != null && !type.equals(replacement.getType()))
			throw new IllegalArgumentException("Incompatible types in rewrite");
		return replacement;
	}
	
	@Override
	public BoundIdentDecl checkReplacement(BoundIdentDecl current,
			BoundIdentDecl replacement) {
		if (current == replacement)
			return current;
		if (current != null && !current.getType().equals(replacement.getType()))
			throw new IllegalArgumentException("Incompatible types in rewrite");
		return replacement;
	}

	@Override
	public Expression rewrite(AssociativeExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Expression rewrite(AtomicExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Expression rewrite(BinaryExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Expression rewrite(BoolExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Expression rewrite(BoundIdentifier identifier) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(identifier, rewriter.rewrite(identifier));
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(identifier, rewriter.rewrite(identifier));
	}

	@Override
	public Expression rewrite(IntegerLiteral literal) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(literal, rewriter.rewrite(literal));

	}

	@Override
	public Predicate rewrite(LiteralPredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Predicate rewrite(MultiplePredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Expression rewrite(QuantifiedExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Expression rewrite(SetExtension expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Predicate rewrite(SimplePredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Expression rewrite(UnaryExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public Expression rewrite(ExtendedExpression expression) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(expression, rewriter.rewrite(expression));
	}

	@Override
	public Predicate rewrite(ExtendedPredicate predicate) {
		if (rewriter == null)
			throw new UnsupportedOperationException(NULL_REWRITER_EXCEPTION);
		return checkReplacement(predicate, rewriter.rewrite(predicate));
	}

	@Override
	public boolean autoFlatteningMode() {
		return rewriter.autoFlatteningMode();
	}

	@Override
	public void enteringQuantifier(int nbOfDeclarations) {
		rewriter.enteringQuantifier(nbOfDeclarations);
	}

	@Override
	public FormulaFactory getFactory() {
		return rewriter.getFactory();
	}

	@Override
	public void leavingQuantifier(int nbOfDeclarations) {
		rewriter.leavingQuantifier(nbOfDeclarations);
	}

}
