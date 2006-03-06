package org.eventb.core.prover.rules;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;

public final class ProofTreeNode implements IProofTreeNode {
	
	private static final IProofTreeNode[] NO_NODE = new IProofTreeNode[0];
	
	private ProofTreeNode[] children;
	// Cache of discharged status
	private boolean discharged;
	private ProofTreeNode parent;
	private ProofRule rule;
	private final IProverSequent sequent;
	
	// Tree to which this node belongs. This field is only set for a root node,
	// so that it's easy to remove a whole subtree from the tree. Always use
	// #getProofTree() to access this information.
	private final ProofTree tree;

	// Creates a root node of a proof tree
	public ProofTreeNode(ProofTree tree, IProverSequent sequent) {
		assert tree != null;
		this.tree = tree;
		this.parent = null;
		this.sequent = sequent;
		this.rule = null;
		this.children = null;
		this.discharged = false;
		this.checkClassInvariant();
	}
	
	// Creates an internal node of a proof tree
	private ProofTreeNode(ProofTreeNode parent, IProverSequent sequent) {
		assert parent != null;
		this.tree = null;
		this.parent = parent;
		this.sequent = sequent;
		this.rule = null;
		this.children = null;
		this.discharged = false;
		this.checkClassInvariant();
	}
	
	// Append the open descendant of this node to the given list.
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
	public boolean applyRule(ProofRule rule) {
		// force resetting to avoid losing child proofs
		assert (this.children == null);
		// if (! rule.isApplicable(this.sequent)) return false;
		IProverSequent[] anticidents = rule.apply(this.sequent);
		if (anticidents == null) return false;
		assert (anticidents != null);
		this.rule = rule;
		final int length = anticidents.length;
		ProofTreeNode[] newChildren = new ProofTreeNode[length];
		for (int i = 0; i < length; i++) {
			newChildren[i] = new ProofTreeNode(this, anticidents[i]);
		}
		setChildren(newChildren);
		if (length == 0)
			this.setDischarged();
		this.checkClassInvariant();
		fireDeltas();
		return true;
	}
	
	private boolean areAllChildrenDischarged() {
		if (children == null)
			return false;
		for (ProofTreeNode child: children) {
			if (! child.discharged)
				return false;
		}
		return true;
	}
	
	private void checkClassInvariant() {
		assert (this.sequent != null);
		assert ((this.rule == null) & (this.children == null)) |
				((this.rule != null) & (this.children != null));
		if (this.rule != null) {
			// assert rule.isApplicable(this.sequent);
			IProverSequent[] anticidents = rule.apply(this.sequent);
			assert (anticidents != null);
			assert (this.children.length == anticidents.length);
			for (int i=0;i<anticidents.length;i++)
			{
				// System.out.println(this.children[i].root);
				// System.out.println(anticidents[i]);
				assert (Lib.identical (this.children[i].sequent,anticidents[i]));
				this.children[i].checkClassInvariant();
			}
		}
		assert this.discharged == (getOpenDescendants().length == 0);
	}
	
	// Report children change to delta processor.
	private void childrenChanged() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.childrenChanged(this);
		}
	}
	
	private void fireDeltas() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.fireDeltas();
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getChildren()
	 */
	public IProofTreeNode[] getChildren() {
		if (children == null)
			return NO_NODE;
		final int length = children.length;
		if (length == 0)
			return NO_NODE;
		IProofTreeNode[] result = new IProofTreeNode[length];
		System.arraycopy(children, 0, result, 0, length);
		return result;
	}
	
	public IProofTreeNode getFirstOpenDescendant() {
		if (isDischarged())
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
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTreeNode#getOpenDescendants()
	 */
	public IProofTreeNode[] getOpenDescendants() {
		if (isDischarged())
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
	 * @see org.eventb.core.prover.IProofTreeNode#isDischarged()
	 */
	public boolean isDischarged() {
		return discharged;
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
	public void pruneChildren() {
		if (isOpen())
			return;
		this.rule = null;
		// Detach all children from this proof tree.
		for (ProofTreeNode child: this.children) {
			child.parent = null;
		}
		setChildren(null);
		reopen();
		checkClassInvariant();
		fireDeltas();
	}
	
	// Reopen this node, setting the status of all ancestors to non-discharged
	private void reopen() {
		ProofTreeNode node = this;
		while (node != null && node.discharged) {
			node.discharged = false;
			node.statusChanged();
			node = node.parent;
		}
	}

	private String rootToString() {
		String ruleStr;
		if (this.rule == null) { ruleStr = "-"; }
		else { ruleStr = this.rule.getName(); };
		return getSequent().toString().replace("\n"," ") + "\t\t" + ruleStr;
	}

	private void setChildren(ProofTreeNode[] newChildren) {
		this.children = newChildren;
		childrenChanged();
	}

	// This node has just been discharged. Update its status, as well as its
	// ancestors'.
	private void setDischarged() {
		this.discharged = true;
		statusChanged();
		ProofTreeNode node = this.parent;
		while (node != null && node.areAllChildrenDischarged()) {
			node.discharged = true;
			node.statusChanged();
			node = node.parent;
		}
	}	

	// Report status change to delta processor.
	private void statusChanged() {
		ProofTree tree = getProofTree();
		if (tree != null) {
			tree.deltaProcessor.statusChanged(this);
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		toStringHelper("", false, str);
		str.append("\n");
		if (this.isDischarged()) {
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
		return;
	}
	
}
