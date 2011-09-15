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
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;

/**
 * Discharge a sequent such as :
 * 
 * <pre>
 * <code>H, x∈A, A⊆B ... C⊂D ⊢ x∈D</code> iff A⊆B⊂ ... ⊆ ... ⊂ ... ⊆C⊂D
 * </pre>
 * 
 * @author Emmanuel Billaud
 */
public class MembershipGoal extends HypothesesReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".mbGoal";

	public static boolean DEBUG = false;

	private static Expression member, set;
	private static Expression startPoint;
	private static Map<MyPosition, Rule<RelationalPredicate>> mapMember;
	private static boolean right;
	private static Predicate memberHyp;

	private static String errorMsg;

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final Predicate goal = seq.goal();
		final boolean preComputeGoal = preComputeGoal(goal);
		if (!preComputeGoal) {
			return ProverFactory.reasonerFailure(this, input, errorMsg);
		}
		final Set<RelationalPredicate> hyps = checkInput(input, seq);
		if (hyps == null) {
			return ProverFactory.reasonerFailure(this, input, errorMsg);
		}
		for (Entry<MyPosition, Rule<RelationalPredicate>> startingMap : mapMember
				.entrySet()) {
			final Map<Expression, PosRule> map = getExpressionsContaining(
					startPoint, startingMap.getKey(), startingMap.getValue());
			for (Entry<Expression, PosRule> containingMap : map.entrySet()) {
				final MyPosition myPos = containingMap.getValue().getPos();
				final Rule<RelationalPredicate> myRule = containingMap
						.getValue().getRule();
				final Rule<RelationalPredicate> link = findALink(hyps,
						containingMap.getKey(), myPos, myRule);
				if (link != null) {
					assert link.consequent.equals(goal);
					Set<Predicate> neededHyps = new HashSet<Predicate>();
					neededHyps.addAll(hyps);
					neededHyps.add(memberHyp);
					return ProverFactory.makeProofRule(this, input, goal,
							neededHyps, "Membership in Goal",
							new IAntecedent[0]);
				}

			}
		}
		return ProverFactory.reasonerFailure(this, input,
				"No path can be found.");
	}

	/**
	 * Test if the predicate <code>goal</code> denotes a membership. If so, the
	 * attribute <code>member</code> is set equal to the left expression of
	 * <code>goal</code>, and the attribute <code>set</code> is set equal to the
	 * right expression <code>goal</code> and it returns <code>true</code>.
	 * <p>
	 * Else it returns <code>false</code> and attribute <code>errorMsg</code> is
	 * changed.
	 * 
	 * @param goal
	 *            the considered predicate.
	 * @return <code>true</code> iff <code>goal</code> denotes a membership,
	 *         <code>false</code> else.
	 */
	private static boolean preComputeGoal(Predicate goal) {
		if (!Lib.isInclusion(goal)) {
			errorMsg = "Goal does not denote a membership.";
			return false;
		}
		set = ((RelationalPredicate) goal).getRight();
		member = ((RelationalPredicate) goal).getLeft();
		return true;
	}

	/**
	 * Check that :
	 * <ul>
	 * <li><code>input</code> is a HypothesesReasoner.</li>
	 * <li>all the predciates contained in <code>input</code> belongs to the
	 * sequent <code>sequent</code></li>
	 * <li>Among those predicates, only one give informations about the goal's
	 * member</li>
	 * <li>all the other one denotes either a subset, or a proper subset or an
	 * equality</li>
	 * </ul>
	 * 
	 * @param input
	 *            the input to check
	 * @param sequent
	 *            the sequent of the considered node
	 * @return a set of the predicate contained in <code>input</code> if all the
	 *         previous check succeed, null else. In the latter case, the global
	 *         attirbute <code>errorMsg</code> is modified consequently to the
	 *         nature of the error.
	 */
	private static Set<RelationalPredicate> checkInput(IReasonerInput input,
			IProverSequent sequent) {
		Set<RelationalPredicate> set = new HashSet<RelationalPredicate>();
		if (!(input instanceof HypothesesReasoner.Input)) {
			errorMsg = "The input must be a HypothesesReasoner Input.";
			return null;
		}
		final HypothesesReasoner.Input hInput = (HypothesesReasoner.Input) input;
		final Predicate[] preds = hInput.getPred();
		boolean ambiguity = false;
		for (Predicate pred : preds) {
			if (!sequent.containsHypothesis(pred)) {
				errorMsg = "The input must contain only hypotheses.";
				return null;
			}
			final Map<MyPosition, Rule<RelationalPredicate>> computedMapMember = isMemberHyp(
					pred, sequent.getFormulaFactory());
			if (computedMapMember == null) {
				errorMsg = "Given hypotheses must denote either an equality, or a subset or a membership.";
				return null;
			}
			if (computedMapMember.isEmpty()) {
				set.add((RelationalPredicate) pred);
				continue;
			}
			if (!ambiguity) {
				ambiguity = true;
				memberHyp = pred;
				mapMember = computedMapMember;
				if (right) {
					startPoint = ((RelationalPredicate) pred).getRight();
				} else {
					startPoint = ((RelationalPredicate) pred).getLeft();
				}
			} else {
				errorMsg = "There should be only one hypothesis giving information about the goal's member.";
				return null;
			}
		}
		return set;
	}

	/**
	 * Tests if the predicate <code>hyp</code> gives any information about the
	 * goal's member.
	 * 
	 * @param hyp
	 *            the tested predicate
	 * @param ff
	 * @return a map giving the position of the goal's member as well as the
	 *         corresponding rule if <code>hyp</code> gives any information
	 *         about the goal's member, null, else.<br>
	 *         Example given : {x↦y, z↦x}⊆A (let's suppose that x is the goal's
	 *         member: x∈S) will return : {[kdom]↦(x∈dom(A) ; [kran]↦(x∈ran(A)}.
	 */
	private static Map<MyPosition, Rule<RelationalPredicate>> isMemberHyp(
			Predicate hyp, FormulaFactory ff) {
		if (!(hyp instanceof RelationalPredicate)) {
			return null;
		}
		final RelationalPredicate rHyp = (RelationalPredicate) hyp;
		final Rule.Hypothesis<RelationalPredicate> hypRule = new Rule.Hypothesis<RelationalPredicate>(
				rHyp, ff);
		final Expression leftExp = rHyp.getLeft();
		final Expression rightExp = rHyp.getRight();
		switch (hyp.getTag()) {
		case EQUAL:
			final Map<MyPosition, Rule<RelationalPredicate>> setRightEQUAL = memberGoalPosition(
					rightExp, member, new MyPosition(), new Rule.EqualRight(
							hypRule));
			if (!setRightEQUAL.isEmpty()) {
				right = false;
				return setRightEQUAL;
			}
			right = true;
			return memberGoalPosition(leftExp, member, new MyPosition(),
					new Rule.EqualLeft(hypRule));
		case IN:
		case SUBSET:
		case SUBSETEQ:
			right = true;
			return memberGoalPosition(leftExp, member, new MyPosition(),
					hypRule);
		default:
			return null;
		}
	}

	private static Rule<RelationalPredicate> findALink(
			Set<RelationalPredicate> givenHyps, Expression toMatch,
			MyPosition position, Rule<RelationalPredicate> previousRule) {

		try {
			final Rule<RelationalPredicate> containedInGoal = isContainedInGoal(
					toMatch, position, previousRule);
			if (containedInGoal != null) {
				return containedInGoal;
			}
		} catch (IllegalArgumentException iae) {
			// If an IllegalArgumentException is raised, there are two
			// possibilities :
			// - either the tactic is able to find path that the rules cannot
			// prove (e.g.: dom(f∪g)⊆dom(f)∪dom(g))
			// - or that the reasoner try to do incorrect inference.
			if (DEBUG) {
				iae.printStackTrace();
			}
		}
		for (RelationalPredicate hyp : givenHyps) {
			final Rule<RelationalPredicate> hypRule = new Rule.Hypothesis<RelationalPredicate>(
					hyp, previousRule.ff);
			final Expression left = hyp.getLeft();
			final Expression right = hyp.getRight();
			Expression expression;
			PosRule posRule = null;
			switch (hyp.getTag()) {
			case SUBSET:
			case SUBSETEQ:
				try {
					posRule = getPositionInExp(left, toMatch, position, hypRule);
				} catch (IllegalArgumentException iae) {
					if (DEBUG) {
						iae.printStackTrace();
					}
					continue;
				}
				if (posRule == null) {
					continue;
				}
				expression = right;
				break;
			case EQUAL:
				final Rule.EqualLeft equalLeft = new Rule.EqualLeft(hypRule);
				try {
					posRule = getPositionInExp(left, toMatch, position,
							equalLeft);
				} catch (IllegalArgumentException iae) {
					if (DEBUG) {
						iae.printStackTrace();
					}
					continue;
				}
				if (posRule != null) {
					expression = right;
					break;
				}
				final Rule.EqualRight equalRight = new Rule.EqualRight(hypRule);
				try {
					posRule = getPositionInExp(right, toMatch, position,
							equalRight);
				} catch (IllegalArgumentException iae) {
					if (DEBUG) {
						iae.printStackTrace();
					}
					continue;
				}
				if (posRule != null) {
					expression = left;
					break;
				}
			default:
				continue;
			}

			Set<RelationalPredicate> restrictedSetRPred = new HashSet<RelationalPredicate>(
					givenHyps);
			restrictedSetRPred.remove(hyp);
			final Map<Expression, PosRule> mapEqExp = getExpressionsContaining(
					expression, posRule.getPos(), posRule.getRule());
			for (Entry<Expression, PosRule> entryEqExp : mapEqExp.entrySet()) {
				final MyPosition myPos = entryEqExp.getValue().getPos();
				final Rule<RelationalPredicate> myRule = entryEqExp.getValue()
						.getRule();
				try {
					Rule<RelationalPredicate> computed;
					if (myRule.isMeaningless()) {
						computed = previousRule;
					} else {
						computed = new Rule.Composition(previousRule, myRule);
					}
					final Rule<RelationalPredicate> link = findALink(
							restrictedSetRPred, entryEqExp.getKey(), myPos,
							computed);
					if (link != null) {
						return link;
					}
				} catch (IllegalArgumentException iae) {
					if (DEBUG) {
						iae.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	private static Rule<RelationalPredicate> isContainedInGoal(
			Expression expression, MyPosition position,
			Rule<RelationalPredicate> rule) {
		final PosRule posRule = getPositionInExp(set, expression, position,
				new Rule.Expr(set, rule.ff));
		if (posRule == null) {
			return null;
		}
		final Rule<RelationalPredicate> myRule = posRule.getRule();
		if (!posRule.getPos().isRoot()) {
			return null;
		}
		if (myRule.isMeaningless()) {
			return rule;
		}
		return new Rule.Composition(rule, myRule);
	}

	private static Rule<RelationalPredicate> finalizeRule(MyPosition position,
			Rule<RelationalPredicate> rule) {
		if (position.isRoot()) {
			return rule;
		}
		final int firstChild = position.getFirstChild();
		final MyPosition children = position.removeFirstChild();
		switch (firstChild) {
		case CONVERSE:
			return finalizeRule(children, new Rule.Converse(rule));
		case KRAN:
			return finalizeRule(children, new Rule.Range(rule));
		case KDOM:
			return finalizeRule(children, new Rule.Domain(rule));
		default:
			return null;
		}
	}

	private static PosRule getPositionInExp(final Expression tested,
			Expression toMatch, MyPosition refPos,
			Rule<RelationalPredicate> rule) {
		final PosRule contains = contains(tested, toMatch, new MyPosition(),
				rule, refPos);
		if (contains != null) {
			final MyPosition pos = contains.getPos();
			final MyPosition result = refPos.sub(pos);
			if (result != null) {
				return new PosRule(result, finalizeRule(result,
						contains.getRule()));
			}
			if (pos.getLastChild() == CONVERSE) {
				final MyPosition children = pos.removeLastChild();
				final MyPosition resultBis = refPos.sub(children);
				if (resultBis == null) {
					return null;
				}
				final MyPosition resultConv = resultBis
						.addChildStartNorm(CONVERSE);
				return new PosRule(resultConv, finalizeRule(resultConv,
						contains.getRule()));
			}
		}
		return null;
	}

	private static Map<MyPosition, Rule<RelationalPredicate>> memberGoalPosition(
			final Expression tested, Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule) {
		final HashMap<MyPosition, Rule<RelationalPredicate>> map = new HashMap<MyPosition, Rule<RelationalPredicate>>();
		final PosRule contains = contains(tested, toMatch, new MyPosition(),
				rule, position);
		if (contains != null) {
			final MyPosition result = position.sub(contains.getPos());
			if (result == null) {
				return map;
			}
			map.put(result, finalizeRule(result, contains.getRule()));
			return map;
		}
		switch (tested.getTag()) {
		case MAPSTO:
			final BinaryExpression testedMAPSTO = (BinaryExpression) tested;
			final Expression leftMAPSTO = testedMAPSTO.getLeft();
			final Expression rightMAPSTO = testedMAPSTO.getRight();
			final MyPosition posLeftMAPSTO = position.addChildEndNorm(KDOM);
			map.putAll(memberGoalPosition(leftMAPSTO, toMatch, posLeftMAPSTO,
					rule));
			final MyPosition posRightMAPSTO = position.addChildEndNorm(KRAN);
			map.putAll(memberGoalPosition(rightMAPSTO, toMatch, posRightMAPSTO,
					rule));
			return map;
		case OVR:
			final AssociativeExpression assoTested = (AssociativeExpression) tested;
			final Expression last = assoTested.getChild(assoTested
					.getChildCount() - 1);
			map.putAll(memberGoalPosition(last, toMatch, position,
					new Rule.IncludOvr(rule, last)));
			return map;
		case SETEXT:
			final Expression[] members = ((SetExtension) tested).getMembers();
			for (Expression e : members) {
				map.putAll(memberGoalPosition(e, toMatch, position,
						new Rule.InclSetext(rule, e)));
			}
			return map;
		}
		return map;
	}

	/**
	 * Tells whether <code>toMatch</code> is contained in <code>tested</code>.
	 * {@link #getExpressionsContaining(Expression, Rule)}
	 * 
	 * @param tested
	 *            the expression tested to tells whether it contains
	 *            <code>toMatch</code>.
	 * @param toMatch
	 *            the expression we are searching for in <code>tested</code>.
	 * @param toMatchPos
	 *            the current position we are searching for.
	 * @param rule
	 * 
	 * @param refPos
	 * @return a position mapped to a rule.
	 */
	private static PosRule contains(Expression tested, Expression toMatch,
			MyPosition toMatchPos, Rule<RelationalPredicate> rule,
			MyPosition refPos) {
		if (tested.equals(toMatch)) {
			return new PosRule(toMatchPos, rule);
		}
		switch (tested.getTag()) {
		case OVR: // (j ⊆ ij) ∧ (i ⊆ hij can be false) ∧ (kj ⊆ hij if k⊆i)
			return containsOVR(tested, toMatch, toMatchPos, rule, refPos);
		case BUNION: // (A ⊆ A∪B∪C) ∧ (B ⊆ A∪B∪C) ∧ (C ⊆ A∪B∪C) ∧ (A∪C ⊆ A∪B∪C)
			return containsBUNION(tested, toMatch, toMatchPos, rule, refPos);
		case CPROD: // (A⊆B ∧ C⊆D) ⇒ (A×C ⊆ B×D)
			return containsCPROD(tested, toMatch, toMatchPos, rule, refPos);
		case BINTER: // A∩B∩C∩D ⊆ B∩D
			// But A∩B∩C∩D ⊆ B is not treated by this method since it is treated
			// by getting an expression containing A∩B∩C∩D (here B).
			// {@link #getExpressionsContaining(Expression, Rule)}
			return containsBINTER(tested, toMatch, toMatchPos, rule, refPos);
		case SETMINUS: // f∖h ⊆ g∖k if f⊆g ∧ h⊇k
			return containsSETMINUS(tested, toMatch, toMatchPos, rule, refPos);
		case RANRES: // f▷A ⊆ g▷B if f⊆g ∧ A⊆B
			return containsRANRES(tested, toMatch, toMatchPos, rule, refPos);
		case DOMRES: // A◁f ⊆ B◁g if A⊆B ∧ f⊆g
			return containsDOMRES(tested, toMatch, toMatchPos, rule, refPos);
		case RANSUB: // f⩥A ⊆ g⩥B if f⊆g ∧ A⊇B
			return containsRANSUB(tested, toMatch, toMatchPos, rule, refPos);
		case DOMSUB: // A⩤f ⊆ B⩤g if A⊇B ∧ f⊆g
			return containsDOMSUB(tested, toMatch, toMatchPos, rule, refPos);
		case KDOM:
			final Expression childKDOM = ((UnaryExpression) tested).getChild();
			final Rule.Expr kdom = new Rule.Expr(childKDOM, rule.ff);
			final PosRule containsKDOM = contains(childKDOM, toMatch,
					toMatchPos.addChildStartNorm(KDOM), kdom, refPos);
			if (containsKDOM == null) {
				return null;
			}
			if (containsKDOM.getRule().isMeaningless()) {
				return new PosRule(containsKDOM.getPos(), rule);
			}
			final Rule.Domain domain = new Rule.Domain(containsKDOM.getRule());
			final Rule.Composition compKDOM = new Rule.Composition(domain, rule);
			return new PosRule(containsKDOM.getPos(), compKDOM);
		case KRAN:
			final Expression childKRAN = ((UnaryExpression) tested).getChild();
			final Rule.Expr kran = new Rule.Expr(childKRAN, rule.ff);
			final PosRule containsKRAN = contains(childKRAN, toMatch,
					toMatchPos.addChildStartNorm(KRAN), kran, refPos);
			if (containsKRAN == null) {
				return null;
			}
			if (containsKRAN.getRule().isMeaningless()) {
				return new PosRule(containsKRAN.getPos(), rule);
			}
			final Rule.Range range = new Rule.Range(containsKRAN.getRule());
			final Rule.Composition compKRAN = new Rule.Composition(range, rule);
			return new PosRule(containsKRAN.getPos(), compKRAN);
		case CONVERSE:
			final Expression childCONV = ((UnaryExpression) tested).getChild();
			final Rule.Expr conv = new Rule.Expr(childCONV, rule.ff);
			final PosRule containsCONV = contains(childCONV, toMatch,
					toMatchPos.addChildStartNorm(CONVERSE), conv, refPos);
			if (containsCONV == null) {
				return null;
			}
			if (containsCONV.getRule().isMeaningless()) {
				return new PosRule(containsCONV.getPos(), rule);
			}
			final Rule.Converse converse = new Rule.Converse(
					containsCONV.getRule());
			final Rule.Composition compCONV = new Rule.Composition(converse,
					rule);
			return new PosRule(containsCONV.getPos(), compCONV);
		}
		return null;
	}

	/**
	 * 
	 * @param tested
	 * @param toMatch
	 * @param ff
	 * @return
	 */
	private static Rule<RelationalPredicate> extendedContains(
			final Expression tested, final Expression toMatch, FormulaFactory ff) {
		final Map<Expression, Rule<RelationalPredicate>> map = getExpressionsContaining(
				toMatch, ff);
		for (Entry<Expression, Rule<RelationalPredicate>> entry : map
				.entrySet()) {
			new Rule.Expr(tested, ff);
			final PosRule contains = contains(tested, entry.getKey(),
					new MyPosition(), new Rule.Expr(tested, ff),
					new MyPosition());
			if (contains == null || !contains.getPos().isRoot()) {
				continue;
			}
			final Rule<RelationalPredicate> entryRule = entry.getValue();
			final Rule<RelationalPredicate> containsRule = contains.getRule();
			if (entryRule.isMeaningless()) {
				return containsRule;
			}
			if (containsRule.isMeaningless()) {
				return entryRule;
			}
			return new Rule.Composition(entryRule, containsRule);
		}
		return null;
	}

	private static PosRule containsDOMSUB(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		if (toMatch.getTag() != DOMSUB) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		Rule<RelationalPredicate> result = rule;
		final Rule<RelationalPredicate> extendedContainsRight = extendedContains(
				binTested.getRight(), binToMatch.getRight(), rule.ff);
		if (extendedContainsRight == null) {
			return null;
		}
		if (!extendedContainsRight.isMeaningless()) {
			result = new Rule.CompositionDomsubRightIncl(extendedContainsRight,
					result);
		}
		final Rule<RelationalPredicate> extendedContainsLeft = extendedContains(
				binToMatch.getLeft(), binTested.getLeft(), rule.ff);
		if (extendedContainsLeft == null) {
			return null;
		}
		if (!extendedContainsLeft.isMeaningless()) {
			result = new Rule.CompositionDomsubLeftIncl(result,
					extendedContainsLeft);
		}
		return new PosRule(position, result);
	}

	private static PosRule containsRANSUB(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		if (toMatch.getTag() != RANSUB) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Rule<RelationalPredicate> extendedContainsLeft = extendedContains(
				binTested.getLeft(), binToMatch.getLeft(), rule.ff);
		if (extendedContainsLeft == null) {
			return null;
		}
		Rule<RelationalPredicate> result = rule;
		if (!extendedContainsLeft.isMeaningless()) {
			result = new Rule.CompositionRansubLeftIncl(extendedContainsLeft,
					result);
		}
		final Rule<RelationalPredicate> extendedContainsRight = extendedContains(
				binToMatch.getRight(), binTested.getRight(), rule.ff);
		if (extendedContainsRight == null) {
			return null;
		}
		if (!extendedContainsRight.isMeaningless()) {
			result = new Rule.CompositionRansubRightIncl(result,
					extendedContainsRight);
		}
		return new PosRule(position, result);
	}

	private static PosRule containsDOMRES(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		if (toMatch.getTag() != DOMRES) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Rule<RelationalPredicate> extendedContainsLeft = extendedContains(
				binTested.getLeft(), binToMatch.getLeft(), rule.ff);
		if (extendedContainsLeft == null) {
			return null;
		}
		Rule<RelationalPredicate> result = rule;
		if (!extendedContainsLeft.isMeaningless()) {
			result = new Rule.CompositionDomresLeftCont(result,
					extendedContainsLeft);
		}
		final Rule<RelationalPredicate> extendedContainsRight = extendedContains(
				binTested.getRight(), binToMatch.getRight(), rule.ff);
		if (extendedContainsRight == null) {
			return null;
		}
		if (!extendedContainsRight.isMeaningless()) {
			result = new Rule.CompositionDomresRightCont(result,
					extendedContainsRight);
		}
		return new PosRule(position, result);
	}

	private static PosRule containsRANRES(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		if (toMatch.getTag() != RANRES) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Rule<RelationalPredicate> extendedContainsLeft = extendedContains(
				binTested.getLeft(), binToMatch.getLeft(), rule.ff);
		if (extendedContainsLeft == null) {
			return null;
		}
		Rule<RelationalPredicate> result = rule;
		if (!extendedContainsLeft.isMeaningless()) {
			result = new Rule.CompositionRanresLeftCont(result,
					extendedContainsLeft);
		}
		final Rule<RelationalPredicate> extendedContainsRight = extendedContains(
				binTested.getRight(), binToMatch.getRight(), rule.ff);
		if (extendedContainsRight == null) {
			return null;
		}
		if (!extendedContainsRight.isMeaningless()) {
			result = new Rule.CompositionRanresRightCont(result,
					extendedContainsRight);
		}
		return new PosRule(position, result);
	}

	private static PosRule containsSETMINUS(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		if (toMatch.getTag() != SETMINUS) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Rule<RelationalPredicate> extendedContainsLeft = extendedContains(
				binTested.getLeft(), binToMatch.getLeft(), rule.ff);
		if (extendedContainsLeft == null) {
			return null;
		}
		Rule<RelationalPredicate> result = rule;
		if (!extendedContainsLeft.isMeaningless()) {
			result = new Rule.CompositionSetminusLeftIncl(extendedContainsLeft,
					result);
		}
		final Rule<RelationalPredicate> extendedContainsRight = extendedContains(
				binToMatch.getRight(), binTested.getRight(), rule.ff);
		if (extendedContainsRight.isMeaningless()) {
			return new PosRule(position, result);
		}
		return new PosRule(position, new Rule.CompositionSetminusRightIncl(
				result, extendedContainsRight));
	}

	private static PosRule containsBINTER(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		if (toMatch.getTag() != BINTER) {
			return null;
		}
		final Expression[] children = ((AssociativeExpression) tested)
				.getChildren();
		final Expression[] toMatches = ((AssociativeExpression) toMatch)
				.getChildren();
		Rule<RelationalPredicate> toMatchRule = new Rule.Expr(toMatch, rule.ff);
		for (Expression testedExp : children) {
			boolean isContained = false;
			for (Expression toMatchExp : toMatches) {
				final Rule<RelationalPredicate> extendedContains = extendedContains(
						testedExp, toMatchExp, rule.ff);
				if (extendedContains == null) {
					continue;
				}
				if (!extendedContains.isMeaningless()) {
					toMatchRule = new Rule.CompositionBinterCont(toMatchRule,
							extendedContains);
				}
				isContained = true;
				break;
			}
			if (!isContained) {
				return null;
			}
		}
		final Rule.ContBInter contBInter = new Rule.ContBInter(toMatchRule,
				children);
		if (contBInter.isMeaningless()) {
			return new PosRule(position, rule);
		}
		return new PosRule(position, new Rule.Composition(contBInter, rule));
	}

	private static PosRule containsCPROD(Expression tested, Expression toMatch,
			MyPosition position, Rule<RelationalPredicate> rule,
			MyPosition refPosition) {
		if (toMatch.getTag() != CPROD) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Rule<RelationalPredicate> extendedContainsLeft = extendedContains(
				binTested.getLeft(), binToMatch.getLeft(), rule.ff);
		if (extendedContainsLeft == null) {
			return null;
		}
		Rule<RelationalPredicate> result = rule;
		if (!extendedContainsLeft.isMeaningless()) {
			result = new Rule.CompositionCProdLeftIncl(extendedContainsLeft,
					result);
		}
		final Rule<RelationalPredicate> extendedContainsRight = extendedContains(
				binTested.getRight(), binToMatch.getRight(), rule.ff);
		if (extendedContainsRight == null) {
			return null;
		}
		if (!extendedContainsRight.isMeaningless()) {
			result = new Rule.CompositionCProdRightIncl(extendedContainsRight,
					result);
		}
		return new PosRule(position, result);
	}

	private static PosRule containsOVR(Expression tested, Expression toMatch,
			MyPosition position, Rule<RelationalPredicate> rule,
			MyPosition refPosition) {
		final AssociativeExpression assoTested = (AssociativeExpression) tested;
		if (toMatch.getTag() == OVR) {
			final Expression[] toMatches = ((AssociativeExpression) toMatch)
					.getChildren();
			final Expression[] testedChildren = assoTested.getChildren();
			final Expression[] testedChildRes = new Expression[toMatches.length];
			System.arraycopy(testedChildren, testedChildren.length
					- toMatches.length, testedChildRes, 0, toMatches.length);
			final Rule<RelationalPredicate> extendedContains = extendedContains(
					testedChildRes[0], toMatches[0], rule.ff);
			if (extendedContains == null) {
				return null;
			}
			for (int i = 1; i < toMatches.length; i++) {
				if (!toMatches[i].equals(testedChildren[testedChildren.length
						- toMatches.length + i])) {
					return null;
				}
			}
			final AssociativeExpression childRes = rule.ff
					.makeAssociativeExpression(OVR, testedChildRes, null);
			Rule<RelationalPredicate> result = rule;
			if (testedChildren.length != testedChildRes.length) {
				result = new Rule.IncludOvr(rule, childRes);
			}
			if (!extendedContains.isMeaningless()) {
				result = new Rule.CompositionOvrIncl(extendedContains, result);
			}
			return new PosRule(position, result);
		} else {
			final Expression last = assoTested.getChild(assoTested
					.getChildCount() - 1);
			final Rule.Expr expr = new Rule.Expr(last, rule.ff);
			final PosRule contains = contains(last, toMatch, position, expr,
					refPosition);
			if (contains != null) {
				final Rule.IncludOvr includOvr = new Rule.IncludOvr(rule, last);
				if (contains.getRule().isMeaningless()) {
					return new PosRule(contains.getPos(), includOvr);
				} else {
					final Rule.Composition composition = new Rule.Composition(
							contains.getRule(), includOvr);
					return new PosRule(contains.getPos(), composition);
				}
			}
		}
		return null;
	}

	private static PosRule containsBUNION(Expression tested,
			Expression toMatch, MyPosition position,
			Rule<RelationalPredicate> rule, MyPosition refPosition) {
		Rule<RelationalPredicate> result = rule;
		final Expression[] children = ((AssociativeExpression) tested)
				.getChildren();
		if (toMatch.getTag() == BUNION) {
			final Expression[] toMatches = ((AssociativeExpression) toMatch)
					.getChildren();
			for (Expression e : toMatches) {
				boolean isContained = false;
				for (Expression member : children) {
					final Rule<RelationalPredicate> extendedContains = extendedContains(
							member, e, rule.ff);
					if (extendedContains == null) {
						continue;
					}
					if (!extendedContains.isMeaningless()) {
						result = new Rule.CompositionBunionIncl(
								extendedContains, result);
					}
					isContained = true;
					break;
				}
				if (!isContained) {
					return null;
				}
			}
			return new PosRule(position, new Rule.IncludBunion(result,
					toMatches));
		} else {
			for (Expression e : children) {
				final Rule.Expr expr = new Rule.Expr(e, rule.ff);
				final PosRule contains = contains(e, toMatch, position, expr,
						refPosition);
				if (contains != null) {
					if (refPosition.sub(contains.getPos()) == null) {
						continue;
					}
					final Rule.IncludBunion includBunion = new Rule.IncludBunion(
							rule, e);
					if (contains.getRule().isMeaningless()) {
						return new PosRule(contains.getPos(), includBunion);
					}
					return new PosRule(contains.getPos(), new Rule.Composition(
							includBunion, contains.getRule()));
				}
			}
		}
		return null;
	}

	private static Map<Expression, PosRule> getExpressionsContaining(
			Expression expression, MyPosition position,
			Rule<RelationalPredicate> rule) {
		Map<Expression, PosRule> map = new HashMap<Expression, PosRule>();
		for (Entry<Expression, Rule<RelationalPredicate>> expCont : getExpressionsContaining(
				expression, rule.ff).entrySet()) {
			Rule<RelationalPredicate> computedRule;
			if (expCont.getValue().isMeaningless()) {
				computedRule = rule;
			} else {
				computedRule = new Rule.Composition(rule, finalizeRule(
						position, expCont.getValue()));
			}
			final Expression expContKey = expCont.getKey();
			final Map<Expression, PosRule> mapEqExp = getEquivalentExp(
					expContKey, position, computedRule);
			for (Entry<Expression, PosRule> eqExp : mapEqExp.entrySet()) {
				final Expression eqExpKey = eqExp.getKey();
				final PosRule eqExpValue = eqExp.getValue();
				final MyPosition eqExpPos = eqExpValue.getPos();
				final Rule<RelationalPredicate> eqExpRule = eqExpValue
						.getRule();
				if (eqExpKey.equals(expContKey) && position.equals(eqExpPos)
						&& eqExpRule.equals(computedRule)) {
					map.put(eqExpKey, eqExpValue);
				} else {
					map.putAll(getExpressionsContaining(eqExpKey, eqExpPos,
							eqExpRule));
				}
			}
		}
		return map;
	}

	private static Map<Expression, Rule<RelationalPredicate>> getExpressionsContaining(
			Expression contained, FormulaFactory ff) {
		return getExpressionsContaining(contained, new Rule.Expr(contained, ff));
	}

	private static Map<Expression, Rule<RelationalPredicate>> getExpressionsContaining(
			Expression contained, Rule<RelationalPredicate> rule) {
		Map<Expression, Rule<RelationalPredicate>> map = new HashMap<Expression, Rule<RelationalPredicate>>();
		map.put(contained, rule);
		switch (contained.getTag()) {
		case BINTER: // f∩g ⊆ f
			AssociativeExpression binter = (AssociativeExpression) contained;
			for (Expression expression : binter.getChildren()) {
				map.putAll(getExpressionsContaining(expression,
						new Rule.ContBInter(rule, expression)));
			}
			break;
		case SETMINUS: // f∖g ⊆ f
			Expression leftSetminus = ((BinaryExpression) contained).getLeft();
			map.putAll(getExpressionsContaining(leftSetminus,
					new Rule.ContSetminus(rule)));
			break;
		case RANRES: // f▷A ⊆ f
			Expression leftRanres = ((BinaryExpression) contained).getLeft();
			map.putAll(getExpressionsContaining(leftRanres,
					new Rule.ContRanres(rule)));
			break;
		case RANSUB: // f⩥A ⊆ f
			Expression leftransub = ((BinaryExpression) contained).getLeft();
			map.putAll(getExpressionsContaining(leftransub,
					new Rule.ContRansub(rule)));
			break;
		case DOMRES: // A◁f ⊆ f
			Expression rightDomres = ((BinaryExpression) contained).getRight();
			map.putAll(getExpressionsContaining(rightDomres,
					new Rule.ContDomres(rule)));
			break;
		case DOMSUB: // A⩤f ⊆ f
			Expression rightDomsub = ((BinaryExpression) contained).getRight();
			map.putAll(getExpressionsContaining(rightDomsub,
					new Rule.ContDomsub(rule)));
			break;
		case CPROD: // (A∩B)×(C∩D) = (A×C)∩(A×D)∩(B×C)∩(B×D)
			final Expression leftCPROD = ((BinaryExpression) contained)
					.getLeft();
			final Rule.Expr exprLCP = new Rule.Expr(leftCPROD, rule.ff);
			final Map<Expression, Rule<RelationalPredicate>> exprContLCP = getExpressionsContaining(
					leftCPROD, exprLCP);
			final Expression rightCPROD = ((BinaryExpression) contained)
					.getRight();
			final Rule.Expr exprRCP = new Rule.Expr(rightCPROD, rule.ff);
			final Map<Expression, Rule<RelationalPredicate>> exprContRCP = getExpressionsContaining(
					rightCPROD, exprRCP);
			for (Entry<Expression, Rule<RelationalPredicate>> entry_left : exprContLCP
					.entrySet()) {
				final Rule.CompositionCProdLeftCont compCPLCont = new Rule.CompositionCProdLeftCont(
						rule, entry_left.getValue());
				final Expression leftExp = entry_left.getKey();
				for (Entry<Expression, Rule<RelationalPredicate>> entry_right : exprContRCP
						.entrySet()) {
					final Rule.CompositionCProdRightCont resultRule = new Rule.CompositionCProdRightCont(
							entry_right.getValue(), compCPLCont);
					final Expression rightExp = entry_right.getKey();
					final BinaryExpression resultExp = rule.ff
							.makeBinaryExpression(CPROD, leftExp, rightExp,
									null);
					map.put(resultExp, resultRule);
				}
			}

		}
		return map;
	}

	/*
	 * TODO finir d'écrire toutes les équivalences possibles.
	 */
	private static Map<Expression, PosRule> getEquivalentExp(
			Expression expression, MyPosition position,
			Rule<RelationalPredicate> rule) {
		Map<Expression, PosRule> map = new HashMap<Expression, PosRule>();
		final Expression child = (expression instanceof UnaryExpression) ? ((UnaryExpression) expression)
				.getChild() : null;
		final BinaryExpression binExp = (expression instanceof BinaryExpression) ? (BinaryExpression) expression
				: null;
		final Rule.Expr expr = new Rule.Expr(expression, rule.ff);
		final MyPosition children = position.removeFirstChild();
		final Rule.Domain domain = new Rule.Domain(expr);
		final Rule.Range range = new Rule.Range(expr);
		final Rule.Converse converse = new Rule.Converse(expr);
		switch (expression.getTag()) {
		case CPROD:
			map.put(expression, new PosRule(position, rule));
			final Expression biExpCLeft = binExp.getLeft();
			final Expression biExpCRight = binExp.getRight();
			switch (position.getFirstChild()) {
			case CONVERSE:
				final BinaryExpression cprod = rule.ff.makeBinaryExpression(
						CPROD, biExpCRight, biExpCLeft, null);
				final Rule.SimpConvCProdRight sccpr = new Rule.SimpConvCProdRight(
						converse);
				final Rule<RelationalPredicate> finalizedCONV = finalizeRule(
						children, sccpr);
				final Rule.Composition compCONV = new Rule.Composition(rule,
						finalizedCONV);
				final Map<Expression, PosRule> eqExpCONV = getEquivalentExp(
						cprod, children, compCONV);
				map.putAll(eqExpCONV);
				break;
			case KDOM:
				final Rule.SimpDomCProdRight sdcpr = new Rule.SimpDomCProdRight(
						new Rule.Domain(expr));
				final Rule<RelationalPredicate> finalizedKDOM = finalizeRule(
						children, sdcpr);
				final Rule.Composition compKDOM = new Rule.Composition(rule,
						finalizedKDOM);
				final Map<Expression, PosRule> eqExpKDOM = getEquivalentExp(
						biExpCLeft, children, compKDOM);
				map.putAll(eqExpKDOM);
				break;
			case KRAN:
				final Rule.SimpRanCProdRight srcpr = new Rule.SimpRanCProdRight(
						new Rule.Range(expr));
				final Rule<RelationalPredicate> finalizedKRAN = finalizeRule(
						children, srcpr);
				final Rule.Composition compKRAN = new Rule.Composition(rule,
						finalizedKRAN);
				final Map<Expression, PosRule> eqExpKRAN = getEquivalentExp(
						biExpCRight, children, compKRAN);
				map.putAll(eqExpKRAN);
				break;
			}
			break;
		case KDOM:
			final MyPosition plusKDOM = position.addChildStartNorm(KDOM);
			map.putAll(getEquivalentExp(child, plusKDOM, rule));
			break;
		case KRAN:
			final MyPosition plusKRAN = position.addChildStartNorm(KRAN);
			map.putAll(getEquivalentExp(child, plusKRAN, rule));
			break;
		case CONVERSE:
			final MyPosition plusCONVERSE = position
					.addChildStartNorm(CONVERSE);
			map.put(child, new PosRule(plusCONVERSE, rule));
			map.putAll(getEquivalentExp(child, plusCONVERSE, rule));
			break;
		// TODO à partir d'ici
		case RANRES:
			map.put(expression, new PosRule(position, rule));
			switch (position.getFirstChild()) {
			case KRAN:
				final Rule.SimpRanRanresRight ranRanres = new Rule.SimpRanRanresRight(
						range);
				final Rule<RelationalPredicate> finalizedRanRanres = finalizeRule(
						children, ranRanres);
				final Rule.Composition compRanRanres = new Rule.Composition(
						rule, finalizedRanRanres);
				switch (binExp.getLeft().getTag()) {
				case KPRJ1_GEN:
				case KPRJ2_GEN:
				case KID_GEN:
					map.putAll(getEquivalentExp(binExp.getRight(), children,
							compRanRanres));
					break;
				default: // TODO mettre pourquoi on ne traite pas le cas ran(f)
							// (déjà traité d'autre part)
					final Expression[] members = {
							rule.ff.makeUnaryExpression(KRAN, binExp.getLeft(),
									null), binExp.getRight() };
					final AssociativeExpression inter = rule.ff
							.makeAssociativeExpression(BINTER, members, null);
					map.put(inter, new PosRule(children, finalizedRanRanres));
					final Rule.ContBInter contBInter = new Rule.ContBInter(
							ranRanres, binExp.getRight());
					final Rule.Composition comp = new Rule.Composition(rule,
							finalizeRule(children, contBInter));
					map.putAll(getEquivalentExp(binExp.getRight(), children,
							comp));
				}
				break;
			case KDOM:
				switch (binExp.getLeft().getTag()) {
				case KPRJ1_GEN:
					if (children.getFirstChild() == KDOM) {
						final Rule.SimpDomDomRanresPrj1Right domDomRanresPrj1 = new Rule.SimpDomDomRanresPrj1Right(
								new Rule.Domain(domain));
						final Rule.Composition compDomDomRanresPrj1 = new Rule.Composition(
								rule, finalizeRule(children.removeFirstChild(),
										domDomRanresPrj1));
						map.putAll(getEquivalentExp(binExp.getRight(),
								children.removeFirstChild(),
								compDomDomRanresPrj1));
					}
					break;
				case KPRJ2_GEN:
					if (children.getFirstChild() == KRAN) {
						final Rule.SimpRanDomRanresPrj2Right ranDomRanresPrj2 = new Rule.SimpRanDomRanresPrj2Right(
								new Rule.Range(domain));
						final Rule.Composition compRanDomRanresPrj2 = new Rule.Composition(
								rule, finalizeRule(children.removeFirstChild(),
										ranDomRanresPrj2));
						map.putAll(getEquivalentExp(binExp.getRight(),
								children.removeFirstChild(),
								compRanDomRanresPrj2));
					}
					break;
				case KID_GEN:
					final Rule.SimpDomRanresIdRight domRanresId = new Rule.SimpDomRanresIdRight(
							domain);
					map.putAll(getEquivalentExp(
							binExp.getRight(),
							children,
							new Rule.Composition(rule, finalizeRule(children,
									domRanresId))));
					break;
				}
				break;
			case CONVERSE:
				if (binExp.getLeft().getTag() == KID_GEN) {
					final Rule.Composition compConvRanres = new Rule.Composition(
							rule, finalizeRule(children, converse));
					map.putAll(getEquivalentExp(binExp, children,
							compConvRanres));
				} else {
					final Rule.SimpConvRanresRight convRanres = new Rule.SimpConvRanresRight(
							converse);
					final Rule.Composition compConvRanres = new Rule.Composition(
							rule, finalizeRule(children, convRanres));
					final BinaryExpression simpConvRanres = rule.ff
							.makeBinaryExpression(DOMRES, binExp.getRight(),
									rule.ff.makeUnaryExpression(CONVERSE,
											binExp.getLeft(), null), null);
					map.putAll(getEquivalentExp(simpConvRanres, children,
							compConvRanres));
				}
				break;
			default:
			}
			break;
		case DOMRES:
			map.put(expression, new PosRule(position, rule));
			switch (position.getFirstChild()) {
			case KRAN:
				Rule.Composition compSimpRanDomres = null;
				try {
					compSimpRanDomres = new Rule.Composition(rule,
							finalizeRule(children,
									new Rule.SimpRanDomresKxxRight(range)));
				} catch (IllegalArgumentException iae) {
					// TODO: handle exception
					break;
				}
				switch (binExp.getRight().getTag()) {
				case KPRJ1_GEN:
					final UnaryExpression srdkdom = rule.ff
							.makeUnaryExpression(KDOM, binExp.getLeft(), null);
					map.putAll(getEquivalentExp(srdkdom, children,
							compSimpRanDomres));
					break;
				case KPRJ2_GEN:
					final UnaryExpression srdkran = rule.ff
							.makeUnaryExpression(KRAN, binExp.getLeft(), null);
					;
					map.putAll(getEquivalentExp(srdkran, children,
							compSimpRanDomres));
					break;
				case KID_GEN:
					map.putAll(getEquivalentExp(binExp.getLeft(), children,
							compSimpRanDomres));
					break;
				default:
				}
				break;
			case KDOM:
				final Rule.SimpDomDomresRight domDomres = new Rule.SimpDomDomresRight(
						domain);
				final Rule.Composition compDomDomres = new Rule.Composition(
						rule, finalizeRule(children, domDomres));
				switch (position.getFirstChild()) {
				case KPRJ1_GEN:
				case KPRJ2_GEN:
				case KID_GEN:
					map.putAll(getEquivalentExp(binExp.getLeft(), children,
							compDomDomres));
				default:
					final Expression[] members = {
							rule.ff.makeUnaryExpression(KDOM,
									binExp.getRight(), null), binExp.getLeft() };
					final AssociativeExpression inter = rule.ff
							.makeAssociativeExpression(BINTER, members, null);
					map.put(inter, new PosRule(children, compDomDomres));
					final Rule.ContBInter domDomresBInter = new Rule.ContBInter(
							compDomDomres, binExp.getLeft());
					map.putAll(getEquivalentExp(binExp.getLeft(), children,
							domDomresBInter));
				}
			case CONVERSE:
				if (binExp.getRight().getTag() == KID_GEN) {
					final Rule.Composition compConvDomres = new Rule.Composition(
							rule, finalizeRule(children, converse));
					final BinaryExpression newRanres = rule.ff
							.makeBinaryExpression(RANRES, binExp.getRight(),
									binExp.getLeft(), null);
					map.putAll(getEquivalentExp(newRanres, children,
							compConvDomres));
				} else {
					final Rule.SimpConvDomresRight convDomres = new Rule.SimpConvDomresRight(
							converse);
					final Rule.Composition compConvDomres = new Rule.Composition(
							rule, finalizeRule(children, convDomres));
					final UnaryExpression conv = rule.ff.makeUnaryExpression(
							CONVERSE, binExp.getRight(), null);
					final BinaryExpression simpConv = rule.ff
							.makeBinaryExpression(RANRES, conv,
									binExp.getLeft(), null);
					map.putAll(getEquivalentExp(simpConv, children,
							compConvDomres));
				}
			default:
			}
			break;
		case RANSUB:
			map.put(expression, new PosRule(position, rule));
			switch (position.getFirstChild()) {
			case KRAN:
				switch (binExp.getLeft().getTag()) {
				case KPRJ1_GEN:
				case KPRJ2_GEN:
				case KID_GEN:
					break;
				default:
					final Rule.SimpRanRansubRight ranRansub = new Rule.SimpRanRansubRight(
							range);
					final Rule.Composition compRanRansub = new Rule.Composition(
							rule, finalizeRule(children, ranRansub));
					final UnaryExpression ran = rule.ff.makeUnaryExpression(
							KRAN, binExp.getLeft(), null);
					final BinaryExpression setminus = rule.ff
							.makeBinaryExpression(SETMINUS, ran,
									binExp.getRight(), null);
					map.put(setminus, new PosRule(children, compRanRansub));
					break;
				}
			case CONVERSE:

			default:
			}
			break;
		case DOMSUB:
			map.put(expression, new PosRule(position, rule));
			switch (position.getFirstChild()) {
			case KDOM:
				switch (binExp.getRight().getTag()) {
				case KPRJ1_GEN:
				case KPRJ2_GEN:
				case KID_GEN:
					break;
				default:
					final Rule.SimpDomDomsubRight domDomsub = new Rule.SimpDomDomsubRight(
							domain);
					final Rule.Composition compDomDomsub = new Rule.Composition(
							rule, finalizeRule(children, domDomsub));
					final UnaryExpression dom = rule.ff.makeUnaryExpression(
							KDOM, binExp.getRight(), null);
					final BinaryExpression setminus = rule.ff
							.makeBinaryExpression(SETMINUS, dom,
									binExp.getLeft(), null);
					map.put(setminus, new PosRule(children, compDomDomsub));
					break;
				}
			case CONVERSE:
			default:
			}
			break;
		default:
			map.put(expression, new PosRule(position, rule));
		}
		return map;
	}

	static class PosRule {
		private final MyPosition pos;
		private final Rule<RelationalPredicate> rule;

		public PosRule(MyPosition pos, Rule<RelationalPredicate> rule) {
			this.pos = pos;
			this.rule = rule;
		}

		public MyPosition getPos() {
			return pos;
		}

		public Rule<RelationalPredicate> getRule() {
			return rule;
		}
	}

}