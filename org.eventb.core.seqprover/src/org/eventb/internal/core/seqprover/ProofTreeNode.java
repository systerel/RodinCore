/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extended interface with getFormulaFactory() method
 *     Systerel - used nested classes instead of anonymous ones
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;


/**
 * Implementation of {@link org.eventb.core.seqprover.IProofTreeNode}
 * 
 * 
 * @author Farhad Mehta
 *
 */
public final class ProofTreeNode implements IProofTreeNode {
	
	private static class ProofSkeleton implements IProofSkeleton {

		private final String comment;
		
		private final IProofSkeleton[] childSkelNodes;

		private final IProofRule proofRule;

		public ProofSkeleton(String comment, IProofSkeleton[] childSkelNodes,
				IProofRule proofRule) {
			this.comment = comment;
			this.childSkelNodes = childSkelNodes;
			this.proofRule = proofRule;
		}

		public IProofSkeleton[] getChildNodes() {
			return childSkelNodes;
		}

		public IProofRule getRule() {
			return proofRule;
		}

		public String getComment() {
			return comment;
		}
	}

	private static final ProofTreeNode[] NO_NODE = new ProofTreeNode[0];
	
	
	/**
	 * Sequent associated to this proof tree node.
	 * 
	 * This field is immutable and always non-null.
	 */
	private final IProverSequent sequent;

	/**
	 * Children nodes of this proof tree node.
	 * 
	 * This field may be changed in a controlled way using methods in this class.
	 * This field is equal to <code>null</code> iff <code>rule</code> equals 
	 * <code>null</code>.
	 */
	private ProofTreeNode[] children;

	/**
	 * Rule applied to this proof tree node.
	 * 
	 * This field may be changed in a controlled way using methods in this class.
	 * This field is equal to null iff <code>children</code> equals <code>null</code>.
	 */
	private ProofRule rule;

	/**
	 * Comment associated to this proof tree node.
	 */
	private String comment;
	
	/**
	 * Parent node of this proof tree node.
	 */
	private ProofTreeNode parent;
	
	/**
	 * Confidence level (see @see IConfidence) for this proof tree node.
	 * 
	 * Although it is possible to calculate the confidence for a node, this 
	 * information is cached here for greater efficiency.
	 */
	private int confidence;
	
	
	/**
	 * Proof tree to which this node belongs. This field is only set for a root node,
	 * so that it's easy to remove a whole subtree from the tree. Always use
	 * #getProofTree() to access this information.
	 */
	private ProofTree tree;

	/**
	 * Creates a root node of a proof tree.
	 * 
	 * @param tree The proof tree to associate to 
	 * @param sequent The sequent used to construct the proof tree node
	 */
	public ProofTreeNode(ProofTree tree, IProverSequent sequent) {
		assert tree != null;
		this.tree = tree;
		this.parent = null;
		this.sequent = sequent;
		this.rule = null;
		this.children = null;
		this.confidence = IConfidence.PENDING;
		this.comment = "";
		assert classInvariant();
		// this.assertClassInvariant();
	}
	
	/**
	 * Creates an internal node of a proof tree.
	 * 
	 * @param parent The parent node for the proof tree node to construct
	 * @param sequent The sequent used to construct the proof tree node
	 */
	private ProofTreeNode(ProofTreeNode parent, IProverSequent sequent) {
		assert parent != null;
		this.tree = null;
		this.parent = parent;
		this.sequent = sequent;
		this.rule = null;
		this.children = null;
		this.confidence = IConfidence.PENDING;
		this.comment = "";
		assert classInvariant();
	}
	
	
	/**
	 * Copy constructor
	 * 
	 * @param node
	 * 				node to copy
	 */
	private ProofTreeNode(ProofTreeNode node) {
		this.tree = node.tree;
		this.parent = node.parent;
		this.sequent = node.sequent;
		this.rule = node.rule;
		if (node.children == null) 
			this.children = null;
		else
		{
			final int length = node.children.length;
			this.children = new ProofTreeNode[length];
			for (int i = 0; i < length; i++) {
				this.children[i]= new ProofTreeNode(node.children[i]);
				this.children[i].parent = this;
			}
		}
		this.confidence = node.confidence;
		this.comment = node.comment;
	}
	
