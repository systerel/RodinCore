package org.eventb.core.pm;

import java.util.ArrayList;

import org.eventb.core.seqprover.ITactic;

public interface IProvingMode {
	
	public boolean isExpertMode();

	public void setExpertMode(boolean mode);

	public ITactic getPostTactic();

	public void setPostTactics(ArrayList<String> tacticIDs);

}