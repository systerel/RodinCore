/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored for using the Proof Manager API
 *     Systerel - added proof simplification on commit
 *     Systerel - removed post-tactics call when saving
 *     Systerel - got formula factory from proof attempt
 *     Systerel - added more getters
 *     Systerel - added simplify proof preference
 *******************************************************************************/
package org.eventb.internal.core.pm;

import static java.util.Collections.emptyList;
import static org.eventb.core.seqprover.proofBuilder.ProofBuilder.rebuild;
import static org.eventb.internal.core.preferences.PreferenceUtils.getSimplifyProofPref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.ProofMonitor;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class keeps the proof state for one proof obligation including
 *         the proof tree, the current proof node, the set of cached and
 *         searched hypotheses.
 */
public class ProofState implements IProofState {

	private static final String US = "UserSupport";
	
	private static final UserSupportManager usm = UserSupportManager.getDefault();

	// The PR sequent associated with this proof obligation.
	final IPSStatus status;
	
	final String poName;

	// The current proof attempt, can be null when it is not initialized.
	IProofAttempt pa;

	// The current proof tree, this might be different from the proof tree in
	// the disk, can be null when it is not initialised.
	IProofTree pt;

	// The current proof node, can be null when the proof tree is uninitialised.
	IProofTreeNode current;

	// The set of cached hypotheses.
	private Collection<Predicate> cached;

	// The set of searched hypotheses.
	private Collection<Predicate> searched;

	// The dirty flag to indicate if there are some unsaved changes with this
	// proof obligation.
	private boolean dirty;

	DeltaProcessor deltaProcessor;

	final UserSupport userSupport;
	
