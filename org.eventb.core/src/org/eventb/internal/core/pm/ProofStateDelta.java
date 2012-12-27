/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.seqprover.IProofTreeDelta;

/**
 * @author htson
 *         <p>
 *         This class contains the delta which used to pass the information to
 *         the User Interface.
 * 
 */
public class ProofStateDelta implements IProofStateDelta {

	private IProofState ps;

	/*
	 * @see IProofStateDelta#getKind()
	 */
	private int kind = 0;

	/*
	 * @see IProofStateDelta#getFlags()
	 */
	private int flags = 0;

	private IProofTreeDelta proofTreeDelta;

	public ProofStateDelta(IProofState ps) {
		this.ps = ps;
		proofTreeDelta = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateDelta#getProofState()
	 */
	@Override
	public IProofState getProofState() {
		return ps;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofStateDelta#getKind()
	 */
	@Override
	public int getKind() {
		return kind;
	}

	public void setKind(int type) {
		this.kind = type;
	}

	@Override
	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	@Override
	public IProofTreeDelta getProofTreeDelta() {
		return proofTreeDelta;
	}

	@Override
	public void setProofTreeDelta(IProofTreeDelta proofTreeDelta) {
		this.proofTreeDelta = proofTreeDelta;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder, "  ");
		return builder.toString();
	}

	private void toString(StringBuilder builder, String indent) {
		builder.append(indent);
		if (kind == ADDED) builder.append("[+] ");
		else if (kind == REMOVED) builder.append("[-] ");
		else if (kind == CHANGED) builder.append("[*] ");
		builder.append(ps.getPSStatus());
		builder.append(" [");
		boolean sep = false;
		sep = toStringFlag(builder, F_CACHE, "CACHE", sep);
		sep = toStringFlag(builder, F_SEARCH, "SEARCH", sep);
		sep = toStringFlag(builder, F_NODE, "NODE", sep);
		sep = toStringFlag(builder, F_PROOFTREE, "PROOFTREE", sep);
		builder.append("]");
//		String childIndent = indent + "  ";
//		for (ProofTreeDelta child: children) {
//			builder.append("\n");
//			child.toString(builder, childIndent);
//		}
	}

	private boolean toStringFlag(StringBuilder builder, int flagToTest,
			String flagName, boolean sep) {
		
		if ((flags & flagToTest) != 0) {
			if (sep) builder.append('|');
			builder.append(flagName);
			return true;
		}
		return sep;
	}

	// public void setNewCurrentNode(IProofTreeNode node) {
	// this.node = node;
	// }
	//
	// public List<Object> getInformation() {
	// return information;
	// }
	//
	// public void setNewProofState(IProofState ps) {
	// this.ps = ps;
	// newProofState = true;
	// }
	//
	// public void setDeletedProofState(IProofState ps) {
	// this.ps = ps;
	// isDeleted = true;
	// }
	//
	// public IProofState getProofState() {
	// return ps;
	// }
	//
	// public IProofTreeNode getNewProofTreeNode() {
	// return node;
	// }
	//
	// public void setNewSearch() {
	// newSearch = true;
	// }
	//
	// public void setProofTreeDelta(IProofTreeDelta proofTreeDelta) {
	// this.proofTreeDelta = proofTreeDelta;
	// }
	//
	// public IProofTreeDelta getProofTreeDelta() {
	// return proofTreeDelta;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#toString()
	// */
	// @Override
	// public String toString() {
	// String result = "\n" + DELTA_SEPARATOR + "\n";
	// result += PROOF_STATE_PROMPT + ps + "\n";
	// result += IS_DELETED_PROMPT + isDeleted + "\n";
	// result += IS_NEW_PROMPT + newProofState + "\n";
	// result += PROOF_TREE_DELTA_PROMPT + proofTreeDelta +"\n";
	// result += CURRENT_NODE_PROMPT + node + "\n";
	// result += NEW_CACHE_PROMPT + newCache + "\n";
	// result += NEW_SEARCH_PROMPT + newSearch + "\n";
	// result += INFORMATION_PROMPT + "\n";
	// for (Object info : information) {
	// result += " " + info + "\n";
	// }
	// result += DELTA_SEPARATOR + "\n";
	// return result;
	// }
	//
	// public void setNewCache() {
	// newCache = true;
	// }
	//
	// public boolean getNewSearch() {
	// return newSearch;
	// }
	//
	// public boolean getNewCache() {
	// return newCache;
	// }
	//
	// public IUserSupport getSource() {
	// return userSupport;
	// }
	//
	// public void addInformation(Object info) {
	// this.information.add(info);
	// }
	//
	// public void addAllInformation(List<Object> infos) {
	// information.addAll(infos);
	// }
	//
	// public boolean isDeleted() {
	// return isDeleted;
	// }
	//
	// public boolean isNewProofState() {
	// return newProofState;
	// }

}
