package org.eventb.internal.pp.core.tracing;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;

public class SplitOrigin extends AbstractInferrenceOrigin {

	private Level level;
	
	public SplitOrigin(List<IClause> parents, Level level) {
		super(parents);
		
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

}
