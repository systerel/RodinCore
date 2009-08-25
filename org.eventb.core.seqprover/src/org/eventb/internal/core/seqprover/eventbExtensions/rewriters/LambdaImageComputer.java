/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.QuantifiedExpression;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class LambdaImageComputer {

	// the lambda expression to simplify
	private final QuantifiedExpression lambda;
	
	// the expression of the lambda pattern (%x,,y,, ...)
	private Expression lambdaPattern;
	
	// the expression of the comprehension set
	private Expression lambdaImage;
	
	// the positions of the bound identifiers in the lambda pattern
	private Map<IPosition, BoundIdentifier> identPositions;

	public LambdaImageComputer(QuantifiedExpression lambda) {
		assert lambda.getTag() == Expression.CSET;
		assert lambda.getForm() == QuantifiedExpression.Form.Lambda;
		assert lambda.getExpression().getTag() == Expression.MAPSTO;
		this.lambda = lambda;
	}

	// initialises non final fields from lambda expression
	// MUST BE CALLED BEFORE computeImage()
	public void init() {
		final BinaryExpression expression = (BinaryExpression) lambda
				.getExpression();

		this.lambdaPattern = expression.getLeft();
		this.lambdaImage = expression.getRight();
		final List<IPosition> positions = lambdaPattern
				.getPositions(new DefaultFilter() {
					public boolean select(BoundIdentifier identifier) {
						// must be all distinct in lambda compset => no
						// duplicate
						return true;
					};
				});
		this.identPositions = new HashMap<IPosition, BoundIdentifier>();

		for (IPosition position : positions) {
			identPositions.put(position, (BoundIdentifier) lambdaPattern
					.getSubFormula(position));
		}
	}

	// computes the image of the given expression under the lambda
	// it is implicitly assumed that the argument matches the lambda pattern
	// init() MUST BE CALLED BEFORE any call to this method
	public Expression computeImage(Expression arg, FormulaFactory ff) {
		checkInit();
		Expression result = lambdaImage;
		for (Entry<IPosition, BoundIdentifier> entry : identPositions
				.entrySet()) {
			final Formula<?> subFormula = arg.getSubFormula(entry.getKey());
			if (subFormula == null || !(subFormula instanceof Expression)) {
				return null;
			}
			final Expression replacement = (Expression) subFormula;
			final BoundIdentifier boundIdent = entry.getValue();
			result = result.rewrite(new DefaultRewriter(true, ff) {
				@Override
				public Expression rewrite(BoundIdentifier identifier) {
					if (identifier.getBoundIndex() == boundIdent
							.getBoundIndex()
							+ getBindingDepth()) {
						return replacement;
					}
					return super.rewrite(identifier);
				}
			});
		}
		return result;
	}

	private void checkInit() {
		assert lambdaPattern != null;
	}

}