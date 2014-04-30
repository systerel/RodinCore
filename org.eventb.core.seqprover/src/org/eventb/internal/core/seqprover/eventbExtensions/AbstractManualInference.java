/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added getters to Input class for testing purposes
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.IN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public abstract class AbstractManualInference extends PredicatePositionReasoner {
	
	@Override
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		final Input input = (Input) reasonerInput;
		final Predicate pred = input.getPred();
		final IPosition position = input.getPosition();

		IAntecedent[] antecedents = getAntecedents(seq, pred, position);
		if (antecedents == null)
			return ProverFactory.reasonerFailure(this, input,
					"Inference " + getReasonerID()
							+ " is not applicable for " + (pred == null ? seq
							.goal() : pred) + " at position " + position);

		if (pred == null) {
			// Generate the successful reasoner output
			return ProverFactory.makeProofRule(this, input, seq.goal(),
					getDisplayName(pred, position), antecedents);
		} else {
			return ProverFactory.makeProofRule(this, input, null, pred,
					getDisplayName(pred, position), antecedents);
		}
	}

	protected abstract IAntecedent[] getAntecedents(IProverSequent seq,
			Predicate pred, IPosition position);

	protected IAntecedent makeAntecedent(Predicate hyp,
			Predicate inferredPred, Predicate ... newHyp) {
		Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.addAll(Arrays.asList(newHyp));

		if (hyp == null) {
			// Function overriding in goal
			return ProverFactory.makeAntecedent(inferredPred,
					addedHyps, null);
		} else {
			// Function overriding in hypothesis
			addedHyps.add(inferredPred); // Added the new hyp

			// Hide the old hyp
			Collection<Predicate> hideHyps = new ArrayList<Predicate>();
			hideHyps.add(hyp);
			IHypAction hypAction = ProverFactory
					.makeHideHypAction(hideHyps);

			return ProverFactory.makeAntecedent(null, addedHyps,
					hypAction);
		}
	}

	public boolean isApplicable(Formula<?> formula) {
		if (formula instanceof Expression)
			return isExpressionApplicable((Expression) formula);
		if (formula instanceof Predicate)
			return isPredicateApplicable((Predicate) formula);
		return false;
	}

	protected boolean isExpressionApplicable(Expression expression) {
		return false;
	}
	
	protected boolean isPredicateApplicable(Predicate predicate) {
		return false;
	}
	
	public List<IPosition> getPositions(Predicate predicate, boolean wdStrict) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativeExpression expression) {
				return isApplicable(expression);
			}

			@Override
			public boolean select(AssociativePredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(AtomicExpression expression) {
				return isApplicable(expression);
			}

			@Override
			public boolean select(BinaryPredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(BoolExpression expression) {
				return isApplicable(expression);
			}

			@Override
			public boolean select(BoundIdentDecl decl) {
				return isApplicable(decl);
			}

			@Override
			public boolean select(BoundIdentifier identifier) {
				return isApplicable(identifier);
			}

			@Override
			public boolean select(FreeIdentifier identifier) {
				return isApplicable(identifier);
			}

			@Override
			public boolean select(IntegerLiteral literal) {
				return isApplicable(literal);
			}

			@Override
			public boolean select(LiteralPredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(QuantifiedExpression expression) {
				return isApplicable(expression);
			}

			@Override
			public boolean select(QuantifiedPredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(RelationalPredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(SetExtension expression) {
				return isApplicable(expression);
			}

			@Override
			public boolean select(SimplePredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(UnaryExpression expression) {
				return isApplicable(expression);
			}

			@Override
			public boolean select(UnaryPredicate predicate) {
				return isApplicable(predicate);
			}

			@Override
			public boolean select(BinaryExpression expression) {
				return isApplicable(expression);
			}
		});

		if (wdStrict) {
			Lib.removeWDUnstrictPositions(positions, predicate);
		}
		
		return filter(predicate, positions);
	}


	protected List<IPosition> filter(Predicate predicate,
			List<IPosition> positions) {
		return positions;
	}

	protected IAntecedent makeFunctionalAntecident(Expression f,
			boolean converse, int tag, FormulaFactory ff) {
		final Type type = f.getType();
		final Expression A = type.getSource().toExpression();
		final Expression B = type.getTarget().toExpression();
		final Predicate pred;
		if (converse) {
			pred = makeInRelset(ff.makeUnaryExpression(CONVERSE, f, null), tag,
					B, A, ff);
		} else {
			pred = makeInRelset(f, tag, A, B, ff);
		}
		return ProverFactory.makeAntecedent(pred);
	}

	private Predicate makeInRelset(Expression f, int tag, Expression A,
			Expression B, FormulaFactory ff) {
		final Expression set = ff.makeBinaryExpression(tag, A, B, null);
		return ff.makeRelationalPredicate(IN, f, set, null);
	}

	protected IAntecedent makeWD(Predicate pred) {
		return ProverFactory.makeAntecedent(DLib.WD(pred));
	}

	protected Expression makeCompIfNeccessary(Collection<Expression> children, FormulaFactory ff) {
		if (children.size() == 1)
			return children.iterator().next();
		else
			return ff.makeAssociativeExpression(FCOMP, children, null);
	}
}
