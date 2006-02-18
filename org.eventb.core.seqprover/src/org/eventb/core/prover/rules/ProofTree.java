package org.eventb.core.prover.rules;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;

public final class ProofTree {
	
	private final ProofTree parent;
	private final IProverSequent root;
	private Rule rule;
	private ProofTree[] children;

	public ProofTree(ProofTree parent, IProverSequent S){
		this.parent = parent;
		this.root = S;
		this.rule = null;
		this.children = null;
		this.classInvariantAssertions();
	}
		
	
	public final void pruneChildren(){
		this.rule = null;
		this.children = null;
		this.classInvariantAssertions();
	}
	
	
	public final boolean applyRule(Rule rule){
		// force resetting to avoid losing child proofs
		assert (this.children == null);
		if (! rule.isApplicable(this.root)) return false;
		IProverSequent[] anticidents = rule.apply(this.root);
		assert (anticidents != null);
		this.rule = rule;
		this.children = new ProofTree[anticidents.length];
		for (int i=0;i<anticidents.length;i++)
			this.children[i] = new ProofTree(this, anticidents[i]);
		this.classInvariantAssertions();
		return true;
	}
	
	private final void classInvariantAssertions(){
		assert (this.root != null);
		assert ((this.rule == null) & (this.children == null)) |
				((this.rule != null) & (this.children != null));
		if (this.rule != null){
			assert rule.isApplicable(this.root);
			IProverSequent[] anticidents = rule.apply(this.root);
			assert (anticidents != null);
			assert (this.children.length == anticidents.length);
			for (int i=0;i<anticidents.length;i++)
			{
				// System.out.println(this.children[i].root);
				// System.out.println(anticidents[i]);
				assert (Lib.identical (this.children[i].root,anticidents[i]));
				this.children[i].classInvariantAssertions();
			}
		}
	}
	

	
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#getRootSeq()
	 */
	public final IProverSequent getRootSeq(){
		return this.root;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#getRootRule()
	 */
	public final Rule getRootRule(){
		return this.rule;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#getChildren()
	 */
	public final ProofTree[] getChildren(){
		return this.children;
	}
	
	/**
	 * Returns the parent of this proof tree node, or <code>null</code> if
	 * this node is top-level.
	 * 
	 * @return the parent of this node or <code>null</code>
	 */
	public final ProofTree getParent() {
		return this.parent;
	}
	
	// A tree node can either (exclusive):
	// * be open (no rule applied, extendable)
	// * have children (rule applied, at least one child)
	// * be closed (rule applied, no children)
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#rootHasChildren()
	 */
	public final boolean rootHasChildren(){
		return (this.children != null && this.children.length != 0);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#rootIsOpen()
	 */
	public final boolean rootIsOpen(){
		return (this.children == null);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#rootIsClosed()
	 */
	public boolean rootIsClosed() {
		return (this.children.length == 0);
	}

	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#pendingSubgoals()
	 */
	public final List<ProofTree> pendingSubgoals(){
		List<ProofTree> pendingSubgoals = new ArrayList<ProofTree>();
		if (this.children == null){
			pendingSubgoals.add(this);
			return pendingSubgoals;
		}
		else{
			for (ProofTree child : this.children)
				pendingSubgoals.addAll(child.pendingSubgoals());
			return pendingSubgoals;
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.prover.rules.IProofTree#isClosed()
	 */
	public final boolean isClosed(){
		return (this.pendingSubgoals().size() == 0);
	}
	
	
	public String rootToString(){
		String ruleStr;
		if (this.rule == null) { ruleStr = "-"; }
		else { ruleStr = this.rule.name(); };
		return getRootSeq().toString().replace("\n"," ") + "\t\t" + ruleStr;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		toStringHelper(0,false,str);
		str.append("\n");
		if (this.isClosed()) str.append("No pending subgoals!\n");
		else {
			str.append(this.pendingSubgoals().size());
			str.append(" pending subgoals\n");
		}
		return str.toString();
	}
	
	private void toStringHelper(int depth,boolean justBranched,StringBuilder str){
		String indent = repeat(" ",depth);
		// String indent1 = repeat("     ",depth+1);
		str.append(indent+this.rootToString());
		if (this.rootIsOpen()) {
			str.append(" =>");
			return;
		}
		if (this.rootIsClosed()) {
			str.append(" <>");
			return;
		}
		int offset = justBranched | this.children.length > 1 ? 4 : 1;
		for (ProofTree child : this.children){
			str.append("\n");
			{child.toStringHelper(depth+offset,this.children.length > 1,str);}	
		}
		return;
	}


	private String repeat(String src, int repeat)
	{
		StringBuilder buf=new StringBuilder();
		for (int i=0;i<repeat;i++) {
			buf.append(src);
		}
		return buf.toString();
	}	
	
	
}