	public ProofState(UserSupport userSupport, IPSStatus ps) {
		assert userSupport != null;
		assert ps != null;
		this.userSupport = userSupport;
		this.status = ps;
		this.poName = status.getElementName();

		cached = new ArrayList<Predicate>();
		searched = new ArrayList<Predicate>();
		deltaProcessor = usm.getDeltaProcessor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#loadProofTree(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void loadProofTree(final IProgressMonitor monitor)
			throws RodinDBException {
		usm.run(new Runnable() {

			@Override
			public void run() {
				try {
					createFreshProofAttempt(monitor);
				} catch (RodinDBException e) {
					throw new IllegalStateException(e);
				}
				
				// Get the proof skeleton and rebuild the tree
				initializeProofTree(monitor);
				pt.addChangeListener(ProofState.this);
				
				ProofState.this.newProofTree();
				
				if (!pt.getRoot().isClosed() && !userSupport.isSaving()) {
					final IAutoPostTacticManager manager = EventBPlugin
							.getAutoPostTacticManager();
					// Run Post tactic at the root of the tree
					final IAutoTacticPreference postTac = manager
							.getPostTacticPreference();
					if (postTac.isEnabled()) {
						if (pa != null) {
							final IEventBRoot root = pa.getComponent()
									.getPORoot();
							final ITactic postTactic = manager
									.getSelectedPostTactics(root);
							postTactic.apply(pt.getRoot(), new ProofMonitor(
									monitor));
						}
					}
				}
				selectInitialSubgoal();

				ProofState.this.setCached(new HashSet<Predicate>());
				ProofState.this.setSearched(new HashSet<Predicate>());
				deltaProcessor.informationChanged(userSupport,
						new UserSupportInformation("Proof Tree is reloaded",
								IUserSupportInformation.MAX_PRIORITY));
			}

		});
	}

	void createFreshProofAttempt(IProgressMonitor monitor)
			throws RodinDBException {
		// Dispose the previous proof attempt
		if (pa != null) {
			pa.getProofTree().removeChangeListener(this);
			pa.dispose();
		}

		// Construct the proof tree from the PO file.
		final IProofComponent pc = userSupport.getProofComponent();
		pa = pc.createProofAttempt(poName, US, monitor);
		pt = pa.getProofTree();
	}

	/*
	 * Initializes the proof tree with the proof from the proof file. The proof
	 * tree is considered dirty if either the rebuild failed or the proof was
	 * marked broken in the status file.
	 */
	void initializeProofTree(IProgressMonitor monitor) {
		setDirty(false);
		final IProofSkeleton skeleton = readProofSkeleton(monitor);
		if (skeleton != null) {
			final IProofTreeNode root = pt.getRoot();
			final ProofMonitor pm = new ProofMonitor(monitor);
			if (!rebuild(root, skeleton, pm))
				setDirty(true);
		}
		if (hasBrokenStatus())
			setDirty(true);
	}

	/*
	 * Returns the proof skeleton if readable and well-formed, null otherwise.
	 */
	private IProofSkeleton readProofSkeleton(IProgressMonitor pm) {
		final IProofComponent pc = pa.getComponent();
		final FormulaFactory ff = pa.getFormulaFactory();
		try {
			return pc.getProofSkeleton(poName, ff, pm);
		} catch (RodinDBException e) {
			Util.log(e, "while reading proof " + poName);
			return null;
		}
	}

	private boolean hasBrokenStatus() {
		try {
			return status.isBroken();
		} catch (RodinDBException e) {
			Util.log(e, "when reading status for " + poName);
			return true;
		}
	}

	protected void newProofTree() {
		deltaProcessor.newProofTree(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isClosed()
	 */
	@Override
	public boolean isClosed() throws RodinDBException {
		if (pt != null)
			return pt.isClosed();

		return isSequentDischarged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getPRSequent()
	 */
	@Override
	public IPSStatus getPSStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getProofTree()
	 */
	@Override
	public IProofTree getProofTree() {
		return pt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getCurrentNode()
	 */
	@Override
	public IProofTreeNode getCurrentNode() {
		if (pt != null)
			return current;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setCurrentNode(org.eventb.core.seqprover.IProofTreeNode)
	 */
	@Override
	public void setCurrentNode(final IProofTreeNode newNode) {
		if (newNode.getProofTree() != pt)
			return;

		usm.run(new Runnable() {
			@Override
			public void run() {
				if (current != newNode) {
					current = newNode;
					// Fire delta
					deltaProcessor.setNewCurrentNode(userSupport,
							ProofState.this);
					deltaProcessor.informationChanged(userSupport,
							new UserSupportInformation(
									"Select a new proof node",
									IUserSupportInformation.MIN_PRIORITY));
				} else {
					deltaProcessor.informationChanged(userSupport,
							new UserSupportInformation("Not a new proof node",
									IUserSupportInformation.MIN_PRIORITY));
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getNextPendingSubgoal(org.eventb.core.seqprover.IProofTreeNode)
	 */
	@Override
	public IProofTreeNode getNextPendingSubgoal(IProofTreeNode node) {
		if (node.getProofTree() != pt) {
			node = pt.getRoot();
		}
		return node.getNextNode(true, new IProofTreeNodeFilter() {
			@Override
			public boolean select(IProofTreeNode n) {
				return n.isOpen();
			}
			
		});
	}

	/**
	 * Get the next reviewed subgoal from the input node. This method does not
	 * change the current proof tree node.
	 * <p>
	 * 
	 * @param node
	 *            a proof tree node
	 * @return a next reviewed proof tree node if any. Return <code>null</code>
	 *         otherwise.
	 */
	protected IProofTreeNode getNextReviewedSubgoal(IProofTreeNode node) {
		if (node.getProofTree() != pt) {
			node = pt.getRoot();
		}
		return node.getNextNode(true, new IProofTreeNodeFilter() {
			@Override
			public boolean select(IProofTreeNode n) {
				int confidence = n.getConfidence();
				return (confidence > IConfidence.PENDING && confidence <= IConfidence.REVIEWED_MAX);
			}
			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getNextPendingSubgoal()
	 */
	IProofTreeNode getNextPendingSubgoal() {
		return getNextPendingSubgoal(pt.getRoot());
	}

	@Override
	public Iterable<Predicate> getSelected() {
		final IProverSequent sequent = getCurrentSequent();
		if (sequent == null) {
			return emptyList();
		}
		return sequent.selectedHypIterable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#addAllToCached(java.util.Collection)
	 */
	@Override
	public void addAllToCached(Collection<Predicate> hyps) {
		cached.addAll(hyps);
		deltaProcessor.cacheChanged(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setSearched(java.util.Collection)
	 */
	protected void setCached(Collection<Predicate> cached) {
		this.cached = cached;
		deltaProcessor.cacheChanged(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#removeAllFromCached(java.util.Collection)
	 */
	@Override
	public void removeAllFromCached(Collection<Predicate> hyps) {
		cached.removeAll(hyps);
		deltaProcessor.cacheChanged(userSupport, this);
		deltaProcessor.informationChanged(userSupport,
				new UserSupportInformation(
						"Removed hypotheses from cache",
						IUserSupportInformation.MAX_PRIORITY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getCached()
	 */
	@Override
	public Collection<Predicate> getCached() {
		return cached;
	}

	@Override
	public Collection<Predicate> filterHypotheses(Collection<Predicate> preds) {
		final IProverSequent sequent = getCurrentSequent();
		if (sequent == null) {
			return emptyList();
		}
		final Collection<Predicate> result = new ArrayList<Predicate>();
		for (final Predicate pred : preds) {
			if (sequent.containsHypothesis(pred)) {
				result.add(pred);
			}
		}
		return result;
	}

	private IProverSequent getCurrentSequent() {
		final IProofTreeNode node = getCurrentNode();
		if (node == null) {
			return null;
		}
		return node.getSequent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#removeAllFromSearched(java.util.Collection)
	 */
	@Override
	public void removeAllFromSearched(Collection<Predicate> hyps) {
		searched.removeAll(hyps);
		deltaProcessor.searchChanged(userSupport, this);
		deltaProcessor.informationChanged(userSupport,
				new UserSupportInformation(
						"Removed hypotheses from search",
						IUserSupportInformation.MAX_PRIORITY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getSearched()
	 */
	@Override
	public Collection<Predicate> getSearched() {
		return searched;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setSearched(java.util.Collection)
	 */
	@Override
	public void setSearched(Collection<Predicate> searched) {
		this.searched = searched;
		deltaProcessor.searchChanged(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setProofTree(IProgressMonitor monitor) throws RodinDBException {
		if (UserSupportUtils.DEBUG)
			UserSupportUtils.debug("Saving: " + pa.getName());
		if (pa != null && !pa.isDisposed()) {
			pa.commit(true, getSimplifyProofPref(), monitor);
		}
		setDirty(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setDirty(boolean)
	 */
	@Override
	public void setDirty(boolean dirty) {
		if (this.dirty != dirty)
			this.dirty = dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProofState))
			return false;
		else {
			IProofState proofState = (IProofState) obj;
			return proofState.getPSStatus().equals(status);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#proofReuse(org.eventb.core.seqprover.IProofMonitor)
	 * 
	 * Not currently used : Used before for supporting copy&paste. This is now
	 * supported using proofSkeletons & rebuildTac()
	 */
	@Override
	public void proofReuse(IProofMonitor monitor) throws RodinDBException {
		IProofSkeleton proofSkeleton = pt.getRoot().copyProofSkeleton();
		createFreshProofAttempt(null);	//TODO add monitor here.
		BasicTactics.reuseTac(proofSkeleton).apply(pt.getRoot(), monitor);
		pt.addChangeListener(this);
		selectInitialSubgoal();
		deltaProcessor.newProofTree(userSupport, this);
	}

	void selectInitialSubgoal() {
		final IProofTreeNode root = pt.getRoot();
		IProofTreeNode newNode = getNextUndischargedSubgoal(root);
		if (newNode == null) {
			newNode = root;
		}
		setCurrentNode(newNode);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#proofRebuilt(org.eventb.internal.core.ProofMonitor)
	 */
	@Override
	public void proofRebuilt(IProofMonitor monitor) throws RodinDBException {
		final IProofSkeleton proofSkeleton = pt.getRoot().copyProofSkeleton();
		createFreshProofAttempt(null); //TODO add monitor here.
		BasicTactics.rebuildTac(proofSkeleton).apply(pt.getRoot(), monitor);
		pt.addChangeListener(this);
		selectInitialSubgoal();
		deltaProcessor.newProofTree(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isUninitialised()
	 */
	@Override
	public boolean isUninitialised() {
		return (pt == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isSequentDischarged()
	 */
	@Override
	public boolean isSequentDischarged() throws RodinDBException {
		final IPRProof prProof = status.getProof();
		return (!status.isBroken() && prProof.exists() && (prProof
				.getConfidence() > IConfidence.PENDING));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isProofReusable()
	 */
	@Override
	public boolean isProofReusable() throws RodinDBException {
		if (pa == null || pa.isDisposed()) {
			return false;
		}
		final IProverSequent seq = POLoader.readPO(status.getPOSequent(), pa
				.getFormulaFactory());
		return ProverLib.proofReusable(pt.getProofDependencies(), seq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#reloadProofTree()
	 */
	@Override
	@Deprecated
	public void reloadProofTree() throws RodinDBException {
		createFreshProofAttempt(null);
		pt.addChangeListener(this);
		selectInitialSubgoal();
		// if the proof tree was previously broken then the rebuild would
		// fix the proof, making it dirty.
		dirty = status.isBroken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#unloadProofTree()
	 */
	@Override
	public void unloadProofTree() {
		if (pa != null) {
			pt.removeChangeListener(this);
			pa.dispose();
		}
		pa = null;
		pt = null;
		current = null;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("****** Proof Status for: ");
		buffer.append(status + " ******\n");
		buffer.append("Is dirty? " + dirty + "\n");
		buffer.append("** Proof Tree **\n");
		buffer.append(pt);
		buffer.append("\n");
		buffer.append("** Cached **\n");
		for (Predicate hyp : cached) {
			buffer.append(hyp);
			buffer.append("\n");			
		}
		buffer.append("** Searched **\n");
		for (Predicate hyp : searched) {
			buffer.append(hyp);
			buffer.append("\n");			
		}
		buffer.append("Current node: ");
		buffer.append(current);
		buffer.append("\n");
		buffer.append("****************************");
		return buffer.toString();
	}

	@Override
	@Deprecated
	public void applyTactic(ITactic t, IProofTreeNode node,
			IProgressMonitor monitor) {
		applyTactic(t, node, true, monitor);
	}


	public void applyTactic(final ITactic t, final IProofTreeNode node,
			final boolean applyPostTactic, final IProgressMonitor monitor) {
		usm.run(new Runnable() {

			@Override
			public void run() {
				if (internalApplyTactic(t, node, new ProofMonitor(monitor),
						applyPostTactic)) {
					selectNextUndischargedSubGoal(node);
				}
			}

		});

	}

	@Override
	@Deprecated
	public void applyTacticToHypotheses(ITactic t, IProofTreeNode node,
			Set<Predicate> hyps, IProgressMonitor monitor) {
		applyTacticToHypotheses(t, node, hyps, true, monitor);
	}

	public void applyTacticToHypotheses(final ITactic t,
			final IProofTreeNode node, final Set<Predicate> hyps,
			final boolean applyPostTactic, final IProgressMonitor monitor) {
		usm.run(new Runnable() {

			@Override
			public void run() {
				ProofState.this.addAllToCached(hyps);
				if (internalApplyTactic(t, node, new ProofMonitor(monitor),
						applyPostTactic)) {
					selectNextUndischargedSubGoal(node);
				}
			}

		});

	}

	/**
	 * Select the next undischarged subgoal starting from the current node.
	 * <ul>
	 * <li> Select the next pending subgoal if any,</li>
	 * <li> otherwise, select the next reviewed subgoal if any.</li>
	 * <li> Do nothing if there is no pending or reviewed subgoal.</li>
	 * </ul>
	 * 
	 * @param node
	 *            the node where the search start.
	 */
	protected void selectNextUndischargedSubGoal(IProofTreeNode node) {
		final IProofTreeNode target = getNextUndischargedSubgoal(node);
		if (target != null) {
			setCurrentNode(target);
		}
	}

	private IProofTreeNode getNextUndischargedSubgoal(IProofTreeNode node) {
		final IProofTreeNode pendingNode = this.getNextPendingSubgoal(node);
		if (pendingNode != null) {
			return pendingNode;
		}
		final IProofTreeNode reviewedNode = this.getNextReviewedSubgoal(node);
		if (reviewedNode != null) {
			return reviewedNode;
		}
		return null;
	}

	protected boolean internalApplyTactic(ITactic t, IProofTreeNode node,
			IProofMonitor pm, boolean applyPostTactic) {
		Object info = t.apply(node, pm);
		if (info == null) {
			info = "Tactic applied successfully";
			if (applyPostTactic) {
				if (pa != null) {
					final IEventBRoot root = pa.getComponent().getPORoot();
					final IAutoPostTacticManager manager = EventBPlugin
							.getAutoPostTacticManager();
					final IAutoTacticPreference postTac = manager.getPostTacticPreference();
					if (postTac.isEnabled()) {
						final ITactic postTactic = manager
						.getSelectedPostTactics(root);
						postTactic.apply(node, pm);
					}
				}
			}
			deltaProcessor.informationChanged(userSupport,
					new UserSupportInformation(info,
							IUserSupportInformation.MAX_PRIORITY));
			return true;
		} else {
			deltaProcessor.informationChanged(userSupport,
					new UserSupportInformation(info,
							IUserSupportInformation.ERROR_PRIORITY));
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#proofTreeChanged(org.eventb.core.seqprover.IProofTreeDelta)
	 */
	@Override
	public void proofTreeChanged(IProofTreeDelta proofTreeDelta) {
		if (UserSupportUtils.DEBUG)
			UserSupportUtils.debug("UserSupport - Proof Tree Changed: "
					+ proofTreeDelta);
		deltaProcessor.proofTreeChanged(userSupport, this, proofTreeDelta);
		setDirty(true);
	}

	@Override
	public void back(IProofTreeNode node, final IProgressMonitor monitor) {
		if (node == null)
			return;

		final IProofTreeNode parent = node.getParent();
		if (node.isOpen() && parent != null) {
			usm.run(new Runnable() {

				@Override
				public void run() {
					applyTactic(Tactics.prune(), parent, false, monitor);
				}

			});
		}
	}

	@Override
	public void setComment(final String text, final IProofTreeNode node) {
		// set dirty BEFORE setting comment, for delta listeners
		// to get the correct dirty state
		setDirty(true);
		node.setComment(text);
	}

	public boolean selectNextSubGoal(IProofTreeNode node, boolean rootIncluded,
			IProofTreeNodeFilter filter) {
		final IProofTreeNode newNode = this.getNextSubgoal(node, rootIncluded,
				filter);
		if (newNode != null) {
			setCurrentNode(newNode);
			return true;
		}
		return false;
	}

	private IProofTreeNode getNextSubgoal(IProofTreeNode node,
			boolean rootIncluded, IProofTreeNodeFilter filter) {
		if (node == null)
			node = pt.getRoot();
		return node.getNextNode(rootIncluded, filter);
	}

}
