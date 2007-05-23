package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;

public class TacticPositionUI {

	private List<Pair<String, IPosition>> tacticPositions;
	private Point point;
	
	public TacticPositionUI(Point point) {
		this.point = point;
		this.tacticPositions = new ArrayList<Pair<String,IPosition>>();
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public  List<Pair<String, IPosition>> getTacticPositions() {
		return tacticPositions;
	}

	public void addTacticPosition(String tacticID, IPosition position) {
		tacticPositions.add(new Pair<String, IPosition>(tacticID, position));
	}
}