	public ProofTree copySubTree(){
		// Copy the subtree
		ProofTreeNode root = new ProofTreeNode(this);
		// Disconnect the copy from the tree
		root.parent = null;
		root.tree = null;
		ProofTree proofTree = new ProofTree(root);
		assert proofTree.getRoot().classInvariant();
		return proofTree;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IProofTreeNode#copyProofSkeleton()
	 */
	public IProofSkeleton copyProofSkeleton() {
		final String comment = getComment();
		final IProofRule proofRule = getRule();
		final IProofTreeNode[] childNodes = getChildNodes();
		final IProofSkeleton[] childSkelNodes = new IProofSkeleton[childNodes.length];
		for (int i = 0; i < childNodes.length; i++) {
			childSkelNodes[i] = childNodes[i].copyProofSkeleton();
		}
		
		return new ProofSkeleton(comment, childSkelNodes, proofRule);
	}
	
	
	/**
	 * Append the open descendants of this node to the given list.
	 * 
	 * @param list The list of open decendants to append to
	 */
	private void appendOpenDescendants(List<IProofTreeNode> list) {
		if (isOpen()) {
			list.add(this);
		}
		else {
			for (ProofTreeNode child : this.children) {
				child.appendOpenDescendants(list);
			}
		}		
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#applyRule(ProofRule)
	 */
	public boolean applyRule(IProofRule proofRule) {
		ProofRule rule = (ProofRule) proofRule;
		// force pruning to avoid losing child proofs
		if (this.children != null) return false;
		if (this.rule != null) return false;
		IProverSequent[] antecedents = rule.apply(this.sequent);
		if (antecedents == null) return false;
		final int length = antecedents.length;
		ProofTreeNode[] newChildren = new ProofTreeNode[length];
		for (int i = 0; i < length; i++) {
			newChildren[i] = new ProofTreeNode(this, antecedents[i]);
		}
		setRule(rule);
		setChildren(newChildren);
		if (length == 0)
			this.setClosed();
		assert classInvariant();
		fireDeltas();
		return true;
	}

	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#graft(org.eventb.core.prover.IProofTree)
	 */
	public boolean graft(IProofTree tree) {
		//	force pruning to avoid losing child proofs
		if (this.children != null) return false;
		if (this.rule != null) return false;
		if (! ProverLib.deepEquals(this.sequent,tree.getSequent())) return false;
		ProofTreeNode treeRoot = (ProofTreeNode)tree.getRoot();
		ProofTreeNode[] treeChildren = treeRoot.getChildNodes();
		ProofRule treeRule = (ProofRule) treeRoot.getRule();
		boolean treeClosed = treeRoot.isClosed();
		
		// Disconnect treeChildren from treeRoot
		treeRoot.setRule(null);
		treeRoot.setChildren(null);
		treeRoot.reopen();
		// treeRoot.assertClassInvariant();
		assert classInvariant();
		treeRoot.fireDeltas();
		// Connect treeChildren to this node
		for (ProofTreeNode treeChild : treeChildren)
			treeChild.parent = this;
		this.setRule(treeRule);
		this.setChildren(treeChildren);
		if (treeClosed)
			this.setClosed();
		assert classInvariant();
		fireDeltas();
		return true;
	}
	
	/**
	 * Calculates the minimum confidence value of all of this node's children
	 * 
	 * @return The minimum confidence value of all of this node's children
	 */
	private int minChildConf() {
		if (children == null)
			return IConfidence.PENDING;
		int minChildConf = IConfidence.DISCHARGED_MAX;
		for (ProofTreeNode child: children) {
			if (child.confidence == IConfidence.PENDING)
				return IConfidence.PENDING;
			if (child.confidence < minChildConf)
				minChildConf = child.confidence;
		}
		return minChildConf;
	}
	
	/**
	 * Report children change to delta processor.
	 */
	private void childrenChanged() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.childrenChanged(this);
		}
	}
	
