/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.DOMSUB;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.OVR;
import static org.eventb.core.ast.Formula.RANRES;
import static org.eventb.core.ast.Formula.RANSUB;
import static org.eventb.core.ast.Formula.SETEXT;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.util.Arrays;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;

/**
 * Class used for reasoner which condense several inference rules in a single
 * ProofRule. This class helps to ensure that the final inference rule is valid.
 * It is used by reasoner MembershipGoal for instance.
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * @author Emmanuel Billaud
 */
public abstract class Rule<T extends Predicate> {

	protected final T consequent;
	protected final FormulaFactory ff;
	protected final Rule<?>[] antecedents;

	protected Rule(T consequent, FormulaFactory ff, Rule<?>... antecedents) {
		if (ff == null) {
			throw new NullPointerException("null formula factory");
		}
		if (consequent == null) {
			throw new NullPointerException("null consequent");
		}
		if (antecedents == null) {
			throw new NullPointerException("null array of antecedents");
		}
		for (Rule<?> antecedent : antecedents) {
			if (antecedent == null) {
				throw new NullPointerException("null antecedent");
			}
		}
		this.consequent = consequent;
		this.ff = ff;
		this.antecedents = antecedents.clone();
	}

	public T getConsequent() {
		return consequent;
	}

	protected boolean isMeaningless() {
		switch (consequent.getTag()) {
		case EQUAL:
		case SUBSET:
		case SUBSETEQ:
			break;
		default:
			return false;
		}
		final RelationalPredicate relCons = (RelationalPredicate) consequent;
		return relCons.getRight().equals(relCons.getLeft());
	}

	// This is not the regular equals() method from Object
	// TODO see if really needed
	public boolean equalsRule(Rule<T> other) {
		if (this == other) {
			return true;
		}
		if (!this.ff.equals(other.ff)) {
			return false;
		}
		if (!this.consequent.equals(other.consequent)) {
			return false;
		}
		return Arrays.equals(this.antecedents, other.antecedents);
	}

	public static class Hypothesis<T extends Predicate> extends Rule<T> {

		public Hypothesis(T pred, FormulaFactory ff) {
			super(pred, ff);
		}

	}

	public static class Expr extends Rule<RelationalPredicate> {

		public Expr(Expression expression, FormulaFactory ff) {
			super(computeConsequent(expression, ff), ff);
			assert expression.isWDStrict();//FIXME why
		}

		private static RelationalPredicate computeConsequent(
				Expression expression, FormulaFactory ff) {
			return ff.makeRelationalPredicate(SUBSETEQ, expression, expression,
					null);
		}
	}

	public static abstract class UnaryRule<T extends Predicate> extends Rule<T> {

		public UnaryRule(Rule<T> rule, T consequent) {
			super(consequent, rule.ff, rule);
		}

		public Rule<?> getAntecedent() {
			return antecedents[0];
		}
	}

	public static abstract class BinaryRule<T extends Predicate> extends
			Rule<T> {

		public BinaryRule(Rule<T> rule1, Rule<T> rule2, T consequent) {
			super(consequent, rule1.ff, rule1, rule2);
			if (!rule1.ff.equals(rule2.ff)) {
				throw new IllegalArgumentException(
						"Formula factory of the two given rules should be equals");
			}
		}

		public Rule<?> getFirstAntecedent() {
			return antecedents[0];
		}

		public Rule<?> getSecondAntecedent() {
			return antecedents[1];
		}

	}

	public static class Composition extends BinaryRule<RelationalPredicate> {

		/**
		 * x∈A, A⊆B ⊢ x∈B <br>
		 * x∈A, A⊂B ⊢ x∈B <br>
		 * A⊆B, B⊆C ⊢ A⊆C <br>
		 * A⊆B, B⊂C ⊢ A⊂C <br>
		 * A⊂B, B⊆C ⊢ A⊂C <br>
		 * A⊂B, B⊂C ⊢ A⊂C
		 * 
		 * @param in
		 *            x∈A / Z⊆A / Z⊂A
		 * @param inclusion
		 *            A⊆B / A⊂B
		 */
		public Composition(Rule<RelationalPredicate> in,
				Rule<RelationalPredicate> inclusion) {
			super(in, inclusion, computeConsequent(in, inclusion));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> in,
				Rule<RelationalPredicate> inclusion) {
			assert in.ff.equals(inclusion.ff);

			final RelationalPredicate inPred = in.consequent;
			inCondition(inPred);
			final RelationalPredicate inclusionPred = inclusion.consequent;
			subsetCondition(inclusionPred);
			equalityCondition(inPred.getRight(), inclusionPred.getLeft());
			int tag;
			if (inPred.getTag() == IN) {
				tag = IN;
			} else if (inPred.getTag() == SUBSET
					|| inclusionPred.getTag() == SUBSET) {
				tag = SUBSET;
			} else {
				tag = SUBSETEQ;
			}
			return in.ff.makeRelationalPredicate(tag, inPred.getLeft(),
					inclusionPred.getRight(), null);
		}

	}

	public static class CompositionOvrIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆f, fgh ⊆ k ⊢ egh ⊆ k <br>
		 * e⊂f, fgh ⊆ k ⊢ egh ⊆ k <br>
		 * e⊆f, fgh ⊂ k ⊢ egh ⊂ k <br>
		 * e⊂f, fgh ⊂ k ⊢ egh ⊂ k
		 * 
		 * @param inclusion
		 *            e⊆f / e⊂f
		 * @param ovrInclusion
		 *            fgh ⊆ k / fgh ⊂ k
		 */
		public CompositionOvrIncl(Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> ovrInclusion) {
			super(inclusion, ovrInclusion, computeConsequent(inclusion,
					ovrInclusion));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> ovrInclusion) {
			assert inclusion.ff.equals(ovrInclusion.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate ovrInclPred = ovrInclusion.consequent;
			subsetCondition(ovrInclPred);
			final Expression ovr = ovrInclPred.getLeft();
			ovrCondition(ovr);
			final Expression[] children = ((AssociativeExpression) ovr)
					.getChildren();
			equalityCondition(inclPred.getRight(), children[0]);
			Expression[] newChidlren = new Expression[children.length];
			newChidlren[0] = inclPred.getLeft();
			System.arraycopy(children, 1, newChidlren, 1, children.length - 1);
			final AssociativeExpression newOvr = inclusion.ff
					.makeAssociativeExpression(OVR, newChidlren, null);
			return inclusion.ff.makeRelationalPredicate(ovrInclPred.getTag(),
					newOvr, ovrInclPred.getRight(), null);
		}
	}

