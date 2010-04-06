package org.eventb.core.pm;

import org.eventb.core.seqprover.IProofTreeDelta;


/**
 * @since 1.0
 */
public interface IProofStateDelta {

	public static final int ADDED = 1;
	public static final int REMOVED = 2;
	public static final int CHANGED = 4;
	
	public static final int F_CACHE = 0x00001;
	
	public static final int F_SEARCH = 0x00002;
	
	public static final int F_NODE = 0x00004;
	
	public static final int F_PROOFTREE = 0x00008;
	
	public IProofState getProofState();
	public int getKind();
	public int getFlags();
	public IProofTreeDelta getProofTreeDelta();
	public void setProofTreeDelta(IProofTreeDelta proofTreeDelta);
}
