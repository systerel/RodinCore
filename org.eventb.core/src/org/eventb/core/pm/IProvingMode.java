package org.eventb.core.pm;

import org.eventb.core.seqprover.ITactic;

public interface IProvingMode {
	
	public boolean isExpertMode();

	public void setExpertMode(boolean mode);

	public ITactic getPostTactic();

	public void setPostTactics(String ... tacticIDs);
}