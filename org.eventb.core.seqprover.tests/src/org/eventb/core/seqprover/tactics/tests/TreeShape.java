/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added FunImgSimp tree shape
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstractManualInference;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstractRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunImageGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewrites;

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
	
	private static class ConjIShape extends TreeShape {

		public ConjIShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			AbstractRewriter.Input i = (AbstractRewriter.Input) input;
			assertNull(i.getPred());
		}

		@Override
		protected String getReasonerID() {
			return Conj.REASONER_ID;
		}
	}

	private static class DisjEShape extends TreeShape {

		private final Predicate predicate;
		
		public DisjEShape(Predicate predicate, TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			HypothesisReasoner.Input i = (HypothesisReasoner.Input) input;
			assertEquals(predicate, i.getPred());
		}

		@Override
		protected String getReasonerID() {
			return DisjE.REASONER_ID;
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
	}

	private static class FunOvrShape extends TreeShape {

		protected final Predicate predicate;

		protected final String position;

		public FunOvrShape(String position, TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = null;
			this.position = position;
		}

		public FunOvrShape(Predicate predicate, String position,
				TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
			this.position = position;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			AbstractManualInference.Input i = (AbstractManualInference.Input) input;
			assertEquals(predicate, i.getPred());
			assertEquals(position, i.getPosition().toString());
		}

		@Override
		protected String getReasonerID() {
			return FunOvr.REASONER_ID;
		}
	}
	
	private static class FunImgSimpShape extends TreeShape {
		
		protected final String position;

		public FunImgSimpShape(String position, TreeShape[] expChildren) {
			super(expChildren);
			this.position = position;
		}

		
		@Override
		protected void checkInput(IReasonerInput input) {
			final AbstractManualRewrites.Input i = (AbstractManualRewrites.Input)input;
			assertEquals(position, i.getPosition().toString());
		}

		@Override
		protected String getReasonerID() {
			return FunImgSimplifies.REASONER_ID;
		}
	}
	
	private static class IsFunGoalShape extends TreeShape{

		public IsFunGoalShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assertEquals(input.getClass(), EmptyInput.class);			
		}

		@Override
		protected String getReasonerID() {
			return IsFunGoal.REASONER_ID;
		}
		
	}
	
	private static class FunImgGoalShape extends TreeShape {

		private final Predicate predicate;
		private final String position;

		public FunImgGoalShape(Predicate pred, String pos,
				TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = pred;
			this.position = pos;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			AbstractManualInference.Input i = (AbstractManualInference.Input) input;
			assertEquals(position, i.getPosition().toString());
			assertEquals(predicate, i.getPred());

		}

		@Override
		protected String getReasonerID() {
			return IsFunImageGoal.REASONER_ID;
		}

	}
	
	private static class TypeRewritesShape extends TreeShape {

		public TypeRewritesShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assertEquals(input.getClass(), EmptyInput.class);
		}

		@Override
		protected String getReasonerID() {
			return TypeRewrites.REASONER_ID;
		}

	}

	private static class TotalDomShape extends TreeShape {

		private final Predicate predicate;

		private final String position;

		private final Expression substitute;

		public TotalDomShape(Predicate predicate, String position,
				Expression substitute, TreeShape[] expChildren) {
			super(expChildren);
			this.predicate = predicate;
			this.position = position;
			this.substitute = substitute;
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			final TotalDomRewrites.Input inp = (TotalDomRewrites.Input) input;
			assertEquals(inp.getPred(), predicate);
			assertEquals(inp.getPosition().toString(), position);
			assertEquals(inp.getSubstitute(), substitute);

		}

		@Override
		protected String getReasonerID() {
			return TotalDomRewrites.REASONER_ID;
		}

	}

	private static class HypShape extends TreeShape {

		public HypShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assertEquals(input.getClass(), EmptyInput.class);

		}

		@Override
		protected String getReasonerID() {
			return "org.eventb.core.seqprover.hyp";
		}

	}

	private static class TrueGoalShape extends TreeShape {

		public TrueGoalShape(TreeShape[] expChildren) {
			super(expChildren);
		}

		@Override
		protected void checkInput(IReasonerInput input) {
			assertEquals(input.getClass(), EmptyInput.class);
		}

		@Override
		protected String getReasonerID() {
			return TrueGoal.REASONER_ID;
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
	
	public static TreeShape conjI(TreeShape... children) {
		return new ConjIShape(children);
	}

	public static TreeShape disjE(Predicate pred, TreeShape... children) {
		return new DisjEShape(pred, children);
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

	protected abstract String getReasonerID();
}