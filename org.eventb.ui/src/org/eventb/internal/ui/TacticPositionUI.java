package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;

public class TacticPositionUI {

	List<Pair<String, IPosition>> tacticPositions;
	public TacticPositionUI() {
		this.tacticPositions = new ArrayList<Pair<String,IPosition>>();
	}

	public  List<Pair<String, IPosition>> getTacticPositions() {
		return tacticPositions;
	}

	public void addTacticPosition(String tacticID, IPosition position) {
		tacticPositions.add(new Pair<String, IPosition>(tacticID, position));
	}
}
