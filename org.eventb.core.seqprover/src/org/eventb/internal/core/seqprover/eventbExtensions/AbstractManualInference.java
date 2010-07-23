/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added getters to Input class for testing purposes
 ******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

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
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
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
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public abstract class AbstractManualInference implements IReasoner {

	private final static String POSITION_KEY = "pos";

	public static class Input implements IReasonerInput {

		Predicate pred;

		IPosition position;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>,
		 * the rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred, IPosition position) {
			this.pred = pred;
			this.position = position;
		}

		public void applyHints(ReplayHints renaming) {
			if (pred != null)
				pred = renaming.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public Predicate getPred() {
			return pred;
		}

		public IPosition getPosition() {
			return position;
		}

	}

	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		final Input input = (Input) reasonerInput;
		Predicate pred = input.pred;
		IPosition position = input.position;

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

	/**
	 * Returns the name to display in the generated rule.
	 * 
	 * @return the name to display in the rule
	 */
	protected abstract String getDisplayName();

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		Set<Predicate> neededHyps = reader.getNeededHyps();
		String image = reader.getString(POSITION_KEY);
		IPosition position = reader.getFormulaFactory().makePosition(image);

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new Input(null, position);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new Input(pred, position);
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Serialise the position only, the predicate is contained inside the
		// rule
		writer.putString(POSITION_KEY, ((Input) input).position.toString());
	}

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
	
	protected List<IPosition> getTopLevelOnly(Predicate predicate, List<IPosition> positions) {
		List<IPosition> toBeRemoved = new ArrayList<IPosition>();
		for (IPosition pos : positions) {
			if (!Tactics.isParentTopLevelPredicate(predicate, pos)) {
				toBeRemoved.add(pos);
			}
		}

		positions.removeAll(toBeRemoved);
		return positions;
	}


	public List<IPosition> getPositions(Predicate predicate, boolean topLevel) {
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

		if (topLevel)
			return getTopLevelOnly(predicate, positions);
		
		return filter(predicate, positions);
	}


	protected List<IPosition> filter(Predicate predicate,
			List<IPosition> positions) {
		return positions;
	}

	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null) {
			return getDisplayName() + " in " + pred.getSubFormula(position);
		}
		else {
			return getDisplayName() + " in goal";
		}
	}


	protected IAntecedent makeFunctionalAntecident(Expression f,
			boolean converse, int tag, FormulaFactory ff) {
		Type type = f.getType();
		assert type instanceof PowerSetType;
		PowerSetType powerType = (PowerSetType) type;
		Type baseType = powerType.getBaseType();
		assert baseType instanceof ProductType;
		ProductType pType = (ProductType) baseType;
		Type A = pType.getLeft();
		Type B = pType.getRight();
		Expression typeA = A.toExpression(ff);
		Expression typeB = B.toExpression(ff);
		Expression typeFun;
		if (converse)
			typeFun = ff.makeBinaryExpression(tag, typeB,
				typeA, null);
		else 
			typeFun = ff.makeBinaryExpression(tag, typeA,
					typeB, null);
		Predicate pred;
		if (converse) {
			Expression fConverse = ff.makeUnaryExpression(Expression.CONVERSE,
					f, null);
			pred = ff.makeRelationalPredicate(Predicate.IN, fConverse, typeFun,
					null);
		}
		else {
			pred = ff.makeRelationalPredicate(Predicate.IN, f,
					typeFun, null);
		}
		
		return ProverFactory.makeAntecedent(pred);
	}

	protected IAntecedent makeWD(FormulaFactory ff, Predicate pred) {
		return ProverFactory.makeAntecedent(mDLib(ff).WD(pred));
	}

	protected Expression makeCompIfNeccessary(Collection<Expression> children, FormulaFactory ff) {
		if (children.size() == 1)
			return children.iterator().next();
		else
			return ff.makeAssociativeExpression(Expression.FCOMP, children, null);	
	}
}
