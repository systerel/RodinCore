package org.eventb.core.prover.rules;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;

public final class ProofTreeNode implements IProofTreeNode {
	
	private static final IProofTreeNode[] NO_NODE = new IProofTreeNode[0];
	
	private ProofTreeNode[] children;
	private ProofTreeNode parent;
	private final IProverSequent sequent;
	private final ProofTree tree;
	private ProofRule rule;

	// Creates a root node of a proof tree
	public ProofTreeNode(ProofTree tree, IProverSequent sequent) {
		assert tree != null;
		this.tree = tree;
		this.parent = null;
		this.sequent = sequent;
		this.rule = null;
		this.children = null;
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
		this.checkClassInvariant();
	}
	
	// Append the open descendant of this node to the given list.
	public void appendOpenDescendants(List<IProofTreeNode> list) {
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
		if (! rule.isApplicable(this.sequent)) return false;
		IProverSequent[] anticidents = rule.apply(this.sequent);
		assert (anticidents != null);
		this.rule = rule;
		this.children = new ProofTreeNode[anticidents.length];
		for (int i=0;i<anticidents.length;i++)
			this.children[i] = new ProofTreeNode(this, anticidents[i]);
		this.checkClassInvariant();
		return true;
	}
	
	private void checkClassInvariant() {
		assert (this.sequent != null);
		assert ((this.rule == null) & (this.children == null)) |
				((this.rule != null) & (this.children != null));
		if (this.rule != null) {
			assert rule.isApplicable(this.sequent);
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
		if (isOpen())
			return this;
		else for (ProofTreeNode child : children) {
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
		// TODO add fast return if isDischarged is true after caching it
		if (isOpen())
			return new IProofTreeNode[] { this };
		
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
	public final boolean isDischarged() {
		// TODO cache "discharged" information
		return getFirstOpenDescendant() == null;
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
		this.rule = null;
		// Detach all children from this proof tree.
		for (ProofTreeNode child: this.children) {
			child.parent = null;
		}
		this.children = null;
		this.checkClassInvariant();
	}
	
	private String repeat(String src, int repeat) {
		StringBuilder buf=new StringBuilder();
		for (int i=0;i<repeat;i++) {
			buf.append(src);
		}
		return buf.toString();
	}
	
	private String rootToString() {
		String ruleStr;
		if (this.rule == null) { ruleStr = "-"; }
		else { ruleStr = this.rule.getName(); };
		return getSequent().toString().replace("\n"," ") + "\t\t" + ruleStr;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		toStringHelper(0,false,str);
		str.append("\n");
		if (this.isDischarged()) str.append("No pending subgoals!\n");
		else {
			str.append(this.getOpenDescendants().length);
			str.append(" pending subgoals\n");
		}
		return str.toString();
	}

	private void toStringHelper(int depth, boolean justBranched,
			StringBuilder str) {
		
		String indent = repeat(" ", depth);
		// String indent1 = repeat(" ",depth+1);
		str.append(indent + this.rootToString());
		if (this.isOpen()) {
			str.append(" =>");
			return;
		}
		if (! this.hasChildren()) {
			str.append(" <>");
			return;
		}
		int offset = justBranched | this.children.length > 1 ? 4 : 1;
		for (ProofTreeNode child : this.children) {
			str.append("\n");
			child.toStringHelper(depth + offset, this.children.length > 1, str);
		}
		return;
	}	
	
}