	/**
	 * Fire deltas for the associated proof tree.
	 */
	private void fireDeltas() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.fireDeltas();
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getChildren()
	 */
	public ProofTreeNode[] getChildNodes() {
		if (children == null)
			return NO_NODE;
		final int length = children.length;
		if (length == 0)
			return NO_NODE;
		ProofTreeNode[] result = new ProofTreeNode[length];
		System.arraycopy(children, 0, result, 0, length);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getFirstOpenDescendant()
	 */
	public IProofTreeNode getFirstOpenDescendant() {
		if (isClosed())
			return null;
		if (isOpen())
			return this;
		for (ProofTreeNode child : children) {
			IProofTreeNode result = child.getFirstOpenDescendant();
			if (result != null)
				return result;
		}
		return null;
	}
	
	public IProofTreeNode getNextOpenNode() {
		return getNextNode(true, new IProofTreeNodeFilter() {

			public boolean select(IProofTreeNode node) {
				return node.isOpen();
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getOpenDescendants()
	 */
	public IProofTreeNode[] getOpenDescendants() {
		if (isClosed())
			return NO_NODE;
		if (isOpen())
			return new IProofTreeNode[] { this };
		
		// Pending node
		List<IProofTreeNode> list = new ArrayList<IProofTreeNode>();
		appendOpenDescendants(list);
		final int length = list.size();
		if (length == 0)
			return NO_NODE;
		IProofTreeNode[] result = new IProofTreeNode[length];
		return list.toArray(result);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getParent()
	 */
	public IProofTreeNode getParent() {
		return this.parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getProofTree()
	 */
	public ProofTree getProofTree() {
		ProofTreeNode node = this;
		while (node.parent != null) {
			node = node.parent;
		}
		return node.tree;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getRule()
	 */
	public IProofRule getRule() {
		return this.rule;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getSequent()
	 */
	public IProverSequent getSequent() {
		return this.sequent;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#hasChildren()
	 */
	public boolean hasChildren() {
		return this.children != null && this.children.length != 0;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#isClosed()
	 */
	public boolean isClosed() {
		return confidence != IConfidence.PENDING;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#isOpen()
	 */
	public boolean isOpen() {
		return this.children == null;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#pruneChildren()
	 */
	public ProofTree[] pruneChildren() {
		if (isOpen())
			return null;
		this.setRule(null);
		
		ProofTree[] prunedChildSubtrees = new ProofTree[this.children.length];
		// Detach all children from this proof tree.
		// Add each child to the result.		
		for (int i = 0; i < children.length; i++) {
			children[i].parent = null;
			prunedChildSubtrees[i] = new ProofTree(children[i]);
		}
		setChildren(null);
		reopen();
		assert classInvariant();
		fireDeltas();
		return prunedChildSubtrees;
	}
	
	/**
	 * Reopen this node, setting the status of all ancestors to pending
	 */
	private void reopen() {
		ProofTreeNode node = this;
		while (node != null && ! ProverLib.isPending(node.confidence)) {
			node.confidence = IConfidence.PENDING;
			node.confidenceChanged();
			node = node.parent;
		}
	}

	private void setChildren(ProofTreeNode[] newChildren) {
		this.children = newChildren;
		childrenChanged();
	}
	
	private void setRule(ProofRule newRule) {
		this.rule = newRule;
		ruleChanged();
	}

	protected void setProofTree(ProofTree tree) {
		this.tree = tree;	
	}
	
	/**
	 * This node has just been closed. Update its status, as well as its
	 * ancestors'.
	 */
	private void setClosed() {
		this.confidence = this.rule.getConfidence();
		assert (ProverLib.isValid(this.confidence) && (! ProverLib.isPending(this.confidence)));
		confidenceChanged();
		ProofTreeNode node = this.parent;
		if (node == null) return;
		int nodeMinChildrenConf = node.minChildConf();
		while (! ProverLib.isPending(nodeMinChildrenConf))
		{
				node.confidence = node.rule.getConfidence();
				if (node.confidence > nodeMinChildrenConf)
					node.confidence = nodeMinChildrenConf;
				node.confidenceChanged();
				node = node.parent;
				if (node == null) return;
				nodeMinChildrenConf = node.minChildConf();
		}
	}
	
	/**
	 * Report a rule change to delta processor.
	 */
	private void ruleChanged() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.ruleChanged(this);
		}
	}
	
	/**
	 *  Report a confidence level change to delta processor.
	 */
	private void confidenceChanged() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.confidenceChanged(this);
		}
	}
	
	/**
	 * Report a comment change to the delta processor.
	 */
	private void commentChanged() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.commentChanged(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#setComment(java.lang.String)
	 */
	public void setComment(String comment) {
		assert comment != null;
		this.comment = comment;
		this.commentChanged();
		this.fireDeltas();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getComment()
	 */
	public String getComment() {
		return this.comment;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getUsedHypotheses()
	 */
	public Set<Predicate> getNeededHypotheses(){
		if (this.rule == null) return new HashSet<Predicate>();
		return rule.getNeededHyps();
	}

	
	public ProofDependenciesBuilder computeProofDeps(){
		if (isOpen()) return new ProofDependenciesBuilder();
		ProofDependenciesBuilder[] childProofDeps = new ProofDependenciesBuilder[children.length];
		for (int i = 0; i < children.length; i++) {
			childProofDeps[i] = children[i].computeProofDeps();
		}
		return ((ProofRule)rule).processDeps(childProofDeps);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getConfidence()
	 */
	public int getConfidence() {
		return this.confidence;
	}
	
	/**
	 * Checks that the class invariant for the proof tree node and its children holds.
	 * 
	 * Used for runtime assertion check for the proof tree data structure.
	 * 
	 * @return <code>true</code> iff the the class invariant for the proof tree node 
	 * 			and its children holds.
	 */
	private boolean classInvariant() {
		if (sequent == null) return false;
		if ((rule == null) && (children != null)) return false;
		if (rule != null) {
			if (children == null) return false;
			IProverSequent[] anticidents = ((ProofRule) rule).apply(sequent);
			if (anticidents == null) return false;
			if (children.length != anticidents.length) return false;
			for (int i=0;i<anticidents.length;i++)
			{
				if  (! ProverLib.deepEquals (this.children[i].sequent,anticidents[i])) return false;
				if (children[i].parent != this) return false;
				if (! children[i].classInvariant()) return false;
			}
		}
		if (isClosed() != (getOpenDescendants().length == 0)) return false;
		if ((parent == null) == (tree == null)) return false;
		if (confidence != computedConfidence()) return false;
		return true;
	}
	

	/**
	 * Computes the confidence level of the proof tree node recursively.
	 * 
	 * <p>
	 * Used in the assertion check to check if the actual and cached confidence level
	 * are the same.
	 * </p>
	 * 
	 * @return The computed confidence level
	 */
	private int computedConfidence() {
		if (rule == null) return IConfidence.PENDING;
		int minConfidence = rule.getConfidence();
		for (ProofTreeNode child : children) {
			int childConfidence = child.getConfidence();
			if (childConfidence < minConfidence)
				minConfidence = childConfidence;
		}
		return minConfidence;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		toStringHelper("", false, str);
		str.append("\n");
		if (this.isClosed()) {
			str.append("No pending subgoals!\n");
		} else {
			str.append(this.getOpenDescendants().length);
			str.append(" pending subgoals\n");
		}
		return str.toString();
	}

	private void toStringHelper(String indent, boolean justBranched,
			StringBuilder str) {
		
		str.append(indent);
		str.append(this.rootToString());
		if (this.isOpen()) {
			str.append(" =>");
			return;
		}
		if (! this.hasChildren()) {
			str.append(" <>");
			return;
		}
		final String childIndent = 
			indent + (justBranched || this.children.length > 1 ? "    " : " ");
		for (ProofTreeNode child : this.children) {
			str.append("\n");
			child.toStringHelper(childIndent, this.children.length > 1, str);
		}
	}

	private String rootToString() {
		final String ruleStr = rule == null ? "-" : rule.getDisplayName();
		return getSequent().toString().replace("\n", " ") + "\t\t" + ruleStr;
	}

	private Iterator<IProofTreeNode> iterator(boolean rootIncluded) {
		return new ProofTreeIterator(this, rootIncluded);
	}

	public IProofTreeNode getNextNode(boolean rootIncluded,
			IProofTreeNodeFilter filter) {
		final Iterator<IProofTreeNode> iterator = iterator(rootIncluded);
		while (iterator.hasNext()) {
			final IProofTreeNode node = iterator.next();
			if (filter.select(node)) {
				return node;
			}
		}
		// Reconsider the root node if no node matches the filter.
		if (!rootIncluded && filter.select(this)) {
			return this;
		}
		return null;
	}

	public FormulaFactory getFormulaFactory() {
		return sequent.getFormulaFactory();
	}
	
}
