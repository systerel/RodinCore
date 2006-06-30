package org.eventb.core.prover;

import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.prover.rules.ProofRule;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Common protocol for a proof tree node.
 * <p>
 * Each node bears a proof sequent (see {@link IProofSequent}) and can be in
 * one of the following states:
 * <ul>
 * <li>open - no rule has been applied to this node and the validity of its
 * associated sequent is unknown.</li>
 * <li>pending - a rule has been applied to this node, but some of its children
 * have not been discharged yet. This corresponds to a proof attempt which is
 * not yet finished.</li>
 * <li>discharged - a rule has been applied to this node and all its children
 * have been discharged. The associated sequent has been proved valid.</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IProofTreeNode {

	/**
	 * Applies the given rule to this node.
	 * 
	 * @param rule
	 *            the rule to apply to this node
	 * @return <code>true</code> iff the application of the rule succeeded
	 */
	boolean applyRule(ProofRule rule);

	/**
	 * Returns the children of this node.
	 * <p>
	 * This method always returns an array, even if this node is a leaf node
	 * (that is with no rule applied to it). It never returns <code>null</code>.
	 * </p>
	 * 
	 * @return an array of the children of this node
	 * @see #hasChildren()
	 */
	IProofTreeNode[] getChildren();

	/**
	 * Returns the first open descendant of this node.
	 * <p>
	 * The first open descendant is selected quite arbitrarily by traversing the
	 * proof subtree rooted at this node depth-first.
	 * </p>
	 * <p>
	 * This node is considered a descendant of itself. Hence, calling this
	 * method on an open node returns the open node itself.
	 * </p>
	 * 
	 * @return an open descendant of this node or <code>null</code> if this
	 *         node has been discharged
	 * @see #getOpenDescendants()
	 */
	IProofTreeNode getFirstOpenDescendant();

	/**
	 * Returns all open descendants of this node.
	 * <p>
	 * This method always returns an array, even if this node has been
	 * discharged (that is has no open descendants). It never returns
	 * <code>null</code>.
	 * </p>
	 * <p>
	 * This node is considered a descendant of itself. Hence, calling this
	 * method on an open node returns an array containg one element: the open
	 * node itself.
	 * </p>
	 * <p>
	 * Clients should not use this method if they're interested only in an
	 * arbitrary open descendant. In that latter case, clients should call
	 * {@link #getFirstOpenDescendant()}.
	 * </p>
	 * 
	 * @return an array of all open descendants of this node
	 * @see #getFirstOpenDescendant()
	 */
	IProofTreeNode[] getOpenDescendants();

	/**
	 * Returns the parent of this proof tree node, or <code>null</code> if
	 * this node is the root of its proof tree.
	 * 
	 * @return the parent of this node or <code>null</code>
	 */
	IProofTreeNode getParent();

	/**
	 * Returns the proof tree to which this node belongs.
	 * <p>
	 * Note that a node that has been pruned out of the proof tree doesn't
	 * belong anymore to it. Hence, this method will return <code>null</code>
	 * if this node is not connected anymore to a proof tree.
	 * </p>
	 * 
	 * @return the proof tree of this node or <code>null</code>
	 */
	IProofTree getProofTree();

	/**
	 * Returns the rule applied to this node.
	 * 
	 * @return the rule applied to this node or <code>null</code> is this node
	 *         is a leaf node
	 */
	IProofRule getRule();

	/**
	 * Returns the proof sequent associated to this node.
	 * 
	 * @return the proof sequent of this node
	 */
	IProverSequent getSequent();

	/**
	 * Tells whether this node has any children.
	 * <p>
	 * Returns identical information to <code>getChildren().length != 0</code>,
	 * but is implemented in a more efficient way.
	 * </p>
	 * 
	 * @return <code>true</code> iff this node has some child
	 * @see #getChildren()
	 */
	boolean hasChildren();

	/**
	 * Tells whether this node is discharged.
	 * 
	 * @return <code>true</code> iff this node is discharged
	 */
	boolean isDischarged();

	/**
	 * Tells whether this node is open.
	 * 
	 * @return <code>true</code> iff this node is open (no rule applied to it)
	 */
	boolean isOpen();

	/**
	 * Prune the children of this node, undoing the result of applying a rule to
	 * this node. Has no effect if no rule was currently applied to this node.
	 * 
	 * @return the child subtrees that have been pruned, or <code>null</code> if no rule 
	 * was applied to this node. 
	 */
	IProofTree[] pruneChildren();
	
	
	/**
	 * Grafts a proof tree to this proof node if it is open, and its sequent is identical
	 * to the root sequent of the given proof tree. 
	 * 
	 * A successful graft has the effect of pruning the input proof tree.
	 * 
	 * @return <code>true</code> iff the operation suceeded. 
	 */
	boolean graft(IProofTree tree);
	
	
	/**
	 * Sets the comment field of the current proof tree node.
	 * 
	 * @param comment
	 * 	 The new comment for the current proof tree node
	 * 
	 * @see #getComment()
	 */
	void setComment(String comment);
	
	/**
	 * Returns the comment field of the current proof tree node.
	 * 
	 * @return The comment field of the current proof tree node.
	 * @see #setComment()
	 */
	String getComment();
	
	Set<Hypothesis> getUsedHypotheses();
	Set<FreeIdentifier> getUsedFreeIdents();
	
	int getConfidence();
	
}