	public static class CompositionSetminusLeftIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆f, f∖g⊆A ⊢ e∖g⊆A <br>
		 * e⊂f, f∖g⊆A ⊢ e∖g⊆A <br>
		 * e⊆f, f∖g⊂A ⊢ e∖g⊂A <br>
		 * e⊂f, f∖g⊂A ⊢ e∖g⊂A
		 * 
		 * @param inclusionRule
		 *            e⊆f / e⊂f
		 * @param setminusRule
		 *            f∖g⊆A / f∖g⊂A
		 */
		public CompositionSetminusLeftIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> setminusRule) {
			super(inclusionRule, setminusRule, computeConsequent(inclusionRule,
					setminusRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> setminusRule) {
			assert inclusionRule.ff.equals(setminusRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate setminusPred = setminusRule.consequent;
			subsetCondition(setminusPred);
			final Expression setminus = setminusPred.getLeft();
			setminusCondition(setminus);
			final BinaryExpression binSetminus = (BinaryExpression) setminus;
			equalityCondition(binSetminus.getLeft(), inclusionPred.getRight());
			final BinaryExpression newSetminus = inclusionRule.ff
					.makeBinaryExpression(SETMINUS, inclusionPred.getLeft(),
							binSetminus.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					setminusPred.getTag(), newSetminus,
					setminusPred.getRight(), null);
		}
	}

	public static class CompositionSetminusRightIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * f∖g⊆A, g⊆h ⊢ f∖h⊆A <br>
		 * f∖g⊆A, g⊂h ⊢ f∖h⊂A <br>
		 * f∖g⊂A, g⊆h ⊢ f∖h⊆A <br>
		 * f∖g⊂A, g⊂h ⊢ f∖h⊂A
		 * 
		 * @param setminusRule
		 *            f∖g⊆A / f∖g⊂A
		 * @param inclusionRule
		 *            g⊆h / g⊂h
		 */
		public CompositionSetminusRightIncl(
				Rule<RelationalPredicate> setminusRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(inclusionRule, setminusRule, computeConsequent(inclusionRule,
					setminusRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> setminusRule) {
			assert setminusRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate setminusPred = setminusRule.consequent;
			subsetCondition(setminusPred);
			final Expression setminus = setminusPred.getLeft();
			setminusCondition(setminus);
			final BinaryExpression binSetminus = (BinaryExpression) setminus;
			equalityCondition(binSetminus.getRight(), inclusionPred.getLeft());
			final BinaryExpression newSetminus = inclusionRule.ff
					.makeBinaryExpression(SETMINUS, binSetminus.getLeft(),
							inclusionPred.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					setminusPred.getTag(), newSetminus,
					setminusPred.getRight(), null);
		}
	}

	public static class CompositionSetminusLeftCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈f∖h, f⊆g ⊢ x∈g∖h <br>
		 * x∈f∖h, f⊂g ⊢ x∈g∖h <br>
		 * A⊆f∖h, f⊆g ⊢ A⊆g∖h <br>
		 * A⊆f∖h, f⊂g ⊢ A⊂g∖h <br>
		 * A⊂f∖h, f⊆g ⊢ A⊆g∖h <br>
		 * A⊂f∖h, f⊂g ⊢ A⊂g∖h
		 * 
		 * @param setminusRule
		 *            x∈f∖h / A⊆f∖h / A⊂f∖h
		 * @param inclusionRule
		 *            f⊆g / f⊂g
		 */
		public CompositionSetminusLeftCont(
				Rule<RelationalPredicate> setminusRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(setminusRule, inclusionRule, computeConsequent(setminusRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> setminusRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert inclusionRule.ff.equals(setminusRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate setminusPred = setminusRule.consequent;
			inCondition(setminusPred);
			final Expression setminus = setminusPred.getRight();
			setminusCondition(setminus);
			final BinaryExpression binSetminus = (BinaryExpression) setminus;
			equalityCondition(binSetminus.getLeft(), inclusionPred.getLeft());
			final BinaryExpression newSetminus = inclusionRule.ff
					.makeBinaryExpression(SETMINUS, inclusionPred.getRight(),
							binSetminus.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					setminusPred.getTag(), setminusPred.getLeft(), newSetminus,
					null);
		}
	}

	public static class CompositionSetminusRightCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆g, x∈f∖g ⊢ x∈f∖e <br>
		 * e⊂g, x∈f∖g ⊢ x∈f∖e <br>
		 * e⊆g, A⊆f∖g ⊢ A⊆f∖e <br>
		 * e⊂g, A⊆f∖g ⊢ A⊆f∖e <br>
		 * e⊆g, A⊂f∖g ⊢ A⊂f∖e <br>
		 * e⊂g, A⊂f∖g ⊢ A⊂f∖e
		 * 
		 * @param inclusionRule
		 *            e⊆g / e⊂g
		 * @param setminusRule
		 *            x∈f∖g / A⊆f∖g / A⊂f∖g
		 */
		public CompositionSetminusRightCont(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> setminusRule) {
			super(setminusRule, inclusionRule, computeConsequent(setminusRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> setminusRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert inclusionRule.ff.equals(setminusRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate setminusPred = setminusRule.consequent;
			inCondition(setminusPred);
			final Expression setminus = setminusPred.getRight();
			setminusCondition(setminus);
			final BinaryExpression binSetminus = (BinaryExpression) setminus;
			equalityCondition(binSetminus.getRight(), inclusionPred.getRight());
			final BinaryExpression newSetminus = inclusionRule.ff
					.makeBinaryExpression(SETMINUS, binSetminus.getLeft(),
							inclusionPred.getLeft(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					setminusPred.getTag(), setminusPred.getLeft(), newSetminus,
					null);
		}
	}

	public static class CompositionRanresLeftIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆f, f▷B⊆g ⊢ e▷B⊆g <br>
		 * e⊂f, f▷B⊆g ⊢ e▷B⊆g <br>
		 * e⊆f, f▷B⊂g ⊢ e▷B⊂g <br>
		 * e⊂f, f▷B⊂g ⊢ e▷B⊂g
		 * 
		 * @param inclusionRule
		 *            e⊆f / e⊂f
		 * @param ranresRule
		 *            f▷B⊆g / f▷B⊂g
		 */
		public CompositionRanresLeftIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ranresRule) {
			super(inclusionRule, ranresRule, computeConsequent(inclusionRule,
					ranresRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ranresRule) {
			assert ranresRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ranresPred = ranresRule.consequent;
			subsetCondition(ranresPred);
			final Expression ranres = ranresPred.getLeft();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			equalityCondition(binRanres.getLeft(), inclusionPred.getRight());
			final BinaryExpression newRanres = inclusionRule.ff
					.makeBinaryExpression(RANRES, inclusionPred.getLeft(),
							binRanres.getRight(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(ranresPred.getTag(), newRanres,
							ranresPred.getRight(), null);
		}
	}

	public static class CompositionRanresRightIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * A⊆B, f▷B⊆g ⊢ f▷A⊆g <br>
		 * A⊂B, f▷B⊆g ⊢ f▷A⊆g <br>
		 * A⊆B, f▷B⊂g ⊢ f▷A⊂g <br>
		 * A⊂B, f▷B⊂g ⊢ f▷A⊂g
		 * 
		 * @param inclusionRule
		 *            A⊆B
		 * @param ranresRule
		 *            f▷B⊆g
		 */
		public CompositionRanresRightIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ranresRule) {
			super(inclusionRule, ranresRule, computeConsequent(inclusionRule,
					ranresRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ranresRule) {
			assert ranresRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ranresPred = ranresRule.consequent;
			subsetCondition(ranresPred);
			final Expression ranres = ranresPred.getLeft();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			equalityCondition(binRanres.getRight(), inclusionPred.getRight());
			final BinaryExpression newRanres = inclusionRule.ff
					.makeBinaryExpression(RANRES, binRanres.getLeft(),
							inclusionPred.getLeft(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(ranresPred.getTag(), newRanres,
							ranresPred.getRight(), null);
		}
	}

	public static class CompositionRanresLeftCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈g▷A, g⊆h ⊢ x∈h▷A <br>
		 * x∈g▷A, g⊂h ⊢ x∈h▷A <br>
		 * f⊆g▷A, g⊆h ⊢ f⊆h▷A <br>
		 * f⊆g▷A, g⊂h ⊢ f⊆h▷A <br>
		 * f⊂g▷A, g⊆h ⊢ f⊂h▷A <br>
		 * f⊂g▷A, g⊂h ⊢ f⊂h▷A
		 * 
		 * @param ranresRule
		 *            x∈g▷A / f⊆g▷A / f⊂g▷A
		 * @param inclusionRule
		 *            g⊆h / g⊂h
		 */
		public CompositionRanresLeftCont(Rule<RelationalPredicate> ranresRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(ranresRule, inclusionRule, computeConsequent(ranresRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> ranresRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert inclusionRule.ff.equals(ranresRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ranresPred = ranresRule.consequent;
			inCondition(ranresPred);
			final Expression ranres = ranresPred.getRight();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			equalityCondition(binRanres.getLeft(), inclusionPred.getLeft());
			final BinaryExpression newRanres = inclusionRule.ff
					.makeBinaryExpression(RANRES, inclusionPred.getRight(),
							binRanres.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					ranresPred.getTag(), ranresPred.getLeft(), newRanres, null);
		}
	}

	public static class CompositionRanresRightCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈g▷A, A⊆B ⊢ x∈g▷B <br>
		 * x∈g▷A, A⊂B ⊢ x∈g▷B <br>
		 * f⊆g▷A, A⊆B ⊢ f⊆g▷B <br>
		 * f⊆g▷A, A⊂B ⊢ f⊆g▷B <br>
		 * f⊂g▷A, A⊆B ⊢ f⊂g▷B <br>
		 * f⊂g▷A, A⊂B ⊢ f⊂g▷B
		 * 
		 * @param ranresRule
		 *            x∈g▷A / f⊆g▷A / f⊂g▷A
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 */
		public CompositionRanresRightCont(Rule<RelationalPredicate> ranresRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(ranresRule, inclusionRule, computeConsequent(ranresRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> ranresRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert inclusionRule.ff.equals(ranresRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ranresPred = ranresRule.consequent;
			inCondition(ranresPred);
			final Expression ranres = ranresPred.getRight();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			equalityCondition(binRanres.getRight(), inclusionPred.getLeft());
			final BinaryExpression newRanres = inclusionRule.ff
					.makeBinaryExpression(RANRES, binRanres.getLeft(),
							inclusionPred.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					ranresPred.getTag(), ranresPred.getLeft(), newRanres, null);
		}
	}

	public static class CompositionDomresLeftIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * A⊆B, B◁f⊆g ⊢ A◁f⊆g <br>
		 * A⊂B, B◁f⊆g ⊢ A◁f⊆g <br>
		 * A⊆B, B◁f⊂g ⊢ A◁f⊂g <br>
		 * A⊂B, B◁f⊂g ⊢ A◁f⊂g
		 * 
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 * @param domresRule
		 *            B◁f⊆g / B◁f⊂g
		 */
		public CompositionDomresLeftIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domresRule) {
			super(inclusionRule, domresRule, computeConsequent(inclusionRule,
					domresRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domresRule) {
			assert domresRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domresPred = domresRule.consequent;
			subsetCondition(domresPred);
			final Expression domres = domresPred.getLeft();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			equalityCondition(binDomres.getLeft(), inclusionPred.getRight());
			final BinaryExpression newDomres = inclusionRule.ff
					.makeBinaryExpression(DOMRES, inclusionPred.getLeft(),
							binDomres.getRight(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(domresPred.getTag(), newDomres,
							domresPred.getRight(), null);
		}
	}

	public static class CompositionDomresRightIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆f, B◁f⊆g ⊢ B◁e⊆g <br>
		 * e⊂f, B◁f⊆g ⊢ B◁e⊆g <br>
		 * e⊆f, B◁f⊂g ⊢ B◁e⊂g <br>
		 * e⊂f, B◁f⊂g ⊢ B◁e⊂g
		 * 
		 * @param inclusionRule
		 *            e⊆f / e⊂f
		 * @param domresRule
		 *            B◁f⊆g / B◁f⊂g
		 */
		public CompositionDomresRightIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domresRule) {
			super(inclusionRule, domresRule, computeConsequent(inclusionRule,
					domresRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domresRule) {
			assert domresRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domresPred = domresRule.consequent;
			subsetCondition(domresPred);
			final Expression domres = domresPred.getLeft();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			equalityCondition(binDomres.getRight(), inclusionPred.getRight());
			final BinaryExpression newDomres = inclusionRule.ff
					.makeBinaryExpression(DOMRES, binDomres.getLeft(),
							inclusionPred.getLeft(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(domresPred.getTag(), newDomres,
							domresPred.getRight(), null);
		}
	}

	public static class CompositionDomresLeftCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈A◁g, A⊆B ⊢ x∈B◁g <br>
		 * x∈A◁g, A⊂B ⊢ x∈B◁g <br>
		 * f⊆A◁g, A⊆B ⊢ f⊆B◁g <br>
		 * f⊆A◁g, A⊂B ⊢ f⊆B◁g <br>
		 * f⊂A◁g, A⊆B ⊢ f⊂B◁g <br>
		 * f⊂A◁g, A⊂B ⊢ f⊂B◁g
		 * 
		 * @param domresRule
		 *            x∈A◁g / f⊆A◁g / f⊂A◁g
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 */
		public CompositionDomresLeftCont(Rule<RelationalPredicate> domresRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(domresRule, inclusionRule, computeConsequent(domresRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> domresRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert domresRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domresPred = domresRule.consequent;
			inCondition(domresPred);
			final Expression domres = domresPred.getRight();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			equalityCondition(binDomres.getLeft(), inclusionPred.getLeft());
			final BinaryExpression newDomres = inclusionRule.ff
					.makeBinaryExpression(DOMRES, inclusionPred.getRight(),
							binDomres.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					domresPred.getTag(), domresPred.getLeft(), newDomres, null);
		}
	}

	public static class CompositionDomresRightCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈A◁g, g⊆h ⊢ x∈A◁h <br>
		 * x∈A◁g, g⊂h ⊢ x∈A◁h <br>
		 * f⊆A◁g, g⊆h ⊢ f⊆A◁h <br>
		 * f⊆A◁g, g⊂h ⊢ f⊆A◁h <br>
		 * f⊂A◁g, g⊆h ⊢ f⊂A◁h <br>
		 * f⊂A◁g, g⊂h ⊢ f⊂A◁h
		 * 
		 * @param domresRule
		 *            x∈A◁g / f⊆A◁g / f⊂A◁g
		 * @param inclusionRule
		 *            g⊆h / g⊂h
		 */
		public CompositionDomresRightCont(Rule<RelationalPredicate> domresRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(domresRule, inclusionRule, computeConsequent(domresRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> domresRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert domresRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domresPred = domresRule.consequent;
			inCondition(domresPred);
			final Expression domres = domresPred.getRight();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			equalityCondition(binDomres.getRight(), inclusionPred.getLeft());
			final BinaryExpression newDomres = inclusionRule.ff
					.makeBinaryExpression(DOMRES, binDomres.getLeft(),
							inclusionPred.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					domresPred.getTag(), domresPred.getLeft(), newDomres, null);
		}
	}

	public static class CompositionRansubLeftIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆f, f⩥A⊆g ⊢ e⩥A⊆g <br>
		 * e⊂f, f⩥A⊆g ⊢ e⩥A⊆g <br>
		 * e⊆f, f⩥A⊂g ⊢ e⩥A⊂g <br>
		 * e⊂f, f⩥A⊂g ⊢ e⩥A⊂g
		 * 
		 * @param inclusionRule
		 *            e⊆f / e⊂f
		 * @param ransubRule
		 *            f⩥A⊆g / f⩥A⊂g
		 */
		public CompositionRansubLeftIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ransubRule) {
			super(inclusionRule, ransubRule, computeConsequent(inclusionRule,
					ransubRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ransubRule) {
			assert ransubRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ransubPred = ransubRule.consequent;
			subsetCondition(ransubPred);
			final Expression ransub = ransubPred.getLeft();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			equalityCondition(binRansub.getLeft(), inclusionPred.getRight());
			final BinaryExpression newRansub = inclusionRule.ff
					.makeBinaryExpression(RANSUB, inclusionPred.getLeft(),
							binRansub.getRight(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(ransubPred.getTag(), newRansub,
							ransubPred.getRight(), null);
		}
	}

	public static class CompositionRansubRightIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * f⩥A⊆g, A⊆B ⊢ f⩥B⊆g <br>
		 * f⩥A⊆g, A⊂B ⊢ f⩥B⊆g <br>
		 * f⩥A⊂g, A⊆B ⊢ f⩥B⊂g <br>
		 * f⩥A⊂g, A⊂B ⊢ f⩥B⊂g
		 * 
		 * @param ransubRule
		 *            f⩥A⊆g / f⩥A⊆g
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 */
		public CompositionRansubRightIncl(Rule<RelationalPredicate> ransubRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(inclusionRule, ransubRule, computeConsequent(inclusionRule,
					ransubRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> ransubRule) {
			assert ransubRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ransubPred = ransubRule.consequent;
			subsetCondition(ransubPred);
			final Expression ransub = ransubPred.getLeft();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			equalityCondition(binRansub.getRight(), inclusionPred.getLeft());
			final BinaryExpression newRansub = inclusionRule.ff
					.makeBinaryExpression(RANSUB, binRansub.getLeft(),
							inclusionPred.getRight(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(ransubPred.getTag(), newRansub,
							ransubPred.getRight(), null);
		}
	}

	public static class CompositionRansubLeftCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈g⩥A, g⊆h ⊢ x∈h⩥A <br>
		 * x∈g⩥A, g⊂h ⊢ x∈h⩥A <br>
		 * f⊆g⩥A, g⊆h ⊢ f⊆h⩥A <br>
		 * f⊆g⩥A, g⊂h ⊢ f⊆h⩥A <br>
		 * f⊂g⩥A, g⊆h ⊢ f⊂h⩥A <br>
		 * f⊂g⩥A, g⊂h ⊢ f⊂h⩥A
		 * 
		 * @param ransubRule
		 *            x∈g⩥A / f⊆g⩥A / f⊂g⩥A
		 * @param inclusionRule
		 *            g⊆h / g⊂h
		 */
		public CompositionRansubLeftCont(Rule<RelationalPredicate> ransubRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(ransubRule, inclusionRule, computeConsequent(ransubRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> ransubRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert inclusionRule.ff.equals(ransubRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ransubPred = ransubRule.consequent;
			inCondition(ransubPred);
			final Expression ransub = ransubPred.getRight();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			equalityCondition(binRansub.getLeft(), inclusionPred.getLeft());
			final BinaryExpression newRansub = inclusionRule.ff
					.makeBinaryExpression(RANSUB, inclusionPred.getRight(),
							binRansub.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					ransubPred.getTag(), ransubPred.getLeft(), newRansub, null);
		}
	}

	public static class CompositionRansubRightCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈g⩥B, A⊆B ⊢ x∈g⩥A <br>
		 * x∈g⩥B, A⊂B ⊢ x∈g⩥A <br>
		 * f⊆g⩥B, A⊆B ⊢ f⊆g⩥A <br>
		 * f⊆g⩥B, A⊂B ⊢ f⊆g⩥A <br>
		 * f⊂g⩥B, A⊆B ⊢ f⊂g⩥A <br>
		 * f⊂g⩥B, A⊂B ⊢ f⊂g⩥A
		 * 
		 * @param ransubRule
		 *            x∈g⩥B / f⊆g⩥B / f⊂g⩥B
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 */
		public CompositionRansubRightCont(Rule<RelationalPredicate> ransubRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(ransubRule, inclusionRule, computeConsequent(ransubRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> ransubRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert inclusionRule.ff.equals(ransubRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate ransubPred = ransubRule.consequent;
			inCondition(ransubPred);
			final Expression ransub = ransubPred.getRight();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			equalityCondition(binRansub.getRight(), inclusionPred.getRight());
			final BinaryExpression newRansub = inclusionRule.ff
					.makeBinaryExpression(RANSUB, binRansub.getLeft(),
							inclusionPred.getLeft(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					ransubPred.getTag(), ransubPred.getLeft(), newRansub, null);
		}
	}

	public static class CompositionDomsubLeftIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * A⩤f⊆g, A⊆B ⊢ B⩤f⊆g <br>
		 * A⩤f⊆g, A⊂B ⊢ B⩤f⊆g <br>
		 * A⩤f⊂g, A⊆B ⊢ B⩤f⊂g <br>
		 * A⩤f⊂g, A⊂B ⊢ B⩤f⊂g
		 * 
		 * @param domsubRule
		 *            A⩤f⊆g / A⩤f⊂g
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 */
		public CompositionDomsubLeftIncl(Rule<RelationalPredicate> domsubRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(inclusionRule, domsubRule, computeConsequent(inclusionRule,
					domsubRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domsubRule) {
			assert domsubRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domsubPred = domsubRule.consequent;
			subsetCondition(domsubPred);
			final Expression domsub = domsubPred.getLeft();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			equalityCondition(binDomsub.getLeft(), inclusionPred.getLeft());
			final BinaryExpression newDomsub = inclusionRule.ff
					.makeBinaryExpression(DOMSUB, inclusionPred.getRight(),
							binDomsub.getRight(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(domsubPred.getTag(), newDomsub,
							domsubPred.getRight(), null);
		}
	}

	public static class CompositionDomsubRightIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * e⊆f, A⩤f⊆g ⊢ A⩤e⊆g <br>
		 * e⊂f, A⩤f⊆g ⊢ A⩤e⊆g <br>
		 * e⊆f, A⩤f⊂g ⊢ A⩤e⊂g <br>
		 * e⊂f, A⩤f⊂g ⊢ A⩤e⊂g
		 * 
		 * @param inclusionRule
		 *            e⊆f / e⊂f
		 * @param domsubRule
		 *            A⩤f⊆g / A⩤f⊂g
		 */
		public CompositionDomsubRightIncl(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domsubRule) {
			super(inclusionRule, domsubRule, computeConsequent(inclusionRule,
					domsubRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domsubRule) {
			assert domsubRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domsubPred = domsubRule.consequent;
			subsetCondition(domsubPred);
			final Expression domsub = domsubPred.getLeft();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			equalityCondition(binDomsub.getRight(), inclusionPred.getRight());
			final BinaryExpression newDomsub = inclusionRule.ff
					.makeBinaryExpression(DOMSUB, binDomsub.getLeft(),
							inclusionPred.getLeft(), null);
			return inclusionRule.ff
					.makeRelationalPredicate(domsubPred.getTag(), newDomsub,
							domsubPred.getRight(), null);
		}
	}

	public static class CompositionDomsubLeftCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * A⊆B, x∈B⩤g ⊢ x∈A⩤g <br>
		 * A⊂B, x∈B⩤g ⊢ x∈A⩤g <br>
		 * A⊆B, f⊆B⩤g ⊢ f⊆A⩤g <br>
		 * A⊂B, f⊆B⩤g ⊢ f⊆A⩤g <br>
		 * A⊆B, f⊂B⩤g ⊢ f⊂A⩤g <br>
		 * A⊂B, f⊂B⩤g ⊢ f⊂A⩤g
		 * 
		 * @param inclusionRule
		 *            A⊆B / A⊂B
		 * @param domsubRule
		 *            x∈B⩤g / f⊆B⩤g / f⊂B⩤g
		 */
		public CompositionDomsubLeftCont(
				Rule<RelationalPredicate> inclusionRule,
				Rule<RelationalPredicate> domsubRule) {
			super(domsubRule, inclusionRule, computeConsequent(domsubRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> domsubRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert domsubRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domsubPred = domsubRule.consequent;
			inCondition(domsubPred);
			final Expression domsub = domsubPred.getRight();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			equalityCondition(binDomsub.getLeft(), inclusionPred.getRight());
			final BinaryExpression newDomsub = inclusionRule.ff
					.makeBinaryExpression(DOMSUB, inclusionPred.getLeft(),
							binDomsub.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					domsubPred.getTag(), domsubPred.getLeft(), newDomsub, null);
		}
	}

	public static class CompositionDomsubRightCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x∈A⩤g, g⊆h ⊢ x∈A⩤h <br>
		 * x∈A⩤g, g⊂h ⊢ x∈A⩤h <br>
		 * f⊆A⩤g, g⊆h ⊢ f⊆A⩤h <br>
		 * f⊆A⩤g, g⊂h ⊢ f⊆A⩤h <br>
		 * f⊂A⩤g, g⊆h ⊢ f⊂A⩤h <br>
		 * f⊂A⩤g, g⊂h ⊢ f⊂A⩤h
		 * 
		 * @param domsubRule
		 *            x∈A⩤g / f⊆A⩤g / f⊂A⩤g
		 * @param inclusionRule
		 *            g⊆h / g⊂h
		 */
		public CompositionDomsubRightCont(Rule<RelationalPredicate> domsubRule,
				Rule<RelationalPredicate> inclusionRule) {
			super(domsubRule, inclusionRule, computeConsequent(domsubRule,
					inclusionRule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> domsubRule,
				Rule<RelationalPredicate> inclusionRule) {
			assert domsubRule.ff.equals(inclusionRule.ff);

			final RelationalPredicate inclusionPred = inclusionRule.consequent;
			subsetCondition(inclusionPred);
			final RelationalPredicate domsubPred = domsubRule.consequent;
			inCondition(domsubPred);
			final Expression domsub = domsubPred.getRight();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			equalityCondition(binDomsub.getRight(), inclusionPred.getLeft());
			final BinaryExpression newDomsub = inclusionRule.ff
					.makeBinaryExpression(DOMSUB, binDomsub.getLeft(),
							inclusionPred.getRight(), null);
			return inclusionRule.ff.makeRelationalPredicate(
					domsubPred.getTag(), domsubPred.getLeft(), newDomsub, null);
		}
	}

	public static class CompositionOvrCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x ∈ ghk, g⊆e ⊢ x ∈ ehk <br>
		 * x ∈ ghk, g⊂e ⊢ x ∈ ehk <br>
		 * f ⊆ ghk, g⊆e ⊢ f ⊆ ehk <br>
		 * f ⊆ ghk, g⊂e ⊢ f ⊆ ehk <br>
		 * f ⊂ ghk, g⊆e ⊢ f ⊂ ehk <br>
		 * f ⊂ ghk, g⊂e ⊢ f ⊂ ehk
		 * 
		 * @param ovrInclusion
		 *            x ∈ ghk / f ⊆ ghk / f ⊂ ghk
		 * @param inclusion
		 *            g⊆e / g⊂e
		 */
		public CompositionOvrCont(Rule<RelationalPredicate> ovrInclusion,
				Rule<RelationalPredicate> inclusion) {
			super(ovrInclusion, inclusion, computeConsequent(ovrInclusion,
					inclusion));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> ovrInclusion,
				Rule<RelationalPredicate> inclusion) {
			assert inclusion.ff.equals(ovrInclusion.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate ovrInclPred = ovrInclusion.consequent;
			inCondition(ovrInclPred);
			final Expression ovr = ovrInclPred.getRight();
			ovrCondition(ovr);
			final Expression[] children = ((AssociativeExpression) ovr)
					.getChildren();
			equalityCondition(inclPred.getLeft(), children[0]);
			Expression[] newChidlren = new Expression[children.length];
			newChidlren[0] = inclPred.getRight();
			System.arraycopy(children, 1, newChidlren, 1, children.length - 1);
			final AssociativeExpression newOvr = inclusion.ff
					.makeAssociativeExpression(OVR, newChidlren, null);
			return inclusion.ff.makeRelationalPredicate(ovrInclPred.getTag(),
					ovrInclPred.getLeft(), newOvr, null);
		}
	}

	public static class CompositionBunionIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * Z⊆B, A∪B∪C ⊆ D ⊢ A∪Z∪C ⊆ D <br>
		 * Z⊂B, A∪B∪C ⊆ D ⊢ A∪Z∪C ⊆ D <br>
		 * Z⊆B, A∪B∪C ⊂ D ⊢ A∪Z∪C ⊂ D <br>
		 * Z⊂B, A∪B∪C ⊂ D ⊢ A∪Z∪C ⊂ D
		 * 
		 * @param inclusion
		 *            Z⊆B / Z⊂B
		 * @param unionIncl
		 *            A∪B∪C ⊆ D / A∪B∪C ⊂ D
		 */
		public CompositionBunionIncl(Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> unionIncl) {
			super(inclusion, unionIncl, computeConsequent(inclusion, unionIncl));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> unionIncl) {
			assert inclusion.ff.equals(unionIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate unionInclPred = unionIncl.consequent;
			subsetCondition(unionInclPred);
			final Expression bunion = unionInclPred.getLeft();
			if (bunion.getTag() != BUNION) {
				throw new IllegalArgumentException(bunion.toString()
						+ " should denote a union.");
			}
			final Expression[] children = ((AssociativeExpression) bunion)
					.getChildren();
			Expression[] newChildren = new Expression[children.length];
			boolean substituted = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(inclPred.getRight())) {
					newChildren[i] = inclPred.getLeft();
					substituted = true;
				} else {
					newChildren[i] = children[i];
				}
			}
			if (!substituted) {
				throw new IllegalArgumentException("Expression \""
						+ inclPred.getRight().toString()
						+ "\" cannot be found in : " + bunion.toString());
			}
			final AssociativeExpression newUnion = inclusion.ff
					.makeAssociativeExpression(BUNION, newChildren, null);
			return inclusion.ff.makeRelationalPredicate(unionInclPred.getTag(),
					newUnion, unionInclPred.getRight(), null);
		}

	}

	public static class CompositionBunionCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x ∈ B∪C∪D, C⊆Z ⊢ x ∈ B∪Z∪D <br>
		 * x ∈ B∪C∪D, C⊂Z ⊢ x ∈ B∪Z∪D <br>
		 * A ⊆ B∪C∪D, C⊆Z ⊢ A ⊆ B∪Z∪D <br>
		 * A ⊆ B∪C∪D, C⊂Z ⊢ A ⊆ B∪Z∪D <br>
		 * A ⊂ B∪C∪D, C⊆Z ⊢ A ⊂ B∪Z∪D <br>
		 * A ⊂ B∪C∪D, C⊂Z ⊢ A ⊂ B∪Z∪D
		 * 
		 * @param unionIncl
		 *            x ∈ B∪C∪D / A ⊆ B∪C∪D / x ∈ B∪C∪D
		 * @param inclusion
		 *            C⊆Z / C⊂Z
		 */
		public CompositionBunionCont(Rule<RelationalPredicate> unionIncl,
				Rule<RelationalPredicate> inclusion) {
			super(unionIncl, inclusion, computeConsequent(inclusion, unionIncl));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> unionIncl,
				Rule<RelationalPredicate> inclusion) {
			assert inclusion.ff.equals(unionIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate unionInclPred = unionIncl.consequent;
			inCondition(unionInclPred);
			final Expression bunion = unionInclPred.getRight();
			if (bunion.getTag() != BUNION) {
				throw new IllegalArgumentException(bunion.toString()
						+ " should denote a union.");
			}
			final Expression[] children = ((AssociativeExpression) bunion)
					.getChildren();
			Expression[] newChildren = new Expression[children.length];
			boolean substituted = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(inclPred.getLeft())) {
					newChildren[i] = inclPred.getRight();
					substituted = true;
				} else {
					newChildren[i] = children[i];
				}
			}
			if (!substituted) {
				throw new IllegalArgumentException("Expression \""
						+ inclPred.getRight().toString()
						+ "\" cannot be found in : " + bunion.toString());
			}
			final AssociativeExpression newUnion = inclusion.ff
					.makeAssociativeExpression(BUNION, newChildren, null);
			return inclusion.ff.makeRelationalPredicate(unionInclPred.getTag(),
					unionInclPred.getLeft(), newUnion, null);
		}

	}

	public static class CompositionBinterIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * Z⊆B, A∩B∩C ⊆ D ⊢ A∩Z∩C ⊆ D <br>
		 * Z⊂B, A∩B∩C ⊆ D ⊢ A∩Z∩C ⊆ D <br>
		 * Z⊆B, A∩B∩C ⊂ D ⊢ A∩Z∩C ⊂ D <br>
		 * Z⊂B, A∩B∩C ⊂ D ⊢ A∩Z∩C ⊂ D
		 * 
		 * @param inclusion
		 *            Z⊆B / Z⊂B
		 * @param unionIncl
		 *            A∩B∩C ⊆ D / A∩B∩C ⊂ D
		 */
		public CompositionBinterIncl(Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> unionIncl) {
			super(inclusion, unionIncl, computeConsequent(inclusion, unionIncl));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> unionIncl) {
			assert inclusion.ff.equals(unionIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate unionInclPred = unionIncl.consequent;
			subsetCondition(unionInclPred);
			final Expression bunion = unionInclPred.getLeft();
			if (bunion.getTag() != BINTER) {
				throw new IllegalArgumentException(bunion.toString()
						+ " should denote a union.");
			}
			final Expression[] children = ((AssociativeExpression) bunion)
					.getChildren();
			Expression[] newChildren = new Expression[children.length];
			boolean substituted = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(inclPred.getRight())) {
					newChildren[i] = inclPred.getLeft();
					substituted = true;
				} else {
					newChildren[i] = children[i];
				}
			}
			if (!substituted) {
				throw new IllegalArgumentException("Expression \""
						+ inclPred.getRight().toString()
						+ "\" cannot be found in : " + bunion.toString());
			}
			final AssociativeExpression newUnion = inclusion.ff
					.makeAssociativeExpression(BINTER, newChildren, null);
			return inclusion.ff.makeRelationalPredicate(unionInclPred.getTag(),
					newUnion, unionInclPred.getRight(), null);
		}

	}

	public static class CompositionBinterCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x ∈ B∩C∩D, C⊆Z ⊢ x ∈ B∩Z∩D <br>
		 * x ∈ B∩C∩D, C⊂Z ⊢ x ∈ B∩Z∩D <br>
		 * A ⊆ B∩C∩D, C⊆Z ⊢ A ⊆ B∩Z∩D <br>
		 * A ⊆ B∩C∩D, C⊂Z ⊢ A ⊆ B∩Z∩D <br>
		 * A ⊂ B∩C∩D, C⊆Z ⊢ A ⊂ B∩Z∩D <br>
		 * A ⊂ B∩C∩D, C⊂Z ⊢ A ⊂ B∩Z∩D
		 * 
		 * @param interIncl
		 *            x ∈ B∩C∩D / A ⊆ B∩C∩D / A ⊂ B∩C∩D
		 * @param inclusion
		 *            C⊆Z/C⊂Z
		 */
		public CompositionBinterCont(Rule<RelationalPredicate> interIncl,
				Rule<RelationalPredicate> inclusion) {
			super(interIncl, inclusion, computeConsequent(interIncl, inclusion));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> interIncl,
				Rule<RelationalPredicate> inclusion) {
			assert inclusion.ff.equals(interIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate interInclPred = interIncl.consequent;
			inCondition(interInclPred);
			final Expression binter = interInclPred.getRight();
			if (binter.getTag() != BINTER) {
				throw new IllegalArgumentException(binter.toString()
						+ " should denote a union.");
			}
			final Expression[] children = ((AssociativeExpression) binter)
					.getChildren();
			Expression[] newChildren = new Expression[children.length];
			boolean substituted = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals(inclPred.getLeft())) {
					newChildren[i] = inclPred.getRight();
					substituted = true;
				} else {
					newChildren[i] = children[i];
				}
			}
			if (!substituted) {
				throw new IllegalArgumentException("Expression \""
						+ inclPred.getRight().toString()
						+ "\" cannot be found in : " + binter.toString());
			}
			final AssociativeExpression newInter = inclusion.ff
					.makeAssociativeExpression(BINTER, newChildren, null);
			return inclusion.ff.makeRelationalPredicate(interInclPred.getTag(),
					interInclPred.getLeft(), newInter, null);
		}

	}

	public static class CompositionCProdLeftIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * A⊆B, B×C ⊆ D ⊢ A×C ⊆ D <br>
		 * A⊂B, B×C ⊆ D ⊢ A×C ⊆ D <br>
		 * A⊆B, B×C ⊂ D ⊢ A×C ⊂ D <br>
		 * A⊂B, B×C ⊂ D ⊢ A×C ⊂ D
		 * 
		 * @param inclusion
		 *            A⊆B / A⊂B
		 * @param cprodIncl
		 *            B×C ⊆ D / B×C ⊂ D
		 */
		public CompositionCProdLeftIncl(Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> cprodIncl) {
			super(inclusion, cprodIncl, computeConsequent(inclusion, cprodIncl));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> cprodIncl) {
			assert inclusion.ff.equals(cprodIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate cprodInclPred = cprodIncl.consequent;
			subsetCondition(cprodInclPred);
			final Expression cprod = cprodInclPred.getLeft();
			cprodCondition(cprod);
			final BinaryExpression binCprod = (BinaryExpression) cprod;
			final Expression right = binCprod.getRight();
			equalityCondition(binCprod.getLeft(), inclPred.getRight());
			final BinaryExpression newCprod = inclusion.ff
					.makeBinaryExpression(CPROD, inclPred.getLeft(), right,
							null);
			return inclusion.ff.makeRelationalPredicate(cprodInclPred.getTag(),
					newCprod, cprodInclPred.getRight(), null);
		}

	}

	public static class CompositionCProdRightIncl extends
			BinaryRule<RelationalPredicate> {

		/**
		 * A⊆C, B×C ⊆ D ⊢ B×A ⊆ D <br>
		 * A⊂C, B×C ⊆ D ⊢ B×A ⊆ D <br>
		 * A⊆C, B×C ⊂ D ⊢ B×A ⊂ D <br>
		 * A⊂C, B×C ⊂ D ⊢ B×A ⊂ D
		 * 
		 * @param inclusion
		 *            A⊆C / A⊂C
		 * @param cprodIncl
		 *            B×C ⊆ D / B×C ⊂ D
		 */
		public CompositionCProdRightIncl(Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> cprodIncl) {
			super(inclusion, cprodIncl, computeConsequent(inclusion, cprodIncl));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> inclusion,
				Rule<RelationalPredicate> cprodIncl) {
			assert inclusion.ff.equals(cprodIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate cprodInclPred = cprodIncl.consequent;
			subsetCondition(cprodInclPred);
			final Expression cprod = cprodInclPred.getLeft();
			cprodCondition(cprod);
			final BinaryExpression binCprod = (BinaryExpression) cprod;
			final Expression left = binCprod.getLeft();
			equalityCondition(binCprod.getRight(), inclPred.getRight());
			final BinaryExpression newCprod = inclusion.ff
					.makeBinaryExpression(CPROD, left, inclPred.getLeft(), null);
			return inclusion.ff.makeRelationalPredicate(cprodInclPred.getTag(),
					newCprod, cprodInclPred.getRight(), null);
		}

	}

	public static class CompositionCProdLeftCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x ∈ C×D, C⊆A ⊢ x ∈ A×D <br>
		 * x ∈ C×D, C⊂A ⊢ x ∈ A×D <br>
		 * B ⊆ C×D, C⊆A ⊢ B ⊆ A×D <br>
		 * B ⊆ C×D, C⊂A ⊢ B ⊆ A×D <br>
		 * B ⊂ C×D, C⊆A ⊢ B ⊂ A×D <br>
		 * B ⊂ C×D, C⊂A ⊢ B ⊂ A×D
		 * 
		 * @param cprodIncl
		 *            x ∈ C×D / B ⊆ C×D / B ⊂ C×D
		 * @param inclusion
		 *            C⊆A / C⊂A
		 */
		public CompositionCProdLeftCont(Rule<RelationalPredicate> cprodIncl,
				Rule<RelationalPredicate> inclusion) {
			super(cprodIncl, inclusion, computeConsequent(cprodIncl, inclusion));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> cprodIncl,
				Rule<RelationalPredicate> inclusion) {
			assert inclusion.ff.equals(cprodIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate cprodInclPred = cprodIncl.consequent;
			inCondition(cprodInclPred);
			final Expression cprod = cprodInclPred.getRight();
			cprodCondition(cprod);
			final BinaryExpression binCprod = (BinaryExpression) cprod;
			final Expression right = binCprod.getRight();
			equalityCondition(binCprod.getLeft(), inclPred.getLeft());
			final BinaryExpression newCprod = inclusion.ff
					.makeBinaryExpression(CPROD, inclPred.getRight(), right,
							null);
			return inclusion.ff.makeRelationalPredicate(cprodInclPred.getTag(),
					cprodInclPred.getLeft(), newCprod, null);
		}
	}

	public static class CompositionCProdRightCont extends
			BinaryRule<RelationalPredicate> {

		/**
		 * x ∈ C×D, D⊆A ⊢ x ∈ C×A <br>
		 * x ∈ C×D, D⊂A ⊢ x ∈ C×A <br>
		 * B ⊆ C×D, D⊆A ⊢ B ⊆ C×A <br>
		 * B ⊆ C×D, D⊂A ⊢ B ⊆ C×A <br>
		 * B ⊂ C×D, D⊆A ⊢ B ⊂ C×A <br>
		 * B ⊂ C×D, D⊂A ⊢ B ⊂ C×A
		 * 
		 * @param cprodIncl
		 *            x ∈ C×D / B ⊆ C×D / B ⊂ C×D
		 * @param inclusion
		 *            D⊆A / D⊂A
		 */
		public CompositionCProdRightCont(Rule<RelationalPredicate> cprodIncl,
				Rule<RelationalPredicate> inclusion) {
			super(inclusion, cprodIncl, computeConsequent(inclusion, cprodIncl));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> cprodIncl,
				Rule<RelationalPredicate> inclusion) {
			assert inclusion.ff.equals(cprodIncl.ff);

			final RelationalPredicate inclPred = inclusion.consequent;
			subsetCondition(inclPred);
			final RelationalPredicate cprodInclPred = cprodIncl.consequent;
			inCondition(cprodInclPred);
			final Expression cprod = cprodInclPred.getRight();
			cprodCondition(cprod);
			final BinaryExpression binCprod = (BinaryExpression) cprod;
			final Expression left = binCprod.getLeft();
			equalityCondition(binCprod.getRight(), inclPred.getLeft());
			final BinaryExpression newCprod = inclusion.ff
					.makeBinaryExpression(CPROD, left, inclPred.getRight(),
							null);
			return inclusion.ff.makeRelationalPredicate(cprodInclPred.getTag(),
					cprodInclPred.getLeft(), newCprod, null);
		}

	}

	public static class Domain extends UnaryRule<RelationalPredicate> {

		/**
		 * A⊆B ⊢ dom(A)⊆dom(B) <br>
		 * A∼⊆B ⊢ ran(A)⊆dom(B) <br>
		 * A⊆B∼ ⊢ dom(A)⊆ran(B) <br>
		 * x↦y∈A ⊢ x∈dom(A) <br>
		 * A⊂B ⊢ dom(A)⊆dom(B) <br>
		 * A∼⊂B ⊢ ran(A)⊆dom(B) <br>
		 * A⊂B∼ ⊢ dom(A)⊆ran(B) 
		 * 
		 * @param rule
		 */
		public Domain(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression right = predicate.getRight();
			final Expression left = predicate.getLeft();
			Expression domLeft;
			switch (predicate.getTag()) {
			case SUBSET:
			case SUBSETEQ:
				domLeft = computeDomainApplication(ff, left);
				break;
			case IN:
				mapstoCondition(left);
				domLeft = ((BinaryExpression) left).getLeft();
				break;
			default:
				throw new IllegalArgumentException(
						predicate.toString()
								+ " should denote either a subset, or, in particular cases, a membership");
			}
			final Expression domRight = computeDomainApplication(ff, right);
			return ff.makeRelationalPredicate(predicate.getTag(), domLeft,
					domRight, null);
		}

		private static Expression computeDomainApplication(
				final FormulaFactory ff, final Expression expression) {
			if (expression.getTag() == CONVERSE) {
				final Expression childC = ((UnaryExpression) expression)
						.getChild();
				return Range.computeRangeApplication(ff, childC);
			}
			return ff.makeUnaryExpression(KDOM, expression, null);
		}

	}

	public static class Range extends UnaryRule<RelationalPredicate> {

		/**
		 * A⊆B ⊢ ran(A)⊆ran(B) <br>
		 * A∼⊆B ⊢ dom(A)⊆ran(B) <br>
		 * A⊆B∼ ⊢ ran(A)⊆dom(B) <br>
		 * x↦y∈A ⊢ y∈ran(A) <br>
		 * A⊂B ⊢ ran(A)⊆ran(B) <br>
		 * A∼⊂B ⊢ dom(A)⊆ran(B) <br>
		 * A⊂B∼ ⊢ ran(A)⊆dom(B) <br>
		 * x↦y∈A ⊢ y∈ran(A)
		 * 
		 * @param rule
		 */
		public Range(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression right = predicate.getRight();
			final Expression left = predicate.getLeft();
			Expression ranLeft;
			switch (predicate.getTag()) {
			case SUBSET:
			case SUBSETEQ:
				ranLeft = computeRangeApplication(ff, left);
				break;
			case IN:
				mapstoCondition(left);
				ranLeft = ((BinaryExpression) left).getRight();
				break;
			default:
				throw new IllegalArgumentException(
						predicate.toString()
								+ " should denote either a subset, or, in particular cases, a membership");
			}
			final Expression ranRight = computeRangeApplication(ff, right);
			return ff.makeRelationalPredicate(predicate.getTag(), ranLeft,
					ranRight, null);
		}

		private static Expression computeRangeApplication(
				final FormulaFactory ff, final Expression expression) {
			if (expression.getTag() == CONVERSE) {
				final Expression childC = ((UnaryExpression) expression)
						.getChild();
				return Domain.computeDomainApplication(ff, childC);
			}
			return ff.makeUnaryExpression(KRAN, expression, null);
		}
	}

	public static class Converse extends UnaryRule<RelationalPredicate> {

		/**
		 * A⊆B ⊢ A∼⊆B∼ <br>
		 * A∼⊆B ⊢ A⊆B∼ <br>
		 * A⊆B∼ ⊢ A∼⊆B <br>
		 * A⊂B ⊢ A∼⊂B∼ <br>
		 * A∼⊂B ⊢ A⊂B∼ <br>
		 * A⊂B∼ ⊢ A∼⊂B
		 * 
		 * @param rule
		 *            A⊆B
		 */
		public Converse(Rule<RelationalPredicate> rule) {
			super(computeRule(rule), computeConsequent(rule));
			if (rule instanceof Rule.Converse) {
				Rule.UnaryRule<RelationalPredicate> p = (Rule.UnaryRule<RelationalPredicate>) rule;
				p.getAntecedent();
				p.getAntecedent().getConsequent();
			}
		}

		@SuppressWarnings("unchecked")
		private static Rule<RelationalPredicate> computeRule(
				Rule<RelationalPredicate> rule) {
			if (rule instanceof Rule.Converse) {
				return (Rule<RelationalPredicate>) ((UnaryRule<RelationalPredicate>) rule)
						.getAntecedent();
			}
			return rule;
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			if (rule instanceof Rule.Converse) {
				return (RelationalPredicate) ((Rule.UnaryRule<RelationalPredicate>) rule)
						.getAntecedent().getConsequent();
			}
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			subsetCondition(predicate);
			final Expression convRight = computeConverseApplication(ff,
					predicate.getRight());
			final Expression convLeft = computeConverseApplication(ff,
					predicate.getLeft());
			return ff.makeRelationalPredicate(predicate.getTag(), convLeft,
					convRight, null);
		}

		private static Expression computeConverseApplication(
				final FormulaFactory ff, final Expression expression) {
			switch (expression.getTag()) {
			case CONVERSE:
				return ((UnaryExpression) expression).getChild();
			case RANRES:
				final BinaryExpression ranres = (BinaryExpression) expression;
				if (ranres.getLeft().getTag() == KID_GEN) {
					return expression;
				}
				break;
			case DOMRES:
				final BinaryExpression domres = (BinaryExpression) expression;
				if (domres.getRight().getTag() == KID_GEN) {
					return ff.makeBinaryExpression(RANRES, domres.getRight(),
							domres.getLeft(), null);
				}
				break;
			}
			return ff.makeUnaryExpression(CONVERSE, expression, null);
		}

	}

	public static class EqualLeft extends UnaryRule<RelationalPredicate> {

		/**
		 * A=B ⊢ A⊆B
		 * 
		 * @param rule
		 */
		public EqualLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			if (predicate.getTag() != EQUAL) {
				throw new IllegalArgumentException(predicate.toString()
						+ " should be an equality.");
			}
			final Expression right = predicate.getRight();
			final Expression left = predicate.getLeft();
			return ff.makeRelationalPredicate(Formula.SUBSETEQ, left, right,
					null);
		}

	}

	public static class EqualRight extends UnaryRule<RelationalPredicate> {

		/**
		 * A=B ⊢ B⊆A
		 * 
		 * @param rule
		 */
		public EqualRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			if (predicate.getTag() != EQUAL) {
				throw new IllegalArgumentException(predicate.toString()
						+ " should be an equality.");
			}
			final Expression right = predicate.getRight();
			final Expression left = predicate.getLeft();
			return ff.makeRelationalPredicate(Formula.SUBSETEQ, right, left,
					null);
		}

	}

	public static class SimpDomCProdLeft extends UnaryRule<RelationalPredicate> {

		/**
		 * dom(A×B) <i>R</i> C ⊢ A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomCProdLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression dom = predicate.getLeft();
			domainCondition(dom);
			final Expression cprod = ((UnaryExpression) dom).getChild();
			cprodCondition(cprod);
			final Expression newExp = ((BinaryExpression) cprod).getLeft();
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpDomCProdRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> dom(A×B) ⊢ C <i>R</i> A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomCProdRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression dom = predicate.getRight();
			domainCondition(dom);
			final Expression cprod = ((UnaryExpression) dom).getChild();
			cprodCondition(cprod);
			final Expression newExp = ((BinaryExpression) cprod).getLeft();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpRanCProdLeft extends UnaryRule<RelationalPredicate> {

		/**
		 * ran(A×B) <i>R</i> C ⊢ B <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanCProdLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression ran = predicate.getLeft();
			rangeCondition(ran);
			final Expression cprod = ((UnaryExpression) ran).getChild();
			cprodCondition(cprod);
			final Expression newExp = ((BinaryExpression) cprod).getRight();
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpRanCProdRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> ran(A×B) ⊢ C <i>R</i> B
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanCProdRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression ran = predicate.getRight();
			rangeCondition(ran);
			final Expression cprod = ((UnaryExpression) ran).getChild();
			cprodCondition(cprod);
			final Expression newExp = ((BinaryExpression) cprod).getRight();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpConvCProdLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * (A×B)∼ <i>R</i> C ⊢ B×A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvCProdLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getLeft();
			converseCondition(converse);
			final Expression cprod = ((UnaryExpression) converse).getChild();
			cprodCondition(cprod);
			final BinaryExpression binCProd = (BinaryExpression) cprod;
			final Expression newExp = ff.makeBinaryExpression(CPROD,
					binCProd.getRight(), binCProd.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpConvCProdRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> (A×B)∼ ⊢ C <i>R</i> B×A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvCProdRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getRight();
			converseCondition(converse);
			final Expression cprod = ((UnaryExpression) converse).getChild();
			cprodCondition(cprod);
			final BinaryExpression binCProd = (BinaryExpression) cprod;
			final Expression newExp = ff.makeBinaryExpression(CPROD,
					binCProd.getRight(), binCProd.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpConvDomresRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> (A◁f)∼ ⊢ C <i>R</i> f∼▷A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvDomresRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getRight();
			converseCondition(converse);
			final Expression domres = ((UnaryExpression) converse).getChild();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binDomres.getRight(), null);
			final Expression newExp = ff.makeBinaryExpression(RANRES, conv,
					binDomres.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpConvDomresLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * (A◁f)∼ <i>R</i> C ⊢ f∼▷A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvDomresLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getLeft();
			converseCondition(converse);
			final Expression domres = ((UnaryExpression) converse).getChild();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binDomres.getRight(), null);
			final Expression newExp = ff.makeBinaryExpression(RANRES, conv,
					binDomres.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpConvRanresRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> (f▷A)∼ ⊢ C <i>R</i> A◁f∼
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvRanresRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getRight();
			converseCondition(converse);
			final Expression ranres = ((UnaryExpression) converse).getChild();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binRanres.getLeft(), null);
			final Expression newExp = ff.makeBinaryExpression(DOMRES,
					binRanres.getRight(), conv, null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpConvRanresLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * (f▷A)∼ <i>R</i> C ⊢ A◁f∼ <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvRanresLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getLeft();
			converseCondition(converse);
			final Expression ranres = ((UnaryExpression) converse).getChild();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binRanres.getLeft(), null);
			final Expression newExp = ff.makeBinaryExpression(DOMRES,
					binRanres.getRight(), conv, null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpConvDomsubRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> (A⩤f)∼ ⊢ C <i>R</i> f∼⩥A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvDomsubRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getRight();
			converseCondition(converse);
			final Expression domsub = ((UnaryExpression) converse).getChild();
			domsubCondition(domsub);
			final BinaryExpression binDomres = (BinaryExpression) domsub;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binDomres.getRight(), null);
			final Expression newExp = ff.makeBinaryExpression(RANSUB, conv,
					binDomres.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpConvDomsubLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * (A⩤f)∼ <i>R</i> C ⊢ f∼⩥A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvDomsubLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getLeft();
			converseCondition(converse);
			final Expression domsub = ((UnaryExpression) converse).getChild();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binDomsub.getRight(), null);
			final Expression newExp = ff.makeBinaryExpression(RANSUB, conv,
					binDomsub.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpConvRansubRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> (f⩥A)∼ ⊢ C <i>R</i> A⩤f∼
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvRansubRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getRight();
			converseCondition(converse);
			final Expression ransub = ((UnaryExpression) converse).getChild();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binRansub.getLeft(), null);
			final Expression newExp = ff.makeBinaryExpression(DOMSUB,
					binRansub.getRight(), conv, null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpConvRansubLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * (f⩥A)∼ <i>R</i> C ⊢ A⩤f∼ <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpConvRansubLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression converse = predicate.getLeft();
			converseCondition(converse);
			final Expression ransub = ((UnaryExpression) converse).getChild();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			final UnaryExpression conv = ff.makeUnaryExpression(CONVERSE,
					binRansub.getLeft(), null);
			final Expression newExp = ff.makeBinaryExpression(DOMSUB,
					binRansub.getRight(), conv, null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpDomDomresRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> dom(A◁f) ⊢ C <i>R</i> dom(f)∩A<br>
		 * C <i>R</i> dom(A◁prj1) ⊢ C <i>R</i> A<br>
		 * C <i>R</i> dom(A◁prj2) ⊢ C <i>R</i> A<br>
		 * C <i>R</i> dom(A◁id) ⊢ C <i>R</i> A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomDomresRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain = predicate.getRight();
			domainCondition(domain);
			final Expression domres = ((UnaryExpression) domain).getChild();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			switch (binDomres.getRight().getTag()) {
			case KPRJ1_GEN:
			case KPRJ2_GEN:
			case KID_GEN:
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), binDomres.getLeft(), null);
			}
			final UnaryExpression dom = ff.makeUnaryExpression(KDOM,
					binDomres.getRight(), null);
			final Expression[] children = { dom, binDomres.getLeft() };
			final Expression newExp = ff.makeAssociativeExpression(BINTER,
					children, null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpDomDomresLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * dom(A◁f) <i>R</i> C ⊢ dom(f)∩A <i>R</i> C<br>
		 * dom(f◁prj1) <i>R</i> g ⊢ f <i>R</i> g<br>
		 * dom(f◁prj2) <i>R</i> g ⊢ f <i>R</i> g<br>
		 * dom(f◁id) <i>R</i> g ⊢ f <i>R</i> g
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomDomresLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain = predicate.getLeft();
			domainCondition(domain);
			final Expression domres = ((UnaryExpression) domain).getChild();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			switch (binDomres.getRight().getTag()) {
			case KPRJ1_GEN:
			case KPRJ2_GEN:
			case KID_GEN:
				return ff.makeRelationalPredicate(predicate.getTag(),
						binDomres.getLeft(), predicate.getRight(), null);
			}
			final UnaryExpression dom = ff.makeUnaryExpression(KDOM,
					binDomres.getRight(), null);
			final Expression[] children = { dom, binDomres.getLeft() };
			final Expression newExp = ff.makeAssociativeExpression(BINTER,
					children, null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpRanRanresRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> ran(f▷A) ⊢ C <i>R</i> ran(f)∩A<br>
		 * C <i>R</i> ran(prj1▷A) ⊢ C <i>R</i> A<br>
		 * C <i>R</i> ran(prj2▷A) ⊢ C <i>R</i> A<br>
		 * C <i>R</i> ran(id▷A) ⊢ C <i>R</i> A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanRanresRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getRight();
			rangeCondition(range);
			final Expression ranres = ((UnaryExpression) range).getChild();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			switch (binRanres.getLeft().getTag()) {
			case KPRJ1_GEN:
			case KPRJ2_GEN:
			case KID_GEN:
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), binRanres.getRight(), null);
			}
			final UnaryExpression ran = ff.makeUnaryExpression(KRAN,
					binRanres.getLeft(), null);
			final Expression[] children = { ran, binRanres.getRight() };
			final Expression newExp = ff.makeAssociativeExpression(BINTER,
					children, null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpRanRanresLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * ran(f▷A) <i>R</i> C ⊢ ran(f)∩A <i>R</i> C<br>
		 * ran(prj1▷A) <i>R</i> C ⊢ A <i>R</i> C<br>
		 * ran(prj2▷A) <i>R</i> C ⊢ A <i>R</i> C<br>
		 * ran(id▷A) <i>R</i> C ⊢ A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanRanresLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getLeft();
			rangeCondition(range);
			final Expression ranres = ((UnaryExpression) range).getChild();
			ranresCondition(ranres);
			final BinaryExpression binRanres = (BinaryExpression) ranres;
			switch (binRanres.getLeft().getTag()) {
			case KPRJ1_GEN:
			case KPRJ2_GEN:
			case KID_GEN:
				return ff.makeRelationalPredicate(predicate.getTag(),
						binRanres.getRight(), predicate.getRight(), null);
			}
			final UnaryExpression ran = ff.makeUnaryExpression(KRAN,
					binRanres.getLeft(), null);
			final Expression[] children = { ran, binRanres.getRight() };
			final Expression newExp = ff.makeAssociativeExpression(BINTER,
					children, null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpDomDomsubRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> dom(A⩤f) ⊢ C <i>R</i> dom(f)∖A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomDomsubRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain = predicate.getRight();
			domainCondition(domain);
			final Expression domsub = ((UnaryExpression) domain).getChild();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			final UnaryExpression dom = ff.makeUnaryExpression(KDOM,
					binDomsub.getRight(), null);
			final Expression newExp = ff.makeBinaryExpression(SETMINUS, dom,
					binDomsub.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpDomDomsubLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * dom(A⩤f) <i>R</i> C ⊢ dom(f)∖A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomDomsubLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain = predicate.getLeft();
			domainCondition(domain);
			final Expression domsub = ((UnaryExpression) domain).getChild();
			domsubCondition(domsub);
			final BinaryExpression binDomsub = (BinaryExpression) domsub;
			final UnaryExpression dom = ff.makeUnaryExpression(KDOM,
					binDomsub.getRight(), null);
			final Expression newExp = ff.makeBinaryExpression(SETMINUS, dom,
					binDomsub.getLeft(), null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpRanRansubRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> ran(f⩥A) ⊢ C <i>R</i> ran(f)∖A
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanRansubRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getRight();
			rangeCondition(range);
			final Expression ransub = ((UnaryExpression) range).getChild();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			final UnaryExpression ran = ff.makeUnaryExpression(KRAN,
					binRansub.getLeft(), null);
			final Expression newExp = ff.makeBinaryExpression(SETMINUS, ran,
					binRansub.getRight(), null);
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class SimpRanRansubLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * ran(f⩥A) <i>R</i> C ⊢ ran(f)∖A <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanRansubLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getLeft();
			rangeCondition(range);
			final Expression ransub = ((UnaryExpression) range).getChild();
			ransubCondition(ransub);
			final BinaryExpression binRansub = (BinaryExpression) ransub;
			final UnaryExpression ran = ff.makeUnaryExpression(KRAN,
					binRansub.getLeft(), null);
			final Expression newExp = ff.makeBinaryExpression(SETMINUS, ran,
					binRansub.getRight(), null);
			return ff.makeRelationalPredicate(predicate.getTag(), newExp,
					predicate.getRight(), null);
		}

	}

	public static class SimpRanDomresKxxLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * ran(f◁prj1) <i>R</i> C ⊢ dom(f) <i>R</i> C<br>
		 * ran(f◁prj2) <i>R</i> C ⊢ ran(f) <i>R</i> C<br>
		 * ran(f◁id) <i>R</i> C ⊢ f <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanDomresKxxLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getLeft();
			rangeCondition(range);
			final Expression domres = ((UnaryExpression) range).getChild();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			final Expression left = binDomres.getLeft();
			switch (binDomres.getRight().getTag()) {
			case KPRJ1_GEN:
				final UnaryExpression dom = ff.makeUnaryExpression(KDOM, left,
						null);
				return ff.makeRelationalPredicate(predicate.getTag(),
						dom, predicate.getRight(), null);
			case KPRJ2_GEN:
				final UnaryExpression ran = ff.makeUnaryExpression(KRAN, left,
						null);
				return ff.makeRelationalPredicate(predicate.getTag(),
						ran, predicate.getRight(), null);
			case KID_GEN:
				return ff.makeRelationalPredicate(predicate.getTag(),
						left, predicate.getRight(), null);
			default:
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
		}

	}

	public static class SimpRanDomresKxxRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> ran(f◁prj1) ⊢ C <i>R</i> dom(f)<br>
		 * C <i>R</i> ran(f◁prj2) ⊢ C <i>R</i> ran(f)<br>
		 * C <i>R</i> ran(f◁id) ⊢ C <i>R</i> f
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanDomresKxxRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getRight();
			rangeCondition(range);
			final Expression domres = ((UnaryExpression) range).getChild();
			domresCondition(domres);
			final BinaryExpression binDomres = (BinaryExpression) domres;
			final Expression left = binDomres.getLeft();
			switch (binDomres.getRight().getTag()) {
			case KPRJ1_GEN:
				final UnaryExpression dom = ff.makeUnaryExpression(KDOM, left,
						null);
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), dom, null);
			case KPRJ2_GEN:
				final UnaryExpression ran = ff.makeUnaryExpression(KRAN, left,
						null);
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), ran, null);
			case KID_GEN:
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), left, null);
			default:
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
		}

	}

	public static class SimpDomRanresIdLeft extends
			UnaryRule<RelationalPredicate> {

		/**
		 * dom(id▷f) <i>R</i> C ⊢ f <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomRanresIdLeft(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain = predicate.getLeft();
			domainCondition(domain);
			final Expression ranres = ((UnaryExpression) domain).getChild();
			ranresCondition(ranres);
			final BinaryExpression binranres = (BinaryExpression) ranres;
			if (binranres.getLeft().getTag() != KID_GEN) {
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
			return ff.makeRelationalPredicate(predicate.getTag(),
					binranres.getRight(), predicate.getRight(), null);
		}

	}

	public static class SimpDomRanresIdRight extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> dom(id▷f) ⊢ C <i>R</i> f
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomRanresIdRight(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain = predicate.getRight();
			domainCondition(domain);
			final Expression ranres = ((UnaryExpression) domain).getChild();
			ranresCondition(ranres);
			final BinaryExpression binranres = (BinaryExpression) ranres;
			if (binranres.getLeft().getTag() != KID_GEN) {
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), binranres.getRight(), null);
		}

	}

	public static class SimpRanDomRanresPrj2Left extends
			UnaryRule<RelationalPredicate> {

		/**
		 * ran(dom(prj2▷f)) <i>R</i> C ⊢ f <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanDomRanresPrj2Left(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getLeft();
			rangeCondition(range);
			final Expression domain = ((UnaryExpression) range).getChild();
			domainCondition(domain);
			final Expression ranres = ((UnaryExpression) domain).getChild();
			ranresCondition(ranres);
			final BinaryExpression binranres = (BinaryExpression) ranres;
			if (binranres.getLeft().getTag() != KPRJ2_GEN) {
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
			return ff.makeRelationalPredicate(predicate.getTag(),
					binranres.getRight(), predicate.getRight(), null);
		}

	}

	public static class SimpRanDomRanresPrj2Right extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> ran(dom(prj2▷f)) ⊢ C <i>R</i> f
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpRanDomRanresPrj2Right(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression range = predicate.getRight();
			rangeCondition(range);
			final Expression domain = ((UnaryExpression) range).getChild();
			domainCondition(domain);
			final Expression ranres = ((UnaryExpression) domain).getChild();
			ranresCondition(ranres);
			final BinaryExpression binranres = (BinaryExpression) ranres;
			if (binranres.getLeft().getTag() != KPRJ2_GEN) {
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), binranres.getRight(), null);
		}

	}

	public static class SimpDomDomRanresPrj1Left extends
			UnaryRule<RelationalPredicate> {

		/**
		 * dom(dom(prj1▷f)) <i>R</i> C ⊢ f <i>R</i> C
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomDomRanresPrj1Left(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain1 = predicate.getLeft();
			domainCondition(domain1);
			final Expression domain2 = ((UnaryExpression) domain1).getChild();
			domainCondition(domain2);
			final Expression ranres = ((UnaryExpression) domain2).getChild();
			ranresCondition(ranres);
			final BinaryExpression binranres = (BinaryExpression) ranres;
			if (binranres.getLeft().getTag() != KPRJ1_GEN) {
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
			return ff.makeRelationalPredicate(predicate.getTag(),
					binranres.getRight(), predicate.getRight(), null);
		}

	}

	public static class SimpDomDomRanresPrj1Right extends
			UnaryRule<RelationalPredicate> {

		/**
		 * C <i>R</i> dom(dom(prj1▷f)) ⊢ C <i>R</i> f
		 * <p>
		 * Where <i>R</i> denote one of these :{EQUAL, NOTEQUAL, LT, LE, GT, GE,
		 * IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}
		 * 
		 * @param rule
		 */
		public SimpDomDomRanresPrj1Right(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			final Expression domain1 = predicate.getRight();
			domainCondition(domain1);
			final Expression domain2 = ((UnaryExpression) domain1).getChild();
			domainCondition(domain2);
			final Expression ranres = ((UnaryExpression) domain2).getChild();
			ranresCondition(ranres);
			final BinaryExpression binranres = (BinaryExpression) ranres;
			if (binranres.getLeft().getTag() != KPRJ1_GEN ) {
				throw new IllegalArgumentException(
						"Cannot simplify this predicate.");
			}
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), binranres.getRight(), null);
		}

	}

	public static class ContBInter extends UnaryRule<RelationalPredicate> {

		/**
		 * E ⊆ A∩B∩C∩D ⊢[B, C]⊢ E ⊆ B∩C <br>
		 * E ⊂ A∩B∩C∩D ⊢[B, C]⊢ E ⊂ B∩C <br>
		 * x ∈ A∩B∩C∩D ⊢[B, C]⊢ x ∈ B∩C
		 * 
		 * @param rule
		 *            E ⊆ A∩B∩C∩D
		 * @param expressions
		 *            B, C
		 */
		public ContBInter(Rule<RelationalPredicate> rule,
				Expression... expressions) {
			super(rule, computeConsequent(rule, expressions));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule, Expression... expressions) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			inCondition(predicate);
			binterCondition(predicate.getRight(), expressions);
			if (expressions.length == 1) {
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), expressions[0], null);
			} else {
				final AssociativeExpression binter = ff
						.makeAssociativeExpression(BINTER, expressions, null);
				return ff.makeRelationalPredicate(predicate.getTag(),
						predicate.getLeft(), binter, null);
			}
		}

	}

	public static class ContSetminus extends UnaryRule<RelationalPredicate> {

		/**
		 * A ⊆ B∖C ⊢ A⊆B <br>
		 * A ⊂ B∖C ⊢ A⊂B <br>
		 * x ∈ B∖C ⊢ x∈B
		 * 
		 * @param rule
		 *            A ⊆ B∖C
		 */
		public ContSetminus(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			inCondition(predicate);
			final Expression right = predicate.getRight();
			setminusCondition(right);
			final Expression newExp = ((BinaryExpression) right).getLeft();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class ContRanres extends UnaryRule<RelationalPredicate> {

		/**
		 * f ⊆ g▷A ⊢ f⊆g <br>
		 * f ⊂ g▷A ⊢ f⊂g <br>
		 * x ∈ g▷A ⊢ x∈g
		 * 
		 * @param rule
		 *            f ⊆ g▷A
		 */
		public ContRanres(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			inCondition(predicate);
			final Expression right = predicate.getRight();
			ranresCondition(right);
			final Expression newExp = ((BinaryExpression) right).getLeft();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class ContRansub extends UnaryRule<RelationalPredicate> {

		/**
		 * f ⊆ g⩥A ⊢ f⊆g <br>
		 * f ⊂ g⩥A ⊢ f⊂g <br>
		 * x ∈ g⩥A ⊢ x∈g
		 * 
		 * @param rule
		 *            f ⊆ g⩥A
		 */
		public ContRansub(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			inCondition(predicate);
			final Expression right = predicate.getRight();
			ransubCondition(right);
			final Expression newExp = ((BinaryExpression) right).getLeft();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class ContDomres extends UnaryRule<RelationalPredicate> {

		/**
		 * f ⊆ A◁g ⊢ f⊆g <br>
		 * f ⊂ A◁g ⊢ f⊂g <br>
		 * x ∈ A◁g ⊢ x∈g
		 * 
		 * @param rule
		 *            f ⊆ A◁g
		 */
		public ContDomres(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			inCondition(predicate);
			final Expression right = predicate.getRight();
			domresCondition(right);
			final Expression newExp = ((BinaryExpression) right).getRight();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class ContDomsub extends UnaryRule<RelationalPredicate> {

		/**
		 * f ⊆ A⩤g ⊢ f⊆g <br>
		 * f ⊂ A⩤g ⊢ f⊂g <br>
		 * x ∈ A⩤g ⊢ x∈g
		 * 
		 * @param rule
		 *            f ⊆ A⩤g
		 */
		public ContDomsub(Rule<RelationalPredicate> rule) {
			super(rule, computeConsequent(rule));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			inCondition(predicate);
			final Expression right = predicate.getRight();
			domsubCondition(right);
			final Expression newExp = ((BinaryExpression) right).getRight();
			return ff.makeRelationalPredicate(predicate.getTag(),
					predicate.getLeft(), newExp, null);
		}

	}

	public static class InclSetext extends UnaryRule<RelationalPredicate> {

		/**
		 * {w, x, y, z} ⊆ A ⊢[x, y]⊢ {x, y} ⊆ A<br>
		 * {w, x, y, z} ⊂ A ⊢[x, y]⊢ {x, y} ⊂ A<br>
		 * {w, x, y, z} ⊆ A ⊢[x]⊢ x∈A
		 * 
		 * @param rule
		 *            {w, x, y, z} ⊆ A
		 * @param expressions
		 *            x,y
		 */
		public InclSetext(Rule<RelationalPredicate> rule,
				Expression... expressions) {
			super(rule, computeConsequent(rule, expressions));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule, Expression... expressions) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			subsetCondition(predicate);
			setextCondition(predicate.getLeft(), expressions);
			if (expressions.length == 1) {
				return ff.makeRelationalPredicate(IN, expressions[0],
						predicate.getRight(), null);
			} else {
				final SetExtension setext = ff.makeSetExtension(expressions,
						null);
				return ff.makeRelationalPredicate(predicate.getTag(), setext,
						predicate.getRight(), null);
			}
		}

	}

	public static class IncludBunion extends UnaryRule<RelationalPredicate> {

		/**
		 * A∪B∪C∪D ⊆ Z ⊢[B, C]⊢ B∪C ⊆ Z <br>
		 * A∪B∪C∪D ⊂ Z ⊢[B, C]⊢ B∪C ⊂ Z
		 * 
		 * @param rule
		 *            A∪B∪C∪D ⊆ Z
		 * @param expressions
		 *            B, C
		 */
		public IncludBunion(Rule<RelationalPredicate> rule,
				Expression... expressions) {
			super(rule, computeConsequent(rule, expressions));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule, Expression... expressions) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			subsetCondition(predicate);
			bunionCondition(predicate.getLeft(), expressions);
			if (expressions.length == 1) {
				return ff.makeRelationalPredicate(predicate.getTag(),
						expressions[0], predicate.getRight(), null);
			} else {
				final AssociativeExpression bunion = ff
						.makeAssociativeExpression(BUNION, expressions, null);
				return ff.makeRelationalPredicate(predicate.getTag(), bunion,
						predicate.getRight(), null);
			}
		}

	}

	public static class IncludOvr extends UnaryRule<RelationalPredicate> {

		/**
		 * efgh ⊆ k ⊢ efgh ⊆ k <br>
		 * efgh ⊆ k ⊢ fgh ⊆ k <br>
		 * efgh ⊆ k ⊢ gh ⊆ k <br>
		 * efgh ⊆ k ⊢ h ⊆ k <br>
		 * efgh ⊂ k ⊢ efgh ⊂ k <br>
		 * efgh ⊂ k ⊢ fgh ⊂ k <br>
		 * efgh ⊂ k ⊢ gh ⊂ k <br>
		 * efgh ⊂ k ⊢ h ⊂ k
		 * 
		 * @param rule
		 *            efgh ⊆ k
		 * @param expression
		 *            efgh <i>or</i> fgh <i>or</i> gh <i>or</i> h
		 */
		public IncludOvr(Rule<RelationalPredicate> rule, Expression expression) {
			super(rule, computeConsequent(rule, expression));
		}

		private static RelationalPredicate computeConsequent(
				Rule<RelationalPredicate> rule, Expression expression) {
			final RelationalPredicate predicate = rule.consequent;
			final FormulaFactory ff = rule.ff;
			subsetCondition(predicate);
			final Expression left = predicate.getLeft();
			ovrCondition(left);
			final AssociativeExpression ovr = (AssociativeExpression) left;
			final int ovrChildCount = ovr.getChildCount();
			if (expression.getTag() == OVR) {
				final AssociativeExpression myOvr = (AssociativeExpression) expression;
				final int myOvrChildCount = myOvr.getChildCount();
				if (myOvrChildCount > ovrChildCount) {
					throw new IllegalArgumentException("The given overriding ("
							+ myOvr.toString() + ") cannot be contained in"
							+ ovr.toString());
				}
				for (int i = 0; i < myOvrChildCount; i++) {
					final Expression ovrChild_i = ovr.getChild(ovrChildCount
							- 1 - i);
					final Expression myOvrChild_i = myOvr
							.getChild(myOvrChildCount - 1 - i);
					if (!ovrChild_i.equals(myOvrChild_i)) {
						throw new IllegalArgumentException(
								"Expression of the overriding ("
										+ ovrChild_i.toString()
										+ ") should be equal to"
										+ myOvrChild_i.toString());
					}
				}
			} else {
				final Expression lastChild = ovr.getChild(ovrChildCount - 1);
				if (!lastChild.equals(expression)) {
					throw new IllegalArgumentException(
							"The last expression of the overriding ("
									+ lastChild.toString()
									+ ") should be equal to "
									+ expression.toString());
				}
			}
			return ff.makeRelationalPredicate(predicate.getTag(), expression,
					predicate.getRight(), null);
		}

	}

	private static void bunionCondition(final Expression exp,
			Expression... expressions) {
		if (exp.getTag() != BUNION) {
			throw new IllegalArgumentException(exp.toString()
					+ " should denote a union.");
		}
		associativeCondition((AssociativeExpression) exp, expressions);
	}

	private static void setextCondition(final Expression exp,
			Expression... expressions) {
		if (exp.getTag() != SETEXT) {
			throw new IllegalArgumentException(exp.toString()
					+ " should denote a set in extension.");
		}
		if (expressions == null || expressions.length == 0) {
			throw new IllegalArgumentException(
					"There should be at least one given expression");
		}
		final SetExtension setext = (SetExtension) exp;
		for (Expression e : expressions) {
			boolean isContained = false;
			for (Expression member : setext.getMembers()) {
				if (member.equals(e)) {
					isContained = true;
					break;
				}
			}
			if (!isContained) {
				throw new IllegalArgumentException(expressions.toString()
						+ " should be contain in " + exp.toString());
			}
		}
	}

	private static void binterCondition(final Expression exp,
			Expression... expressions) {
		if (exp.getTag() != BINTER) {
			throw new IllegalArgumentException(exp.toString()
					+ " should denote an intersection.");
		}
		associativeCondition((AssociativeExpression) exp, expressions);
	}

	private static void associativeCondition(final AssociativeExpression exp,
			Expression... expressions) {
		if (expressions == null || expressions.length == 0) {
			throw new IllegalArgumentException(
					"There should be at least one given expression");
		}
		for (Expression e : expressions) {
			boolean isContained = false;
			for (Expression member : exp.getChildren()) {
				if (member.equals(e)) {
					isContained = true;
					break;
				}
			}
			if (!isContained) {
				throw new IllegalArgumentException(expressions.toString()
						+ " should be contain in " + exp.toString());
			}
		}
	}

	private static void subsetCondition(final Predicate predicate) {
		switch (predicate.getTag()) {
		case SUBSET:
		case SUBSETEQ:
			break;
		default:
			throw new IllegalArgumentException(predicate.toString()
					+ " should denote a subset (proper or not).");
		}
	}

	private static void inCondition(final Predicate predicate) {
		switch (predicate.getTag()) {
		case SUBSET:
		case SUBSETEQ:
		case IN:
			break;
		default:
			throw new IllegalArgumentException(
					predicate.toString()
							+ " should denote a subset (proper or not) or in particular case a membership.");
		}
	}

	private static void equalityCondition(final Expression firstExpression,
			final Expression secondExpression) {
		if (!firstExpression.equals(secondExpression)) {
			throw new IllegalArgumentException(firstExpression.toString()
					+ " should be equal to " + secondExpression.toString());
		}
	}

	private static void ovrCondition(final Expression expression) {
		if (expression.getTag() != OVR) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote an overriding.");
		}
	}

	private static void setminusCondition(final Expression expression) {
		if (expression.getTag() != SETMINUS) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a set difference.");
		}
	}

	private static void ranresCondition(final Expression expression) {
		if (expression.getTag() != RANRES) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a range restriction.");
		}
	}

	private static void domresCondition(final Expression expression) {
		if (expression.getTag() != DOMRES) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a domain restriction.");
		}
	}

	private static void ransubCondition(final Expression expression) {
		if (expression.getTag() != RANSUB) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a range substraction.");
		}
	}

	private static void domsubCondition(final Expression expression) {
		if (expression.getTag() != DOMSUB) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a domain substraction.");
		}
	}

	private static void cprodCondition(final Expression expression) {
		if (expression.getTag() != CPROD) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a cartesian product.");
		}
	}

	private static void mapstoCondition(final Expression expression) {
		if (expression.getTag() != MAPSTO) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a mapping");
		}
	}

	private static void converseCondition(final Expression expression) {
		if (expression.getTag() != CONVERSE) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a converse");
		}
	}

	private static void domainCondition(final Expression expression) {
		if (expression.getTag() != KDOM) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a domain");
		}
	}

	private static void rangeCondition(final Expression expression) {
		if (expression.getTag() != KRAN) {
			throw new IllegalArgumentException(expression.toString()
					+ " should denote a range");
		}
	}

}