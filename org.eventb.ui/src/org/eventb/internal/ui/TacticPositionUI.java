package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;

public class TacticPositionUI {

	List<String> tacticIDs;

	List<IPosition> positions;

	public TacticPositionUI() {
		this.tacticIDs = new ArrayList<String>();
		this.positions = new ArrayList<IPosition>();
	}

	public String [] getTacticIDs() {
		return tacticIDs.toArray(new String[tacticIDs.size()]);
	}

	public IPosition [] getPositions() {
		return positions.toArray(new IPosition[positions.size()]);
	}
	
	public void addTacticPosition(String tacticID, IPosition position) {
		tacticIDs.add(tacticID);
		positions.add(position);
	}
}
