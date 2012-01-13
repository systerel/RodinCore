/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.SequentProver.getReasonerRegistry;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;
import org.eventb.core.seqprover.reasoners.Review;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstractManualInference;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.ConjF;
import org.eventb.internal.core.seqprover.eventbExtensions.DTDistinctCase;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.eventb.internal.core.seqprover.eventbExtensions.ExI;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.FunImageGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpE;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjunctionToImplicationRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqvRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipL1;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewrites;
import org.junit.Assert;

/**
 * Common implementation for verifying rule applications to a proof subtree. The
 * intent is to simplify writing tests about tactic application on a proof node
 * (or more exactly the subtree rooted at that node).
 * <p>
 * Clients should use code similar to:
 * 
 * <pre>
 * assertRulesApplied(node, conjI(empty, empty, empty));
 * </pre>
 * 
 * where <code>node</code> is a proof tree node and the second parameter is
 * the expected shape of the proof tree rooted at the given node.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class TreeShape {
	
	private static <T> T[] arr(T... t) {
		return t;
	}
	
	private static class ConjIShape extends HypothesisShape {

		public ConjIShape(TreeShape[] expChildren) {
			super(null, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return Conj.REASONER_ID;
		}
	}

	private static class ConjFShape extends FwdInferenceShape {

		public ConjFShape(Predicate predicate, TreeShape[] expChildren) {
			super(predicate, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return ConjF.REASONER_ID;
		}
	}

	private static class DisjEShape extends HypothesisShape {

		public DisjEShape(Predicate predicate, TreeShape[] expChildren) {
			super(predicate, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return DisjE.REASONER_ID;
		}
	}

	private static class ExIShape extends TreeShape {

		private final Expression[] inst;
		
		public ExIShape(TreeShape wd, TreeShape instantiated, Expression... inst) {
			super(arr(wd, instantiated));
			this.inst = inst;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			MultipleExprInput i = (MultipleExprInput) input;
			assertTrue(Arrays.equals(inst, i.getExpressions()));
		}

		@Override
		protected String getReasonerID() {
			return ExI.REASONER_ID;
		}

		@Override
		protected IReasonerInput getInput() {
			return new MultipleExprInput(inst);
		}
	}

	private static class EmptyShape extends TreeShape {

		public EmptyShape() {
			super(null);
		}

		public void check(IProofTreeNode node) {
			assertTrue(node.isOpen());
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assert false;
		}

		@Override
		protected String getReasonerID() {
			assert false;
			return null;
		}

		@Override
		protected IReasonerInput getInput() {
			return new EmptyInput();
		}
	}

	private static class FunOvrShape extends ManualInferenceShape {

		public FunOvrShape(Predicate predicate, String position,
				TreeShape[] expChildren) {
			super(predicate, position, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return FunOvr.REASONER_ID;
		}
	}
	
	private static class FunImgSimpShape extends PosShape {

		public FunImgSimpShape(String position, TreeShape[] expChildren) {
			super(position, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return FunImgSimplifies.REASONER_ID;
		}
	}
	
	private static class IsFunGoalShape extends VoidShape{

		public IsFunGoalShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return IsFunGoal.REASONER_ID;
		}
		
	}
	
	private static class FunImgGoalShape extends ManualInferenceShape {

		public FunImgGoalShape(Predicate pred, String pos,
				TreeShape[] expChildren) {
			super(pred, pos, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return FunImageGoal.REASONER_ID;
		}

	}
	
	private static class TypeRewritesShape extends VoidShape {

		public TypeRewritesShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return TypeRewrites.REASONER_ID;
		}

	}
	
	private static class AutoRewritesShape extends VoidShape {

		public AutoRewritesShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return AutoRewrites.REASONER_ID;
		}

	}

	private static class TotalDomShape extends ManualRewritesShape {

		private final Expression substitute;

		public TotalDomShape(Predicate predicate, String position,
				Expression substitute, TreeShape[] expChildren) {
			super(predicate, position, expChildren);
			this.substitute = substitute;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			super.checkInput(input);
			final TotalDomRewrites.Input inp = (TotalDomRewrites.Input) input;
			assertEquals(inp.getSubstitute(), substitute);
		}

		@Override
		protected String getReasonerID() {
			return TotalDomRewrites.REASONER_ID;
		}

		@Override
		protected IReasonerInput getInput() {
			return new TotalDomRewrites.Input(predicate, makePosition(position), substitute);
		}
	}

	private static class HypShape extends VoidShape {

		public HypShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return "org.eventb.core.seqprover.hyp";
		}

	}

	private static class TrueGoalShape extends VoidShape {

		public TrueGoalShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return TrueGoal.REASONER_ID;
		}

	}

	private static class FalseHypShape extends VoidShape {

		public FalseHypShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return FalseHyp.REASONER_ID;
		}

	}

	private static class DTDestrWDShape extends ManualInferenceShape {

		public DTDestrWDShape(String position, Expression[] inst) {
			super(null, position, arr(exI(trueGoal(), hyp(), inst)));
		}

		@Override
		protected String getReasonerID() {
			return DTDistinctCase.REASONER_ID;
		}
		
	}
	
	private static class RmShape extends PosShape {

		public RmShape(String position, TreeShape... children) {
			super(position, children);
		}

		@Override
		protected String getReasonerID() {
			return RemoveMembershipL1.REASONER_ID;
		}
		
	}
	
	private static class RiShape extends PosShape {

		public RiShape(String position, TreeShape... children) {
			super(position, children);
		}

		@Override
		protected String getReasonerID() {
			return RemoveInclusion.REASONER_ID;
		}
		
	}
	
	private static class EqvShape extends PosShape {

		public EqvShape(String position, TreeShape... children) {
			super(position, children);
		}

		@Override
		protected String getReasonerID() {
			return EqvRewrites.REASONER_ID;
		}
		
	}
	
	private static class RnShape extends ManualRewritesShape {

		public RnShape(Predicate predicate, String position, TreeShape... expChildren) {
			super(predicate, position, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return RemoveNegation.REASONER_ID;
		}

	}

	private static class ImpIShape extends VoidShape {

		public ImpIShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected String getReasonerID() {
			return ImpI.REASONER_ID;
		}

	}

	private static class ImpEShape extends HypothesisShape {

		public ImpEShape(Predicate predicate, TreeShape[] expChildren) {
			super(predicate, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return ImpE.REASONER_ID;
		}

	}

	
	
	private static class DTIShape extends ManualRewritesShape {

		public DTIShape(Predicate predicate, String position,
				TreeShape[] expChildren) {
			super(predicate, position, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return DisjunctionToImplicationRewrites.REASONER_ID;
		}

	}

	private static class MBGoalShape extends HypothesesShape {

		public MBGoalShape(Predicate[] predicates, TreeShape[] expChildren) {
			super(predicates, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return "org.eventb.core.seqprover.mbGoal";
		}
	}

	private static abstract class HypothesisShape extends TreeShape {

		private final Predicate predicate;

		public HypothesisShape(Predicate predicate, TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
		}

		@Override
		protected void checkInput(IReasonerInput rInput) {
			HypothesisReasoner.Input input = (HypothesisReasoner.Input) rInput;
			assertEquals(predicate, input.getPred());
		}

		@Override
		protected IReasonerInput getInput() {
			return new HypothesisReasoner.Input(predicate);
		}
	}

	private static abstract class HypothesesShape extends TreeShape {

		private final Predicate[] predicates;

		public HypothesesShape(Predicate[] predicates, TreeShape[] expChildren) {
			super(expChildren);
			this.predicates = predicates;
		}

		@Override
		protected void checkInput(IReasonerInput rInput) {
			HypothesesReasoner.Input input = (HypothesesReasoner.Input) rInput;
			Assert.assertArrayEquals(predicates, input.getPred());
		}

		@Override
		protected IReasonerInput getInput() {
			return new HypothesesReasoner.Input(predicates);
		}
	}

	private static abstract class ManualRewritesShape extends TreeShape {

		protected final Predicate predicate;
		protected final String position;

		private ManualRewritesShape(Predicate predicate, String position,
				TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
			this.position = position;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			final AbstractManualRewrites.Input inp = ((AbstractManualRewrites.Input) input);
			assertEquals(position, inp.getPosition().toString());
			assertEquals(predicate, inp.getPred());
		}

		@Override
		protected IReasonerInput getInput() {
			return new AbstractManualRewrites.Input(predicate, makePosition(position));
		}
	}
	
	private static abstract class ManualInferenceShape extends TreeShape {

		private final Predicate predicate;
		private final String position;

		private ManualInferenceShape(Predicate predicate, String position,
				TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
			this.position = position;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			final AbstractManualInference.Input inp = ((AbstractManualInference.Input) input);
			assertEquals(position, inp.getPosition().toString());
			assertEquals(predicate, inp.getPred());
		}

		@Override
		protected IReasonerInput getInput() {
			return new AbstractManualInference.Input(predicate, makePosition(position));
		}
	}

	private static abstract class FwdInferenceShape extends TreeShape {

		private final Predicate predicate;

		private FwdInferenceShape(Predicate predicate,
				TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			final ForwardInfReasoner.Input inp = ((ForwardInfReasoner.Input) input);
			assertEquals(predicate, inp.getPred());
		}

		@Override
		protected IReasonerInput getInput() {
			return new ForwardInfReasoner.Input(predicate);
		}
	}

	private static abstract class PosShape extends TreeShape {

		private final String position;

		private PosShape(String position, TreeShape[] expChildren) {
			super(expChildren);
			this.position = position;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			final AbstractManualRewrites.Input inp = ((AbstractManualRewrites.Input) input);
			assertEquals(position, inp.getPosition().toString());
		}

		@Override
		protected IReasonerInput getInput() {
			// FIXME no predicate ? means goal here
			return new AbstractManualRewrites.Input(null, makePosition(position));
		}
	}

	private static abstract class VoidShape extends TreeShape {

		private VoidShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assertEquals(input.getClass(), EmptyInput.class);
		}

		@Override
		protected IReasonerInput getInput() {
			return new EmptyInput();
		}
	}

	private static class MapOvrGShape extends HypothesisShape {

		public MapOvrGShape(Predicate predicate, TreeShape... expChildren) {
			super(predicate, expChildren);
		}

		@Override
		protected String getReasonerID() {
			return "org.eventb.core.seqprover.mapOvrG";
		}

	}

	private static class ReviewShape extends TreeShape {

		private final IProverSequent sequent;

		public ReviewShape(TreeShape[] expChildren, IProverSequent sequent) {
			super(expChildren);
			this.sequent = sequent;
		}
		
		@Override
		protected String getReasonerID() {
			return "org.eventb.core.seqprover.review";
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assertTrue(input instanceof Review.Input);
			// TODO check input.sequent (requires accessor)
		}

		@Override
		protected IReasonerInput getInput() {
			return new Review.Input(sequent, IConfidence.REVIEWED_MAX);
		}
		
	}
	
	public static final TreeShape empty = new EmptyShape();

	/**
	 * Ensures that the proof subtree rooted at the given node as the shape
	 * described by the <code>expected</code> parameter.
	 * 
	 * @param node
	 *            a proof tree node
	 * @param expected
	 *            a description of the expected proof subtree shape
	 */
	public static void assertRulesApplied(IProofTreeNode node,
			TreeShape expected) {
		expected.check(node);
	}
	
	public static void assertSuccess(IProofTreeNode node, TreeShape expected,
			ITactic tactic) {
		assertNull(tactic.apply(node, null));
		assertRulesApplied(node, expected);
	}

	public static void assertFailure(IProofTreeNode node, ITactic tactic) {
		assertNotNull(tactic.apply(node, null));
		assertRulesApplied(node, empty);
	}

	public static TreeShape isFunGoal(TreeShape...children){
		return new IsFunGoalShape(children);
	}
	
	public static TreeShape funImgGoal(Predicate pred, String pos,
			TreeShape... children) {
		return new FunImgGoalShape(pred, pos, children);
	}

	public static TreeShape typeRewrites(TreeShape... children) {
		return new TypeRewritesShape(children);
	}

	public static TreeShape autoRewrites(TreeShape... children) {
		return new AutoRewritesShape(children);
	}

	public static TreeShape hyp(TreeShape... children) {
		return new HypShape(children);
	}

	public static TreeShape totalDom(Predicate predicate, String position,
			Expression substitute, TreeShape... children) {
		return new TotalDomShape(predicate, position, substitute,children);
	}

	public static TreeShape trueGoal(TreeShape... children) {
		return new TrueGoalShape(children);
	}
	
	public static TreeShape falseHyp(TreeShape... children) {
		return new FalseHypShape(children);
	}
	
	public static TreeShape conjI(TreeShape... children) {
		return new ConjIShape(children);
	}

	public static TreeShape conjF(Predicate hyp, TreeShape... children) {
		return new ConjFShape(hyp, children);
	}

	public static TreeShape disjE(Predicate pred, TreeShape... children) {
		return new DisjEShape(pred, children);
	}

	public static TreeShape exI(TreeShape wd, TreeShape instantiated, Expression... inst) {
		return new ExIShape(wd, instantiated, inst);
	}

	public static TreeShape funOvr(String position, TreeShape... children) {
		return new FunOvrShape(null, position, children);
	}

	public static TreeShape funOvr(Predicate predicate, String position,
			TreeShape... children) {
		return new FunOvrShape(predicate, position, children);
	}
	
	public static TreeShape funImgSimp(String position, TreeShape... children){
		return new FunImgSimpShape(position, children);
	}
	
	public static TreeShape dtDestrWD(String position, Expression... inst) {
		return new DTDestrWDShape(position, inst);
	}
	
	public static TreeShape rm(String position, TreeShape... children) {
		return new RmShape(position, children);
	}

	public static TreeShape ri(String position, TreeShape... children) {
		return new RiShape(position, children);
	}

	public static TreeShape eqv(String position, TreeShape... children) {
		return new EqvShape(position, children);
	}
	
	public static TreeShape rn(String position, TreeShape... children) {
		return new RnShape(null, position, children);
	}

	public static TreeShape rn(Predicate hyp, String pos, TreeShape... children) {
		return new RnShape(hyp, pos, children);
	}

	public static TreeShape impI(TreeShape... children) {
		return new ImpIShape(children);
	}

	public static TreeShape impE(Predicate hyp, TreeShape... children) {
		return new ImpEShape(hyp, children);
	}

	public static TreeShape dti(Predicate hyp, String pos, TreeShape... children) {
		return new DTIShape(hyp, pos, children);
	}

	public static TreeShape dti(String pos, TreeShape... children) {
		return new DTIShape(null, pos, children);
	}

	public static TreeShape mbg(Predicate[] predicates, TreeShape... children) {
		return new MBGoalShape(predicates, children);
	}

	public static TreeShape mapOvrG(Predicate predicate, TreeShape... chidlren) {
		return new MapOvrGShape(predicate, chidlren);
	}
	
	public static TreeShape review(IProverSequent sequent, TreeShape... children) {
		return new ReviewShape(children, sequent);
	}

	protected final TreeShape[] expChildren;

	public TreeShape(TreeShape[] expChildren) {
		this.expChildren = expChildren;
	}

	public void check(IProofTreeNode node) {
		final IProofRule rule = node.getRule();
		assertNotNull(rule);
		assertEquals(getReasonerID(), rule.generatedBy().getReasonerID());
		checkInput(rule.generatedUsing());
		checkChildren(node);
	}

	protected void checkChildren(IProofTreeNode node) {
		final IProofTreeNode[] actChildren = node.getChildNodes();
		final int len = expChildren.length;
		assertEquals(len, actChildren.length);
		for (int i = 0; i < len; ++i) {
			expChildren[i].check(actChildren[i]);
		}
	}

	protected abstract void checkInput(IReasonerInput input);

	protected abstract IReasonerInput getInput();
	
	protected abstract String getReasonerID();
	
	private static final IReasonerRegistry REASONER_REG = getReasonerRegistry();
	
	public void apply(IProofTreeNode node) {
		final String reasonerID = getReasonerID();
		final IReasoner reasoner = REASONER_REG.getReasonerDesc(reasonerID).getInstance();
		final ITactic tactic = reasonerTac(reasoner, getInput());
		
		final Object failure = tactic.apply(node, null);
		if (failure != null) {
			fail(failure.toString());
		}
		
		final IProofTreeNode[] childNodes = node.getChildNodes();
		final TreeShape[] childShapes = expChildren;
		assertEquals(childShapes.length, childNodes.length);
		
		for (int i = 0; i < childShapes.length; i++) {
			final IProofTreeNode childNode = childNodes[i];
			final TreeShape childShape = childShapes[i];
			childShape.apply(childNode);
		}
	}

}