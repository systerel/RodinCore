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
package fr.systerel.tactics.perfs.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.pm.ProofManager;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Class used to record all the proof tree nodes of either a IRodinProject, or a
 * IRodinFile, or a IPOSequent. Implements {@link Iterable}. Should be used as follows :
 * <p>
 * <code>se = new SequentExtractor;<br>
 * se.extract(...);<br>
 * ...<br>
 * se.computeProverSequent();<br>
 * ...<br>
 * for (List<'IProverSequent'> l : se) {<br>
 * 		//Do something with l<br>
 * }</code><br>
 * 
 * 
 * @author Emmanuel Billaud
 */
@SuppressWarnings("restriction")
public class SequentExtractor implements Iterable<List<IProverSequent>> {
	private List<List<IProverSequent>> listProSeq;
	private List<IProofTreeNode> listPtNode;
	private ProofManager pm;
	private boolean root;

	/**
	 * Create a new sequentExtractor with the parameter <code>root</code>.
	 * 
	 * @param root
	 *            indicates when the compute of the IProverSequent (by calling
	 *            <code> computeProverSequent</code>) is proceeded if it should
	 *            record only the sequent of the IProofTreeNode contained in
	 *            <code>listPtNode</code>, or if it should record the sequent of
	 *            all their descendants.
	 */
	public SequentExtractor(boolean root) {
		listProSeq = new ArrayList<List<IProverSequent>>();
		listPtNode = new ArrayList<IProofTreeNode>();
		pm = ProofManager.getDefault();
		this.root = root;
	}

	/**
	 * Returns the list <code>listPtNode</code>.
	 */
	public List<IProofTreeNode> getPtNodeRoot() {
		return listPtNode;
	}

	/**
	 * Set the list <code>listPtNode</code> equals to the singleton
	 * <code>ptNode</code>.
	 * 
	 * @param ptNode
	 *            the considered IProofTreeNode
	 */
	public void setSingletonPtNode(IProofTreeNode ptNode) {
		listPtNode.clear();
		listPtNode.add(ptNode);
	}

	/**
	 * Try to extract all the IRodinFile of the given IrodinProject.
	 * 
	 * @param project
	 *            the considered IRodinProject
	 * @throws RodinDBException
	 *             if there is some problem loading proof obligations
	 */
	public void extract(IRodinProject project) throws RodinDBException {
		for (IRodinFile file : project.getRodinFiles()) {
			extract(file);
		}
	}

	/**
	 * Try to extract all the IPOSequent of the given IRodinFile.
	 * 
	 * @param file
	 *            the considered IRodinFile
	 * @throws RodinDBException
	 *             if there is some problem loading proof obligationS
	 */
	public void extract(IRodinFile file) throws RodinDBException {
		IInternalElement ielt = file.getRoot();
		if (ielt instanceof IPORoot) {
			for (IPOSequent sequent : ((IPORoot) ielt).getSequents()) {
				extract(sequent);
				System.gc();
			}
		}
	}

	/**
	 * Record the IProofTreeNode root of the IPOSequent in
	 * <code>listProSeq</code>.
	 * 
	 * @param sequent
	 *            the considered IPOSequent
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 */
	public void extract(IPOSequent sequent) throws RodinDBException {
		final IInternalElement root = sequent.getRoot();
		if (root instanceof IPORoot) {
			final IProofComponent pc = pm.getProofComponent((IPORoot) root);
			final IProofTree proofTree = getProofTree(sequent, pc);
			if (proofTree != null) {
				IProofTreeNode ptNode = proofTree.getRoot();
				if (ptNode != null) {
					listPtNode.add(ptNode);
				}
			}
		}
	}

	/**
	 * Try to return the proof tree of the sequent of the proof component.
	 * 
	 * @param sequent
	 *            the considered IPOSequent
	 * @param pc
	 *            the considered proof component
	 * @return the proof tree of the sequent if it successfully get it,
	 *         <code>null</code> else
	 * @throws RodinDBException
	 *             if there is some problem loading the corresponding proof
	 *             obligation
	 */
	private IProofTree getProofTree(IPOSequent sequent, IProofComponent pc)
			throws RodinDBException {
		final String poName = sequent.getElementName();
		final IProofAttempt pa = pc.createProofAttempt(poName, "ME", null);
		final IProofTree pt = pa.getProofTree();
		pa.dispose();
		final IProofSkeleton skeleton = pc.getProofSkeleton(poName, pt
				.getSequent().getFormulaFactory(), null);
		final Object result = BasicTactics.reuseTac(skeleton).apply(
				pt.getRoot(), null);
		if (result != null) {
			return null;
		}
		return pt;
	}

	/**
	 * For each IProofTreeNode contained in <code>listPtNode</code>, it records
	 * in <code>listProSeq</code> the sequent of the given proof tree node
	 * <code>ptNode</code>. If the attribute <code>root</code> equals false, it
	 * records sequents of the IProofTreeNode descendant of <code>ptNode</code>
	 * as well, else it does nothing.<br>
	 * At the end, the list <code>listPtNode</code> is cleared.
	 */
	public void computeProverSequent() {
		for (IProofTreeNode ptNode : listPtNode) {
			listProSeq.add(new ArrayList<IProverSequent>());
			recordList(ptNode);
		}
		listPtNode.clear();
	}

	/**
	 * Record in <code>listProSeq</code> the sequent of the given proof tree
	 * node <code>ptNode</code>. If the attribute <code>root</code> equals
	 * false, it records sequents of the IProofTreeNode descendant of
	 * <code>ptNode</code> as well, else it does nothing.
	 * 
	 * @param ptNode
	 *            the given proof tree node to record
	 */
	private void recordList(IProofTreeNode ptNode) {
		listProSeq.get(listProSeq.size() - 1).add(ptNode.getSequent());
		if (root) {
			return;
		}
		for (IProofTreeNode ptN : ptNode.getChildNodes()) {
			recordList(ptN);
		}
	}

	/**
	 * Tells whether the sequentExtractor contains at least one IProverSequent.
	 * 
	 * @return false iff the sequentExtractor contains at least one
	 *         IProverSequent
	 */
	public boolean isEmpty() {
		for (List<IProverSequent> l : listProSeq) {
			if (!l.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<List<IProverSequent>> iterator() {
		return listProSeq.iterator();
	}

}