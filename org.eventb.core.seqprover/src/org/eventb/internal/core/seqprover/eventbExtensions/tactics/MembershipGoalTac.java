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
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.DOMSUB;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KDOM;
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
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MyPosition;

/**
 * Try to find hypotheses to apply the reasoner MembershipGoal in order to
 * discharge a sequent such as :
 * 
 * <pre>
 * <code>H, x∈A, A⊆B ... C⊂D ⊢ x∈D</code> iff A⊆B⊂ ... ⊆ ... ⊂ ... ⊆C⊂D
 * </pre>
 * 
 * 
 * @author Emmanuel Billaud
 */
public class MembershipGoalTac implements ITactic {
	private static Expression member, set;
	private static Set<RelationalPredicate> setInclusionHyps;
	private static Map<RelationalPredicate, ExpSetPos> mapMemberHyps;
	private static IProverSequent sequent;
	private static FormulaFactory ff;

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		sequent = ptNode.getSequent();
		ff = sequent.getFormulaFactory();
		if (!preComputeGoal(sequent.goal())) {
			return "Goal does not denote a membership.";
		}
		findMemberHyps();
		if (mapMemberHyps.isEmpty()) {
			return "No hypothesis denoting a membership";
		}
		findInclusionHyps(mapMemberHyps.keySet());
//		if (setInclusionHyps.isEmpty()) {
//			return "No hypothesis denoting an inclusion (proper or not) or an equality";
//		}
		return applyToReasoner(ptNode, pm);
	}


	private static void findMemberHyps() {
		mapMemberHyps = new HashMap<RelationalPredicate, ExpSetPos>();
		for (Predicate hyp : sequent.visibleHypIterable()) {
			if (!(hyp instanceof RelationalPredicate)) {
				continue;
			}
			final ExpSetPos memberHyp = isMemberHyp(hyp);
			if (memberHyp == null) {
				continue;
			}
			final RelationalPredicate rHyp = (RelationalPredicate) hyp;
			mapMemberHyps.put(rHyp, memberHyp);
		}
	}

	/**
	 * Returns a set containing all visible hypotheses denoting an inclusion.
	 */
	private static void findInclusionHyps(Set<RelationalPredicate> memberHyps) {
		setInclusionHyps = new HashSet<RelationalPredicate>();
		for (Predicate hyp : sequent.visibleHypIterable()) {
			switch (hyp.getTag()) {
			case EQUAL:
			case SUBSET:
			case SUBSETEQ:
				if (!memberHyps.contains(hyp)) {
					setInclusionHyps.add((RelationalPredicate) hyp);
				}
			}
		}
	}

	private Object applyToReasoner(IProofTreeNode ptNode, IProofMonitor pm) {
		for (Entry<RelationalPredicate, ExpSetPos> entry : mapMemberHyps
				.entrySet()) {
			final RelationalPredicate rPred = entry.getKey();
			final Expression toMatch = entry.getValue().getExpression();
			for (MyPosition position : entry.getValue().getSetPos()) {
				final Map<Expression, MyPosition> map = getExpressionsContaining(
						toMatch, position);
				for (Entry<Expression, MyPosition> entry_map : map.entrySet()) {
					final Set<Predicate> path = findAPath(setInclusionHyps,
							entry_map.getKey(), entry_map.getValue());
					if (path == null) {
						continue;
					}
					path.add(rPred);
					final HypothesesReasoner.Input input = new HypothesesReasoner.Input(
							path.toArray(new Predicate[path.size()]));
					return BasicTactics
							.reasonerTac(new MembershipGoal(), input).apply(
									ptNode, pm);
				}
			}
		}
		return "Tactic cannot be applied";
	}

	/**
	 * Test if the predicate <code>goal</code> denotes a membership. If so, the
	 * attribute <code>member</code> is set equal to the left expression of
	 * <code>goal</code>, and the attribute <code>set</code> is set equal to the
	 * right expression <code>goal</code> and it returns <code>true</code>.
	 * <p>
	 * 
	 * @param goal
	 *            the considered predicate.
	 * @return <code>true</code> iff <code>goal</code> denotes a membership,
	 *         <code>false</code> else.
	 */
	private static boolean preComputeGoal(Predicate goal) {
		if (!Lib.isInclusion(goal)) {
			return false;
		}
		set = ((RelationalPredicate) goal).getRight();
		member = ((RelationalPredicate) goal).getLeft();
		return true;
	}

	private static ExpSetPos isMemberHyp(Predicate hyp) {
		if (!(hyp instanceof RelationalPredicate)) {
			return null;
		}
		final RelationalPredicate rHyp = (RelationalPredicate) hyp;
		final Expression leftExp = rHyp.getLeft();
		final Expression rightExp = rHyp.getRight();
		switch (hyp.getTag()) {
		case EQUAL:
			final Set<MyPosition> setRight = memberGoalPosition(rightExp,
					member, new MyPosition());
			if (!setRight.isEmpty()) {
				return new ExpSetPos(leftExp, setRight);
			}
		case IN:
		case SUBSET:
		case SUBSETEQ:
			final Set<MyPosition> setLeft = memberGoalPosition(leftExp, member, new MyPosition());
			if (!setLeft.isEmpty()) {
				return new ExpSetPos(rightExp, setLeft);
			}
		default:
			return null;
		}
	}

	private static Set<Predicate> findAPath(Set<RelationalPredicate> givenHyps,
			Expression toMatch, MyPosition position) {

		if (isContainedInGoal(toMatch, position)) {
			return new HashSet<Predicate>();
		}
		for (RelationalPredicate hyp : givenHyps) {
			final Expression left = hyp.getLeft();
			final Expression right = hyp.getRight();
			Expression expression;
			MyPosition pos = null;
			switch (hyp.getTag()) {
			case EQUAL:
				pos = getPositionInExp(right, toMatch, position);
				if (pos != null) {
					expression = left;
					break;
				}
			case SUBSET:
			case SUBSETEQ:
				pos = getPositionInExp(left, toMatch, position);
				if (pos != null) {
					expression = right;
					break;
				}
			default:
				continue;
			}

			Set<RelationalPredicate> restrictedSetRPred = new HashSet<RelationalPredicate>(
					givenHyps);
			restrictedSetRPred.remove(hyp);
			final Map<Expression, MyPosition> mapEqExp = getExpressionsContaining(
					expression, pos);
			for (Entry<Expression, MyPosition> entryEqExp : mapEqExp.entrySet()) {
				final MyPosition myPos = entryEqExp.getValue();
					final Set<Predicate> link = findAPath(
							restrictedSetRPred, entryEqExp.getKey(), myPos);
					if (link != null) {
						link.add(hyp);
						return link;
					}
			}
		}
		return null;
	}

	private static boolean isContainedInGoal(Expression expression,
			MyPosition position) {
		final MyPosition pos = getPositionInExp(set, expression, position);
		if (pos == null || !pos.isRoot()) {
			return false;
		}
		return true;
	}

	private static MyPosition getPositionInExp(final Expression tested,
			Expression toMatch, MyPosition refPos) {
		return getPositionInExp(tested, toMatch, new MyPosition(), refPos);
	}

	private static MyPosition getPositionInExp(final Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPos) {
		final MyPosition contains = contains(tested, toMatch, position, refPos);
		if (contains != null) {
			final MyPosition result = refPos.sub(contains);
			if (result != null) {
				return result;
			}
			if (contains.getLastChild() == CONVERSE) {
				final MyPosition children = contains.removeLastChild();
				final MyPosition resultBis = refPos.sub(children);
				if (resultBis == null) {
					return null;
				}
				return resultBis.addChildStartNorm(CONVERSE);
			}
		}
		return null;
	}

	private static Set<MyPosition> memberGoalPosition(final Expression tested,
			Expression toMatch, MyPosition position) {
		final Set<MyPosition> mySet = new HashSet<MyPosition>();
		final MyPosition contains = contains(tested, toMatch, new MyPosition(),
				position);
		if (contains != null) {
			final MyPosition result = position.sub(contains);
			if (result == null) {
				return mySet;
			}
			mySet.add(result);
			return mySet;
		}
		switch (tested.getTag()) {
		case MAPSTO:
			final BinaryExpression testedMAPSTO = (BinaryExpression) tested;
			final Expression leftMAPSTO = testedMAPSTO.getLeft();
			final Expression rightMAPSTO = testedMAPSTO.getRight();
			final MyPosition posLeftMAPSTO = position.addChildEndNorm(KDOM);
			mySet.addAll(memberGoalPosition(leftMAPSTO, toMatch, posLeftMAPSTO));
			final MyPosition posRightMAPSTO = position.addChildEndNorm(KRAN);
			mySet.addAll(memberGoalPosition(rightMAPSTO, toMatch,
					posRightMAPSTO));
			return mySet;
		case OVR:
			final AssociativeExpression assoTested = (AssociativeExpression) tested;
			final Expression last = assoTested.getChild(assoTested
					.getChildCount() - 1);
			mySet.addAll(memberGoalPosition(last, toMatch, position));
			return mySet;
		case SETEXT:
			final Expression[] members = ((SetExtension) tested).getMembers();
			for (Expression e : members) {
				mySet.addAll(memberGoalPosition(e, toMatch, position));
			}
			return mySet;
		}
		return mySet;
	}

	private static MyPosition contains(Expression tested, Expression toMatch,
			MyPosition toMatchPos, MyPosition refPos) {
		if (tested.equals(toMatch)) {
			return toMatchPos;
		}
		Expression child = null;
		if (tested instanceof UnaryExpression) {
			child = ((UnaryExpression) tested).getChild();
		}
		switch (tested.getTag()) {
		case OVR: // (j ⊆ ij) ∧ (i ⊆ hij can be false) ∧ (kj ⊆ hij if k⊆i)
			return containsOVR(tested, toMatch, toMatchPos, refPos);
		case BUNION: // (A ⊆ A∪B∪C) ∧ (B ⊆ A∪B∪C) ∧ (C ⊆ A∪B∪C) ∧ (A∪C ⊆ A∪B∪C)
			return containsBUNION(tested, toMatch, toMatchPos, refPos);
		case CPROD: // (A⊆B ∧ C⊆D) ⇒ (A×C ⊆ B×D)
			return containsCPROD(tested, toMatch, toMatchPos, refPos);
		case BINTER: // A∩B∩C∩D ⊆ B∩D
			// But A∩B∩C∩D ⊆ B is not treated by this method since it is treated
			// by getting an expression containing A∩B∩C∩D (here B).
			// {@link #getExpressionsContaining(Expression, Rule)}
			return containsBINTER(tested, toMatch, toMatchPos, refPos);
		case SETMINUS: // f∖h ⊆ g∖k if f⊆g ∧ h⊇k
			return containsSETMINUS(tested, toMatch, toMatchPos, refPos);
		case RANRES: // f▷A ⊆ g▷B if f⊆g ∧ A⊆B
			return containsRANRES(tested, toMatch, toMatchPos, refPos);
		case DOMRES: // A◁f ⊆ B◁g if A⊆B ∧ f⊆g
			return containsDOMRES(tested, toMatch, toMatchPos, refPos);
		case RANSUB: // f⩥A ⊆ g⩥B if f⊆g ∧ A⊇B
			return containsRANSUB(tested, toMatch, toMatchPos, refPos);
		case DOMSUB: // A⩤f ⊆ B⩤g if A⊇B ∧ f⊆g
			return containsDOMSUB(tested, toMatch, toMatchPos, refPos);
		case KDOM:
			return contains(child, toMatch, toMatchPos.addChildStartNorm(KDOM),
					refPos);
		case KRAN:
			return contains(child, toMatch, toMatchPos.addChildStartNorm(KRAN),
					refPos);
		case CONVERSE:
			return contains(child, toMatch,
					toMatchPos.addChildStartNorm(CONVERSE), refPos);
		}
		return null;
	}

	private static MyPosition containsDOMSUB(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != DOMSUB) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Expression right = binTested.getRight();
		final MyPosition containsRight = contains(right, binToMatch.getRight(),
				position, refPosition);
		if (containsRight == null) {
			return null;
		}
		if (!containsRight.equals(position)) {
			return null;
		}
		final Expression left = binTested.getLeft();
		final Set<Expression> mySet = getExpressionsContaining(left);
		for (Expression exp : mySet) {
			final MyPosition containsLeft = contains(binToMatch.getLeft(), exp,
					position, refPosition);
			if (containsLeft == null) {
				continue;
			}
			if (!containsLeft.equals(position)) {
				continue;
			}
			return position;
		}
		return null;
	}

	private static MyPosition containsRANSUB(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != RANSUB) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Expression left = binTested.getLeft();
		final MyPosition containsLeft = contains(left, binToMatch.getLeft(),
				position, refPosition);
		if (containsLeft == null) {
			return null;
		}
		if (!containsLeft.equals(position)) {
			return null;
		}
		final Expression right = binTested.getRight();

		final Set<Expression> mySet = getExpressionsContaining(right);
		for (Expression exp : mySet) {
			final MyPosition containsRight = contains(binToMatch.getRight(),
					exp, position, refPosition);
			if (containsRight == null) {
				continue;
			}
			if (!containsRight.equals(position)) {
				continue;
			}
			return position;
		}
		return null;
	}

	private static MyPosition containsDOMRES(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != DOMRES) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Expression left = binTested.getLeft();
		final MyPosition containsLeft = contains(left, binToMatch.getLeft(),
				position, refPosition);
		if (containsLeft == null) {
			return null;
		}
		if (!containsLeft.equals(position)) {
			return null;
		}
		final Expression right = binTested.getRight();
		final MyPosition containsRight = contains(right, binToMatch.getRight(),
				position, refPosition);
		if (containsRight == null) {
			return null;
		}
		if (!containsRight.equals(position)) {
			return null;
		}
		return position;
	}

	private static MyPosition containsRANRES(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != RANRES) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Expression left = binTested.getLeft();
		final MyPosition containsLeft = contains(left, binToMatch.getLeft(),
				position, refPosition);
		if (containsLeft == null) {
			return null;
		}
		if (!containsLeft.equals(position)) {
			return null;
		}
		final Expression right = binTested.getRight();
		final MyPosition containsRight = contains(right, binToMatch.getRight(),
				position, refPosition);
		if (containsRight == null) {
			return null;
		}
		if (!containsRight.equals(position)) {
			return null;
		}
		return position;
	}

	private static MyPosition containsSETMINUS(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != SETMINUS) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Expression left = binTested.getLeft();
		final MyPosition containsLeft = contains(left, binToMatch.getLeft(),
				position, refPosition);
		if (containsLeft == null) {
			return null;
		}
		if (!containsLeft.equals(position)) {
			return null;
		}
		final Expression right = binTested.getRight();

		final Set<Expression> mySet = getExpressionsContaining(
				right);
		for (Expression exp : mySet
				) {
			final MyPosition containsRight = contains(binToMatch.getRight(),
					exp, position, refPosition);
			if (containsRight == null) {
				continue;
			}
			if (!containsRight.equals(position)) {
				continue;
			}
			return position;
		}
		return null;
	}

	private static MyPosition containsBINTER(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != BINTER) {
			return null;
		}
		final Expression[] children = ((AssociativeExpression) tested)
				.getChildren();
		final Expression[] toMatches = ((AssociativeExpression) toMatch)
				.getChildren();
		MyPosition pos = null;
		for (Expression testedExp : children) {
			boolean isContained = false;
			boolean positionsAreEqual = true;
			for (Expression toMatchExp : toMatches) {
				final MyPosition contains = contains(testedExp, toMatchExp,
						position, refPosition);
				if (contains == null) {
					continue;
				}
				if (pos == null) {
					if (refPosition.sub(contains) == null) {
						continue;
					}
				} else {
					positionsAreEqual &= (pos.equals(contains));
					if (!positionsAreEqual) {
						continue;
					}
				}
				pos = contains;
				isContained = true;
				break;
			}
			if (!isContained) {
				return null;
			}
		}
		return pos;
	}

	private static MyPosition containsCPROD(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		if (toMatch.getTag() != CPROD) {
			return null;
		}
		final BinaryExpression binToMatch = (BinaryExpression) toMatch;
		final BinaryExpression binTested = (BinaryExpression) tested;
		final Expression testedLeft = binTested.getLeft();
		final MyPosition left = contains(testedLeft, binToMatch.getLeft(),
				position, refPosition);
		if (left == null) {
			return null;
		}
		if (!left.equals(position)) {
			return null;
		}
		final Expression testedRight = binTested.getRight();
		final MyPosition right = contains(testedRight, binToMatch.getRight(),
				position, refPosition);
		if (right == null || !right.equals(position)) {
			return null;
		}
		return position;
	}

	private static MyPosition containsOVR(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		final AssociativeExpression assoTested = (AssociativeExpression) tested;
		if (toMatch.getTag() == OVR) {
			final Expression[] toMatches = ((AssociativeExpression) toMatch)
					.getChildren();
			final Expression[] testedChildren = assoTested.getChildren();
			final Expression[] testedChildRes = new Expression[toMatches.length];
			System.arraycopy(testedChildren, testedChildren.length
					- toMatches.length, testedChildRes, 0, toMatches.length);
			final MyPosition contains = contains(testedChildRes[0],
					toMatches[0], position, refPosition);
			if (contains == null || !contains.equals(position)) {
				return null;
			}
			for (int i = 1; i < toMatches.length; i++) {
				if (!toMatches[i].equals(testedChildren[testedChildren.length
						- toMatches.length + i])) {
					return null;
				}
			}
			return contains;
		} else {
			final Expression last = assoTested.getChild(assoTested
					.getChildCount() - 1);
			return contains(last, toMatch, position, refPosition);
		}
	}

	private static MyPosition containsBUNION(Expression tested,
			Expression toMatch, MyPosition position, MyPosition refPosition) {
		final Expression[] children = ((AssociativeExpression) tested)
				.getChildren();
		if (toMatch.getTag() == BUNION) {
			final Expression[] toMatches = ((AssociativeExpression) toMatch)
					.getChildren();
			MyPosition pos = null;
			for (Expression e : toMatches) {
				boolean isContained = false;
				boolean positionsAreEqual = true;
				for (Expression member : children) {
					final MyPosition contains = contains(member, e, position,
							refPosition);
					if (contains == null) {
						continue;
					}
					if (pos == null) {
						if (refPosition.sub(contains) == null) {
							continue;
						}
					} else {
						positionsAreEqual &= (pos.equals(contains));
						if (!positionsAreEqual) {
							continue;
						}
					}
					pos = contains;
					isContained = true;
					break;
				}
				if (!isContained) {
					return null;
				}
			}
			return pos;
		} else {
			for (Expression e : children) {
				final MyPosition contains = contains(e, toMatch, position,
						refPosition);
				if (contains != null) {
					if (refPosition.sub(contains) != null) {
						return contains;
					}
				}
			}
		}
		return null;
	}

	private static Map<Expression, MyPosition> getExpressionsContaining(
			Expression expression, MyPosition position) {
		Map<Expression, MyPosition> map = new HashMap<Expression, MyPosition>();
		for (Expression exp : getExpressionsContaining(expression)) {
			final Map<Expression, MyPosition> mapEqExp = getEquivalentExp(
					exp, position);
			for (Entry<Expression, MyPosition> eqExp : mapEqExp.entrySet()) {
				final Expression eqExpKey = eqExp.getKey();
				final MyPosition eqExpPos = eqExp.getValue();
				if (eqExpKey.equals(exp) && position.equals(eqExpPos)) {
					map.put(eqExpKey, eqExpPos);
				} else {
					map.putAll(getExpressionsContaining(eqExpKey, eqExpPos));
				}
			}
		}
		return map;
	}

	private static Set<Expression> getExpressionsContaining(
			Expression contained) {
		Set<Expression> mySet = new HashSet<Expression>();
		mySet.add(contained);
		switch (contained.getTag()) {
		case BINTER: // f∩g ⊆ f
			AssociativeExpression binter = (AssociativeExpression) contained;
			for (Expression expression : binter.getChildren()) {
				mySet.addAll(getExpressionsContaining(expression));
			}
			break;
		case SETMINUS: // f∖g ⊆ f
			Expression leftSetminus = ((BinaryExpression) contained).getLeft();
			mySet.addAll(getExpressionsContaining(leftSetminus));
			break;
		case RANRES: // f▷A ⊆ f
			Expression leftRanres = ((BinaryExpression) contained).getLeft();
			mySet.addAll(getExpressionsContaining(leftRanres));
			break;
		case RANSUB: // f⩥A ⊆ f
			Expression leftransub = ((BinaryExpression) contained).getLeft();
			mySet.addAll(getExpressionsContaining(leftransub));
			break;
		case DOMRES: // A◁f ⊆ f
			Expression rightDomres = ((BinaryExpression) contained).getRight();
			mySet.addAll(getExpressionsContaining(rightDomres));
			break;
		case DOMSUB: // A⩤f ⊆ f
			Expression rightDomsub = ((BinaryExpression) contained).getRight();
			mySet.addAll(getExpressionsContaining(rightDomsub));
			break;
		case CPROD: // (A∩B)×(C∩D) = (A×C)∩(A×D)∩(B×C)∩(B×D)
			final Expression leftCPROD = ((BinaryExpression) contained)
					.getLeft();
			final Set<Expression> exprContLCP = getExpressionsContaining(leftCPROD);
			final Expression rightCPROD = ((BinaryExpression) contained)
					.getRight();
			final Set<Expression> exprContRCP = getExpressionsContaining(rightCPROD);
			for (Expression leftExp : exprContLCP) {
				for (Expression rightExp : exprContRCP) {
					final BinaryExpression resultExp = ff.makeBinaryExpression(
							CPROD, leftExp, rightExp, null);
					mySet.add(resultExp);
				}
			}

		}
		return mySet;
	}

	private static Map<Expression, MyPosition> getEquivalentExp(
			Expression expression, MyPosition position) {
		Map<Expression, MyPosition> map = new HashMap<Expression, MyPosition>();
		Expression child = null;
		if (expression instanceof UnaryExpression) {
			child = ((UnaryExpression) expression).getChild();
		}
		switch (expression.getTag()) {
		case CPROD:
			map.put(expression, position);
			final BinaryExpression biExpC = (BinaryExpression) expression;
			final Expression biExpCLeft = biExpC.getLeft();
			final Expression biExpCRight = biExpC.getRight();
			final MyPosition children = position.removeFirstChild();
			switch (position.getFirstChild()) {
			case CONVERSE:
				final BinaryExpression cprod = ff.makeBinaryExpression(CPROD,
						biExpCRight, biExpCLeft, null);
				map.putAll(getEquivalentExp(cprod, children));
				break;
			case KDOM:
				map.putAll(getEquivalentExp(biExpCLeft, children));
				break;
			case KRAN:
				map.putAll(getEquivalentExp(biExpCRight, children));
				break;
			}
			break;
		case KDOM:
			final MyPosition plusKDOM = position.addChildStartNorm(KDOM);
			map.putAll(getEquivalentExp(child, plusKDOM));
			break;
		case KRAN:
			final MyPosition plusKRAN = position.addChildStartNorm(KRAN);
			map.putAll(getEquivalentExp(child, plusKRAN));
			break;
		case CONVERSE:
			final MyPosition plusCONVERSE = position
					.addChildStartNorm(CONVERSE);
			map.put(child, plusCONVERSE);
			map.putAll(getEquivalentExp(child, plusCONVERSE));
			break;
		default:
			map.put(expression, position);
		}
		return map;
	}
	
	private static class ExpSetPos {
		private final Expression expression;
		private final Set<MyPosition> setPos;
		
		public ExpSetPos(Expression exp, Set<MyPosition> set) {
			this.expression = exp;
			this.setPos = set;
		}

		public Expression getExpression() {
			return expression;
		}

		public Set<MyPosition> getSetPos() {
			return setPos;
		}
	}

}